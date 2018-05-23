package com.cerner.ccl.j4ccl.impl.commands.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.impl.commands.util.ListingParser.CclError;
import com.cerner.ccl.j4ccl.impl.commands.util.ListingParser.CclWarning;
import com.cerner.ccl.j4ccl.impl.commands.util.ListingParser.ListingParseResult;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * Unit test of {@link ListingParser}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(PointFactory.class)
public class ListingParserTest {
    /**
     * A {@link Rule} to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    private final ListingParser parser = new ListingParser();

    /**
     * Test the parsing of a listing for CCL errors.
     *
     * @throws Exception
     *             If any errors occur while running the test.
     */
    @Test
    public void testListingParserErrors() throws Exception {
        final EtmPoint point = mock(EtmPoint.class);
        mockStatic(PointFactory.class);
        when(PointFactory.getPoint(ListingParser.class, "parseListing")).thenReturn(point);

        final File listingFile = new File("target/unit/test_listing.out");
        final String firstError = "%CCL-E-6-CCL(2,12)S0L0.0l1{}Unexpected end of line, symbol must start and end on same line.";
        final List<String> secondError = Arrays.asList("%CCL-E-9-CCL(2,12)S0L0.0p2{<ILLC>}Unexpected symbol found.",
                "<ILLC>", "Found <ILLC>", "Expecting  <INT> <NAME> <REAL> <STRING> $ ( - + IF");

        FileUtils.deleteQuietly(listingFile);

        // Generate the listing file
        final List<String> output = new ArrayList<String>();
        output.add("1) drop program blech go");
        output.add(firstError);
        output.add("");
        output.add("2) set blah = 'bleu'");
        output.addAll(secondError);
        FileUtils.writeLines(listingFile, output);

        final ListingParseResult parseResult = parser.parseListing(listingFile);
        assertThat(parseResult.getWarnings()).isEmpty();

        final Collection<? extends CclError> errors = parseResult.getErrors();
        assertThat(errors).hasSize(2);
        final Iterator<? extends CclError> errorsIt = errors.iterator();

        assertThat(errorsIt.next().getMessage()).isEqualTo(firstError);

        final StringBuilder messageBuilder = new StringBuilder(secondError.size());
        final Iterator<String> secondErrorIt = secondError.iterator();
        messageBuilder.append(secondErrorIt.next());
        while (secondErrorIt.hasNext())
            messageBuilder.append(String.format("%n%s", secondErrorIt.next()));

        assertThat(errorsIt.next().getMessage()).isEqualTo(messageBuilder.toString().trim());
        verify(point).collect();
    }

    /**
     * Test that the parser correctly reports if no errors or warnings are found.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testListingParserNone() throws Exception {
        final List<String> lines = Arrays.asList("1) drop program blech go", "2) create program blech",
                "3) call echo('hello')", "4) end go");
        final File listingFile = new File("target/unit/listing.out");

        FileUtils.deleteQuietly(listingFile);
        FileUtils.writeLines(listingFile, lines);

        final ListingParseResult parseResult = parser.parseListing(listingFile);
        assertThat(parseResult.getErrors()).isEmpty();
        assertThat(parseResult.getWarnings()).isEmpty();
    }

    /**
     * Test that warnings are properly parsed.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testListingParserWarnings() throws Exception {
        final File listingFile = new File("target/unit/test_listing.out");
        final String warning = "%CCL-W-44-CCL(1,15) SAMPLE_PROGRAM cannot be dropped - it does not exist";

        FileUtils.deleteQuietly(listingFile);

        // Generate the listing file
        final List<String> output = new ArrayList<String>();
        output.add("1) drop program blech go");
        output.add(warning);
        output.add("");
        output.add("end go");
        FileUtils.writeLines(listingFile, output);

        final ListingParseResult parseResult = parser.parseListing(listingFile);
        assertThat(parseResult.getErrors()).isEmpty();

        final Collection<? extends CclWarning> warnings = parseResult.getWarnings();
        assertThat(warnings).hasSize(1);
        final Iterator<? extends CclWarning> it = warnings.iterator();

        assertThat(it.next().getMessage()).isEqualTo(warning);
    }

    /**
     * Verify that parser fails if given a {@code null} reference for the file.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testNullFile() throws Exception {
        expected.expect(NullPointerException.class);
        expected.expectMessage("Listing cannot be null.");
        parser.parseListing(null);
    }
}