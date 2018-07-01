package com.cerner.ccl.analysis.core.rules;

import java.util.HashSet;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.cerner.ccl.analysis.core.violations.NulltermViolation;
import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;

/**
 * An {@link AnalysisRule} that flags any instance where the nullterm() is not used while using uar_srvsetstring
 *
 * @author Albert Ponraj
 *
 */

public class NulltermRules extends TimedDelegate {

    /**
     * @param document
     *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
     */
    public NulltermRules(final Document document) {
        super(document);
    }

    @Override
    protected Set<Violation> doMeasuredAnalysis() throws JDOMException {
        final Set<Violation> violations = new HashSet<Violation>();

        for (final Element uarFunction : selectNodesByName("CALL.", "[NAME[1]/@text = 'UAR_SRVSETSTRING']")) {

            if (uarFunction.getChildren().size() > 3) {
                final Element Param3 = uarFunction.getChildren().get(3);

                if (!Param3.getName().equalsIgnoreCase("CALL.") && !getCclName(Param3).equalsIgnoreCase("NULLTERM")) {
                    violations.add(new NulltermViolation(getLineNumber(Param3)));
                }
            } else {
                violations.add(new NulltermViolation(getLineNumber(uarFunction)));
            }
        }
        return violations;
    }

    @Override
    public Set<Violation> getCheckedViolations() {
        final Set<Violation> violations = new HashSet<Violation>();
        violations.add(new NulltermViolation(1));
        return violations;
    }
}
