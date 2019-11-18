package com.cerner.ccl.parser.text.documentation.parser;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

/**
 * Unit tests for {@link DocumentationParser}.
 *
 * @author Joshua Hyde
 *
 */

public class DocumentationParserTest {
    private final DocumentationParser parser = new DocumentationParser();

    /**
     * Testing a {@code null} line for parseability should fail.
     */
    @Test
    public void testCanParseNullLine() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            parser.canParse(null);
        });
        assertThat(e.getMessage()).isEqualTo("Line cannot be null.");
    }
}
