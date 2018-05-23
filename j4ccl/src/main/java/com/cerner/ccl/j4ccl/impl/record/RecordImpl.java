package com.cerner.ccl.j4ccl.impl.record;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.cerner.ccl.j4ccl.impl.record.factory.DynamicRecordListImplFactory;
import com.cerner.ccl.j4ccl.impl.record.factory.RecordImplFactory;
import com.cerner.ccl.j4ccl.impl.record.factory.RecordListImplFactory;
import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.DynamicRecordList;
import com.cerner.ccl.j4ccl.record.Field;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.RecordList;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * Implementation of {@link Record}.
 *
 * @author Mark Cummings
 * @author Joshua Hyde
 */

public final class RecordImpl implements Record {
    private static final RecordListImplFactory DEFAULT_FIXED_LIST_FACTORY = new RecordListImplFactory();
    private static final DynamicRecordListImplFactory DEFAULT_VAR_LIST_FACTORY = new DynamicRecordListImplFactory();
    private static final RecordImplFactory DEFAULT_RECORD_FACTORY = new RecordImplFactory();

    private static final Date DEFAULT_DQ8_VALUE;

    static {
        final GregorianCalendar cal = new GregorianCalendar(1900, Calendar.JANUARY, 1);
        DEFAULT_DQ8_VALUE = cal.getTime();
    }

    private final Map<String, Object> values = new HashMap<String, Object>();
    private final RecordListImplFactory fixedListFactory;
    private final DynamicRecordListImplFactory varListFactory;
    private final RecordImplFactory recordFactory;
    private final String name;
    private final Structure structure;
    private final int level;
    private final Record parent;

    /**
     * Package-private constructor.
     *
     * @param name
     *            The name of the record structure element. If {@code null}, this is assumed to be the root record
     *            structure.
     * @param structure
     *            The {@link Structure} object that represents the structure that backs this record structure instance.
     * @param parent
     *            A {@link RecordImpl} object that represents the parent record of this object. If {@code null}, this
     *            object assumes that it is the root record element.
     */
    public RecordImpl(final String name, final Structure structure, final Record parent) {
        this(name, structure, parent, DEFAULT_FIXED_LIST_FACTORY, DEFAULT_VAR_LIST_FACTORY, DEFAULT_RECORD_FACTORY);
    }

    /**
     * Create a record.
     *
     * @param name
     *            The name of the record.
     * @param structure
     *            A {@link Structure} object that represents the structure that backs this record structure.
     * @param parent
     *            A {@link Record} object representing the record that is the parent record structure element of this
     *            element. If {@code
     *            null}, this object assumes it is the root record, immediately below the record structure element.
     * @param fixedListFactory
     *            A {@link RecordListImplFactory} that can be used to create {@link RecordList} objects.
     * @param varListFactory
     *            A {@link DynamicRecordListImplFactory} object used to create {@link DynamicRecordList} objects.
     * @param recordFactory
     *            A {@link RecordImplFactory} object used to create nested record structures.
     *
     */
    RecordImpl(final String name, final Structure structure, final Record parent,
            final RecordListImplFactory fixedListFactory, final DynamicRecordListImplFactory varListFactory,
            final RecordImplFactory recordFactory) {
        if (structure == null)
            throw new NullPointerException("Structure must not be null.");

        this.name = name;
        this.structure = structure;
        this.parent = parent;
        this.level = parent == null ? 0 : parent.getNestedLevel() + 1;
        this.fixedListFactory = fixedListFactory;
        this.varListFactory = varListFactory;
        this.recordFactory = recordFactory;

        // Initialize field values
        for (final Field field : structure.getFields())
            values.put(field.getName().toUpperCase(Locale.getDefault()), getDefaultValue(field));
    }

    public String getDeclaration() {
        final StringBuilder builder = new StringBuilder();

        builder.append("record ").append(getName()).append("\n");
        builder.append("(\n");
        ((StructureImpl) structure).addDeclaration(builder, 1);
        builder.append(")");
        return builder.toString();
    }

