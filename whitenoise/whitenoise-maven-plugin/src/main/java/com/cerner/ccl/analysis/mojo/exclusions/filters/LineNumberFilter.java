package com.cerner.ccl.analysis.mojo.exclusions.filters;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.mojo.exclusions.filters.ViolationFilterChain.ViolationFilter;

/**
 * A {@link ViolationFilter} that will filter out a violation by line number. If the violation has no
 * {@link Violation#getLineNumber() line number} associated with it, then this filter will not mark it as excluded.
 *
 * @author Joshua Hyde
 *
 */

public class LineNumberFilter implements ViolationFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LineNumberFilter.class);
    private final int lineNumber;

    /**
     * Create a line number filter.
     *
     * @param lineNumber
     *            The number of the line by which to filter out a violation.
     */
    public LineNumberFilter(final int lineNumber) {
        this.lineNumber = lineNumber;
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

        if (violation.getLineNumber() == null) {
            LOGGER.warn(
                    "An exclusion of line number {} was set, but violation {} has no line number information available.",
                    Integer.valueOf(lineNumber), violation);
            return false;
        }

        return lineNumber == violation.getLineNumber().intValue();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
