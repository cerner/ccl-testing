package com.cerner.ccl.analysis.mojo.exclusions.filters;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.cerner.ccl.analysis.data.Violation;

/**
 * Unit tests for {@link LineNumberFilter}.
 *
 * @author Joshua Hyde
 *
 */

public class LineNumberFilterTest extends AbstractViolationFilterUnitTest<LineNumberFilter> {
    private final int lineNumber = 1337;
    private final LineNumberFilter filter = new LineNumberFilter(lineNumber);

    /**
     * Test the exclusion of a violation by its line number.
     */
    @Test
    public void testExclude() {
        final Violation violation = mock(Violation.class);
        when(violation.getLineNumber()).thenReturn(Integer.valueOf(lineNumber));
        assertThat(filter.exclude("a.script", violation)).isTrue();
    }

    /**
     * If a violation has a different line number, then it should not be excluded.
     */
    @Test
    public void testExcludeNotEquals() {
        final Violation violation = mock(Violation.class);
        when(violation.getLineNumber()).thenReturn(Integer.valueOf(lineNumber + 1));
        assertThat(filter.exclude("another.script", violation)).isFalse();
    }

    /**
     * If a violation has line number zero, it should not be excluded.
     */
    @Test
    public void testExcludeZeroLineNumber() {
        final Violation violation = mock(Violation.class);
        when(violation.getLineNumber()).thenReturn(0);
        assertThat(filter.exclude("a.script", violation)).isFalse();
    }

    /**
     * If a violation has null line number, it should not be excluded.
     */
    @Test
    public void testExcludeNullLineNumber() {
        final Violation violation = mock(Violation.class);
        when(violation.getLineNumber()).thenReturn(null);
        assertThat(filter.exclude("a.script", violation)).isFalse();
    }

    @Override
    protected LineNumberFilter getViolationFilter() {
        return filter;
    }
}
