package com.cerner.ccl.analysis.core.rules;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.cerner.ccl.analysis.core.AbstractJDomTest;
import com.cerner.ccl.analysis.core.violations.MissingCnvtStringlengthParamViolation;
import com.cerner.ccl.analysis.data.Violation;

/**
 * Integration tests for {@link MissingCnvtStringlengthParamRules}.
 *
 * @author Albert Ponraj
 *
 */

public class CnvtStringFunctionRulesTest extends AbstractJDomTest {

    /**
     * Test to ensure that the appropriate violations are caught when the CCL record structure that doesn't have a
     * declaration is accessed various ways
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testMissingLengthParam() throws Exception {
        final Set<Violation> violations = new MissingCnvtStringlengthParamRules(
                toDocument("cnvtstring-missin-param2.xml")).doMeasuredAnalysis();
        assertThat(violations).hasSize(1);

        assertThat(violations).contains(new MissingCnvtStringlengthParamViolation(13));
    }

}
