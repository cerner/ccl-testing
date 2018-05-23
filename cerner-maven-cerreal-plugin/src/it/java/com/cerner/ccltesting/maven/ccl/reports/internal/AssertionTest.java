package com.cerner.ccltesting.maven.ccl.reports.internal;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A bean representing an assertion executed within a CCL Testing Framework test.
 * 
 * @author Joshua Hyde
 * 
 */

public class AssertionTest {
    private final int lineNumber;
    private final String context;
    private final String testExecuted;
    private final ExecutionResult result;
    private final String sourceCode;
    private String errorText;

    /**
     * Create an assertion.
     * 
     * @param lineNumber
     *            The line number of the assertion.
     * @param context
     *            The context of the assertion.
     * @param testExecuted
     *            The actual test that was executed.
     * @param result
     *            The result of the test.
     */
    public AssertionTest(int lineNumber, String context, String testExecuted, ExecutionResult result) {
        this.lineNumber = lineNumber;
        this.context = context;
        this.testExecuted = testExecuted;
        this.result = result;
        this.sourceCode = "assertTrue(" + testExecuted + ")";
    }

    /**
     * Get the line number of the assertion.
     * 
     * @return The line number of the assertion.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Get the context of the assertion.
     * 
     * @return The context of the assertion.
     */
    public String getContext() {
        return context;
    }

    /**
     * Get the error text, if any.
     * 
     * @return {@code null} if no error text has been set; otherwise, the text of the error message.
     */
    public String getErrorText() {
        return errorText;
    }

    /**
     * Get the result of the assertion.
     * 
     * @return An {@link ExecutionResult} enum representing the result of the test.
     */
    public ExecutionResult getResult() {
        return result;
    }

    /**
     * Get the source code of this assertion.
     * 
     * @return The source code of the assertion.
     */
    public String getSourceCode() {
        return sourceCode;
    }

    /**
     * Get the actual test that was executed.
     * 
     * @return The actual test that was executed.
     */
    public String getTestExecuted() {
        return testExecuted;
    }

    /**
     * Set the error text.
     * 
     * @param errorText
     *            The error text.
     */
    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
