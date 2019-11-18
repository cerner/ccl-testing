package com.cerner.ccl.analysis.core.violations;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.analysis.core.violations.internal.AbstractViolationTest;

/**
 * Unit tests for {@link SizeOfRecordMemberViolation}.
 *
 * @author Joshua Hyde
 */

public class SizeOfRecordMemberViolationTest extends AbstractViolationTest<SizeOfRecordMemberViolation> {
    private final String option = "option numero dos";

    /**
     * Construction with a {@code null} option should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullOption() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new SizeOfRecordMemberViolation(null, null);
        });
        assertThat(e.getMessage()).isEqualTo("Option cannot be null.");
    }

    /**
     * The comparison of two violations by their option should be case-sensitive.
     */
    @Test
    public void testEqualsCaseSensitiveOption() {
        final SizeOfRecordMemberViolation first = new SizeOfRecordMemberViolation(option, null);
        final SizeOfRecordMemberViolation second = new SizeOfRecordMemberViolation(StringUtils.swapCase(option), null);
        assertThat(first).isNotEqualTo(second);
        assertThat(second).isNotEqualTo(first);
        assertThat(first.hashCode()).isNotEqualTo(second.hashCode());
    }

    @Override
    protected SizeOfRecordMemberViolation createViolation(final Integer lineNumber) {
        return new SizeOfRecordMemberViolation(option, lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "SIZE_OF_RECORD_MEMBER_OPTION";
    }

}
