package com.cerner.ccl.analysis.core.violations;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.analysis.core.violations.internal.AbstractViolationTest;

/**
 * Unit tests for {@link MissingGoToLabelViolation}.
 *
 * @author Joshua Hyde
 */

@SuppressWarnings("unused")
public class MissingGoToLabelViolationTest extends AbstractViolationTest<MissingGoToLabelViolation> {
    private final String goToLabel = "error";

    /**
     * Construction with a {@code null} label name should fail.
     */
    @Test
    public void testConstructNullLabelName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new MissingGoToLabelViolation(null, null);
        });
        assertThat(e.getMessage()).isEqualTo("Label name cannot be null.");
    }

    /**
     * Two violations with the same label name - independent of case - should be equal.
     */
    @Test
    public void testEqualsCaseInsensitiveLabelName() {
        final MissingGoToLabelViolation first = new MissingGoToLabelViolation(goToLabel, null);
        final MissingGoToLabelViolation second = new MissingGoToLabelViolation(StringUtils.swapCase(goToLabel), null);
        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    /**
     * Two violations with different label names should be inequal.
     */
    @Test
    public void testEqualsDifferentLabelName() {
        final MissingGoToLabelViolation first = new MissingGoToLabelViolation(goToLabel, null);
        final MissingGoToLabelViolation second = new MissingGoToLabelViolation(StringUtils.reverse(goToLabel), null);
        assertThat(first).isNotEqualTo(second);
        assertThat(second).isNotEqualTo(first);
    }

    @Override
    protected MissingGoToLabelViolation createViolation(final Integer lineNumber) {
        return new MissingGoToLabelViolation(goToLabel, lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "MISSING_GO_TO_LABEL";
    }

}
