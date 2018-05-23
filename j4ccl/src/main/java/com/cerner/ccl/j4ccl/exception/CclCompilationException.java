package com.cerner.ccl.j4ccl.exception;

/**
 * An exception to represent that an error occurred during compilation.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("serial")
public class CclCompilationException extends CclException {

    /**
     * Create an exception with the associated message and exception as its cause.
     *
     * @param message
     *            The message associated with the exception.
     * @param t
     *            The {@link Throwable} identified as the cause of this exception.
     */
    public CclCompilationException(final String message, final Throwable t) {
        super(message, t);
    }

    /**
     * Create an exception with the given message.
     *
     * @param message
     *            The message associated with the exception.
     */
    public CclCompilationException(final String message) {
        super(message);
    }
}
