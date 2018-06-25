package com.cerner.ccl.analysis.data;

/**
 * A violation of a static analysis rule.
 * 
 * @author Joshua Hyde
 * 
 */

public interface Violation {
    /**
     * Get the number of the line within the compiled CCL code at which this appears.
     * 
     * @return {@code null} if insufficient line number information was available, or a line number is inapplicable to
     *         this violation; otherwise, an {@link Integer} representing the line within the CCL program - after the
     *         in-line inclusion of include files - at which this violation appears.
     */
    Integer getLineNumber();

    /**
     * Get a brief description of the violation.
     * 
     * @return A brief description of the violation.
     */
    String getViolationDescription();

    /**
     * Get an explanation of the violation, explaining why this violation should concern a user.
     * 
     * @return An explanation of the violation.
     */
    String getViolationExplanation();

    /**
     * Get the unique identifier of this violation.
     * 
     * @return The {@link ViolationId} object uniquely identifying this violation.
     */
    ViolationId getViolationId();
}
