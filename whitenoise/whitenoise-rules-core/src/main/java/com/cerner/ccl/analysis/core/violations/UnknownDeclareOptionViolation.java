package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.VariableViolation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A violation class for an unknown declare option.
 *
 */
public class UnknownDeclareOptionViolation implements VariableViolation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("UNKNOWN_DECLARE_OPTION");
    private final String variableName;
    private final Integer lineNumber;

    /**
     * Create a violation for an unknown declare option
     *
     * @param variableName
     *            The name of the variable with the unknown option.
     * @param lineNumber
     *            The line number where the violation occurs.
     */
    public UnknownDeclareOptionViolation(final String variableName, final Integer lineNumber) {
        if (variableName == null) {
            throw new IllegalArgumentException("Variable name cannot be null.");
        }

        this.variableName = variableName;
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof UnknownDeclareOptionViolation)) {
            return false;
        }

        final UnknownDeclareOptionViolation other = (UnknownDeclareOptionViolation) obj;
        return getVariableName().equalsIgnoreCase(other.getVariableName())
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
        return getVariableName() + " has been declared with an invalid option.";
    }

    @Override
    public String getViolationExplanation() {
        return "Variables which are declared should use valid options such as 'protect', 'private', 'noconstant', 'constant'";
    }

    @Override
    public ViolationId getViolationId() {
        return VIOLATION_ID;
    }

    @Override
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
