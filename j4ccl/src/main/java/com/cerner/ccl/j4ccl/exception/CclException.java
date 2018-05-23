package com.cerner.ccl.j4ccl.exception;

/**
 * Generic definition of any exception related to CCL actions.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("serial")
public abstract class CclException extends RuntimeException {
    /**
     * Create an exception with a given message and cause.
     *
     * @param message
     *            The message associated with the exception.
     * @param t
     *            The {@link Throwable} identified as the cause of the exception.
     */
    public CclException(final String message, final Throwable t) {
        super(message, t);
    }

    /**
     * Create an exception with a given message.
     *
     * @param message
     *            The message associated with the exception.
     */
    public CclException(final String message) {
        super(message);
    }
}
