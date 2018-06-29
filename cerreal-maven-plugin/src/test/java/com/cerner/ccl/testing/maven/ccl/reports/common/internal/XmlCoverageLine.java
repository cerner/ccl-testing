package com.cerner.ccl.testing.maven.ccl.reports.common.internal;

import com.cerner.ccl.testing.maven.ccl.reports.common.CoveredStatus;

/**
 * A representation of coverage data of a line in code by a test.
 * 
 * @author Joshua Hyde
 * 
 */
public class XmlCoverageLine {
    private final CoveredStatus status;
    private final int lineNumber;

    /**
     * Create coverage data.
     * 
     * @param status
     *            The {@link CoveredStatus test coverage} of a line.
     * @param lineNumber
     *            The line number of the line that was covered.
     */
    public XmlCoverageLine(CoveredStatus status, int lineNumber) {
        this.status = status;
        this.lineNumber = lineNumber;
    }

    /**
     * Get the line number.
     * 
     * @return The line number.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Get the status of the coverage of this line.
     * 
     * @return A {@link CoveredStatus} enum representing the test coverage of this line.
     */
    public CoveredStatus getStatus() {
        return status;
    }
}
