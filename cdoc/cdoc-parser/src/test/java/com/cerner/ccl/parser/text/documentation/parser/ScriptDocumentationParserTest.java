package com.cerner.ccl.parser.text.documentation.parser;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

/**
 * Unit tests for {@link ScriptDocumentationParser}.
 *
 * @author Joshua Hyde
 *
 */

public class ScriptDocumentationParserTest {
    private final ScriptDocumentationParser parser = new ScriptDocumentationParser();

    /**
     * Test that inspecting a {@code null} line for parseability fails.
     */
    @Test
    public void testCanParseNullLine() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            parser.canParse(null);
        });
        assertThat(e.getMessage()).isEqualTo("Line cannot be null.");
    }
}
