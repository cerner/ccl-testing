package com.cerner.ftp.data;

import java.net.URI;

/**
 * A read-only snapshot of the builder that constructs it at the time of construction.
 *
 * @author Joshua Hyde
 *
 */

public interface FtpProduct {
    /**
     * Get the address of the server on which the SFTP server exists.
     *
     * @return A {@link URI} object representing the location of the address from which files are to be downloaded.
     */
    URI getServerAddress();

    /**
     * Get the username to be used to log into the server.
     *
     * @return The username to be used to log into the server.
     */
    String getUsername();
}
