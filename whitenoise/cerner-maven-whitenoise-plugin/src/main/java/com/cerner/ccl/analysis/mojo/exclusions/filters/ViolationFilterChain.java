package com.cerner.ccl.analysis.mojo.exclusions.filters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A chain of {@link ViolationFilter} objects representing the criteria of exclusion for a violation. This chain operates by ANDing all of the evaluations of the individual filters composing the chain
 * and returning the result.
 *
 * @author Joshua Hyde
 *
 */

public class ViolationFilterChain {
    /**
     * A builder used to construct a {@link ViolationFilterChain}.
     *
     * @author Joshua Hyde
     *
     */
    public static class Builder {
        private String scriptName;
        private String violationId;
        private String variableName;
        private String subroutineName;
        private Integer lineNumber;

        /**
         * Private constructor to prevent direct instantiation.
         */
        private Builder() {
        }

        /**
         * Build the filter chain.
         *
         * @return A {@link ViolationFilterChain}.
         */
        @SuppressWarnings("synthetic-access")
        public ViolationFilterChain build() {
            return new ViolationFilterChain(scriptName, violationId, variableName, subroutineName, lineNumber);
        }

        /**
         * Add a {@link LineNumberFilter}.
         *
         * @param lineNumber
         *            The line number to be filtered.
         * @return This builder.
         */
        public Builder withLineNumber(final int lineNumber) {
            this.lineNumber = Integer.valueOf(lineNumber);
            return this;
        }

        /**
         * Add a {@link ScriptNameFilter} to the chain.
         *
         * @param scriptName
         *            The name of the script to be filtered.
         * @return This builder.
         * @throws IllegalArgumentException
         *             If the given script name is {@code null}.
         */
        public Builder withScriptName(final String scriptName) {
            if (scriptName == null)
                throw new IllegalArgumentException("Script name cannot be null.");

            this.scriptName = scriptName;
            return this;
        }

        /**
         * Add a {@link SubroutineNameFilter}.
         *
         * @param subroutineName
         *            The name of the subroutine filter.
         * @return This builder.
         * @throws IllegalArgumentException
         *             If the given subroutine name is {@code null}.
         */
        public Builder withSubroutineName(final String subroutineName) {
            if (subroutineName == null)
                throw new IllegalArgumentException("Subroutine name cannot be null.");

            this.subroutineName = subroutineName;
            return this;
        }

        /**
         * Add a {@link VariableNameFilter}.
         *
         * @param variableName
         *            The name of the variable to be filtered.
         * @return This builder.
         * @throws IllegalArgumentException
         *             If the given variable name is {@code null}.
         */
        public Builder withVariableName(final String variableName) {
            if (variableName == null)
                throw new IllegalArgumentException("Variable name cannot be null.");

            this.variableName = variableName;
            return this;
        }

        /**
         * Add a {@link ViolationIdFilter} to the chain.
         *
         * @param violationId
         *            The ID of the violation.
         * @return This builder.
         * @throws IllegalArgumentException
         *             If the given violation is {@code null} or is not qualified (e.g., it must be {@code <namespace>.<namespace identifier>}).
         */
        public Builder withViolationId(final String violationId) {
            if (violationId == null)
                throw new IllegalArgumentException("Violation ID cannot be null.");

            if (violationId.indexOf('.') < 0)
                throw new IllegalArgumentException("Violation ID must be fully qualified name: " + violationId);

            this.violationId = violationId;
            return this;
        }
    }

    /**
     * Definition of an object that specifies a single criteria for excluding a violation reported for a script.
     *
     * @author Joshua Hyde
     *
     */
    public interface ViolationFilter {
        /**
         * Determine whether or not a violation should be excluded based on this specific filter's criteria.
         *
         * @param scriptName
         *            The name of the script.
         * @param violation
         *            The {@link Violation} that was reported.
         * @return {@code true} if this filter's criteria indicates that the violation should be exempted; {@code false} if not.
         * @throws IllegalArgumentException
         *             If the script name or {@link Violation} are {@code null}.
         */
        boolean exclude(String scriptName, Violation violation);
    }

    /**
     * Create a new builder to construction a chain.
     *
     * @return A {@link Builder} instance.
     */
    @SuppressWarnings("synthetic-access")
    public static Builder build() {
        return new Builder();
    }

    private final Collection<ViolationFilter> filters = new ArrayList<ViolationFilter>();

    /**
     * Create a filter chain. If any of the given values are not {@code null}, then a corresponding {@link ViolationFilter} will be created.
     *
     * @param scriptName
     *            The name of the script.
     * @param violationId
     *            The ID of the violation.
     * @param variableName
     *            The name of the variable.
     * @param subroutineName
     *            The name of the subroutine.
     * @param lineNumber
     *            The line number.
     */
    private ViolationFilterChain(final String scriptName, final String violationId, final String variableName, final String subroutineName, final Integer lineNumber) {
        if (scriptName != null)
            filters.add(new ScriptNameFilter(scriptName));

        if (violationId != null) {
            final int periodPos = violationId.indexOf('.');
            filters.add(new ViolationIdFilter(new ViolationId(violationId.substring(0, periodPos), violationId.substring(periodPos + 1))));
        }

        if (variableName != null)
            filters.add(new VariableNameFilter(variableName));

        if (subroutineName != null)
            filters.add(new SubroutineNameFilter(subroutineName));

        if (lineNumber != null)
            filters.add(new LineNumberFilter(lineNumber));
    }

    /**
     * Determine whether or not a violation should be excluded.
     *
     * @param scriptName
     *            The name of the script whose violation is being checked.
     * @param violation
     *            The {@link Violation} to be checked for exclusion.
     * @return {@code true} if the violation has been marked for exclusion; {@code false} if it has not.
     * @throws IllegalArgumentException
     *             If the given script name or violation are {@code null}.
     */
    public boolean exclude(final String scriptName, final Violation violation) {
        if (violation == null)
            throw new IllegalArgumentException("Violation cannot be null.");

        if (scriptName == null)
            throw new IllegalArgumentException("Script name cannot be null.");

        if (filters.isEmpty())
            return false;

        boolean exclude = true;
        final Iterator<ViolationFilter> filterIt = filters.iterator();
        while (exclude && filterIt.hasNext())
            exclude &= filterIt.next().exclude(scriptName, violation);

        return exclude;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
