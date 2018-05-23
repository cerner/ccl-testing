package com.cerner.ccl.analysis.core.rules;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.cerner.ccl.analysis.core.AbstractJDomTest;
import com.cerner.ccl.analysis.core.violations.MissingGoToLabelViolation;
import com.cerner.ccl.analysis.data.Violation;

/**
 * Integration tests for {@link MissingGoToLabelRules}.
 *
 * @author Jeff Wiedemann
 *
 */

public class MissingGoToLabelRuleTest extends AbstractJDomTest {
    /**
     * This test is designed to ensure that a go to statement with the corresponding label does not inaccurately report
     * failure
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testNoMissingGoToLabel() throws Exception {
        final Set<Violation> violations = new MissingGoToLabelRules(toDocument("go-to-label.xml")).doMeasuredAnalysis();
        assertThat(violations).hasSize(0);

    }

    /**
     * This test is designed to ensure that a go to statement without the corresponding label does correctly report
     * failure
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testMissingGoToLabel() throws Exception {
        final Set<Violation> violations = new MissingGoToLabelRules(toDocument("missing-go-to-label.xml"))
                .doMeasuredAnalysis();
        assertThat(violations).hasSize(1);

        assertThat(violations).contains(new MissingGoToLabelViolation("EXIT_SCRIPT", 3));
    }
}
