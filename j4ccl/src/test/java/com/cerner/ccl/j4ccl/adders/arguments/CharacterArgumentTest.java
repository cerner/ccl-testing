package com.cerner.ccl.j4ccl.adders.arguments;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

/**
 * Unit tests for {@link CharacterArgument}.
 *
 * @author Joshua Hyde
 *
 */

public class CharacterArgumentTest {
    /**
     * Construction with a {@code null} value should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullValue() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new CharacterArgument(null);
        });
        assertThat(e.getMessage()).isEqualTo("Value cannot be null.");
    }

    /**
     * If the given value contains both single- and double-quote characters, construction should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructBadQuotationMarks() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new CharacterArgument("'\"");
        });
        assertThat(e.getMessage())
                .isEqualTo("This does not support strings containing both single quote and double quotation marks.");
    }

    /**
     * Test the conversion of the given value to a string with double quotations.
     */
    @Test
    public void testGetCommandLineValueDoubleQuote() {
        assertThat(new CharacterArgument("\"this is a test\"").getCommandLineValue()).isEqualTo("'\"this is a test\"'");
    }

    /**
     * Test the conversion of the given value to a string with double quotations.
     */
    @Test
    public void testGetCommandLineValueSingleQuote() {
        assertThat(new CharacterArgument("'this is a test'").getCommandLineValue()).isEqualTo("\"'this is a test'\"");
    }
}
