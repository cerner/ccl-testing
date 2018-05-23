package com.cerner.ccl.parser.text.documentation.parser;

import org.junit.Test;

import com.cerner.ccl.parser.AbstractUnitTest;

/**
 * Unit tests for {@link DocumentationParser}.
 * 
 * @author Joshua Hyde
 * 
 */

public class DocumentationParserTest extends AbstractUnitTest {
    private final DocumentationParser parser = new DocumentationParser();

    /**
     * Testing a {@code null} line for parseability should fail.
     */
    @Test
    public void testCanParseNullLine() {
        expect(IllegalArgumentException.class);
        expect("Line cannot be null.");
        parser.canParse(null);
    }
}
