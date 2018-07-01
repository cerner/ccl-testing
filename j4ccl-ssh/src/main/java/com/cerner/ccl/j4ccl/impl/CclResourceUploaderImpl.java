package com.cerner.ccl.j4ccl.impl;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.cerner.ccl.j4ccl.impl.util.AuthHelper;
import com.cerner.ccl.j4ccl.impl.util.FileAssistant;
import com.cerner.ccl.j4ccl.util.CclResourceUploader;
import com.cerner.ftp.Uploader;
import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.data.factory.FileRequestFactory;
import com.cerner.ftp.sftp.SftpUploader;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * An implementation of {@link CclResourceUploader}. <br>
 * This requires a subject in context at all times in order to retrieve the paths on the remote server that are going to
 * be used.
 *
 * @author Joshua Hyde
 *
 */

public class CclResourceUploaderImpl extends CclResourceUploader {
    private final Set<File> requestedFiles = new HashSet<File>();

    @Override
    public void queueUpload(final File file) {
        if (file == null)
            throw new NullPointerException("File cannot be null.");

        requestedFiles.add(file);
    }

    @Override
    public Map<File, URI> upload() {
        final EtmPoint point = PointFactory.getPoint(getClass(), "upload");
        try {
            if (requestedFiles.isEmpty())
                return Collections.<File, URI> emptyMap();

            final Map<File, URI> uploads = new HashMap<File, URI>(requestedFiles.size());
            final Set<FileRequest> uploadRequests = new HashSet<FileRequest>(requestedFiles.size());
            for (final File requestedFile : requestedFiles) {
                final URI remotePath = FileAssistant.createRemotePath(requestedFile);
                uploadRequests.add(FileRequestFactory.create(requestedFile.toURI(), remotePath));
                uploads.put(requestedFile, remotePath);
            }

            final Uploader uploader = SftpUploader.createUploader(AuthHelper.fromCurrentSubject());
            uploader.ignoreChmodErrors(true);
            uploader.upload(uploadRequests);

            return uploads;
        } finally {
            point.collect();
        }
    }
}
