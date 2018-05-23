package com.cerner.ftp.sftp.jsch.internal;

import com.cerner.ftp.jsch.Connection;
import com.cerner.ftp.sftp.jsch.processor.JschSftpProcessor;

/**
 * A concrete implementation of {@link JschSftpProcessor} used for testing.
 *
 * @author Joshua Hyde
 *
 */

public class StubbedJschSftpProcessor extends JschSftpProcessor {
    private Connection connection;

    @Override
    public Connection getConnection() {
        return connection;
    }

    /**
     * Set the connection to be used by this object.
     *
     * @param connection
     *            A {@link Connection} object.
     */
    public void setConnection(final Connection connection) {
        this.connection = connection;
    }
}
