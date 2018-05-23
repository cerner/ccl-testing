package com.cerner.ccl.j4ccl.exception;

/**
 * An exception to represent that an error has occurred while attempting to execute a CCL command.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("serial")
public class CclCommandException extends CclException {

    /**
     * Create an exception with the associated message and exception as its cause.
     *
     * @param message
     *            The message associated with the exception.
     * @param t
     *            The {@link Throwable} identified as the cause of this exception.
     */
    public CclCommandException(final String message, final Throwable t) {
        super(message, t);
    }

    /**
     * Create an exception with the given message.
     *
     * @param message
     *            The message associated with the exception.
     */
    public CclCommandException(final String message) {
        super(message);
    }

}
