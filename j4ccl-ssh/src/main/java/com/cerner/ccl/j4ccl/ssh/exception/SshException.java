package com.cerner.ccl.j4ccl.ssh.exception;

/**
 * An exception to indicate that an error occurred during an SSH action.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("serial")
public class SshException extends Exception {
    /**
     * Create an exception with the associated message and exception as its cause.
     *
     * @param message
     *            The message associated with the exception.
     * @param t
     *            The {@link Throwable} identified as the cause of this exception.
     */
    public SshException(final String message, final Throwable t) {
        super(message, t);
    }

    /**
     * Create an exception with the given message.
     *
     * @param message
     *            The message associated with the exception.
     */
    public SshException(final String message) {
        super(message);
    }
}
