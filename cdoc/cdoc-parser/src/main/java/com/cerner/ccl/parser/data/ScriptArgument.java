package com.cerner.ccl.parser.data;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Definition of a bean corresponding to an {@code @arg} documentation tag.
 * <p>
 * This probably should have been named ScriptArgumentDocumentation rather than ScriptArgument since the only defining
 * characteristic is the documentation for the argument.
 *
 * @author Joshua Hyde
 * @author Fred Eckertson
 *
 */

public class ScriptArgument implements Described {
    private final String description;

    /**
     * Create a script argument.
     *
     * @param description
     *            The description of the argument.
     * @throws IllegalArgumentException
     *             If the given description is {@code null}.
     */
    public ScriptArgument(final String description) {
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null.");
        }

        this.description = description;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ScriptArgument)) {
            return false;
        }

        final ScriptArgument other = (ScriptArgument) obj;
        return new EqualsBuilder().append(description, other.description).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {
        return description.hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
