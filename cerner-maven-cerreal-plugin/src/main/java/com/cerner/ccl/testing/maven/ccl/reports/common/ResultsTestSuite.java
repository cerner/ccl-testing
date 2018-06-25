package com.cerner.ccl.testing.maven.ccl.reports.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.maven.reporting.MavenReportException;

import com.cerner.ccl.testing.maven.ccl.reports.AbstractCCLMavenReport;

/**
 * Stores a collection of test case objects which are derived from the test-results from the last test run.
 *
 * @author Jeff Wiedemann
 *
 */
public class ResultsTestSuite {
    private final Collection<ResultsTestCase> testCases = new ArrayList<ResultsTestCase>();
    private final CclEnvironment cclEnvironment;

    /**
     * Constructor for the test suite
     *
     * @param testResultsDirectory
     *            The directory of the test-results which holds all of the data for the results from the last test run
     * @throws MavenReportException
     *             When the test cases cannot be created from the test results passed in, or when the test results
     *             directory passed in is invalid
     */
    public ResultsTestSuite(File testResultsDirectory) throws MavenReportException {
        File environmentXmlFile = AbstractCCLMavenReport.getDirectoryFile(testResultsDirectory, "environment.xml");
        cclEnvironment = new CclEnvironment(environmentXmlFile);
        if (!testResultsDirectory.exists() || !testResultsDirectory.isDirectory()) {
            throw new MavenReportException("The specified test-results directory is invalid");
        }
        File[] directoryList = testResultsDirectory.listFiles();
        if (directoryList == null) {
            return;
        }
        for (File file : directoryList) {
            // If the subdirectory of test-results contains a listing.xml and a test-results.xml
            if (AbstractCCLMavenReport.getDirectoryFile(file, "listing.xml") != null
                    && AbstractCCLMavenReport.getDirectoryFile(file, "test-results.xml") != null) {
                testCases.add(new ResultsTestCase(file));
            }
        }
    }

    /**
     * Get the number of test cases.
     *
     * @return The total number of tests for all test cases within this suite
     */
    public int getTestCount() {
        int total = 0;
        for (ResultsTestCase tc : testCases) {
            total += tc.getTestCount();
        }
        return total;
    }

    /**
     * Get the number of failed tests.
     *
     * @return The total number of failed tests
     */
    public int getFailedCount() {
        int total = 0;
        for (ResultsTestCase tc : testCases) {
            total += tc.getFailedTestCount();
        }
        return total;
    }

    /**
     * Get the number of errored tests.
     *
     * @return The total number of errored tests
     */
    public int getErroredCount() {
        int total = 0;
        for (ResultsTestCase tc : testCases) {
            total += tc.getErroredTestCount();
        }
        return total;
    }

    /**
     * Get the number of passed tests.
     *
     * @return The total number of passed tests
     */
    public int getPassedCount() {
        int total = 0;
        for (ResultsTestCase tc : testCases) {
            total += tc.getPassedTestCount();
        }
        return total;
    }

    /**
     * Get all of the test cases in this suite.
     *
     * @return A {@link Collection} of {@link ResultsTestCase} objects representing all test cases in this suite.
     */
    public Collection<ResultsTestCase> getTestCases() {
        return Collections.unmodifiableCollection(testCases);
    }

    /**
     * @return the cclEnvironment.
     */
    public CclEnvironment getCclEnvironment() {
        return cclEnvironment;
    }
}
