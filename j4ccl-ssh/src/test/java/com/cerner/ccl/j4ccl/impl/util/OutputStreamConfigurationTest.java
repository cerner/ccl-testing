package com.cerner.ccl.j4ccl.impl.util;

import static org.fest.assertions.Assertions.assertThat;

import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.cerner.ccl.j4ccl.enums.OutputType;
import com.cerner.ccl.j4ccl.internal.AbstractUnitTest;

/**
 * Unit tests for {@link OutputStreamConfiguration}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class OutputStreamConfigurationTest extends AbstractUnitTest {
    private final OutputType type = OutputType.CCL_SESSION;
    @Mock
    private OutputStream stream;
    private OutputStreamConfiguration configuration;

    /**
     * Set up the configuration for each test.
     */
    @Before
    public void setUp() {
        configuration = new OutputStreamConfiguration(stream, type);
    }

    /**
     * Construction with a {@code null} {@link OutputStream} should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructNullOutputStream() {
        expect(NullPointerException.class);
        expect("Output stream cannot be null.");
        new OutputStreamConfiguration(null, type);
    }

    /**
     * Construction with a {@code null} {@link OutputType} should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructNullOutputType() {
        expect(NullPointerException.class);
        expect("Output type cannot be null.");
        new OutputStreamConfiguration(stream, null);
    }

    /**
     * Test the retrieval of the output stream.
     */
    @Test
    public void testGetOutputStream() {
        assertThat(configuration.getOutputStream()).isEqualTo(stream);
    }

    /**
     * Test the retrieval of the output type.
     */
    @Test
    public void testGetOutputType() {
        assertThat(configuration.getOutputType()).isEqualTo(type);
    }

}
