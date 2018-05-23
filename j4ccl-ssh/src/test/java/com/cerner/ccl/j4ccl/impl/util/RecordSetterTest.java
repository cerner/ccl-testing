package com.cerner.ccl.j4ccl.impl.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.junit.Test;

import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.DynamicRecordList;
import com.cerner.ccl.j4ccl.record.Field;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.RecordList;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * Unit test of {@link RecordSetter}.
 *
 * @author Joshua Hyde
 *
 */

public class RecordSetterTest {
    /**
     * If a string exceeds a maximum value, it should be split up into a series of concat() parameters.
     */
    @Test
    public void testGetSetterVcCommandsWayTooLong() {
        final String xChunk = StringUtils.repeat("x", 129);
        final String recordName = "record";
        final String fieldName = "vc_field";
        final int maxCharLength = 132 - ("set " + recordName + "->" + fieldName + " = concat('',\"'\",'')").length();
        final String data = StringUtils.repeat("x", maxCharLength + 259);

        final Field field = mockField(fieldName, DataType.VC);
        final Structure struct = mockStructure(field);
        final Record record = mockRecord(recordName, struct);
        when(record.getVC(fieldName)).thenReturn(data);

        final List<String> commands = RecordSetter.getSetterCommands(record);
        final String singleDataChunk = StringUtils.repeat("x", maxCharLength);
        int index = 0;
        /**
         * Expected to be:
         *
         * <pre>
         * set record->vc_field =
         * concat(
         * '<x to maxCharLength>'
         * ,
         * '<x to 129>'
         * ,
         * '<x to 129>'
         * ,
         * 'x'
         * )
         * go
         * </pre>
         */
        assertThat(commands.get(index++)).isEqualTo("set " + recordName + "->" + fieldName + " = ");
        assertThat(commands.get(index++)).isEqualTo("concat(");
        assertThat(commands.get(index++)).isEqualTo("'" + singleDataChunk + "'");
        assertThat(commands.get(index++)).isEqualTo(",");
        assertThat(commands.get(index++)).isEqualTo("'" + xChunk + "'");
        assertThat(commands.get(index++)).isEqualTo(",");
        assertThat(commands.get(index++)).isEqualTo("'" + xChunk + "'");
        assertThat(commands.get(index++)).isEqualTo(",");
        assertThat(commands.get(index++)).isEqualTo("'x'");
        assertThat(commands.get(index++)).isEqualTo(")");
        assertThat(commands.get(index++)).isEqualTo("go");
    }

    /**
     * If a string is exactly the maximum length, it should not be split up into a series of concat() parameters.
     */
    @Test
    public void testGetSetterVcCommandsBarelyTooLong() {
        final String recordName = "record";
        final String fieldName = "vc_field";
        final int maxCharLength = 133 - ("set " + recordName + "->" + fieldName + " = concat('',\"'\",'')").length();
        final String data = StringUtils.repeat("x", maxCharLength);

        final Field field = mockField(fieldName, DataType.VC);
        final Structure struct = mockStructure(field);
        final Record record = mockRecord(recordName, struct);
        when(record.getVC(fieldName)).thenReturn(data);

        final List<String> commands = RecordSetter.getSetterCommands(record);
        /**
         * Expected to be:
         *
         * <pre>
         * set record->vc_field =
         * concat(
         * 'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'
         * ,
         * 'x'
         * )
         * go
         *
         * </pre>
         */
        assertThat(commands.size()).isEqualTo(7);
        assertThat(commands.get(0)).isEqualTo("set " + recordName + "->" + fieldName + " = ");
        assertThat(commands.get(1)).isEqualTo("concat(");
        assertThat(commands.get(2)).isEqualTo(
                "'xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx'");
        assertThat(commands.get(3)).isEqualTo(",");
        assertThat(commands.get(4)).isEqualTo("'x'");
        assertThat(commands.get(5)).isEqualTo(")");
        assertThat(commands.get(6)).isEqualTo("go");
    }

