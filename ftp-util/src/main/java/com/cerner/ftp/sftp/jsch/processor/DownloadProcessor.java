package com.cerner.ftp.sftp.jsch.processor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.exception.TransferException;
import com.cerner.ftp.instrument.EtmMonitorFactory;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * An {@link FileTransferProcessor} implementation that downloads a requested file.
 *
 * @author Joshua Hyde
 *
 */
public class DownloadProcessor implements FileTransferProcessor {
    private static final EtmMonitor MONITOR = EtmMonitorFactory.getEtmMonitor();
    private final FileRequest request;

    /**
     * Create a processor to download a file.
     *
     * @param request
     *            A {@link FileRequest} object.
     */
    public DownloadProcessor(final FileRequest request) {
        this.request = request;
    }

    @Override
    public void run(final ChannelSftp channel) {
        final EtmPoint point = MONITOR.createPoint(getClass().getName() + ": run(ChannelSftp)");

        try {
            final FileOutputStream writer = new FileOutputStream(new File(request.getTargetFile()), false);
            final InputStream stream = channel.get(request.getSourceFile().getPath());
            IOUtils.copy(stream, writer);
        } catch (final SftpException e) {
            throw new TransferException(
                    "Failed to retrieve file from remote server: " + request.getSourceFile().getPath(), e);
        } catch (final IOException e) {
            throw new TransferException("Failed to download file to local disk: " + request.getSourceFile().getPath(),
                    e);
        } finally {
            point.collect();
        }
    }
}