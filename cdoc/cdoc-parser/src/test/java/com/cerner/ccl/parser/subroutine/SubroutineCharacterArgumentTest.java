package com.cerner.ccl.parser.subroutine;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;
import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.subroutine.SubroutineArgument;
import com.cerner.ccl.parser.data.subroutine.SubroutineCharacterArgument;

/**
 * Unit tests for {@link SubroutineCharacterArgument}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class SubroutineCharacterArgumentTest extends AbstractBeanUnitTest<SubroutineCharacterArgument> {
    private final String name = "a_name";
    private final int dataLength = 4783;
    private final SubroutineCharacterArgument arg = new SubroutineCharacterArgument(name, dataLength);

    /**
     * Construction with a zero data length should fail.
     */
    @Test
    public void testConstructZeroDataLength() {
        expect(IllegalArgumentException.class);
        expect("Data length must be at least 1: " + Integer.toString(0));
        new SubroutineCharacterArgument(name, 0);
    }

    /**
     * A character argument should not be equal to a non-character argument. Since {@link SubroutineArgument} does not
     * use its data type in its equality comparison, this helps ensure that, in some way, it is considered.
     */
    @Test
    public void testEqualsNonChar() {
        assertThat(arg).isNotEqualTo(new SubroutineArgument(name, DataType.F8, true, "asdf"));
    }

    /**
     * Two character arguments of different data lengths should be inequal.
     */
    @Test
    public void testEqualsDifferentDataLength() {
        final SubroutineCharacterArgument other = new SubroutineCharacterArgument(name, dataLength + 1);
        assertThat(arg).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(arg);
    }

    /**
     * Test the retrieval of the data length.
     */
    @Test
    public void testGetDataLength() {
        assertThat(arg.getDataLength()).isEqualTo(dataLength);
    }

    @Override
    protected SubroutineCharacterArgument getBean() {
        return arg;
    }

    @Override
    protected SubroutineCharacterArgument newBeanFrom(final SubroutineCharacterArgument otherBean) {
        return new SubroutineCharacterArgument(otherBean.getName(), otherBean.getDataLength(), otherBean.isByRef(),
                otherBean.getDescription());
    }

}
