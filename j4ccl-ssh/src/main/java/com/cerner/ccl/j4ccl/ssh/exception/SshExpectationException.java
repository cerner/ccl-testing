package com.cerner.ccl.j4ccl.ssh.exception;

/**
 * An exception to indicate that an SSH terminal execution has exceeded its maximum runtime.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("serial")
public class SshExpectationException extends SshException {
    /**
     * Create an exception with a message.
     *
     * @param message
     *            The message associated with the exception
     */
    public SshExpectationException(final String message) {
        super(message);
    }

}
