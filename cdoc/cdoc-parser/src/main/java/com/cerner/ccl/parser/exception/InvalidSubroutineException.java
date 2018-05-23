package com.cerner.ccl.parser.exception;

/**
 * An exception indicating that a subroutine declaration or definition is syntactically invalid.
 * 
 * @author Joshua Hyde
 * 
 */

public class InvalidSubroutineException extends CDocParsingException {
    private static final long serialVersionUID = 1755772539595462630L;

    /**
     * Create an exception.
     * 
     * @param message
     *            The message associated with the exception.
     */
    public InvalidSubroutineException(final String message) {
        super(message);
    }
}
