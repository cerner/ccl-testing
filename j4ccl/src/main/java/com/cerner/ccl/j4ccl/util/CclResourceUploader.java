package com.cerner.ccl.j4ccl.util;

import java.io.File;
import java.net.URI;
import java.util.Enumeration;
import java.util.Map;

import org.apache.commons.discovery.tools.Service;

/**
 * An object that can be used to upload CCL resources to a remote server.
 *
 * @author Joshua Hyde
 *
 */

public abstract class CclResourceUploader {
    /**
     * Get a resource uploader.
     *
     * @return A {@link CclResourceUploader} object to be used to upload CCL resources.
     */
    public static CclResourceUploader getUploader() {
        @SuppressWarnings("unchecked")
        final Enumeration<CclResourceUploader> providers = Service.providers(CclResourceUploader.class);
        if (!providers.hasMoreElements())
            throw new IllegalStateException("No implementations found of: " + CclResourceUploader.class.getName());

        return providers.nextElement();
    }

    /**
     * Queue a file for upload.
     *
     * @param file
     *            A {@link File} object representing the file to be upload.
     * @throws NullPointerException
     *             If the given file is {@code null}.
     */
    public abstract void queueUpload(File file);

    /**
     * Upload all queued files.
     *
     * @return A {@link Map}; the keys are the files on the local directory that have been uploaded, and the values are
     *         {@link URI} objects that are the paths on the remote system to which the files were uploaded.
     */
    public abstract Map<File, URI> upload();
}
