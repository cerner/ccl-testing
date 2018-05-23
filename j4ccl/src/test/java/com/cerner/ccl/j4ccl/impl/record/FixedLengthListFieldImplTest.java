package com.cerner.ccl.j4ccl.impl.record;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * Unit test of {@link FixedLengthListFieldImpl}.
 *
 * @author Joshua Hyde
 *
 */

public class FixedLengthListFieldImplTest {
    private static final String LIST_NAME = "list";
    private static final Structure STRUCTURE = mock(Structure.class);
    private static final int LIST_SIZE = 2;
    private FixedLengthListFieldImpl field;

    /**
     * Create a new field object for each test.
     */
    @Before
    public void setUp() {
        field = new FixedLengthListFieldImpl(LIST_NAME, STRUCTURE, LIST_SIZE);
    }

    /**
     * Verify that constructing with a negative list size fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testConstructionBadListSize() {
        new FixedLengthListFieldImpl(LIST_NAME, STRUCTURE, -1);
    }

    /**
     * Verify that constructing with an empty list is okay.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructionEmptyList() {
        new FixedLengthListFieldImpl(LIST_NAME, STRUCTURE, 0);
    }

    /**
     * Test that constructing with a blank name fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionBlankName() {
        new FixedLengthListFieldImpl("  ", STRUCTURE, LIST_SIZE);
    }

    /**
     * Test that constructing with a null name fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void testConstructionNullName() {
        new FixedLengthListFieldImpl(null, STRUCTURE, LIST_SIZE);
    }

    /**
     * Test that constructing with a null structure fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void testConstructionNullStructure() {
        new FixedLengthListFieldImpl(LIST_NAME, null, LIST_SIZE);
    }

    /**
     * Verify that getting the data length fails.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetDataLength() {
        field.getDataLength();
    }

    /**
     * Verify that the correct declaration is returned.
     */
    @Test
    public void testGetDeclaration() {
        assertThat(field.getDeclaration()).isEqualTo(String.format("%s [%d]", LIST_NAME, LIST_SIZE));
    }

    /**
     * Test that the list size is properly returned.
     */
    @Test
    public void testGetListSize() {
        assertThat(field.getListSize()).isEqualTo(LIST_SIZE);
    }

    /**
     * Test getting the name.
     */
    @Test
    public void testGetName() {
        assertThat(field.getName()).isEqualTo(LIST_NAME);
    }

    /**
     * Test getting the structure.
     */
    @Test
    public void testGetStructure() {
        assertThat(field.getStructure()).isEqualTo(STRUCTURE);
    }

    /**
     * Test that the correct data type is returned.
     */
    @Test
    public void testGetType() {
        assertThat(field.getType()).isEqualTo(DataType.LIST);
    }

    /**
     * Test {@link FixedLengthListFieldImpl#toString()}.
     */
    @Test
    public void testToString() {
        assertThat(field.toString()).isEqualTo(String.format("%s [%s]", LIST_NAME, DataType.LIST.toString()));
    }

    /**
     * Exercises the happy path
     */
    @Test
    public void testHappyPath() {
        assertThat(field.getName()).isEqualTo(LIST_NAME);
        assertThat(field.getStructure()).isEqualTo(STRUCTURE);
        assertThat(field.getListSize()).isEqualTo(LIST_SIZE);
        assertThat(field.getDeclaration()).isEqualTo("list [2]");
    }
}
