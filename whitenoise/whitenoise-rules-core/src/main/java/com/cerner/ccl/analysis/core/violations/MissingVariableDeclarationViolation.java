package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.VariableViolation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link VariableViolation} indicating that a variable has been referenced before it has been explicitly declared
 * using a {@code declare} statement.
 * <p>
 * The uniqueness of this violation is dictated by the given variable name and, if available, the line number; the
 * comparison of variable names is case-insensitive.
 *
 * @author Joshua Hyde
 *
 */

public class MissingVariableDeclarationViolation implements VariableViolation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("MISSING_VARIABLE_DECLARATION");
    private final String variableName;
    private final Integer lineNumber;

    /**
     * Create a violation.
     *
     * @param variableName
     *            The name of the variable that was used before it was explicitly declared.
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     * @throws IllegalArgumentException
     *             If the given variable name is {@code null}.
     */
    public MissingVariableDeclarationViolation(final String variableName, final Integer lineNumber) {
        if (variableName == null)
            throw new IllegalArgumentException("Variable name cannot be null.");

        this.variableName = variableName;
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof MissingVariableDeclarationViolation))
            return false;

        final MissingVariableDeclarationViolation other = (MissingVariableDeclarationViolation) obj;
        return getVariableName().equalsIgnoreCase(other.getVariableName())
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
        return "Variable " + getVariableName() + " is referenced, but not declared";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "A variable that is implicitly declared, rather than explicitly, could be typed to a type other than what is later expected in the program.";
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
    public String getVariableName() {
        return variableName;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getVariableName().toLowerCase(Locale.US)).append(getLineNumber())
                .toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
