package com.cerner.ccl.j4ccl.impl.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;

import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Unit tests for {@link OutputStreamProxy}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class OutputStreamProxyTest {
    @Mock
    private OutputStream proxied;
    private OutputStreamProxy proxy;

    /**
     * Set up the proxy for each test.
     */
    @Before
    public void setUp() {
        proxy = new OutputStreamProxy(proxied);
    }

    /**
     * Construction with a {@code null} {@link OutputStream} should fail.
     */
    @SuppressWarnings({ "unused" })
    @Test
    public void testConstructNullOutputStream() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            new OutputStreamProxy(null);
        });
        assertThat(e.getMessage()).isEqualTo("Output stream cannot be null.");
    }

    /**
     * Test the retrieval of the proxied output stream.
     */
    @Test
    public void testGetProxied() {
        assertThat(proxy.getProxied()).isEqualTo(proxied);
    }

    /**
     * Test the writing of a byte.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWrite() throws Exception {
        proxy.write(2);
        verify(proxied).write(2);
    }
}
