package com.cerner.ccl.analysis.core.rules;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.cerner.ccl.analysis.core.AbstractJDomTest;
import com.cerner.ccl.analysis.core.violations.MismatchedSubroutineInvocationViolation;
import com.cerner.ccl.analysis.core.violations.MissingSubroutineDeclarationViolation;
import com.cerner.ccl.analysis.data.Violation;

/**
 * Integration tests for {@link SubroutineDeclarationRules}.
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineDeclarationRulesTest extends AbstractJDomTest {
    /**
     * If the XML indicates a missing subroutine declaration, then the analysis new SubroutineDeclarationRules() should
     * indicate as much, too.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAnalyzeMissingDeclaration() throws Exception {
        final Set<Violation> violations = new SubroutineDeclarationRules(toDocument("subroutine-no-declaration.xml"))
                .analyze();
        assertThat(violations).hasSize(1);

        final Violation violation = violations.iterator().next();
        assertThat(violation).isInstanceOf(MissingSubroutineDeclarationViolation.class);
        assertThat(((MissingSubroutineDeclarationViolation) violation).getSubroutineName()).isEqualTo("NO_DECLARE");
        assertThat(violation.getLineNumber()).isEqualTo(3);
    }

    /**
     * Confirms the analyzer finds no missing declarations when subroutines are declared with a namespace.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAnalyzeWithNamespaceAndDeclaration() throws Exception {
        assertThat(new SubroutineDeclarationRules(toDocument("subroutine-with-ns-declaration.xml")).analyze())
                .isEmpty();
    }

    /**
     * If all subroutines in a program have their declarations, then the analyzer should indicate no missing
     * declarations.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAnalyzeWithDeclaration() throws Exception {
        assertThat(new SubroutineDeclarationRules(toDocument("subroutine-with-declaration.xml")).analyze()).isEmpty();
    }

    /**
     * Ensure that a properly declared subroutine within a validate statement is correctly identified as being declared
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAnalyzeWithDeclarationInsideValidate() throws Exception {
        assertThat(
                new SubroutineDeclarationRules(toDocument("subroutine-with-declaration-inside-validate.xml")).analyze())
                        .isEmpty();
    }

    /**
     * Ensure that subroutines whose declaration mismatch their implementation are identified by the analysis
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAnalyzeMismatchedSubroutineDeclarations() throws Exception {
        final Set<Violation> violations = new SubroutineDeclarationRules(
                toDocument("subroutine-with-mismatched-declaration.xml")).analyze();
        assertThat(violations).hasSize(8);
    }

    /**
     * Ensure that subroutines whose invocation mismatch their parameter
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAnalyzeMismatchedSubroutineInvocation() throws Exception {
        final Set<Violation> violations = new SubroutineDeclarationRules(
                toDocument("subroutine-with-mismatched-invocation.xml")).analyze();
        assertThat(violations).hasSize(3);

        assertThat(violations).contains(new MismatchedSubroutineInvocationViolation("mismatchedParamCnt1", 10));
        assertThat(violations).contains(new MismatchedSubroutineInvocationViolation("mismatchedParamCnt3", 30));
        assertThat(violations).contains(new MismatchedSubroutineInvocationViolation("mismatchedParamCnt5", 32));
    }
}
