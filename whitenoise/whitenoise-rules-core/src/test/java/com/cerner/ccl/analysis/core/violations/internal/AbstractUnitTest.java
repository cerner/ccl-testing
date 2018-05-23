package com.cerner.ccl.analysis.core.violations.internal;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * Skeleton definition of a unit test.
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
     * Prepare the test to expect an exception.
     * 
     * @param expectedClass
     *            The {@link Class} of the expected {@link Exception}.
     * @param expectedMessage
     *            The expected message of the thrown exception.
     */
    protected void expect(Class<? extends Exception> expectedClass, String expectedMessage) {
        expected.expect(expectedClass);
        expected.expectMessage(expectedMessage);
    }
}
