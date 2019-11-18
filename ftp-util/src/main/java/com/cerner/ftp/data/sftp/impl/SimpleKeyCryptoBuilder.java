package com.cerner.ftp.data.sftp.impl;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import com.cerner.ftp.data.sftp.KeyCryptoBuilder;

/**
 * This is a very simple, re-usable builder.
 *
 * @author Joshua Hyde
 *
 */

public class SimpleKeyCryptoBuilder extends KeyCryptoBuilder {
    private static final int NUM_ATTRIBUTES = Attribute.values().length;
    private String keySalt;
    private URI privateKey;
    private URI serverAddress;
    private String username;

    private final Set<Attribute> setAttributes = new HashSet<Attribute>(NUM_ATTRIBUTES);

    /**
     * Enumerations of the attributes stored by this builder and its product.
     *
     * @author Joshua Hyde
     *
     */
    private static enum Attribute {
        /**
         * The key salt.
         */
        KEY_SALT,
        /**
         * The location of the private key.
         */
        PRIVATE_KEY,
        /**
         * The address of the SFTP server.
         */
        SERVER_ADDRESS,
        /**
         * The username.
         */
        USERNAME;
    }

    @Override
    public KeyCryptoProduct build() {
        for (final Attribute a : Attribute.values()) {
            if (!isSet(a)) {
                throw new IllegalStateException("Unset attribute: " + a.toString());
            }
        }

        return new KeyCryptoProductImpl(username, keySalt, privateKey, serverAddress);
    }

    @Override
    public KeyCryptoBuilder setKeySalt(final String keySalt) {
        if (keySalt == null) {
            throw new NullPointerException("Key salt cannot be null.");
        }

        this.keySalt = keySalt;
        markAsSet(Attribute.KEY_SALT);
        return this;
    }

    @Override
    public KeyCryptoBuilder setPrivateKey(final URI privateKey) {
        if (privateKey == null) {
            throw new NullPointerException("Private key location cannot be null.");
        }

        this.privateKey = privateKey;
        markAsSet(Attribute.PRIVATE_KEY);
        return this;
    }

    @Override
    public KeyCryptoBuilder setServerAddress(final URI serverAddress) {
        if (serverAddress == null) {
            throw new NullPointerException("Server address cannot be null.");
        }

        this.serverAddress = serverAddress;
        markAsSet(Attribute.SERVER_ADDRESS);
        return this;
    }

    @Override
    public KeyCryptoBuilder setUsername(final String username) {
        if (username == null) {
            throw new NullPointerException("Username cannot be null.");
        }

        this.username = username;
        markAsSet(Attribute.USERNAME);
        return this;
    }

    /**
     * Determine whether or not an attribute has been set.
     *
     * @param attribute
     *            A {@link Attribute} enum.
     * @return {@code true} if the attribute has been set; {@code false} if it has not.
     */
    private boolean isSet(final Attribute attribute) {
        return setAttributes.contains(attribute);
    }

    /**
     * Mark an attribute as set.
     *
     * @param attribute
     *            A {@link Attribute} enum.
     */
    private void markAsSet(final Attribute attribute) {
        setAttributes.add(attribute);
    }

    /**
     * An implementation of {@link com.cerner.ftp.data.sftp.KeyCryptoBuilder.KeyCryptoProduct}.
     *
     * @author Joshua Hyde
     *
     */
    public static class KeyCryptoProductImpl implements KeyCryptoProduct {
        private final String username;
        private final String keySalt;
        private final URI privateKey;
        private final URI serverAddress;

        /**
         * Construct a product.
         *
         * @param username
         *            The username to be used to log in.
         * @param keySalt
         *            The salt used in the passphrase generation.
         * @param privateKey
         *            The location of the private key.
         * @param serverAddress
         *            The location of the address.
         */
        public KeyCryptoProductImpl(final String username, final String keySalt, final URI privateKey,
                final URI serverAddress) {
            this.username = username;
            this.keySalt = keySalt;
            this.privateKey = privateKey;
            this.serverAddress = serverAddress;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getKeySalt() {
            return keySalt;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public URI getPrivateKey() {
            return privateKey;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public URI getServerAddress() {
            return serverAddress;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getUsername() {
            return username;
        }

    }

}