    /**
     * Test boundary case for command length.
     */
    @Test
    public void testGetSetterVcCommandsAlmostTooLong() {
        final String recordName = "record";
        final String fieldName = "vc_field";
        final int maxCharLength = 132 - ("set " + recordName + "->" + fieldName + " = concat('',\"'\",'')").length();
        final String data = StringUtils.repeat("x", maxCharLength);

        final Field field = mockField(fieldName, DataType.VC);
        final Structure struct = mockStructure(field);
        final Record record = mockRecord(recordName, struct);
        when(record.getVC(fieldName)).thenReturn(data);

        final List<String> commands = RecordSetter.getSetterCommands(record);
        int index = 0;
        /**
         * Expected to be:
         *
         * <pre>
         * set record->vc_field =
         * concat(
         * '<x to maxCharLength>'
         * ,
         * '<x to 129>'
         * ,
         * '<x to 129>'
         * ,
         * 'x'
         * )
         * go
         * </pre>
         */
        assertThat(commands.get(index++)).isEqualTo("set " + recordName + "->" + fieldName + " = ");
        assertThat(commands.get(index++)).isEqualTo("'" + data + "'");
        assertThat(commands.get(index++)).isEqualTo("go");
    }

    /**
     * If a string exceeds a maximum value AND contains a single quote, it should be split up into a concat() call with
     * nested concat() calls for the single quote.
     */
    @Test
    public void testGetSetterVcCommandsTooLongSingleQuote() {
        final String xChunk = StringUtils.repeat("x", 129);
        final String recordName = "record";
        final String fieldName = "vc_field";
        final int maxCharLength = 132 - ("set " + recordName + "->" + fieldName + " = concat('',\"'\",'')").length();
        final String data = StringUtils.repeat("x", maxCharLength - 2) + "'x" + xChunk + xChunk + "x";

        final Field field = mockField(fieldName, DataType.VC);
        final Structure struct = mockStructure(field);
        final Record record = mockRecord(recordName, struct);
        when(record.getVC(fieldName)).thenReturn(data);

        final List<String> commands = RecordSetter.getSetterCommands(record);
        final String singleDataChunk = StringUtils.repeat("x", maxCharLength);
        int index = 0;

        /**
         * Expected to be:
         *
         * <pre>
         * set record->vc_field =
         * concat(
         * concat(
         * '<x to maxCharLength-2>'
         * , "'"
         * , 'x'
         * )
         * ,
         * '<x to 129>'
         * ,
         * '<x to 129>'
         * ,
         * 'x'
         * )
         * go
         * </pre>
         */
        assertThat(commands.get(index++)).isEqualTo("set " + recordName + "->" + fieldName + " = ");
        assertThat(commands.get(index++)).isEqualTo("concat(");
        assertThat(commands.get(index++)).isEqualTo("concat(");
        assertThat(commands.get(index++))
                .isEqualTo("'" + singleDataChunk.substring(0, singleDataChunk.length() - 2) + "'");
        assertThat(commands.get(index++)).isEqualTo(", \"'\"");
        assertThat(commands.get(index++)).isEqualTo(", 'x'");
        assertThat(commands.get(index++)).isEqualTo(")");
        assertThat(commands.get(index++)).isEqualTo(",");
        assertThat(commands.get(index++)).isEqualTo("'" + xChunk + "'");
        assertThat(commands.get(index++)).isEqualTo(",");
        assertThat(commands.get(index++)).isEqualTo("'" + xChunk + "'");
        assertThat(commands.get(index++)).isEqualTo(",");
        assertThat(commands.get(index++)).isEqualTo("'x'");
        assertThat(commands.get(index++)).isEqualTo(")");
        assertThat(commands.get(index++)).isEqualTo("go");
    }

