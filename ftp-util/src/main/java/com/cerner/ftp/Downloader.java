package com.cerner.ftp;

import java.util.Collection;

import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.exception.ConnectionException;
import com.cerner.ftp.exception.TransferException;

/**
 * A generic definition of any object that performs downloads.
 *
 * @author Joshua Hyde
 *
 */
public interface Downloader {
    /**
     * Download all requested files.
     *
     * @param requests
     *            A {@link Collection} of objects that are or extend {@link FileRequest}.
     * @throws ConnectionException
     *             If an error occurs while attempting to establish the connection.
     * @throws TransferException
     *             If an error occurs during the transfer of files.
     */
    void download(Collection<? extends FileRequest> requests);
}
