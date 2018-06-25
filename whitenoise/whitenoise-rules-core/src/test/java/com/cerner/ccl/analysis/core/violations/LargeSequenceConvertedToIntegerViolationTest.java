package com.cerner.ccl.analysis.core.violations;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.analysis.core.violations.internal.AbstractViolationTest;

/**
 * Unit tests for {@link LargeSequenceConvertedToIntegerViolation}.
 * 
 * @author Joshua Hyde
 */

public class LargeSequenceConvertedToIntegerViolationTest
        extends AbstractViolationTest<LargeSequenceConvertedToIntegerViolation> {
    private final String onVariable = "whoops";

    /**
     * Construction with a {@code null} "on variable" name should be fine.
     */
    @Test
    public void testConstructNullOnVariableName() {
        final LargeSequenceConvertedToIntegerViolation violation = new LargeSequenceConvertedToIntegerViolation(null,
                null);
        assertThat(violation.getOnVariable()).isNull();
    }

    /**
     * Two violations with the same "on variable" name - just different cases - should be equal.
     */
    @Test
    public void testEqualsCaseInsensitiveVariableName() {
        final LargeSequenceConvertedToIntegerViolation first = new LargeSequenceConvertedToIntegerViolation(onVariable,
                null);
        final LargeSequenceConvertedToIntegerViolation second = new LargeSequenceConvertedToIntegerViolation(
                StringUtils.swapCase(onVariable), null);
        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    /**
     * Two violations with different variable names should fail.
     */
    @Test
    public void testEqualsDifferentVariableName() {
        final LargeSequenceConvertedToIntegerViolation first = new LargeSequenceConvertedToIntegerViolation(onVariable,
                null);
        final LargeSequenceConvertedToIntegerViolation second = new LargeSequenceConvertedToIntegerViolation(
                StringUtils.reverse(onVariable), null);
        assertThat(first).isNotEqualTo(second);
        assertThat(second).isNotEqualTo(first);
    }

    @Override
    protected LargeSequenceConvertedToIntegerViolation createViolation(final Integer lineNumber) {
        return new LargeSequenceConvertedToIntegerViolation(lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "LARGE_SEQ_CNVTINT";
    }

}
