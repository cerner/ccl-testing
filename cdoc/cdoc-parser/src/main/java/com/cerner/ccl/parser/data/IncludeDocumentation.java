package com.cerner.ccl.parser.data;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * An object representing the top-level documentation of an include file.
 *
 * @author Joshua Hyde
 *
 */

public class IncludeDocumentation implements Described {
    private final String description;

    /**
     * Create an empty top-level documentation for the include file.
     */
    public IncludeDocumentation() {
        this(null);
    }

    /**
     * Create a top-level documentation object for an include file.
     *
     * @param description
     *            The top-level description of the include file. If {@code null}, a blank string will be stored
     *            internally.
     */
    public IncludeDocumentation(final String description) {
        this.description = description == null ? "" : description;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof IncludeDocumentation)) {
            return false;
        }
        final IncludeDocumentation other = (IncludeDocumentation) obj;
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
