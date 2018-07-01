package com.cerner.ccl.testing.maven.ccl.util.factory;

import java.io.File;

import com.cerner.ccl.testing.maven.ccl.util.ProgramListingWriter;

/**
 * A factory to create {@link ProgramListingWriter} objects.
 *
 * @author Joshua Hyde
 *
 */

public class ProgramListingWriterFactory {
    /**
     * Create a program listing writer.
     *
     * @param outputDirectory
     *            The directory to which the listing compilation output of a script should be written.
     * @return A {@link ProgramListingWriter} object.
     */
    public ProgramListingWriter create(final File outputDirectory) {
        return new ProgramListingWriter(outputDirectory);
    }
}
