package com.cerner.ccl.analysis.core.rules;

import java.util.HashSet;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.cerner.ccl.analysis.core.violations.MissingCnvtStringlengthParamViolation;
import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;

/**
 * An {@link AnalysisRule} that flags any instance where the cnvtstring() function without the second parameter(length)
 *
 * @author Albert Ponraj
 *
 */

public class MissingCnvtStringlengthParamRules extends TimedDelegate {

    /**
     * @param document
     *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
     */
    public MissingCnvtStringlengthParamRules(final Document document) {
        super(document);
    }

    @Override
    protected Set<Violation> doMeasuredAnalysis() throws JDOMException {
        final Set<Violation> violations = new HashSet<Violation>();

        for (final Element cnvtstringFunction : selectNodesByName("CALL.")) {
            // If the call is not to the cnvtstring function then continue
            if (getCclName(cnvtstringFunction).equalsIgnoreCase("CNVTSTRING")
                    && cnvtstringFunction.getChildren().size() < 3) {
                violations.add(new MissingCnvtStringlengthParamViolation(getLineNumber(cnvtstringFunction)));
            }
        }
        return violations;
    }

    @Override
    public Set<Violation> getCheckedViolations() {
        final Set<Violation> violations = new HashSet<Violation>();
        violations.add(new MissingCnvtStringlengthParamViolation(1));
        return violations;
    }
}
