package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.VariableViolation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link VariableViolation} indicating that a variable has been initialized with an invalid value.
 * <p>
 * This violation bases its uniqueness on a combination of the given variable name, initialization value, and (if
 * available) the line number. The comparison of variable names is case-insensitive.
 *
 * @author Joshua Hyde
 *
 */

public class InvalidVariableInitializationViolation implements VariableViolation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("INVALID_VARIABLE_INITIALIZATION");
    private final String variableName;
    private final String initializationValue;
    private final Integer lineNumber;

    /**
     * Create a variable initialization violation.
     *
     * @param variableName
     *            The name of the variable that was improperly initialized.
     * @param initializationValue
     *            The value to which the variable was improperly initialized.
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     * @throws IllegalArgumentException
     *             If any of the given objects, except for the line number, are {@code null}.
     */
    public InvalidVariableInitializationViolation(final String variableName, final String initializationValue,
            final Integer lineNumber) {
        if (variableName == null) {
            throw new IllegalArgumentException("Variable name cannot be null.");
        }

        if (initializationValue == null) {
            throw new IllegalArgumentException("Initialization value cannot be null.");
        }

        this.variableName = variableName;
        this.initializationValue = initializationValue;
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof InvalidVariableInitializationViolation)) {
            return false;
        }

        final InvalidVariableInitializationViolation other = (InvalidVariableInitializationViolation) obj;
        return getVariableName().equalsIgnoreCase(other.getVariableName())
                && getInitializationValue().equals(other.getInitializationValue())
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
        return getVariableName() + " is improperly intitialized to the value '" + getInitializationValue() + "'";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViolationExplanation() {
        return "If the value used to initialize a variable does not match a type that CCL's declarations can tolerate, this can result in a runtime error.";
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
    public String getVariableName() {
        return variableName;
    }

    /**
     * Get the value to which the variable was improperly initialized.
     *
     * @return The value of the initialization.
     */
    public String getInitializationValue() {
        return initializationValue;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(variableName.toLowerCase(Locale.US)).append(initializationValue)
                .append(getLineNumber()).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
