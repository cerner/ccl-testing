package com.cerner.ccl.analysis.core.rules;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.cerner.ccl.analysis.core.AbstractJDomTest;
import com.cerner.ccl.analysis.core.violations.NulltermViolation;
import com.cerner.ccl.analysis.data.Violation;

/**
 * Integration tests for {@link NulltermRules}.
 *
 * @author Albert Ponraj
 *
 */

public class NulltermRulesTest extends AbstractJDomTest {
    /**
     * Test to ensure that the appropriate violations are caught when the CCL record structure that doesn't have a
     * declaration is accessed various ways
     *
     * @throws Exception
     *             Not expected.
     */

    @Test
    public void testMissingNullTermFunction() throws Exception {
        final Set<Violation> violations = new NulltermRules(toDocument("missing-nullterm-function.xml"))
                .doMeasuredAnalysis();
        assertThat(violations).hasSize(1);

        assertThat(violations).contains(new NulltermViolation(15));
    }

}
