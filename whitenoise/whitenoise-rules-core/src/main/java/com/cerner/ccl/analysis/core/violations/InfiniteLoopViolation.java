package com.cerner.ccl.analysis.core.violations;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} indicating that a while loop is detected where the conditional clause does not appear to be
 * updated within the body of the while loop and therefore could result in an infinite loop
 *
 * @author Jeff Wiedemann
 */

public class InfiniteLoopViolation implements Violation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("INFINITE_WHILE_LOOP");
    private final Integer lineNumber;

    /**
     * Create a violation.
     *
     * @param lineNumber
     *            An {@link Integer} representing the line of the beginning of the while loop with the violation, if
     *            applicable.
     */
    public InfiniteLoopViolation(final Integer lineNumber) {
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof InfiniteLoopViolation))
            return false;

        final InfiniteLoopViolation other = (InfiniteLoopViolation) obj;
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
        return "Possible infinite while loop. No reference to conditional variable(s) in loop body.";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "While loops must update the conditional variable(s) during each iteration of the loop; failure do so will result in an  "
                + "infinite loop. If the conditional variable(s) are being modified from a subroutine called from within the loop, this rule will "
                + "return a false positive result. In this case it is highly recommended, for the sake of code clairity, that the while loop "
                + "be refactored so that the conditional variable(s) are altered from within the loop body. If the conditional statement is the "
                + "boolean result of a subroutine call, this rule will return a false positive result.";
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