    public DynamicRecordList getDynamicList(final String fieldName) {
        return (DynamicRecordList) getValue(fieldName, DataType.DYNAMIC_LIST);
    }

    public short getI2(final String fieldName) {
        return (short) getI4(fieldName);
    }

    public boolean getI2Boolean(final String fieldName) {
        final short value = getI2(fieldName);
        switch (value) {
        case 1:
            return true;
        case 0:
            return false;
        default:
            throw new IllegalStateException(
                    String.format("Cannot translate short to boolean: %s is not in [0,1].", Short.toString(value)));
        }
    }

    public int getI4(final String fieldName) {
        return ((Number) getValue(fieldName, DataType.I2, DataType.I4)).intValue();
    }

    public String getChar(final String fieldName) {
        return (String) getValue(fieldName, DataType.CHARACTER);
    }

    public Date getDQ8(final String fieldName) {
        return (Date) getValue(fieldName, DataType.DQ8);
    }

    public double getF8(final String fieldName) {
        return ((Double) getValue(fieldName, DataType.F8)).doubleValue();
    }

    public RecordList getList(final String fieldName) {
        return (RecordList) getValue(fieldName, DataType.LIST);
    }

    public String getName() {
        return name == null ? "(anonymous)" : name;
    }

    public int getNestedLevel() {
        return level;
    }

    public Record getRecord(final String fieldName) {
        return (Record) getValue(fieldName, DataType.RECORD);
    }

    public Structure getStructure() {
        return structure;
    }

    public DataType getType(final String fieldName) {
        if (!hasMember(fieldName))
            throw new IllegalArgumentException("Field not found: " + fieldName);

        return structure.getType(fieldName);
    }

    public String getVC(final String fieldName) {
        return (String) getValue(fieldName, DataType.VC);
    }

    public boolean hasMember(final String fieldName) {
        return structure.hasMember(fieldName);
    }

    public boolean isRoot() {
        return parent == null;
    }

    public void setChar(final String fieldName, final String value) {
        final long dataLength = getStructure().getField(fieldName).getDataLength();
        if (value.length() > dataLength)
            throw new ArrayIndexOutOfBoundsException(
                    String.format("String data length exceeds maximum data length: %s > %s",
                            Integer.toString(value.length()), Long.toString(dataLength)));
        setValue(fieldName, value, DataType.CHARACTER);
    }

    public void setDQ8(final String fieldName, final Date value) {
        setValue(fieldName, value, DataType.DQ8);
    }

    public void setI2(final String fieldName, final short value) {
        setI4(fieldName, value);
    }

    public void setI2(final String fieldName, final boolean value) {
        if (value)
            setI2(fieldName, (short) 1);
        else
            setI2(fieldName, (short) 0);
    }

    public void setI4(final String fieldName, final int value) {
        if (getType(fieldName).equals(DataType.I2))
            validateShortValue(value);

        final Integer valueToSet = Integer.valueOf(value);
        setValue(fieldName, valueToSet, DataType.I2, DataType.I4);
    }

    public void setF8(final String fieldName, final double value) {
        setValue(fieldName, Double.valueOf(value), DataType.F8);
    }

    public void setVC(final String fieldName, final String value) {
        setValue(fieldName, value, DataType.VC);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        if (level == 0)
            builder.append(getName()).append("\n");

        for (final Entry<String, Object> entry : values.entrySet()) {
            String fieldName = entry.getKey();
            final Field field = getStructure().getField(fieldName);
            final Object value = entry.getValue();

            if (field.getType().isList()) {
                final RecordList recordList = (RecordList) value;
                int listIndex = 1;
                for (final Record record : recordList) {
                    builder.append(StringUtils.repeat(" ", level + 1));
                    builder.append(level + 1).append(" ").append(fieldName);

                    builder.append(" [");
                    builder.append(listIndex++);
                    builder.append(",").append(recordList.getSize());
                    builder.append("]\n");
                    builder.append(record);
                }
            } else {
                builder.append(StringUtils.repeat(" ", level + 1));
                builder.append(level + 1).append(" ").append(fieldName);

                if (field.getType() == DataType.RECORD) {
                    builder.append("\n");
                    builder.append(value);
                } else {
                    builder.append(" = ").append(field.getType());
                    builder.append(" {").append(values.get(fieldName)).append("}");
                    builder.append("\n");
                }
            }
        }

        return builder.toString();
    }

