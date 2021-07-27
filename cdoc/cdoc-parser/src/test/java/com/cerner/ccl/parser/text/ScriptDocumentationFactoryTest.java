package com.cerner.ccl.parser.text;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import com.cerner.ccl.parser.text.documentation.parser.ScriptDocumentationParser;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for {@link ScriptDocumentationFactory}.
 *
 * @author Joshua Hyde
 */
@SuppressWarnings("unused")
public class ScriptDocumentationFactoryTest {
    @Mock
    private ScriptDocumentationParser parser;
    private ScriptDocumentationFactory factory;

    /** Set up the documentation factory for each test. */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        factory = new ScriptDocumentationFactory(parser);
    }

    /** Construction with a {@code null} parser should fail. */
    @Test
    public void testConstructNullParser() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new ScriptDocumentationFactory(null);
        });
        assertThat(e.getMessage()).isEqualTo("Parser cannot be null.");
    }

    /** Test the determination of a line's parseability. */
    @Test
    public void testCanParse() {
        final List<String> source = Arrays.asList("CREATE PROGRAM TEST", "/** doc */");
        when(parser.canParse(source.get(1))).thenReturn(Boolean.TRUE);
        assertThat(factory.canParse(1, source)).isTrue();
    }

    /**
     * Test the determination of a script's documentation when there are intervening blank lines and spaces.
     */
    @Test
    public void testCanParseWithInterveningBlankLines() {
        final List<String> source = Arrays.asList("   \tCREATE PROGRAM TEST", "\t\t", "   ", "   /** ",
                " the script doc ", "   */");
        final ScriptDocumentationParser myParser = new ScriptDocumentationParser();
        final ScriptDocumentationFactory myFactory = new ScriptDocumentationFactory(myParser);

        assertThat(myFactory.canParse(3, source)).isTrue();
        assertThat(myFactory.parse(3, source).getDescription()).isEqualTo("the script doc");
    }

    /** If the given index is for the first line, then the documentation cannot be parsed. */
    @Test
    public void testCanParseFirstLine() {
        final List<String> source = Arrays.asList("CREATE PROGRAM TEST", "/** doc */");
        when(parser.canParse(source.get(0))).thenReturn(Boolean.TRUE);
        assertThat(factory.canParse(0, source)).isFalse();
    }

    /**
     * If the preceding line is not a {@code CREATE PROGRAM} line, then the documentation cannot be parsed.
     */
    @Test
    public void testCanParseNotCreateProgram() {
        final List<String> source = Arrays.asList("DROP PROGRAM TEST", "/** doc */");
        when(parser.canParse(source.get(1))).thenReturn(Boolean.TRUE);
        assertThat(factory.canParse(1, source)).isFalse();
    }

    /** If the parser cannot parse the line, then the factory cannot parse the line. */
    @Test
    public void testCanParseParserCannotParse() {
        final List<String> source = Arrays.asList("CREATE PROGRAM TEST", "/** doc */");
        when(parser.canParse(source.get(1))).thenReturn(Boolean.FALSE);
        assertThat(factory.canParse(1, source)).isFalse();
    }
}