    /**
     * Test the setting of a character value.
     */
    @Test
    public void testGetSetterCommandsCharNoSingleQuote() {
        final String fieldName = "char_field";
        final String recordName = "record_name";
        final String value = "test";
        final int dataLength = value.length();

        final Field field = mockCharacterField(fieldName, dataLength);

        final Structure struct = mockStructure(field);

        final Record record = mockRecord(recordName, struct);
        when(record.getChar(fieldName)).thenReturn(value);

        final List<String> commands = RecordSetter.getSetterCommands(record);
        assertThat(commands).containsExactly("set record_name->char_field = ", "'" + value + "'", "go");
    }

    /**
     * Verify that, given a character field with single quotes, they're properly embedded inside a concat() call.
     */
    @Test
    public void testGetSetterCommandsCharSingleQuote() {
        final String fieldName = "char_field";
        final String recordName = "record_name";
        final String value = "test o'auth";
        final int dataLength = value.length();

        final Field field = mockCharacterField(fieldName, dataLength);

        final Structure struct = mockStructure(field);

        final Record record = mockRecord(recordName, struct);
        when(record.getChar(fieldName)).thenReturn(value);

        final List<String> commands = RecordSetter.getSetterCommands(record);
        assertThat(commands).containsExactly("set record_name->char_field = ", "concat(", "'test o'", ", \"'\"",
                ", 'auth'", ")", "go");
    }

    /**
     * If a character field has no value set, then it should not have a setter contributed.
     */
    @Test
    public void testGetSetterCommandsCharUnset() {
        final String fieldName = "char_field";
        final String recordName = "record_name";
        final int dataLength = 4;

        final Field field = mockCharacterField(fieldName, dataLength);

        final Structure struct = mockStructure(field);

        final Record record = mockRecord(recordName, struct);
        when(record.getChar(fieldName)).thenReturn("");

        assertThat(RecordSetter.getSetterCommands(record)).isEmpty();
    }

    /**
     * Test the setting of a DQ8 value.
     */
    @Test
    public void testGetSetterCommandsDq8() {
        final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        builder.appendDayOfMonth(2).appendLiteral('-').appendMonthOfYearShortText().appendLiteral('-').appendYear(4, 4)
                .appendLiteral(' ').appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).appendLiteral(':')
                .appendSecondOfMinute(2).appendLiteral('.').appendMillisOfSecond(3);
        final DateTimeFormatter formatter = builder.toFormatter();

        final Date currentDate = Calendar.getInstance().getTime();

        final String fieldName = "dq8_field";
        final String recordName = "record_name";

        final Field field = mockField(fieldName, DataType.DQ8);

        final Structure struct = mockStructure(field);

        final Record record = mockRecord(recordName, struct);
        when(record.getDQ8(fieldName)).thenReturn(currentDate);

