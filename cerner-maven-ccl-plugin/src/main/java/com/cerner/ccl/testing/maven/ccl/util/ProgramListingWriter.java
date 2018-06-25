package com.cerner.ccl.testing.maven.ccl.util;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.io.FileUtils;

/**
 * An object to write the listing output of a program's compilation to the local disk.
 * <br>
 * The output will be structured in the following structure (assume that "target" is the output directory and "program1"
 * is a program that was compiled):
 *
 * <pre>
 * target
 *      /program-listings
 *          program1.xml
 * </pre>
 *
 * @author Joshua Hyde
 *
 */

public class ProgramListingWriter {
    private final Set<String> listingsWritten = new HashSet<String>();
    private final XmlFormatter formatter;
    private final File listingsDirectory;

    /**
     * Create a compilation listing writer.
     *
     * @param outputDirectory
     *            A {@link File} object representing the top-level directory under which the data is to be written. This
     *            should be ${project.build.directory}.
     */
    public ProgramListingWriter(final File outputDirectory) {
        this.listingsDirectory = new File(outputDirectory, "program-listings/");
        try {
            FileUtils.forceMkdir(listingsDirectory);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to create program listings directory.", e);
        }

        formatter = new XmlFormatter();
    }

    /**
     * Determine whether or not this instance has written out the listing information for the given object.
     *
     * @param cclObjectName
     *            The name of the object for which previously list-writing is to be determined.
     * @return {@code true} if this instance has previously written listing data for the given object.
     */
    public boolean hasWrittenListing(final String cclObjectName) {
        return listingsWritten.contains(cclObjectName.toUpperCase(Locale.getDefault()));
    }

    /**
     * Write a program's listing to disk. If the object's listing has been previously written by this instance, it will
     * not be written again.
     *
     * @param cclObjectName
     *            The name of the object whose listing is to be written.
     * @param xml
     *            The XML to be written.
     * @throws IOException
     *             If any errors occur during the write-out.
     */
    public void writeListing(final String cclObjectName, final String xml) throws IOException {
        // Don't re-write the listing
        if (hasWrittenListing(cclObjectName))
            return;
        try {
        formatter.formatAndWriteXml(xml,
                new File(listingsDirectory, cclObjectName.toLowerCase(Locale.getDefault()) + ".xml"));
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to write xml program listing for " + cclObjectName, e);
        }
        markAsWritten(cclObjectName);
    }

    /**
     * Mark a CCL object as written.
     *
     * @param cclObjectName
     *            The name of the object to be mark as having its listing output written out.
     */
    private void markAsWritten(final String cclObjectName) {
        listingsWritten.add(cclObjectName.toUpperCase(Locale.getDefault()));
    }
}
