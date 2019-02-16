package com.cerner.ccl.analysis.core.rules;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.cerner.ccl.analysis.core.AbstractJDomTest;
import com.cerner.ccl.analysis.core.violations.MissingVariableDeclarationViolation;
import com.cerner.ccl.analysis.core.violations.UnknownDeclareOptionViolation;
import com.cerner.ccl.analysis.data.Violation;

/**
 * Integration tests for {@link VariableDeclarationRules}.
 *
 * @author Joshua Hyde
 *
 */

public class VariableDeclarationRuleTest extends AbstractJDomTest {
    /**
     * Test the analysis of a variable usage without a declaration of that variable.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAnalyzeMissingDeclaration() throws Exception {
        final Set<Violation> violations = new VariableDeclarationRules(toDocument("variable-no-declaration.xml"))
                .analyze();
        assertThat(violations).hasSize(1);

        final Violation violation = violations.iterator().next();
        assertThat(violation).isInstanceOf(MissingVariableDeclarationViolation.class);
        assertThat(((MissingVariableDeclarationViolation) violation).getVariableName()).isEqualTo("UNDECLARED_VAR");
        assertThat(violation.getLineNumber()).isEqualTo(3);
    }

    /**
     * Test the analysis of a variable use when it has a declaration of that variable.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAnalyzeWithDeclaration() throws Exception {
        assertThat(new VariableDeclarationRules(toDocument("variable-with-declaration.xml")).analyze()).isEmpty();
    }

    /**
     * Test the analysis of a a variable whose declaration is contained within the subroutine definition. Should
     * recognize two variables set correctly from the subroutine declaration and one that is not
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAnalyzeWithDeclarationFromSubroutine() throws Exception {
        final Set<Violation> violations = new VariableDeclarationRules(
                toDocument("variable-with-declaration-from-subroutine.xml")).analyze();
        assertThat(violations).hasSize(1);

        final Violation violation = violations.iterator().next();
        assertThat(violation).isInstanceOf(MissingVariableDeclarationViolation.class);
        assertThat(violation.getLineNumber()).isEqualTo(14);
    }

    /**
     * Ensure the new VariableDeclarationRules() doesn't fail when encountering sets of reserved CCL keywords
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSettingReservedCCLKeywords() throws Exception {
        assertThat(new VariableDeclarationRules(toDocument("variable-reserved-keywords.xml")).analyze()).isEmpty();
    }

    /**
     * Test the analysis of a a variable whose declaration is contained within the subroutine definition. Should
     * recognize two variables set correctly from the subroutine declaration and one that is not
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testUnknownVariableDeclarationOption() throws Exception {
        final Set<Violation> violations = new VariableDeclarationRules(
                toDocument("variable-unknown-declare-option.xml")).analyze();
        assertThat(violations).hasSize(4);

        assertThat(violations).contains(new UnknownDeclareOptionViolation("VARIABLEONE", 3));
        assertThat(violations).contains(new UnknownDeclareOptionViolation("VARIABLETWO", 4));
        assertThat(violations).contains(new UnknownDeclareOptionViolation("VARIABLETHREE", 5));
        assertThat(violations).contains(new UnknownDeclareOptionViolation("VARIABLEFOUR", 12));
    }

    /**
     * Verifies that subroutine variables of in-line declared subroutines do not get flagged as being undeclared.
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testInLineByReference() throws Exception {
        final Set<Violation> violations = new VariableDeclarationRules(toDocument("in-line-ref.xml")).analyze();
        assertThat(violations).hasSize(0);
    }
}
