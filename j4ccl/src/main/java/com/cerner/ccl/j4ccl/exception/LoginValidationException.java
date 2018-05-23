package com.cerner.ccl.j4ccl.exception;

/**
 * An exception to indicate that a login validation has failed.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("serial")
public class LoginValidationException extends CclException {
    /**
     * Create an exception with a given message and cause.
     *
     * @param message
     *            The message associated with the exception.
     * @param t
     *            The {@link Throwable} identified as the cause of the exception.
     */
    public LoginValidationException(final String message, final Throwable t) {
        super(message, t);
    }

    /**
     * Create an exception with a given message.
     *
     * @param message
     *            The message associated with the exception.
     */
    public LoginValidationException(final String message) {
        super(message);
    }
}
