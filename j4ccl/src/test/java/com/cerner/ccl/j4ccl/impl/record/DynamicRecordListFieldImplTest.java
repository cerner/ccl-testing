package com.cerner.ccl.j4ccl.impl.record;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * Unit test of {@link DynamicRecordListFieldImpl}.
 *
 * @author Joshua Hyde
 *
 */

public class DynamicRecordListFieldImplTest {
    private static final String LIST_NAME = "list";
    private static final Structure STRUCTURE = mock(Structure.class);
    private DynamicRecordListFieldImpl field;

    /**
     * Create a new field for each test.
     */
    @Before
    public void setUp() {
        field = new DynamicRecordListFieldImpl(LIST_NAME, STRUCTURE);
    }

    /**
     * Test that constructing with a blank name fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionBlankName() {
        new DynamicRecordListFieldImpl("  ", STRUCTURE);
    }

    /**
     * Test that constructing with a null name fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void testConstructionNullName() {
        new DynamicRecordListFieldImpl(null, STRUCTURE);
    }

    /**
     * Test that constructing with a null structure fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void testConstructionNullStructure() {
        new DynamicRecordListFieldImpl(LIST_NAME, null);
    }

    /**
     * Verify that getting the data length fails.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetDataLength() {
        field.getDataLength();
    }

    /**
     * Test the construction of a declaration.
     */
    @Test
    public void testGetDeclaration() {
        assertThat(field.getDeclaration()).isEqualTo(String.format("%s [*]", LIST_NAME));
    }

    /**
     * Test that fetch the field's list size fails.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetListSize() {
        field.getListSize();
    }

    /**
     * Test the fetching of the name.
     */
    @Test
    public void testGetName() {
        assertThat(field.getName()).isEqualTo(LIST_NAME);
    }

    /**
     * Test the fetching of the structure.
     */
    @Test
    public void testGetStructure() {
        assertThat(field.getStructure()).isSameAs(STRUCTURE);
    }

    /**
     * Test that the correct data type is returned.
     */
    @Test
    public void testGetType() {
        assertThat(field.getType()).isEqualTo(DataType.DYNAMIC_LIST);
    }

    /**
     * Test the string representation of the structure.
     */
    @Test
    public void testToString() {
        assertThat(field.toString()).isEqualTo(String.format("%s [%s]", LIST_NAME, DataType.DYNAMIC_LIST.toString()));
    }

}
