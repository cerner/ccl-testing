package com.cerner.ccl.j4ccl.impl.util;

import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;

import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.DynamicRecordList;
import com.cerner.ccl.j4ccl.record.Field;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.RecordList;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * A utility to put values from an external source into a {@link Record} object.
 *
 * @author Joshua Hyde
 *
 */

public class RecordWriter {
    private static final Pattern jsonDatePattern;

    static {
        jsonDatePattern = Pattern.compile("^\\/Date\\(.*\\)\\/$");
    }

    /**
     * Extract record structure data from JSON into a {@link Record} data object.
     *
     * @param json
     *            JSON representing a record structure and the data stored within it.
     * @param record
     *            A {@link Record} object into which the values are to be stored.
     * @throws IllegalArgumentException
     *             If the given record structure is not in the given JSON text.
     */
    public static void putFromJson(final String json, final Record record) {
        final JSONObject jsonObject = JSONObject.fromObject(json);
        // JSON data from CCL is presumed to be all upper-case
        final String recordNameUpper = record.getName().toUpperCase(Locale.getDefault());
        if (!jsonObject.has(recordNameUpper))
            throw new IllegalArgumentException("JSON does not contain record " + recordNameUpper);

        putFromJsonObject(jsonObject.getJSONObject(recordNameUpper), record);
    }

    /**
     * Populate a record structure from the contents of a JSON data object.
     *
     * @param json
     *            The {@link JSONObject} from which data will be pulled.
     * @param record
     *            A {@link Record} object into which the values in the JSON object will be populated.
     * @throws IllegalArgumentException
     *             If the data type of a field within the given record identifies itself as a complext type but is not a
     *             known complex type.
     */
    private static void putFromJsonObject(final JSONObject json, final Record record) {
        for (final Field field : record.getStructure().getFields()) {
            final String fieldName = field.getName().toUpperCase(Locale.getDefault());
            final DataType dataType = field.getType();
            // If it's not a primitive...
            if (dataType.isComplexType()) {
                switch (dataType) {
                case LIST:
                    putFixedListFromJson((JSON) json.get(fieldName), field, record.getList(fieldName));
                    break;
                case DYNAMIC_LIST:
                    putVariableListFromJson(json.getJSONArray(fieldName), record.getDynamicList(fieldName));
                    break;
                case RECORD:
                    putFromJsonObject(json.getJSONObject(fieldName), record.getRecord(fieldName));
                    break;
                default:
                    throw new IllegalArgumentException("Unrecognized complex data type: " + dataType);
                }
            } else
                putPrimitiveFromJson(json, field, record);
        }
    }

    /**
     * Populate a fixed-length list from JSON data objects.
     *
     * @param sourceJson
     *            A {@link JSON} object representing an array of JSON data objects or a single JSON data object from
     *            which data will be pulled.
     * @param field
     *            A {@link Field} object representing the fixed-length list to be populated.
     * @param recordList
     *            A {@link RecordList} containing records into which values will be populated.
     */
    private static void putFixedListFromJson(final JSON sourceJson, final Field field, final RecordList recordList) {
        if (sourceJson.isArray())
            putFixedListDeepFromJson((JSONArray) sourceJson, field, recordList);
        else
            putFixedListShallowFromJson((JSONObject) sourceJson, field, recordList);
    }

    /**
     * Populate a fixed-length list containing more than 1 element.
     *
     * @param array
     *            An array of JSON data objects from which list element values will be retrieved.
     * @param recordList
     *            A {@link RecordList} object containing record objects that will be populated with values from the JSON
     *            data object.
     * @throws ArrayIndexOutOfBoundsException
     *             If the size of the data object array and the size of the record list do not match.
     */
    private static void putFixedListDeepFromJson(final JSONArray array, final Field field,
            final RecordList recordList) {
        if (array.size() != recordList.getSize())
            throw new ArrayIndexOutOfBoundsException(
                    String.format("Fixed-length list %s has %d elements, but JSON has %d.",
                            field.getName().toUpperCase(Locale.getDefault()), recordList.getSize(), array.size()));

        int arrayIndex = 0;
        for (final Record record : recordList)
            putFromJsonObject(array.getJSONObject(arrayIndex++), record);
    }

