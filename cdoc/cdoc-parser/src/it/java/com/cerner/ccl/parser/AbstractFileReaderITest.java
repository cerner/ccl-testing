package com.cerner.ccl.parser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * Skeleton definition of an integration test that reads files.
 * 
 * @author Joshua Hyde
 * 
 */

public abstract class AbstractFileReaderITest {
    /**
     * Read a resource.
     * 
     * @param path
     *            The path to the file to be read, relative to "/&lt;{@link Class#getSimpleName() simple name}&gt;/".
     * @return A {@link List} of {@link String} objects representing the lines of the request file.
     * @throws IOException
     *             If any errors occur while reading the file.
     */
    protected List<String> readResource(final String path) throws IOException {
        return FileUtils.readLines(toFile(path), Charset.forName("utf-8"));
    }

    /**
     * Get a file.
     * 
     * @param path
     *            The path to the file to be retrieved, relative to "/&lt;{@link Class#getSimpleName() simple name}&gt;/".
     * @return A {@link File} reference representing the requested file.
     * @throws IllegalArgumentException
     *             If the requested resource cannot be found.
     */
    private File toFile(final String path) {
        final URL resourceUrl = getClass().getResource("/" + getClass().getSimpleName() + "/" + path);
        if (resourceUrl == null) {
            throw new IllegalArgumentException("Resource not found: " + path);
        }

        return FileUtils.toFile(resourceUrl);
    }
}
