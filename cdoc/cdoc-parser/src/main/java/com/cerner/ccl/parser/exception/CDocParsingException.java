package com.cerner.ccl.parser.exception;

/**
 * A contribution to project hierarchy in order to facilitate the catching of all exceptions that could possibly be
 * thrown by this API.
 * 
 * @author Joshua Hyde
 * 
 */

@SuppressWarnings("serial")
public abstract class CDocParsingException extends RuntimeException {
    /**
     * Create an exception.
     * 
     * @param message
     *            The message associated with the exception.
     */
    public CDocParsingException(final String message) {
        super(message);
    }
}
