package com.cerner.ccl.analysis.core.violations;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} used to describe a situation of an improper use of the CCL {@code cnvtstring()} function.
 * <p>
 * The uniqueness of this violation is a combination of the given option, and the line number - if any - at which it
 * appears.
 *
 * @author Albert Ponraj
 */

public class MissingCnvtStringlengthParamViolation implements Violation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("MISSING_CNVTSTRING_PARAM2");

    private final Integer lineNumber;

    /**
     * Create a violation.
     *
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     * @throws IllegalArgumentException
     *             If any of the given objects, except for the line number, are {@code null}.
     */
    public MissingCnvtStringlengthParamViolation(final Integer lineNumber) {
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof MissingCnvtStringlengthParamViolation))
            return false;

        final MissingCnvtStringlengthParamViolation other = (MissingCnvtStringlengthParamViolation) obj;
        return getLineNumber().equals(other.getLineNumber());
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
        return "cnvtstring() found without length param";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return ""
                + "The use of cnvtstring() without length param is discouraged. The CNVTSTRING() output defaults to 11 characters ,"
                + "If the numeric value contains more than 11 digits a length must be specified to prevent the result from truncating "
                + "to 11 characters";
    }

    /**
     * {@inheritDoc}
     */
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
