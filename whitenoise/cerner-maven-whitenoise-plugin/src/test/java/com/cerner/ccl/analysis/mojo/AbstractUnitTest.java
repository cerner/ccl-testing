package com.cerner.ccl.analysis.mojo;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * A skeleton definition of a unit test.
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
     * Set the test to expect a message to be thrown as part of an exception.
     * @param message The message expected to be in the thrown exception.
     */
    protected void expect(String message) {
        expected.expectMessage(message);
    }
    
    /**
     * Set the test to expect an exception to be thrown.
     * @param clazz The {@link Class} of the {@link Throwable} object to be expected.
     */
    protected void expect(Class<? extends Throwable> clazz) {
        expected.expect(clazz);
    }
}
