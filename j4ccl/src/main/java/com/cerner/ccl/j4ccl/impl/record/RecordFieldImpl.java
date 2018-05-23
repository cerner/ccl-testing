package com.cerner.ccl.j4ccl.impl.record;

import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.Field;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * A {@link Field} object to represent a record structure in CCL.
 *
 * @author Joshua Hyde
 *
 */

public class RecordFieldImpl extends AbstractField {
    private final String fieldName;
    private final Structure structure;

    /**
     * Create a field object to represent a record structure.
     *
     * @param fieldName
     *            The name of the record structure.
     * @param structure
     *            A {@link Structure} object representing the structure that backs this record structure.
     * @throws IllegalArgumentException
     *             If the given field name is blank.
     * @throws NullPointerException
     *             If the given field name or structure is {@code null}.
     */
    public RecordFieldImpl(final String fieldName, final Structure structure) {
        if (fieldName == null)
            throw new NullPointerException("Field name cannot be null.");

        if (structure == null)
            throw new NullPointerException("Structure cannot be null.");

        if (fieldName.trim().length() == 0)
            throw new IllegalArgumentException("Field name cannot be blank.");

        this.fieldName = fieldName;
        this.structure = structure;
    }

    public long getDataLength() {
        throw new UnsupportedOperationException("Records are not fixed-length primitives.");
    }

    public String getDeclaration() {
        return getName();
    }

    public int getListSize() {
        throw new UnsupportedOperationException("This field represents a record structure; it has no size.");
    }

    public String getName() {
        return fieldName;
    }

    public Structure getStructure() {
        return structure;
    }

    public DataType getType() {
        return DataType.RECORD;
    }

}
