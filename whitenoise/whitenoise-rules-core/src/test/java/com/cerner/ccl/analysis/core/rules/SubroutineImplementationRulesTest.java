package com.cerner.ccl.analysis.core.rules;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.cerner.ccl.analysis.core.AbstractJDomTest;
import com.cerner.ccl.analysis.core.violations.DuplicateSubroutineDefinitionViolation;
import com.cerner.ccl.analysis.core.violations.SubroutineReturnRequiredAndMissingViolation;
import com.cerner.ccl.analysis.data.Violation;

/**
 * Integration tests for {@link SubroutineImplementationRules}.
 *
 * @author Jeff Wiedemann
 *
 */

public class SubroutineImplementationRulesTest extends AbstractJDomTest {
    /**
     * Test to ensure that all instances where a subroutine value which has no return statment, but appears to require
     * one based on the way the routine is invoked is identified
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAnalyzeReturnStatementRequiredAndMissingCheckCall() throws Exception {
        final Set<Violation> violations = new SubroutineImplementationRules(
                toDocument("subroutine-return-required-and-missing-check-call-statement.xml")).analyze();
        assertThat(violations).hasSize(5);

        assertThat(violations).contains(new SubroutineReturnRequiredAndMissingViolation("NO_RETURN1", 43, 12));
        assertThat(violations).contains(new SubroutineReturnRequiredAndMissingViolation("NO_RETURN2", 47, 17));
        assertThat(violations).contains(new SubroutineReturnRequiredAndMissingViolation("NO_RETURN3", 51, 22));
        assertThat(violations).contains(new SubroutineReturnRequiredAndMissingViolation("NO_RETURN4", 55, 27));
        assertThat(violations).contains(new SubroutineReturnRequiredAndMissingViolation("NO_RETURN5", 59, 34));
    }

    /**
     * Test to ensure that all instances where a subroutine value which has no return statment, based on the structure
     * of the subroutine code itself is identified when the return appears to be required
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAnalyzeReturnStatementRequiredAndMissingCheckReturn() throws Exception {
        final Set<Violation> violations = new SubroutineImplementationRules(
                toDocument("subroutine-return-required-and-missing-check-return-statement.xml")).analyze();
        assertThat(violations).hasSize(2);

        assertThat(violations).contains(new SubroutineReturnRequiredAndMissingViolation("MYSUB2", 14, 4));
        assertThat(violations).contains(new SubroutineReturnRequiredAndMissingViolation("MYSUB4", 31, 6));
    }

    /**
     * Ensure that subroutines which are defined twice in the same program are identified
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAnalyzeDuplicateSubroutineDefinition() throws Exception {
        final Set<Violation> violations = new SubroutineImplementationRules(
                toDocument("subroutine-with-duplicate-definition.xml")).analyze();
        assertThat(violations).hasSize(1);

        assertThat(violations).contains(new DuplicateSubroutineDefinitionViolation("MYSUB", 11));
    }
}
