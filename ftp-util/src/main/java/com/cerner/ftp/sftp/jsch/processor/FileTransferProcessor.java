package com.cerner.ftp.sftp.jsch.processor;

import com.cerner.ftp.exception.TransferException;
import com.jcraft.jsch.ChannelSftp;

/**
 * A generic interface that defines anything that should process an FTP operation.
 *
 * @author Joshua Hyde
 *
 */
public interface FileTransferProcessor {
    /**
     * Run a set of FTP operations using the given channel.
     *
     * @param channel
     *            A {@link ChannelSftp} object to be used to perform the FTP operations.
     * @throws TransferException
     *             If any errors occur in the FTP operation.
     */
    void run(ChannelSftp channel);
}
