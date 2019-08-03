package com.cerner.ftp.jsch.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.cerner.ftp.jsch.Connection;
import com.cerner.ftp.jsch.impl.CachedLibrarian.ConnectionCreator;
import com.cerner.ftp.jsch.impl.DefaultConnectionPool.ConnectionLibrarian.IdentifierGenerator;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Unit tests for {@link CachedLibrarian}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class CachedLibrarianTest {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final URI SERVER_ADDRESS = URI.create("www.google.com");
    private static final String ID = "axbdfukdfjkd";
    private static Cache cache;

    @Mock
    private IdentifierGenerator generator;
    @Mock
    private CachedConnection defaultConnection;

    private Element element;
    private Set<CachedConnection> cachedConnections;
    private InjectableCreator creator;
    private InjectableLibrarian librarian;

    /**
     * Set up the cache to be used in each test.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        final CacheManager manager = CacheManager
                .create(CachedLibrarianTest.class.getResource("/cache/cache.config.xml"));
        cache = new Cache("testCache", 5000, false, false, 5, 2);
        manager.addCache(cache);
        cache = manager.getCache("testCache");
    }

    /**
     * Create a new librarian for each test.
     */
    @Before
    public void setUp() {
        // Set up the cache for the given username and password
        cachedConnections = new HashSet<CachedConnection>();
        element = new Element(ID, cachedConnections);
        cache.put(element);

        when(generator.buildKey(USERNAME, PASSWORD, SERVER_ADDRESS)).thenReturn(ID);

        creator = new InjectableCreator(defaultConnection);
        librarian = new InjectableLibrarian(generator, cache, creator);
    }

    /**
     * Clear the cache of all values after each test.
     */
    @After
    public void tearDown() {
        if (cache != null) {
            cache.removeAll();
        }
    }

    /**
     * If an attempt is made to check in a connection that was not checked out through the librarian, the check-in
     * should fail.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCheckInNotCheckedOut() {
        librarian.checkIn(mock(CachedConnection.class));
    }

    /**
     * Test that checking in a checked-out connection works.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCheckIn() throws Exception {
        final CachedConnection conn = librarian.checkOut(USERNAME, PASSWORD, SERVER_ADDRESS);
        librarian.checkIn(conn);
        // The connection should have been returned to the cache.
        assertThat(cachedConnections).contains(conn);
    }

    /**
     * When checking in a connection, it should be removed from the "checked-out" list.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testCheckInCheckedOutRemoval() throws Exception {
        final CachedConnection conn = librarian.checkOut(USERNAME, PASSWORD, SERVER_ADDRESS);
        librarian.checkIn(conn);

        final Field checkOutField = CachedLibrarian.class.getDeclaredField("checkedOut");
        if (!checkOutField.isAccessible()) {
            checkOutField.setAccessible(true);
        }
        final Map<Connection, String> checkedOut = (Map<Connection, String>) checkOutField.get(librarian);
        assertThat(checkedOut.containsKey(conn)).isFalse();
    }

    /**
     * If a connection is cached, then it should be the returned cache.
     */
    @Test
    public void testCheckOutCached() {
        final CachedConnection cachedConnection = mock(CachedConnection.class);
        cachedConnections.add(cachedConnection);

        assertThat(librarian.checkOut(USERNAME, PASSWORD, SERVER_ADDRESS)).isEqualTo(cachedConnection);
    }

    /**
     * If there are no cached connections, then a new connection should be created and returned.
     */
    @Test
    public void testCheckOutNotCached() {
        assertThat(librarian.checkOut(USERNAME, PASSWORD, SERVER_ADDRESS)).isEqualTo(defaultConnection);
    }

    /**
     * Given a cached connection and two sequential check out calls, the cached connection and then a new connection
     * should be returned.
     */
    @Test
    public void testCheckOutMultiple() {
        final CachedConnection cachedConnection = mock(CachedConnection.class);
        cachedConnections.add(cachedConnection);

        assertThat(librarian.checkOut(USERNAME, PASSWORD, SERVER_ADDRESS)).isEqualTo(cachedConnection);
        assertThat(librarian.checkOut(USERNAME, PASSWORD, SERVER_ADDRESS)).isEqualTo(defaultConnection);
    }

    /**
     * If a connection is checked out, checked in, and checked out (given that no other connections exist in the cache),
     * then the same connection should be returned.
     */
    @Test
    public void testCheckOutRecycle() {
        final CachedConnection conn = librarian.checkOut(USERNAME, PASSWORD, SERVER_ADDRESS);
        librarian.checkIn(conn);
        assertThat(librarian.checkOut(USERNAME, PASSWORD, SERVER_ADDRESS)).isEqualTo(conn);
    }

    /**
     * A {@link CachedLibrarian} that allows the injection of the {@link ConnectionCreator} used to create the
     * connection in the case of a non-cached
     *
     * @author Joshua Hyde
     *
     */
    private static class InjectableLibrarian extends CachedLibrarian {
        private final ConnectionCreator creator;

        public InjectableLibrarian(final IdentifierGenerator idGenerator, final Cache cache,
                final ConnectionCreator creator) {
            super(idGenerator, cache);
            this.creator = creator;
        }

        @Override
        CachedConnection retrieveCachedConnection(final String id, final ConnectionCreator creator) {
            return super.retrieveCachedConnection(id, this.creator);
        }
    }

    /**
     * A {@link ConnectionCreator} that returns whatever {@link CachedConnection} is given to it.
     *
     * @author Joshua Hyde
     *
     */
    private static class InjectableCreator implements ConnectionCreator {
        private final CachedConnection connection;

        /**
         * Create a connection creator.
         *
         * @param connection
         *            A {@link CachedConnection} to be returned by this creator.
         */
        public InjectableCreator(final CachedConnection connection) {
            this.connection = connection;
        }

        @Override
        public CachedConnection create() {
            return connection;
        }
    }
}
