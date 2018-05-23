package com.cerner.ccl.analysis.core.violations;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.analysis.core.violations.internal.AbstractVariableViolationTest;

/**
 * Unit tests for {@link InvalidVariableInitializationViolation}.
 *
 * @author Joshua Hyde
 */

public class InvalidVariableInitializationViolationTest extends AbstractVariableViolationTest<InvalidVariableInitializationViolation> {
    private final String variableName = "a.variable";
    private final String initializationValue = "24";

    /**
     * Construction with a {@code null} initialization value should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullInitializationValue() {
        expect(IllegalArgumentException.class, "Initialization value cannot be null.");
        new InvalidVariableInitializationViolation(variableName, null, null);
    }

    /**
     * Two violations with different initialization values should be inequal.
     */
    @Test
    public void testEqualsDifferentInitializationValue() {
        final InvalidVariableInitializationViolation first = createViolation(variableName, null);
        final InvalidVariableInitializationViolation second = new InvalidVariableInitializationViolation(variableName, StringUtils.reverse(initializationValue), null);
        assertThat(first).isNotEqualTo(second);
        assertThat(second).isNotEqualTo(first);
    }

    /**
     * Test the retrieval of the initialization value.
     */
    @Test
    public void testGetInitializationValue() {
        assertThat(new InvalidVariableInitializationViolation(variableName, initializationValue, null).getInitializationValue()).isEqualTo(initializationValue);
    }

    @Override
    protected InvalidVariableInitializationViolation createViolation(final String variableName, final Integer lineNumber) {
        return new InvalidVariableInitializationViolation(variableName, initializationValue, lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "INVALID_VARIABLE_INITIALIZATION";
    }

}
