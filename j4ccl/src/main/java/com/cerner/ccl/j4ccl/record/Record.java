package com.cerner.ccl.j4ccl.record;

import java.util.Date;

/**
 * Represents a CCL record. <br>
 * A CCL record is defined as an instance of a record structure - a name, the values within it - backed by a
 * {@link Structure}.
 *
 * @author Mark Cummings
 */
public interface Record {

    /**
     * Returns the name of this record.
     *
     * @return the name of the record. Guaranteed to be {@code non-null}.
     */
    String getName();

    /**
     * Returns the structure definition of this record.
     *
     * @return the structure definition of this record. Guaranteed to be {@code
     *         non-null}.
     */
    Structure getStructure();

    /**
     * Returns the CCL declaration for this record and any nested structures. Note that the declaration will not include
     * the parent record if the current record is not the root.
     *
     * @return the CCL declaration for this record. Guaranteed to be {@code
     *         non-null}.
     */
    String getDeclaration();

    /**
     * Returns whether a field with the given name exists within this record. <br>
     * Equivalent to calling getStructure().hasMember(fieldName)
     *
     * @param fieldName
     *            the field name
     * @return whether a field with the given name exists
     */
    boolean hasMember(String fieldName);

    /**
     * Returns the {@link DataType type} of the field with the given name. <br>
     * Equivalent to calling getStructure().getType(fieldName)
     *
     * @param fieldName
     *            the field name
     * @return the {@link DataType type} of the field. Guaranteed to be {@code
     *         non-null}.
     * @throws IllegalArgumentException
     *             if no field with the given name exists within this record
     */
    DataType getType(String fieldName);

    /**
     * Returns whether this the root record.
     *
     * @return whether this is the root record.
     */
    boolean isRoot();

    /**
     * Returns the value of the fixed-length character field with the given name.
     *
     * @param fieldName
     *            The name of the character field to be retrieved.
     * @return The value of the character field.
     * @throws IllegalArgumentException
     *             If no field with the given name exists within this record.
     */
    String getChar(String fieldName);

    /**
     * Returns the value of the DQ8 field with the given name.
     *
     * @param fieldName
     *            the field name
     * @return the value of the DQ8 field
     * @throws IllegalArgumentException
     *             if no field with the given name exists within this record
     */
    Date getDQ8(String fieldName);

    /**
     * Returns the <em>dynamic</em> list field with the given name. This equates to a list that is declared in CCL with
     * an unspecified size as follows: <br>
     *
     * <pre>
     * record my_record
     * (
     *   1 list [*]
     *     2 field = i4
     *       ...
     * )
     * </pre>
     *
     * @param fieldName
     *            the field name
     * @return the dynamic list field. Guaranteed to be {@code non-null}.
     * @throws IllegalArgumentException
     *             if no field with the given name exists within this record
     */
    DynamicRecordList getDynamicList(String fieldName);

    /**
     * Returns the value of the F8 field with the given name.
     *
     * @param fieldName
     *            the field name
     * @return the value of the F8 field
     * @throws IllegalArgumentException
     *             if no field with the given name exists within this record
     */
    double getF8(String fieldName);

    /**
     * Returns the value of the I2 field with the given name.
     *
     * @param fieldName
     *            the field name
     * @return the value of the I2 field
     * @throws IllegalArgumentException
     *             if no field with the given name exists within this record
     */
    short getI2(String fieldName);

    /**
     * Returns the value of the I2 field as a boolean.
     *
     * @param fieldName
     *            The name of the field.
     * @return {@code true} if the I2 is 1; {@code false} if the I2 value is 0.
     * @throws IllegalArgumentException
     *             If no field with the given name exists within this record.
     * @throws IllegalStateException
     *             If the value of the I2 is not 0 or 1.
     */
    boolean getI2Boolean(String fieldName);

