package com.cerner.ccl.j4ccl.exception;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

/**
 * Unit test for {@link CclCompilationException}.
 *
 * @author Joshua Hyde
 *
 */

public class CclCompilationExceptionTest {
    /**
     * Unit test for {@link CclCompilationException#CclCompilationException(String, Throwable)} .
     */
    @Test
    public void testCclCompilationExceptionStringThrowable() {
        final String message = "message";
        final Throwable cause = mock(Throwable.class);
        final CclCompilationException exc = new CclCompilationException(message, cause);

        assertThat(exc.getMessage()).isEqualTo(message);
        assertThat(exc.getCause()).isSameAs(cause);
    }

    /**
     * Unit test for {@link CclCompilationException#CclCompilationException(String)}.
     */
    @Test
    public void testCclCompilationExceptionString() {
        final String message = "message";
        final CclCompilationException exc = new CclCompilationException(message);

        assertThat(exc.getMessage()).isEqualTo(message);
        assertThat(exc.getCause()).isNull();
    }

}
