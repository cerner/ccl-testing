package com.cerner.ccl.j4ccl.impl.util;

import java.io.OutputStream;

import com.cerner.ccl.j4ccl.CclExecutor;
import com.cerner.ccl.j4ccl.enums.OutputType;

/**
 * A bean to hold the user-set {@link CclExecutor#setOutputStream(OutputStream, OutputType) output stream
 * configuration}.
 *
 * @author Joshua Hyde
 *
 */

public class OutputStreamConfiguration {
    private final OutputStream outputStream;
    private final OutputType outputType;

    /**
     * Create an output stream configuration.
     *
     * @param outputStream
     *            The user-set {@link OutputStream}.
     * @param outputType
     *            The {@link OutputType} enum representing the user-specified output type to be piped.
     */
    public OutputStreamConfiguration(final OutputStream outputStream, final OutputType outputType) {
        if (outputStream == null)
            throw new NullPointerException("Output stream cannot be null.");

        if (outputType == null)
            throw new NullPointerException("Output type cannot be null.");

        this.outputStream = outputStream;
        this.outputType = outputType;
    }

    /**
     * Get the output stream.
     *
     * @return The output stream.
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Get the output type.
     *
     * @return The output type.
     */
    public OutputType getOutputType() {
        return outputType;
    }
}
