package com.cerner.ccl.analysis.core.violations;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} used to describe a situation of an improper use of the CCL
 * {@code uar_srvsetstring()} function.
 * <p>
 * The uniqueness of this violation is a combination of the given option, and the line
 * number - if any - at which it appears.
 *
 * @author Albert Ponraj
 */

public class NulltermViolation implements Violation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("UAR_SRVSETSTRING_MISSING_NULLTERM");

    private final Integer lineNumber;

    /**
     * Create a violation.
     *
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was
     *            encountered, if applicable.
     * @throws IllegalArgumentException
     *             If any of the given objects, except for the line number, are
     *             {@code null}.
     */
    public NulltermViolation(final Integer lineNumber) {
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) return true;

        if (!(obj instanceof NulltermViolation)) return false;

        final NulltermViolation other = (NulltermViolation) obj;
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
        return "uar_srvsetstring() param3 missing nullterm() wrapping";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "When using vc strings as input into a UAR, the nullterm function should be wrapped around the variable. " + "Garbage characters get injected in CCL when using SRV_UAR calls without the nullterm() usage.";
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
