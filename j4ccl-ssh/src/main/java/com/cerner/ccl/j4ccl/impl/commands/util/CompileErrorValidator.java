package com.cerner.ccl.j4ccl.impl.commands.util;

import java.io.File;
import java.io.IOException;

import com.cerner.ccl.j4ccl.exception.CclCommandException;
import com.cerner.ccl.j4ccl.exception.CclCompilationException;
import com.cerner.ccl.j4ccl.impl.commands.util.ListingParser.ListingParseResult;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * An object that validates that no errors are found within a compilation's listing output.
 *
 * @author Joshua Hyde
 *
 */

public class CompileErrorValidator {
    private static final CompileErrorValidator INSTANCE = new CompileErrorValidator(ListingParser.getInstance());

    /**
     * Get a singleton instance of the validator.
     *
     * @return A {@link CompileErrorValidator}.
     */
    public static CompileErrorValidator getInstance() {
        return INSTANCE;
    }

    private final ListingParser listingParser;

    /**
     * Create a validator to check a listing output for errors.
     * <br>
     * This constructor is intentionally left package-private to expose it for unit testing while obfuscating it from
     * consumers.
     *
     * @param listingParser
     *            A {@link ListingParser} used to parse the data from the given listing file.
     * @throws NullPointerException
     *             If the given listing parser is {@code null}.
     */
    CompileErrorValidator(final ListingParser listingParser) {
        if (listingParser == null)
            throw new NullPointerException("Listing parser cannot be null.");

        this.listingParser = listingParser;
    }

    /**
     * Validate that no errors are found in the given listing.
     *
     * @param listingFile
     *            A {@link File} representing the compilation output to be scanned for CCL errors.
     * @throws CclCompilationException
     *             If any errors are found in the listing.
     * @throws NullPointerException
     *             If the given file is {@code null}.
     */
    public void validate(final File listingFile) {
        if (listingFile == null)
            throw new NullPointerException("File cannot be null.");

        final EtmPoint point = PointFactory.getPoint(getClass(), "validate");
        try {
            ListingParseResult parserResult;
            try {
                parserResult = listingParser.parseListing(listingFile);
            } catch (final IOException e) {
                throw new CclCommandException(
                        "Failed to parse listing data from file: " + listingFile.getAbsolutePath(), e);
            }
            if (!parserResult.getErrors().isEmpty())
                throw new CclCompilationException(
                        "Failure to compile code: " + parserResult.getErrors().iterator().next().getMessage());
        } finally {
            point.collect();
        }
    }

}
