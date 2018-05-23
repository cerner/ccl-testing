package com.cerner.ccl.analysis.exception;

/**
 * An exception to indicate that an error occurred during analysis.
 * 
 * @author Joshua Hyde
 * 
 */

public class AnalysisException extends RuntimeException {
    private static final long serialVersionUID = 6607775944451210445L;

    /**
     * Create an exception.
     * 
     * @param message
     *            The message associated with the exception.
     */
    public AnalysisException(final String message) {
        super(message);
    }
}
