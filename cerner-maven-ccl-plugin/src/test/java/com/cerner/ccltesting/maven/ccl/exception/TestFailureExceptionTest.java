package com.cerner.ccltesting.maven.ccl.exception;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.cerner.ccltesting.maven.ccl.data.UnitTest;

/**
 * Unit tests for {@link TestFailureException}.
 *
 * @author Joshua Hyde
 *
 */

public class TestFailureExceptionTest {
    /**
     * Test that the tests are reported properly by the exception.
     */
    @Test
    public void testTestFailureException() {
        final UnitTest test = mock(UnitTest.class);
        when(test.getName()).thenReturn("test");

        final Set<UnitTest> tests = Collections.singleton(test);
        final TestFailureException exc = new TestFailureException(tests);
        assertThat(exc.getMessage()).isEqualTo("1 test(s) failed: [test]");
    }

    /**
     * Test that, given an empty list, the associated message is correct.
     */
    @Test
    public void testTestFailureExceptionEmptyList() {
        final List<UnitTest> tests = Collections.<UnitTest> emptyList();
        final TestFailureException exc = new TestFailureException(tests);
        assertThat(exc.getMessage()).isEqualTo("0 test(s) failed: []");
    }

}
