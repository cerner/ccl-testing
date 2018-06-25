package com.cerner.ccl.testing.maven.ccl.util.factory;

import java.io.File;

import com.cerner.ccl.testing.maven.ccl.util.TestResultWriter;

/**
 * A factory object to create {@link TestResultWriter} objects.
 *
 * @author Joshua Hyde
 *
 */

public class TestResultWriterFactory {
    /**
     * Create a test result writer.
     *
     * @param testName
     *            The name of the test.
     * @param outputDirectory
     *            The location on the disk to where the results should be written.
     * @return A {@link TestResultWriter} object.
     */
    public TestResultWriter create(final String testName, final File outputDirectory) {
        return new TestResultWriter(testName, outputDirectory);
    }
}
