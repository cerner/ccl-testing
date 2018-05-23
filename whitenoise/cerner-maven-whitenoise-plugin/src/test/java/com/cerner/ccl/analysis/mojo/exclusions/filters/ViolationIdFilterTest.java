package com.cerner.ccl.analysis.mojo.exclusions.filters;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;
import com.cerner.ccl.analysis.mojo.AbstractUnitTest;

/**
 * Unit tests for {@link ViolationIdFilter}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class ViolationIdFilterTest extends AbstractUnitTest {
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
        expect("Violation ID cannot be null.");
        expect(IllegalArgumentException.class);
        new ViolationIdFilter(null);
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
        expect(IllegalArgumentException.class);
        expect("Script name cannot be null.");
        filter.exclude(null, violation);
    }

    /**
     * Testing a {@code null} {@link Violation} for exclusion should fail.
     */
    @Test
    public void testExcludeNullViolation() {
        expect(IllegalArgumentException.class);
        expect("Violation cannot be null.");
        filter.exclude("script.name", null);
    }
}
