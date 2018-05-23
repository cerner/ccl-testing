package com.cerner.ccl.analysis.jdom;

import com.cerner.ccl.analysis.exception.AnalysisException;

/**
 * An {@link AnalysisException} representing that an error occurred while processing the data using JDOM.
 * 
 * @author Joshua Hyde
 */

public class JdomException extends AnalysisException {
    private static final long serialVersionUID = 745540392700492878L;

    /**
     * Create an exception.
     * 
     * @param message
     *            The message associated with the exception.
     * @param cause
     *            The {@link Throwable} cause of the exception.
     */
    public JdomException(final String message, final Throwable cause) {
        super(message);
        this.initCause(cause);
    }
}
