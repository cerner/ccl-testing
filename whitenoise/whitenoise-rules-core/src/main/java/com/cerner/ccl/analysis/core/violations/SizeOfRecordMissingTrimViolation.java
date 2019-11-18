package com.cerner.ccl.analysis.core.violations;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} used to describe a situation of an improper use of the CCL {@code size()} function.
 * <p>
 * The uniqueness of this violation is a combination of the given option, and the line number - if any - at which it
 * appears.
 *
 * @author Albert Ponraj
 */

public class SizeOfRecordMissingTrimViolation implements Violation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("SIZE_OF_RECORD_MISSING_TRIM");

    private final Integer lineNumber;

    /**
     * Create a violation.
     *
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     * @throws IllegalArgumentException
     *             If any of the given objects, except for the line number, are {@code null}.
     */
    public SizeOfRecordMissingTrimViolation(final Integer lineNumber) {
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof SizeOfRecordMissingTrimViolation)) {
            return false;
        }

        final SizeOfRecordMissingTrimViolation other = (SizeOfRecordMissingTrimViolation) obj;
        return getLineNumber().equals(other.getLineNumber());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getLineNumber() {
        return lineNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViolationDescription() {
        return "Size function on string variable should be wrapped with trim function";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViolationExplanation() {
        return "When using the size() function on a string variable, the variable should be wrapped "
                + "with trim(). This ensures that when value is empty size will return 0.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViolationId getViolationId() {
        return VIOLATION_ID;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(lineNumber).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
