package com.cerner.ccl.analysis.core.violations;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.analysis.core.violations.internal.AbstractViolationTest;

/**
 * Unit tests for {@link OverwrittenForLoopIteratorViolation}.
 *
 * @author Joshua Hyde
 */

public class OverwrittenForLoopIteratorViolationTest
        extends AbstractViolationTest<OverwrittenForLoopIteratorViolation> {
    private final String iterator = "an.iterator";

    /**
     * Construction with a {@code null} iterator should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullIterator() {
        expect(IllegalArgumentException.class, "Iterator cannot be null.");
        new OverwrittenForLoopIteratorViolation(null, null);
    }

    /**
     * Two violations with the same iterator - independent of case - should be equal.
     */
    @Test
    public void testEqualsCaseInsensitiveIterator() {
        final OverwrittenForLoopIteratorViolation first = createViolation(null);
        final OverwrittenForLoopIteratorViolation second = new OverwrittenForLoopIteratorViolation(
                StringUtils.swapCase(iterator), null);
        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    /**
     * Two violations with different iterators should be inequal.
     */
    @Test
    public void testEqualsDifferentIterator() {
        final OverwrittenForLoopIteratorViolation first = createViolation(null);
        final OverwrittenForLoopIteratorViolation second = new OverwrittenForLoopIteratorViolation(
                StringUtils.reverse(iterator), null);
        assertThat(first).isNotEqualTo(second);
        assertThat(second).isNotEqualTo(first);
    }

    @Override
    protected OverwrittenForLoopIteratorViolation createViolation(Integer lineNumber) {
        return new OverwrittenForLoopIteratorViolation(iterator, lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "OVERWRITTEN_LOOP_ITERATOR";
    }

}
