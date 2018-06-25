package com.cerner.ccl.analysis.core.rules;

import java.util.HashSet;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.cerner.ccl.analysis.core.violations.ModuloByOneViolation;
import com.cerner.ccl.analysis.core.violations.ReversedModuloParametersViolation;
import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;

/**
 * A {@link AnalysisRule} that identifies the following issues with the modulo function: 1. Modulo by 1 which is always
 * 0 2. Reversed modulo parameters
 *
 * @author Jeff Wiedemann
 */

public class ModuloFunctionRules extends TimedDelegate {

    /**
     * @param document
     *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
     */
    public ModuloFunctionRules(final Document document) {
        super(document);
    }

    @Override
    protected Set<Violation> doMeasuredAnalysis() throws JDOMException {
        final Set<Violation> violations = new HashSet<Violation>();

        for (final Element call : selectNodesByName("CALL.")) {
            // If the call isn't a call to the mod routine then skip it
            if (!getCclName(call).equalsIgnoreCase("MOD"))
                continue;

            if (call.getChildren().size() != 3)
                throw new JDOMException("Unhandled number of child elements to call mod() statement at line ["
                        + getLineNumber(call) + "]");

            final Element param1 = call.getChildren().get(1);
            final Element param2 = call.getChildren().get(2);

            // The rule will assume that any hardcoded integer in the first parameter is an indication of reversed
            // parameters
            if (param1.getName().equalsIgnoreCase("INT")) {
                violations.add(new ReversedModuloParametersViolation(getLineNumber(param1)));
            }

            // If the second parameter to the modulo function is 1 then add a violation
            if (param2.getName().equalsIgnoreCase("INT") && param2.getAttributeValue("text").equalsIgnoreCase("1")) {
                violations.add(new ModuloByOneViolation(getLineNumber(param2)));
            }
        }

        return violations;
    }

    @Override
    public Set<Violation> getCheckedViolations() {
        final Set<Violation> violations = new HashSet<Violation>();
        violations.add(new ReversedModuloParametersViolation(1));
        violations.add(new ModuloByOneViolation(1));
        return violations;
    }
}