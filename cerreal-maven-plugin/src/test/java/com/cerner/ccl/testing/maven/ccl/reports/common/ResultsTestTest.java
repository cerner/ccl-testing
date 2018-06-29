package com.cerner.ccl.testing.maven.ccl.reports.common;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Node;

import com.cerner.ccl.testing.maven.ccl.reports.common.ResultsTest;
import com.cerner.ccl.testing.maven.ccl.reports.common.ResultsTest.TestResult;
import com.cerner.ccl.testing.xsl.XslAPI;

/**
 * Unit tests for {@link ResultsTest}.
 * 
 * @author Joshua Hyde
 * 
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { XslAPI.class })
public class ResultsTestTest {
    private final String testName = "a.test.name";
    private final int totalAsserts = 123;
    private final int totalErrors = 456;
    private final TestResult testResult = TestResult.PASSED;

    @Mock
    private Node node;
    private ResultsTest test;

    /**
     * Set up the test result for each test.
     * 
     * @throws Exception
     *             If any errors occur during the setup.
     */
    @Before
    public void setUp() throws Exception {
        mockStatic(XslAPI.class);
        when(XslAPI.getNodeXPathValue(node, "NAME")).thenReturn(testName);
        when(XslAPI.getNodeXPathValue(node, "count(./ASSERTS/ASSERT)")).thenReturn(Integer.toString(totalAsserts));
        when(XslAPI.getNodeXPathValue(node, "count(./ERRORS/ERROR)")).thenReturn(Integer.toString(totalErrors));
        when(XslAPI.getNodeXPathValue(node, "RESULT")).thenReturn(testResult.name());

        test = new ResultsTest(node);
    }

    /**
     * Test the retrieval of the total assert count.
     */
    @Test
    public void testGetAssertCount() {
        assertThat(test.getAssertCount()).isEqualTo(totalAsserts);
    }

    /**
     * Test the retrieval of the number of errors.
     */
    @Test
    public void testGetErrorCount() {
        assertThat(test.getErrorCount()).isEqualTo(totalErrors);
    }

    /**
     * Test the retrieval of the test result.
     */
    @Test
    public void testGetResult() {
        assertThat(test.getResult()).isEqualTo(testResult);
    }

    /**
     * Test the retrieval of the test name.
     */
    @Test
    public void testGetName() {
        assertThat(test.getName()).isEqualTo(testName);
    }

    /**
     * Test the retrieval of the result of an assertion at a specific line.
     */
    @Test
    public void testGetAssertResult() {
        final int assertIdx = 23;
        final TestResult result = TestResult.ERRORED;
        when(XslAPI.getNodeXPathValue(node, "./ASSERTS/ASSERT[" + assertIdx + "]/RESULT")).thenReturn(result.name());
        assertThat(test.getAssertResult(assertIdx)).isEqualTo(result);
    }

    /**
     * Test the retrieval of a test executed at a given line index.
     */
    @Test
    public void testGetAssertTest() {
        final String testText = "a test text";
        final int assertIdx = 4738;
        when(XslAPI.getNodeXPathValue(node, "./ASSERTS/ASSERT[" + assertIdx + "]/TEST")).thenReturn(testText);
        assertThat(test.getAssertTest(assertIdx)).isEqualTo(testText);
    }

    /**
     * Test the retrieval of an assertion's context at a given line.
     */
    @Test
    public void testGetAssertContext() {
        final String context = "i am a context";
        final int assertIdx = 8658;
        when(XslAPI.getNodeXPathValue(node, "./ASSERTS/ASSERT[" + assertIdx + "]/CONTEXT")).thenReturn(context);
        assertThat(test.getAssertContext(assertIdx)).isEqualTo(context);
    }

    /**
     * Test the retrieval of a line number for a given index.
     */
    @Test
    public void testGetAssertSourceCodeLineNumber() {
        final int lineNumber = 2346;
        final int assertIdx = 363;
        when(XslAPI.getNodeXPathValue(node, "./ASSERTS/ASSERT[" + assertIdx + "]/LINENUMBER"))
                .thenReturn(Integer.toString(lineNumber));
        assertThat(test.getAssertSourceCodeLineNumber(assertIdx)).isEqualTo(lineNumber);
    }

    /**
     * Test the retrieval of the line number for an index of an error.
     */
    @Test
    public void testGetErrorLineNumber() {
        final int lineNumber = 47099;
        final int errorIdx = 1695;
        when(XslAPI.getNodeXPathValue(node, "./ERRORS/ERROR[" + errorIdx + "]/LINENUMBER"))
                .thenReturn(Integer.toString(lineNumber));
        assertThat(test.getErrorLineNumber(errorIdx)).isEqualTo(lineNumber);
    }

    /**
     * Test the retrieval of the error text at a specific line.
     */
    @Test
    public void testGetErrorText() {
        final String errorText = "i am an error";
        final int errorIdx = 3028;
        when(XslAPI.getNodeXPathValue(node, "./ERRORS/ERROR[" + errorIdx + "]/ERRORTEXT")).thenReturn(errorText);
        assertThat(test.getErrorText(errorIdx)).isEqualTo(errorText);
    }
}
