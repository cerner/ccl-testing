package com.cerner.ccl.testing.maven.ccl.reports.common;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.reporting.MavenReportException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.testing.maven.ccl.reports.AbstractCCLMavenReport;
import com.cerner.ccl.testing.maven.ccl.reports.common.ResultsTestCase;
import com.cerner.ccl.testing.maven.ccl.reports.common.ResultsTestSuite;

/**
 * Unit tests for {@link ResultsTestSuite}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { AbstractCCLMavenReport.class, ResultsTestCase.class, ResultsTestSuite.class })
public class ResultsTestSuiteTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();
    private ResultsTestSuite suite;
    private List<ResultsTestCase> cases;

    /**
     * Set up the test suite for each test.
     *
     * @throws Exception
     *             If any errors occur during the setup.
     */
    @Before
    public void setUp() throws Exception {
        final File directory = mock(File.class);
        when(directory.exists()).thenReturn(Boolean.TRUE);
        when(directory.isDirectory()).thenReturn(Boolean.TRUE);

        final File subdirA = mock(File.class);
        final ResultsTestCase caseA = mock(ResultsTestCase.class);

        final File subdirB = mock(File.class);
        final ResultsTestCase caseB = mock(ResultsTestCase.class);

        setUpDirectories(new File[] { subdirA, subdirB }, new ResultsTestCase[] { caseA, caseB });
        when(directory.listFiles()).thenReturn(new File[] { subdirA, subdirB });

        suite = new ResultsTestSuite(directory);
        cases = Arrays.asList(caseA, caseB);
    }

    /**
     * If a directory does not have a {@code listing.xml} file, then it should not be turned into a test case.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testConstructNoListing() throws Exception {
        final File directory = mock(File.class);
        when(directory.exists()).thenReturn(Boolean.TRUE);
        when(directory.isDirectory()).thenReturn(Boolean.TRUE);

        final File subdirectory = mock(File.class);
        when(directory.listFiles()).thenReturn(new File[] { subdirectory });

        mockStatic(AbstractCCLMavenReport.class);
        when(AbstractCCLMavenReport.getDirectoryFile(subdirectory, "listing.xml")).thenReturn(null);

        assertThat(new ResultsTestSuite(directory).getTestCases()).isEmpty();
    }

    /**
     * If the given subdirectory has a listing, but no test results, then it shouldn't be turned into a case.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testConstructNoTestResults() throws Exception {
        final File directory = mock(File.class);
        when(directory.exists()).thenReturn(Boolean.TRUE);
        when(directory.isDirectory()).thenReturn(Boolean.TRUE);

        final File subdirectory = mock(File.class);
        when(directory.listFiles()).thenReturn(new File[] { subdirectory });

        mockStatic(AbstractCCLMavenReport.class);
        final File indicatorFile = mock(File.class);
        when(AbstractCCLMavenReport.getDirectoryFile(subdirectory, "listing.xml")).thenReturn(indicatorFile);
        when(AbstractCCLMavenReport.getDirectoryFile(subdirectory, "test-results.xml")).thenReturn(null);
        assertThat(new ResultsTestSuite(directory).getTestCases()).isEmpty();
    }

    /**
     * If the given directory does not exist, then construction should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNonexistentDirectory() throws Exception {
        final File directory = mock(File.class);
        when(directory.exists()).thenReturn(Boolean.FALSE);

        expected.expect(MavenReportException.class);
        expected.expectMessage("The specified test-results directory is invalid");
        new ResultsTestSuite(directory);
    }

    /**
     * If the given directory is not, in fact, a directory, then construction should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNotDirectory() throws Exception {
        final File directory = mock(File.class);
        when(directory.exists()).thenReturn(Boolean.TRUE);
        when(directory.isDirectory()).thenReturn(Boolean.FALSE);

        expected.expect(MavenReportException.class);
        expected.expectMessage("The specified test-results directory is invalid");
        new ResultsTestSuite(directory);
    }

    /**
     * Test the retrieval of the errored test count.
     */
    @Test
    public void testGetErroredCount() {
        int expectedSum = 0;
        for (ResultsTestCase testCase : cases) {
            when(testCase.getErroredTestCount()).thenReturn(Integer.valueOf(2));
            expectedSum += 2;
        }
        assertThat(suite.getErroredCount()).isEqualTo(expectedSum);
    }

    /**
     * Test the retrieval of the failed test count.
     */
    @Test
    public void testGetFailedCount() {
        int expectedSum = 0;
        for (ResultsTestCase testCase : cases) {
            when(testCase.getFailedTestCount()).thenReturn(Integer.valueOf(2));
            expectedSum += 2;
        }
        assertThat(suite.getFailedCount()).isEqualTo(expectedSum);
    }

    /**
     * Test the summing of the passed test counts.
     */
    @Test
    public void testGetPassedCount() {
        int expectedSum = 0;
        for (ResultsTestCase testCase : cases) {
            when(testCase.getPassedTestCount()).thenReturn(Integer.valueOf(2));
            expectedSum += 2;
        }
        assertThat(suite.getPassedCount()).isEqualTo(expectedSum);
    }

    /**
     * Test the retrieval of the test count.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetTestCount() throws Exception {
        int expectedSum = 0;
        for (ResultsTestCase testCase : cases) {
            when(testCase.getTestCount()).thenReturn(Integer.valueOf(2));
            expectedSum += 2;
        }
        assertThat(suite.getTestCount()).isEqualTo(expectedSum);
    }

    /**
     * Set up a series of directories to be turned into {@link ResultsTestCase} objects. The two given arrays should be
     * parallel.
     *
     * @param directories
     *            An array of {@link File} objects representing the directories to be turned into results test case
     *            objects.
     * @param cases
     *            An array {@link ResultsTestCase} objects representing the cases into which the files are to be turned.
     * @throws Exception
     *             If any errors occur while setting up the directory/case mappings.
     */
    private void setUpDirectories(File[] directories, ResultsTestCase[] cases) throws Exception {
        assert directories.length == cases.length;
        final File indicatorFile = mock(File.class);

        mockStatic(AbstractCCLMavenReport.class);
        int caseIdx = 0;
        for (File directory : directories) {
            when(AbstractCCLMavenReport.getDirectoryFile(directory, "listing.xml")).thenReturn(indicatorFile);
            when(AbstractCCLMavenReport.getDirectoryFile(directory, "test-results.xml")).thenReturn(indicatorFile);
            whenNew(ResultsTestCase.class).withArguments(directory).thenReturn(cases[caseIdx++]);
        }
    }
}
