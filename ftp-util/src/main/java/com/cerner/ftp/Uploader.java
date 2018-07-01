package com.cerner.ftp;

import java.util.Collection;

import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.exception.ConnectionException;
import com.cerner.ftp.exception.TransferException;
import com.cerner.ftp.util.PermissionsBuilder;

/**
 * Definition of an object that is used to upload files.
 *
 * @author Joshua Hyde
 *
 */
public interface Uploader {
    /**
     * A bitwise representation of 777 (god permissions) for UNIX file systems.
     */
    int GOD_PERMISSIONS = PermissionsBuilder.build(7, 7, 7);

    /**
     * Set the uploader to ignore errors that occur during permissions setting. By default, this property of the
     * uploader is {@code false}.
     *
     * @param ignoreChmodErrors
     *            A {@code boolean} value. {@code true} indicates that errors encountered while trying to chmod a file
     *            that has been uploaded should be ignored.
     */
    void ignoreChmodErrors(boolean ignoreChmodErrors);

    /**
     * Set the permissions level to be set on each file uploaded to the remote server. By default, these will be god
     * permissions (777), in which every user has read and write access to the file. <br>
     * Although you can generate your own bitwise representation of permissions, it is recommended you use
     * {@link PermissionsBuilder#build(int, int, int)} to generate your permissions.
     *
     * @param permissions
     *            An {@code int} representing the bitwise representation of permissions on the file when it is uploaded
     *            to the remote server.
     */
    void setPermissions(int permissions);

    /**
     * Upload all requested files.
     *
     * @param requests
     *            A {@link Collection} of objects that are or extend {@link FileRequest}.
     * @throws ConnectionException
     *             If an error occurs while attempting to establish the connection.
     * @throws TransferException
     *             If an error occurs during the transfer of files.
     */
    void upload(Collection<? extends FileRequest> requests);
}
