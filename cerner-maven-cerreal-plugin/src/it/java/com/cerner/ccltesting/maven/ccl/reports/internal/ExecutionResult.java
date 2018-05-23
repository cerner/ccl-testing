package com.cerner.ccltesting.maven.ccl.reports.internal;

/**
 * Enumerations of possible results of executions.
 * 
 * @author Joshua Hyde
 * 
 */

public enum ExecutionResult {
    /**
     * The execution errored.
     */
    ERRORED,
    /**
     * The execution failed.
     */
    FAILED,
    /**
     * The execution passed.
     */
    PASSED;
}
