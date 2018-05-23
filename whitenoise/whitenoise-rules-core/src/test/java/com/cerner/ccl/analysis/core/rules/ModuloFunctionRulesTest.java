package com.cerner.ccl.analysis.core.rules;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.cerner.ccl.analysis.core.AbstractJDomTest;
import com.cerner.ccl.analysis.core.violations.ModuloByOneViolation;
import com.cerner.ccl.analysis.core.violations.ReversedModuloParametersViolation;
import com.cerner.ccl.analysis.data.Violation;

/**
 * Integration tests for {@link ModuloFunctionRules}.
 *
 * @author Jeff Wiedemann
 *
 */

public class ModuloFunctionRulesTest extends AbstractJDomTest {
    /**
     * This test is designed to ensure that for various permutations of how the MOD() function is called in CCL the rule
     * does not identify any false positives
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testCorrectUseOfModuloFunction() throws Exception {
        final Set<Violation> violations = new ModuloFunctionRules(toDocument("correct-modulo-usage.xml"))
                .doMeasuredAnalysis();
        assertThat(violations).hasSize(0);
    }

    /**
     * This test is designed to ensure that for various permutations of how someone might incorrectly reverse the
     * parameters of the MOD() function, the rule appropriately identifies the problems
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testReversedModuloParameters() throws Exception {
        final Set<Violation> violations = new ModuloFunctionRules(toDocument("reversed-modulo-parameters.xml"))
                .doMeasuredAnalysis();
        assertThat(violations).hasSize(3);

        assertThat(violations).contains(new ReversedModuloParametersViolation(13));
        assertThat(violations).contains(new ReversedModuloParametersViolation(15));
        assertThat(violations).contains(new ReversedModuloParametersViolation(17));

    }

    /**
     * This test is designed to ensure that if the MOD() function is specified with a 1 for the second parameter the
     * rule appropriately identifies the issue
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testModuloByOne() throws Exception {
        final Set<Violation> violations = new ModuloFunctionRules(toDocument("modulo-by-one.xml"))
                .doMeasuredAnalysis();
        assertThat(violations).hasSize(3);

        assertThat(violations).contains(new ModuloByOneViolation(13));
        assertThat(violations).contains(new ModuloByOneViolation(15));
        assertThat(violations).contains(new ModuloByOneViolation(17));
    }
}
