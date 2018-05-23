package com.cerner.ccl.parser.text.documentation.parser;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.parser.text.documentation.Parameter;
import com.cerner.ccl.parser.text.smoosh.DocumentationTagIndexedSmoosher;

/**
 * Unit tests for {@link ParameterParser}.
 * 
 * @author Joshua Hyde
 * 
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { DocumentationTagIndexedSmoosher.class, ParameterParser.class })
public class ParameterParserTest {
    private final ParameterParser parser = new ParameterParser();

    /**
     * Test the determination of parseability of a line.
     */
    @Test
    public void testCanParse() {
        assertThat(parser.canParse("@param")).isTrue();
        assertThat(parser.canParse("@field")).isFalse();
    }

    /**
     * Test the parsing of a parameter declaration.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParse() throws Exception {
        final int startIndex = 3478;
        @SuppressWarnings("unchecked")
        final List<String> lines = mock(List.class);
        final int endingIndex = 1377;

        final String parameterName = "parameter_name";
        final String description = "This is the parameter name";
        final String smooshed = "@param " + parameterName + " " + description;

        final DocumentationTagIndexedSmoosher smoosher = mock(DocumentationTagIndexedSmoosher.class);
        when(smoosher.getEndingIndex()).thenReturn(endingIndex);
        when(smoosher.smoosh(startIndex, lines)).thenReturn(smooshed);

        whenNew(DocumentationTagIndexedSmoosher.class).withNoArguments().thenReturn(smoosher);
        final Parameter parameter = parser.parse(startIndex, lines);
        assertThat(parameter.getName()).isEqualTo(parameterName);
        assertThat(parameter.getDescription()).isEqualTo(description);
    }

    /**
     * Test the parsing of a parameter with no documentation.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseNoDocumentation() throws Exception {
        final int startIndex = 3478;
        @SuppressWarnings("unchecked")
        final List<String> lines = mock(List.class);
        final int endingIndex = 1377;

        final String parameterName = "parameter_name";
        final String smooshed = "@param " + parameterName;

        final DocumentationTagIndexedSmoosher smoosher = mock(DocumentationTagIndexedSmoosher.class);
        when(smoosher.getEndingIndex()).thenReturn(endingIndex);
        when(smoosher.smoosh(startIndex, lines)).thenReturn(smooshed);

        whenNew(DocumentationTagIndexedSmoosher.class).withNoArguments().thenReturn(smoosher);
        final Parameter parameter = parser.parse(startIndex, lines);
        assertThat(parameter.getName()).isEqualTo(parameterName);
        assertThat(parameter.getDescription()).isEmpty();
    }
}
