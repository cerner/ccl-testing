package com.cerner.ccl.j4ccl.impl.util;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.DynamicRecordList;
import com.cerner.ccl.j4ccl.record.Field;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.RecordList;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * Unit test for {@link RecordWriter}.
 *
 * @author Joshua Hyde
 *
 */

public class RecordWriterTest {
    private static final Field I2_FIELD = mock(Field.class);
    private static final Field I4_FIELD = mock(Field.class);
    private static final Field DQ8_FIELD = mock(Field.class);
    private static final Field F8_FIELD = mock(Field.class);
    private static final Field VC_FIELD = mock(Field.class);
    private static final Field CHAR_FIELD = mock(Field.class);

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
        when(CHAR_FIELD.getDataLength()).thenReturn(Long.valueOf(10));
    }

    /**
     * Test that the search of the JSON data is case-insensitive.
     */
    @Test
    public void testHasJsonElementCaseInsensitive() {
        final String json = "{\"REPLY\":{\"" + F8_FIELD.getName() + "\":2.025000}}";
        final Record record = mockRecord("reply", mockStructure(F8_FIELD));
        RecordWriter.putFromJson(json, record);
        verify(record, times(1)).setF8(F8_FIELD.getName(), 2.025);
    }

    /**
     * Test that a fixed-length character field can be properly populated from JSON data.
     */
    @Test
    public void testPutFromJsonChar() {
        final String json = "{\"REPLY\":{\"CHAR_FIELD\":\"blech\"}}";
        final Record record = mockRecord("REPLY", mockStructure(CHAR_FIELD));
        RecordWriter.putFromJson(json, record);
        verify(record, times(1)).setChar(CHAR_FIELD.getName(), "blech");
    }

    /**
     * Test the storage of a fixed-length list with multiple elements.
     */
    @Test
    public void testPutFromJsonDeepSimpleList() {
        final String json = "{\"REPLY\":{\"FIXED_LIST\":[{\"F8_FIELD\":123.456000},{\"F8_FIELD\":789.0120000}]}}";

        final Structure listStructure = mockStructure(F8_FIELD);
        final Field listField = mockField("FIXED_LIST", DataType.LIST, listStructure);
        when(listField.getListSize()).thenReturn(Integer.valueOf(1));

        final Record firstListElement = mockRecord("FIXED_LIST", listStructure);
        final Record secondListElement = mockRecord("FIXED_LIST", listStructure);
        final RecordList recordList = mock(RecordList.class);
        when(recordList.iterator()).thenReturn(Arrays.asList(firstListElement, secondListElement).iterator());
        when(recordList.getSize()).thenReturn(Integer.valueOf(2));

        final Structure recordStructure = mockStructure(listField);
        final Record record = mockRecord("REPLY", recordStructure);
        when(record.getList("FIXED_LIST")).thenReturn(recordList);

        RecordWriter.putFromJson(json, record);
        verify(firstListElement, times(1)).setF8(F8_FIELD.getName(), 123.456);
        verify(secondListElement, times(1)).setF8(F8_FIELD.getName(), 789.012);
    }

    /**
     * Test that the putter fails if given JSON with a different number of elements than the list.
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testPutFromJsonDeepSimpleListDifferentSize() {
        final String json = "{\"REPLY\":{\"FIXED_LIST\":[{\"F8_FIELD\":123.456000},{\"F8_FIELD\":789.0120000}]}}";

        final Structure listStructure = mockStructure(F8_FIELD);
        final Field listField = mockField("FIXED_LIST", DataType.LIST, listStructure);
        when(listField.getListSize()).thenReturn(Integer.valueOf(1));

        final RecordList recordList = mock(RecordList.class);
        when(recordList.getSize()).thenReturn(Integer.valueOf(1));

        final Structure recordStructure = mockStructure(listField);
        final Record record = mockRecord("REPLY", recordStructure);
        when(record.getList("FIXED_LIST")).thenReturn(recordList);

        RecordWriter.putFromJson(json, record);
    }

    /**
     * Test the storage of a DQ8 variable.
     */
    @Test
    public void testPutFromJsonDq8() {
        final String timestamp = "2009-01-01T01:01:01.000-05:00";
        final Date expectedDate = CclUtils.convertTimestamp(timestamp);
        final String json = "{\"REPLY\":{\"" + DQ8_FIELD.getName() + "\":\"\\/Date(" + timestamp + ")\\/\"}}";

        final Record record = mockRecord("REPLY", mockStructure(DQ8_FIELD));
        RecordWriter.putFromJson(json, record);
        verify(record, times(1)).setDQ8(DQ8_FIELD.getName(), expectedDate);
    }

    /**
     * Test that, if the date value is the wrong format, the writer fails.
     */
    @Test(expected = RuntimeException.class)
    public void testPutFromJsonDq8BadFormat() {
        final String timestamp = "2009-01-01T01:01:01.000-05:00";
        final String json = "{\"REPLY\":{\"" + DQ8_FIELD.getName() + "\":\"\\te(" + timestamp + ")\\/\"}}";

        final Record record = mockRecord("REPLY", mockStructure(DQ8_FIELD));
        RecordWriter.putFromJson(json, record);
    }

    /**
     * Test that, if the date value is 0, the writer will insert null.
     */
    @Test()
    public void testPutFromJsonDq8ZeroValue() {
        final String timestamp = "0000-00-00T00:00:00.000+00:00";
        final String json = "{\"REPLY\":{\"" + DQ8_FIELD.getName() + "\":\"\\/Date(" + timestamp + ")\\/\"}}";

        final Record record = mockRecord("REPLY", mockStructure(DQ8_FIELD));

        RecordWriter.putFromJson(json, record);

        verify(record, times(1)).setDQ8(DQ8_FIELD.getName(), null);
    }

    /**
     * Test that data can be loaded from a variable-length list.
     */
    @Test
    public void testPutFromJsonDynamicList() {
        final String json = "{\"REPLY\":{\"VAR_LIST\":[{\"F8_FIELD\":987.654000},{\"F8_FIELD\":321.098000}]}}";

        final Structure listStructure = mockStructure(F8_FIELD);
        final Field listField = mockField("VAR_LIST", DataType.DYNAMIC_LIST, listStructure);
        when(listField.getListSize()).thenReturn(Integer.valueOf(2));

        final Record firstListElement = mockRecord("VAR_LIST", listStructure);
        final Record secondListElement = mockRecord("VAR_LIST", listStructure);
        final DynamicRecordList recordList = mock(DynamicRecordList.class);
        when(recordList.get(0)).thenReturn(firstListElement);
        when(recordList.get(1)).thenReturn(secondListElement);
        when(recordList.getSize()).thenReturn(Integer.valueOf(2));

        final Structure recordStructure = mockStructure(listField);
        final Record record = mockRecord("REPLY", recordStructure);
        when(record.getDynamicList("VAR_LIST")).thenReturn(recordList);

        RecordWriter.putFromJson(json, record);
        verify(firstListElement, times(1)).setF8(F8_FIELD.getName(), 987.654);
        verify(secondListElement, times(1)).setF8(F8_FIELD.getName(), 321.098);
    }

    /**
     * Verify that, if the size of the JSON object's list does not match the data object's size of the list, the writer
     * adds fields to the list
     */
    @Test
    public void testPutFromJsonDynamicListDifferentSize() {
        final String json = "{\"REPLY\":{\"VAR_LIST\":[{\"F8_FIELD\":987.654000},{\"F8_FIELD\":321.098000}]}}";

        final Structure listStructure = mockStructure(F8_FIELD);
        final Field listField = mockField("VAR_LIST", DataType.DYNAMIC_LIST, listStructure);
        when(listField.getListSize()).thenReturn(Integer.valueOf(2));

        final Record firstListElement = mockRecord("VAR_LIST", listStructure);
        final Record secondListElement = mockRecord("VAR_LIST", listStructure);
        final DynamicRecordList recordList = mock(DynamicRecordList.class);
        when(recordList.getSize()).thenReturn(Integer.valueOf(1));
        when(recordList.get(0)).thenReturn(firstListElement);
        when(recordList.addItem()).thenReturn(secondListElement);

        final Structure recordStructure = mockStructure(listField);
        final Record record = mockRecord("REPLY", recordStructure);
        when(record.getDynamicList("VAR_LIST")).thenReturn(recordList);

        RecordWriter.putFromJson(json, record);
        verify(recordList, times(1)).addItem();
        verify(firstListElement, times(1)).setF8(F8_FIELD.getName(), 987.654);
        verify(secondListElement, times(1)).setF8(F8_FIELD.getName(), 321.098);
    }

    /**
     * Verify that no errors occur when attempting to populate an empty variable-length list.
     */
    @Test
    public void testPutFromJsonEmptyVariableList() {
        final String json = "{\"REPLY\":{\"VAR_LIST\":[]}}";

        final Structure listStructure = mockStructure(F8_FIELD);
        final Field listField = mockField("VAR_LIST", DataType.DYNAMIC_LIST, listStructure);
        when(listField.getListSize()).thenReturn(Integer.valueOf(2));

        final DynamicRecordList recordList = mock(DynamicRecordList.class);
        final List<Record> records = Collections.emptyList();
        when(recordList.iterator()).thenReturn(records.iterator());
        when(recordList.getSize()).thenReturn(Integer.valueOf(0));

        final Structure recordStructure = mockStructure(listField);
        final Record record = mockRecord("REPLY", recordStructure);
        when(record.getDynamicList("VAR_LIST")).thenReturn(recordList);

        RecordWriter.putFromJson(json, record);
    }

    /**
     * Test the storage of an F8 variable.
     */
    @Test
    public void testPutFromJsonF8() {
        final String json = "{\"REPLY\":{\"" + F8_FIELD.getName() + "\":2.025000}}";
        final Record record = mockRecord("REPLY", mockStructure(F8_FIELD));
        RecordWriter.putFromJson(json, record);
        verify(record, times(1)).setF8(F8_FIELD.getName(), 2.025);
    }

    /**
     * Make sure that an I2 field is populated.
     */
    @Test
    public void testPutFromJsonI2() {
        final String json = "{\"REPLY\":{\"" + I2_FIELD.getName() + "\":123}}";
        final Record record = mockRecord("REPLY", mockStructure(I2_FIELD));

        RecordWriter.putFromJson(json, record);
        verify(record, times(1)).setI2(I2_FIELD.getName(), (short) 123);
    }

    /**
     * Test that, if given a value that exceeds {@link Short#MAX_VALUE}, that the I4 is attempted to be stored.
     */
    @Test
    public void testPutFromJsonI4() {
        final String json = "{\"REPLY\":{\"" + I4_FIELD.getName() + "\":32768}}";
        final Record record = mockRecord("REPLY", mockStructure(I4_FIELD));

        RecordWriter.putFromJson(json, record);
        verify(record, times(1)).setI4(I4_FIELD.getName(), 32768);
    }

    /**
     * Test that referencing a field in JSON that does not exist in the record structure data object.
     */
    @Test(expected = NoSuchFieldError.class)
    public void testPutFromJsonMissingField() {
        final String json = "{\"REPLY\":{\"NESTED_REC\":{\"F8_FIELD\":567.123000}}}";
        final Structure structure = mockStructure(VC_FIELD);
        final Record record = mockRecord("REPLY", structure);

        RecordWriter.putFromJson(json, record);
    }

    /**
     * Test that, if the name of a record structure does nto match the JSON structure, the writer fails.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testPutFromJsonRootNameMismatch() {
        final String json = "{\"REPLY\":{\"NESTED_REC\":{\"F8_FIELD\":567.123000}}}";
        final Record record = mock(Record.class);
        when(record.getName()).thenReturn("NOT_REPLY");

        RecordWriter.putFromJson(json, record);
    }

    /**
     * Test the storage of a nested record structure.
     */
    @Test
    public void testPutFromJsonRecord() {
        final String json = "{\"REPLY\":{\"NESTED_REC\":{\"F8_FIELD\":567.123000}}}";

        final Structure nestedStructure = mockStructure(F8_FIELD);
        final Field nestedField = mockField("NESTED_REC", DataType.RECORD, nestedStructure);
        final Record nestedRecord = mockRecord("NESTED_REC", nestedStructure);

        final Structure rootStructure = mockStructure(nestedField);
        final Record rootRecord = mockRecord("REPLY", rootStructure);
        when(rootRecord.getRecord("NESTED_REC")).thenReturn(nestedRecord);

        RecordWriter.putFromJson(json, rootRecord);
        verify(nestedRecord, times(1)).setF8(F8_FIELD.getName(), 567.123);
    }

    /**
     * Test the storage of a single-level fixed-length list.
     */
    @Test
    public void testPutFromJsonShallowSimpleList() {
        final String json = "{\"REPLY\":{\"FIXED_LIST\":{\"F8_FIELD\":123.456000}}}";

        final Structure listStructure = mockStructure(F8_FIELD);
        final Field listField = mockField("FIXED_LIST", DataType.LIST, listStructure);
        when(listField.getListSize()).thenReturn(Integer.valueOf(1));

        final Record listElement = mockRecord("FIXED_LIST", listStructure);
        final RecordList recordList = mock(RecordList.class);
        when(recordList.get(0)).thenReturn(listElement);
        when(recordList.getSize()).thenReturn(Integer.valueOf(1));

        final Structure recordStructure = mockStructure(listField);
        final Record record = mockRecord("REPLY", recordStructure);
        when(record.getList("FIXED_LIST")).thenReturn(recordList);

        RecordWriter.putFromJson(json, record);
        verify(listElement, times(1)).setF8(F8_FIELD.getName(), 123.456);
    }

    /**
     * Test that, when attempting to populate a single-element fixed-length list into a record structure has more than
     * one element, the population fails.
     */
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testPutFromJsonShallowSimpleListBadSize() {
        final String json = "{\"REPLY\":{\"FIXED_LIST\":{\"F8_FIELD\":123.456000}}}";

        final Structure listStructure = mockStructure(F8_FIELD);
        final Field listField = mockField("FIXED_LIST", DataType.LIST, listStructure);
        when(listField.getListSize()).thenReturn(Integer.valueOf(1));

        final Record listElement = mockRecord("FIXED_LIST", listStructure);
        final RecordList recordList = mock(RecordList.class);
        when(recordList.get(0)).thenReturn(listElement);
        when(recordList.getSize()).thenReturn(Integer.valueOf(234));

        final Structure recordStructure = mockStructure(listField);
        final Record record = mockRecord("REPLY", recordStructure);
        when(record.getList("FIXED_LIST")).thenReturn(recordList);

        RecordWriter.putFromJson(json, record);
    }

    /**
     * Test that the parsing of a nested double-quote within a VC field.
     */
    @Test
    public void testPutFromJsonNestedQuote() {
        final String json = "{\"REPLY\":{\"VC_FIELD\":\"\\\"\"}}";
        final Record record = mockRecord("REPLY", mockStructure(VC_FIELD));

        RecordWriter.putFromJson(json, record);
        verify(record, times(1)).setVC(VC_FIELD.getName(), "\"");
    }

    /**
     * Test that parsing of nested brackets works.
     */
    @Test
    public void testPutFromJsonNestedBrackets() {
        final String json = "{\"REPLY\":{\"VC_FIELD\":\"[nested brackets]\"}}";
        final Record record = mockRecord("REPLY", mockStructure(VC_FIELD));

        RecordWriter.putFromJson(json, record);
        verify(record, times(1)).setVC(VC_FIELD.getName(), "[nested brackets]");
    }

    /**
     * Verify that the parsing of a string with nested braces works within a VC field.
     */
    @Test
    public void testPutFromJsonNestedBraces() {
        final String json = "{\"REPLY\":{\"VC_FIELD\":\"{nested braces}\"}}";
        final Record record = mockRecord("REPLY", mockStructure(VC_FIELD));

        RecordWriter.putFromJson(json, record);
        verify(record, times(1)).setVC(VC_FIELD.getName(), "{nested braces}");
    }

    /**
     * Test that CDATA can be successfully parsed from the JSON notation.
     */
    @Test
    public void testPutFromJsonCData() {
        final String json = "{\"REPLY\":{\"VC_FIELD\":\"<![CDATA[test data]]>\"}}";
        final Record record = mockRecord("REPLY", mockStructure(VC_FIELD));

        RecordWriter.putFromJson(json, record);
        verify(record, times(1)).setVC(VC_FIELD.getName(), "<![CDATA[test data]]>");
    }

    /**
     * Test that a nested colon can be properly parsed.
     */
    @Test
    public void testPutFromJsonNestedColon() {
        final String json = "{\"REPLY\":{\"VC_FIELD\":\"nested colon:\"}}";
        final Record record = mockRecord("REPLY", mockStructure(VC_FIELD));

        RecordWriter.putFromJson(json, record);
        verify(record, times(1)).setVC(VC_FIELD.getName(), "nested colon:");
    }

    /**
     * Mock a field object.
     *
     * @param fieldName
     *            The name of the field.
     * @param dataType
     *            The data type of the field.
     * @return A mock {@link Field} object.
     */
    private Field mockField(final String fieldName, final DataType dataType) {
        final Field field = mock(Field.class);
        when(field.getName()).thenReturn(fieldName);
        when(field.getType()).thenReturn(dataType);
        return field;
    }

    /**
     * Mock a field object.
     *
     * @param fieldName
     *            The name of the field.
     * @param dataType
     *            The data type of the field.
     * @param structure
     *            A mock {@link Field} object.
     * @return
     */
    private Field mockField(final String fieldName, final DataType dataType, final Structure structure) {
        final Field field = mockField(fieldName, dataType);
        when(field.getStructure()).thenReturn(structure);
        return field;
    }

    /**
     * Create a mock record structure.
     *
     * @param name
     *            The name of the record structure.
     * @param structure
     *            The {@link Structure} backing the record.
     * @return A mock {@link Record} object.
     */
    private Record mockRecord(final String name, final Structure structure) {
        final Record record = mock(Record.class);
        when(record.getName()).thenReturn(name);
        when(record.getStructure()).thenReturn(structure);
        return record;
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