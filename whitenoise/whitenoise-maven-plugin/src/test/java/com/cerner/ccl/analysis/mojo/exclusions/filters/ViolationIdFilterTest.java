package com.cerner.ccl.analysis.mojo.exclusions.filters;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * Unit tests for {@link ViolationIdFilter}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class ViolationIdFilterTest {
    private ViolationIdFilter filter;
    @Mock
    private ViolationId violationId;
    @Mock
    private Violation violation;

    /**
     * Set up the filter for each test.
     */
    @Before
    public void setUp() {
        filter = new ViolationIdFilter(violationId);
    }

    /**
     * Construction with a {@code null} {@link ViolationId} should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullViolationId() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new ViolationIdFilter(null);
        });
        assertThat(e.getMessage()).isEqualTo("Violation ID cannot be null.");
    }

    /**
     * If the violation has a matching ID, then it should be marked as a candidate for exclusion.
     */
    @Test
    public void testExclude() {
        when(violation.getViolationId()).thenReturn(violationId);
        assertThat(filter.exclude("script.name", violation)).isTrue();
    }

    /**
     * If the violation has a non-matching ID, then the filter shouldn't exclude the violation.
     */
    @Test
    public void testExcludeNotEqual() {
        final ViolationId otherId = mock(ViolationId.class);
        when(violation.getViolationId()).thenReturn(otherId);
        assertThat(filter.exclude("some.script.name", violation)).isFalse();
    }

    /**
     * Testing for exclusion with a {@code null} script name should fail.
     */
    @Test
    public void testExcludeNullScriptName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            filter.exclude(null, violation);
        });
        assertThat(e.getMessage()).isEqualTo("Script name cannot be null.");
    }

    /**
     * Testing a {@code null} {@link Violation} for exclusion should fail.
     */
    @Test
    public void testExcludeNullViolation() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            filter.exclude("script.name", null);
        });
        assertThat(e.getMessage()).isEqualTo("Violation cannot be null.");
    }
}
