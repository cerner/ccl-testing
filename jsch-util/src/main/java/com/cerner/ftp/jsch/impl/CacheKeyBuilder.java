package com.cerner.ftp.jsch.impl;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import org.apache.commons.codec.binary.Base64;

import com.cerner.ftp.jsch.impl.DefaultConnectionPool.ConnectionLibrarian.IdentifierGenerator;

/**
 * An {@link IdentifierGenerator} that uses base-64 encoded strings out of the given data to construct keys.
 *
 * @author Joshua Hyde
 *
 */

public class CacheKeyBuilder implements IdentifierGenerator {
    /**
     * The phrase used to salt keys built out of usernames and passwords.
     */
    static final String USERNAME_PASSWORD_SALT = "username/password: ";
    /**
     * The phrase used to salt keys built out of usernames and private keys.
     */
    static final String PRIVATE_KEY_SALT = "private key: ";

    @Override
    public String buildKey(final String username, final String password, final URI serverAddress) {
        try {
            return Base64.encodeBase64String(
                    (USERNAME_PASSWORD_SALT + username + password + serverAddress.toString()).getBytes("utf-8"));
        } catch (final UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }

    @Override
    public String buildKey(final String username, final String salt, final URI privateKeyLocation,
            final URI serverAddress) {
        try {
            return Base64.encodeBase64String(
                    (PRIVATE_KEY_SALT + username + salt + privateKeyLocation.toString() + serverAddress.toString())
                            .getBytes("utf-8"));
        } catch (final UnsupportedEncodingException e) {
            throw new Error(e);
        }
    }
}
