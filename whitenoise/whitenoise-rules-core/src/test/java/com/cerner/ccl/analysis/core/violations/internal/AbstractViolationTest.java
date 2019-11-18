package com.cerner.ccl.analysis.core.violations.internal;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.cerner.ccl.analysis.data.Violation;

/**
 * Skeleton definition of a class used to test {@link Violation} implementations.
 *
 * @author Joshua Hyde
 *
 * @param <T>
 *            The type of violation to be tested.
 */

public abstract class AbstractViolationTest<T extends Violation> {
    private final Integer lineNumber = Integer.valueOf(23);

    /**
     * Construction with a {@code null} line number should be okay.
     */
    @Test
    public void testConstructNullLineNumber() {
        final T violation = createViolation(null);
        assertThat(violation.getLineNumber()).isEqualTo(0);
    }

    /**
     * Two violations with different line numbers should be unequal.
     */
    @Test
    public void testEqualsDifferentLineNumber() {
        final T first = createViolation(lineNumber);
        final T second = createViolation(lineNumber + 1);
        assertThat(first).isNotEqualTo(second);
        assertThat(second).isNotEqualTo(first);
    }

    /**
     * If one violation has a {@code null} line number and the other does not, they should be unequal.
     */
    @Test
    public void testEqualsNonNullLineNumber() {
        final T withNull = createViolation(null);
        final T withoutNull = createViolation(lineNumber);
        assertThat(withNull).isNotEqualTo(withoutNull);
        assertThat(withoutNull).isNotEqualTo(withNull);
        /*
         * This is necessarily guaranteed by the hash code contract, but is more to ensure the proper handling of null
         * line numbers
         */
        assertThat(withNull.hashCode()).isNotEqualTo(withoutNull.hashCode());
    }

    /**
     * If the given object for comparison is not of the same or a child of the class under test, then they should be
     * unequal.
     */
    @Test
    public void testEqualsNotInstance() {
        assertThat(createViolation(lineNumber)).isNotEqualTo(new Object());
    }

    /**
     * A violation should not be equal to {@code null}.
     */
    @Test
    public void testEqualsNull() {
        assertThat(createViolation(lineNumber)).isNotEqualTo(null);
    }

    /**
     * Two violations with {@code null} line numbers should be equal, all other things being equal.
     */
    @Test
    public void testEqualsNullLineNumber() {
        assertThat(createViolation(null)).isEqualTo(createViolation(null));
    }

    /**
     * Two instances created with the same values should be equal.
     */
    @Test
    public void testEqualsSameValues() {
        final T first = createViolation(lineNumber);
        final T second = createViolation(lineNumber);
        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    /**
     * Test that the violation is equal to itself.
     */
    @Test
    public void testEqualsSelf() {
        final T violation = createViolation(lineNumber);
        assertThat(violation).isEqualTo(violation);
    }

    /**
     * All of these violations should be part of the CORE namespace.
     */
    @Test
    public void testGetViolationIdNamespace() {
        assertThat(createViolation(lineNumber).getViolationId().getNamespace()).isEqualTo("CORE");
    }

    /**
     * Test the retrieval of the namespaced identifier.
     */
    @Test
    public void testGetViolationIdNamespacedIdentifier() {
        assertThat(createViolation(lineNumber).getViolationId().getNamespacedIdentifier())
                .isEqualTo(getNamespacedIdentifier());
    }

    /**
     * Create an instance of the violation.
     *
     * @param lineNumber
     *            The line number of the violation's occurrence.
     * @return An instance of the violation.
     */
    protected abstract T createViolation(Integer lineNumber);

    /**
     * Get the expected namespaced identifier of the violation under test.
     *
     * @return The expected namespaced identifier of the violation under test.
     */
    protected abstract String getNamespacedIdentifier();
}
