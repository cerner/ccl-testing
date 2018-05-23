package com.cerner.ftp.sftp.jsch.processor;

import java.io.File;

import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.exception.TransferException;
import com.cerner.ftp.instrument.EtmMonitorFactory;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * An implementation of {@link FileTransferProcessor} that uploads a requested file.
 *
 * @author Joshua Hyde
 *
 */
public class UploadProcessor implements FileTransferProcessor {
    private static final EtmMonitor MONITOR = EtmMonitorFactory.getEtmMonitor();
    private final FileRequest request;
    private final int permissions;
    private final boolean ignoreChmodErrors;

    /**
     * Create an uploader that uploads a requested file to a requested location.
     *
     * @param request
     *            A {@link FileRequest} object representing the file to be uploaded.
     * @param ignoreChmodErrors
     *            {@code true} if any errors that occur during an attempt to chmod the uploaded file should be ignored.
     * @param permissions
     *            The bitwise representation of the permissions on the file to be placed once it has been uploaded.
     */
    public UploadProcessor(final FileRequest request, final boolean ignoreChmodErrors, final int permissions) {
        this.request = request;
        this.ignoreChmodErrors = ignoreChmodErrors;
        this.permissions = permissions;
    }

    public void run(final ChannelSftp channel) {
        final EtmPoint point = MONITOR.createPoint(getClass().getName() + ": run(ChannelSftp)");
        try {
            final File sourceFile = new File(request.getSourceFile());
            final String remotePath = request.getTargetFile().getPath();

            try {
                channel.put(sourceFile.getAbsolutePath(), remotePath);
            } catch (final SftpException e) {
                throw new TransferException("Failed to upload: " + sourceFile.getAbsolutePath(), e);
            }

            try {
                channel.chmod(permissions, remotePath);
            } catch (final SftpException e) {
                if (!ignoreChmodErrors)
                    throw new TransferException("Failed to chmod uploaded file.", e);
            }
        } finally {
            point.collect();
        }
    }
}