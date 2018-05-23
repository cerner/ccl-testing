package com.cerner.ccl.analysis.exception;

/**
 * An exception to indicate that an error occurred during the translation of a script.
 * 
 * @author Joshua Hyde
 * 
 */

public class TranslationException extends AnalysisException {
    private static final long serialVersionUID = 6835108621200359983L;

    /**
     * Create an exception.
     * 
     * @param message
     *            The message associated with the exception.
     */
    public TranslationException(final String message) {
        super(message);
    }
}
