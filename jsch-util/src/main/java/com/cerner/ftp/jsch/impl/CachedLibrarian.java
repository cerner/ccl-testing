package com.cerner.ftp.jsch.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.cerner.ftp.jsch.Connection;
import com.cerner.ftp.jsch.impl.DefaultConnectionPool.ConnectionLibrarian;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/*
 * Example code:
 *         final CacheManager manager = CacheManager.create(CachedConnectionPool.class
 *                  .getResource("/cache/cache.config.xml"));
 *         this.cache = manager.getCache("jsch-connection-cache");
 */

/**
 * A {@link ConnectionLibrarian} backed by a {@link Cache}.
 *
 * @author Joshua Hyde
 *
 */

public class CachedLibrarian implements ConnectionLibrarian<CachedConnection> {
    /**
     * A catalogue of connections that have been checked out. The keys are the connections, the values are their
     * identifiers.
     */
    private final Map<Connection, String> checkedOut = new HashMap<Connection, String>();
    private final IdentifierGenerator idGenerator;
    /**
     * Never call {@link Cache#removeAll()} on this object, as listening on this event does not provide the elements
     * that existed previously in the cache - as such, the connections cannot be physically closed.
     */
    private final Cache cache;

    /**
     * Create a librarian.
     *
     * @param idGenerator
     *            An {@link DefaultConnectionPool.ConnectionLibrarian.IdentifierGenerator IdentifierGenerator} used to
     *            identify connections sharing common attributes.
     * @param cache
     *            A {@link Cache} used to manage the lifecycle of a connection outside of its consumer's use.
     */
    public CachedLibrarian(final IdentifierGenerator idGenerator, final Cache cache) {
        this.cache = cache;
        this.idGenerator = idGenerator;
    }

    public void checkIn(final CachedConnection connection) {
        if (!checkedOut.containsKey(connection))
            throw new IllegalArgumentException(
                    "The given connection is either unknown to this librarian or has already been checked in.");

        final String cacheKey = checkedOut.get(connection);
        final Set<Connection> connections = cast(cache.get(cacheKey).getObjectValue());
        checkedOut.remove(connection);
        connections.add(connection);
    }

    public CachedConnection checkOut(final String username, final String password, final URI serverAddress) {
        final String id = idGenerator.buildKey(username, password, serverAddress);
        return retrieveCachedConnection(id, new UserPasswordCreator(username, password, serverAddress, this));
    }

    public CachedConnection checkOut(final String username, final String salt, final URI privateKeyLocation,
            final URI serverAddress) {
        final String id = idGenerator.buildKey(username, salt, privateKeyLocation, serverAddress);
        return retrieveCachedConnection(id,
                new PrivateKeyCreator(username, salt, privateKeyLocation, serverAddress, this));
    }

    /**
     * Retrieve a cached connection. If one is not available in the cache, it will be created using the given connection
     * createor.
     *
     * @param id
     *            The ID representing the entry within the cache containing the connection pool.
     * @param creator
     *            The {@link ConnectionCreator} that will be used to create a connection if one is not available in the
     *            cache.
     * @return A {@link CachedConnection} that can be used to communicate with the remote server.
     */
    CachedConnection retrieveCachedConnection(final String id, final ConnectionCreator creator) {
        final Set<CachedConnection> connections = getCachedConnections(id);

        CachedConnection connection;
        if (connections.isEmpty())
            connection = creator.create();
        else {
            connection = connections.iterator().next();
            connections.remove(connection);
        }

        checkedOut.put(connection, id);
        connection.open();
        return connection;
    }

    /**
     * Get a set of cached connections for the given key.
     *
     * @param key
     *            The key corresponding to the cache entry whose connections are to be retrieved.
     * @return A {@link Set} of {@link Connection} objects for the given key. This set will be created if it does not
     *         exist in the cache.
     */
    private Set<CachedConnection> getCachedConnections(final String key) {
        Set<CachedConnection> connections;
        if (!cache.isKeyInCache(key)) {
            connections = new HashSet<CachedConnection>();
            cache.put(new Element(key, connections));
        } else
            connections = cast(cache.get(key).getObjectValue());

        return connections;
    }

    /**
     * Cast an object to another object type.
     * <br>
     * This method exists to reduce the scope of {@link SuppressWarnings} annotations.
     *
     * @param <T>
     *            The inferred type to which the object is to be cast.
     * @param o
     *            The object to be cast.
     * @return The given object, cast as a {@code T} object.
     */
    @SuppressWarnings("unchecked")
    private static <T> T cast(final Object o) {
        return (T) o;
    }

    /**
     * Because the steps to retrieve a cached connection only differ by the step to create a new connection, this
     * interface allows for an abstracted way to create the connection, facilitating a single point of retrieval of a
     * cached connection.
     *
     * @author Joshua Hyde
     *
     */
    interface ConnectionCreator {
        /**
         * Create a cached connection.
         *
         * @return A {@link CachedConnection}.
         */
        CachedConnection create();
    }

    /**
     * A connection creator that uses username/password authentication.
     *
     * @author Joshua Hyde
     *
     */
    private static class UserPasswordCreator implements ConnectionCreator {
        private final String username;
        private final String password;
        private final URI serverAddress;
        private final ConnectionLibrarian<CachedConnection> librarian;

        /**
         * Create a password creator.
         *
         * @param username
         *            The username.
         * @param password
         *            The password.
         * @param serverAddress
         *            The location of the server.
         * @param librarian
         *            The managing {@link ConnectionLibrarian}.
         */
        public UserPasswordCreator(final String username, final String password, final URI serverAddress,
                final ConnectionLibrarian<CachedConnection> librarian) {
            this.username = username;
            this.password = password;
            this.serverAddress = serverAddress;
            this.librarian = librarian;
        }

        public CachedConnection create() {
            return new CachedConnection(username, password, serverAddress, librarian);
        }
    }

    /**
     * A connection creator that uses username/private key authentication.
     *
     * @author Joshua Hyde
     *
     */
    private static class PrivateKeyCreator implements ConnectionCreator {
        private final String username;
        private final String salt;
        private final URI privateKeyLocation;
        private final URI serverAddress;
        private final ConnectionLibrarian<CachedConnection> librarian;

        /**
         * Create a connection creator.
         *
         * @param username
         *            The username.
         * @param salt
         *            The salt used to create the private key.
         * @param privateKeyLocation
         *            The location of the private key.
         * @param serverAddress
         *            The address of the server.
         * @param librarian
         *            The {@link ConnectionLibrarian} to manage the created connection.
         */
        public PrivateKeyCreator(final String username, final String salt, final URI privateKeyLocation,
                final URI serverAddress, final ConnectionLibrarian<CachedConnection> librarian) {
            this.username = username;
            this.salt = salt;
            this.privateKeyLocation = privateKeyLocation;
            this.serverAddress = serverAddress;
            this.librarian = librarian;
        }

        public CachedConnection create() {
            return new CachedConnection(username, salt, privateKeyLocation, serverAddress, librarian);
        }

    }

}
