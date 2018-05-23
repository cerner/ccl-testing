package com.cerner.ccl.analysis.core.rules;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.cerner.ccl.analysis.core.AbstractJDomTest;
import com.cerner.ccl.analysis.core.violations.InvalidVariableInitializationViolation;
import com.cerner.ccl.analysis.data.Violation;

/**
 * Integration tests for {@link IntegerInitializationRules}.
 *
 * @author Joshua Hyde
 *
 */

public class IntegerInitializationRuleTest extends AbstractJDomTest {
    /**
     * A violation should be generated for when an integer is initialized with a float value.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWithFloatInitialization() throws Exception {
        final Set<Violation> violations = new IntegerInitializationRules(toDocument("i4-float-initialization.xml"))
                .analyze();
        assertThat(violations).hasSize(4);
        assertThat(violations).contains(
                new InvalidVariableInitializationViolation("SOME_CONSTANT_VAR", "1.0", Integer.valueOf(4)),
                new InvalidVariableInitializationViolation("SOME_NONCONSTANT_VAR", "0.0", Integer.valueOf(3)),
                new InvalidVariableInitializationViolation("NS::SOME_OTHER_CONSTANT_VAR", "1.0", Integer.valueOf(6)),
                new InvalidVariableInitializationViolation("NS::SOME_OTHER_NONCONSTANT_VAR", "0.0",
                        Integer.valueOf(5)));
    }

    /**
     * No violation should be generated with an integer is initialized with an integer value.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testwithIntegerInitialization() throws Exception {
        assertThat(new IntegerInitializationRules(toDocument("i4-int-initialization.xml")).analyze()).isEmpty();
    }
}
