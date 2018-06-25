package com.cerner.ccl.j4ccl.record;

/**
 * Represents a field definition within a {@link Structure}.
 *
 * @author Mark Cummings
 * @author Joshua Hyde
 */

public interface Field {
    /**
     * Get the length of the data stored within the field. This applies to only fixed-length primitive fields. <br>
     * Use {@link #getType()} to get the data type of this field and then {@link DataType#isFixedLengthPrimitive()} to
     * determine whether or not this field represents a fixed-length primitive value.
     *
     * @return The length, in bytes, of the primitive field.
     * @throws UnsupportedOperationException
     *             If this field represents a complex type or a variable-length primitive, such as a {@link DataType#VC}
     *             field.
     */
    long getDataLength();

    /**
     * Get a textual declaration of this field's underlying structure that can be used to create the structure in CCL.
     *
     * @return The CCL declaration of this structure.
     */
    String getDeclaration();

    /**
     * If applicable, get the size of the fixed-length list represented by this field. <br>
     * Use {@link #getType()} to obtain this field's datatype and verify that it is a {@link DataType#LIST} type to
     * avoid thrown exceptions.
     *
     * @return The size of the fixed-length list.
     * @throws UnsupportedOperationException
     *             If this field represents an object that is not a list.
     */
    int getListSize();

    /**
     * Get the name of the field.
     *
     * @return The name of the field.
     */
    String getName();

    /**
     * If applicable, get the underlying structure that backs this field. <br>
     * To determine whether or not this object has an underlying structure, use {@link #getType()} to get the type of
     * the field and {@link DataType#isComplexType()} to determine whether or not this represents a primitive datatype.
     *
     * @return A {@link Structure} object that backs this field.
     * @throws UnsupportedOperationException
     *             If this object represents a field that is not a complex data type.
     */
    Structure getStructure();

    /**
     * Get the type of the field.
     *
     * @return A {@link DataType} field representing the type of field represented by this object.
     */
    DataType getType();
}
