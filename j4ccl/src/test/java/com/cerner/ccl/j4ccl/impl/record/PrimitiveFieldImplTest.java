package com.cerner.ccl.j4ccl.impl.record;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.cerner.ccl.j4ccl.record.DataType;

/**
 * Unit test for {@link PrimitiveFieldImpl}.
 *
 * @author Joshua Hyde
 *
 */

public class PrimitiveFieldImplTest {
    private static final String FIELD_NAME = "field";
    private static final DataType DATA_TYPE = DataType.DQ8;
    private PrimitiveFieldImpl field;

    /**
     * Create a new field for each test.
     */
    @Before
    public void setUp() {
        field = new PrimitiveFieldImpl(FIELD_NAME, DATA_TYPE);
    }

    /**
     * Verify that, given a bad primitive type for the primitive field, that the construction fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionBadType() {
        new PrimitiveFieldImpl(FIELD_NAME, DataType.CHARACTER);
    }

    /**
     * Test that building with a blank name fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionBlankName() {
        new PrimitiveFieldImpl("  ", DATA_TYPE);
    }

    /**
     * Test that building with a complex type fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionComplexType() {
        assertThat(DataType.DYNAMIC_LIST.isComplexType()).isTrue();
        new PrimitiveFieldImpl(FIELD_NAME, DataType.DYNAMIC_LIST);
    }

    /**
     * Test that constructing with a null name fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void testConstructionNullName() {
        new PrimitiveFieldImpl(null, DATA_TYPE);
    }

    /**
     * Test that constructing with a null data type fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void testConstructionNullType() {
        new PrimitiveFieldImpl(FIELD_NAME, null);
    }

    /**
     * Test that the size of the DQ8 field is an appropriate size.
     */
    @Test
    public void testGetDataLengthDq8() {
        assertThat(new PrimitiveFieldImpl(FIELD_NAME, DataType.DQ8).getDataLength()).isEqualTo(8);
    }

    /**
     * Test that the size of the F8 is properly returned.
     */
    @Test
    public void testGetDataLengthF8() {
        assertThat(new PrimitiveFieldImpl(FIELD_NAME, DataType.F8).getDataLength()).isEqualTo(8);
    }

    /**
     * Test that the size of the I2 is properly returned.
     */
    @Test
    public void testGetDataLengthI2() {
        assertThat(new PrimitiveFieldImpl(FIELD_NAME, DataType.I2).getDataLength()).isEqualTo(2);
    }

    /**
     * Test that the size of the I4 is properly returned.
     */
    @Test
    public void testGetDataLengthI4() {
        assertThat(new PrimitiveFieldImpl(FIELD_NAME, DataType.I4).getDataLength()).isEqualTo(4);
    }

    /**
     * Verify that trying to get the data length for a VC field fails.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetDataLengthVc() {
        new PrimitiveFieldImpl(FIELD_NAME, DataType.VC).getDataLength();
    }

    /**
     * Test the declaration generation.
     */
    @Test
    public void testGetDeclaration() {
        assertThat(field.getDeclaration()).isEqualTo(String.format("%s = %s", FIELD_NAME, DATA_TYPE.toString()));
    }

    /**
     * Test that getting the list size fails.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetListSize() {
        field.getListSize();
    }

    /**
     * Test the getting of the field name.
     */
    @Test
    public void testGetName() {
        assertThat(field.getName()).isEqualTo(FIELD_NAME);
    }

    /**
     * Test that getting the structure fails.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetStructure() {
        field.getStructure();
    }

    /**
     * Test that the type is returned.
     */
    @Test
    public void testGetType() {
        assertThat(field.getType()).isEqualTo(DATA_TYPE);
    }

    /**
     * Test the string equivalent.
     */
    @Test
    public void testToString() {
        assertThat(field.toString()).isEqualTo(String.format("%s [%s]", FIELD_NAME, DATA_TYPE.toString()));
    }
}
