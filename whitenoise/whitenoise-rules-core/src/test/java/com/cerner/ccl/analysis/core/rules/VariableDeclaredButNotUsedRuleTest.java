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
}
