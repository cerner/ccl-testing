package com.cerner.ccl.j4ccl.record;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link StructureBuilder}.
 *
 * @author Joshua Hyde
 *
 */

public class StructureBuilderTest {
    private StructureBuilder builder;

    /**
     * Get a new builder for each test.
     */
    @Before
    public void setUp() {
        builder = StructureBuilder.getBuilder();
    }

    /**
     * Test the addition of a fixed-length character field.
     */
    @Test
    public void testAddChar() {
        final String fieldName = "CHAR_FIELD";
        final int dataLength = Integer.MAX_VALUE;
        builder.addChar(fieldName, dataLength);

        final Structure structure = builder.build();
        validateCharacterField(structure, fieldName, dataLength);
    }

    /**
     * Verify that adding a duplicate field fails and that the uniqueness is case-insensitive.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddCharDuplicate() {
        final String fieldName = "CHAR_FIELD";
        final int dataLength = Integer.MAX_VALUE;
        builder.addChar(fieldName, dataLength).addF8(fieldName.toLowerCase(Locale.getDefault()));

    }

    /**
     * Verify that adding a DQ8 field works.
     */
    @Test
    public void testAddDQ8() {
        final String fieldName = "DQ8_FIELD";
        builder.addDQ8(fieldName);
        validateField(builder.build(), fieldName, DataType.DQ8);
    }

    /**
     * Verify that adding the same field twice fails and that the uniqueness of names is case-insensitive.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDQ8Duplicate() {
        final String fieldName = "DQ8_FIELD";
        builder.addDQ8(fieldName).addVC(fieldName.toLowerCase(Locale.getDefault()));
    }

    /**
     * Verify that a dynamic list can be added.
     */
    @Test
    public void testAddDynamicList() {
        final String fieldName = "dynamic_list";
        final Structure structure = mock(Structure.class);
        builder.addDynamicList(fieldName, structure);
        validateField(builder.build(), fieldName, DataType.DYNAMIC_LIST);
        validateSupportingStructure(builder.build(), structure, fieldName);
    }

    /**
     * Verify that adding the same field twice fails and that the uniqueness of names is case-insensitive.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddDynamicListDuplicate() {
        final String fieldName = "dynamic_list";
        final Structure structure = mock(Structure.class);
        builder.addDynamicList(fieldName, structure).addDQ8(fieldName.toLowerCase(Locale.getDefault()));
    }

    /**
     * Test the addition of an F8 field.
     */
    @Test
    public void testAddF8() {
        final String fieldName = "F8_FIELD";
        builder.addF8(fieldName);
        validateField(builder.build(), fieldName, DataType.F8);
    }

    /**
     * Verify that adding the same field twice fails and that the uniqueness of names is case-insensitive.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddF8Duplicate() {
        final String fieldName = "F8_FIELD";
        builder.addF8(fieldName).addI4(fieldName.toLowerCase(Locale.getDefault()));
    }

    /**
     * Test the addition of an I2 field.
     */
    @Test
    public void testAddI2() {
        final String fieldName = "I2_FIELD";
        builder.addI2(fieldName);
        validateField(builder.build(), fieldName, DataType.I2);
    }

    /**
     * Verify that adding the same field twice fails and that the uniqueness of names is case-insensitive.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddI2Duplicate() {
        final String fieldName = "I2_FIELD";
        builder.addI2(fieldName).addDQ8(fieldName.toLowerCase(Locale.getDefault()));
    }

    /**
     * Test the addition of an I4 field.
     */
    @Test
    public void testAddI4() {
        final String fieldName = "I4_FIELD";
        builder.addI4(fieldName);
        validateField(builder.build(), fieldName, DataType.I4);
    }

    /**
     * Verify that adding the same field twice fails and that the uniqueness of names is case-insensitive.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddI4Duplicate() {
        final String fieldName = "I4_FIELD";
        builder.addI4(fieldName).addI2(fieldName.toLowerCase(Locale.getDefault()));
    }

    /**
     * Test the addition of a fixed-length list.
     */
    @Test
    public void testAddList() {
        final Structure listStructure = mock(Structure.class);
        final String listName = "fixed_list";
        builder.addList(listName, listStructure, 2);
        validateFixedList(builder.build(), listName, 2);
    }

    /**
     * Verify that adding the same field twice fails and that the uniqueness of names is case-insensitive.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddListDuplicate() {
        final Structure listStructure = mock(Structure.class);
        final String listName = "fixed_list";
        builder.addList(listName, listStructure, 2).addF8(listName.toUpperCase(Locale.getDefault()));
    }

    /**
     * Test the addition of a nested record.
     */
    @Test
    public void testAddRecord() {
        final Structure recordStructure = mock(Structure.class);
        final String recordName = "record_name";
        builder.addRecord(recordName, recordStructure);
        validateField(builder.build(), recordName, DataType.RECORD);
    }

    /**
     * Verify that adding the same field twice fails and that the uniqueness of names is case-insensitive.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddRecordDuplicate() {
        final Structure recordStructure = mock(Structure.class);
        final String recordName = "record_name";
        builder.addRecord(recordName, recordStructure).addVC(recordName.toUpperCase(Locale.getDefault()));
    }

    /**
     * Test the construction of a status_data block. The expected structure is:
     *
     * <pre>
     * 1 status_data
     *     2 status = c1
     *     2 subeventstatus[1]
     *       3 OperationName = c25
     *       3 OperationStatus = c1
     *       3 TargetObjectName = c25
     *       3 TargetObjectValue = vc
     * </pre>
     */
    @Test
    public void testAddStatusData() {
        builder.addStatusData();

        final Structure structure = builder.build();
        validateField(structure, "status_data", DataType.RECORD);

        final Structure statusDataStructure = structure.getField("status_data").getStructure();
        validateCharacterField(statusDataStructure, "status", 1);
        validateFixedList(statusDataStructure, "subeventstatus", 1);

        final Structure subeventStructure = statusDataStructure.getField("subeventstatus").getStructure();
        validateCharacterField(subeventStructure, "operationName", 25);
        validateCharacterField(subeventStructure, "operationStatus", 1);
        validateCharacterField(subeventStructure, "targetObjectName", 25);
        validateField(subeventStructure, "targetObjectValue", DataType.VC);
    }

