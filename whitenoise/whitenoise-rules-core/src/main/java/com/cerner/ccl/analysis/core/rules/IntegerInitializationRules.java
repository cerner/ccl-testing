package com.cerner.ccl.analysis.core.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cerner.ccl.analysis.core.violations.InvalidVariableInitializationViolation;
import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;

/**
 * An {@link AnalysisRule} that prohibits the initialization of an integer data type to a float value.
 *
 * @author Joshua Hyde
 *
 */

public class IntegerInitializationRules extends TimedDelegate {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntegerInitializationRules.class);
    private static final List<String> INTEGER_TYPES = Arrays
            .asList(new String[] { "I1", "UI1", "UI2", "UI2", "I4", "UI4", "W8", "UW8", "H" });

    /**
     * @param document
     *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
     */
    public IntegerInitializationRules(final Document document) {
        super(document);
    }

    @Override
    protected Set<Violation> doMeasuredAnalysis() throws JDOMException {
        final Set<Violation> violations = new HashSet<Violation>();

        for (final Element declaredInt : getDeclaredIntegers()) {
            final Element real = getVariableDeclarationRealElement(declaredInt);

            if (real != null) {
                final String variableName = getCclName(declaredInt);
                final String initializationValue = real.getAttributeValue("text");
                LOGGER.trace("Variable {} has an initialization value of {} and will be marked as a violation.",
                        variableName, initializationValue);
                violations.add(new InvalidVariableInitializationViolation(variableName, initializationValue,
                        getLineNumber(declaredInt)));
            }
        }

        return violations;
    }

    private Element getVariableDeclarationRealElement(final Element declaredInt) throws JDOMException {
        Element returnValue = null;

        for (final Element real : selectNodesByName(declaredInt, "REAL")) {
            if (!real.getParentElement().getName().equalsIgnoreCase("CALL."))
                continue;
            if (!real.getParentElement().getParentElement().getName().equalsIgnoreCase("OPTION."))
                continue;
            if (!real.getParentElement().getParentElement().getParentElement().getName().equalsIgnoreCase("OPTIONS."))
                continue;

            returnValue = real;
        }
        return returnValue;
    }

    /**
     * Get all integers declared within the document.
     * 
     * @param document
     *            The {@link Document} representing the XML representation of the CCL program.
     * @return A {@link List} of {@link Element} objects representing the integer declarations within the document.
     * @throws JDOMException
     *             If any errors occur during the retrieval.
     */
    private List<Element> getDeclaredIntegers() throws JDOMException {
        final List<Element> result = new ArrayList<Element>();

        for (final Element declare : getVariableDeclarations()) {
            if (declare.getChildren("NAMESPACE.").size() > 0) {
                if (declare.getChildren("NAME").size() > 0) {
                    final String type = declare.getChildren("NAME").get(0).getAttributeValue("text")
                            .toUpperCase(Locale.getDefault());
                    if (INTEGER_TYPES.contains(type)) {
                        result.add(declare);
                    }
                }
            } else if (declare.getChildren("NAME").size() > 1) {
                final String type = declare.getChildren("NAME").get(1).getAttributeValue("text")
                        .toUpperCase(Locale.getDefault());
                if (INTEGER_TYPES.contains(type)) {
                    result.add(declare);
                }
            }
        }
        return result;
    }

    @Override
    public Set<Violation> getCheckedViolations() {
        final Set<Violation> violations = new HashSet<Violation>();
        violations.add(new InvalidVariableInitializationViolation("variable", "0.0", 1));
        return violations;
    }
}
