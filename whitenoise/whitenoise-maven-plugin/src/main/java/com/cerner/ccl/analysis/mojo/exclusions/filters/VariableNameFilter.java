package com.cerner.ccl.analysis.mojo.exclusions.filters;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.VariableViolation;
import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.mojo.exclusions.filters.ViolationFilterChain.ViolationFilter;

/**
 * A {@link ViolationFilter} that excludes a violation by its variable name. It operates on the following criteria:
 * <ol>
 * <li>If the given {@link Violation} is a {@link VariableViolation}, then a comparison is made against its
 * {@link VariableViolation#getVariableName() variable name}.</li>
 * <li>If it has a {@code getVariableName()} method that returns a {@link String}, a comparison is made against
 * that.</li>
 * <li>If it has a field called {@code VariableName}, then the comparison is made against that.</li>
 * <li>If none of these hold true, then no comparison is made and the violation is not excluded.</li>
 * </ol>
 * The comparison of variable names is case-insensitive.
 *
 * @author Joshua Hyde
 *
 */

public class VariableNameFilter extends AbstractPropertyReflectingFilter {
    private final String variableName;

    /**
     * Create a filter that filters by variable name.
     *
     * @param variableName
     *            The name of the variable.
     * @throws IllegalArgumentException
     *             If the given variable name is {@code null}.
     */
    public VariableNameFilter(final String variableName) {
        if (variableName == null) {
            throw new IllegalArgumentException("Variable name cannot be null.");
        }

        this.variableName = variableName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exclude(final String scriptName, final Violation violation) {
        if (scriptName == null) {
            throw new IllegalArgumentException("Script name cannot be null.");
        }

        if (violation == null) {
            throw new IllegalArgumentException("Violation cannot be null.");
        }

        if (violation instanceof VariableViolation) {
            return variableName.equalsIgnoreCase(((VariableViolation) violation).getVariableName());
        }

        final String internalValue = getInternalValue(violation, "variableName");
        return variableName.equalsIgnoreCase(internalValue);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
