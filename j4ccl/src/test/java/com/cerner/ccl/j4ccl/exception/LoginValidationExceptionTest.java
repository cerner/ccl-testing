package com.cerner.ccl.j4ccl.exception;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

/**
 * Unit tests for {@link LoginValidationException}.
 *
 * @author Joshua Hyde
 *
 */

public class LoginValidationExceptionTest {
    /**
     * Test {@link LoginValidationException#LoginValidationException(String, Throwable)} .
     */
    @Test
    public void testLoginValidationExceptionStringThrowable() {
        final String message = "message";
        final Throwable cause = mock(Throwable.class);

        final LoginValidationException exc = new LoginValidationException(message, cause);
        assertThat(exc.getMessage()).isEqualTo(message);
        assertThat(exc.getCause()).isSameAs(cause);
    }

    /**
     * Test {@link LoginValidationException#LoginValidationException(String)}.
     */
    @Test
    public void testLoginValidationExceptionString() {
        final String message = "message";

        final LoginValidationException exc = new LoginValidationException(message);
        assertThat(exc.getMessage()).isEqualTo(message);
        assertThat(exc.getCause()).isNull();
    }

}