    /**
     * Verify that adding the same field twice fails and that the uniqueness of names is case-insensitive.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddStatusDataDuplicate() {
        builder.addStatusData().addDQ8("STATUS_DATA");
    }

    /**
     * Test the addition of a VC field.
     */
    @Test
    public void testAddVC() {
        final String fieldName = "VC_FIELD";
        builder.addVC(fieldName);
        validateField(builder.build(), fieldName, DataType.VC);
    }

    /**
     * Verify that adding the same field twice fails and that the uniqueness of names is case-insensitive.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddVcDuplicate() {
        final String fieldName = "VC_FIELD";
        builder.addVC(fieldName).addI2(fieldName.toLowerCase(Locale.getDefault()));
    }

    /**
     * Test the construction of a structure builder from an existing structure.
     */
    @Test
    public void testGetStructureBuilderExistingStructure() {
        final Structure recordStructure = StructureBuilder.getBuilder().addChar("recordChar", 1).build();
        final Structure listStructure = StructureBuilder.getBuilder().addDQ8("listDate").addI2("listI2").build();
        final Structure dynamicListStructure = StructureBuilder.getBuilder().addI4("dynamicI4").addVC("dynamicVc")
                .build();
        final Structure existingStructure = StructureBuilder.getBuilder().addF8("rootFloat")
                .addRecord("record", recordStructure).addList("list", listStructure, 3)
                .addDynamicList("dynamicList", dynamicListStructure).build();

        final Structure otherStructure = StructureBuilder.getBuilder(existingStructure).build();

        final Field otherRecordStructure = otherStructure.getField("record");
        assertThat(otherRecordStructure.getType()).isEqualTo(DataType.RECORD);

        final Field otherRecordCharField = otherRecordStructure.getStructure().getField("recordChar");
        assertThat(otherRecordCharField.getType()).isEqualTo(DataType.CHARACTER);
        assertThat(otherRecordCharField.getDataLength()).isEqualTo(1);

        final Field otherListStructure = otherStructure.getField("list");
        assertThat(otherListStructure.getType()).isEqualTo(DataType.LIST);
        assertThat(otherListStructure.getListSize()).isEqualTo(3);
        assertThat(otherListStructure.getStructure().getField("listDate").getType()).isEqualTo(DataType.DQ8);
        assertThat(otherListStructure.getStructure().getField("listI2").getType()).isEqualTo(DataType.I2);

        final Field otherRootFloat = otherStructure.getField("rootFloat");
        assertThat(otherRootFloat.getType()).isEqualTo(DataType.F8);

        final Field otherDynamicListField = otherStructure.getField("dynamicList");
        assertThat(otherDynamicListField.getType()).isEqualTo(DataType.DYNAMIC_LIST);
        assertThat(otherDynamicListField.getStructure().getField("dynamicI4").getType()).isEqualTo(DataType.I4);
        assertThat(otherDynamicListField.getStructure().getField("dynamicVc").getType()).isEqualTo(DataType.VC);
    }

    /**
     * Validate that a fixed-length character field exists in the given structure and is the appropriate size.
     *
     * @param structure
     *            The structure whose containment of a field is to be validated.
     * @param fieldName
     *            The name of the field to whose presence, type, and length is to be validated.
     * @param dataLength
     *            The expected length of the character field.
     */
    private void validateCharacterField(final Structure structure, final String fieldName, final int dataLength) {
        validateField(structure, fieldName, DataType.CHARACTER);
        assertThat(structure.getField(fieldName).getDataLength()).isEqualTo(dataLength);
    }

    /**
     * Validate that a field exists within the given structure.
     *
     * @param structure
     *            The structure whose containment of a field is to be validated.
     * @param fieldName
     *            The name of the field whose presence and type is to be validated.
     * @param expectedType
     *            The expected type of the desired field.
     */
    private void validateField(final Structure structure, final String fieldName, final DataType expectedType) {
        assertThat(structure.hasMember(fieldName)).overridingErrorMessage("Field not found: " + fieldName).isTrue();

        assertThat(structure.getType(fieldName))
                .overridingErrorMessage(String.format("Incorrect field type for field %s; expected %s and found %s",
                        fieldName, expectedType, structure.getType(fieldName)))
                .isEqualTo(expectedType);
    }

    /**
     * Validate that a fixed list exists within the given structure.
     *
     * @param structure
     *            The structure whose containment of the list is to be verified.
     * @param listName
     *            The name of the list whose presence, type, and size is to be validated.
     * @param listSize
     *            The expected size of the list.
     */
    private void validateFixedList(final Structure structure, final String listName, final int listSize) {
        validateField(structure, listName, DataType.LIST);
        assertThat(structure.getField(listName).getListSize()).isEqualTo(listSize);
    }

    /**
     * Validate that the structure backing a field matches the expected structure.
     *
     * @param container
     *            The structure that contains the field whose structure is to be verified.
     * @param fieldStructure
     *            The structure against which the field's structure is to be validated.
     * @param fieldName
     *            The name of the field whose structure is to be validated.
     */
    private void validateSupportingStructure(final Structure container, final Structure fieldStructure,
            final String fieldName) {
        assertThat(container.getField(fieldName).getStructure()).isEqualTo(fieldStructure);
    }

}
