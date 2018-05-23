package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} indicating that a {@code go to} command has no corresponding label inside of a CCL script.
 * <p>
 * This violation dictates its uniquess on the given label name and, if available, its line number; the comparison of label names is case-insensitive.
 *
 * @author Joshua Hyde
 * @author Jeff Wiedemann
 */

public class MissingGoToLabelViolation implements Violation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("MISSING_GO_TO_LABEL");
    private final String labelName;
    private final Integer lineNumber;

    /**
     * Create a violation.
     *
     * @param labelName
     *            The label referenced in the {@code go to} statement that does not exist.
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     * @throws IllegalArgumentException
     *             If the given label name is {@code null}.
     */
    public MissingGoToLabelViolation(final String labelName, final Integer lineNumber) {
        if (labelName == null) {
            throw new IllegalArgumentException("Label name cannot be null.");
        }

        this.labelName = labelName;
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof MissingGoToLabelViolation))
            return false;

        final MissingGoToLabelViolation other = (MissingGoToLabelViolation) obj;
        return getLabelName().equalsIgnoreCase(other.getLabelName()) && getLineNumber().equals(other.getLineNumber());
    }

    /**
     * Get the name of the label referenced in a {@code go to} statement that does not actually exist as a definition within the CCL script.
     *
     * @return The label name.
     */
    public String getLabelName() {
        return labelName;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getLineNumber() {
        return lineNumber;
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationDescription() {
        return "'go to " + labelName + "' found without corresponding #" + labelName + " label";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "A go to statement that references a non-existent label will not function.";
    }

    /**
     * {@inheritDoc}
     */
    public ViolationId getViolationId() {
        return VIOLATION_ID;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(labelName.toLowerCase(Locale.US)).append(lineNumber).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
