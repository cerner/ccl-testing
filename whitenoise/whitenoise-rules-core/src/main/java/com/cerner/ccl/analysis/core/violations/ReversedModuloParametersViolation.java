package com.cerner.ccl.analysis.core.violations;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} used to describe a situation where a call to the modulo function (call mod()) is made and the
 * first and second parameters appear to be backwards
 * <p>
 *
 * @author Jeff Wiedemann
 */

public class ReversedModuloParametersViolation implements Violation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("REVERSED_MODULO_PARAMETERS");

    private final Integer lineNumber;

    /**
     * Create a violation.
     *
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     */
    public ReversedModuloParametersViolation(final Integer lineNumber) {
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof ReversedModuloParametersViolation))
            return false;

        final ReversedModuloParametersViolation other = (ReversedModuloParametersViolation) obj;
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
        return "Call to MOD function with parameters in reverse order";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "When using the modulo function in CCL the first parameter is usually a variable representing the dividend while the second "
                + "parameter is usually a hardcoded integer indicating the divisor. If these parameters are switched while using this function "
                + "the resulting value is almost always incorrect";
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
