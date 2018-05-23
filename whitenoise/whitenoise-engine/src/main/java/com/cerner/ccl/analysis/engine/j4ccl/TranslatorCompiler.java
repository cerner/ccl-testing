package com.cerner.ccl.analysis.engine.j4ccl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;

import com.cerner.ccl.j4ccl.CclExecutor;

/**
 * A compiler used to compile the CCL script used to perform the translation.
 *
 * @author Joshua Hyde
 *
 */

public class TranslatorCompiler {
    /**
     * Compile the CCL script.
     */
    // TODO - only do this if the script does not exist.
    public void compile() {
        final File tempFile = copyTranslatorScript();
        final CclExecutor executor = CclExecutor.getExecutor();
        executor.addScriptCompiler(tempFile).commit();
        executor.execute();
        if (!tempFile.delete()) {
            System.out.println("failed to delete " + tempFile);
        }
    }

    /**
     * Copy the translator script from a classpath resource to a temporary file.
     * <p>
     * This method is purposefully made package-private to make it visible for testing.
     *
     * @return A {@link File} reference to the local copy of the translation script.
     */
    File copyTranslatorScript() {
        final File tempFile = createTemporaryFile();
        try (final InputStream scriptInput = getScriptStream();
                OutputStreamWriter scriptOutput = new OutputStreamWriter(new FileOutputStream(tempFile),
                        Charset.forName("utf-8"))) {
            IOUtils.copy(scriptInput, scriptOutput, Charset.forName("utf-8"));
        } catch (final IOException e) {
            throw new RuntimeException("Failed to copy XML translator to local file.", e);
        }

        return tempFile;
    }

    /**
     * Create a temporary file.
     * <p>
     * This method is purposefully made package-private to make it visible for testing.
     *
     * @return A {@link File} reference to a temporary file.
     */
    File createTemporaryFile() {
        return new File(System.getProperty("java.io.tmpdir") + "/ccl_xml_translator.prg");
    }

    /**
     * Get an input stream pointer to the CCL translation script.
     * <p>
     * This is abstracted into its own method to allow it to be overridden for testing.
     *
     * @return An {@link InputStream} representing the CCL translation script resource.
     */
    InputStream getScriptStream() {
        return TranslatorCompiler.class.getResourceAsStream("/ccl/ccl_xml_translator.prg");
    }
}
