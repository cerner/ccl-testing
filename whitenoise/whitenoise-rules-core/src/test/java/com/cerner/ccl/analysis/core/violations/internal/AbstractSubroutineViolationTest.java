package com.cerner.ccl.analysis.core.violations.internal;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.analysis.data.SubroutineViolation;

/**
 * Skeleton definition of a test for {@link SubroutineViolation} objects. This provides common tests for these objects.
 * 
 * @author Joshua Hyde
 * 
 * @param <T>
 *            The type of {@link SubroutineViolation} to be tested.
 */

public abstract class AbstractSubroutineViolationTest<T extends SubroutineViolation> extends AbstractViolationTest<T> {
    private final String subroutineName = "aSubroutineName";
    private final Integer lineNumber = Integer.valueOf(23);

    /**
     * Construction with a {@code null} subroutine name should fail.
     */
    @Test
    public void testConstructNullSubroutineName() {
        expect(IllegalArgumentException.class, "Subroutine name cannot be null.");
        createViolation(null, lineNumber);
    }

    /**
     * The comparison of two violations' subroutine names should be case-insensitive.
     */
    @Test
    public void testEqualsCaseInsensitive() {
        final T first = createViolation(lineNumber);
        final T second = createViolation(StringUtils.swapCase(subroutineName), lineNumber);
        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    /**
     * Two subroutine violations for different subroutines should be inequal.
     */
    @Test
    public void testEqualsDifferentSubroutineName() {
        final T first = createViolation(lineNumber);
        final T second = createViolation(StringUtils.reverse(subroutineName), lineNumber);
        assertThat(first).isNotEqualTo(second);
        assertThat(second).isNotEqualTo(first);
    }

    /**
     * Test the retrieval of the subroutine name.
     */
    @Test
    public void testGetSubroutineName() {
        assertThat(createViolation(lineNumber).getSubroutineName()).isEqualTo(subroutineName);
    }

    @Override
    protected T createViolation(Integer lineNumber) {
        return createViolation(subroutineName, lineNumber);
    }

    /**
     * Create an instance of the violation.
     * 
     * @param subroutineName
     *            The subroutine name.
     * @param lineNumber
     *            The line number of the violation.
     * @return An instance of the violation.
     */
    protected abstract T createViolation(String subroutineName, Integer lineNumber);
}
