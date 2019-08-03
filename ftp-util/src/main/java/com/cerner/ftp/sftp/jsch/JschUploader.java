package com.cerner.ftp.sftp.jsch;

import java.util.Collection;

import com.cerner.ftp.Uploader;
import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.instrument.EtmMonitorFactory;
import com.cerner.ftp.sftp.jsch.processor.JschSftpProcessor;

import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;

/**
 * An implementation of {@link Uploader} that uses JSch to upload files using SFTP over SSH.
 *
 * @author Joshua Hyde
 *
 */

public class JschUploader implements Uploader {
    private static final EtmMonitor MONITOR = EtmMonitorFactory.getEtmMonitor();

    private final JschSftpProcessor processor;
    private int permissions = Uploader.GOD_PERMISSIONS;
    private boolean ignoreChmodErrors = false;

    /**
     * Create an uploader.
     *
     * @param processor
     *            A {@link JschSftpProcessor} object that dictates the behavior of this uploader.
     */
    public JschUploader(final JschSftpProcessor processor) {
        this.processor = processor;
    }

	@Override
	public void ignoreChmodErrors(final boolean ignoreChmodErrors) {
        this.ignoreChmodErrors = ignoreChmodErrors;
    }

	@Override
	public void setPermissions(final int permissions) {
        this.permissions = permissions;
    }

	@Override
	public void upload(final Collection<? extends FileRequest> requests) {
        final EtmPoint point = MONITOR.createPoint(getClass().getName() + ": upload(Collection)");

        try {
            if (requests.isEmpty()) {
				return;
			}

            for (final FileRequest request : requests) {
				processor.queueUpload(request, ignoreChmodErrors, permissions);
			}

            processor.upload();
        } finally {
            point.collect();
        }
    }

}
