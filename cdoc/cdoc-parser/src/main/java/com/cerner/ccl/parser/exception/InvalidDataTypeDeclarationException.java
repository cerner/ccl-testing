package com.cerner.ccl.parser.exception;

/**
 * An exception to represent when an invalid data type declaration is encountered.
 * 
 * @author Joshua Hyde
 * 
 */

public class InvalidDataTypeDeclarationException extends CDocParsingException {
    private static final long serialVersionUID = 7267036187508242508L;

    /**
     * Create an exception.
     * 
     * @param message
     *            The message associated with the exception.
     */
    public InvalidDataTypeDeclarationException(final String message) {
        super(message);
    }

}
