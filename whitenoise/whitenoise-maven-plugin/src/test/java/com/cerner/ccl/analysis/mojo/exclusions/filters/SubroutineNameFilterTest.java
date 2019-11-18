package com.cerner.ccl.analysis.mojo.exclusions.filters;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.junit.Test;

import com.cerner.ccl.analysis.data.SubroutineViolation;
import com.cerner.ccl.analysis.data.Violation;

/**
 * Unit tests for {@link SubroutineNameFilter}.
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineNameFilterTest extends AbstractViolationFilterUnitTest<SubroutineNameFilter> {
    private final String subroutineName = "a.subroutine.name";
    private final SubroutineNameFilter filter = new SubroutineNameFilter(subroutineName);

    /**
     * Construction with a {@code null} subroutine name should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullSubroutineName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new SubroutineNameFilter(null);
        });
        assertThat(e.getMessage()).isEqualTo("Subroutine name cannot be null.");
    }

    /**
     * If the given violation is a {@link SubroutineViolation}, then it should merely compare against the violation's
     * getter.
     */
    @Test
    public void testExcludeSubroutineViolation() {
        final SubroutineViolation doesMatch = mock(SubroutineViolation.class);
        when(doesMatch.getSubroutineName()).thenReturn(StringUtils.swapCase(subroutineName));

        final SubroutineViolation noMatch = mock(SubroutineViolation.class);
        when(noMatch.getSubroutineName()).thenReturn(StringUtils.reverse(subroutineName));

        assertThat(filter.exclude("a.script", doesMatch)).isTrue();
        assertThat(filter.exclude("another.script", noMatch)).isFalse();
    }

    /**
     * If the given {@link Violation} is not a {@link SubroutineViolation}, then reflections should be used.
     */
    @Test
    public void testExcludeInternalValue() {
        final List<String> internalValues = new ArrayList<String>(3);
        internalValues.add(StringUtils.swapCase(subroutineName));
        internalValues.add(StringUtils.reverse(subroutineName));
        internalValues.add(null);

        final Violation violation = mock(Violation.class);

        final SubroutineNameFilter toTest = new SubroutineNameFilter(subroutineName) {
            @SuppressWarnings("unchecked")
            @Override
            protected String getInternalValue(final Violation violation, final String propertyName) {
                return internalValues.remove(0);
            }
        };

        assertThat(toTest.exclude("a.script.name", violation)).isTrue();
        assertThat(toTest.exclude("another.script.name", violation)).isFalse();
        assertThat(toTest.exclude("yet.another.", violation)).isFalse();
    }

    @Override
    protected SubroutineNameFilter getViolationFilter() {
        return filter;
    }
}
