package com.cerner.ccltesting.maven.ccl.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * An {@link OutputStream} that delegates its work to an underlying collection of {@code OutputStream} objects.
 *
 * @author Joshua Hyde
 *
 */

public class DelegatingOutputStream extends OutputStream {
    private final List<OutputStream> streams = new ArrayList<OutputStream>();

    /**
     * Add an output stream.
     *
     * @param outputStream
     *            The {@link OutputStream} to be added to this delegate.
     */
    public void addStream(final OutputStream outputStream) {
        streams.add(outputStream);
    }

    @Override
    public void close() throws IOException {
        for (final OutputStream stream : streams)
            stream.close();
    }

    /**
     * Get the streams that lie beneath this output stream.
     *
     * @return An immutable collection of the streams that lie beneath this output stream.
     */
    public Collection<OutputStream> getStreams() {
        return Collections.unmodifiableCollection(streams);
    }

    /**
     * Determine whether or not this delegate contains the given output stream already, based on its
     * {@link OutputStream#equals(Object) equals} contract.
     *
     * @param outputStream
     *            The {@link OutputStream} whose existence is to be determined.
     * @return {@code true} if the output stream already exists; {@code false} if otherwise.
     */
    public boolean hasOutputStream(final OutputStream outputStream) {
        return streams.contains(outputStream);
    }

    @Override
    public void write(final int b) throws IOException {
        for (final OutputStream stream : streams)
            stream.write(b);
    }

}