    /**
     * Populate a single-element list with data from a JSON data object.
     *
     * @param json
     *            A {@link JSON} data object containing the values to be stored in the record list.
     * @param field
     *            A {@link Field} object representing the fixed-length list to be populated.
     * @param recordList
     *            A {@link RecordList} object containing a single record to be populated with values.
     * @throws ArrayIndexOutOfBoundsException
     *             If the size of the given record list is not 1.
     */
    private static void putFixedListShallowFromJson(final JSONObject json, final Field field,
            final RecordList recordList) {
        if (recordList.getSize() != 1)
            throw new ArrayIndexOutOfBoundsException(
                    "Expected one element in shallow list " + field.getName().toUpperCase(Locale.getDefault())
                            + "; found " + Integer.toString(recordList.getSize()));

        putFromJsonObject(json, recordList.get(0));
    }

    /**
     * Store a primitive value into a record structure.
     *
     * @param json
     *            A {@link JSONObject} object representing the JSON data to be stored.
     * @param field
     *            A {@link Field} object representing the field into which data is to be stored.
     * @param record
     *            A {@link Record} object into which the values are to be stored.
     * @throws NoSuchFieldError
     *             If the given JSON data object does not contain an element matching the name of the given field.
     * @throws RuntimeException
     *             If the field represents a date and that date value does not match the expected pattern.
     */
    private static void putPrimitiveFromJson(final JSONObject json, final Field field, final Record record) {
        final String fieldName = field.getName().toUpperCase(Locale.getDefault());
        if (!json.has(fieldName))
            throw new NoSuchFieldError("Expected primitive " + field.getName() + ", but did not exist in JSON object.");

        final DataType dataType = field.getType();
        switch (dataType) {
        case VC:
            record.setVC(fieldName, json.getString(fieldName));
            break;
        case F8:
            record.setF8(fieldName, json.getDouble(fieldName));
            break;
        case I4:
            record.setI4(fieldName, json.getInt(fieldName));
            break;
        case I2:
            record.setI2(fieldName, (short) json.getInt(fieldName));
            break;
        case DQ8:
            String dateStringValue = json.getString(fieldName);
            if (!jsonDatePattern.matcher(dateStringValue).matches())
                throw new RuntimeException(
                        "Date value " + dateStringValue + " does not match pattern " + jsonDatePattern.toString());

            dateStringValue = dateStringValue.substring(6, dateStringValue.length() - 2);
            if(dateStringValue.equals("0000-00-00T00:00:00.000+00:00"))
                record.setDQ8(fieldName,null);
            else
                record.setDQ8(fieldName, CclUtils.convertTimestamp(dateStringValue));
            return;
        case CHARACTER:
            record.setChar(fieldName, json.getString(fieldName));
            break;
        default:
            throw new IllegalArgumentException("Unrecognized primitive type: " + dataType);
        }
    }

    /**
     * Populate a variable-length list with data from a list of JSON data objects.
     *
     * @param array
     *            A {@link JSONArray} object from which JSON data objects with values will be pulled.
     * @param recordList
     *            A {@link DynamicRecordList} object containing records into which values from the JSON data objects
     *            will be populated.
     * @throws IllegalArgumentException
     *             If the size of the given array does not match the size of the record list.
     */
    private static void putVariableListFromJson(final JSONArray array, final DynamicRecordList recordList) {
        int listIndex = 0;
        for (final Iterator<?> it = array.iterator(); it.hasNext();) {
            final JSONObject object = (JSONObject) it.next();
            /*
             * Add a record if the size of the array exceeds the size of the list; otherwise, use an existing record
             */
            if (listIndex + 1 > recordList.getSize())
                putFromJsonObject(object, recordList.addItem());
            else
                putFromJsonObject(object, recordList.get(listIndex));
            listIndex++;
        }
    }
}
