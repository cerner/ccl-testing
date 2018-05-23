package com.cerner.ccltesting.maven.ccl.util;

import java.io.File;
import java.util.Locale;

import org.codehaus.plexus.util.FileUtils;

/**
 * An object to write out test result data from a given XML string. <br>
 * The test result data will be written out to the following structure (assume that "target" is the specified output
 * directory, "test1" is the name of the test, and "program1" and "program2" are programs tested by test1):
 *
 * <pre>
 * target/
 *  test-results/
 *      test1/
 *          listing.xml
 *          test-results.xml
 *          coverage/
 *              test-coverage.xml
 *              program1.xml
 *              program2.xml
 *
 * </pre>
 *
 * @author Joshua Hyde
 *
 */

public class TestResultWriter {
    private static final XmlFormatter formatter = new XmlFormatter();

    private final String testName;
    private final File testResultsDirectory;
    private final File testDataDirectory;
    private final File testCoverageDirectory;
    private boolean environmentXmlHasBeenWritten = false;

    /**
     * Create a test result writer.
     *
     * @param testName
     *            The name of the test for which test data is to be written.
     * @param outputDirectory
     *            A {@link File} object representing the location to the hard disk to which the output should be
     *            written. This should be ${project.build.directory}.
     */
    public TestResultWriter(final String testName, final File outputDirectory) {
        this.testName = testName;
        testResultsDirectory = new File(outputDirectory, "test-results/");
        if (!testResultsDirectory.exists())
            FileUtils.mkdir(testResultsDirectory.getAbsolutePath());

        testDataDirectory = new File(testResultsDirectory, testName.toLowerCase(Locale.getDefault()) + "/");
        if (!testDataDirectory.exists())
            FileUtils.mkdir(testDataDirectory.getAbsolutePath());

        testCoverageDirectory = new File(testDataDirectory, "coverage");
        if (!testCoverageDirectory.exists())
            FileUtils.mkdir(testCoverageDirectory.getAbsolutePath());
    }

    /**
     * Writes xml data to a file named environment.xml in the test-results directory, but only if this has not been done
     * previously by this instance.
     *
     * @param xml
     *            The xml to write.
     */
    public void writeEnvironmentXml(final String xml) {
        if (environmentXmlHasBeenWritten) {
            return;
        }
        try {
            writeXmlToFile(xml, new File(testResultsDirectory, "environment.xml"));
        } catch (RuntimeException e) {
            throw new RuntimeException("failed to write environment xml");
        }
        environmentXmlHasBeenWritten = true;
    }

    /**
     * Write to the disk a listing for the test. A listing is defined as the output of the compilation of the test file.
     *
     * @param xml
     *            The XML representing the listing output.
     */
    public void writeTestListing(final String xml) {
        try {
            writeXmlToFile(xml, new File(testDataDirectory, "listing.xml"));
        } catch (RuntimeException e) {
            throw new RuntimeException("failed to write test listing for " + testName, e);
        }
    }

    /**
     * Write the results of the test run to the local disk.
     *
     * @param xml
     *            The XML representing the test results.
     */
    public void writeTestResults(final String xml) {
        try {
            writeXmlToFile(xml, new File(testDataDirectory, "test-results.xml"));
        } catch (RuntimeException e) {
            throw new RuntimeException("failed to write test results for " + testName, e);
        }
    }

    /**
     * Write the coverage of a given test.
     *
     * @param xml
     *            The XML representing the code coverage.
     */
    public void writeTestCoverage(final String xml) {
        try {
            writeXmlToFile(xml, new File(testCoverageDirectory, "test-coverage.xml"));
        } catch (RuntimeException e) {
            throw new RuntimeException("failed to write test coverage for " + testName, e);
        }
    }

    /**
     * Write the coverage of a program by the given test.
     *
     * @param cclObjectName
     *            The name of the CCL script (without the .prg extension) that was tested. This will be the base of the
     *            name of the file created.
     * @param xml
     *            The XML representing the code coverage.
     */
    public void writeTestProgramCoverage(final String cclObjectName, final String xml) {
        try {
            writeXmlToFile(xml,
                    new File(testCoverageDirectory, cclObjectName.toLowerCase(Locale.getDefault()) + ".xml"));
        } catch (RuntimeException e) {
            throw new RuntimeException(
                    "failed to write program coverage for " + cclObjectName.toLowerCase(Locale.getDefault()), e);
        }
    }

    /**
     * Write XML out to a file.
     *
     * @param xml
     *            The XML to be written.
     * @param file
     *            The file to which the XML data is to be written.
     */
    private void writeXmlToFile(final String xml, final File file) {
        formatter.formatAndWriteXml(xml, file);
    }
}
