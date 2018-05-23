package com.cerner.ftp.jsch;

import com.cerner.ftp.jsch.impl.DefaultConnectionPool;

/**
 * Factory for obtaining a ConnectionPool.
 *
 * @author Joshua Hyde
 *
 */
public class ConnectionPoolFactory {

    /**
     * Hidden constructor to prevent construction.
     */
    private ConnectionPoolFactory() {
        super();
        throw new UnsupportedOperationException();
    }

    /**
     * Retrieve a ConnectionPool instance
     *
     * @return A ConnectionPool instance
     */
    public static ConnectionPool getInstance() {
        return DefaultConnectionPool.getInstance();
    }
}
