package com.cerner.ccl.j4ccl.enums;

import java.io.OutputStream;

import com.cerner.ccl.j4ccl.CclExecutor;

/**
 * Enumerations of the possible output.
 *
 * @see CclExecutor#setOutputStream(OutputStream,OutputType)
 * @author Joshua Hyde
 *
 */

public enum OutputType {
    /**
     * Output only content from the CCL session.
     */
    CCL_SESSION,
    /**
     * Output all content.
     */
    FULL_DEBUG;
}
