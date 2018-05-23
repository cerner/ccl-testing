package com.cerner.ccl.cdoc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;

import com.cerner.ccl.parser.data.CclScript;
import com.cerner.ccl.parser.data.IncludeFile;
import com.cerner.ccl.parser.text.TextParser;

/**
 * Skeleton definition of an integration test that reads source code of CCL scripts.
 * 
 * @author Joshua Hyde
 * 
 */

public abstract class AbstractSourceReadingITest {
    private final TextParser parser = new TextParser();

    /**
     * Parse an include file's documentation.
     * 
     * @param includeFilename
     *            The filename of the include file to be parsed.
     * @return A {@link IncludeFile} object representing the parsed include file.
     * @throws IOException
     *             If any errors occur while reading the include file.
     */
    protected IncludeFile getIncludeFile(final String includeFilename) throws IOException {
        return parser.parseIncludeFile(includeFilename.toLowerCase(Locale.US), readIncludeFile(includeFilename));
    }

    /**
     * Parse a script's documentation.
     * 
     * @param scriptName
     *            The name of the script to be retrieved.
     * @return A {@link CclScript} object representing the parsed script.
     * @throws IOException
     *             If any errors occur while reading the script.
     */
    protected CclScript getScript(final String scriptName) throws IOException {
        return parser.parseCclScript(scriptName.toLowerCase(Locale.US), readCclScript(scriptName));
    }

    /**
     * Read the source of a CCL script.
     * 
     * @param scriptName
     *            The name of the script to be read.
     * @return A {@link List} of {@link String} objects representing the source of the script.
     * @throws IOException
     *             If any errors occur while reading the script.
     */
    protected List<String> readCclScript(final String scriptName) throws IOException {
        final String path = "/ccl-scripts/" + scriptName + ".prg";
        final URL resourceUrl = getClass().getResource(path);
        if (resourceUrl == null) {
            throw new FileNotFoundException("Script not found: " + path);
        }

        return FileUtils.readLines(FileUtils.toFile(resourceUrl));
    }

    /**
     * Read the source of an include file.
     * 
     * @param includeFilename
     *            The filename of the include file to be read.
     * @return A {@link List} of {@link String} objects representing the source of the include file.
     * @throws IOException
     *             If any errors occur while reading the include file.
     */
    protected List<String> readIncludeFile(final String includeFilename) throws IOException {
        final String path = "/ccl-includes/" + includeFilename;
        final URL resourceUrl = getClass().getResource(path);
        if (resourceUrl == null) {
            throw new FileNotFoundException("Include file not found: " + path);
        }

        return FileUtils.readLines(FileUtils.toFile(resourceUrl));
    }
}
