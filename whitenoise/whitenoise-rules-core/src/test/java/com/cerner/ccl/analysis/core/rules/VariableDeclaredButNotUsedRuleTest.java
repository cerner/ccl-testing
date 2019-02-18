package com.cerner.ccl.analysis.core.rules;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.cerner.ccl.analysis.core.AbstractJDomTest;
import com.cerner.ccl.analysis.core.violations.VariableDeclaredButNotUsedViolation;
import com.cerner.ccl.analysis.data.Violation;

/**
 * Integration tests for {@link VariableDeclaredButNotUsedRules}.
 *
 * @author Jeff Wiedemann
 *
 */

public class VariableDeclaredButNotUsedRuleTest extends AbstractJDomTest {
    /**
     * Ensure that VariableDeclaredButNotUsedRules() appropriately identifies instances where variables are declared but
     * never referenced
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testVariableDeclaredButNotUsed() throws Exception {
        final Set<Violation> violations = new VariableDeclaredButNotUsedRules(
                toDocument("variable-declared-but-not-used.xml")).analyze();
        assertThat(violations).hasSize(7);

        assertThat(violations).contains(new VariableDeclaredButNotUsedViolation("MYUNUSEDVARIABLE1", 5));
        assertThat(violations).contains(new VariableDeclaredButNotUsedViolation("MYUNUSEDVARIABLE2", 6));
        assertThat(violations).contains(new VariableDeclaredButNotUsedViolation("MYUNUSEDVARIABLE3", 7));
        assertThat(violations).contains(new VariableDeclaredButNotUsedViolation("DECLARED", 15));
        assertThat(violations).contains(new VariableDeclaredButNotUsedViolation("DECLARED", 19));
        assertThat(violations).contains(new VariableDeclaredButNotUsedViolation("DECLARED", 31));
        assertThat(violations).contains(new VariableDeclaredButNotUsedViolation("DECLARED1", 52));
    }

    /**
     * Ensure that VariableDeclaredButNotUsedRules() appropriately identifies instances where variables are declared but
     * never referenced and does not create an infinite loop when recursive function calls are involved.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testRecursion() throws Exception {
        final Set<Violation> violations = new VariableDeclaredButNotUsedRules(
                toDocument("variable-declared-but-not-used-recursion.xml")).analyze();
        assertThat(violations).hasSize(2);
        assertThat(violations).contains(new VariableDeclaredButNotUsedViolation("UNUSED_ONE", 8));
        assertThat(violations).contains(new VariableDeclaredButNotUsedViolation("UNUSED_ONE", 25));
    }

    /**
     * Confirms that a variables is not inappropriately flagged as unused when the usage is made in function and the
     * call to that function is wrapped.
     *
     * @throws Exception
     *             Sometimes bad things happen
     */
    @Test
    public void testWrappedFunctionCalls() throws Exception {
        final Set<Violation> violations = new VariableDeclaredButNotUsedRules(toDocument("translate2.xml")).analyze();
        assertThat(violations).hasSize(0);
    }

    /**
     * Confirms that an unused variable violation occurs if the only use of the variable is to set its value inside or
     * outside of a report writer section.
     *
     * @throws Exception
     *             Sometimes bad things happen
     */
    @Test
    public void testReportWriter() throws Exception {
        final Set<Violation> violations = new VariableDeclaredButNotUsedRules(toDocument("report-writer.xml"))
                .analyze();
        assertThat(violations).hasSize(1);
        assertThat(violations).contains(new VariableDeclaredButNotUsedViolation("SOMESTR2", 9));
    }

    /**
     * Confirms that variable usage is recognized through transcending scopes but hidden by an intervening definition
     * and that subroutine calls made from report writer sections are recognized.
     *
     * @throws Exception
     *             Sometimes bad things happen
     */
    @Test
    public void testTranscendingScopes() throws Exception {
        final Set<Violation> violations = new VariableDeclaredButNotUsedRules(toDocument("transcending-scopes.xml"))
                .analyze();
        assertThat(violations).hasSize(1);
        assertThat(violations).contains(new VariableDeclaredButNotUsedViolation("VAR2", 12));
    }

    /**
     * Confirms that a variable not used violation is not flagged if a variable is used to set the value of another
     * variable or record structure member.
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testSetAnotherValue() throws Exception {
        final Set<Violation> violations = new VariableDeclaredButNotUsedRules(toDocument("set-another-value.xml"))
                .analyze();
        for (Violation violation : violations) {
            System.out.println(violation);
        }
        assertThat(violations).hasSize(4);
        assertThat(violations).contains(new VariableDeclaredButNotUsedViolation("NONPUBLIC::USEDVARVAL1", 13));
        assertThat(violations).contains(new VariableDeclaredButNotUsedViolation("OTHERVAR", 16));
        assertThat(violations).contains(new VariableDeclaredButNotUsedViolation("PUBLIC::UNUSED", 17));
        assertThat(violations).contains(new VariableDeclaredButNotUsedViolation("NONPUBLIC::USEDVARVAL1A", 46));
    }
}
