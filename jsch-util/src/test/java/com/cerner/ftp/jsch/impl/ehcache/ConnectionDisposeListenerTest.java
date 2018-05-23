package com.cerner.ftp.jsch.impl.ehcache;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.cerner.ftp.jsch.impl.CachedConnection;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * Unit tests for {@link ConnectionDisposeListener}.
 *
 * @author Joshua Hyde
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ConnectionDisposeListenerTest {
    private static final String ELEMENT_NAME = "connections";
    private static Cache cache;

    @Mock
    private CachedConnection connection;
    private Set<CachedConnection> connections;

    /**
     * Set up the cache to be used in each test.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        final CacheManager manager = CacheManager
                .create(ConnectionDisposeListener.class.getResource("/cache/cache.config.xml"));
        cache = manager.getCache("jsch-connection-cache");
        assertThat(cache).isNotNull();
    }

    /**
     * Set up the cached connection.
     */
    @Before
    public void setUp() {
        connections = new HashSet<CachedConnection>();
        connections.add(connection);

        cache.put(new Element(ELEMENT_NAME, connections));
    }

    /**
     * Remove all cached elements.
     */
    @After
    public void tearDown() {
        cache.removeAll();
    }

    /**
     * If an element is evicted from the cache, its connections should be closed.
     */
    @Test
    public void testNotifyElementEvicted() {
        final long existingLimit = cache.getCacheConfiguration().getMaxEntriesLocalHeap();
        try {
            final Element element = cache.get(ELEMENT_NAME);
            element.setTimeToLive(1);
            // Wait long enough for the life of the element to be expired
            try {
                Thread.sleep(2000);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            cache.evictExpiredElements();
            verify(connection).closePhysical();
        } finally {
            // Restore the previous max elements value
            cache.getCacheConfiguration().setMaxEntriesLocalHeap(existingLimit);
        }
    }

    /**
     * If an element is removed from the cache, its connections should be closed.
     */
    @Test
    public void testNotifyElementRemoved() {
        cache.remove(ELEMENT_NAME);
        verify(connection).closePhysical();
    }

}
