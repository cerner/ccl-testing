package com.cerner.ccl.analysis.mojo.exclusions.filters;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;
import com.cerner.ccl.analysis.mojo.exclusions.filters.ViolationFilterChain.ViolationFilter;

/**
 * A {@link ViolationFilter} that will mark a violation as a candidate for exclusion if it matches the given violation ID.
 * 
 * @author Joshua Hyde
 * 
 */

public class ViolationIdFilter implements ViolationFilter {
    private final ViolationId violationId;

    /**
     * Create a filter that filters by violation ID.
     * 
     * @param violationId
     *            The {@link ViolationId} by which this filter is to filter a violation.
     * @throws IllegalArgumentException
     *             If the given violation ID is {@code null}.
     */
    public ViolationIdFilter(final ViolationId violationId) {
        if (violationId == null)
            throw new IllegalArgumentException("Violation ID cannot be null.");

        this.violationId = violationId;
    }

    /**
     * {@inheritDoc}
     */
    public boolean exclude(final String scriptName, final Violation violation) {
        if (scriptName == null)
            throw new IllegalArgumentException("Script name cannot be null.");

        if (violation == null)
            throw new IllegalArgumentException("Violation cannot be null.");

        return violationId.equals(violation.getViolationId());
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
