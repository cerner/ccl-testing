package com.cerner.ccl.parser.data.record;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;

/**
 * Unit tests for {@link RecordStructureCharacterField}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class RecordStructureCharacterFieldTest extends AbstractBeanUnitTest<RecordStructureCharacterField> {
    private final String name = "char_field";
    private final int dataLength = 1337;
    private final RecordStructureCharacterField field = new RecordStructureCharacterField(name, 1, dataLength);

    /**
     * Construction with a zero data length should fail.
     */
    @Test
    public void testConstructZeroDataLength() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new RecordStructureCharacterField(name, 1, 0);
        });
        assertThat(e.getMessage()).isEqualTo("Data length must be at least 1: " + Integer.toString(0));
    }

    /**
     * Two character fields of different lengths should be inequal.
     */
    @Test
    public void testEqualsDifferentDataLength() {
        final RecordStructureCharacterField other = new RecordStructureCharacterField(name, 1, dataLength + 1);
        assertThat(field).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(field);
    }

    /**
     * Test the retrieval of the data length.
     */
    @Test
    public void testGetDataLength() {
        assertThat(field.getDataLength()).isEqualTo(dataLength);
    }

    @Override
    protected RecordStructureCharacterField getBean() {
        return field;
    }

    @Override
    protected RecordStructureCharacterField newBeanFrom(final RecordStructureCharacterField otherBean) {
        return new RecordStructureCharacterField(otherBean.getName(), otherBean.getLevel(), otherBean.getDataLength(),
                otherBean.isOptional(), otherBean.getDescription(), otherBean.getCodeSets(), otherBean.getValues());
    }

}
