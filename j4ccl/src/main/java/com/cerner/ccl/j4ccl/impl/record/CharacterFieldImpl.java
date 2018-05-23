package com.cerner.ccl.j4ccl.impl.record;

import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.Field;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * A {@link Field} object to represent a fixed-length character field.
 *
 * @author Joshua Hyde
 *
 */

public class CharacterFieldImpl implements Field {
    private final long dataLength;
    private final String fieldName;

    /**
     * Create a field to represent a fixed-length character field.
     *
     * @param fieldName
     *            The name of the field.
     * @param dataLength
     *            The number of characters (or length of the variable, in bytes) to be stored in this field.
     */
    public CharacterFieldImpl(final String fieldName, final long dataLength) {
        this.dataLength = dataLength;
        this.fieldName = fieldName;
    }

    public long getDataLength() {
        return dataLength;
    }

    public String getDeclaration() {
        return String.format("%s = C%s", getName(), Long.toString(getDataLength()));
    }

    public int getListSize() {
        throw new UnsupportedOperationException("Character fields are not fixed-length lists.");
    }

    public String getName() {
        return fieldName;
    }

    public Structure getStructure() {
        throw new UnsupportedOperationException("Character fields are not complex types.");
    }

    public DataType getType() {
        return DataType.CHARACTER;
    }

    @Override
    public String toString() {
        return String.format("%s [C%d]", getName(), getDataLength());
    }

}
