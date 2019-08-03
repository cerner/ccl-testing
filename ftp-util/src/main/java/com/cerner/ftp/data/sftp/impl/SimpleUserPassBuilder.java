package com.cerner.ftp.data.sftp.impl;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import com.cerner.ftp.data.sftp.UserPassBuilder;

/**
 * A simple, reusable implementation of {@link UserPassBuilder}.
 *
 * @author Joshua Hyde
 *
 */

public class SimpleUserPassBuilder extends UserPassBuilder {
    private static final int NUM_ATTRIBUTES = Attribute.values().length;

    private String username;
    private String password;
    private URI serverAddress;
    private final Set<Attribute> attributes = new HashSet<Attribute>(NUM_ATTRIBUTES);

    /**
     * An enumeration of the attributes stored by this builder.
     *
     * @author Joshua Hyde
     *
     */
    private static enum Attribute {
        /**
         * The password to be used to log into the server.
         */
        PASSWORD,
        /**
         * The address of the remote server.
         */
        SERVER_ADDRESS,
        /**
         * The username to be used to log into the remote server.
         */
        USERNAME;
    }

    @Override
    public UserPassBuilder setPassword(final String password) {
        if (password == null) {
			throw new NullPointerException("Password cannot be null.");
		}

        this.password = password;
        markAsSet(Attribute.PASSWORD);
        return this;
    }

    @Override
    public UserPassProduct build() {
        for (final Attribute a : Attribute.values()) {
			if (!isSet(a)) {
				throw new IllegalStateException("Unset attribute: " + a.toString());
			}
		}

        return new UserPassProductImpl(username, password, serverAddress);
    }

    @Override
    public UserPassBuilder setServerAddress(final URI serverAddress) {
        if (serverAddress == null) {
			throw new NullPointerException("Server address cannot be null.");
		}

        this.serverAddress = serverAddress;
        markAsSet(Attribute.SERVER_ADDRESS);
        return this;
    }

    @Override
    public UserPassBuilder setUsername(final String username) {
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
     *            The {@link Attribute} whose set status is to be determined.
     * @return {@code true} if the attribute has been marked as set; {@code
     *         false} if it has not.
     */
    private boolean isSet(final Attribute attribute) {
        return attributes.contains(attribute);
    }

    /**
     * Mark an attribute as set.
     *
     * @param attribute
     *            The {@link Attribute} to be marked as set.
     */
    private void markAsSet(final Attribute attribute) {
        attributes.add(attribute);
    }

    /**
     * An implementation of {@link UserPassProduct}.
     *
     * @author Joshua Hyde
     *
     */
    private static class UserPassProductImpl implements UserPassProduct {
        private final String username;
        private final String password;
        private final URI serverAddress;

        /**
         * Create a product.
         *
         * @param username
         *            The username.
         * @param password
         *            The password.
         * @param serverAddress
         *            The address at which the remote server resides.
         */
        public UserPassProductImpl(final String username, final String password, final URI serverAddress) {
            this.username = username;
            this.password = password;
            this.serverAddress = serverAddress;
        }

        /**
         * {@inheritDoc}
         */
		@Override
		public String getPassword() {
            return password;
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
