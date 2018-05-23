package com.cerner.ftp.data.sftp.impl;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import com.cerner.ftp.data.sftp.KeyCryptoBuilder;
import com.cerner.ftp.data.sftp.KeyCryptoBuilder.KeyCryptoProduct;

/**
 * An automated unit test of {@link SimpleKeyCryptoBuilder}.
 *
 * @author Joshua Hyde
 *
 */

public class SimpleKeyCryptoBuilderTest {
    private KeyCryptoBuilder builder;

    /**
     * Set up a new instance of the simple builder.
     */
    @Before
    public void setUp() {
        builder = new SimpleKeyCryptoBuilder();
    }

    /**
     * Test the construction of a product.
     */
    @Test
    public void testBuild() {
        final String username = "user";
        final String salt = "salt";
        final URI privateKey = new File("temp/id_dsa").toURI();
        final URI serverAddress = URI.create("sftp://sftp.github.com");

        final KeyCryptoProduct product = builder.setKeySalt(salt).setUsername(username).setPrivateKey(privateKey)
                .setServerAddress(serverAddress).build();

        assertEquals("Username does not match.", username, product.getUsername());
        assertEquals("Salt does not match.", salt, product.getKeySalt());
        assertEquals("Private key location does not match.", privateKey.toString(), product.getPrivateKey().toString());
        assertEquals("Server address does not match.", serverAddress.toString(), product.getServerAddress().toString());
    }

    /**
     * Test that the builder fails if not all attributes have been set.
     */
    @Test(expected = IllegalStateException.class)
    public void testInvalidBuild() {
        builder.build();
    }

    /**
     * Verify that the builder fails for {@code null} key salts.
     */
    @Test(expected = NullPointerException.class)
    public void testNullKeySalt() {
        builder.setKeySalt(null);
    }

    /**
     * Verify that the builder fails for {@code null} private key locations.
     */
    @Test(expected = NullPointerException.class)
    public void testNullPrivateKeyLocation() {
        builder.setPrivateKey(null);
    }

    /**
     * Verify that the builder fails for {@code null} server address.
     */
    @Test(expected = NullPointerException.class)
    public void testNullServerAddress() {
        builder.setServerAddress(null);
    }

    /**
     * Verify that the builder fails for {@code null} usernames.
     */
    @Test(expected = NullPointerException.class)
    public void testNullUsername() {
        builder.setUsername(null);
    }
}
