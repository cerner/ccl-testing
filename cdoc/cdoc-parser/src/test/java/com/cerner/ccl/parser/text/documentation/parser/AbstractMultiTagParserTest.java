package com.cerner.ccl.parser.text.documentation.parser;

import static org.fest.assertions.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.parser.text.smoosh.DocumentationTagIndexedSmoosher;

/**
 * Unit tests for {@link AbstractMultiTagParser}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { AbstractMultiTagParser.class, DocumentationTagIndexedSmoosher.class })
public class AbstractMultiTagParserTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();
    @Mock
    private DocumentationTagIndexedSmoosher smoosher;
    private ConcreteParser parser;

    /**
     * Set up the parser for each test.
     *
     * @throws Exception
     *             If any errors occur during the setup.
     */
    @Before
    public void setUp() throws Exception {
        whenNew(DocumentationTagIndexedSmoosher.class).withNoArguments().thenReturn(smoosher);
        parser = new ConcreteParser();
    }

    /**
     * Test the parsing of objects.
     */
    @Test
    public void testParse() {
        // parse:skipped will be skipped because the smoosher will indicate it moved onto the next line
        final List<String> lines = Arrays.asList("parse:one", "parse:two", "parse:skipped", "not_parseable",
                "parse:three");
        int listIdx = -1;
        for (final String line : lines) {
            listIdx++;
            if (line.startsWith("parse:")) {
                final String parsed = line + "-parsed";
                when(smoosher.smoosh(listIdx, lines)).thenReturn(parsed);
                parser.addParseable(parsed, parsed + "-product");
            }
        }

        when(smoosher.getEndingIndex()).thenReturn(Integer.valueOf(0), Integer.valueOf(3), Integer.valueOf(3),
                Integer.valueOf(4));

        final List<Object> parsed = parser.parse(lines);
        assertThat(parsed).containsExactly("parse:one-parsed-product", "parse:two-parsed-product",
                "parse:three-parsed-product");
    }

    /**
     * Parsing {@code null} documentation should fail.
     */
    @Test
    public void testParseNullDocumentation() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Documentation list cannot be null.");
        parser.parse(null);
    }

    /**
     * A concrete implementation of {@link AbstractMultiTagParser} for testing purposes.
     *
     * @author Joshua Hyde
     *
     */
    private static class ConcreteParser extends AbstractMultiTagParser<Object> {
        private final Map<String, Object> parseables = new HashMap<String, Object>();

        ConcreteParser() {
        }

        /**
         * Add a parsing product for a given line.
         *
         * @param line
         *            The line that is to be treated as the source of the product.
         * @param product
         *            The product of the parsing.
         */
        public void addParseable(final String line, final Object product) {
            parseables.put(line, product);
        }

        @Override
        protected boolean isParseable(final String line) {
            return line.startsWith("parse:");
        }

        @Override
        protected Object parseElement(final String line) {
            if (!parseables.containsKey(line)) {
                throw new IllegalArgumentException("Parseable not found for line: " + line);
            }
            return parseables.get(line);
        }
    }
}
