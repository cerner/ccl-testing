package com.cerner.ccltesting.maven.ccl.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.FileOutputStream;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for {@link DelegatingOutputStream}.
 *
 * @author Joshua Hyde
 *
 */
public class DelegatingOutputStreamTest {
    @Mock
    private FileOutputStream stream;
    private DelegatingOutputStream delegate;

    /**
     * Create and set up the delegate for each test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        delegate = new DelegatingOutputStream();
        delegate.addStream(stream);
    }

    /**
     * Test the delegation of work to underlying streams.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteInt() throws Exception {
        for (int i = 1; i <= 10; i++)
            delegate.write(i);

        for (int i = 1; i <= 10; i++)
            verify(stream).write(i);

        verifyNoMoreInteractions(stream);
    }

    /**
     * Verify that, if the delegate is closed, so are all of its underlying streams.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testClose() throws Exception {
        delegate.close();
        verify(stream).close();
        verifyNoMoreInteractions(stream);
    }

    /**
     * Test that an equals contract is honored for determining existence of an output stream within the delegate.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testHasOutputStream() throws Exception {
        final EqualsStream equalsA = new EqualsStream();
        final EqualsStream equalsB = new EqualsStream();

        final NotEqualsStream notEquals = new NotEqualsStream();

        delegate.addStream(equalsA);
        assertThat(delegate.hasOutputStream(equalsB)).isTrue();

        delegate.addStream(notEquals);
        assertThat(delegate.hasOutputStream(notEquals)).isFalse();
    }

    /**
     * Stub implementation of {@link OutputStream} that bases its equality contact on the other object having the same
     * class.
     *
     * @author Joshua Hyde
     *
     */
    private static class EqualsStream extends OutputStream {

        public EqualsStream() {
        }

        @Override
        public void write(final int b) {
            // no-op
        }

        @Override
        public boolean equals(final Object object) {
            return (object != null && object.getClass().equals(getClass()));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

    /**
     * Stub implementation of {@link OutputStream} that always says it is not equal to the other object.
     *
     * @author Joshua Hyde
     *
     */
    private static class NotEqualsStream extends OutputStream {

        public NotEqualsStream() {
        }

        @Override
        public void write(final int b) {
            // no-op
        }

        @Override
        public boolean equals(final Object object) {
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }

}
