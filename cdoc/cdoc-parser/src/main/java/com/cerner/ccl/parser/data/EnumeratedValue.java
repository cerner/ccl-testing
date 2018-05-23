package com.cerner.ccl.parser.data;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A bean representing a documented {@code @value} tag.
 *
 * @author Joshua Hyde
 *
 */

public class EnumeratedValue implements Described {
    private final String value;
    private final String description;

    /**
     * Create an enumerated value without a description.
     *
     * @param value
     *            The value.
     * @throws IllegalArgumentException
     *             If the given value is {@code null}.
     */
    public EnumeratedValue(final String value) {
        this(value, null);
    }

    /**
     * Create an enumerated value.
     *
     * @param value
     *            The value.
     * @param description
     *            The description of the value.
     * @throws IllegalArgumentException
     *             If the given value is {@code null}.
     */
    public EnumeratedValue(final String value, final String description) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null.");
        }

        this.value = value;
        this.description = description == null ? "" : description;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof EnumeratedValue)) {
            return false;
        }
        final EnumeratedValue other = (EnumeratedValue) obj;
        return new EqualsBuilder().append(value, other.value).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Get the value.
     *
     * @return The value.
     */
    public String getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return getValue().hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
