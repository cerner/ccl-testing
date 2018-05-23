package com.cerner.ftp.jsch.impl.ehcache;

import java.util.Set;

import com.cerner.ftp.jsch.impl.CachedConnection;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

/**
 * A {@link CacheEventListener} object to close connections when they're evicted from the cache.
 *
 * @author Joshua Hyde
 *
 */

public class ConnectionDisposeListener implements CacheEventListener {

    public void dispose() {
    }

    public void notifyElementEvicted(final Ehcache cache, final Element element) {
    }

    public void notifyElementExpired(final Ehcache cache, final Element element) {
        close(element);
    }

    public void notifyElementPut(final Ehcache cache, final Element element) throws CacheException {
    }

    public void notifyElementRemoved(final Ehcache cache, final Element element) throws CacheException {
        close(element);
    }

    public void notifyElementUpdated(final Ehcache cache, final Element element) throws CacheException {
    }

    public void notifyRemoveAll(final Ehcache cache) {
        /*
         * This method is not implemented because, when #removeAll() is invoked, the elements within the cache have
         * already been removed and are unavilable.
         */
    }

    @Override
    public ConnectionDisposeListener clone() throws CloneNotSupportedException {
        return (ConnectionDisposeListener) super.clone();
    }

    private void close(final Element element) {
        final Object values = element.getObjectValue();
        if (values == null || !(values instanceof Set))
            return;

        for (final Object value : (Set<?>) values)
            if (value instanceof CachedConnection)
                ((CachedConnection) value).closePhysical();
    }
}
