package com.cerner.ccl.j4ccl.exception;

/**
 * An exception to indicate that a compilation attempt has exceeded its maximum amount of time.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("serial")
public class CclCompilationTimeoutException extends CclException {
    /**
     * Create an exception with a message.
     *
     * @param message
     *            The message associated with the exception.
     */
    public CclCompilationTimeoutException(final String message) {
        super(message);
    }

    /**
     * Create an exception with the given message and cause.
     *
     * @param message
     *            The message associated with the exception.
     * @param t
     *            The {@link Throwable} identified as the cause of the exception.
     */
    public CclCompilationTimeoutException(final String message, final Throwable t) {
        super(message, t);
    }

}
