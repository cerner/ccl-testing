package com.cerner.ccl.testing.maven.ccl.reports.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.apache.maven.reporting.MavenReportException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.cerner.ccl.testing.xsl.XslAPI;
import com.cerner.ccl.testing.xsl.XslAPIException;

/**
 * Stores the test results of a single test.inc file from /src/test/ccl.
 * 
 * @author Jeff Wiedemann
 * 
 */
public class ResultsTestCase {
    private final String name;
    private final CCLProgram testProgram;
    private final String testResultsXML;
    private final Document testResultsDOM;

    private Collection<ResultsTest> tests = new ArrayList<ResultsTest>();

    /**
     * Constructor for a test case
     * 
     * @param testCaseDirectory
     *            The directory from target/test-results which stores the listing xml for this test case as well as the
     *            test results from the last test run
     * @throws MavenReportException
     *             When the listing xml file cannot be read or interpreted as valid xml for a CCL program. When the
     *             test-results xml file cannot be read or interpreted as valid results xml for the test run. When a
     *             data abnormality in the test results xml is identified which likely compromises the integrity of the
     *             test results
     */
    public ResultsTestCase(File testCaseDirectory) throws MavenReportException {
        this.name = testCaseDirectory.getName();
        try {
            this.testProgram = new CCLProgram(
                    FileUtils.readFileToString(new File(testCaseDirectory, "listing.xml"), "utf-8"));
            this.testResultsXML = FileUtils.readFileToString(new File(testCaseDirectory, "test-results.xml"), "utf-8");
        } catch (IOException e) {
            throw new MavenReportException(
                    "Failed to open listing.xml or test-results.xml files from test-result directory " + this.name
                            + " due to error",
                    e);
        }

        try {
            this.testResultsDOM = XslAPI.getDocumentFromString(testResultsXML);
        } catch (XslAPIException e) {
            throw new MavenReportException("Failed to parse listing or test-results xml due to error", e);
        }

        final NodeList nodes = XslAPI.getXPathNodeList(this.testResultsDOM, "/TESTCASE/TESTS/TEST");
        for (int idx = 0; idx < nodes.getLength(); idx++)
            tests.add(new ResultsTest(nodes.item(idx)));
    }

    /**
     * Get the name of the test case.
     * 
     * @return The name of the test case.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the listing XML that represents the source code for this test case.
     * 
     * @return The listing XML that represents the source code for this test case.
     */
    public String getListingXML() {
        return testProgram.getListingXML();
    }

    /**
     * Get the test results XML that represents the results of the last test run of this test case.
     * 
     * @return The test results XML that represents the results of the last test run of this test case.
     */
    public String getTestResultsXML() {
        return testResultsXML;
    }

    /**
     * Get the line of source code at the given number.
     * 
     * @param lineNumber
     *            The number of the line to be retrieved.
     * @return The source code at the given line.
     */
    public String getSourceByLineNumber(int lineNumber) {
        return testProgram.getSourceCodeAtLine(lineNumber);
    }

    /**
     * Get the number of tests in this test case.
     * 
     * @return The total number of tests in the test case.
     */
    public int getTestCount() {
        return tests.size();
    }

    /**
     * Get the number of failed tests.
     * 
     * @return The total number of failed tests in the test case.
     */
    public int getFailedTestCount() {
        int total = 0;
        for (ResultsTest t : tests) {
            if (t.getResult() == ResultsTest.TestResult.FAILED)
                total++;

        }
        return total;
    }

    /**
     * Get the total number of passed tests.
     * 
     * @return The total number of passed tests in the test case
     */
    public int getPassedTestCount() {
        int total = 0;
        for (ResultsTest t : tests) {
            if (t.getResult() == ResultsTest.TestResult.PASSED)
                total++;
        }
        return total;
    }

    /**
     * Get the number of tests that errored.
     * 
     * @return The total number of tests in the test case that have encountered one or more errors.
     */
    public int getErroredTestCount() {
        int total = 0;
        for (ResultsTest t : tests) {
            if (t.getResult() == ResultsTest.TestResult.ERRORED)
                total++;
        }
        return total;
    }

    /**
     * Get all tests.
     * 
     * @return A collection of ResultsTest objects representing all of the tests in this test case.
     */
    public Collection<ResultsTest> getTests() {
        return Collections.unmodifiableCollection(this.tests);
    }
}
