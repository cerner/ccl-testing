package com.cerner.ccl.parser.data;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A bean representing the top-level documentation for a CCL script.
 *
 * @author Joshua Hyde
 *
 */

public class ScriptDocumentation implements Described {
    private final String description;
    private final Integer boundTransaction;
    private final List<ScriptArgument> scriptArguments;

    /**
     * Create a script documentation object containing no actual script documentation.
     */
    public ScriptDocumentation() {
        this(null, null, null);
    }

    /**
     * Create a script documentation object.
     *
     * @param description
     *            The overall description of the script. If {@code null}, a blank string will be stored internally.
     * @param boundTransaction
     *            An {@link Integer} representing the TDB transaction to which the script is bound. If {@code null},
     *            then it is assumed that this script is bound to no transaction.
     * @param scriptArguments
     *            A {@link List} of {@link ScriptArgument} objects representing the command-line arguments, if any,
     *            attributed to the script. If {@code null}, then an empty list will be stored internally.
     */
    public ScriptDocumentation(final String description, final Integer boundTransaction,
            final List<ScriptArgument> scriptArguments) {
        this.description = description == null ? "" : description;
        this.boundTransaction = boundTransaction;
        this.scriptArguments = scriptArguments == null || scriptArguments.isEmpty()
                ? Collections.<ScriptArgument> emptyList() : Collections.unmodifiableList(scriptArguments);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ScriptDocumentation)) {
            return false;
        }

        final ScriptDocumentation other = (ScriptDocumentation) obj;
        return new EqualsBuilder().append(description, other.description)
                .append(boundTransaction, other.boundTransaction).append(scriptArguments, other.scriptArguments)
                .isEquals();
    }

    /**
     * Get the transaction to which this script is bound.
     *
     * @return {@code null} if this script is not bound to any transaction; otherwise, an {@link Integer} representing
     *         the ID of the TDB transaction to which this script is bound.
     */
    public Integer getBoundTransaction() {
        return boundTransaction;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Get the script arguments, if any, attributed to this script.
     *
     * @return A {@link List} of {@link ScriptArgument} objects representing the command-line arguments, if any,
     *         attributed to this script.
     */
    public List<ScriptArgument> getScriptArguments() {
        return scriptArguments;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((boundTransaction == null) ? 0 : boundTransaction.hashCode());
        result = prime * result + description.hashCode();
        result = prime * result + scriptArguments.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
