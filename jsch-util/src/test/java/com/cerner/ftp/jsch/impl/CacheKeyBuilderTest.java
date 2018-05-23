package com.cerner.ftp.jsch.impl;

import static org.fest.assertions.Assertions.assertThat;

import java.net.URI;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link CacheKeyBuilder}.
 *
 * @author Joshua Hyde
 *
 */

public class CacheKeyBuilderTest {
    private CacheKeyBuilder builder;

    /**
     * Create a new builder for each test.
     */
    @Before
    public void setUp() {
        builder = new CacheKeyBuilder();
    }

    /**
     * Test construction of a key for a username/password authentication pairing.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testBuildKeyUsernamePassword() throws Exception {
        final String username = "username";
        final String password = "password";
        final URI serverAddress = URI.create("www.google.com");

        final String expected = Base64.encodeBase64String(
                (CacheKeyBuilder.USERNAME_PASSWORD_SALT + username + password + serverAddress.toString())
                        .getBytes("utf-8"));
        assertThat(builder.buildKey(username, password, serverAddress)).isEqualTo(expected);
    }

    /**
     * Test construction of a key for a username/private key authentication pairing.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testBuildKeyUsernamePrivateKey() throws Exception {
        final String username = "username";
        final String salt = "salt";
        final URI privateKeyLocation = URI.create("/somewhere/on/ze/disk");
        final URI serverAddress = URI.create("www.google.com");

        final String expected = Base64.encodeBase64String((CacheKeyBuilder.PRIVATE_KEY_SALT + username + salt
                + privateKeyLocation.toString() + serverAddress.toString()).getBytes("utf-8"));
        assertThat(builder.buildKey(username, salt, privateKeyLocation, serverAddress)).isEqualTo(expected);
    }

}
