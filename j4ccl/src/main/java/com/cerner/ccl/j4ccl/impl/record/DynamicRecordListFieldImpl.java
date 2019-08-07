package com.cerner.ccl.j4ccl.impl.record;

import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.Field;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * A {@link Field} object that represents a variable-length list within a CCL record structure.
 *
 * @author Joshua Hyde
 *
 */

public class DynamicRecordListFieldImpl extends AbstractField {
    final private String name;
    final private Structure structure;

    /**
     * Create a field to represent a variable-length list.
     *
     * @param name
     *            The name of the list.
     * @param structure
     *            A {@link Structure} object representing the structure that backs this list.
     * @see DataType#isList()
     * @throws IllegalArgumentException
     *             If the given field name is blank.
     * @throws NullPointerException
     *             If the given name or structure are {@code null}.
     */
    public DynamicRecordListFieldImpl(final String name, final Structure structure) {
        if (name == null) {
            throw new NullPointerException("Name cannot be null.");
        }

        if (structure == null) {
            throw new NullPointerException("Structure cannot be null.");
        }

        if (name.trim().length() == 0) {
            throw new IllegalArgumentException("Field name cannot be blank.");
        }

        this.name = name;
        this.structure = structure;
    }

    @Override
    public long getDataLength() {
        throw new UnsupportedOperationException("Variable-length list has no definable fixed length.");
    }

    @Override
    public String getDeclaration() {
        return getName() + " [*]";
    }

    @Override
    public int getListSize() {
        throw new UnsupportedOperationException(
                "Field represents a variable-length list; use DynamicRecordList to obtain the size of this list");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Structure getStructure() {
        return structure;
    }

    @Override
    public DataType getType() {
        return DataType.DYNAMIC_LIST;
    }
}
