package com.cerner.ftp.sftp.jsch.processor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.data.FtpProduct;
import com.cerner.ftp.data.sftp.KeyCryptoBuilder.KeyCryptoProduct;
import com.cerner.ftp.data.sftp.UserPassBuilder.UserPassProduct;
import com.cerner.ftp.exception.ConnectionException;
import com.cerner.ftp.exception.TransferException;
import com.cerner.ftp.instrument.EtmMonitorFactory;
import com.cerner.ftp.jsch.Connection;
import com.cerner.ftp.jsch.ConnectionPoolFactory;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * A generic utility class designed to facilitate SFTP operations beneath JSch.
 *
 * @author Joshua Hyde
 *
 */

public abstract class JschSftpProcessor {
    private static final EtmMonitor MONITOR = EtmMonitorFactory.getEtmMonitor();

    private final Set<DownloadProcessor> downloadQueue = new HashSet<DownloadProcessor>(0);
    private final Set<UploadProcessor> uploadQueue = new HashSet<UploadProcessor>(0);

    /**
     * Get an SFTP processor.
     *
     * @param product
     *            The {@link FtpProduct} object containing information to be used for authentication.
     * @return A {@link JschSftpProcessor} object.
     * @throws IllegalArgumentException
     *             If the given product is not a {@link KeyCryptoProduct} or {@link UserPassProduct} object.
     */
    public static JschSftpProcessor getProcessor(final FtpProduct product) {
        if (product instanceof KeyCryptoProduct) {
            return new KeyCryptoSftpProcessor((KeyCryptoProduct) product, ConnectionPoolFactory.getInstance());
        } else if (product instanceof UserPassProduct) {
            return new UserPassSftpProcessor((UserPassProduct) product, ConnectionPoolFactory.getInstance());
        }

        throw new IllegalArgumentException(
                "Invalid product type: " + (product == null ? "null" : product.getClass().getName()));
    }

    /**
     * Perform all queued downloaded operations.
     *
     * @throws ConnectionException
     *             If an error occurs establishing the connection.
     * @throws IllegalStateException
     *             If no {@link Session} has been configured for this object.
     * @throws TransferException
     *             If an error occurs while downloading a file.
     */
    public void download() {
        process(downloadQueue);
    }

    /**
     * Add a file request to the download queue.
     *
     * @param request
     *            A {@link FileRequest} object representing the file to be downloaded.
     */
    public void queueDownload(final FileRequest request) {
        downloadQueue.add(new DownloadProcessor(request));
    }

    /**
     * Add a file request to the upload queue.
     *
     * @param request
     *            A {@link FileRequest} object representing the file to be uploaded.
     * @param ignoreChmodErrors
     *            {@code true} to make the upload process ignore any errors that occur during the attempt to chmod the
     *            uploaded file.
     * @param permissions
     *            The bitwise representation of the permissions to be placed on the file once it has been uploaded.
     */
    public void queueUpload(final FileRequest request, final boolean ignoreChmodErrors, final int permissions) {
        uploadQueue.add(new UploadProcessor(request, ignoreChmodErrors, permissions));
    }

    /**
     * Establish a connection to the remote server.
     *
     * @return A {@link Connection} representing a connection to the remote server.
     */
    public abstract Connection getConnection();

    /**
     * Perform all queued upload operations.
     *
     * @throws ConnectionException
     *             If an error occurs while establishing the connection.
     * @throws IllegalStateException
     *             If no {@link Session} has been configured for this object.
     * @throws TransferException
     *             If an error occurs during a file upload.
     */
    public void upload() {
        process(uploadQueue);
    }

    /**
     * Execute the {@link FileTransferProcessor#run(ChannelSftp)} within a given collection of objects.
     *
     * @param queue
     *            A {@link Collection} of {@link FileTransferProcessor} objects that will be run.
     * @throws ConnectionException
     *             If an error occurs while attempting to establish the connection.
     */
    private void process(final Collection<? extends FileTransferProcessor> queue) {
        final EtmPoint point = MONITOR.createPoint(getClass().getName() + ": process(Collection)");
        try {
            if (queue.isEmpty()) {
                return;
            }

            ChannelSftp channel = null;
            Connection conn = null;
            try {
                conn = getConnection();

                channel = conn.getSFtp();
                channel.connect();

                for (final FileTransferProcessor request : queue) {
                    request.run(channel);
                }

            } catch (final JSchException e) {
                throw new ConnectionException("Failed to establish SSH connection.", e);
            } finally {
                if (channel != null) {
                    channel.disconnect();
                }

                if (conn != null) {
                    conn.close();
                }
            }
        } finally {
            point.collect();
        }
    }
}
