package com.cerner.ccl.parser.text.subroutine;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.cerner.ccl.parser.AbstractBeanUnitTest;

/**
 * Unit tests for {@link SubroutineCharacterArgumentDeclaration}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class SubroutineCharacterArgumentDeclarationTest
        extends AbstractBeanUnitTest<SubroutineCharacterArgumentDeclaration> {
    private final String name = "char_arg";
    private final int dataLength = 57;
    private final SubroutineCharacterArgumentDeclaration decl = new SubroutineCharacterArgumentDeclaration(name,
            dataLength, false);

    /**
     * Construction with a zero data length should fail.
     */
    @Test
    public void testConstructZeroDataLength() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new SubroutineCharacterArgumentDeclaration(name, 0, false);
        });
        assertThat(e.getMessage()).isEqualTo("Data length cannot be less than 1: " + Integer.toString(0));
    }

    /**
     * Two arguments of different data lengths should be inequal.
     */
    @Test
    public void testEqualsDifferentDataLength() {
        final SubroutineCharacterArgumentDeclaration other = new SubroutineCharacterArgumentDeclaration(name,
                dataLength + 1, decl.isByRef());
        assertThat(other).isNotEqualTo(decl);
        assertThat(decl).isNotEqualTo(other);
    }

    /**
     * Test the retrieval of the data length.
     */
    @Test
    public void testGetDataLength() {
        assertThat(decl.getDataLength()).isEqualTo(dataLength);
    }

    @Override
    protected SubroutineCharacterArgumentDeclaration getBean() {
        return decl;
    }

    @Override
    protected SubroutineCharacterArgumentDeclaration newBeanFrom(
            final SubroutineCharacterArgumentDeclaration otherBean) {
        return new SubroutineCharacterArgumentDeclaration(otherBean.getName(), otherBean.getDataLength(),
                otherBean.isByRef());
    }

}
