package com.cerner.ccl.j4ccl.impl.util;

import static org.fest.assertions.Assertions.assertThat;

import java.io.OutputStream;

import org.junit.Test;

/**
 * Unit tests of {@link CclOutputStreamProxy}.
 *
 * @author Joshua Hyde
 *
 */

public class CclOutputStreamProxyTest {
    private static final String OUTPUT_BEGIN = "##BEGIN##OUTPUT##";
    private static final String OUTPUT_END = "##END##OUTPUT##";

    /**
     * Test the forwarding of data.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWriteInt() throws Exception {
        final StringBuilderOutputStream stream = new StringBuilderOutputStream();
        final CclOutputStreamProxy proxy = new CclOutputStreamProxy(stream, OUTPUT_BEGIN, OUTPUT_END);
        final String[] strings = new String[6];
        strings[0] = String.format("I should not be in the output%n");
        strings[1] = String.format("%s%n", OUTPUT_BEGIN);
        strings[2] = String.format("I am the middle content%n");
        strings[3] = String.format("Second middle content%n");
        strings[4] = String.format("%s%n", OUTPUT_END);
        strings[5] = String.format("I should not be in the output, either%n");

        for (final String string : strings)
            for (final char currentChar : string.toCharArray())
                proxy.write(currentChar);

        assertThat(stream.toString()).isEqualTo(strings[2] + strings[3]);
        proxy.close();
    }

    private static class StringBuilderOutputStream extends OutputStream {
        private final StringBuilder builder = new StringBuilder();

        public StringBuilderOutputStream() {
        }

        @Override
        public void write(final int b) {
            builder.append((char) b);
        }

        @Override
        public String toString() {
            return builder.toString();
        }
    }
}