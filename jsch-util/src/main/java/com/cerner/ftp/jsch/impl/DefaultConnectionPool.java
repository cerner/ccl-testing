package com.cerner.ftp.jsch.impl;

import java.net.URI;

import com.cerner.ftp.jsch.Connection;
import com.cerner.ftp.jsch.ConnectionPool;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

/**
 * A connection pool implementation.
 *
 * @author Joshua Hyde
 *
 */

public class DefaultConnectionPool implements ConnectionPool {
    private static ConnectionPool INSTANCE;

    static {
        final CacheManager manager = CacheManager.create(ConnectionPool.class.getResource("/cache/cache.config.xml"));
        final Cache cache = manager.getCache("jsch-connection-cache");
        INSTANCE = new DefaultConnectionPool(new CachedLibrarian(new CacheKeyBuilder(), cache));
    }

    /**
     * Retrieves the singleton ConnectionPool instance.
     * 
     * @return The singleton ConnectionPool instance.
     */
    public static ConnectionPool getInstance() {
        return INSTANCE;
    }

    /**
     * A librarian that's used to manage checking in and checking out connections from the connection pool.
     *
     * @author Joshua Hyde
     * @param <T>
     *            The type of object to be managed by this librarian.
     *
     */
    public interface ConnectionLibrarian<T extends Connection> {
        /**
         * Definition of an object that generates identifiers used by the librarian to identify collections of pooled
         * connections for a given set of credentials.
         *
         * @author Joshua Hyde
         *
         */
        interface IdentifierGenerator {
            /**
             * Construct a key for a username/password combination.
             *
             * @param username
             *            The username.
             * @param password
             *            The password.
             * @param serverAddress
             *            A {@link URI} object representing the address of the server.
             * @return A key used to identify the given set of credentials.
             */
            String buildKey(String username, String password, URI serverAddress);

            /**
             * Construct a key for a username/private key combination.
             *
             * @param username
             *            The username.
             * @param salt
             *            The salt used to create the private key.
             * @param privateKeyLocation
             *            A {@link URI} object representing the location of the private key.
             * @param serverAddress
             *            A {@link URI} object representing the address of the server.
             * @return A key used to identify the given set of credentials.
             */
            String buildKey(String username, String salt, URI privateKeyLocation, URI serverAddress);
        }

        /**
         * Check out a connection using username/password authentication.
         *
         * @param username
         *            The username.
         * @param password
         *            The password.
         * @param serverAddress
         *            A {@link URI} object representing the address of the server.
         * @return A {@link Connection} object representing the established connection.
         */
        T checkOut(String username, String password, URI serverAddress);

        /**
         * Check out a connection using username/private key authentication.
         *
         * @param username
         *            The username.
         * @param salt
         *            The salt used to create the private key.
         * @param privateKeyLocation
         *            A {@link URI} object representing the location of the private key.
         * @param serverAddress
         *            A {@link URI} object representing the location of the server.
         * @return A {@link Connection} object representing the established connection.
         */
        T checkOut(String username, String salt, URI privateKeyLocation, URI serverAddress);

        /**
         * Indicate that a connection is no longer in use and should be re-admitted into the pool.
         *
         * @param connection
         *            The {@link Connection} to be checked in.
         * @throws IllegalArgumentException
         *             If the given connection is not one known to the librarian.
         */
        void checkIn(T connection);
    }

    private final ConnectionLibrarian<? extends Connection> librarian;

    /**
     * Create a connection pool.
     *
     * @param librarian
     *            The {@link ConnectionLibrarian} used to manage the connections.
     */
    public DefaultConnectionPool(final ConnectionLibrarian<? extends Connection> librarian) {
        this.librarian = librarian;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection(final String username, final String password, final URI serverAddress) {
        return librarian.checkOut(username, password, serverAddress);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Connection getConnection(final String username, final String salt, final URI privateKeyLocation,
            final URI serverAddress) {
        return librarian.checkOut(username, salt, privateKeyLocation, serverAddress);
    }

}
