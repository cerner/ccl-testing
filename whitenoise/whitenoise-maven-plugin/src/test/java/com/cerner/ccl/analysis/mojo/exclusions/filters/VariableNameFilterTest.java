package com.cerner.ccl.analysis.mojo.exclusions.filters;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.codehaus.plexus.util.StringUtils;
import org.junit.Test;

import com.cerner.ccl.analysis.data.VariableViolation;

/**
 * Unit tests for {@link VariableNameFilter}.
 *
 * @author Joshua Hyde
 *
 */

public class VariableNameFilterTest extends AbstractViolationFilterUnitTest<VariableNameFilter> {
    private final String variableName = "a.variable.name";
    private final VariableNameFilter filter = new VariableNameFilter(variableName);

    /**
     * Construction with a {@code null} variable name should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullVariableName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new VariableNameFilter(null);
        });
        assertThat(e.getMessage()).isEqualTo("Variable name cannot be null.");
    }

    /**
     * Test VariableViolation exclusions.
     */
    @Test
    public void testExcludeVariableViolation() {
        final VariableViolation doesMatch = mock(VariableViolation.class);
        when(doesMatch.getVariableName()).thenReturn(StringUtils.swapCase(variableName));

        final VariableViolation noMatch = mock(VariableViolation.class);
        when(noMatch.getVariableName()).thenReturn(StringUtils.reverse(variableName));

        assertThat(filter.exclude("a.variable.name", doesMatch)).isTrue();
        assertThat(filter.exclude("another.variable.name", noMatch)).isFalse();
    }

    @Override
    protected VariableNameFilter getViolationFilter() {
        return filter;
    }

}
