package com.cerner.ccl.testing.maven.ccl.reports.common;
import static org.fest.assertions.Assertions.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cerner.ccl.testing.maven.ccl.reports.common.CCLProgram;
import com.cerner.ccl.testing.maven.ccl.reports.common.ResultsTest;
import com.cerner.ccl.testing.maven.ccl.reports.common.ResultsTestCase;
import com.cerner.ccl.testing.maven.ccl.reports.common.ResultsTest.TestResult;
import com.cerner.ccl.testing.xsl.XslAPI;

/**
 * Unit tests for {@link ResultsTestCase}.
 * 
 * @author Joshua Hyde
 * 
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { CCLProgram.class, File.class, FileUtils.class, ResultsTestCase.class, ResultsTest.class, XslAPI.class })
public class ResultsTestCaseTest {
    private final String testResultsXml = "i am the rest results XML";
    private final String testName = "a test name";

    @Mock
    private File testDirectory;
    @Mock
    private CCLProgram program;
    @Mock
    private Document testResultsDocument;
    @Mock
    private NodeList nodeList;

    /**
     * Set up all of the prerequisites to set up a {@link ResultsTest} instantiation with the exception of the actual {@link ResultsTest} objects.
     * 
     * @throws Exception
     *             If any errors occur during the setup.
     */
    @Before
    public void setUp() throws Exception {
        when(testDirectory.getName()).thenReturn(testName);

        final File listingFile = mock(File.class);
        final File testResultsFile = mock(File.class);
        whenNew(File.class).withArguments(testDirectory, "listing.xml").thenReturn(listingFile);
        whenNew(File.class).withArguments(testDirectory, "test-results.xml").thenReturn(testResultsFile);

        final String listingXml = "a listing XML";
        mockStatic(FileUtils.class);
        when(FileUtils.readFileToString(listingFile, "utf-8")).thenReturn(listingXml);
        when(FileUtils.readFileToString(testResultsFile, "utf-8")).thenReturn(testResultsXml);

        whenNew(CCLProgram.class).withArguments(listingXml).thenReturn(program);

        mockStatic(XslAPI.class);
        when(XslAPI.getDocumentFromString(testResultsXml)).thenReturn(testResultsDocument);
        when(XslAPI.getXPathNodeList(testResultsDocument, "/TESTCASE/TESTS/TEST")).thenReturn(nodeList);
    }

    /**
     * Test the retrieval of the test name.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetName() throws Exception {
        setUpTestResults();
        assertThat(new ResultsTestCase(testDirectory).getName()).isEqualTo(testName);
    }

    /**
     * Test the retrieval of the listing XML.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetListingXml() throws Exception {
        setUpTestResults();
        
        final String listingXml = "i am the listing xml";
        when(program.getListingXML()).thenReturn(listingXml);
        assertThat(new ResultsTestCase(testDirectory).getListingXML()).isEqualTo(listingXml);
    }

    /**
     * Test the retrieval of the test results XML.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetTestResultsXml() throws Exception {
        setUpTestResults();
        assertThat(new ResultsTestCase(testDirectory).getTestResultsXML()).isEqualTo(testResultsXml);
    }

    /**
     * Test the retrieval of source code by line number.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetSourceByLineNumber() throws Exception {
        setUpTestResults();

        final String sourceCode = "i am source code";
        final int lineNumber = 2648739;
        when(program.getSourceCodeAtLine(lineNumber)).thenReturn(sourceCode);
        assertThat(new ResultsTestCase(testDirectory).getSourceByLineNumber(lineNumber)).isEqualTo(sourceCode);
    }

    /**
     * Test the retrieval of the test count.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetTestCount() throws Exception {
        setUpTestResults(TestResult.PASSED, TestResult.ERRORED);
        assertThat(new ResultsTestCase(testDirectory).getTestCount()).isEqualTo(2);
    }

    /**
     * Test the counting of failed tests.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetFailedTestCount() throws Exception {
        setUpTestResults(TestResult.PASSED, TestResult.FAILED, TestResult.FAILED, TestResult.ERRORED);
        assertThat(new ResultsTestCase(testDirectory).getFailedTestCount()).isEqualTo(2);
    }

    /**
     * Test the counting of passed tests.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetPassedTestCount() throws Exception {
        setUpTestResults(TestResult.PASSED, TestResult.FAILED, TestResult.PASSED, TestResult.PASSED, TestResult.ERRORED);
        assertThat(new ResultsTestCase(testDirectory).getPassedTestCount()).isEqualTo(3);
    }

    /**
     * Test the counting of errored tests.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetErroredTestCount() throws Exception {
        setUpTestResults(TestResult.PASSED, TestResult.ERRORED, TestResult.FAILED);
        assertThat(new ResultsTestCase(testDirectory).getErroredTestCount()).isEqualTo(1);
    }

    /**
     * Test the retrieval of the test results.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetTests() throws Exception {
        final List<ResultsTest> results = setUpTestResults(TestResult.PASSED, TestResult.FAILED, TestResult.ERRORED);
        assertThat(new ResultsTestCase(testDirectory).getTests()).containsOnly(results.toArray());
    }

    /**
     * Set up the returned test results.
     * 
     * @param results
     *            A varargs array of {@link TestResult} objects to be returned as part of {@link ResultsTest} objects.
     * @return A {@link List} of {@link ResultsTest} objects that will be returned.
     * @throws Exception
     *             If any errors occur during the setup.
     */
    private List<ResultsTest> setUpTestResults(TestResult... results) throws Exception {
        assert results != null;

        when(nodeList.getLength()).thenReturn(Integer.valueOf(results.length));
        if (results.length == 0)
            return Collections.emptyList();

        final List<ResultsTest> testResults = new ArrayList<ResultsTest>(results.length);
        for (int i = 0; i < results.length; i++) {
            final Node node = mock(Node.class);
            when(nodeList.item(i)).thenReturn(node);

            final ResultsTest test = mock(ResultsTest.class);
            when(test.getResult()).thenReturn(results[i]);
            testResults.add(test);

            whenNew(ResultsTest.class).withArguments(node).thenReturn(test);
        }

        return testResults;
    }
}
