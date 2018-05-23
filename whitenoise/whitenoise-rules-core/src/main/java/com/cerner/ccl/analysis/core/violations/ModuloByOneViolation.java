package com.cerner.ccl.analysis.core.violations;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} used to describe a situation where a call to the modulo function (call mod()) is made and the
 * second parameter is 1 which always yields a result of 0
 * <p>
 *
 * @author Jeff Wiedemann
 */

public class ModuloByOneViolation implements Violation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("MODULO_BY_ONE");

    private final Integer lineNumber;

    /**
     * Create a violation.
     *
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     */
    public ModuloByOneViolation(final Integer lineNumber) {
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof ModuloByOneViolation))
            return false;

        final ModuloByOneViolation other = (ModuloByOneViolation) obj;
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
        return "Call to MOD function with divisor of one which always returns zero";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "When using the modulo function with a divisor of one, the resulting value will always be zero. Inspect the modulo " +
        		"function call to ensure that it is coded as intended";
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
