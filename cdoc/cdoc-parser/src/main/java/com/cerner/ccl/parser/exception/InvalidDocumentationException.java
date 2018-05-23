package com.cerner.ccl.parser.exception;

/**
 * An exception to indicate that documentation is malformed.
 * 
 * @author Joshua Hyde
 * 
 */

public class InvalidDocumentationException extends CDocParsingException {
    private static final long serialVersionUID = -817806435499348430L;

    /**
     * Create an exception.
     * 
     * @param message
     *            The message associated with the exception.
     */
    public InvalidDocumentationException(final String message) {
        super(message);
    }
}
