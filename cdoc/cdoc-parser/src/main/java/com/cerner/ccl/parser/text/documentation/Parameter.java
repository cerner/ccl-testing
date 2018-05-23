package com.cerner.ccl.parser.text.documentation;

import java.util.Locale;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.Described;
import com.cerner.ccl.parser.data.Named;

/**
 * A bean representing the {@code @param} documentation tag.
 * <p>
 * A parameter's uniqueness is driven by its name, case-insensitively, assumed to be limited to the context of a single
 * subroutine; this, the equality of two parameters is driven by the same criteria.
 *
 * @author Joshua Hyde
 *
 */

public class Parameter implements Described, Named {
    private final String name;
    private final String description;

    /**
     * Create a parameter with no documentation.
     *
     * @param name
     *            The name of the parameter.
     * @throws IllegalArgumentException
     *             If the given name is {@code null}.
     */
    public Parameter(final String name) {
        this(name, null);
    }

    /**
     * Create a parameter.
     *
     * @param name
     *            The name of the parameter.
     * @param description
     *            The description of the parameter. If {@code null}, then a blank string is stored internally.
     * @throws IllegalArgumentException
     *             If the given name is {@code null}.
     */
    public Parameter(final String name, final String description) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        this.name = name;
        this.description = description == null ? "" : description;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Parameter)) {
            return false;
        }

        final Parameter other = (Parameter) obj;
        return new EqualsBuilder()
                .append(name.toLowerCase(Locale.getDefault()), other.name.toLowerCase(Locale.getDefault())).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
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
        return name.toLowerCase(Locale.getDefault()).hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
