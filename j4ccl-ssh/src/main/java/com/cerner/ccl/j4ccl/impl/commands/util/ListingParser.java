package com.cerner.ccl.j4ccl.impl.commands.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * An object to parse a CCL listing for warnings and errors.
 *
 * @author Joshua Hyde
 *
 */

public class ListingParser {
    private static final Pattern LINE_NUM_PATTERN = Pattern.compile("[0-9][0-9]*\\).*");
    private static final ListingParser INSTANCE = new ListingParser();

    /**
     * Get a singleton instance of {@link ListingParser}.
     *
     * @return A singleton instance of {@link ListingParser}.
     */
    public static ListingParser getInstance() {
        return INSTANCE;
    }

    /**
     * Read in the file and parse out the contents of the listing, looking for errors and warnings.
     *
     * @param listing
     *            A {@link File} object representing the location of the listing file to be parsed.
     * @return A {@link ListingParseResult} object representing the result of the parsing.
     * @throws IOException
     *             If any errors occur while reading the file.
     * @throws NullPointerException
     *             If the given file is {@code null}.
     */
    public ListingParseResult parseListing(final File listing) throws IOException {
        if (listing == null)
            throw new NullPointerException("Listing cannot be null.");

        final EtmPoint point = PointFactory.getPoint(getClass(), "parseListing");
        final Collection<CclError> errors = new ArrayList<CclError>();
        final Collection<CclWarning> warnings = new ArrayList<CclWarning>();
        try {
            for (final Iterator<String> it = readFile(listing).iterator(); it.hasNext();) {
                final String line = it.next();
                if (!LINE_NUM_PATTERN.matcher(line).matches()) {
                    if (line.startsWith("%CCL-E"))
                        errors.add(new CclError(parseMessage(line, it)));
                    else if (line.startsWith("%CCL-W"))
                        warnings.add(new CclWarning(parseMessage(line, it)));
                }
            }
        } finally {
            point.collect();
        }

        return new ListingParseResult(warnings, errors);
    }

    /**
     * Parse the CCL-W or CCL-E message from the given reader.
     *
     * @param currentLine
     *            The line currently being read. This usually is the line that contains the "%CCL-E" or "%CCL-W" text
     *            keyword.
     * @param iterator
     *            A {@link Iterator} pointing to a collection of {@code String} objects.
     * @return A {@code String} representing the entirety of the CCL-E or CCL-W message.
     * @throws IOException
     *             If any errors occur while reading the listing.
     */
    private String parseMessage(final String currentLine, final Iterator<String> iterator) throws IOException {
        final Collection<String> lines = readMessage(iterator);
        final StringBuilder builder = new StringBuilder(currentLine);
        for (final String line : lines)
            builder.append(String.format("%n%s", line));

        return builder.toString();
    }

    /**
     * Read the lines from a file. <br>
     * This method exists primarily to reduce the scope of the {@link SuppressWarnings} annotation.
     *
     * @param file
     *            A {@link File} object representing the file whose lines are to be read.
     * @return A {@link List} of {@code String} objects representing the lines of the given file.
     * @throws IOException
     *             If any errors occur during the file read-in.
     */
    private List<String> readFile(final File file) throws IOException {
        return FileUtils.readLines(file, "utf-8");
    }

    /**
     * Read the remaining message from a listing.
     *
     * @param iterator
     *            A {@link Iterator} pointing to a collection of {@code String} objects.
     * @return A {@link Collection} of {@link String} objects representing any remaining lines of the error or warning
     *         message.
     * @throws IOException
     *             If any errors occur while reading the listing.
     */
    private Collection<String> readMessage(final Iterator<String> iterator) throws IOException {
        final List<String> lines = new ArrayList<String>();
        String currentLine = null;
        while (iterator.hasNext()) {
            currentLine = iterator.next();
            if (currentLine.trim().length() > 0 && !LINE_NUM_PATTERN.matcher(currentLine).matches())
                lines.add(currentLine);
            else
                break;
        }

        return lines;
    }

    /**
     * An object representing a CCL error that was encountered.
     *
     * @author Joshua Hyde
     *
     */

    public static class CclError {
        private final String message;

        /**
         * Construct a CCL error data object.
         *
         * @param message
         *            The message associated with the CCL error.
         */
        public CclError(final String message) {
            this.message = message;
        }

        /**
         * Get the message associated with the error.
         *
         * @return The message.
         */
        public String getMessage() {
            return message;
        }
    }

    /**
     * A data object to represent any CCL-W messages.
     *
     * @author Joshua Hyde
     *
     */
    public static class CclWarning {
        private final String message;

        /**
         * Create a warning out of a message.
         *
         * @param message
         *            The message communicating the warning.
         */
        public CclWarning(final String message) {
            this.message = message;
        }

        /**
         * Get the warning's message.
         *
         * @return The message that is the warning.
         */
        public String getMessage() {
            return message;
        }
    }

    /**
     * A bean representing the data parsed from a CCL compilation listing.
     *
     * @author Joshua Hyde
     *
     */
    public static class ListingParseResult {
        private final Collection<CclWarning> warnings;
        private final Collection<CclError> errors;

        /**
         * Create a parsing result.
         *
         * @param warnings
         *            A {@link Collection} of {@link CclWarning} objects representing all of the {@code CCL-W} warnings
         *            found within the listing.
         * @param errors
         *            A {@link Collection} of {@link CclError} objects representing all of the {@code CCL-E} errors
         *            found within the listing.
         */
        public ListingParseResult(final Collection<CclWarning> warnings, final Collection<CclError> errors) {
            this.warnings = warnings;
            this.errors = errors;
        }

        /**
         * Get any errors found in the listing.
         *
         * @return A {@link Collection} of {@link CclError} objects representing any errors found in the listing file.
         */
        public Collection<CclError> getErrors() {
            return errors;
        }

        /**
         * Get any warnings found in the listing.
         *
         * @return A {@link Collection} of {@link CclWarning} objects representing any warnings found in the listing
         *         file.
         */
        public Collection<CclWarning> getWarnings() {
            return warnings;
        }
    }
}
