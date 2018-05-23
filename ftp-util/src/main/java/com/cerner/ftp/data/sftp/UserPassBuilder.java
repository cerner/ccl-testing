package com.cerner.ftp.data.sftp;

import java.net.URI;

import com.cerner.ftp.data.FtpProduct;
import com.cerner.ftp.data.sftp.impl.SimpleUserPassBuilder;

/**
 * An object that controls the behavior of the SFTP downloader using username and password to authenticate and
 * authorize.
 *
 * @author Joshua Hyde
 *
 */
public abstract class UserPassBuilder {
    /**
     * A read-only snapshot of the {@link UserPassBuilder} object that constructs it at the time of construction.
     *
     * @author Joshua Hyde
     *
     */
    public interface UserPassProduct extends FtpProduct {
        /**
         * Get the password to be used to log into the remote server.
         *
         * @return The password.
         */
        String getPassword();
    }

    /**
     * Get an instance of a username/password builder.
     *
     * @return A instance of a {@link UserPassBuilder} implementation.
     */
    public static UserPassBuilder getBuilder() {
        return new SimpleUserPassBuilder();
    }

    /**
     * Construct a product out of the currently configuration stored within this builder.
     *
     * @return An instance of an {@link UserPassProduct} object.
     * @throws IllegalStateException
     *             If the builder has not yet been fully fleshed-out and properly configured.
     */
    public abstract UserPassProduct build();

    /**
     * Set the password to be used to log into the remote server.
     *
     * @param password
     *            The password.
     * @return This object.
     * @throws NullPointerException
     *             If the given password is {@code null}.
     */
    public abstract UserPassBuilder setPassword(String password);

    /**
     * Set the address of the server on which the SFTP server exists.
     *
     * @param serverAddress
     *            A {@link URI} object representing the location of the server.
     * @return This object.
     * @throws NullPointerException
     *             If the given URI is {@code null}.
     */
    public abstract UserPassBuilder setServerAddress(URI serverAddress);

    /**
     * Set the username to be used to log into the remote SFTP server.
     *
     * @param username
     *            The username.
     * @return This object.
     * @throws NullPointerException
     *             If the given username is {@code null}.
     */
    public abstract UserPassBuilder setUsername(String username);
}
