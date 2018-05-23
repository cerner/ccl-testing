package com.cerner.ccl.parser.text.record;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.FixedLengthDataTyped;

/**
 * Definition of a structure member that is a fixed-length character field.
 *
 * @author Joshua Hyde
 *
 */

public class StructureCharacterField extends StructureField implements FixedLengthDataTyped {
    private final int dataLength;

    /**
     * Create a character field.
     *
     * @param name
     *            The name of the field.
     * @param level
     *            The {@link #getLevel() level} of this member.
     * @param dataLength
     *            The length of the character field.
     * @throws IllegalArgumentException
     *             If the given data length is less than zero.
     */
    public StructureCharacterField(final String name, final int level, final int dataLength) {
        super(name, level, DataType.CHAR);

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

        if (!(obj instanceof StructureCharacterField)) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        final StructureCharacterField other = (StructureCharacterField) obj;
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
