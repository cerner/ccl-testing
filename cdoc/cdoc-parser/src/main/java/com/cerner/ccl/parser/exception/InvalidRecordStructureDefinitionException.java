package com.cerner.ccl.parser.exception;

/**
 * An exception representing an invalid record structure definition.
 * 
 * @author Joshua Hyde
 * 
 */

public class InvalidRecordStructureDefinitionException extends CDocParsingException {
    private static final long serialVersionUID = 7421980878269762439L;

    /**
     * Create an exception.
     * 
     * @param message
     *            The message associated with the exception.
     */
    public InvalidRecordStructureDefinitionException(final String message) {
        super(message);
    }
}
