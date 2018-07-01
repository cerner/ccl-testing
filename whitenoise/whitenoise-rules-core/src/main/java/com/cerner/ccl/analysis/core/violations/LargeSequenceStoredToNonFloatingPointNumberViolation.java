package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} indicating that the resulting value of a SEQ() function (and a corresponding cnvtreal) was stored
 * to a variable that was not define to store floating point numbers
 * <p>
 *
 * @author Jeff Wiedemann
 *
 */

public class LargeSequenceStoredToNonFloatingPointNumberViolation implements Violation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("LARGE_SEQ_TO_NON_FLOAT");
    private final Integer lineNumber;
    private final String seqResultVariable;
    private final String attemptedStoreVariable;

    /**
     * Create a large sequence stored to non float violation.
     *
     * @param seqResultVariable
     *            The name of the variable holding the returned value of the SEQ function
     * @param attemptedStoreVariable
     *            The name of the variable to which the seqResultVariable was attempted to be stored. This is the
     *            non-float value in violation
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     */
    public LargeSequenceStoredToNonFloatingPointNumberViolation(final String seqResultVariable,
            final String attemptedStoreVariable, final Integer lineNumber) {
        if (StringUtils.isBlank(seqResultVariable))
            throw new IllegalArgumentException("seqResultVariable cannot be null or empty");

        if (StringUtils.isBlank(attemptedStoreVariable))
            throw new IllegalArgumentException("attemptedStoreVariable cannot be null or empty");

        this.seqResultVariable = seqResultVariable;
        this.attemptedStoreVariable = attemptedStoreVariable;
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof LargeSequenceStoredToNonFloatingPointNumberViolation))
            return false;

        final LargeSequenceStoredToNonFloatingPointNumberViolation other = (LargeSequenceStoredToNonFloatingPointNumberViolation) obj;
        return StringUtils.equalsIgnoreCase(seqResultVariable, other.seqResultVariable)
                && StringUtils.equalsIgnoreCase(attemptedStoreVariable, other.attemptedStoreVariable)
                && getLineNumber().equals(other.getLineNumber());
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
        return "Attempt to store result of SEQ() into non-floating point variable with assignment "
                + attemptedStoreVariable + " = " + seqResultVariable;
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "An attempt to store the resulting return value of the Oracle SEQ() function into a variable that was not defined as an F8 will "
                + "overflow and not function correctly when the sequence exceeds 2^31. This is commonly referred to as the large sequence problem. "
                + "Explicitly declare a CCL floating point variable when attempting to store the return value of the SEQ() function.";

    }

    /**
     * {@inheritDoc}
     */
    public ViolationId getViolationId() {
        return VIOLATION_ID;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(StringUtils.lowerCase(seqResultVariable, Locale.US))
                .append(StringUtils.lowerCase(attemptedStoreVariable, Locale.US)).append(getLineNumber()).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
