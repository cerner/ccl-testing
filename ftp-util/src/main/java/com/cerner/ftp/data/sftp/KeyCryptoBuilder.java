package com.cerner.ftp.data.sftp;

import java.net.URI;

import com.cerner.ftp.data.FtpProduct;
import com.cerner.ftp.data.sftp.impl.SimpleKeyCryptoBuilder;

/**
 * An object that controls the behavior of the SFTP downloader using public and private keys.
 *
 * @author Joshua Hyde
 *
 */
public abstract class KeyCryptoBuilder {
    /**
     * A read-only snapshot of the {@link KeyCryptoBuilder} object that constructs it at the time of construction.
     *
     * @author Joshua Hyde
     *
     */
    public interface KeyCryptoProduct extends FtpProduct {
        /**
         * Get the salt used to generate the private key.
         *
         * @return The key salt.
         */
        String getKeySalt();

        /**
         * Get the location of the private key.
         *
         * @return A {@link URI} object representing the location of the private key file.
         */
        URI getPrivateKey();
    }

    /**
     * Get an instance of a key crypto builder.
     *
     * @return An instance of a {@link KeyCryptoBuilder} implementation.
     */
    public static KeyCryptoBuilder getBuilder() {
        return new SimpleKeyCryptoBuilder();
    }

    /**
     * Construct a product out of the currently configuration stored within this builder.
     *
     * @return An instance of an {@link KeyCryptoProduct} object.
     * @throws IllegalStateException
     *             If the builder has not yet been fully fleshed-out and properly configured.
     */
    public abstract KeyCryptoProduct build();

    /**
     * Set the salt passphrase used in the generation of the private key.
     *
     * @param keySalt
     *            The passphrase used in the generation of the private key.
     * @return This object.
     * @throws NullPointerException
     *             If the given string is {@code null}.
     */
    public abstract KeyCryptoBuilder setKeySalt(String keySalt);

    /**
     * Set the location of the private key.
     *
     * @param privateKey
     *            A {@link URI} object representing the location of the private key.
     * @return This object.
     * @throws NullPointerException
     *             If the given URI is {@code null}.
     */
    public abstract KeyCryptoBuilder setPrivateKey(URI privateKey);

    /**
     * Set the address of the server on which the SFTP server exists.
     *
     * @param serverAddress
     *            A {@link URI} object representing the location of the server.
     * @return This object.
     * @throws NullPointerException
     *             If the given URI is {@code null}.
     */
    public abstract KeyCryptoBuilder setServerAddress(URI serverAddress);

    /**
     * Set the username to be used to log into the remote SFTP server.
     *
     * @param username
     *            The username.
     * @return This object.
     * @throws NullPointerException
     *             If the given username is {@code null}.
     */
    public abstract KeyCryptoBuilder setUsername(String username);
}
