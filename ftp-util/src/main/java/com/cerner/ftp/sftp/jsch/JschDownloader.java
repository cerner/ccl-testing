package com.cerner.ftp.sftp.jsch;

import java.util.Collection;

import com.cerner.ftp.Downloader;
import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.instrument.EtmMonitorFactory;
import com.cerner.ftp.sftp.jsch.processor.JschSftpProcessor;

import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * An implementation of {@link Downloader} that uses SFTP over SSH through JSch.
 *
 * @author Joshua Hyde
 *
 */

public class JschDownloader implements Downloader {
    private static final EtmMonitor MONITOR = EtmMonitorFactory.getEtmMonitor();
    private final JschSftpProcessor processor;

    /**
     * Create a downloader.
     *
     * @param processor
     *            A {@link JschSftpProcessor} object that dictates the behavior of the downloader.
     */
    public JschDownloader(final JschSftpProcessor processor) {
        this.processor = processor;
    }

	@Override
	public void download(final Collection<? extends FileRequest> requests) {
        final EtmPoint point = MONITOR.createPoint(getClass().getName() + ": download(Collection)");

        try {
            // If there's nothing to download...
            if (requests.isEmpty()) {
				return;
			}

            for (final FileRequest request : requests) {
				processor.queueDownload(request);
			}

            processor.download();
        } finally {
            point.collect();
        }
    }

}
