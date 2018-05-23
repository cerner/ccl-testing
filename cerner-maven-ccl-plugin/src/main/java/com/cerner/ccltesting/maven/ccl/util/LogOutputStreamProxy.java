package com.cerner.ccltesting.maven.ccl.util;

import java.io.OutputStream;

import org.apache.maven.plugin.logging.Log;

/**
 * An output stream that proxies its input to {@link Log#debug(CharSequence)}.
 *
 * @author Joshua Hyde
 *
 */

public class LogOutputStreamProxy extends OutputStream {
    /**
     * An interface that provides an anonymous method of injecting the desired behavior of the logger into this proxy.
     * For example, the injector may want to use {@link Log#info(CharSequence)} or {@link Log#debug(CharSequence)}; this
     * interface allows for the usage of either with this stream proxy.
     *
     * @author Joshua Hyde
     *
     */
    public interface LogProxy {
        /**
         * Get the logger in used by this proxy.
         *
         * @return A {@link Log} representing the log in use by this proxy.
         */
        Log getLog();

        /**
         * Log a string of text.
         *
         * @param text
         *            The text to be logged.
         */
        void log(String text);
    }

    private static final char DELIMITER = '\n';
    private final StringBuilder builder = new StringBuilder();
    private final LogProxy logProxy;

    /**
     * Create a proxy that outputs to the given log's debug output.
     *
     * @param logProxy
     *            A {@link LogProxy} object that defines how the log output should be handled.
     */
    public LogOutputStreamProxy(final LogProxy logProxy) {
        this.logProxy = logProxy;
    }

    @Override
    public void close() {
        // Make sure that nothing is left in the buffer
        flush();
    }

    @Override
    public void flush() {
        if (builder.length() > 0) {
            log(builder);
        }
    }

    /**
     * Get the log in use by this proxy.
     *
     * @return A {@link LogProxy} object representing the log proxy in use by this proxy.
     */
    public LogProxy getLogProxy() {
        return logProxy;
    }

    @Override
    public void write(final int b) {
        final char current = (char) b;
        if (b == DELIMITER)
            log(builder);
        else
            builder.append(current);
    }

    /**
     * Log data out to the log proxy.
     *
     * @param builder
     *            A {@link StringBuilder} representing the entirety of the data to be logged.
     */
    private void log(final StringBuilder builder) {
        logProxy.log(builder.toString());
        builder.delete(0, builder.length());
    }

}
