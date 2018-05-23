package com.cerner.ccl.analysis.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.FileUtils;

/**
 * Skeleton definition of an integration test that reads file.
 *
 * @author Joshua Hyde
 *
 */

public abstract class AbstractFileReaderTest extends AbstractJetmTest {
    /**
     * Convert a test resource to a {@link File} reference.
     *
     * @param path
     *            The path to the resource to be read.
     * @return A {@link File} reference to the desired resource.
     * @throws IllegalArgumentException
     *             If the given resource is not found.
     */
    protected File toFile(final String path) {
        final URL resourceUrl = getClass().getResource(path);
        if (resourceUrl == null)
            throw new IllegalArgumentException("Resource not found: " + path);

        return FileUtils.toFile(resourceUrl);
    }

    /**
     * Read a test resource as a string.
     *
     * @param fileName
     *            The name of the file to be read in. The file will be read in from the path of {@code /ccl/xml/<simple class name>/<given filename>}.
     * @return A {@link String} representing the contents of the requested resource.
     */
    protected String toString(final String fileName) {
        try {
            return FileUtils.readFileToString(toFile("/ccl/xml/" + getClass().getSimpleName() + "/" + fileName),
                    "utf-8");
        } catch (final IOException e) {
            throw new RuntimeException("Failed to read file: " + fileName, e);
        }
    }
}
