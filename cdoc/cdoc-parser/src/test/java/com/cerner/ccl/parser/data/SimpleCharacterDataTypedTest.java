package com.cerner.ccl.parser.data;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;

/**
 * Unit tests for {@link SimpleCharacterDataTyped}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class SimpleCharacterDataTypedTest extends AbstractBeanUnitTest<SimpleCharacterDataTyped> {
    private final int dataLength = 4783;
    private final SimpleCharacterDataTyped dataType = new SimpleCharacterDataTyped(dataLength);

    /**
     * Construction with a zero data length should fail.
     */
    @Test
    public void testConstructZeroDataLength() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new SimpleCharacterDataTyped(0);
        });
        assertThat(e.getMessage()).isEqualTo("Data length cannot be less than 1: " + Integer.toString(0));
    }

    /**
     * Two data types of different data lengths should be inequal.
     */
    @Test
    public void testEqualsDifferentDataLengths() {
        final SimpleCharacterDataTyped other = new SimpleCharacterDataTyped(dataLength + 1);
        assertThat(dataType).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(dataType);
        assertThat(other.hashCode()).isNotEqualTo(dataType.hashCode());
    }

    /**
     * Test the retrieval of data length.
     */
    @Test
    public void testGetDataLength() {
        assertThat(dataType.getDataLength()).isEqualTo(dataLength);
    }

    @Override
    protected SimpleCharacterDataTyped getBean() {
        return dataType;
    }

    @Override
    protected SimpleCharacterDataTyped newBeanFrom(final SimpleCharacterDataTyped otherBean) {
        return new SimpleCharacterDataTyped(otherBean.getDataLength());
    }

}
