package com.cerner.ftp.sftp.jsch.processor;

import com.cerner.ftp.data.sftp.KeyCryptoBuilder.KeyCryptoProduct;
import com.cerner.ftp.instrument.EtmMonitorFactory;
import com.cerner.ftp.jsch.Connection;
import com.cerner.ftp.jsch.ConnectionPool;

import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * Implementation of {@link JschSftpProcessor} that is backed by a {@link KeyCryptoProduct}.
 *
 * @author Joshua Hyde
 *
 */

public class KeyCryptoSftpProcessor extends JschSftpProcessor {
    private static final EtmMonitor MONITOR = EtmMonitorFactory.getEtmMonitor();
    private final KeyCryptoProduct product;
    private final ConnectionPool pool;

    /**
     * Construct an SFTP processor backed by username/private key authentication.
     *
     * @param product
     *            The {@link KeyCryptoProduct} to be used to provide information for authentication.
     * @param pool
     *            A {@link ConnectionPool} from which a connection is to be obtained.
     */
    public KeyCryptoSftpProcessor(final KeyCryptoProduct product, final ConnectionPool pool) {
        this.product = product;
        this.pool = pool;
    }

    @Override
    public Connection getConnection() {
        final EtmPoint point = MONITOR.createPoint(getClass().getName() + ": getConnection()");
        try {
            return pool.getConnection(product.getUsername(), product.getKeySalt(), product.getPrivateKey(),
                    product.getServerAddress());
        } finally {
            point.collect();
        }
    }

}
