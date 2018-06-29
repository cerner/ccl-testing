package com.cerner.ccl.analysis.mojo.exclusions.filters;

import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.mojo.AbstractUnitTest;
import com.cerner.ccl.analysis.mojo.exclusions.filters.ViolationFilterChain.ViolationFilter;

/**
 * Skeleton definition of a class used to test {@link ViolationFilter} implementations.
 * 
 * @author Joshua Hyde
 * 
 * @param <V>
 *            The type of {@link ViolationFilter} to be tested.
 */

public abstract class AbstractViolationFilterUnitTest<V extends ViolationFilter> extends AbstractUnitTest {
    /**
     * Testing for exclusion with a {@code null} script name should fail.
     */
    @Test
    public void testExcludeNullScriptName() {
        expect(IllegalArgumentException.class);
        expect("Script name cannot be null.");
        getViolationFilter().exclude(null, mock(Violation.class));
    }

    /**
     * Testing a {@code null} {@link Violation} for exclusion should fail.
     */
    @Test
    public void testExcludeNullViolation() {
        expect(IllegalArgumentException.class);
        expect("Violation cannot be null.");
        getViolationFilter().exclude("script.name", null);
    }

    /**
     * Retrieve the {@link ViolationFilter} to be tested.
     * 
     * @return A {@link ViolationFilter} to be tested.
     */
    protected abstract V getViolationFilter();
}
