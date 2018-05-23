package com.cerner.ccl.parser.text.documentation.parser;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.cerner.ccl.parser.data.ScriptArgument;

/**
 * Unit tests for {@link ScriptArgumentParser}.
 * 
 * @author Joshua Hyde
 * 
 */

public class ScriptArgumentParserTest {
    private final ScriptArgumentParser parser = new ScriptArgumentParser();

    /**
     * Test the determination of parseability of a line.
     */
    @Test
    public void testIsParseable() {
        assertThat(parser.isParseable("@arg")).isTrue();
        assertThat(parser.isParseable("@field")).isFalse();
    }

    /**
     * Test the parsing of an argument description.
     */
    @Test
    public void testParse() {
        final String description = "This is an argument description";
        final ScriptArgument arg = parser.parseElement("@arg " + description);
        assertThat(arg.getDescription()).isEqualTo(description);
    }
}
