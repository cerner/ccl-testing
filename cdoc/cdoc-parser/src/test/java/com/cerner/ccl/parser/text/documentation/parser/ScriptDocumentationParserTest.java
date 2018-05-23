package com.cerner.ccl.parser.text.documentation.parser;

import org.junit.Test;

import com.cerner.ccl.parser.AbstractUnitTest;

/**
 * Unit tests for {@link ScriptDocumentationParser}.
 * 
 * @author Joshua Hyde
 * 
 */

public class ScriptDocumentationParserTest extends AbstractUnitTest {
    private final ScriptDocumentationParser parser = new ScriptDocumentationParser();

    /**
     * Test that inspecting a {@code null} line for parseability fails.
     */
    @Test
    public void testCanParseNullLine() {
        expect(IllegalArgumentException.class);
        expect("Line cannot be null.");
        parser.canParse(null);
    }
}
