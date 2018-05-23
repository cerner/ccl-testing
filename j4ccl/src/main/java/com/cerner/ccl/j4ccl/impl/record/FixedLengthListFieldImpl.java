package com.cerner.ccl.j4ccl.impl.record;

import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.Field;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * A {@link Field} object that represents a fixed-length list in a CCL record structure.
 *
 * @author Joshua Hyde
 *
 */

public class FixedLengthListFieldImpl extends AbstractField {
    private final String fieldName;
    private final Structure structure;
    private final int listSize;

    /**
     * Create a field to represent a fixed-length list.
     *
     * @param fieldName
     *            The name of the field.
     * @param structure
     *            A {@link Structure} object that represents the structure that backs this field.
     * @param listSize
     *            The size of the list.
     * @throws IllegalArgumentException
     *             If the given field name is blank.
     * @throws ArrayIndexOutOfBoundsException
     *             If the given list size is less than 0.
     * @throws NullPointerException
     *             If the given field name or structure is {@code null}.
     */
    public FixedLengthListFieldImpl(final String fieldName, final Structure structure, final int listSize) {
        if (fieldName == null)
            throw new NullPointerException("Field name cannot be null.");

        if (structure == null)
            throw new NullPointerException("Structure cannot be null.");

        if (listSize < 0)
            throw new ArrayIndexOutOfBoundsException("List size cannot be less than 0.");

        if (fieldName.trim().length() == 0)
            throw new IllegalArgumentException("Field name cannot be blank.");

        this.fieldName = fieldName;
        this.structure = structure;
        this.listSize = listSize;
    }

    public long getDataLength() {
        throw new UnsupportedOperationException("Fixed-length lists are not primitives.");
    }

    public String getDeclaration() {
        return getName() + " [" + getListSize() + "]";
    }

    public int getListSize() {
        return listSize;
    }

    public String getName() {
        return fieldName;
    }

    public Structure getStructure() {
        return structure;
    }

    public DataType getType() {
        return DataType.LIST;
    }

}
