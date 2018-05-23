package com.cerner.ccl.analysis.mojo.exclusions.filters;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.SubroutineViolation;
import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.mojo.exclusions.filters.ViolationFilterChain.ViolationFilter;

/**
 * A {@link ViolationFilter} that filters by subroutine name. It operates on the following criteria:
 * <ol>
 * <li>If the given {@link Violation} is a {@link SubroutineViolation}, then a comparison is made against its {@link SubroutineViolation#getSubroutineName() subroutine name}.</li>
 * <li>If it has a {@code getSubroutineName()} method that returns a {@link String}, a comparison is made against that.</li>
 * <li>If it has a field called {@code subroutineName}, then the comparison is made against that.</li>
 * <li>If none of these hold true, then no comparison is made and the violation is not excluded.</li>
 * </ol>
 * The comparison of subroutine names is case-insensitive.
 * 
 * @author Joshua Hyde
 * 
 */

public class SubroutineNameFilter extends AbstractPropertyReflectingFilter {
    private final String subroutineName;

    /**
     * Create a filter that filters by subroutine name.
     * 
     * @param subroutineName
     *            The name of the subroutine by which to filter.
     * @throws IllegalArgumentException
     *             If the given subroutine name is {@code null}.
     */
    public SubroutineNameFilter(final String subroutineName) {
        if (subroutineName == null)
            throw new IllegalArgumentException("Subroutine name cannot be null.");

        this.subroutineName = subroutineName;
    }

    /**
     * {@inheritDoc}
     */
    public boolean exclude(final String scriptName, final Violation violation) {
        if (scriptName == null)
            throw new IllegalArgumentException("Script name cannot be null.");

        if (violation == null)
            throw new IllegalArgumentException("Violation cannot be null.");

        if (violation instanceof SubroutineViolation)
            return subroutineName.equalsIgnoreCase(((SubroutineViolation) violation).getSubroutineName());

        final String internalValue = getInternalValue(violation, "subroutineName");
        return subroutineName.equalsIgnoreCase(internalValue);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
