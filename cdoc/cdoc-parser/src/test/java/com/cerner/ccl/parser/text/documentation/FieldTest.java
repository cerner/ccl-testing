package com.cerner.ccl.parser.text.documentation;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cerner.ccl.parser.AbstractBeanUnitTest;
import com.cerner.ccl.parser.data.CodeSet;
import com.cerner.ccl.parser.data.EnumeratedValue;

/**
 * Unit tests for {@link Field}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class FieldTest extends AbstractBeanUnitTest<Field> {
    private final String name = "a_field";
    private final String description = "a description";
    @Mock
    private EnumeratedValue value;
    @Mock
    private CodeSet codeSet;
    private List<EnumeratedValue> values;
    private List<CodeSet> codeSets;
    private Field field;

    /**
     * Set up the field for each test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        values = Collections.singletonList(value);
        codeSets = Collections.singletonList(codeSet);
        field = new Field(name, description, false, values, codeSets);
    }

    /**
     * Construction of a field with a {@code null} name should fail.
     */
    @Test
    public void testConstructNullName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new Field(null, description, true, values, codeSets);
        });
        assertThat(e.getMessage()).isEqualTo("Name cannot be null.");
    }

    /**
     * Two fields with different code sets should be inequal.
     */
    @Test
    public void testEqualsDifferentCodeSets() {
        final Field other = new Field(name, description, field.isOptional(), values, Collections.<CodeSet> emptyList());
        assertThat(field).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(field);
    }

    /**
     * Two fields with different names should be inequal.
     */
    @Test
    public void testEqualsDifferentName() {
        final Field other = new Field(StringUtils.reverse(name), description, field.isOptional(), values, codeSets);
        assertThat(field).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(field);
    }

    /**
     * Two fields with different optionality should be inequal.
     */
    @Test
    public void testEqualsDifferentOptionality() {
        final Field other = new Field(name, description, !field.isOptional(), values, codeSets);
        assertThat(other).isNotEqualTo(field);
        assertThat(field).isNotEqualTo(other);
    }

    /**
     * Two fields with different enumerated values should be inequal.
     */
    @Test
    public void testEqualsDifferentValues() {
        final Field other = new Field(name, description, field.isOptional(), Collections.<EnumeratedValue> emptyList(),
                codeSets);
        assertThat(other).isNotEqualTo(field);
        assertThat(field).isNotEqualTo(other);
    }

    /**
     * The comparison of names should be case-insensitive.
     */
    @Test
    public void testEqualsNameCaseInsensitive() {
        final Field other = new Field(StringUtils.swapCase(name), description, field.isOptional(), field.getValues(),
                field.getCodeSets());
        assertThat(other).isEqualTo(field);
        assertThat(field).isEqualTo(other);
        assertThat(other.hashCode()).isEqualTo(field.hashCode());
    }

    /**
     * Test the retrieval of the description.
     */
    @Test
    public void testGetDescription() {
        assertThat(field.getDescription()).isEqualTo(description);
    }

    /**
     * Test the retrieval of code sets.
     */
    @Test
    public void testGetCodeSets() {
        assertThat(field.getCodeSets()).isEqualTo(codeSets);
    }

    /**
     * Test the retrieval of the name.
     */
    @Test
    public void testGetName() {
        assertThat(field.getName()).isEqualTo(name);
    }

    /**
     * Test the retrieval of the values.
     */
    @Test
    public void testGetValues() {
        assertThat(field.getValues()).isEqualTo(values);
    }

    /**
     * Test the retrieval of the optionality of the field.
     */
    @Test
    public void testIsOptional() {
        assertThat(field.isOptional()).isFalse();
    }

    @Override
    protected Field getBean() {
        return field;
    }

    @Override
    protected Field newBeanFrom(final Field otherBean) {
        return new Field(otherBean.getName(), otherBean.getDescription(), otherBean.isOptional(), otherBean.getValues(),
                otherBean.getCodeSets());
    }

}
