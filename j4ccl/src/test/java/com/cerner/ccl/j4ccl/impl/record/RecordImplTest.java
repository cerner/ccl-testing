package com.cerner.ccl.j4ccl.impl.record;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TreeMap;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

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
 * Unit test for {@link RecordImpl}.
 *
 * @author Joshua Hyde
 *
 */

public class RecordImplTest {
    private static final String ROOT_NAME = "root";
    private static final Field I2_FIELD = mock(Field.class);
    private static final Field I4_FIELD = mock(Field.class);
    private static final Field DQ8_FIELD = mock(Field.class);
    private static final Field F8_FIELD = mock(Field.class);
    private static final Field VC_FIELD = mock(Field.class);
    private static final Field CHAR_FIELD = mock(Field.class);

    private static final RecordImplFactory RECORD_FACTORY = mock(RecordImplFactory.class);
    private static final RecordListImplFactory FIXED_LIST_FACTORY = mock(RecordListImplFactory.class);
    private static final DynamicRecordListImplFactory VAR_LIST_FACTORY = mock(DynamicRecordListImplFactory.class);

    /**
     * Set up basic primitive types.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        when(I2_FIELD.getType()).thenReturn(DataType.I2);
        when(I2_FIELD.getName()).thenReturn("I2_FIELD");

        when(I4_FIELD.getType()).thenReturn(DataType.I4);
        when(I4_FIELD.getName()).thenReturn("I4_FIELD");

        when(DQ8_FIELD.getType()).thenReturn(DataType.DQ8);
        when(DQ8_FIELD.getName()).thenReturn("DQ8_FIELD");

        when(F8_FIELD.getType()).thenReturn(DataType.F8);
        when(F8_FIELD.getName()).thenReturn("F8_FIELD");

        when(VC_FIELD.getType()).thenReturn(DataType.VC);
        when(VC_FIELD.getName()).thenReturn("VC_FIELD");

        when(CHAR_FIELD.getType()).thenReturn(DataType.CHARACTER);
        when(CHAR_FIELD.getName()).thenReturn("CHAR_FIELD");
        when(CHAR_FIELD.getDataLength()).thenReturn(Long.valueOf(123));
    }

    /**
     * Test that constructing a record with a null structure fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void testConstructionNullStructure() {
        new RecordImpl(ROOT_NAME, null, null);
    }

    /**
     * Test that a character field is initialized to the appropriate length.
     */
    @Test
    public void testGetChar() {
        final Structure structure = mockStructure(CHAR_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        assertThat(record.getChar(CHAR_FIELD.getName())).isEqualTo("");
    }

    /**
     * Test the building of a declaration
     */
    @Test
    public void testGetDeclaration() {
        // Create a structure that returns its "declaration"
        final Answer<Object> structureAnswer = new Answer<Object>() {
            public String answer(final InvocationOnMock invocation) throws Throwable {
                if (!"addDeclaration".equals(invocation.getMethod().getName()))
                    return null;

                final StringBuilder builder = (StringBuilder) invocation.getArguments()[0];
                builder.append("declaration");
                return null;
            }
        };
        final StructureImpl structure = mock(StructureImpl.class, structureAnswer);
        when(structure.getFields()).thenReturn(Arrays.asList(F8_FIELD));

        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        assertThat(record.getDeclaration()).isEqualTo("record " + ROOT_NAME + "\n(\ndeclaration)");
    }

    /**
     * Test that the correct default value is set for DQ8 values.
     */
    @Test
    public void testGetDq8DefaultValue() {
        final Date expected = new GregorianCalendar(1900, Calendar.JANUARY, 1).getTime();

        final Structure structure = mockStructure(DQ8_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        assertThat(record.getDQ8(DQ8_FIELD.getName())).isEqualTo(expected);
    }

    /**
     * Test the fetching of a dynamic list.
     */
    @Test
    public void testGetDynamicList() {
        final Structure varListStructure = mockStructure();

        final Field varListField = mock(Field.class);
        when(varListField.getType()).thenReturn(DataType.DYNAMIC_LIST);
        when(varListField.getName()).thenReturn("DYNAMIC_LIST");
        when(varListField.getStructure()).thenReturn(varListStructure);

        // Create a factory to return a specific record list
        final DynamicRecordList recordList = mock(DynamicRecordList.class);
        final Answer<DynamicRecordList> factoryAnswer = new Answer<DynamicRecordList>() {
            public DynamicRecordList answer(final InvocationOnMock invocation) throws Throwable {
                return recordList;
            }
        };
        final DynamicRecordListImplFactory factory = mock(DynamicRecordListImplFactory.class, factoryAnswer);

        final Structure recordStructure = mockStructure(varListField);
        final RecordImpl record = new RecordImpl(ROOT_NAME, recordStructure, null, FIXED_LIST_FACTORY, factory,
                RECORD_FACTORY);
        assertThat(record.getDynamicList(varListField.getName())).isSameAs(recordList.iterator());
        final DynamicRecordList drl = record.getDynamicList(varListField.getName());
        assertThat(drl).isSameAs(recordList.iterator());
    }

    /**
     * Test the fetching of a non-existent dynamic list.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetDynamicListNonExixtant() {
        final Structure varListStructure = mockStructure();

        final Field varListField = mock(Field.class);
        when(varListField.getType()).thenReturn(DataType.DYNAMIC_LIST);
        when(varListField.getName()).thenReturn("DYNAMIC_LIST");
        when(varListField.getStructure()).thenReturn(varListStructure);

        // Create a factory to return a specific record list
        final DynamicRecordList recordList = mock(DynamicRecordList.class);
        final Answer<DynamicRecordList> factoryAnswer = new Answer<DynamicRecordList>() {
            public DynamicRecordList answer(final InvocationOnMock invocation) throws Throwable {
                return recordList;
            }
        };
        final DynamicRecordListImplFactory factory = mock(DynamicRecordListImplFactory.class, factoryAnswer);

        final Structure recordStructure = mockStructure(varListField);
        final RecordImpl record = new RecordImpl(ROOT_NAME, recordStructure, null, FIXED_LIST_FACTORY, factory,
                RECORD_FACTORY);
        assertThat(record.getDynamicList("non_existent_list")).isNull();
    }

    /**
     * Test that the correct default value is set for an dynamic list field.
     */
    @Test
    public void testGetDynamicListDefaultValue() {
        final Structure varListStructure = mockStructure();

        final Field varListField = mock(Field.class);
        when(varListField.getType()).thenReturn(DataType.DYNAMIC_LIST);
        when(varListField.getName()).thenReturn("DYNAMIC_LIST");
        when(varListField.getStructure()).thenReturn(varListStructure);

        final Structure recordStructure = mockStructure(varListField);

        final RecordImpl record = new RecordImpl(ROOT_NAME, recordStructure, null);
        assertThat(record.getDynamicList("DYNAMIC_LIST")).isEmpty();
    }

    /**
     * Test that the correct default value is set for an F8 field.
     */
    @Test
    public void testGetF8DefaultValue() {
        final Structure structure = mockStructure(F8_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        assertThat(record.getF8(F8_FIELD.getName())).isZero();
    }

    /**
     * Test that getting the value for a field that does not exist fails.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetFieldNotFound() {
        new RecordImpl(ROOT_NAME, mockStructure(), null).getF8(F8_FIELD.getName());
    }

    /**
     * Verify that I2 value can be interpreted to {@code false} values.
     */
    @Test
    public void testGetI2BooleanFalse() {
        final Structure structure = mockStructure(I2_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI2(I2_FIELD.getName(), (short) 0);
        assertThat(record.getI2Boolean(I2_FIELD.getName())).isFalse();
    }

    /**
     * Test that I2 values that are not 0 or 1 fail to be translated to boolean values.
     */
    @Test(expected = IllegalStateException.class)
    public void testGetI2BooleanInvalidValue() {
        final Structure structure = mockStructure(I2_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI2(I2_FIELD.getName(), (short) 23);
        record.getI2Boolean(I2_FIELD.getName());
    }

    /**
     * Test that I2 values that are 1 are translated to {@code true}.
     */
    @Test
    public void testGetI2BooleanTrue() {
        final Structure structure = mockStructure(I2_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI2(I2_FIELD.getName(), (short) 1);
        assertThat(record.getI2Boolean(I2_FIELD.getName())).isTrue();
    }

    /**
     * Test that the correct default value is set for I2 values.
     */
    @Test
    public void testGetI2DefaultValue() {
        final Structure structure = mockStructure(I2_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        assertThat(record.getI2(I2_FIELD.getName())).isZero();
    }

    /**
     * Test that the correct default value is set for I4 values.
     */
    @Test
    public void testGetI4DefaultValue() {
        final Structure structure = mockStructure(I4_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        assertThat(record.getI4(I4_FIELD.getName())).isZero();
    }

    /**
     * Test that getI4 if correct value is returned for extra small I4 values.
     */
    @Test
    public void testGetI4ExtraSmallValue() {
        final Structure structure = mockStructure(I4_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI4(I4_FIELD.getName(), Short.MIN_VALUE - 10);
        assertThat(record.getI4(I4_FIELD.getName())).isEqualTo(Short.MIN_VALUE - 10);
    }

    /**
     * Test that the correct value is returned for extra large I4 values.
     */
    @Test
    public void testGetI4ExtraLargeValue() {
        final Structure structure = mockStructure(I4_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI4(I4_FIELD.getName(), Short.MAX_VALUE + 10);
        assertThat(record.getI4(I4_FIELD.getName())).isEqualTo(Short.MAX_VALUE + 10);
    }

    /**
     * Test that setI4 succeeds on an I2 field with a value that is almost too small.
     */
    @Test
    public void testSetI4AlmostTooSmallOnI2Field() {
        final Structure structure = mockStructure(I2_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI4(I2_FIELD.getName(), Short.MIN_VALUE);
        assertThat(record.getI4(I2_FIELD.getName())).isEqualTo(Short.MIN_VALUE);
    }

    /**
     * Test that setI4 fails on an I2 field with a value that is almost too large.
     */
    @Test
    public void testSetI4AlmostTooLargeOnI2Field() {
        final Structure structure = mockStructure(I2_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI4(I2_FIELD.getName(), Short.MAX_VALUE);
        assertThat(record.getI4(I2_FIELD.getName())).isEqualTo(Short.MAX_VALUE);
    }

    /**
     * Test that setI4 fails on an I2 field with a value that is too small.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetI4TooSmallOnI2Field() {
        final Structure structure = mockStructure(I2_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI4(I2_FIELD.getName(), Short.MIN_VALUE - 1);
    }

    /**
     * Test that setI4 fails on an I2 field with a value that is too large.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetI4TooLargeOnI2Field() {
        final Structure structure = mockStructure(I2_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI4(I2_FIELD.getName(), Short.MAX_VALUE + 1);
    }

    /**
     * Test the fetching of a list nested beneath a record.
     */
    @Test
    public void testGetList() {
        final Structure listStructure = mockStructure();

        final Field listField = mock(Field.class);
        when(listField.getName()).thenReturn("LIST");
        when(listField.getType()).thenReturn(DataType.LIST);
        when(listField.getStructure()).thenReturn(listStructure);

        // Create a factory to return a specific record list
        final RecordList recordList = mock(RecordList.class);
        final Answer<RecordList> factoryAnswer = new Answer<RecordList>() {
            public RecordList answer(final InvocationOnMock invocation) throws Throwable {
                return recordList;
            }
        };
        final RecordListImplFactory factory = mock(RecordListImplFactory.class, factoryAnswer);

        final Structure recordStructure = mockStructure(listField);
        final RecordImpl record = new RecordImpl(ROOT_NAME, recordStructure, null, factory, VAR_LIST_FACTORY,
                RECORD_FACTORY);
        assertThat(record.getList(listField.getName())).isSameAs(recordList.iterator());
        final RecordList rl = record.getList(listField.getName());
        assertThat(rl).isSameAs(recordList.iterator());
    }

    /**
     * Test the fetching of an actual from a record.
     */
    @Test
    public void testGetListForReal() {
        final Map<String, Field> listFields = new TreeMap<String, Field>();
        listFields.put("name_l", new PrimitiveFieldImpl("name_l", DataType.VC));
        listFields.put("id_l", new PrimitiveFieldImpl("id_l", DataType.F8));
        final Structure listStructure = new StructureImpl(listFields);

        final Map<String, Field> rootFields = new TreeMap<String, Field>();
        rootFields.put("name", new PrimitiveFieldImpl("name", DataType.VC));
        rootFields.put("id", new PrimitiveFieldImpl("id", DataType.F8));
        rootFields.put("relations", new FixedLengthListFieldImpl("relations", listStructure, 3));
        final Structure rootStructure = new StructureImpl(rootFields);

        final Record rootRecord = new RecordImpl(ROOT_NAME, rootStructure, null);
        final Record listRecord = new RecordImpl("list", listStructure, rootRecord);

        assertThat(rootRecord.getList("relations").getStructure()).isEqualTo(listRecord.getStructure());
        assertThat(rootRecord.getList("relations").getSize()).isEqualTo(3);
    }

    /**
     * Test the fetching of a non-existent list member.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetListNonExistent() {
        final Structure listStructure = mockStructure();

        final Field listField = mock(Field.class);
        when(listField.getName()).thenReturn("LIST");
        when(listField.getType()).thenReturn(DataType.LIST);
        when(listField.getStructure()).thenReturn(listStructure);

        // Create a factory to return a specific record list
        final RecordList recordList = mock(RecordList.class);
        final Answer<RecordList> factoryAnswer = new Answer<RecordList>() {
            public RecordList answer(final InvocationOnMock invocation) throws Throwable {
                return recordList;
            }
        };
        final RecordListImplFactory factory = mock(RecordListImplFactory.class, factoryAnswer);

        final Structure recordStructure = mockStructure(listField);
        final RecordImpl record = new RecordImpl(ROOT_NAME, recordStructure, null, factory, VAR_LIST_FACTORY,
                RECORD_FACTORY);
        assertThat(record.getList("non_existent_list")).isNull();
    }

    /**
     * Test that fetching the name works.
     */
    @Test
    public void testGetName() {
        final RecordImpl record = new RecordImpl(ROOT_NAME, mock(Structure.class), null);
        assertThat(record.getName()).isEqualTo(ROOT_NAME);
    }

    /**
     * Verify that root elements have the proper level nested value.
     */
    @Test
    public void testGetNestedLevelRoot() {
        final RecordImpl record = new RecordImpl(ROOT_NAME, mock(Structure.class), null);
        assertThat(record.getNestedLevel()).isZero();
    }

    /**
     * Verify that an element inherits the nested level of its parent.
     */
    @Test
    public void testGetNestedLevelNested() {
        final Record parent = mock(Record.class);
        when(parent.getNestedLevel()).thenReturn(Integer.valueOf(2));

        final RecordImpl record = new RecordImpl(ROOT_NAME, mock(Structure.class), parent);
        assertThat(record.getNestedLevel()).isEqualTo(parent.getNestedLevel() + 1);
    }

    /**
     * Test the fetching of a record structure nested within another record structure.
     */
    @Test
    public void testGetRecord() {
        final Field nestedRecordField = mock(Field.class);
        when(nestedRecordField.getType()).thenReturn(DataType.RECORD);
        when(nestedRecordField.getName()).thenReturn("RECORD_NAME");

        final Record nestedRecord = mock(Record.class);
        final Answer<Record> recordAnswer = new Answer<Record>() {
            public Record answer(final InvocationOnMock invocation) throws Throwable {
                return nestedRecord;
            }
        };
        final RecordImplFactory factory = mock(RecordImplFactory.class, recordAnswer);

        final Structure rootStructure = mockStructure(nestedRecordField);
        final RecordImpl record = new RecordImpl(ROOT_NAME, rootStructure, null, FIXED_LIST_FACTORY, VAR_LIST_FACTORY,
                factory);
        assertThat(record.getRecord(nestedRecordField.getName())).isEqualTo(nestedRecord);
    }

    /**
     * Test that obtaining the structure works.
     */
    @Test
    public void testGetStructure() {
        final Structure structure = mockStructure();
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        assertThat(record.getStructure()).isSameAs(structure);
    }

    /**
     * Validate that the field type can be obtained.
     */
    @Test
    public void testGetType() {
        final Structure structure = mockStructure(VC_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        assertThat(record.getType(VC_FIELD.getName())).isEqualTo(VC_FIELD.getType());
    }

    /**
     * Validate that attempting to get the type of a field that does not exist within the record.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetTypeFieldNotFound() {
        final Structure structure = mockStructure(VC_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.getType("FIELD NOT FOUND");
    }

    /**
     * Test the fetching of the default value of a VC field.
     */
    @Test
    public void testGetVcDefaultValue() {
        final Structure structure = mockStructure(VC_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        assertThat(record.getVC(VC_FIELD.getName())).isEqualTo("");
    }

    /**
     * Test that a member of the record can be found.
     */
    @Test
    public void testHasMember() {
        final Structure structure = mockStructure(VC_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        assertThat(record.hasMember(VC_FIELD.getName())).isTrue();
    }

    /**
     * Test that the record correctly reflects when a field does not exist within it.
     */
    @Test
    public void testHasNotMember() {
        final Structure structure = mockStructure(VC_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        assertThat(record.hasMember("DOES NOT EXIST")).isFalse();
    }

    /**
     * Test that the record correctly reflects that it's a root record.
     */
    @Test
    public void testIsRoot() {
        assertThat(new RecordImpl(ROOT_NAME, mockStructure(), null).isRoot()).isTrue();
    }

    /**
     * Test that the record correctly reflects that it's not a root record.
     */
    @Test
    public void testIsNotRoot() {
        assertThat(new RecordImpl(ROOT_NAME, mockStructure(), mock(Record.class)).isRoot()).isFalse();
    }

    /**
     * Test that the character field is properly set.
     */
    @Test
    public void testSetChar() {
        final String setValue = "I am a set value.";
        final RecordImpl record = new RecordImpl(ROOT_NAME, mockStructure(CHAR_FIELD), null);
        record.setChar(CHAR_FIELD.getName(), setValue);
        assertThat(record.getChar(CHAR_FIELD.getName())).isEqualTo(setValue);
    }

    /**
     * Test that the character field is properly set when the string is as long as the field allows.
     */
    @Test
    public void testSetCharMaximumLenght() {
        final int valueLength = (int) CHAR_FIELD.getDataLength();
        final StringBuilder builder = new StringBuilder(valueLength);
        for (int i = 0; i < valueLength; i++) {
            builder.append('.');
        }
        final String setValue = builder.toString();
        final RecordImpl record = new RecordImpl(ROOT_NAME, mockStructure(CHAR_FIELD), null);
        record.setChar(CHAR_FIELD.getName(), setValue);
        assertThat(record.getChar(CHAR_FIELD.getName())).isEqualTo(setValue);
    }

    /**
     * Verify that setting a character field to a one too long of a value fails.
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSetCharOneTooLong() {
        final int valueLength = (int) (CHAR_FIELD.getDataLength() + 1);
        final StringBuilder builder = new StringBuilder(valueLength);
        for (int i = 0; i < valueLength; i++)
            builder.append(' ');

        final RecordImpl record = new RecordImpl(ROOT_NAME, mockStructure(CHAR_FIELD), null);
        record.setChar(CHAR_FIELD.getName(), builder.toString());
    }

    /**
     * Verify that setting a character field to a more than one too long value fails.
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testSetCharTwoTooLong() {
        final int valueLength = (int) (CHAR_FIELD.getDataLength() + 2);
        final StringBuilder builder = new StringBuilder(valueLength);
        for (int i = 0; i < valueLength; i++)
            builder.append(' ');

        final RecordImpl record = new RecordImpl(ROOT_NAME, mockStructure(CHAR_FIELD), null);
        record.setChar(CHAR_FIELD.getName(), builder.toString());
    }

    /**
     * Test the setting of a DQ8 value.
     */
    @Test
    public void testSetDq8() {
        final Date newValue = new Date();

        final Structure structure = mockStructure(DQ8_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setDQ8(DQ8_FIELD.getName(), newValue);
        assertThat(record.getDQ8(DQ8_FIELD.getName())).isEqualTo(newValue);
    }

    /**
     * Test the setting of an I2 value to a short value.
     */
    @Test
    public void testSetI2Short() {
        final short newValue = 123;

        final Structure structure = mockStructure(I2_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI2(I2_FIELD.getName(), newValue);
        assertThat(record.getI2(I2_FIELD.getName())).isEqualTo(newValue);
    }

    /**
     * Test the setting of an I2 value to a boolean value.
     */
    @Test
    public void testSetI2Boolean() {
        final Structure structure = mockStructure(I2_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI2(I2_FIELD.getName(), true);
        assertThat(record.getI2(I2_FIELD.getName())).isEqualTo((short) 1);
        record.setI2(I2_FIELD.getName(), false);
        assertThat(record.getI2(I2_FIELD.getName())).isEqualTo((short) 0);
    }

    /**
     * Test the setting of an I2 field using setI4 with a value that is within the bounds of a short.
     */
    @Test
    public void testSetI2LongValid() {
        final int newValue = 123;

        final Structure structure = mockStructure(I2_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI4(I2_FIELD.getName(), newValue);
        assertThat(record.getI2(I2_FIELD.getName())).isEqualTo((short) newValue);
    }

    /**
     * Test the setting of an I2 field using setI4 with a value that is less than Short.MIN_VAL.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetI2LongTooSmall() {
        final int newValue = Short.MIN_VALUE - 1;

        final Structure structure = mockStructure(I2_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI4(I2_FIELD.getName(), newValue);
    }

    /**
     * Test the setting of an I2 field using setI4 with a value that is greater than Short.MAX_VAL.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetI2LongTooBig() {
        final int newValue = Short.MAX_VALUE + 1;

        final Structure structure = mockStructure(I2_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI4(I2_FIELD.getName(), newValue);
    }

    /**
     * Verify that boolean {@code false} values are properly translated.
     */
    @Test
    public void testSetI2BooleanFalse() {
        final Structure structure = mockStructure(I2_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI2(I2_FIELD.getName(), false);
        assertThat(record.getI2(I2_FIELD.getName())).isZero();
    }

    /**
     * Verify that boolean {@code true} values are properly translated.
     */
    @Test
    public void testSetI2BooleanTrue() {
        final Structure structure = mockStructure(I2_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI2(I2_FIELD.getName(), true);
        assertThat(record.getI2(I2_FIELD.getName())).isEqualTo((short) 1);
    }

    /**
     * Test that setting an I2 value to too big fails.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetI2TooBig() {
        final int newValue = Integer.MAX_VALUE;

        final Structure structure = mockStructure(I2_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI4(I2_FIELD.getName(), newValue);
    }

    /**
     * Test that setting an I2 value to too small fails.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetI2TooSmall() {
        final int newValue = Integer.MIN_VALUE;

        final Structure structure = mockStructure(I2_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI4(I2_FIELD.getName(), newValue);
    }

    /**
     * Test the setting of an I4 value.
     */
    @Test
    public void testSetI4() {
        final int newValue = 123;

        final Structure structure = mockStructure(I4_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setI4(I4_FIELD.getName(), newValue);
        assertThat(record.getI4(I4_FIELD.getName())).isEqualTo(newValue);
    }

    /**
     * Test the setting of an F8 value.
     */
    @Test
    public void testSetF8() {
        final double newValue = 123d;

        final Structure structure = mockStructure(F8_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setF8(F8_FIELD.getName(), newValue);
        assertThat(record.getF8(F8_FIELD.getName())).isEqualTo(newValue);
    }

    /**
     * Test that an exception is thrown on an attempt to set a value on a non-existent F8 field.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetF8VNonExistent() {
        final double newValue = 123d;

        final Structure structure = mockStructure(F8_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setF8("non_existent_field", newValue);
    }

    /**
     * Test the setting of a VC value.
     */
    @Test
    public void testSetVc() {
        final String newValue = "This is Jack Bauer, I need to speak with the president.";

        final Structure structure = mockStructure(VC_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);
        record.setVC(VC_FIELD.getName(), newValue);
        assertThat(record.getVC(VC_FIELD.getName())).isEqualTo(newValue);
    }

    /**
     * Test the toString() of a record when all it has are primitive fields.
     * <br>
     * The expected output of the method is:
     *
     * <pre>
     * root
     *  1 F8_FIELD = F8 {0.0}
     * </pre>
     */
    @Test
    public void testToStringAllPrimitives() {
        final Structure structure = mockStructure(F8_FIELD);
        final RecordImpl record = new RecordImpl(ROOT_NAME, structure, null);

        assertThat(record.toString())
                .isEqualTo("root\n" + " 1 " + F8_FIELD.getName() + " = " + F8_FIELD.getType().toString() + " {0.0}\n");
    }

    /**
     * Test that the toString() method handles a fixed-length list.
     *
     * <pre>
     * root
     *  1 LIST [1,2]
     * firstElement
     *  1 LIST [2,2]
     * secondElement
     * </pre>
     */
    @Test
    public void testToStringList() {
        final Record firstElement = mock(Record.class);
        when(firstElement.toString()).thenReturn("firstElement\n");
        final Record secondElement = mock(Record.class);
        when(secondElement.toString()).thenReturn("secondElement\n");

        final RecordList list = mock(RecordList.class);
        when(list.iterator()).thenReturn(Arrays.asList(firstElement, secondElement).iterator());
        when(list.getSize()).thenReturn(2);

        final Structure listElementStructure = mockStructure(F8_FIELD);
        final Field listField = mock(Field.class);
        when(listField.getType()).thenReturn(DataType.LIST);
        when(listField.getName()).thenReturn("LIST");
        when(listField.getStructure()).thenReturn(listElementStructure);

        final Structure rootStructure = mockStructure(listField);
        when(rootStructure.getFields()).thenReturn(Arrays.asList(listField));

        final Answer<RecordList> factoryAnswer = new Answer<RecordList>() {
            public RecordList answer(final InvocationOnMock invocation) throws Throwable {
                return list;
            }
        };
        final RecordListImplFactory factory = mock(RecordListImplFactory.class, factoryAnswer);
        final RecordImpl record = new RecordImpl(ROOT_NAME, rootStructure, null, factory, VAR_LIST_FACTORY,
                RECORD_FACTORY);

        assertThat(record.toString())
                .isEqualTo("root\n" + " 1 LIST [1,2]\n" + "firstElement\n" + " 1 LIST [2,2]\n" + "secondElement\n");
    }

    /**
     * Test the toString() method for a record structure that has a nested record structure.
     * <br>
     * The expected output is:
     *
     * <pre>
     * root
     *  1 RECORD
     * RECORD toString()
     * </pre>
     */
    @Test
    public void testToStringRecord() {
        final Structure recordStructure = mockStructure(F8_FIELD);
        final Record nestedRecord = mock(Record.class);
        when(nestedRecord.getStructure()).thenReturn(recordStructure);
        when(nestedRecord.toString()).thenReturn("RECORD toString()");

        final Field recordField = mock(Field.class);
        when(recordField.getName()).thenReturn("RECORD");
        when(recordField.getType()).thenReturn(DataType.RECORD);

        final Structure rootStructure = mockStructure(recordField);

        final Answer<Record> factoryAnswer = new Answer<Record>() {
            public Record answer(final InvocationOnMock invocation) throws Throwable {
                return nestedRecord;
            }
        };
        final RecordImplFactory factory = mock(RecordImplFactory.class, factoryAnswer);

        final RecordImpl record = new RecordImpl(ROOT_NAME, rootStructure, null, FIXED_LIST_FACTORY, VAR_LIST_FACTORY,
                factory);

        assertThat(record.toString()).isEqualTo("root\n" + " 1 RECORD\n" + "RECORD toString()");
    }

    /**
     * Create a mock structure.
     *
     * @param fields
     *            An array of {@link Field} objects to be stored within the created structure.
     * @return A {@link Structure} object.
     */
    private Structure mockStructure(final Field... fields) {
        final Structure structure = mock(Structure.class);
        for (final Field field : fields) {
            when(structure.getField(field.getName())).thenReturn(field);

            final DataType dataType = field.getType();
            when(structure.getType(field.getName())).thenReturn(dataType);

            when(structure.hasMember(field.getName())).thenReturn(Boolean.TRUE);
        }
        when(structure.getFields()).thenReturn(Arrays.asList(fields));
        return structure;
    }
}
