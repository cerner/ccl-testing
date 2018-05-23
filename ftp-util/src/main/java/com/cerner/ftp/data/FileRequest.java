package com.cerner.ftp.data;

import java.net.URI;

/**
 * Definition of a request for a file.
 *
 * @author Joshua Hyde
 *
 */

public interface FileRequest {
    /**
     * Get the location of the source file (the file to be uploaded or downloaded).
     *
     * @return A {@link URI} representing the location of the source file.
     */
    URI getSourceFile();

    /**
     * Get the location of the target file (the location to which a file is to be downloaded or uploaded).
     *
     * @return A {@link URI} representing the location of the target file.
     */
    URI getTargetFile();
}
