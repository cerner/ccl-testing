package com.cerner.ftp.jsch.impl.ehcache;

import java.util.Properties;

import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.event.CacheEventListenerFactory;

/**
 * A factory to produce a {@link ConnectionDisposeListener}.
 *
 * @author Joshua Hyde
 *
 */

public class ConnectionDisposeListenerFactory extends CacheEventListenerFactory {

    @Override
    public CacheEventListener createCacheEventListener(final Properties properties) {
        return new ConnectionDisposeListener();
    }

}
