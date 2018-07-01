package com.cerner.ccl.analysis.core.rules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.cerner.ccl.analysis.core.violations.MissingGoToLabelViolation;
import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;

/**
 * A {@link AnalysisRule} that identifies a go to statement without a corresponding label defined
 *
 * @author Joshua Hyde
 * @author Jeff Wiedemann
 */

public class MissingGoToLabelRules extends TimedDelegate {

    /**
     * @param document
     *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
     */
    public MissingGoToLabelRules(final Document document) {
        super(document);
    }

    @Override
    protected Set<Violation> doMeasuredAnalysis() throws JDOMException {
        final Set<Violation> violations = new HashSet<Violation>();

        final List<Element> candidateLabels = selectNodesByName("GOTO.");
        final List<Element> actualLabels = selectNodesByName("LABEL.");

        for (final Element candidateLabel : candidateLabels) {
            boolean matchFound = false;
            for (final Element actualLabel : actualLabels) {
                if (getCclName(candidateLabel).equalsIgnoreCase(getCclName(actualLabel))) {
                    matchFound = true;
                    break;
                }
            }

            if (!matchFound) {
                violations
                        .add(new MissingGoToLabelViolation(getCclName(candidateLabel), getLineNumber(candidateLabel)));
            }
        }

        return violations;
    }

    @Override
    public Set<Violation> getCheckedViolations() {
        final Set<Violation> violations = new HashSet<Violation>();
        violations.add(new MissingGoToLabelViolation("label", 1));
        return violations;
    }
}