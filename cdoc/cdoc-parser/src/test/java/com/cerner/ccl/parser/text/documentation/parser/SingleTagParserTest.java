package com.cerner.ccl.parser.text.documentation.parser;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.parser.AbstractUnitTest;
import com.cerner.ccl.parser.text.smoosh.DocumentationTagIndexedSmoosher;

/**
 * Unit tests for {@link SingleTagParser}.
 *
 * @author jrh3k5
 *
 */

@SuppressWarnings("unused")
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { DocumentationTagIndexedSmoosher.class, SingleTagParser.class })
public class SingleTagParserTest extends AbstractUnitTest {
    private final String tag = "@tag";
    private final SingleTagParser parser = new SingleTagParser(tag);

    /**
     * Construction with a {@code null} tag should fail.
     */
    @Test
    public void testConstructNullTag() {
        expect(IllegalArgumentException.class);
        expect("Tag cannot be null.");
        new SingleTagParser(null);
    }

    /**
     * Test the determination of the parseability of a line.
     */
    @Test
    public void testCanParse() {
        assertThat(parser.canParse(tag)).isTrue();
        assertThat(parser.canParse(StringUtils.reverse(tag))).isFalse();
    }

    /**
     * Testing a {@code null} line for parsing should fail.
     */
    @Test
    public void testCanParseNullLine() {
        expect(IllegalArgumentException.class);
        expect("Line cannot be null.");
        parser.canParse(null);
    }

    /**
     * Test the parsing of data from a tag's line.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParse() throws Exception {
        final int startingIndex = 23;
        final int endingIndex = 72;
        @SuppressWarnings("unchecked")
        final List<String> lines = mock(List.class);
        final String smooshed = tag + " smooshed";

        final DocumentationTagIndexedSmoosher smoosher = mock(DocumentationTagIndexedSmoosher.class);
        whenNew(DocumentationTagIndexedSmoosher.class).withNoArguments().thenReturn(smoosher);
        when(smoosher.getEndingIndex()).thenReturn(endingIndex);
        when(smoosher.smoosh(startingIndex, lines)).thenReturn(smooshed);

        assertThat(parser.parse(startingIndex, lines)).isEqualTo("smooshed");
        assertThat(parser.getEndingIndex()).isEqualTo(endingIndex);
    }

    /**
     * If the line to be parsed contains no spaces - i.e., it has no following documentation - then a blank line should
     * be returned.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseNoSpace() throws Exception {
        final int startingIndex = 23;
        @SuppressWarnings("unchecked")
        final List<String> lines = mock(List.class);

        final DocumentationTagIndexedSmoosher smoosher = mock(DocumentationTagIndexedSmoosher.class);
        whenNew(DocumentationTagIndexedSmoosher.class).withNoArguments().thenReturn(smoosher);
        when(smoosher.smoosh(startingIndex, lines)).thenReturn("noSpacesAtAll");

        assertThat(parser.parse(startingIndex, lines)).isEmpty();
    }
}
