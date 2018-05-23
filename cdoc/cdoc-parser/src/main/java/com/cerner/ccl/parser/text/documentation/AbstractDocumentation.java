package com.cerner.ccl.parser.text.documentation;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.Described;

/**
 * An all-encompassing bean that can represent any block of documentation.
 *
 * @author Joshua Hyde
 *
 */

public abstract class AbstractDocumentation implements Described {
    private final String description;

    /**
     * Create a documentation object.
     *
     * @param description
     *            The top-level documentation. If {@code null}, a blank string is stored internally.
     */
    public AbstractDocumentation(final String description) {
        this.description = description == null ? "" : description;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof AbstractDocumentation)) {
            return false;
        }
        final AbstractDocumentation other = (AbstractDocumentation) obj;
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
