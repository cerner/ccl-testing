package com.cerner.ccl.parser.text.record;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;
import com.cerner.ccl.parser.data.DataType;

/**
 * Unit tests for {@link StructureField}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class StructureFieldTest extends AbstractBeanUnitTest<StructureField> {
    private final String name = "field_name";
    private final DataType dataType = DataType.DQ8;
    private final int level = 47384;
    private final StructureField field = new StructureField(name, level, dataType);

    /**
     * Construction with a {@code null} name should fail.
     */
    @Test
    public void testConstructNullName() {
        expect(IllegalArgumentException.class);
        expect("Name cannot be null.");
        new StructureField(null, level, dataType);
    }

    /**
     * Construction with a level of less than 1 should fail.
     */
    @Test
    public void testConstructZeroLevel() {
        expect(IllegalArgumentException.class);
        expect("Level cannot be less than 1: " + Integer.toString(0));
        new StructureField(name, 0, dataType);
    }

    /**
     * Two fields by different names should be inequal.
     */
    @Test
    public void testEqualsDifferentName() {
        final StructureField other = new StructureField(StringUtils.reverse(name), level, dataType);
        assertThat(other).isNotEqualTo(field);
        assertThat(field).isNotEqualTo(other);
    }

    /**
     * Test the retrieval of the level.
     */
    @Test
    public void testGetLevel() {
        assertThat(field.getLevel()).isEqualTo(level);
    }

    /**
     * Test the retrieval of the field name.
     */
    @Test
    public void testGetName() {
        assertThat(field.getName()).isEqualTo(name);
    }

    @Override
    protected StructureField getBean() {
        return field;
    }

    @Override
    protected StructureField newBeanFrom(final StructureField otherBean) {
        return new StructureField(otherBean.getName(), otherBean.getLevel(), otherBean.getDataType());
    }

}
