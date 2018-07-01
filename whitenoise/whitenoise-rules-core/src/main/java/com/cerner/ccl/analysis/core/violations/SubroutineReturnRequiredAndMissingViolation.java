package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.SubroutineViolation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link SubroutineViolation} detailing when a subroutine is missing a return statement but the code is coded to
 * store the return
 *
 *
 * @author Jeff Wiedemann
 *
 */

public class SubroutineReturnRequiredAndMissingViolation implements SubroutineViolation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("SUBROUTINE_RETURN_MISSING");
    private final String subroutineName;
    private final Integer subroutineLineNumber;
    private final Integer invocationLineNumber;

    /**
     * Create a violation for a missing subroutine declaration.
     *
     * @param subroutineName
     *            The name of the subroutine that is missing a {@code declare} statement.
     * @param subroutineLineNumber
     *            An {@link Integer} representing the line at which the subroutine was defined, if known.
     * @param invocationLineNumber
     *            An {@link Integer} representing the line at which the in-violation subroutine was invoked.
     * @throws IllegalArgumentException
     *             If the given subroutine name is {@code null}.
     */
    public SubroutineReturnRequiredAndMissingViolation(final String subroutineName, final Integer subroutineLineNumber,
            final Integer invocationLineNumber) {
        if (subroutineName == null)
            throw new IllegalArgumentException("Subroutine name cannot be null.");

        this.subroutineName = subroutineName;
        this.subroutineLineNumber = subroutineLineNumber != null ? subroutineLineNumber : 0;
        this.invocationLineNumber = invocationLineNumber != null ? invocationLineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof SubroutineReturnRequiredAndMissingViolation))
            return false;

        final SubroutineReturnRequiredAndMissingViolation other = (SubroutineReturnRequiredAndMissingViolation) obj;
        return getSubroutineName().equalsIgnoreCase(other.getSubroutineName())
                && invocationLineNumber.equals(other.invocationLineNumber)
                && subroutineLineNumber.equals(other.subroutineLineNumber);
    }

    /**
     * {@inheritDoc}
     */
    public Integer getLineNumber() {
        return invocationLineNumber;
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationDescription() {
        return getSubroutineName() + " invoked from line [" + invocationLineNumber
                + "] appears to have no return statement but is required by invocation";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "A subroutine which does not return a value but is called in a manner in which the return value appears to be required "
                + "is problemmatic. CCL will not explicitly detect this condition at compile or runtime; therefore, you might experience "
                + "unexpected results if the return value is consumed. If the return is not required, consider refactoring the subroutine "
                + "to use the 'CALL mySubroutine()' style invocation or store the result to a variable beginning with 'dummy' to avoid this "
                + "violation. (I.E. dummyVar). If the subroutine appears to correctly return values, ensure that all logic paths appropriately return "
                + "values. This rule will fire when the last line of the subroutine requiring a return is not a return statement. If all return "
                + "statements are nested within if-else blocks of code then you are likely dealing with an anti-pattern know as the arrowhead "
                + "anti-pattern. If possible the code should be refactored, or at least, not made worse.";
    }

    /**
     * {@inheritDoc}
     */
    public ViolationId getViolationId() {
        return VIOLATION_ID;
    }

    /**
     * {@inheritDoc}
     */
    public String getSubroutineName() {
        return subroutineName;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(subroutineName.toUpperCase(Locale.US)).append(invocationLineNumber)
                .append(subroutineLineNumber).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
