package com.cerner.ccl.j4ccl.impl.record;

import java.util.EnumSet;
import java.util.Set;

import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.Field;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * A {@link Field} object to represent a primitive data type in a CCL record structure.
 *
 * @author Joshua Hyde
 *
 */

public class PrimitiveFieldImpl extends AbstractField {
    /**
     * The valid primitive types that can be represented by this field.
     */
    private static final Set<DataType> VALID_TYPES;

    static {
        VALID_TYPES = EnumSet.of(DataType.DQ8, DataType.F8, DataType.I2, DataType.I4, DataType.VC);
    }

    private final String fieldName;
    private final DataType dataType;

    /**
     * Create a field to represent a primitive datatype.
     *
     * @param fieldName
     *            The name of the field.
     * @param dataType
     *            A {@link DataType} representing the primitive data type represented by this field.
     * @throws IllegalArgumentException
     *             If the given field name is blank or the given data type is a complex type.
     * @throws NullPointerException
     *             If the given field name or data type is {@code null}.
     * @see DataType#isComplexType()
     */
    public PrimitiveFieldImpl(final String fieldName, final DataType dataType) {
        if (fieldName == null)
            throw new NullPointerException("Field name cannot be null.");

        if (dataType == null)
            throw new NullPointerException("Data type cannot be null.");

        if (fieldName.trim().length() == 0)
            throw new IllegalArgumentException("Field name cannot be blank.");

        if (dataType.isComplexType())
            throw new IllegalArgumentException("Data type must be a primitive data type.");

        if (!VALID_TYPES.contains(dataType))
            throw new IllegalArgumentException("Invalid primitive type: " + dataType);

        this.fieldName = fieldName;
        this.dataType = dataType;
    }

    public long getDataLength() {
        switch (getType()) {
        case I4:
            return 4;
        case I2:
            return 2;
        case F8:
        case DQ8:
            return 8;
        default:
            throw new UnsupportedOperationException(
                    "Unrecognized or invalid fixed-length primitive type: " + getType());
        }
    }

    public String getDeclaration() {
        return getName() + " = " + getType().toString();
    }

    public int getListSize() {
        throw new UnsupportedOperationException("Field represents a primitive data type; it has no list size.");
    }

    public String getName() {
        return fieldName;
    }

    public Structure getStructure() {
        throw new UnsupportedOperationException(
                "Field represents a primtiive data type; it has no underlying structure.");
    }

    public DataType getType() {
        return dataType;
    }

}
