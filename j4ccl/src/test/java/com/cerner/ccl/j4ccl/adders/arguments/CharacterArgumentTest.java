package com.cerner.ccl.j4ccl.adders.arguments;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit tests for {@link CharacterArgument}.
 *
 * @author Joshua Hyde
 *
 */

public class CharacterArgumentTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    /**
     * Construction with a {@code null} value should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullValue() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Value cannot be null.");
        new CharacterArgument(null);
    }

    /**
     * If the given value contains both single- and double-quote characters, construction should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructBadQuotationMarks() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage(
                "This does not support strings containing both single quote and double quotation marks.");
        new CharacterArgument("'\"");
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
