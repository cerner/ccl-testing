package com.cerner.ccl.cdoc.script;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A bean representing the details of scripts executed and methodologies used to execute scripts in CCL source code.
 * 
 * @author Joshua Hyde
 * 
 */

public class ScriptExecutionDetails {
    private final Set<String> executedScripts;
    private final List<ScriptExecutionWarning> warnings;

    /**
     * Create script execution details.
     * 
     * @param executedScripts
     *            A {@link Set} of {@link String} objects representing the names of the scripts executed by the CCL
     *            source code.
     * @param warnings
     *            A {@link List} of {@link ScriptExecutionWarning} objects representing warnings, if any, about the
     *            methodologies used to execute scripts.
     */
    public ScriptExecutionDetails(final Set<String> executedScripts, final List<ScriptExecutionWarning> warnings) {
        if (executedScripts == null) {
            throw new IllegalArgumentException("Executed scripts cannot be null.");
        }

        if (warnings == null) {
            throw new IllegalArgumentException("Warnings cannot be null.");
        }

        this.executedScripts = Collections.unmodifiableSet(executedScripts);
        this.warnings = Collections.unmodifiableList(warnings);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ScriptExecutionDetails)) {
            return false;
        }

        final ScriptExecutionDetails other = (ScriptExecutionDetails) obj;
        return getExecutedScripts().equals(other.getExecutedScripts()) && getWarnings().equals(other.getWarnings());
    }

    /**
     * Get the scripts executed.
     * 
     * @return An immutable {@link Set} of {@link String} objects representing the names of the scripts executed by the
     *         CCL source code.
     */
    public Set<String> getExecutedScripts() {
        return executedScripts;
    }

    /**
     * Get warnings about the methodologies used to execute scripts.
     * 
     * @return An immutable {@link List} of {@link ScriptExecutionWarning} objects representing warnings, if any, about
     *         the methodologies used to execute scripts.
     */
    public List<ScriptExecutionWarning> getWarnings() {
        return warnings;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + executedScripts.hashCode();
        result = prime * result + warnings.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
