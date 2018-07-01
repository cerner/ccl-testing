package com.cerner.ccl.analysis.core.rules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.cerner.ccl.analysis.core.violations.InfiniteLoopViolation;
import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;

/**
 * A {@link AnalysisRule} that identifies the following issues with while loops: 1. While loop with no attempt to set or
 * update the conditional variable
 *
 * @author Jeff Wiedemann
 */

public class WhileLoopRules extends TimedDelegate {

    /**
     * @param document
     *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
     */
    public WhileLoopRules(final Document document) {
        super(document);
    }

    @Override
    protected Set<Violation> doMeasuredAnalysis() throws JDOMException {
        final Set<Violation> violations = new HashSet<Violation>();

        final List<Element> whileLoops = selectNodesByName("WHILE.");

        for (final Element whileLoop : whileLoops) {
            // The way I am going to do this check is pretty good but not perfect. Basically I will look at the
            // conditional portion of the while loop and locate all NAME variables. At least one of those name variables
            // will be by conditional, so I will then scan the body of the while loop looking for instances where the
            // conditional is referenced on the left hand side of an operator to ensure that something in the
            // conditional is getting a new value periodically.
            List<Attribute> nameAttributes = selectAttributes(whileLoop, "./*[1]/descendant-or-self::NAME/@text");

            boolean found = false;
            for (Attribute nameAttribute : nameAttributes) {
                // Search for this name within a Z_SET command to see if it's used there
                if (!selectNodes(whileLoop, "./COMMA.//Z_SET./NAME[1][@text = '" + nameAttribute.getValue() + "']")
                        .isEmpty()) {
                    found = true;
                    break;
                }

                // Search for this name within a IS. command to see if it's used within a report writer section
                if (!selectNodes(whileLoop, "./COMMA.//IS./NAME[1][@text = '" + nameAttribute.getValue() + "']")
                        .isEmpty()) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                violations.add(new InfiniteLoopViolation(getLineNumber(whileLoop)));
            }
        }

        return violations;
    }

    @Override
    public Set<Violation> getCheckedViolations() {
        Set<Violation> violations = new HashSet<Violation>();
        violations.add(new InfiniteLoopViolation(1));
        return violations;
    }
}