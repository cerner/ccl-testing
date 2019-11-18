package com.cerner.ccl.j4ccl.adders.arguments;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit tests for {@link FloatArgument}.
 *
 * @author Joshua Hyde
 *
 */

public class FloatArgumentTest {
    /**
     * Verify that the string representation of a floating value has at least one decimal place to signify to CCL that
     * it is, indeed, a floating point value.
     */
    @Test
    public void testGetCommandLineValueWholeValue() {
        assertThat(new FloatArgument(10, 0).getCommandLineValue()).isEqualTo("10.0");
    }

    /**
     * Verify that, to a reasonable extent, the decimal places of a value are correctly displayed.
     */
    @Test
    public void testGetCommandLineValuePrecision() {
        assertThat(new FloatArgument(123456789123456789L, 987654321098765432L).getCommandLineValue())
                .isEqualTo("123456789123456789.987654321098765432");
    }

    /**
     * The sign of the integer should be honored as the sign of the floating point value.
     */
    @Test
    public void testGetCommandLineValueSigned() {
        assertThat(new FloatArgument(-10, 1).getCommandLineValue()).isEqualTo("-10.1");
    }
}
