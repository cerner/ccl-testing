package com.cerner.ftp.jsch;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;

/**
 * Definition of an object representing a JSch connection. <br>
 * This object is intended to be a <i>short-lived</i> object; that is, do not retain a reference to this object.
 * {@link #close() Close} it immediately once you are done with it to free any resources allocated to it as part of its
 * run while under your ownership.
 *
 * @author Joshua Hyde
 *
 */

public interface Connection {
    /**
     * Get a shell terminal used to execute commands on a remote server over SSH.
     *
     * @return A {@link ChannelShell} object.
     * @throws IllegalStateException
     *             If this connection is {@link #isClosed() closed}.
     */
    ChannelShell getShell();

    /**
     * Get an SFTP channel to be used to upload or download files to and from a remote server using FTP over SSH.
     *
     * @return A {@link ChannelSftp} object.
     * @throws IllegalStateException
     *             If this connection is {@link #isClosed() closed}.
     */
    ChannelSftp getSFtp();

    /**
     * Close this connection. This may either physically close the connection or return it to a pool.
     */
    void close();

    /**
     * Determine whether or not this connection is closed.
     *
     * @return {@code true} if the connection is closed; {@code false} if it is still open.
     */
    boolean isClosed();
}
