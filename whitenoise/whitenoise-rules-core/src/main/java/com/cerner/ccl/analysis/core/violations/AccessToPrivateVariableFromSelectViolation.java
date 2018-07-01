package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.SubroutineViolation;
import com.cerner.ccl.analysis.data.VariableViolation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link SubroutineViolation} detailing when a private variable is read from or written two from a select statement
 * <p>
 *
 * @author Jeff Wiedemann
 *
 */

public class AccessToPrivateVariableFromSelectViolation implements VariableViolation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("PRIVATE_VARIABLE_IN_SELECT");
    private final String variableName;
    private final Integer lineNumber;

    /**
     * Create a violation for a missing subroutine declaration.
     *
     * @param variableName
     *            The name of the subroutine that has been defined multiple times.
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     * @throws IllegalArgumentException
     *             If the given subroutine name is {@code null}.
     */
    public AccessToPrivateVariableFromSelectViolation(final String variableName, final Integer lineNumber) {
        if (variableName == null)
            throw new IllegalArgumentException("Variable name cannot be null.");

        this.variableName = variableName;
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof AccessToPrivateVariableFromSelectViolation))
            return false;

        final AccessToPrivateVariableFromSelectViolation other = (AccessToPrivateVariableFromSelectViolation) obj;
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
    public String getVariableName() {
        return variableName;
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationDescription() {
        return getVariableName() + " has been declared as private and cannot be read from select";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "Variables which have been defined as private scope from a CCL script are not accessible from a select statement. "
                + "Either the script will throw a runtime CCL-E when an attempt to access the variable is made, or a second instance "
                + "of the variable will be dynamically created within the scope of the select and not be accessible outside of the select.";
    }

    /**
     * {@inheritDoc}
     */
    public ViolationId getViolationId() {
        return VIOLATION_ID;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getVariableName().toUpperCase(Locale.US)).append(lineNumber).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
