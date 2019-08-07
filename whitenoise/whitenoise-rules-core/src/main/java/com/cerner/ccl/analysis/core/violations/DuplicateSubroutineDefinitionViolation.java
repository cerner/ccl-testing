package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.SubroutineViolation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link SubroutineViolation} detailing when a subroutine has been defined twice with two different implementations
 * <p>
 *
 * @author Jeff Wiedemann
 *
 */

public class DuplicateSubroutineDefinitionViolation implements SubroutineViolation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("DUPLICATE_SUBROUTINE_DEFINITION");
    private final String subroutineName;
    private final Integer lineNumber;

    /**
     * Create a violation for a missing subroutine declaration.
     *
     * @param subroutineName
     *            The name of the subroutine that has been duplicately defined
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     * @throws IllegalArgumentException
     *             If the given subroutine name is {@code null}.
     */
    public DuplicateSubroutineDefinitionViolation(final String subroutineName, final Integer lineNumber) {
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

        if (!(obj instanceof DuplicateSubroutineDefinitionViolation)) {
            return false;
        }

        final DuplicateSubroutineDefinitionViolation other = (DuplicateSubroutineDefinitionViolation) obj;
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
        return getSubroutineName() + " has been defined twice with two different implementations";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViolationExplanation() {
        return "Unless you are carefully managing the scope of subroutine definitions, it is not recommended to have two different implementations "
                + "of the same subroutine. Consider consolidating the logic to a single consistent routine or rename the subroutine to avoid "
                + "conflicting implementations";
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
