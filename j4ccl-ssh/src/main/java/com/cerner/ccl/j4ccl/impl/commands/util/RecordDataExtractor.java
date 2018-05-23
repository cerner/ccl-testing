package com.cerner.ccl.j4ccl.impl.commands.util;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.FileUtils;

import com.cerner.ccl.j4ccl.impl.data.Environment;
import com.cerner.ccl.j4ccl.impl.util.AuthHelper;
import com.cerner.ccl.j4ccl.impl.util.RecordWriter;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ftp.Downloader;
import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.data.factory.FileRequestFactory;
import com.cerner.ftp.sftp.SftpDownloader;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * An extractor to convert JSON representations of record structures from CCL into {@link Record} objects.
 *
 * @author Joshua Hyde
 *
 */

public class RecordDataExtractor {
    private static int objectCount = 0;

    private final Record record;
    private final URI remoteDataLocation;
    private final File localDataLocation;

    /**
     * Create an extractor.
     *
     * @param record
     *            The {@link Record} object into which the data values will be stored.
     */
    public RecordDataExtractor(final Record record) {
        this(record, Environment.getEnvironment());
    }

    /**
     * Package-private constructor.
     *
     * @param record
     *            The {@link Record} object into which the data values will be stored.
     * @param environment
     *            An {@link Environment} bean describing the environment with which this extractor is to interact.
     */
    public RecordDataExtractor(final Record record, final Environment environment) {
        final String fileName = createRandomFileName();
        this.record = record;

        remoteDataLocation = URI.create(environment.getCerTemp() + "/" + fileName);
        try {
            localDataLocation = File.createTempFile(fileName, null);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to create temporary file for local storage of JSON data.", e);
        }
        localDataLocation.deleteOnExit();
    }

    /**
     * Extract the record data from a CCL-generated JSON file on the remote server's hard disk.
     *
     * @throws IOException
     *             If any errors occur during the fetching and reading of the XML file.
     */
    public void extractRecordData() throws IOException {
        final EtmPoint point = PointFactory.getPoint(getClass(), "extractRecordData");
        try {
            final Downloader downloader = SftpDownloader.createDownloader(AuthHelper.fromCurrentSubject());
            final Collection<FileRequest> requests = Collections
                    .singleton(FileRequestFactory.create(getRemoteDataLocation(), getLocalDataLocation().toURI()));
            downloader.download(requests);

            RecordWriter.putFromJson(retrieveJsonData(), getRecord());
        } finally {
            point.collect();
        }
    }

    /**
     * Create a set of commands used to execute the parser adapter script needed to retrieve the record structure data
     * from the CCL session.
     *
     * @return A {@link Collection} of {@code String} objects representing the CCL commands.
     */
    public Collection<String> getExtractionCommands() {
        final List<String> lines = new ArrayList<String>(4);
        lines.add("call echojson(");
        lines.add(getRecord().getName());
        lines.add(", '" + remoteDataLocation.getPath() + "'");
        lines.add(") go");
        return lines;
    }

    /**
     * Get the record into which the data is to be written.
     *
     * @return A {@link Record} object.
     */
    public Record getRecord() {
        return record;
    }

    /**
     * Get the location on the local disk to which the JSON output should be written.
     *
     * @return A {@link File} object representing the location on the local hard disk to which the XML output should be
     *         downloaded.
     */
    File getLocalDataLocation() {
        return localDataLocation;
    }

    /**
     * Get the location on the remote server to which the XML output should be written.
     *
     * @return A {@link URI} object representing the remote server location.
     */
    URI getRemoteDataLocation() {
        return remoteDataLocation;
    }

    /**
     * Create a random name for the JSON output file.
     *
     * @return A random name.
     */
    private synchronized String createRandomFileName() {
        return String.format("j4ccl_dataout_%s_%d%d.json",
                System.getProperty("user.name").toLowerCase(Locale.getDefault()).replace("$", ""),
                ++objectCount, System.currentTimeMillis());
    }

    /**
     * Read the JSON data file and build its contents into a single string for parsing.
     *
     * @return A single line of text representing the entirety of the JSON document's contents.
     * @throws IOException
     *             If any errors occur during the read-in.
     */
    private String retrieveJsonData() throws IOException {
        final StringBuilder builder = new StringBuilder();

        for (String line : readFile(getLocalDataLocation())) {
            line = line.trim();
            if (line.endsWith("\\")) {
                /*
                 * Count the number of slashes at the end - if odd, then it's a trailing slash and should be removed
                 */
                int slashParity = 1;
                final char[] characters = line.toCharArray();
                int characterIndex = characters.length - 2;
                while (characterIndex >= 0 && characters[characterIndex] == '\\') {
                    characterIndex--;
                    slashParity = 1 - slashParity;
                }

                if (slashParity == 1)
                    line = line.substring(0, line.length() - 1);
            }
            builder.append(line);
        }

        return builder.toString();
    }

    /**
     * Read the text from a file.
     * <br>
     * This method primarily serves to reduce the scope of the {@link SuppressWarnings} annotation.
     *
     * @param file
     *            A {@link File} object representing the file to be read.
     * @return A {@link List} of {@code String} objects representing the lines within the given file.
     * @throws IOException
     *             If any errors occur during the file read.
     */
    private List<String> readFile(final File file) throws IOException {
        return FileUtils.readLines(file, "utf-8");
    }
}
