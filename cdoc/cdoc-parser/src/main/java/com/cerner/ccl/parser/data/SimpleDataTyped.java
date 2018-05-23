package com.cerner.ccl.parser.data;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A simple definition of a {@link DataTyped} object.
 *
 * @author Joshua Hyde
 *
 */

public class SimpleDataTyped implements DataTyped {
    private final DataType dataType;

    /**
     * Create a simple data typed object.
     *
     * @param dataType
     *            A {@link DataType} object representing the data type of the object.
     * @throws IllegalArgumentException
     *             If the given data type is {@code null}.
     */
    public SimpleDataTyped(final DataType dataType) {
        if (dataType == null) {
            throw new IllegalArgumentException("Data type cannot be null.");
        }

        this.dataType = dataType;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof SimpleDataTyped)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        final SimpleDataTyped other = (SimpleDataTyped) obj;
        return new EqualsBuilder().append(dataType, other.dataType).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final DataType getDataType() {
        return dataType;
    }

    @Override
    public int hashCode() {
        return dataType.hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
