package com.cerner.ftp.jsch;

import java.net.URI;

/**
 * A pool from which {@link Connection connections} can be retrieved. Use ConnectionPoolFactory.getInstance() to obtain
 * one.
 *
 * @author Joshua Hyde
 *
 */

public interface ConnectionPool {
    /**
     * Get a connection using a username/password method of authentication.
     *
     * @param username
     *            The username.
     * @param password
     *            The password.
     * @param serverAddress
     *            A {@link URI} representing the location of the address.
     * @return A {@link Connection} object.
     */
    Connection getConnection(String username, String password, URI serverAddress);

    /**
     * Get a connection using a username/private key method of authentication.
     *
     * @param username
     *            The username.
     * @param salt
     *            The salt used to create the private key.
     * @param privateKeyLocation
     *            A {@link URI} object representing the location of the private key.
     * @param serverAddress
     *            A {@link URI} object representing the location of the address.
     * @return A {@link Connection} object.
     */
    Connection getConnection(String username, String salt, URI privateKeyLocation, URI serverAddress);
}
