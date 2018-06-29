package com.cerner.ccl.testing.maven.ccl.data;

import com.cerner.ccl.testing.maven.ccl.data.enums.AssertionStatus;

/**
 * An assertion within a CCL unit test.
 *
 * @author Joshua Hyde
 *
 */

public class Assertion {
    private final int lineNumber;
    private final String test;
    private final String context;
    private final AssertionStatus status;

    /**
     * Create a test failure.
     *
     * @param test
     *            The actual test that was performed as part of the assertion.
     * @param context
     *            The context in which the test was executed.
     * @param status
     *            The status of the assertion.
     * @param lineNumber
     *            The line number of the test that was executed.
     */
    public Assertion(final String test, final String context, final AssertionStatus status, final int lineNumber) {
        this.test = test;
        this.context = context;
        this.lineNumber = lineNumber;
        this.status = status;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null)
            return false;

        if (o == this)
            return true;

        if (!(o instanceof Assertion))
            return false;

        final Assertion other = (Assertion) o;

        return getStatus().equals(other.getStatus()) && getContext().equals(other.getContext())
                && getTest().equals(other.getTest()) && getLineNumber() == other.getLineNumber();
    }

    /**
     * Get the context used in the executed test.
     *
     * @return The context used in the test.
     */
    public String getContext() {
        return context;
    }

    /**
     * Get the line number at which the test failed.
     *
     * @return The line number.
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Get the status of the assertion.
     *
     * @return A {@link AssertionStatus} enum representing the status of the assertion.
     */
    public AssertionStatus getStatus() {
        return status;
    }

    /**
     * Get the test that was performed as part of this assertion.
     *
     * @return The test.
     */
    public String getTest() {
        return test;
    }

    @Override
    public int hashCode() {
        int hashCode = 31;

        hashCode = 37 * hashCode + getContext().hashCode();
        hashCode = 37 * hashCode + getTest().hashCode();
        hashCode = 37 * hashCode + getLineNumber();
        hashCode = 37 * hashCode + getStatus().hashCode();

        return hashCode;
    }

    @Override
    public String toString() {
        return String.format("[CONTEXT = %s, LINE# = %d]", getContext(), getLineNumber());
    }
}
