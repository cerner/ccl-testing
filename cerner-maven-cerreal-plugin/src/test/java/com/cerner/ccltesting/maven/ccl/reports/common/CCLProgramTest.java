package com.cerner.ccltesting.maven.ccl.reports.common;

import static com.cerner.ccltesting.maven.ccl.reports.common.internal.XmlGenerator.INC_END;
import static com.cerner.ccltesting.maven.ccl.reports.common.internal.XmlGenerator.INC_START;
import static com.cerner.ccltesting.maven.ccl.reports.common.internal.XmlGenerator.createListingXml;
import static org.fest.assertions.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.cerner.ccltesting.maven.ccl.reports.common.CCLProgram.ProgramLine;
import com.cerner.ccltesting.xsl.XslAPI;

/**
 * Unit tests for {@link CCLProgram}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { XslAPI.class })
public class CCLProgramTest {
    /**
     * A {@link Rule} used to retrieve the current test name.
     */
    @Rule
    public TestName testName = new TestName();
    /**
     * A {@link Rule} used to test for thrown exceptions. This <b>must</b> appear last of all rules in order to ensure it can catch any exceptions (instead of letting another rule catch and mishandle
     * the expected exception).
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    /**
     * Constructing a program with a {@code null} listing XML should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullListingXml() throws Exception {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Listing XML cannot be null.");
        new CCLProgram(null);
    }

    /**
     * Test the retrieval of the listing XML.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetListingXml() throws Exception {
        final String listingXml = createListingXml(testName.getMethodName(), Collections.singleton("call echo('test')"));
        assertThat(new CCLProgram(listingXml).getListingXML()).isEqualTo(listingXml);
    }

    /**
     * Test the retrieval of the listing {@link Document} object.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetListingDom() throws Exception {
        final String listingXml = "i am the listing XML";
        final Document document = mock(Document.class);
        final NodeList nodes = mock(NodeList.class);

        mockStatic(XslAPI.class);
        when(XslAPI.getDocumentFromString(listingXml)).thenReturn(document);
        when(XslAPI.getXPathNodeList(document, "/LISTING/LINES/LINE")).thenReturn(nodes);

        assertThat(new CCLProgram(listingXml).getListingDOM()).isEqualTo(document);
    }

    /**
     * Test the parsing of the name from the listing XML.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetName() throws Exception {
        final String listingName = testName.getMethodName();
        final String listingXml = createListingXml(listingName, Collections.singleton("set i = 2"));

        assertThat(new CCLProgram(listingXml).getName()).isEqualTo(listingName);
    }

    /**
     * Test the parsing of program lines.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetProgramLines() throws Exception {
        final List<String> lines = Arrays.asList("a", "b", "c");
        final CCLProgram program = new CCLProgram(createListingXml(testName.getMethodName(), lines));
        final List<ProgramLine> programLines = program.getProgramLines();
        assertThat(programLines).hasSize(lines.size());
        for (int i = 0; i < lines.size(); i++) {
            final ProgramLine programLine = programLines.get(i);
            assertThat(programLine.getLineNumber()).isEqualTo(i + 1);
            assertThat(programLine.getOrigin()).as("Incorrect origin for index " + i).isEqualTo("PROGRAM");
            assertThat(programLine.getSourceCode()).isEqualTo(lines.get(i));
        }
    }

    /**
     * Test the parsing of program lines when it contains an include file.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetProgramLinesWithInclude() throws Exception {
        final String includeStatement = "%i cclsource:test.inc";
        final List<String> source = Arrays.asList("call echo('not nested before')", INC_START + includeStatement, "call echo('nested in include')", INC_END + includeStatement,
                "call echo('not nested after')");
        final String listingXml = createListingXml(testName.getMethodName(), source);
        final CCLProgram program = new CCLProgram(listingXml);
        final List<ProgramLine> programLines = program.getProgramLines();

        assertThat(programLines).hasSize(3);

        final ProgramLine notNestedBefore = programLines.get(0);
        assertThat(notNestedBefore.getLineNumber()).isEqualTo(1);
        assertThat(notNestedBefore.getOrigin()).isEqualTo("PROGRAM");
        assertThat(notNestedBefore.getSourceCode()).isEqualTo("call echo('not nested before')");

        final ProgramLine nested = programLines.get(1);
        assertThat(nested.getLineNumber()).isEqualTo(3);
        assertThat(nested.getOrigin()).isEqualTo(includeStatement);
        assertThat(nested.getSourceCode()).isEqualTo("call echo('nested in include')");

        final ProgramLine notNestedAfter = programLines.get(2);
        assertThat(notNestedAfter.getLineNumber()).isEqualTo(5);
        assertThat(notNestedAfter.getOrigin()).isEqualTo("PROGRAM");
        assertThat(notNestedAfter.getSourceCode()).isEqualTo("call echo('not nested after')");
    }

    /**
     * Test the retrieval of a specific line of code.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetSourceCodeAtLine() throws Exception {
        final List<String> lines = Arrays.asList("x", "y", "z");
        final CCLProgram program = new CCLProgram(createListingXml(testName.getMethodName(), lines));
        for (int i = 0; i < lines.size(); i++)
            assertThat(program.getSourceCodeAtLine(i + 1)).as("Incorrect line at index " + i).isEqualTo(lines.get(i));
    }

    /**
     * If the given line number is not found, then a blank string should be returned.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetSourceCodeAtLineNotFound() throws Exception {
        final List<String> lines = Arrays.asList("x", "y", "z");
        final CCLProgram program = new CCLProgram(createListingXml(testName.getMethodName(), lines));
        assertThat(program.getSourceCodeAtLine(lines.size() + 1)).isEmpty();
    }
}