        final Collection<String> commands = RecordSetter.getSetterCommands(record);
        assertThat(commands).containsOnly("set record_name->dq8_field = cnvtdatetime('"
                + formatter.print(currentDate.getTime()).toUpperCase() + "') go");
    }

    /**
     * Test the setting of F8 values.
     */
    @Test
    public void testGetSetterCommandsF8() {
        final String fieldName = "f8_field";
        final String recordName = "record_name";
        final double value = Double.MAX_VALUE;

        final Field field = mockField(fieldName, DataType.F8);

        final Structure struct = mockStructure(field);

        final Record record = mockRecord(recordName, struct);
        when(record.getF8(fieldName)).thenReturn(value);

        final Collection<String> commands = RecordSetter.getSetterCommands(record);
        assertThat(commands).containsOnly(String.format("set record_name->f8_field = %f go", value));
    }

    /**
     * If the F8 field is not set to anything other than its default value (0), then no setter should be generated.
     */
    @Test
    public void testGetSetterCommandsF8Unset() {
        final String fieldName = "f8_field";
        final String recordName = "record_name";
        final Field field = mockField(fieldName, DataType.F8);

        final Structure struct = mockStructure(field);

        final Record record = mockRecord(recordName, struct);
        when(record.getF8(fieldName)).thenReturn(Double.valueOf(0));
        assertThat(RecordSetter.getSetterCommands(record)).isEmpty();
    }

    /**
     * Test the setting of an I2 value.
     */
    @Test
    public void testGetSetterCommandsI2() {
        final String fieldName = "i2_field";
        final String recordName = "record_name";
        final short value = Short.MAX_VALUE;

        final Field field = mockField(fieldName, DataType.I2);

        final Structure struct = mockStructure(field);

        final Record record = mockRecord(recordName, struct);
        when(record.getI2(fieldName)).thenReturn(value);

        final Collection<String> commands = RecordSetter.getSetterCommands(record);
        assertThat(commands).containsOnly(String.format("set record_name->i2_field = %d go", value));
    }

    /**
     * If any I2 value is not set to anything beyond its default value (0), it should not have a setter command
     * generated.
     */
    @Test
    public void testGetSetterCommandsI2Unset() {
        final String fieldName = "i2_field";
        final String recordName = "record_name";

        final Field field = mockField(fieldName, DataType.I2);

        final Structure struct = mockStructure(field);

        final Record record = mockRecord(recordName, struct);
        when(record.getI2(fieldName)).thenReturn(Short.valueOf((short) 0));
        assertThat(RecordSetter.getSetterCommands(record)).isEmpty();
    }

    /**
     * Test the setting of an I4 value.
     */
    @Test
    public void testGetSetterCommandsI4() {
        final String fieldName = "i4_field";
        final String recordName = "record_name";
        final int value = Integer.MAX_VALUE;

        final Field field = mockField(fieldName, DataType.I4);

        final Structure struct = mockStructure(field);

        final Record record = mockRecord(recordName, struct);
        when(record.getI4(fieldName)).thenReturn(value);

        final Collection<String> commands = RecordSetter.getSetterCommands(record);
        assertThat(commands).containsOnly(String.format("set record_name->i4_field = %d go", value));
    }

    /**
     * If an I4 field is not set to anything other than its default value (0), then no setter command should be
     * generated.
     */
    @Test
    public void testGetSetterCommandsI4Unset() {
        final String fieldName = "i4_field";
        final String recordName = "record_name";

        final Field field = mockField(fieldName, DataType.I4);

        final Structure struct = mockStructure(field);

        final Record record = mockRecord(recordName, struct);
        when(record.getI4(fieldName)).thenReturn(Integer.valueOf(0));
        assertThat(RecordSetter.getSetterCommands(record)).isEmpty();
    }

    /**
     * Test the setting of a VC field with no single quote characters.
     */
    @Test
    public void testGetSetterCommandsVcNoSingleQuote() {
        final String fieldName = "vc_field";
        final String recordName = "record_name";
        final String value = "no single quotes";

        final Field field = mockField(fieldName, DataType.VC);

        final Structure struct = mockStructure(field);

        final Record record = mockRecord(recordName, struct);
        when(record.getVC(fieldName)).thenReturn(value);

        final List<String> commands = RecordSetter.getSetterCommands(record);
        assertThat(commands).containsExactly("set record_name->vc_field = ", "'no single quotes'", "go");
    }

    /**
     * Test the setting of a VC value with a single quote inside of it.
     */
    @Test
    public void testGetSetterCommandsVcSingleQuote() {
        final String fieldName = "vc_field";
        final String recordName = "record_name";
        final String value = "i'm a little teapot";

        final Field field = mockField(fieldName, DataType.VC);

        final Structure struct = mockStructure(field);

        final Record record = mockRecord(recordName, struct);
        when(record.getVC(fieldName)).thenReturn(value);

        final List<String> commands = RecordSetter.getSetterCommands(record);
        assertThat(commands).containsExactly("set record_name->vc_field = ", "concat(", "'i'", ", \"'\"",
                ", 'm a little teapot'", ")", "go");
    }

    /**
     * If a VC field is not set to anything other than its default value (""), then it should not have a setter issued
     * for it.
     */
    @Test
    public void testGetSetterCommandsUnset() {
        final String fieldName = "vc_field";
        final String recordName = "record_name";

        final Field field = mockField(fieldName, DataType.VC);

        final Structure struct = mockStructure(field);

        final Record record = mockRecord(recordName, struct);
        when(record.getVC(fieldName)).thenReturn("");
        assertThat(RecordSetter.getSetterCommands(record)).isEmpty();
    }

    /**
     * Test the setting of values for a list structure.
     * <br>
     * The constructed structure is:
     *
     * <pre>
     * record record_name (
     *   1 list_name[*]
     *     2 i2_field = i2
     * )
     *
     * </pre>
     */
    @Test
    public void testGetSetterCommandsDynamicList() {
        final String fieldName = "i2_field";
        final String listName = "list_name";
        final String recordName = "record_name";
        final short firstValue = 123;
        final short secondValue = 456;

        final Field listElementField = mockField(fieldName, DataType.I2);
        final Structure listStructure = mockStructure(listElementField);
        final Field listField = mockField(listName, DataType.DYNAMIC_LIST, listStructure);
        final Structure rootStruct = mockStructure(listField);

        final Record firstElement = mockRecord(null, listStructure);
        when(firstElement.getI2(fieldName)).thenReturn(firstValue);
        final Record secondElement = mockRecord(null, listStructure);
        when(secondElement.getI2(fieldName)).thenReturn(secondValue);

        final DynamicRecordList recordList = mock(DynamicRecordList.class);
        when(recordList.iterator()).thenReturn(Arrays.asList(firstElement, secondElement).iterator());
        when(recordList.getSize()).thenReturn(Integer.valueOf(2));

        final Record rootRecord = mockRecord(recordName, rootStruct);
        when(rootRecord.getDynamicList(listName)).thenReturn(recordList);

        assertThat(RecordSetter.getSetterCommands(rootRecord)).containsExactly(
                "set stat = alterlist(record_name->list_name, 2) go",
                "set record_name->list_name[1]->i2_field = 123 go", "set record_name->list_name[2]->i2_field = 456 go");
    }

    /**
     * If a dynamic list has no child elements actually added to it, then it shouldn't be altered to any size and no
     * child elements should be populated.
     */
    @Test
    public void testGetSetterCommandsDynamicListEmpty() {
        final String fieldName = "i2_field";
        final String listName = "list_name";
        final String recordName = "record_name";

        final Field listElementField = mockField(fieldName, DataType.I2);
        final Structure listStructure = mockStructure(listElementField);
        final Field listField = mockField(listName, DataType.DYNAMIC_LIST, listStructure);
        final Structure rootStruct = mockStructure(listField);

        final DynamicRecordList recordList = mock(DynamicRecordList.class);
        when(recordList.getSize()).thenReturn(Integer.valueOf(0));

        final Record rootRecord = mockRecord(recordName, rootStruct);
        when(rootRecord.getDynamicList(listName)).thenReturn(recordList);

        assertThat(RecordSetter.getSetterCommands(rootRecord)).isEmpty();
    }

    /**
     * Test the creation of setter commands for a fixed-length list.
     * <br>
     * The constructed structure is:
     *
     * <pre>
     * record record_name (
     *   1 list_name[2]
     *     2 i2_field = i2
     * )
     *
     * </pre>
     */
    @Test
    public void testGetSetterCommandsFixedList() {
        final String fieldName = "i2_field";
        final String listName = "list_name";
        final String recordName = "record_name";
        final short firstValue = 123;
        final short secondValue = 456;

        final Field listElementField = mockField(fieldName, DataType.I2);
        final Structure listStructure = mockStructure(listElementField);
        final Field listField = mockField(listName, DataType.LIST, listStructure);
        final Structure rootStruct = mockStructure(listField);

        final Record firstElement = mockRecord(null, listStructure);
        when(firstElement.getI2(fieldName)).thenReturn(firstValue);
        final Record secondElement = mockRecord(null, listStructure);
        when(secondElement.getI2(fieldName)).thenReturn(secondValue);

        final RecordList recordList = mock(RecordList.class);
        when(recordList.iterator()).thenReturn(Arrays.asList(firstElement, secondElement).iterator());

        final Record rootRecord = mockRecord(recordName, rootStruct);
        when(rootRecord.getList(listName)).thenReturn(recordList);

        assertThat(RecordSetter.getSetterCommands(rootRecord)).containsExactly(
                "set record_name->list_name[1]->i2_field = 123 go", "set record_name->list_name[2]->i2_field = 456 go");
    }

    /**
     * Test that assignment statements for a nested record structure work.
     * <br>
     * The structure of the record structure is:
     *
     * <pre>
     * ROOT
     *   1 NESTED_RECORD
     *    2 F8_FIELD = F8 {123.456}
     * </pre>
     */
    @Test
    public void testGetSetterCommandsNestedRecord() {
        final Field dq8Field = mockField("F8_FIELD", DataType.F8);
        final Structure nestedStructure = mockStructure(dq8Field);

        final Record nestedRecord = mockRecord(null, nestedStructure);
        when(nestedRecord.getF8("F8_FIELD")).thenReturn(123.456);

        final Field recordField = mockField("NESTED_RECORD", DataType.RECORD);
        final Structure rootStructure = mockStructure(recordField);

        final Record rootRecord = mockRecord("ROOT", rootStructure);
        when(rootRecord.getRecord(recordField.getName())).thenReturn(nestedRecord);

        assertThat(RecordSetter.getSetterCommands(rootRecord))
                .containsOnly("set ROOT->NESTED_RECORD->F8_FIELD = 123.456000 go");
    }

    /**
     * Mock a fixed-length character field.
     *
     * @param fieldName
     *            The name of the field.
     * @param length
     *            The length of the character field.
     * @return A mock {@link Field} object representing a fixed-length character field.
     */
    private Field mockCharacterField(final String fieldName, final int length) {
        final Field field = mockField(fieldName, DataType.CHARACTER);
        when(field.getDataLength()).thenReturn(Long.valueOf(length));
        return field;
    }

    /**
     * Mock a field.
     *
     * @param fieldName
     *            The name of the field to be mocked.
     * @param dataType
     *            The datatype of the field to be mocked.
     * @return A mock {@link Field} object.
     */
    private Field mockField(final String fieldName, final DataType dataType) {
        final Field field = mock(Field.class);
        when(field.getType()).thenReturn(dataType);
        when(field.getName()).thenReturn(fieldName);
        return field;
    }

    /**
     * Mock a field.
     *
     * @param fieldName
     *            The name of the field to be mocked.
     * @param dataType
     *            The type of the field to be mocked.
     * @param structure
     *            The structure that backs the field.
     * @return A mock {@link Field} object.
     */
    private Field mockField(final String fieldName, final DataType dataType, final Structure structure) {
        final Field field = mockField(fieldName, dataType);
        when(field.getStructure()).thenReturn(structure);
        return field;
    }

    /**
     * Mock a record.
     *
     * @param recordName
     *            The name of the record.
     * @param structure
     *            The structure backing the record.
     * @return A mock {@link Record} object.
     */
    private Record mockRecord(final String recordName, final Structure structure) {
        final Record record = mock(Record.class);
        when(record.getName()).thenReturn(recordName);
        when(record.getStructure()).thenReturn(structure);
        return record;
    }

    /**
     * Mock a structure.
     *
     * @param fields
     *            The fields to be contained within the created structure.
     * @return A mock {@link Structure} object.
     */
    private Structure mockStructure(final Field... fields) {
        final Structure struct = mock(Structure.class);
        when(struct.getFields()).thenReturn(Arrays.asList(fields));
        return struct;
    }

}