package com.cerner.ccl.analysis.internal;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * A class that provides common methods for unit tests.
 * 
 * @author Joshua Hyde
 * 
 */

public abstract class AbstractUnitTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    /**
     * Set the expectation of an exception to be thrown by the end of this test.
     * 
     * @param exceptionClass
     *            The {@link Class} of the {@link Throwable} that is expected to be thrown.
     */
    protected void expect(final Class<? extends Throwable> exceptionClass) {
        expected.expect(exceptionClass);
    }

    /**
     * Set the message of the exception expected to be thrown by the end of this test.
     * 
     * @param message
     *            The message of the exception expected to be thrown by the end of this test.
     */
    protected void expect(final String message) {
        expected.expectMessage(message);
    }
}
