package com.cerner.ftp.sftp.jsch.processor.internal;

import java.net.URI;

import com.cerner.ftp.jsch.Connection;
import com.cerner.ftp.jsch.ConnectionPool;

/**
 * A stubbed concrete implementation of {@link ConnectionPool} to facilitate testing.
 *
 * @author Joshua Hyde
 *
 */

public class StubbedConnectionPool implements ConnectionPool {

    @Override
    public Connection getConnection(final String username, final String password, final URI serverAddress) {
        return null;
    }

    @Override
    public Connection getConnection(final String username, final String salt, final URI privateKeyLocation,
            final URI serverAddress) {
        return null;
    }

}
