package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.VariableViolation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link VariableViolation} indicating that a variable has been declared but never used
 * <p>
 * This violation bases its uniqueness on a combination of the given variable name, and (if available) the line number.
 * The comparison of variable names is case-insensitive.
 *
 * @author Jeff Wiedemann
 *
 */

public class VariableDeclaredButNotUsedViolation implements VariableViolation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("UNUSED_VARIABLE_DECLARATION");
    private final String variableName;
    private final Integer lineNumber;

    /**
     * Create a variable initialization violation.
     *
     * @param variableName
     *            The name of the variable that was improperly initialized.
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     * @throws IllegalArgumentException
     *             If any of the given objects, except for the line number, are {@code null}, or if the variable name is
     *             blank.
     */
    public VariableDeclaredButNotUsedViolation(final String variableName, final Integer lineNumber) {
        if (variableName == null)
            throw new IllegalArgumentException("Variable name cannot be null.");

        this.variableName = variableName;
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof VariableDeclaredButNotUsedViolation))
            return false;

        final VariableDeclaredButNotUsedViolation other = (VariableDeclaredButNotUsedViolation) obj;
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
        return getVariableName() + " is declared but never used";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "Variables which are declared but never used are often left over from refactored code and contribute to clutter and readability "
                + "problems in the script. Consider removing the declaration to the unused variable.";
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
        return new HashCodeBuilder().append(variableName.toLowerCase(Locale.US)).append(getLineNumber()).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
