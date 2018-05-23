package com.cerner.ccl.j4ccl.impl.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A proxy that redirects writing to a given proxy and swallows all attempts to close the underlying proxy.
 *
 * @author Joshua Hyde
 *
 */

public class OutputStreamProxy extends OutputStream {
    private final OutputStream proxied;

    /**
     * Create a proxy.
     *
     * @param proxied
     *            The {@link OutputStream} to be proxied.
     * @throws NullPointerException
     *             If the given proxy is {@code null}.
     */
    public OutputStreamProxy(final OutputStream proxied) {
        if (proxied == null)
            throw new NullPointerException("Output stream cannot be null.");

        this.proxied = proxied;
    }

    @Override
    public void write(final int b) throws IOException {
        getProxied().write(b);
    }

    /**
     * Get the proxied output stream.
     *
     * @return The proxied {@link OutputStream}.
     */
    protected OutputStream getProxied() {
        return proxied;
    }
}
