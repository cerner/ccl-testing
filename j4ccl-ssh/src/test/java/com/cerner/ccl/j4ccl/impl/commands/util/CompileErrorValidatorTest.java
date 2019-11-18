package com.cerner.ccl.j4ccl.impl.commands.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.File;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.exception.CclCompilationException;
import com.cerner.ccl.j4ccl.impl.commands.util.ListingParser.CclError;
import com.cerner.ccl.j4ccl.impl.commands.util.ListingParser.ListingParseResult;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * Unit tests for {@link CompileErrorValidator}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(PointFactory.class)
public class CompileErrorValidatorTest {
    @Mock
    private ListingParser listingParser;
    private CompileErrorValidator validator;

    /**
     * Set up the validator for each test.
     */
    @Before
    public void setUp() {
        validator = new CompileErrorValidator(listingParser);
    }

    /**
     * Validate the getInstance returns a non-null singleton.
     */
    @Test
    public void testGetInstance() {
        final CompileErrorValidator instanceA = CompileErrorValidator.getInstance();
        final CompileErrorValidator instanceB = CompileErrorValidator.getInstance();
        assertThat(instanceA).isNotNull();
        assertThat(instanceA).isSameAs(instanceB);
    }

    /**
     * Construction with a {@code null} {@link ListingParser} should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructNullListingParser() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            new CompileErrorValidator(null);
        });
        assertThat(e.getMessage()).isEqualTo("Listing parser cannot be null.");
    }

    /**
     * If the validator reports no errors, then nothing should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testValidateNoErrors() throws Exception {
        final EtmPoint point = mock(EtmPoint.class);
        mockStatic(PointFactory.class);
        when(PointFactory.getPoint(CompileErrorValidator.class, "validate")).thenReturn(point);

        final File listingFile = mock(File.class);
        final ListingParseResult result = mock(ListingParseResult.class);
        when(listingParser.parseListing(listingFile)).thenReturn(result);
        validator.validate(listingFile);

        verify(point).collect();
    }

    /**
     * Validation of a {@code null} {@link File} should fail.
     */
    @Test
    public void testValidateNullFile() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            validator.validate(null);
        });
        assertThat(e.getMessage()).isEqualTo("File cannot be null.");
    }

    /**
     * If the validator reports an error, an exception should be thrown.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testValidateWithErrors() throws Exception {
        final File listingFile = mock(File.class);
        final ListingParseResult result = mock(ListingParseResult.class);
        when(listingParser.parseListing(listingFile)).thenReturn(result);

        final String errorMessage = "oh noes!";
        final CclError error = mock(CclError.class);
        when(error.getMessage()).thenReturn(errorMessage);
        when(result.getErrors()).thenReturn(Collections.singleton(error));

        CclCompilationException e = assertThrows(CclCompilationException.class, () -> {
            validator.validate(listingFile);
        });
        assertThat(e.getMessage()).isEqualTo("Failure to compile code: " + errorMessage);
    }
}
