package com.cerner.ccl.j4ccl.impl.record;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.cerner.ccl.j4ccl.record.DataType;

/**
 * Unit tests for {@link CharacterFieldImpl}.
 *
 * @author Joshua Hyde
 *
 */

public class CharacterFieldImplTest {
    private static final int DATA_LENGTH = 123;
    private static final String FIELD_NAME = "character_field";
    private CharacterFieldImpl field;

    /**
     * Create a new field for each test.
     */
    @Before
    public void setUp() {
        field = new CharacterFieldImpl(FIELD_NAME, DATA_LENGTH);
    }

    /**
     * Test the returned length is properly reflected.
     */
    @Test
    public void testGetDataLength() {
        assertThat(field.getDataLength()).isEqualTo(DATA_LENGTH);
    }

    /**
     * Verify that the declaration matches.
     */
    @Test
    public void testGetDeclaration() {
        assertThat(field.getDeclaration()).isEqualTo("character_field = C123");
    }

    /**
     * Verify that getting the list size fails.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetListSize() {
        field.getListSize();
    }

    /**
     * Verify the getting of the field name.
     */
    @Test
    public void testGetName() {
        assertThat(field.getName()).isEqualTo(FIELD_NAME);
    }

    /**
     * Verify that getting the structure fails.
     */
    @Test(expected = UnsupportedOperationException.class)
    public void testGetStructure() {
        field.getStructure();
    }

    /**
     * Verify that getting the data type gets the right type.
     */
    @Test
    public void testGetType() {
        assertThat(field.getType()).isEqualTo(DataType.CHARACTER);
    }

    /**
     * Verify that the toString() method returns the correct value.
     */
    @Test
    public void testToString() {
        assertThat(field.toString()).isEqualTo(String.format("%s [C%d]", field.getName(), field.getDataLength()));
    }

}
