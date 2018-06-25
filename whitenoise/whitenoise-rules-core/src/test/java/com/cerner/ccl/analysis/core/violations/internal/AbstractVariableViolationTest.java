package com.cerner.ccl.analysis.core.violations.internal;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.analysis.data.VariableViolation;

/**
 * Skeleton of test for an implementation of {@link VariableViolation}.
 * 
 * @author Joshua Hyde
 * 
 * @param <T>
 *            The implementation of {@link VariableViolation} to be tested.
 */

public abstract class AbstractVariableViolationTest<T extends VariableViolation> extends AbstractViolationTest<T> {
    private final String variableName = "a.variable.name";
    private final Integer lineNumber = Integer.valueOf(5);

    /**
     * Construction with a {@code null} variable name should fail.
     */
    @Test
    public void testConstructNullVariableName() {
        expect(IllegalArgumentException.class, "Variable name cannot be null.");
        createViolation(null, lineNumber);
    }

    /**
     * Two violations with the same variable name - just with different casing - should be equal.
     */
    @Test
    public void testEqualsCaseInsensitiveVariableName() {
        final T first = createViolation(lineNumber);
        final T second = createViolation(StringUtils.swapCase(variableName), lineNumber);
        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    /**
     * Two violations with different variable names should be inequal.
     */
    @Test
    public void testEqualsDifferentVariableName() {
        final T first = createViolation(lineNumber);
        final T second = createViolation(StringUtils.reverse(variableName), lineNumber);
        assertThat(first).isNotEqualTo(second);
        assertThat(second).isNotEqualTo(first);
    }

    /**
     * Test the retrieval of the variable name.
     */
    @Test
    public void testGetVariableName() {
        assertThat(createViolation(lineNumber).getVariableName()).isEqualTo(variableName);
    }

    @Override
    protected T createViolation(Integer lineNumber) {
        return createViolation(variableName, lineNumber);
    }

    /**
     * Create a violation.
     * 
     * @param variableName
     *            The name of the variable.
     * @param lineNumber
     *            The line number on which the violation occurred.
     * @return An instance of the violation
     */
    protected abstract T createViolation(String variableName, Integer lineNumber);
}
