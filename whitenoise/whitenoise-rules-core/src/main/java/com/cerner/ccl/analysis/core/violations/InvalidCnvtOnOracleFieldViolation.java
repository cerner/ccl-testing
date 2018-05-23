package com.cerner.ccl.analysis.core.violations;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} indicating that an oracle field has had the cnvtint or cnvtreal function applied on it
 *
 * @author Jeff Wiedemann
 */

public class InvalidCnvtOnOracleFieldViolation implements Violation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("INVALID_CNVT_ON_ORACLE_FIELD");
    private final Integer lineNumber;

    /**
     * Create a violation.
     *
     * @param lineNumber
     *            An {@link Integer} representing the line of the beginning of the while loop with the violation, if applicable.
     */
    public InvalidCnvtOnOracleFieldViolation(final Integer lineNumber) {
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof InvalidCnvtOnOracleFieldViolation))
            return false;

        final InvalidCnvtOnOracleFieldViolation other = (InvalidCnvtOnOracleFieldViolation) obj;
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
        return "Suspicious use of cnvtint() or cnvtreal() on Oracle field";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "The use of cnvtreal(string) or cnvtint(string) on an Oracle database field is discouraged. If the field stores non-numeric " +
        		"values on any row of the table then you run the risk of an Oracle error being raised when a cnvtreal or cnvtint is attempted " +
        		"against a non-numeric value in your result set. You cannot guarantee conditional filters (where field = value) or join path " +
        		"will limit your result set to only numeric values prior to the cnvt function being applied. Environmental variables such as " +
        		"optimization mode or Oracle version will eventually cause your assumption to be proven incorrect. Consider using cnvtstring " +
        		"in CCL or some other method evaluating this field.";
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
