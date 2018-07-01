package com.cerner.ccl.cdoc.script;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A bean to represent that a condition worthy of a warning was encountered while parsing the scripts executed by a CCL
 * object.
 *
 * @author Joshua Hyde
 *
 */

public class ScriptExecutionWarning {
    private final int lineNumber;
    private final String sourceCode;

    /**
     * Create a warning.
     *
     * @param lineNumber
     *            The line number.
     * @param sourceCode
     *            The text of the source.
     * @throws IllegalArgumentException
     *             If the given line number is less than 1 or the given source code is {@code null}.
     */
    public ScriptExecutionWarning(final int lineNumber, final String sourceCode) {
        if (lineNumber < 1) {
            throw new IllegalArgumentException(
                    "Line number must be a non-zero, positive integer: " + Integer.toString(lineNumber));
        }

        if (sourceCode == null) {
            throw new IllegalArgumentException("Source code cannot be null.");
        }

        this.lineNumber = lineNumber;
        this.sourceCode = sourceCode;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ScriptExecutionWarning)) {
            return false;
        }

        final ScriptExecutionWarning other = (ScriptExecutionWarning) obj;
        return getLineNumber() == other.getLineNumber() && getSourceCode().equals(other.getSourceCode());
    }

    /**
     * Get the line number of the source code at which the warning-worthy condition was encountered.
     *
     * @return The line number of the source code at which the warning-worthy condition was encountered.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Get the source code that is worthy of being warned about.
     *
     * @return The source code that is worthy of being warned about.
     */
    public String getSourceCode() {
        return sourceCode;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + lineNumber;
        result = prime * result + sourceCode.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