    /**
     * Get the default value for a datatype.
     *
     * @param field
     *            A {@link Field} representing the field for which a default value is to be obtained.
     * @return An object that is the default value for the given field type.
     * @throws IllegalArgumentException
     *             If the given field type is not known to this method.
     */
    private Object getDefaultValue(final Field field) {
        switch (field.getType()) {
        case I2:
            return Short.valueOf((short) 0);
        case I4:
            return Integer.valueOf(0);
        case F8:
            return Double.valueOf(0d);
        case DQ8:
            return DEFAULT_DQ8_VALUE;
        case CHARACTER:
        case VC:
            return "";
        case RECORD:
            return recordFactory.createNestedRecord(field.getStructure(), this);
        case LIST:
            return fixedListFactory.create(this, field.getStructure(), field.getListSize());
        case DYNAMIC_LIST:
            return varListFactory.create(field.getStructure(), this);
        default:
            throw new IllegalArgumentException("Unknown data type: " + field.getType());
        }
    }

    /**
     * Get a field's value.
     *
     * @param fieldName
     *            The name of the field whose value is to be fetched.
     * @param getAsTypes
     *            An array of potential {@link DataType} enums representing the potential types of the desired field.
     * @return An {@code Object} representing the obtained value.
     */
    private Object getValue(String fieldName, final DataType... getAsTypes) {
        fieldName = fieldName.toUpperCase(Locale.getDefault());
        validateField(fieldName, getAsTypes);
        return values.get(fieldName);
    }

    /**
     * Set the value for a field.
     *
     * @param fieldName
     *            The name of the field whose value is to be set.
     * @param value
     *            The value to be set.
     * @param setAsTypes
     *            An array of possible {@link DataType} enums the represent the possible data types that the field could
     *            be.
     */
    private void setValue(String fieldName, final Object value, final DataType... setAsTypes) {
        fieldName = fieldName.toUpperCase(Locale.getDefault());
        validateField(fieldName, setAsTypes);
        values.put(fieldName, value);
    }

    /**
     * Validate that the given field exists within this record and that, of the given data types, the field's data type
     * is one of them.
     *
     * @param fieldName
     *            The name of the field.
     * @param expectedTypes
     *            An array of {@link DataType} objects that are candidates for the potential matches.
     * @throws IllegalArgumentException
     *             If the field matching the given name is among the given array of data types.
     */
    private void validateField(final String fieldName, final DataType... expectedTypes) {
        final Structure structure = getStructure();

        if (!structure.hasMember(fieldName))
            throw new IllegalArgumentException("Field " + fieldName + " not found in " + getName());

        final Field field = structure.getField(fieldName);
        if (!Arrays.asList(expectedTypes).contains(field.getType()))
            throw new IllegalArgumentException(
                    "Field " + fieldName + " does not match the expected type: " + Arrays.toString(expectedTypes));
    }

    /**
     * Verify that the given integer value is within the bounds of a short value.
     *
     * @param value
     *            The value to be used in a short boundary comparison.
     * @throws IllegalArgumentException
     *             If the given int value is less than {@value Short#MIN_VALUE} or greater than {@link Short#MAX_VALUE}.
     */
    private void validateShortValue(final int value) {
        if (value < Short.MIN_VALUE || value > Short.MAX_VALUE)
            throw new IllegalArgumentException(String.format("Short value exceeds bounds; %d < %d || %d > %d", value,
                    Short.MIN_VALUE, value, Short.MAX_VALUE));
    }
}
