package com.cerner.ccl.testing.maven.ccl.reports.internal;

import java.util.Collection;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Definition of a test.
 * 
 * @author Joshua Hyde
 * 
 */

public class CclUnitTest {
    private final String testName;
    private final ExecutionResult result;
    private final Collection<AssertionTest> tests;

    /**
     * Create a test.
     * 
     * @param testName
     *            The name of the test.
     * @param tests
     *            A {@link Collection} of {@link AssertionTest} objects representing the tests that were ran.
     */
    public CclUnitTest(final String testName, final Collection<AssertionTest> tests) {
        this.testName = testName;
        this.tests = tests;

        ExecutionResult aggregateResult = ExecutionResult.PASSED;
        for (AssertionTest test : tests) {
            final ExecutionResult testResult = test.getResult();
            // If any assertion FAILED or ERRORED, then the test as a whole should have failed or errored
            if (!ExecutionResult.PASSED.equals(testResult)) {
                aggregateResult = testResult;
                break;
            }
        }

        this.result = aggregateResult;
    }

    /**
     * Get the result of the test.
     * 
     * @return The result of the test
     */
    public ExecutionResult getResult() {
        return result;
    }

    /**
     * Get the tests.
     * 
     * @return A {@link Collection} of {@link AssertionTest} objects representing the tests that were ran.
     */
    public Collection<AssertionTest> getTests() {
        return tests;
    }

    /**
     * Get the name of the test.
     * 
     * @return The name of the test.
     */
    public String getTestName() {
        return testName;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