    /**
     * Returns the value of the I4 field with the given name.
     *
     * @param fieldName
     *            the field name
     * @return the value of the I4 field
     * @throws IllegalArgumentException
     *             if no field with the given name exists within this record
     */
    int getI4(String fieldName);

    /**
     * Returns the <em>fixed-size</em> list field with the given name. This equates to a list that is declared in CCL
     * with a specific size as follows: <br>
     *
     * <pre>
     * record my_record
     * (
     *   1 list [5]
     *     2 field = i4
     *       ...
     * )
     * </pre>
     *
     * @param fieldName
     *            the field name
     * @return the list field
     * @throws IllegalArgumentException
     *             if no field with the given name exists within this fieldName
     */
    RecordList getList(String fieldName);

    /**
     * Get the level in which this record is nested within a record structure.
     *
     * @return The level of nesting within the record structure at which this record exists. If 0, this is the record
     *         structure itself.
     */
    int getNestedLevel();

    /**
     * Returns the record field with the given name. This equates in CCL to a nested record of the form: <br>
     *
     * <pre>
     * record my_record
     * (
     *   1 nested_record
     *     2 field = i4
     *     ...
     * )
     * </pre>
     *
     * @param fieldName
     *            the field name
     * @return the record field
     * @throws IllegalArgumentException
     *             if no field with the given name exists within this record
     */
    Record getRecord(String fieldName);

    /**
     * Returns the value of the VC field with the given name.
     *
     * @param fieldName
     *            the field name
     * @return the value of the VC field
     * @throws IllegalArgumentException
     *             if no field with the given name exists within this record
     */
    String getVC(String fieldName);

    /**
     * Set the value of the fixed-length character field with the given name.
     *
     * @param fieldName
     *            The name of the field.
     * @param value
     *            The value to which the field is to be set.
     * @throws ArrayIndexOutOfBoundsException
     *             If the given value exceeds the maximum length of the character field.
     * @throws IllegalArgumentException
     *             If no field with the given name exists within this record or if the field is not of the correct type.
     */
    void setChar(String fieldName, String value);

    /**
     * Sets the value of the DQ8 field with the given name.
     *
     * @param fieldName
     *            the field name
     * @param value
     *            the value to set
     * @throws IllegalArgumentException
     *             if no field with the given name exists within this record, or if the field is not of the correct
     *             type.
     */
    void setDQ8(String fieldName, Date value);

    /**
     * Sets the value of the F8 field with the given name.
     *
     * @param fieldName
     *            the field name
     * @param value
     *            the value to set
     * @throws IllegalArgumentException
     *             if no field with the given name exists within this record, or if the field is not of the correct
     *             type.
     */
    void setF8(String fieldName, double value);

    /**
     * Sets the value of the I2 field with the given name.
     *
     * @param fieldName
     *            the field name
     * @param value
     *            the value to set
     * @throws IllegalArgumentException
     *             if no field with the given name exists within this record, or if the field is not of the correct
     *             type.
     */
    void setI2(String fieldName, short value);

    /**
     * Set the value of an I2 field with the given name, effectively treating it as a boolean field.
     *
     * @param fieldName
     *            The name of the I2 field.
     * @param value
     *            The value to be set; {@code true} is equivalent to 1, {@code
     *            false} is equivalent to 0.
     */
    void setI2(String fieldName, boolean value);

    /**
     * Sets the value of the I4 field with the given name.
     *
     * @param fieldName
     *            the field name
     * @param value
     *            the value to set
     * @throws IllegalArgumentException
     *             if no field with the given name exists within this record, or if the field is not of the correct
     *             type.
     */
    void setI4(String fieldName, int value);

    /**
     * Sets the value of the VC field with the given name.
     *
     * @param fieldName
     *            the field name
     * @param value
     *            the value to set
     * @throws IllegalArgumentException
     *             if no field with the given name exists within this record, or if the field is not of the correct
     *             type.
     */
    void setVC(String fieldName, String value);
}
