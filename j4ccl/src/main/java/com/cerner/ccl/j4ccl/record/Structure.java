package com.cerner.ccl.j4ccl.record;

import java.util.List;

/**
 * Represents a CCL structure definition.
 * <br>
 * A structure is defined as the skeleton that supports a record structure - the fields within it, the names of those
 * fields. It contains no information about the name of the record structure or the values stored within it; for that,
 * see {@link Record}.
 *
 * @author Mark Cummings
 */
public interface Structure {

    /**
     * Returns the list of field definitions for this structure.
     *
     * @return an unmodifiable list of fields within this structure, or {@link java.util.Collections#emptyList} if this
     *         structure does not contain any fields
     */
    List<Field> getFields();

    /**
     * Get a specific field within the structure.
     *
     * @param name
     *            The name of the desired field.
     * @return A {@link Field} object representing the requested field.
     * @throws NullPointerException
     *             If the given field name is {@code null}.
     * @throws IllegalArgumentException
     *             If no field is found by the given name.
     */
    Field getField(String name);

    /**
     * Returns whether a field with the given name exists within this structure.
     *
     * @param fieldName
     *            the field name
     * @return whether a field with the given name exists
     */
    boolean hasMember(String fieldName);

    /**
     * Returns the {@link DataType type} of the field with the given name.
     *
     * @param fieldName
     *            the field name
     * @return the {@link DataType type} of the field. Guaranteed to be {@code
     *         non-null}.
     * @throws IllegalArgumentException
     *             if no field with the given name exists within this structure
     */
    DataType getType(String fieldName);
}
