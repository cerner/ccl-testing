package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.SubroutineViolation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link SubroutineViolation} detailing when a subroutine's implementation does not match its corresponding
 * {@code declare} statement.
 * <p>
 * The uniqueness of this violation is determined by its given subroutine name and, if available, the line number; the
 * comparison of subroutine names is case-insensitive.
 *
 * @author Joshua Hyde
 *
 */

public class MismatchedSubroutineDeclarationViolation implements SubroutineViolation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("MISMATCHED_SUBROUTINE_DECLARATION");
    private final String subroutineName;
    private final Integer lineNumber;

    /**
     * Create a violation for a missing subroutine declaration.
     *
     * @param subroutineName
     *            The name of the subroutine that is missing a {@code declare} statement.
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     * @throws IllegalArgumentException
     *             If the given subroutine name is {@code null}.
     */
    public MismatchedSubroutineDeclarationViolation(final String subroutineName, final Integer lineNumber) {
        if (subroutineName == null) {
            throw new IllegalArgumentException("Subroutine name cannot be null.");
        }

        this.subroutineName = subroutineName;
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof MismatchedSubroutineDeclarationViolation)) {
            return false;
        }

        final MismatchedSubroutineDeclarationViolation other = (MismatchedSubroutineDeclarationViolation) obj;
        return getSubroutineName().equalsIgnoreCase(other.getSubroutineName())
                && getLineNumber().equals(other.getLineNumber());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getLineNumber() {
        return lineNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViolationDescription() {
        return "The declaration of subroutine " + getSubroutineName() + " does not match it's implementation";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViolationExplanation() {
        return "Subroutines with mismatched declarations cannot enforce typing of parameters and lead to ambiguity about the inputs and outputs of a subroutine.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViolationId getViolationId() {
        return VIOLATION_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSubroutineName() {
        return subroutineName;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(subroutineName.toUpperCase(Locale.US)).append(lineNumber).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
