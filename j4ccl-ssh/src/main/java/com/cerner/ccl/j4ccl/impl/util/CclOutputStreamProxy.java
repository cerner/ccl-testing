package com.cerner.ccl.j4ccl.impl.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * An {@link OutputStream} object that forwards data within a specific set of keywords to a given output stream.
 *
 * @author Joshua Hyde
 *
 */

public class CclOutputStreamProxy extends OutputStreamProxy {
    private final StringBuilder builder = new StringBuilder();
    private final String outputBegin;
    private final String outputEnd;
    private boolean doForward;

    /**
     * Create an output stream proxy.
     * <br>
     * Data starting the line after the given starting keyword through the line before the line containing the given
     * keyword will be forwarded. A line is defined as a string of characters terminated by a {@code \n} character.
     *
     * @param out
     *            The {@link OutputStream} object to which data is to be forwarded.
     * @param outputBegin
     *            The keyword whose containing line will mark the beginning of output forwarding.
     * @param outputEnd
     *            The keyword whose containing line will mark the end of the output forwarding.
     */
    public CclOutputStreamProxy(final OutputStream out, final String outputBegin, final String outputEnd) {
        super(out);
        this.outputBegin = outputBegin;
        this.outputEnd = outputEnd;
    }

    @Override
    public void write(final int b) throws IOException {
        final char bChar = (char) b;
        builder.append(bChar);

        if (bChar == '\n') {
            final String string = builder.toString();
            if (string.contains(outputEnd))
                doForward = false;
            else if (string.contains(outputBegin))
                doForward = true;
            else if (doForward) {
                final OutputStream proxied = getProxied();
                for (final char currentChar : string.toCharArray())
                    proxied.write(currentChar);
            }

            builder.delete(0, builder.length());
        }
    }
}
