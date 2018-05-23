package com.cerner.ftp.sftp;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import com.cerner.ftp.data.FtpProduct;
import com.cerner.ftp.data.sftp.KeyCryptoBuilder;
import com.cerner.ftp.data.sftp.UserPassBuilder;
import com.cerner.ftp.data.sftp.impl.SimpleKeyCryptoBuilder;

/**
 * Unit test of {@link SftpUploader}.
 *
 * @author Joshua Hyde
 *
 */

public class SftpUploaderTest {
    /**
     * Test that creating an uploader with a bad product type.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCreateUploaderBadProduct() {
        SftpUploader.createUploader(mock(FtpProduct.class));
    }

    /**
     * Test that creating an uploader with a null product fails.
     */
    @Test(expected = NullPointerException.class)
    public void testCreateUploaderNullProduct() {
        SftpUploader.createUploader(null);
    }

    /**
     * Verify that correct implementation of {@link KeyCryptoBuilder} is returned.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetKeyCryptoBuilder() {
        assertThat(SftpUploader.getKeyCryptoBuilder()).isInstanceOf(SimpleKeyCryptoBuilder.class);
    }

    /**
     * Verify that the correct implementation of {@link UserPassBuilder} is returned.
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetUserPassBuilder() {
        assertThat(SftpUploader.getUserPassBuilder()).isInstanceOf(UserPassBuilder.class);
    }
}
