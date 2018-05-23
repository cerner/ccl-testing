package com.cerner.ccl.parser.text.documentation.parser;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.cerner.ccl.parser.AbstractUnitTest;
import com.cerner.ccl.parser.data.EnumeratedValue;
import com.cerner.ccl.parser.exception.InvalidDocumentationException;

/**
 * Unit tests for {@link EnumeratedValueParser}.
 * 
 * @author Joshua Hyde
 * 
 */

public class EnumeratedValueParserTest extends AbstractUnitTest {
    private final EnumeratedValueParser parser = new EnumeratedValueParser();

    /**
     * Test the determination of whether or not a line is parseable.
     */
    @Test
    public void testIsParseable() {
        assertThat(parser.isParseable("@value")).isTrue();
        assertThat(parser.isParseable("@codeSet")).isFalse();
    }

    /**
     * Test the parsing of a value.
     */
    @Test
    public void testParse() {
        final int value = 1234;
        final String description = "i am the description";
        final EnumeratedValue enumerated = parser.parseElement("@value " + Integer.toString(value) + " " + description);
        assertThat(enumerated.getValue()).isEqualTo(Integer.toString(value));
        assertThat(enumerated.getDescription()).isEqualTo(description);
    }

    /**
     * Test the parsing of a value with escaped quotations.
     */
    @Test
    public void testParseEscapedQuotations() {
        final String value = "a value with \"\"escaped quotes\"\"";
        final String description = "a description of an escaped value";
        final EnumeratedValue enumerated = parser.parseElement("@value \"" + value + "\" " + description);
        assertThat(enumerated.getValue()).isEqualTo(value.replaceAll("\"\"", "\""));
        assertThat(enumerated.getDescription()).isEqualTo(description);
    }

    /**
     * Test the parsing of a value containing escaped quotes and no description.
     */
    @Test
    public void testParseEscapedQuotationsNoDocumentation() {
        final String value = "value with \"\"escaped quotes\"\" inside of it";
        final EnumeratedValue enumerated = parser.parseElement("@value \"" + value + "\"");
        assertThat(enumerated.getValue()).isEqualTo(value.replaceAll("\"\"", "\""));
        assertThat(enumerated.getDescription()).isEmpty();
    }

    /**
     * Test the parsing of a value without documentation.
     */
    @Test
    public void testParseNoDocumentation() {
        final int value = 4738;
        final EnumeratedValue enumerated = parser.parseElement("@value " + Integer.toString(value));
        assertThat(enumerated.getValue()).isEqualTo(Integer.toString(value));
        assertThat(enumerated.getDescription()).isEmpty();
    }

    /**
     * Test the parsing of a value wrapped in quotation marks.
     */
    @Test
    public void testParseQuotations() {
        final String value = "a value";
        final String description = "a description of a string value";
        final EnumeratedValue enumerated = parser.parseElement("@value \"" + value + "\" " + description);
        assertThat(enumerated.getValue()).isEqualTo(value);
        assertThat(enumerated.getDescription()).isEqualTo(description);
    }

    /**
     * If the value does not have a closing quote, then parsing it should fail.
     */
    @Test
    public void testParseQuotationsNoClosing() {
        final String line = "@value \"no closing quote\"\"";
        expect(InvalidDocumentationException.class);
        expect("Unable to find closing quotation of field value: " + line);
        parser.parseElement(line);
    }

    /**
     * Test the parsing of a quoted string value with no documentation.
     */
    @Test
    public void testParseQuotationsNoDocumentation() {
        final String value = "a value";
        final EnumeratedValue enumerated = parser.parseElement("@value \"" + value + "\"");
        assertThat(enumerated.getValue()).isEqualTo(value);
        assertThat(enumerated.getDescription()).isEmpty();
    }

    /**
     * If the documentation itself contains quotes, it should not interfere with parsing.
     */
    @Test
    public void testParseQuotationsWithQuotedDocumentation() {
        final String value = "a value with quoted documentation";
        final String description = "This is a \"description\". I hate it when people use \"air quotes\".";
        final EnumeratedValue enumerated = parser.parseElement("@value \"" + value + "\" " + description);
        assertThat(enumerated.getValue()).isEqualTo(value);
        assertThat(enumerated.getDescription()).isEqualTo(description);
    }
}
