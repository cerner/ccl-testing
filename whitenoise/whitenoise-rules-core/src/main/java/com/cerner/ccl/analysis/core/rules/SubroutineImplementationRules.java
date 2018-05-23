package com.cerner.ccl.analysis.core.rules;

import java.security.InvalidParameterException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;

import com.cerner.ccl.analysis.core.violations.DuplicateSubroutineDefinitionViolation;
import com.cerner.ccl.analysis.core.violations.SubroutineReturnRequiredAndMissingViolation;
import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;

/**
 * A {@link AnalysisRule} that flags the following: 1. One subroutine which has been defined twice with two different
 * implementations 2. A subroutine that requires a return statement, but doesn't actually return something
 *
 * @author Jeff Wiedemann
 *
 */

public class SubroutineImplementationRules extends TimedDelegate {

    /**
     * @param document
     *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
     */
    public SubroutineImplementationRules(final Document document) {
        super(document);
    }

    @Override
    protected Set<Violation> doMeasuredAnalysis() throws JDOMException {
        final Map<String, Element> definedSubroutines = new HashMap<String, Element>();
        final Set<Violation> violations = new HashSet<Violation>();

        // Loop through all subroutine implementations and store off for later checks
        for (final Element subroutine : getDefinedSubroutines()) {
            final String subroutineName = getCclName(subroutine);

            // As we are storing off subroutine implementations, we want to see if the script has accidently declared
            // this
            // subroutine previously with a different implementations. If so, add the violation.
            if (definedSubroutines.get(subroutineName) != null
                    && !cclElementsAreEqual(subroutine, definedSubroutines.get(subroutineName))) {
                violations.add(new DuplicateSubroutineDefinitionViolation(subroutineName, getLineNumber(subroutine)));
            }

            definedSubroutines.put(subroutineName, subroutine);
        }

        if (definedSubroutines.isEmpty()) {
            return Collections.emptySet();
        }

        // Store off all call statement that might invoke subroutines for later evaluation
        final List<Element> allCalls = selectNodesByName("CALL.");

        // Loop through every subroutine and determine if it appears to require a return statement
        // based on the manner in which it was invoked
        for (final Entry<String, Element> subroutine : definedSubroutines.entrySet()) {
            for (final Element call : allCalls) {
                final String callName = getCclName(call);

                // This call statement is not pertinent to this subroutine, so move on
                if (!callName.equalsIgnoreCase(subroutine.getKey())) {
                    continue;
                }

                // If the call statement is invoking a subroutine and storing the result into a variable which is
                // prefixed
                // with dummy, then assume that the variable is not used anywhere and assume the subroutines return
                // isn't
                // required for this invocation, move on and check the next call
                if (callResultIsStoredToDummyVariable(call)) {
                    continue;
                }

                // Inspect the nature in which the subroutine is being invoked to determine if there is an attempt to
                // consume
                // the return value of the subroutine, if the call statement does not consume a return value, then
                // assume the
                // subroutine return is not required... at least for this call, move on to the next call
                if (!callStatementConsumesResult(call)) {
                    continue;
                }

                // If we made it here then we have concluded that the subroutine requires a return statement, therefore
                // we will check to see if it has one. If it does, then move on to the next subroutine without error,
                // otherwise
                // add a violation for the subroutine and move on to the next subroutine
                if (subroutineHasReturn(subroutine.getValue())) {
                    break;
                }

                violations.add(new SubroutineReturnRequiredAndMissingViolation(subroutine.getKey(),
                        getLineNumber(subroutine.getValue()), getLineNumber(call)));
                break;
            }
        }

        return violations;
    }

    @Override
    public Set<Violation> getCheckedViolations() {
        final Set<Violation> violations = new HashSet<Violation>();
        violations.add(new SubroutineReturnRequiredAndMissingViolation("subroutine", 1, 1));
        violations.add(new DuplicateSubroutineDefinitionViolation("subroutine", 1));
        return violations;
    }

    private boolean callStatementConsumesResult(final Element call) {
        final Element callParent = call.getParentElement();

        // Determine if the subroutine call is storing or reading the return value. If the immediate parent is not a
        // Z_CALL. or Z_DECLARE. then we assume it cares about the return
        if (!callParent.getName().equalsIgnoreCase("Z_CALL.") && !callParent.getName().equalsIgnoreCase("Z_DECLARE.")) {
            return true;
        }

        return false;
    }

    private boolean callResultIsStoredToDummyVariable(final Element call) {
        final Element callParent = call.getParentElement();
        if ((callParent.getName().equalsIgnoreCase("Z_SET.") || callParent.getName().equalsIgnoreCase("IS."))
                && getCclName(callParent).toLowerCase(Locale.getDefault()).startsWith("dummy")) {
            return true;
        }
        return false;
    }

    // It's really tough to determine if a subroutine returns results correctly under all circumstances
    // so what we will assume is that the last line of any subroutine which is required to return a value is returning a
    // value.
    // The only false positives we will find with this assumption would be unreachable code or correct usage of return
    // statements
    // nested within if,else,while,for-loop blocks (which is generally risky practice)
    private boolean subroutineHasReturn(final Element subroutine) {
        final Element subroutineCode = subroutine.getChildren("COMMA.").get(1);

        if (subroutineCode == null) {
            throw new IllegalStateException(
                    "Expected subroutine [" + getCclName(subroutine) + "] to have two COMMA. children");
        }

        if (subroutineCode.getChildren().size() == 0) {
            throw new IllegalStateException(
                    "Expected subroutine [" + getCclName(subroutine) + "] to have children in second COMMA. element");
        }

        final Element lastElement = subroutineCode.getChildren().get(subroutineCode.getChildren().size() - 1);

        // Ensure that the return statement is actually returning information too!
        if (lastElement.getName().equalsIgnoreCase("RETURN.") && lastElement.getChildren().size() > 0) {
            return true;
        }

        return false;
    }

    /**
     * Poor mans version of checking to see if two CCL elements are identical... generally speaking, if for each element
     * within the passed in elements, if the element descendant names are the same and the text attribute values are the
     * same when present, then they are the same
     *
     * @param e1
     *            The first CCL element to compare
     * @param e2
     *            The second CCL element to compare
     * @return True if the CCL elements are coded equally, false otherwise
     */
    private boolean cclElementsAreEqual(final Element e1, final Element e2) {
        // Validate parameters
        if (e1 == null || e2 == null)
            throw new InvalidParameterException("Element cannot be null");

        if (!e1.getName().equalsIgnoreCase(e2.getName()))
            return false;

        final Iterator<Element> e1Descendants = e1.getDescendants(new ElementFilter());
        final Iterator<Element> e2Descendants = e2.getDescendants(new ElementFilter());

        while (e1Descendants.hasNext()) {
            final Element e1Descendant = e1Descendants.next();

            if (!e2Descendants.hasNext()) {
                return false;
            }

            final Element e2Descendant = e2Descendants.next();

            if (e1Descendant.getAttributes().size() != e2Descendant.getAttributes().size()) {
                return false;
            }

            if (!e1Descendant.getName().equalsIgnoreCase(e2Descendant.getName())) {
                return false;
            }

            if (!StringUtils.equalsIgnoreCase(e1Descendant.getAttributeValue("text"),
                    e2Descendant.getAttributeValue("text"))) {
                return false;
            }
        }
        return true;
    }
}
