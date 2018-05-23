package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} indicating that a cnvtint() function was applied to a new sequence returned from
 * Oracle which would violate large sequence logic
 * <p>
 *
 * @author Jeff Wiedemann
 *
 */

public class LargeSequenceConvertedToIntegerViolation implements Violation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("LARGE_SEQ_CNVTINT");
    private final Integer lineNumber;
    private final String onVariable;

    /**
     * Create a large sequence cnvtint violation.
     *
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     */
    public LargeSequenceConvertedToIntegerViolation(final Integer lineNumber) {
        this(null, lineNumber);
    }

    /**
     * Create a large sequence cnvtint violation.
     *
     * @param onVariable
     * 		The name of the variable the cnvtint was done on when not directly done on the seq() function result
     *
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     */
    public LargeSequenceConvertedToIntegerViolation(final String onVariable, final Integer lineNumber) {
        this.onVariable = StringUtils.isEmpty(onVariable) ? null : onVariable;
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof LargeSequenceConvertedToIntegerViolation))
            return false;

        final LargeSequenceConvertedToIntegerViolation other = (LargeSequenceConvertedToIntegerViolation) obj;
        return StringUtils.equalsIgnoreCase(onVariable, other.onVariable)
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
        if (onVariable == null)
            return "CNVTINT function applied to return value of SEQ() function";

        return "CNVTINT on variable " + onVariable + " applied to return value of SEQ() function";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "CNVTINT on the resulting return value of the Oracle SEQ() function will overflow and not function correctly when the sequence " +
                "exceeds 2^31. This is commonly referred to as the large sequence problem. CNVTREAL is the appropriate function to use instead.";

    }

    /**
     * {@inheritDoc}
     */
    public ViolationId getViolationId() {
        return VIOLATION_ID;
    }

    /**
     * Get the name of the variable on which the invalid conversion is being done.
     *
     * @return The name of the variable on which the invalid conversion is being done.
     */
    public String getOnVariable() {
        return onVariable;
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(StringUtils.lowerCase(onVariable, Locale.US)).append(getLineNumber()).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
