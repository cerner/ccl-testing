package com.cerner.ccl.parser.text.documentation.parser;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.parser.data.CodeSet;
import com.cerner.ccl.parser.exception.InvalidDocumentationException;

/**
 * Unit tests for {@link CodeSetParser}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { CodeSet.class, CodeSetParser.class })
public class CodeSetParserTest {
    private final CodeSetParser parser = new CodeSetParser();

    /**
     * Test the determination of parseability of a line.
     */
    @Test
    public void testIsParseable() {
        assertThat(parser.isParseable("@codeSet")).isTrue();
        assertThat(parser.isParseable("@value")).isFalse();
    }

    /**
     * Test the parsing of a code set.
     *
     * @throws Exception
     *             If any exceptions occur during the test run.
     */
    @Test
    public void testParse() throws Exception {
        final int codeSetNumber = 7848;
        final String documentation = "i am the documentation";
        final String line = "@codeSet " + Integer.toString(codeSetNumber) + " " + documentation;

        final CodeSet codeSet = mock(CodeSet.class);
        whenNew(CodeSet.class).withArguments(Integer.valueOf(codeSetNumber), documentation).thenReturn(codeSet);
        assertThat(parser.parseElement(line)).isEqualTo(codeSet);
    }

    /**
     * Test parsing when only a code set number is provided.
     *
     * @throws Exception
     *             If any exceptions occur during the test run.
     */
    @Test
    public void testParseNoDocumentation() throws Exception {
        final int codeSetNumber = 7848;
        final String line = "@codeSet " + Integer.toString(codeSetNumber);

        final CodeSet codeSet = mock(CodeSet.class);
        whenNew(CodeSet.class).withArguments(Integer.valueOf(codeSetNumber)).thenReturn(codeSet);
        assertThat(parser.parseElement(line)).isEqualTo(codeSet);
    }

    /**
     * When parsing without a space in the line should fail.
     */
    @Test
    public void testParseNoFirstSpace() {
        final String codeSet = "@codeSet237";
        InvalidDocumentationException e = assertThrows(InvalidDocumentationException.class, () -> {
            parser.parseElement(codeSet);
        });
        assertThat(e.getMessage()).isEqualTo("No space found in code set definition: " + codeSet);
    }
}
