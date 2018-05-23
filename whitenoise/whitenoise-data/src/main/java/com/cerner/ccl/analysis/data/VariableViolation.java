package com.cerner.ccl.analysis.data;

/**
 * A violation specific to a variable.
 * 
 * @author Joshua Hyde
 * 
 */

public interface VariableViolation extends Violation {
    /**
     * Get the name of the variable participating in the violation.
     * 
     * @return The name of the variable.
     */
    String getVariableName();
}
