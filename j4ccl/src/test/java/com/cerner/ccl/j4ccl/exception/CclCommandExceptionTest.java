package com.cerner.ccl.j4ccl.exception;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

/**
 * Unit test for {@link CclCommandException}.
 *
 * @author Joshua Hyde
 *
 */

public class CclCommandExceptionTest {
    /**
     * Test {@link CclCommandException#CclCommandException(String, Throwable)}.
     */
    @Test
    public void testCclCommandExceptionStringThrowable() {
        final String message = "Exception";
        final Throwable cause = mock(Throwable.class);
        final CclCommandException exc = new CclCommandException(message, cause);

        assertThat(exc.getMessage()).isEqualTo(message);
        assertThat(exc.getCause()).isSameAs(cause);
    }

    /**
     * Test {@link CclCommandException#CclCommandException(String)}.
     */
    @Test
    public void testCclCommandExceptionString() {
        final String message = "Exception";
        final CclCommandException exc = new CclCommandException(message);

        assertThat(exc.getMessage()).isEqualTo(message);
        assertThat(exc.getCause()).isNull();
    }

}
