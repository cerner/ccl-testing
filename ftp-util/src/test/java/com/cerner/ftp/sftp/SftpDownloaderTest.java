package com.cerner.ftp.sftp;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.cerner.ftp.data.FtpProduct;
import com.cerner.ftp.data.sftp.KeyCryptoBuilder;
import com.cerner.ftp.data.sftp.UserPassBuilder;
import com.cerner.ftp.data.sftp.impl.SimpleKeyCryptoBuilder;
import com.cerner.ftp.data.sftp.impl.SimpleUserPassBuilder;

/**
 * Automated unit test of {@link SftpDownloader}.
 *
 * @author Joshua Hyde
 *
 */

public class SftpDownloaderTest {
    /**
     * Test that creating a downloader for an invalid product type fails.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateDownloaderBadProduct() {
        SftpDownloader.createDownloader(mock(FtpProduct.class));
    }

    /**
     * Test that creating a downloader for a null product fails.
     */
    @Test(expected = NullPointerException.class)
    public void testCreateDownloaderNull() {
        SftpDownloader.createDownloader(null);
    }

    /**
     * Verify that the correct implementation of {@link KeyCryptoBuilder} is returned.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetKeyCryptoBuilder() {
        assertThat(SftpDownloader.getKeyCryptoBuilder()).isInstanceOf(SimpleKeyCryptoBuilder.class);
    }

    /**
     * Verify that the correct implementation of {@link UserPassBuilder} is returned.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetUserPassBuilder() {
        assertThat(SftpDownloader.getUserPassBuilder()).isInstanceOf(SimpleUserPassBuilder.class);
    }

}
