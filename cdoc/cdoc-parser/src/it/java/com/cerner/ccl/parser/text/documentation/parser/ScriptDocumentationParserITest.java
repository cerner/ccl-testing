package com.cerner.ccl.parser.text.documentation.parser;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;

import com.cerner.ccl.parser.AbstractFileReaderITest;
import com.cerner.ccl.parser.TimingsWriter;
import com.cerner.ccl.parser.data.ScriptArgument;
import com.cerner.ccl.parser.data.ScriptDocumentation;

import etm.core.monitor.EtmMonitor;

/**
 * Integration tests for {@link ScriptDocumentationParser}.
 * 
 * @author Joshua Hyde
 * 
 */

public class ScriptDocumentationParserITest extends AbstractFileReaderITest {
    private static final EtmMonitor monitor = TimingsWriter.startMonitor();
    private final ScriptDocumentationParser parser = new ScriptDocumentationParser();

    /**
     * Write out the results of all of the test runs.
     * 
     * @throws Exception
     *             If any errors occur during the write-out.
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (monitor != null) {
            monitor.stop();

            TimingsWriter.writeTimings(monitor, ScriptDocumentationParserITest.class);
        }
    }

    /**
     * Test the parsing of a script's documentation.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParse() throws Exception {
        final ScriptDocumentation doc = parser.parse(0, readResource("script_documentation.txt"));
        assertThat(doc.getDescription()).isEqualTo("This is the top-level documentation.");
        assertThat(doc.getBoundTransaction()).isEqualTo(12117);

        final List<ScriptArgument> arguments = doc.getScriptArguments();
        assertThat(arguments).hasSize(3);
        assertThat(arguments.get(0).getDescription()).isEqualTo("First argument");
        assertThat(arguments.get(1).getDescription()).isEqualTo("Second argument");
        assertThat(arguments.get(2).getDescription()).isEqualTo("Third argument");
    }

    /**
     * Test the parsing of top-level script documentation with a {@code <pre />} tag.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParsePreTag() throws Exception {
        final ScriptDocumentation doc = parser.parse(0, readResource("pre_tag.txt"));
        final String lineSeparator = System.getProperty("line.separator");
        assertThat(doc.getDescription()).isEqualTo(
                "Transform a list of source code text into an XML format. The XML will be formatted as follows:<pre>" + lineSeparator + "    &lt;LISTING&gt;" + lineSeparator
                        + "        &lt;LISTING_NAME&gt;program name&lt;/LISTING_NAME&gt;" + lineSeparator + "    &lt;/LISTING&gt;" + lineSeparator + "</pre>");
    }
}
