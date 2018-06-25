package com.cerner.ccl.testing.maven.ccl.util;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cerner.ccl.testing.maven.ccl.util.TestResultWriter;

/**
 * Unit test of {@link TestResultWriter}.
 *
 * @author Joshua Hyde
 *
 */

public class TestResultWriterTest {
    private static final String TEST_NAME = "test1";
    private static final File TEST_OUTPUT_DIRECTORY = new File("target/unit/resultWriterTest/");
    private static final File TEST_RESULTS_DIRECTORY = new File(TEST_OUTPUT_DIRECTORY, "test-results/" + TEST_NAME);
    private static TestResultWriter WRITER;

    private static final String INPUT_XML = "<ROOT><CHILD>TEXT</CHILD></ROOT>";
    private static final List<String> EXPECTED_CONTENTS = new ArrayList<String>(4);

    /**
     * Create the directory to contain the generated files, create a test result writer, and set up the expected
     * contents of the generated XML files.
     *
     * @throws Exception
     *             If creating the tests fails.
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        FileUtils.forceMkdir(TEST_OUTPUT_DIRECTORY);
        FileUtils.forceMkdir(TEST_RESULTS_DIRECTORY);

        WRITER = new TestResultWriter(TEST_NAME, TEST_OUTPUT_DIRECTORY);

        EXPECTED_CONTENTS.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        EXPECTED_CONTENTS.add("");
        EXPECTED_CONTENTS.add("<ROOT>");
        EXPECTED_CONTENTS.add("    <CHILD>TEXT</CHILD>");
        EXPECTED_CONTENTS.add("</ROOT>");
    }

    /**
     * Verify that the test-results/ directory is created if it does not exist.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unused")
    @Test
    public void testCreateTestResultsDir() throws Exception {
        final File resultsDir = new File(TEST_OUTPUT_DIRECTORY, "test-results/");
        FileUtils.deleteDirectory(resultsDir);

        new TestResultWriter(TEST_NAME, TEST_OUTPUT_DIRECTORY);
        assertThat(resultsDir).exists();
    }

    /**
     * Verify that the test data directory (beneath "test-results/" by the name of the test) is created.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unused")
    @Test
    public void testCreateTestDataDirectory() throws Exception {
        FileUtils.deleteDirectory(TEST_RESULTS_DIRECTORY);
        new TestResultWriter(TEST_NAME, TEST_OUTPUT_DIRECTORY);
        assertThat(TEST_RESULTS_DIRECTORY).exists();
    }

    /**
     * Verify that the test coverage directory is created.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unused")
    @Test
    public void testCreateTestCoverageDirectory() throws Exception {
        final File testDir = new File(TEST_RESULTS_DIRECTORY, "coverage/");
        FileUtils.deleteDirectory(testDir);
        new TestResultWriter(TEST_NAME, TEST_OUTPUT_DIRECTORY);
        assertThat(testDir).exists();
    }

    /**
     * Test {@link TestResultWriter#writeTestListing(String)}.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteTestListing() throws Exception {
        final File expectedLocation = new File(TEST_RESULTS_DIRECTORY, "listing.xml");
        if (expectedLocation.exists())
            assertThat(expectedLocation.delete()).isTrue();

        WRITER.writeTestListing(INPUT_XML);
        assertThat(FileUtils.readLines(expectedLocation, "UTF-8")).isEqualTo(EXPECTED_CONTENTS);
    }

    /**
     * Test {@link TestResultWriter#writeTestResults(String)}.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteTestResults() throws Exception {
        final File expectedLocation = new File(TEST_RESULTS_DIRECTORY, "test-results.xml");
        if (expectedLocation.exists())
            assertThat(expectedLocation.delete()).isTrue();

        WRITER.writeTestResults(INPUT_XML);
        assertThat(FileUtils.readLines(expectedLocation, "UTF-8")).isEqualTo(EXPECTED_CONTENTS);
    }

    /**
     * Test {@link TestResultWriter#writeTestProgramCoverage(String, String)}.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteTestProgramCoverage() throws Exception {
        final File expectedLocation = new File(TEST_RESULTS_DIRECTORY, "coverage/program1.xml");
        if (expectedLocation.exists())
            assertThat(expectedLocation.delete()).isTrue();

        WRITER.writeTestProgramCoverage("PROGRAM1", INPUT_XML);
        assertThat(FileUtils.readLines(expectedLocation, "UTF-8")).isEqualTo(EXPECTED_CONTENTS);
    }

    /**
     * Test that the test coverage data is written out to the correct location.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteTestCoverages() throws Exception {
        final File expectedLocation = new File(TEST_RESULTS_DIRECTORY, "coverage/test-coverage.xml");
        if (expectedLocation.exists())
            assertThat(expectedLocation.delete()).isTrue();

        WRITER.writeTestCoverage(INPUT_XML);
        assertThat(FileUtils.readLines(expectedLocation, "UTF-8")).isEqualTo(EXPECTED_CONTENTS);
    }

    /**
     * Test {@link TestResultWriter#writeEnvironmentXml(String)}.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteEnvironmentXml() throws Exception {
        final File expectedLocation = new File(TEST_OUTPUT_DIRECTORY + "/test-results/", "environment.xml");
        if (expectedLocation.exists())
            assertThat(expectedLocation.delete()).isTrue();

        WRITER.writeEnvironmentXml(INPUT_XML);
        assertThat(FileUtils.readLines(expectedLocation, "UTF-8")).isEqualTo(EXPECTED_CONTENTS);
    }
}
