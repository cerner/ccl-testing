package com.cerner.ccl.parser.text.record;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;

/**
 * Unit tests for {@link StructureCharacterField}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class StructureCharacterFieldTest extends AbstractBeanUnitTest<StructureCharacterField> {
    private final String fieldName = "char_field";
    private final int dataLength = 23478;
    private final StructureCharacterField field = new StructureCharacterField(fieldName, 1, dataLength);

    /**
     * Construction with a zero data length should fail.
     */
    @Test
    public void testConstructZeroLength() {
        expect(IllegalArgumentException.class);
        expect("Data length cannot be less than 1: " + Integer.toString(0));
        new StructureCharacterField(fieldName, 1, 0);
    }

    /**
     * Two fields of differing length should be inequal.
     */
    @Test
    public void testEqualsDifferentDataLength() {
        final StructureCharacterField other = new StructureCharacterField(fieldName, 1, dataLength + 1);
        assertThat(other).isNotEqualTo(field);
        assertThat(field).isNotEqualTo(other);
    }

    /**
     * Test the retrieval of the data length.
     */
    @Test
    public void testGetDataLength() {
        assertThat(field.getDataLength()).isEqualTo(dataLength);
    }

    @Override
    protected StructureCharacterField getBean() {
        return field;
    }

    @Override
    protected StructureCharacterField newBeanFrom(final StructureCharacterField otherBean) {
        return new StructureCharacterField(otherBean.getName(), otherBean.getLevel(), otherBean.getDataLength());
    }

}
