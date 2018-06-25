package com.cerner.ccl.j4ccl.impl.record;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.Field;

/**
 * Unit test of {@link StructureImpl}.
 *
 * @author Joshua Hyde
 *
 */

public class StructureImplTest {
    private static final Map<String, Field> FIELDS = new HashMap<String, Field>(1);
    private static final String FIELD_NAME = "field";
    private static final Field FIELD = mock(Field.class);
    private StructureImpl structure;

    /**
     * Set up the fields used in the construction of each structure.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        FIELDS.put(FIELD_NAME, FIELD);

        when(FIELD.getType()).thenReturn(DataType.DQ8);
        when(FIELD.getDeclaration()).thenReturn("declared!");
    }

    /**
     * Create a new structure for each test.
     */
    @Before
    public void setUp() {
        structure = new StructureImpl(FIELDS);
    }

    /**
     * Test that construction with a null map fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void testConstructionNullMap() {
        new StructureImpl(null);
    }

    /**
     * Test the fetching of a field.
     */
    @Test
    public void testGetField() {
        assertThat(structure.getField(FIELD_NAME.toLowerCase())).isSameAs(FIELD);
    }

    /**
     * Test that getting a non-existent field fails.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetFieldNoSuchField() {
        structure.getField("NO SUCH FIELD");
    }

    /**
     * Test that getting a null field fails.
     */
    @Test(expected = NullPointerException.class)
    public void testGetFieldNullName() {
        structure.getField(null);
    }

    /**
     * Test getting the fields.
     */
    @Test
    public void testGetFields() {
        assertThat(structure.getFields()).containsOnly(FIELD);
    }

    /**
     * Test that getting a field's type works.
     */
    @Test
    public void testGetType() {
        assertThat(structure.getType(FIELD_NAME)).isEqualTo(FIELD.getType());
    }

    /**
     * Test that getting the type for a non-existent field fails.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetTypeNoSuchField() {
        assertThat(structure.getType("NO SUCH FIELD"));
    }

    /**
     * Test that determination of having a member works.
     */
    @Test
    public void testHasMember() {
        assertThat(structure.hasMember(FIELD_NAME.toLowerCase())).isTrue();
    }

    /**
     * Test that determination of not having a member works.
     */
    @Test
    public void testNotHasMember() {
        assertThat(structure.hasMember("NO SUCH FIELD")).isFalse();
    }

    /**
     * Test that the object contributes to the builder as expected.
     */
    @Test
    public void testAddDeclaration() {
        final StringBuilder builder = new StringBuilder();
        structure.addDeclaration(builder, 1);

        final String declaration = builder.toString();
        assertThat(declaration).isEqualTo("  1 " + FIELD.getDeclaration() + "\n");
    }

    /**
     * Test the contribution of a declaration when the field represents a nested structure. <br>
     * The built record structure is:
     *
     * <pre>
     * record (
     *   1 root field declaration
     *     2 nested field declaration
     * )
     * </pre>
     */
    @Test
    public void testAddDeclarationWithNestedStructure() {
        final StringBuilder builder = new StringBuilder();

        final Field nestedField = mock(Field.class);
        when(nestedField.getType()).thenReturn(DataType.DQ8);
        when(nestedField.getDeclaration()).thenReturn("nested field declaration");

        final StructureImpl nestedStructure = mock(StructureImpl.class);
        doCallRealMethod().when(nestedStructure).addDeclaration(builder, 2);
        when(nestedStructure.getFields()).thenReturn(Arrays.asList(nestedField));

        final Field rootField = mock(Field.class);
        when(rootField.getType()).thenReturn(DataType.DYNAMIC_LIST);
        when(rootField.getStructure()).thenReturn(nestedStructure);
        when(rootField.getDeclaration()).thenReturn("root field declaration");
        when(rootField.getName()).thenReturn("root field");

        final Map<String, Field> fields = new HashMap<String, Field>(1);
        fields.put(rootField.getName(), rootField);

        final StructureImpl structure = new StructureImpl(fields);

        structure.addDeclaration(builder, 1);

        assertThat(builder.toString())
                .isEqualTo("  1 " + rootField.getDeclaration() + "\n" + "    2 " + nestedField.getDeclaration() + "\n");
    }
}
