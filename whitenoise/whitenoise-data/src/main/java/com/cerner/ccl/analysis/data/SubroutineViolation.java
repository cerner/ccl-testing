package com.cerner.ccl.analysis.data;

/**
 * A violation specific to a subroutine.
 * 
 * @author Joshua Hyde
 * 
 */

public interface SubroutineViolation extends Violation {
    /**
     * Get the name of the affected subroutine.
     * 
     * @return The name of the subroutine that participates in the violation.
     */
    String getSubroutineName();
}
