package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.SubroutineViolation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link SubroutineViolation} detailing when a subroutine is missing its corresponding {@code declare} statement.
 * <p>
 * The uniqueness of this violation is determined by its given subroutine name and, if available, the line number; the
 * comparison of subroutine names is case-insensitive.
 *
 * @author Joshua Hyde
 *
 */

public class MissingSubroutineDeclarationViolation implements SubroutineViolation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("MISSING_SUBROUTINE_DECLARATION");
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
    public MissingSubroutineDeclarationViolation(final String subroutineName, final Integer lineNumber) {
        if (subroutineName == null)
            throw new IllegalArgumentException("Subroutine name cannot be null.");

        this.subroutineName = subroutineName;
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof MissingSubroutineDeclarationViolation))
            return false;

        final MissingSubroutineDeclarationViolation other = (MissingSubroutineDeclarationViolation) obj;
        return getSubroutineName().equalsIgnoreCase(other.getSubroutineName())
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
        return getSubroutineName() + " has no explicit DECLARE statement";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "Subroutines without declarations cannot enforce typing of parameters and lead to ambiguity about the inputs and outputs of a subroutine.";
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
        return new HashCodeBuilder().append(subroutineName.toUpperCase(Locale.US)).append(lineNumber).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
