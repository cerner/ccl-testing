package com.cerner.ccl.j4ccl.impl.record.factory;

import com.cerner.ccl.j4ccl.impl.record.CharacterFieldImpl;
import com.cerner.ccl.j4ccl.impl.record.DynamicRecordListFieldImpl;
import com.cerner.ccl.j4ccl.impl.record.FixedLengthListFieldImpl;
import com.cerner.ccl.j4ccl.impl.record.PrimitiveFieldImpl;
import com.cerner.ccl.j4ccl.impl.record.RecordFieldImpl;
import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.Field;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * A factory object to construct {@link Field} objects.
 *
 * @author Mark Cummings
 * @author Joshua Hyde
 *
 */

public class FieldImplFactory {
    /**
     * Create a simple field that represents a basic fixed-length character field.
     *
     * @param name
     *            The name of the field.
     * @param characterLength
     *            The length of the character field.
     * @return A {@link Field} object representing a fixed-length character field.
     */
    public Field createCharacterField(final String name, final int characterLength) {
        return new CharacterFieldImpl(name, characterLength);
    }

    /**
     * Create a simple field that represents a basic primitive datatype (F8, VC, etc.).
     *
     * @param name
     *            The name of the field.
     * @param type
     *            A {@link DataType} enum representing the datatype of the field.
     * @return A {@link Field} object.
     * @throws IllegalArgumentException
     *             If the given data type is {@link DataType#CHARACTER}. Use {@link #createCharacterField(String, int)}
     *             to create such fields.
     */
    public Field createSimpleField(final String name, final DataType type) {
        if (DataType.CHARACTER.equals(type))
            throw new IllegalArgumentException("Use createCharacterField(String, int) to create character fields.");

        return new PrimitiveFieldImpl(name, type);
    }

    /**
     * Create a field that represents a record structure object.
     *
     * @param name
     *            The name of the record structure.
     * @param structure
     *            A {@link Structure} object representing the structure that backs the record structure.
     * @return A {@link Field} object.
     */
    public Field createRecordField(final String name, final Structure structure) {
        return new RecordFieldImpl(name, structure);
    }

    /**
     * Create a fixed-length list.
     *
     * @param name
     *            The name of the list.
     * @param structure
     *            A {@link Structure} object that represents the structure backing the fixed-length list.
     * @param size
     *            The size of the list.
     * @return A {@link Field} object represeting the list.
     */
    public Field createListField(final String name, final Structure structure, final int size) {
        return new FixedLengthListFieldImpl(name, structure, size);
    }

    /**
     * Create a variable-length list.
     *
     * @param name
     *            The name of the list.
     * @param structure
     *            A {@link Structure} object representing the structure that backs the list.
     * @return A {@link Field} object representing the list.
     */
    public Field createDynamicListField(final String name, final Structure structure) {
        return new DynamicRecordListFieldImpl(name, structure);
    }
}
