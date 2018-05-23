package com.cerner.ccltesting.maven.ccl.util;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cerner.ccltesting.maven.ccl.util.LogOutputStreamProxy.LogProxy;

/**
 * Unit tests for {@link LogOutputStreamProxy}.
 *
 * @author Joshua Hyde
 *
 */
public class LogOutputStreamProxyTest {
    @Mock
    private LogProxy logProxy;

    /**
     * Pretest initialization
     */
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Verify that writing data works.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWrite() throws Exception {
        final List<String> lines = Arrays.asList("test_one", "test_two", "test_three");
        final LogOutputStreamProxy proxy = new LogOutputStreamProxy(logProxy);
        for (final String line : lines) {
            for (final char c : line.toCharArray())
                proxy.write(c);
            proxy.write('\n');
        }

        /*
         * Verify that the above lines - and ONLY the above lines - were proxied to the Log object
         */
        for (final String line : lines)
            verify(logProxy).log(line);

        verifyNoMoreInteractions(logProxy);
        proxy.close();
    }

    /**
     * Verify that flushing works.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testFlush() throws Exception {
        final LogOutputStreamProxy proxy = new LogOutputStreamProxy(logProxy);
        final String line = "i am a line";
        for (final char c : line.toCharArray())
            proxy.write(c);

        // Verify that nothing's been proxied to the log yet
        verifyNoMoreInteractions(logProxy);

        proxy.flush();
        verify(logProxy).log(line);
        verifyNoMoreInteractions(logProxy);
        proxy.close();
    }

    /**
     * Verify that close() flushes the buffer.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testClose() throws Exception {
        final LogOutputStreamProxy proxy = new LogOutputStreamProxy(logProxy);
        final String line = "i am a line";
        for (final char c : line.toCharArray())
            proxy.write(c);

        // Verify that nothing's been proxied to the log yet
        verifyNoMoreInteractions(logProxy);

        proxy.close();
        verify(logProxy).log(line);
        verifyNoMoreInteractions(logProxy);
    }
}
