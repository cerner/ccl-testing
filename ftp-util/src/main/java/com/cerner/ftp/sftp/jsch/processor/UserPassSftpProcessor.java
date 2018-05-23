package com.cerner.ftp.sftp.jsch.processor;

import com.cerner.ftp.data.sftp.UserPassBuilder.UserPassProduct;
import com.cerner.ftp.instrument.EtmMonitorFactory;
import com.cerner.ftp.jsch.Connection;
import com.cerner.ftp.jsch.ConnectionPool;

import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * An implementation of {@link JschSftpProcessor} backed by a {@link UserPassProduct}.
 *
 * @author Joshua Hyde
 *
 */

public class UserPassSftpProcessor extends JschSftpProcessor {
    private static final EtmMonitor MONITOR = EtmMonitorFactory.getEtmMonitor();
    private final UserPassProduct product;
    private final ConnectionPool pool;

    /**
     * Create a processor.
     *
     * @param product
     *            The {@link UserPassProduct} containing authentication details.
     * @param pool
     *            The {@link ConnectionPool} from which connections will be retrieved.
     */
    public UserPassSftpProcessor(final UserPassProduct product, final ConnectionPool pool) {
        this.product = product;
        this.pool = pool;
    }

    @Override
    public Connection getConnection() {
        final EtmPoint point = MONITOR.createPoint(getClass().getName() + ": getConnection()");
        try {
            return pool.getConnection(product.getUsername(), product.getPassword(), product.getServerAddress());
        } finally {
            point.collect();
        }
    }

}
