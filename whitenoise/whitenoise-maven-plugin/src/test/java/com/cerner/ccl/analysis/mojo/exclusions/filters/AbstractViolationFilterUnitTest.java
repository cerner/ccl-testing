package com.cerner.ccl.analysis.mojo.exclusions.filters;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.mojo.exclusions.filters.ViolationFilterChain.ViolationFilter;

/**
 * Skeleton definition of a class used to test {@link ViolationFilter} implementations.
 *
 * @author Joshua Hyde
 *
 * @param <V>
 *            The type of {@link ViolationFilter} to be tested.
 */

public abstract class AbstractViolationFilterUnitTest<V extends ViolationFilter> {
    /**
     * Testing for exclusion with a {@code null} script name should fail.
     */
    @Test
    public void testExcludeNullScriptName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            getViolationFilter().exclude(null, mock(Violation.class));
        });
        assertThat(e.getMessage()).isEqualTo("Script name cannot be null.");
    }

    /**
     * Testing a {@code null} {@link Violation} for exclusion should fail.
     */
    @Test
    public void testExcludeNullViolation() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            getViolationFilter().exclude("script.name", null);
        });
        assertThat(e.getMessage()).isEqualTo("Violation cannot be null.");
    }

    /**
     * Retrieve the {@link ViolationFilter} to be tested.
     *
     * @return A {@link ViolationFilter} to be tested.
     */
    protected abstract V getViolationFilter();
}
