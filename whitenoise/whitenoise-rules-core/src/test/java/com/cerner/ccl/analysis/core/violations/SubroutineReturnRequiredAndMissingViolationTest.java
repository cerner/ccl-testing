package com.cerner.ccl.analysis.core.violations;

import static org.fest.assertions.Assertions.*;
import org.junit.Test;

import com.cerner.ccl.analysis.core.violations.internal.AbstractSubroutineViolationTest;

/**
 * Unit tests for {@link SubroutineReturnRequiredAndMissingViolation}.
 * 
 * @author Joshua Hyde
 */

public class SubroutineReturnRequiredAndMissingViolationTest
        extends AbstractSubroutineViolationTest<SubroutineReturnRequiredAndMissingViolation> {
    private final String subroutineName = "a.subroutine";
    private final Integer invocationLineNumber = Integer.valueOf(24);
    private final Integer subroutineLineNumber = Integer.valueOf(invocationLineNumber + 1);

    /**
     * If one of the violations has a {@code null} invocation line number and the other doesn't, they should be inequal.
     */
    @Test
    public void testEqualsNonNullInvocationLineNumber() {
        final SubroutineReturnRequiredAndMissingViolation withNull = new SubroutineReturnRequiredAndMissingViolation(
                subroutineName, subroutineLineNumber, null);
        final SubroutineReturnRequiredAndMissingViolation withoutNull = new SubroutineReturnRequiredAndMissingViolation(
                subroutineName, subroutineLineNumber, invocationLineNumber);
        assertThat(withNull).isNotEqualTo(withoutNull);
        assertThat(withoutNull).isNotEqualTo(withNull);
    }

    /**
     * If one of the violations has a {@code null} subroutine line number and the other doesn't, they should be inequal.
     */
    @Test
    public void testEqualsNonNullSubroutineLineNumber() {
        final SubroutineReturnRequiredAndMissingViolation withNull = new SubroutineReturnRequiredAndMissingViolation(
                subroutineName, null, invocationLineNumber);
        final SubroutineReturnRequiredAndMissingViolation withoutNull = new SubroutineReturnRequiredAndMissingViolation(
                subroutineName, subroutineLineNumber, invocationLineNumber);
        assertThat(withNull).isNotEqualTo(withoutNull);
        assertThat(withoutNull).isNotEqualTo(withNull);
    }

    /**
     * If both of the violations has a {@code null} invocation line number, they should be equal.
     */
    @Test
    public void testEqualsNullInvocationLineNumber() {
        final SubroutineReturnRequiredAndMissingViolation first = new SubroutineReturnRequiredAndMissingViolation(
                subroutineName, subroutineLineNumber, null);
        final SubroutineReturnRequiredAndMissingViolation second = new SubroutineReturnRequiredAndMissingViolation(
                subroutineName, subroutineLineNumber, null);
        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
    }

    /**
     * If both of the violations has a {@code null} subroutine line number, they should be equal.
     */
    @Test
    public void testEqualsNullSubroutineLineNumber() {
        final SubroutineReturnRequiredAndMissingViolation first = new SubroutineReturnRequiredAndMissingViolation(
                subroutineName, null, invocationLineNumber);
        final SubroutineReturnRequiredAndMissingViolation second = new SubroutineReturnRequiredAndMissingViolation(
                subroutineName, null, invocationLineNumber);
        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
    }

    @Override
    protected SubroutineReturnRequiredAndMissingViolation createViolation(String subroutineName, Integer lineNumber) {
        return new SubroutineReturnRequiredAndMissingViolation(subroutineName, subroutineLineNumber, lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "SUBROUTINE_RETURN_MISSING";
    }
}
