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
 * @author Joshua Hyde
 * @author Jeff Wiedemann
 */

public class SizeOfRecordMemberViolation implements Violation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("SIZE_OF_RECORD_MEMBER_OPTION");

    private final String option;
    private final Integer lineNumber;

    /**
     * Create a violation.
     *
     * @param option
     *            The option passed in as the second parameter to the {@code size()} function.
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     * @throws IllegalArgumentException
     *             If any of the given objects, except for the line number, are {@code null}.
     */
    public SizeOfRecordMemberViolation(final String option, final Integer lineNumber) {
        if (option == null) {
            throw new IllegalArgumentException("Option cannot be null.");
        }

        this.option = option;
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof SizeOfRecordMemberViolation)) {
            return false;
        }

        final SizeOfRecordMemberViolation other = (SizeOfRecordMemberViolation) obj;
        return getOption().equals(other.getOption()) && getLineNumber().equals(other.getLineNumber());
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
        return "Size function with record list member is most likely not intended to be used with size option "
                + getOption();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViolationExplanation() {
        return "When using the size() function on a record structure dynamic list, the second parameter to the "
                + "size function is almost always required to be 5. This ensures that you are testing for the number"
                + "of occurrences of items within the list.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViolationId getViolationId() {
        return VIOLATION_ID;
    }

    /**
     * Get the option that was passed in as the second parameter to the {@code size()} function.
     *
     * @return The option given to the {@code size()} function.
     */
    public String getOption() {
        return option;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(option).append(lineNumber).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
