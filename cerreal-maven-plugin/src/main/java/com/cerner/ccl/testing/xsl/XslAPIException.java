package com.cerner.ccl.testing.xsl;

/**
 * An exception to indicate that an error occurred during the transformation of XML by XSL.
 * 
 * @author Jeff Wiedemann
 * 
 */

public class XslAPIException extends Exception {
    private static final long serialVersionUID = 2302377641246733540L;

    /**
     * Other constructor.
     * 
     * @param msg
     *            the exception message.
     * @param e
     *            the exception.
     */
    public XslAPIException(String msg, Exception e) {
        super(msg, e);
    }
}
