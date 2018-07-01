package com.cerner.ccl.testing.maven.ccl.reports.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.cerner.ccl.testing.maven.ccl.reports.common.CCLProgram.ProgramLine;

/**
 * This represents a single line of coverage within a CCL script's coverage report.
 * 
 * @author Jeff Wiedemann
 * 
 */
public class CoverageLine {
    // TODO: just use the program name instead of the entire program object
    private final Map<CCLCoverageProgram, CoveredStatus> coverage = new HashMap<CCLCoverageProgram, CoveredStatus>();
    private final ProgramLine line;

    /**
     * Create a line coverage.
     * 
     * @param line
     *            The {@link ProgramLine} that was covered.
     * @throws IllegalArgumentException
     *             If the given line is {@code null}.
     */
    public CoverageLine(ProgramLine line) {
        if (line == null)
            throw new IllegalArgumentException("Line cannot be null.");

        this.line = line;
    }

    /**
     * Add test coverage data.
     * 
     * @param program
     *            A {@link CCLCoverageProgram} representing the test program whose coverage of this line is to be added.
     * 
     * @param status
     *            A {@link CoveredStatus} enum representing the coverage of this line by the given test.
     * @throws IllegalArgumentException
     *             If the given program or status is {@code null}.
     */
    public void addTestCoverage(CCLCoverageProgram program, CoveredStatus status) {
        if (program == null)
            throw new IllegalArgumentException("Coverage program cannot be null.");

        if (status == null)
            throw new IllegalArgumentException("The covered status cannot be null.");

        coverage.put(program, status);
    }

    /**
     * Get the aggregation of the statuses.
     * 
     * @return A {@link CoveredStatus} representing the aggregation of all coverages of this line.
     */
    public CoveredStatus getAggregateCoveredStatus() {
        boolean isNotCovered = false;
        for (CoveredStatus status : coverage.values()) {
            // If ever the line is marked as not executable, it's not executable
            if (status == CoveredStatus.NOT_EXECUTABLE)
                return CoveredStatus.NOT_EXECUTABLE;

            // If ever the line was covered, indicate covered
            if (status == CoveredStatus.COVERED)
                return CoveredStatus.COVERED;

            isNotCovered |= CoveredStatus.NOT_COVERED.equals(status);
        }

        // If the line was never covered, and never not executable and there exists a time when it was not covered...
        // it's not covered
        if (isNotCovered)
            return CoveredStatus.NOT_COVERED;

        // If it's none of the other statuses... it's not defined
        return CoveredStatus.UNDEFINED;
    }

    /**
     * Get the coverage data for this line.
     * 
     * @return A {@link Map}. The keys are each test script that has covered this line; the values are the coverage of
     *         this line.
     */
    public Map<CCLCoverageProgram, CoveredStatus> getCoverage() {
        return Collections.unmodifiableMap(coverage);
    }

    /**
     * Determine whether or not the given test case covered this line.
     * 
     * @param testCase
     *            The {@link CCLCoverageProgram} for which coverage is to be determined.
     * @return A {@link CoveredStatus} enum; if the given test case is {@code null}, then this is the aggregate covered
     *         status; otherwise, this represents the coverage of this line by the given test case.
     */
    public CoveredStatus getCoveredStatusByTestCase(CCLCoverageProgram testCase) {
        if (testCase == null)
            return getAggregateCoveredStatus();

        return coverage.get(testCase);
    }

    /**
     * Get the line number of the line.
     * 
     * @return The line number of the line.
     */
    public int getLineNumber() {
        return line.getLineNumber();
    }

    /**
     * Get the source code of the line.
     * 
     * @return The source code of the line.
     */
    public String getSourceCode() {
        return line.getSourceCode();
    }

    /**
     * Get the origin of the source code.
     * 
     * @return The origin of the source code.
     */
    public String getSourceCodeOrigin() {
        return line.getOrigin();
    }
}
