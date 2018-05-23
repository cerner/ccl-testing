package com.cerner.ccltesting.maven.ccl.util;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for {@link ProgramListingWriter}.
 *
 * @author Joshua Hyde
 *
 */

public class ProgramListingWriterTest {
    private static final String TEST_NAME = "test1";
    private static final File TEST_OUTPUT_DIRECTORY = new File("target/unit/listingWriterTest/");
    private static final File TEST_RESULTS_DIRECTORY = new File(TEST_OUTPUT_DIRECTORY, "test-results/" + TEST_NAME);
    private static ProgramListingWriter WRITER;

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

        WRITER = new ProgramListingWriter(TEST_OUTPUT_DIRECTORY);

        EXPECTED_CONTENTS.add("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        EXPECTED_CONTENTS.add("");
        EXPECTED_CONTENTS.add("<ROOT>");
        EXPECTED_CONTENTS.add("    <CHILD>TEXT</CHILD>");
        EXPECTED_CONTENTS.add("</ROOT>");
    }

    /**
     * Test that writing a listing works.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteListing() throws Exception {
        final String objectName = "object";
        final File expectedLocation = new File(TEST_OUTPUT_DIRECTORY,
                "program-listings/" + objectName.toLowerCase(Locale.getDefault()) + ".xml");
        WRITER.writeListing(objectName, INPUT_XML);

        assertThat(FileUtils.readLines(expectedLocation, "UTF-8")).isEqualTo(EXPECTED_CONTENTS);
    }

    /**
     * Test that the listing will not be written out again after it has been written once.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteListingNotAgain() throws Exception {
        final String objectName = "object";
        final File expectedLocation = new File(TEST_OUTPUT_DIRECTORY,
                "program-listings/" + objectName.toLowerCase(Locale.getDefault()) + ".xml");
        WRITER.writeListing(objectName, INPUT_XML);

        assertThat(expectedLocation.exists()).isTrue();

        // Delete the file and verify that it's not written again
        FileUtils.forceDelete(expectedLocation);
        WRITER.writeListing(objectName, INPUT_XML);
        assertThat(expectedLocation.exists()).isFalse();
    }

    /**
     * Verify that the writer correctly reflects that the listing for an object has been written.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testHasWrittenListing() throws Exception {
        final String objectName = "object";
        WRITER.writeListing(objectName, INPUT_XML);
        assertThat(WRITER.hasWrittenListing(objectName)).isTrue();
    }

}
