package com.cerner.ftp.exception;

/**
 * An exception to represent when an error occurs during a file transfer.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("serial")
public class TransferException extends RuntimeException {
    /**
     * Create an exception with the given message and the given throwable.
     *
     * @param message
     *            The message associated with this exception.
     * @param t
     *            The {@link Throwable} identified as the cause of this exception.
     */
    public TransferException(final String message, final Throwable t) {
        super(message);
        initCause(t);
    }
}
