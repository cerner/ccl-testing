package com.cerner.ccltesting.maven.ccl.reports.internal;

/**
 * A factory to create {@link AssertionTest} objects. This facilitates the creation of an in-sequence set of assertions, handling the tracking of line numbers between created assertions; that is, to
 * say, that the first assertion created by this factory will have a line number of "1", and the next will have "2", et cetera, et cetera.
 * <br>
 * The tracking of line numbers is <b>not</b> global; each sequence of line numbers is tied to each instance of the factory.
 * 
 * @author Joshua Hyde
 * 
 */

public class AssertionFactory {
    private int lineNumber = 1;

    /**
     * Create an errored assertion.
     * 
     * @param context
     *            The context of the assertion.
     * @param testExecuted
     *            The actual test executed.
     * @return A {@link AssertionTest} representing an errored assertion.
     */
    public AssertionTest erroredAssertion(String context, String testExecuted) {
        return createAssertion(context, testExecuted, ExecutionResult.ERRORED);
    }

    /**
     * Create a failed assertion.
     * 
     * @param context
     *            The context of the assertion.
     * @param testExecuted
     *            The actual test executed.
     * @return A {@link AssertionTest} representing a failed assertion.
     */
    public AssertionTest failedAssertion(String context, String testExecuted) {
        return createAssertion(context, testExecuted, ExecutionResult.FAILED);
    }

    /**
     * Create a passed assertion.
     * 
     * @param context
     *            The context of the assertion.
     * @param testExecuted
     *            The actual test executed.
     * @return A {@link AssertionTest} representing a passed assertion.
     */
    public AssertionTest passedAssertion(String context, String testExecuted) {
        return createAssertion(context, testExecuted, ExecutionResult.PASSED);
    }

    /**
     * Create an assertion.
     * 
     * @param context
     *            The context of the assertion.
     * @param testExecuted
     *            The actual test executed.
     * @param result
     *            An {@link ExecutionResult} enum representing the result of the assertion.
     * @return An {@link AssertionTest} composed of the given data.
     */
    private AssertionTest createAssertion(String context, String testExecuted, ExecutionResult result) {
        return new AssertionTest(lineNumber++, context, testExecuted, result);
    }
}
