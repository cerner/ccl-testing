package com.cerner.ccl.analysis.mojo.exclusions.filters;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.mojo.exclusions.filters.ViolationFilterChain.ViolationFilter;

/**
 * A {@link ViolationFilter} that excludes a script by script name.
 *
 * @author Joshua Hyde
 *
 */

public class ScriptNameFilter implements ViolationFilter {
    private final String scriptName;

    /**
     * Create a filter to filter by script name.
     *
     * @param scriptName
     *            The name of the script by which to filter.
     * @throws IllegalArgumentException
     *             If any errors occur during the test run.
     */
    public ScriptNameFilter(final String scriptName) {
        if (scriptName == null) {
            throw new IllegalArgumentException("Script name cannot be null.");
        }

        this.scriptName = scriptName;
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

        return this.scriptName.equalsIgnoreCase(scriptName);
    }

}
