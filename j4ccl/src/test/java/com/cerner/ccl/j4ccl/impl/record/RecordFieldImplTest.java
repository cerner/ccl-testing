package com.cerner.ccl.j4ccl.impl.record;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * Unit test of {@link RecordFieldImpl}.
 *
 * @author Joshua Hyde
 *
 */

public class RecordFieldImplTest {
    private static final String RECORD_NAME = "record";
    private static final Structure STRUCTURE = mock(Structure.class);
    private RecordFieldImpl field;

    /**
     * Create a new field for each test.
     */
    @Before
    public void setUp() {
        field = new RecordFieldImpl(RECORD_NAME, STRUCTURE);
    }

    /**
     * Test that constructing with a blank name fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionBlankName() {
        new RecordFieldImpl("  ", STRUCTURE);
    }

    /**
     * Test that constructing with a null name fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void testConstructionNullName() {
        new RecordFieldImpl(null, STRUCTURE);
    }

    /**
     * Test that constructing with a null structure fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void testConstructionNullStructure() {
        new RecordFieldImpl(RECORD_NAME, null);
    }

    /**
     * Test that getting the data length fails.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetDataLength() {
        field.getDataLength();
    }

    /**
     * Test the declaration.
     */
    @Test
    public void testGetDeclaration() {
        assertThat(field.getDeclaration()).isEqualTo(RECORD_NAME);
    }

    /**
     * Test that getting the list size fails.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetListSize() {
        field.getListSize();
    }

    /**
     * Test that getting the name works.
     */
    @Test
    public void testGetName() {
        assertThat(field.getName()).isEqualTo(RECORD_NAME);
    }

    /**
     * Test that getting the structure works.
     */
    @Test
    public void testGetStructure() {
        assertThat(field.getStructure()).isEqualTo(STRUCTURE);
    }

    /**
     * Test that getting the datatype works.
     */
    @Test
    public void testGetType() {
        assertThat(field.getType()).isEqualTo(DataType.RECORD);
    }

    /**
     * Test that the string equivalent works.
     */
    @Test
    public void testToString() {
        assertThat(field.toString()).isEqualTo(String.format("%s [%s]", RECORD_NAME, DataType.RECORD.toString()));
    }
}
