package com.cerner.ftp.sftp;

import com.cerner.ftp.Downloader;
import com.cerner.ftp.data.FtpProduct;
import com.cerner.ftp.data.sftp.KeyCryptoBuilder;
import com.cerner.ftp.data.sftp.KeyCryptoBuilder.KeyCryptoProduct;
import com.cerner.ftp.data.sftp.UserPassBuilder;
import com.cerner.ftp.data.sftp.UserPassBuilder.UserPassProduct;
import com.cerner.ftp.data.sftp.impl.SimpleKeyCryptoBuilder;
import com.cerner.ftp.data.sftp.impl.SimpleUserPassBuilder;
import com.cerner.ftp.sftp.jsch.JschDownloader;
import com.cerner.ftp.sftp.jsch.processor.JschSftpProcessor;

/**
 * A factory object that provides access to downloaders that use SFTP over SSH to access files.
 *
 * @author Joshua Hyde
 *
 */

public final class SftpDownloader {

    /**
     * Private constructor to prevent instantiation.
     */
    private SftpDownloader() {
    }

    /**
     * Create an SFTP downloader that uses public/private key encryption for the given attributes.
     *
     * @param product
     *            A {@link FtpProduct} object representing the attributes that will determine the behavior of the
     *            created downloader.
     * @return A {@link Downloader}.
     * @throws NullPointerException
     *             If the given product is {@code null}.
     * @throws IllegalArgumentException
     *             If the product is not a {@link KeyCryptoProduct} or {@link UserPassProduct}.
     */
    public static Downloader createDownloader(final FtpProduct product) {
        if (product == null)
            throw new NullPointerException("Product cannot be null.");

        if (!(product instanceof UserPassProduct) && !(product instanceof KeyCryptoProduct))
            throw new IllegalArgumentException("Invalid product type: " + product.getClass());

        return new JschDownloader(JschSftpProcessor.getProcessor(product));
    }

    /**
     * Get a builder that can be used to construct an SFTP downloader that uses private/public keys to authenticate and
     * authorize a user.
     *
     * @return A {@link KeyCryptoBuilder} implementation.
     * @deprecated Use {@link KeyCryptoBuilder#getBuilder()} instead.
     */
    @Deprecated
    public static KeyCryptoBuilder getKeyCryptoBuilder() {
        return new SimpleKeyCryptoBuilder();
    }

    /**
     * Get a builder that can be used to construct an SFTP downloader that uses a username and password for
     * authentication and authorization.
     *
     * @return A {@link UserPassBuilder} implementation.
     * @deprecated Use {@link UserPassBuilder#getBuilder()} instead.
     */
    @Deprecated
    public static UserPassBuilder getUserPassBuilder() {
        return new SimpleUserPassBuilder();
    }
}
