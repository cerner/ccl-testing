package com.cerner.ccl.parser.data.record;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cerner.ccl.parser.AbstractBeanUnitTest;
import com.cerner.ccl.parser.data.CodeSet;
import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.EnumeratedValue;

/**
 * Unit tests for {@link RecordStructureField}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class RecordStructureFieldTest extends AbstractBeanUnitTest<RecordStructureField> {
    private final String name = "field_Name";
    private final String description = "i am the description";
    private final DataType dataType = DataType.DQ8;
    private final int level = 35678;
    @Mock
    private CodeSet codeSet;
    @Mock
    private EnumeratedValue value;
    private List<CodeSet> codeSets;
    private List<EnumeratedValue> values;
    private RecordStructureField field;

    /**
     * Set up the values and code sets for each test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        values = Collections.singletonList(value);
        codeSets = Collections.singletonList(codeSet);
        field = new RecordStructureField(name, level, dataType, false, description, codeSets, values);
    }

    /**
     * Test the construction of a non-fixed-length record structure field.
     */
    @Test
    public void testConstruct() {
        assertThat(field.getName()).isEqualTo(name);
        assertThat(field.getDataType()).isEqualTo(DataType.DQ8);
        assertThat(field.isOptional()).isFalse();
        assertThat(field.getDescription()).isEqualTo(description);
        assertThat(field.getCodeSets()).isEqualTo(codeSets);
        assertThat(field.getValues()).isEqualTo(values);
    }

    /**
     * Test the construction of a non-fixed-length record structure field with no documentation.
     */
    @Test
    public void testConstructNoDocumentation() {
        final RecordStructureField field = new RecordStructureField(name, 1, DataType.F8);
        assertThat(field.getName()).isEqualTo(name);
        assertThat(field.getDataType()).isEqualTo(DataType.F8);
        assertThat(field.getDescription()).isEmpty();
        assertThat(field.getCodeSets()).isEmpty();
        assertThat(field.getValues()).isEmpty();
    }

    /**
     * Construction of a non-fixed-length-character field with a {@code null} name should fail.
     */
    @Test
    public void testConstructNullName() {
        expect(IllegalArgumentException.class);
        expect("Name cannot be null.");
        new RecordStructureField(null, 1, DataType.DQ8);
    }

    /**
     * Construction with a zero level should fail.
     */
    @Test
    public void testConstructZeroLevel() {
        expect(IllegalArgumentException.class);
        expect("Level cannot be less than 1: " + Integer.toString(0));
        new RecordStructureField(name, 0, dataType);
    }

    /**
     * Two fields with different names (that are not merely case-insensitive equivalents) should be unequal.
     */
    @Test
    public void testEqualsDifferentName() {
        final RecordStructureField field = new RecordStructureField(name, 1, DataType.DQ8);
        final RecordStructureField other = new RecordStructureField(StringUtils.reverse(name), 1, DataType.DQ8);
        assertThat(field).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(field);
    }

    /**
     * Two fields with the same name (just different casing) should be equal.
     */
    @Test
    public void testEqualsNameCaseInsensitive() {
        final RecordStructureField field = new RecordStructureField(name, 1, DataType.DQ8);
        final RecordStructureField other = new RecordStructureField(StringUtils.swapCase(name), 1, DataType.DQ8);
        assertThat(field).isEqualTo(other);
        assertThat(other).isEqualTo(field);
        assertThat(field.hashCode()).isEqualTo(other.hashCode());
    }

    /**
     * Test the retrieval of the level.
     */
    @Test
    public void testGetLevel() {
        assertThat(field.getLevel()).isEqualTo(level);
    }

    @Override
    protected RecordStructureField getBean() {
        return new RecordStructureField(name, 1, DataType.DQ8, true, description, codeSets, values);
    }

    @Override
    protected RecordStructureField newBeanFrom(final RecordStructureField otherBean) {
        return new RecordStructureField(name, otherBean.getLevel(), otherBean.getDataType(), otherBean.isOptional(),
                otherBean.getDescription(), otherBean.getCodeSets(), otherBean.getValues());
    }

}
