package com.cerner.ccl.analysis.mojo.exclusions.filters;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.codehaus.plexus.util.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.cerner.ccl.analysis.data.Violation;

/**
 * Unit tests for {@link ScriptNameFilter}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class ScriptNameFilterTest extends AbstractViolationFilterUnitTest<ScriptNameFilter> {
    private final String scriptName = getClass().getSimpleName();
    private final ScriptNameFilter filter = new ScriptNameFilter(scriptName);
    @Mock
    private Violation violation;

    /**
     * Construction with a {@code null} script name should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullScriptName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new ScriptNameFilter(null);
        });
        assertThat(e.getMessage()).isEqualTo("Script name cannot be null.");
    }

    /**
     * Test exclusion, case-insensitive, by script name.
     */
    @Test
    public void testExclude() {
        assertThat(filter.exclude(StringUtils.swapCase(scriptName), violation)).isTrue();
    }

    /**
     * If the script name does not match, even ignoring case, then the filter should not exclude it.
     */
    @Test
    public void testExcludeNotEquals() {
        assertThat(filter.exclude(StringUtils.reverse(scriptName), violation)).isFalse();
    }

    @Override
    protected ScriptNameFilter getViolationFilter() {
        return filter;
    }
}
