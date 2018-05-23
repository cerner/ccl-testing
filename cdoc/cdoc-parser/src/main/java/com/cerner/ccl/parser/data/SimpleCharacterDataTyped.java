package com.cerner.ccl.parser.data;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A data type to represent a fixed-length character datatype.
 *
 * @author Joshua Hyde
 *
 */

public class SimpleCharacterDataTyped extends SimpleDataTyped implements FixedLengthDataTyped {
    private final int dataLength;

    /**
     * Create a fixed-length character data type.
     *
     * @param dataLength
     *            The length of the datatype.
     * @throws IllegalArgumentException
     *             If the given data length is less than one.
     */
    public SimpleCharacterDataTyped(final int dataLength) {
        super(DataType.CHAR);

        if (dataLength < 1) {
            throw new IllegalArgumentException("Data length cannot be less than 1: " + Integer.toString(dataLength));
        }

        this.dataLength = dataLength;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SimpleCharacterDataTyped)) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }
        final SimpleCharacterDataTyped other = (SimpleCharacterDataTyped) obj;
        return new EqualsBuilder().append(dataLength, other.dataLength).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDataLength() {
        return dataLength;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + dataLength;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
