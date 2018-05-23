package com.cerner.ccl.analysis.core.violations;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.analysis.core.violations.internal.AbstractViolationTest;

/**
 * Unit tests for {@link LargeSequenceStoredToNonFloatingPointNumberViolation}.
 *
 * @author Joshua Hyde
 */

@SuppressWarnings("unused")
public class LargeSequenceStoredToNonFloatingPointNumberViolationTest extends AbstractViolationTest<LargeSequenceStoredToNonFloatingPointNumberViolation> {
    private final String seqResultVariable = "seqResultVariable";
    private final String attemptedStoreVariable = "attemptedStoreVariable";

    /**
     * Construction with a blank sequence result variable should fail.
     */
    @Test
    public void testConstructBlankSeqResultVariable() {
        expect(IllegalArgumentException.class, "seqResultVariable cannot be null or empty");
        new LargeSequenceStoredToNonFloatingPointNumberViolation(" ", attemptedStoreVariable, null);
    }

    /**
     * Construction with a blank storage variable should fail.
     */
    @Test
    public void testConstructBlankStoreVariable() {
        expect(IllegalArgumentException.class, "attemptedStoreVariable cannot be null or empty");
        new LargeSequenceStoredToNonFloatingPointNumberViolation(seqResultVariable, " ", null);
    }

    /**
     * Construction with a {@code null} sequence result variable should fail.
     */
    @Test
    public void testConstructNulleqResultVariable() {
        expect(IllegalArgumentException.class, "seqResultVariable cannot be null or empty");
        new LargeSequenceStoredToNonFloatingPointNumberViolation(null, attemptedStoreVariable, null);
    }

    /**
     * Construction with a {@code null} storage variable should fail.
     */
    @Test
    public void testConstructNullStoreVariable() {
        expect(IllegalArgumentException.class, "attemptedStoreVariable cannot be null or empty");
        new LargeSequenceStoredToNonFloatingPointNumberViolation(seqResultVariable, null, null);
    }

    /**
     * Two violations should be equal if they have the same sequence result variable, independent of case.
     */
    @Test
    public void testEqualsCaseInsensitiveSeqResultVariable() {
        final LargeSequenceStoredToNonFloatingPointNumberViolation first = new LargeSequenceStoredToNonFloatingPointNumberViolation(seqResultVariable, attemptedStoreVariable, null);
        final LargeSequenceStoredToNonFloatingPointNumberViolation second = new LargeSequenceStoredToNonFloatingPointNumberViolation(StringUtils.swapCase(seqResultVariable), attemptedStoreVariable,
                null);
        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    /**
     * Two violations should be if they have the same storage variable name, independent of case.
     */
    @Test
    public void testEqualsCaseInsensitiveStoreVariable() {
        final LargeSequenceStoredToNonFloatingPointNumberViolation first = new LargeSequenceStoredToNonFloatingPointNumberViolation(seqResultVariable, attemptedStoreVariable, null);
        final LargeSequenceStoredToNonFloatingPointNumberViolation second = new LargeSequenceStoredToNonFloatingPointNumberViolation(seqResultVariable, StringUtils.swapCase(attemptedStoreVariable),
                null);
        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    /**
     * Two violations should be inequal if they have different sequence result variables.
     */
    @Test
    public void testEqualsDifferentSeqResultVariable() {
        final LargeSequenceStoredToNonFloatingPointNumberViolation first = new LargeSequenceStoredToNonFloatingPointNumberViolation(seqResultVariable, attemptedStoreVariable, null);
        final LargeSequenceStoredToNonFloatingPointNumberViolation second = new LargeSequenceStoredToNonFloatingPointNumberViolation(StringUtils.reverse(seqResultVariable), attemptedStoreVariable,
                null);
        assertThat(first).isNotEqualTo(second);
        assertThat(second).isNotEqualTo(first);
    }

    /**
     * Two violations should be inequal if they have different storage variables.
     */
    @Test
    public void testEqualsDifferentStoreVariable() {
        final LargeSequenceStoredToNonFloatingPointNumberViolation first = new LargeSequenceStoredToNonFloatingPointNumberViolation(seqResultVariable, attemptedStoreVariable, null);
        final LargeSequenceStoredToNonFloatingPointNumberViolation second = new LargeSequenceStoredToNonFloatingPointNumberViolation(seqResultVariable, StringUtils.reverse(attemptedStoreVariable),
                null);
        assertThat(first).isNotEqualTo(second);
        assertThat(second).isNotEqualTo(first);
    }

    @Override
    protected LargeSequenceStoredToNonFloatingPointNumberViolation createViolation(final Integer lineNumber) {
        return new LargeSequenceStoredToNonFloatingPointNumberViolation(seqResultVariable, attemptedStoreVariable, lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "LARGE_SEQ_TO_NON_FLOAT";
    }

}
