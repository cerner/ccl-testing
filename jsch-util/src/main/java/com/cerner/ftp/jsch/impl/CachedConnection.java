package com.cerner.ftp.jsch.impl;

import java.io.File;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import com.cerner.ftp.jsch.Connection;
import com.cerner.ftp.jsch.impl.DefaultConnectionPool.ConnectionLibrarian;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * Implementation of {@link Connection}.
 *
 * @author Joshua Hyde
 *
 */

public class CachedConnection implements Connection {
    private final Set<Channel> channels = new HashSet<Channel>();
    private final Session session;
    private final ConnectionLibrarian<CachedConnection> librarian;
    private boolean closed;

    /**
     * Create a connection that uses a username/password form of authentication.
     *
     * @param username
     *            The username.
     * @param password
     *            The password.
     * @param serverAddress
     *            A {@link URI} object representing the server address.
     * @param librarian
     *            The {@link ConnectionLibrarian} that manages this connection.
     */
    public CachedConnection(final String username, final String password, final URI serverAddress,
            final ConnectionLibrarian<CachedConnection> librarian) {
        this.librarian = librarian;
        this.session = createSession(username, password, serverAddress);
    }

    /**
     * Create a connection that uses a username/private key form of authentication.
     *
     * @param username
     *            The username.
     * @param salt
     *            The salt used to create private key.
     * @param privateKeyLocation
     *            The location of the private key.
     * @param serverAddress
     *            The address of the server.
     * @param librarian
     *            The {@link ConnectionLibrarian} that manages this connection.
     */
    public CachedConnection(final String username, final String salt, final URI privateKeyLocation,
            final URI serverAddress, final ConnectionLibrarian<CachedConnection> librarian) {
        this.librarian = librarian;
        this.session = createSession(username, salt, privateKeyLocation, serverAddress);
    }

    @Override
    public void close() {
        closed = true;

        for (final Channel channel : channels) {
            if (!channel.isClosed()) {
                channel.disconnect();
            }
        }

        channels.clear();

        librarian.checkIn(this);
    }

    /**
     * Physically close the connection wrapped by this object.
     */
    public void closePhysical() {
        closed = true;

        if (session != null) {
            session.disconnect();
        }
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CachedConnection other = (CachedConnection) obj;
        if (session == null) {
            if (other.session != null) {
                return false;
            }
        } else if (!session.equals(other.session)) {
            return false;
        }
        return true;
    }

    @Override
    public ChannelSftp getSFtp() {
        if (isClosed()) {
            throw new IllegalStateException("Connection is closed.");
        }

        try {
            final ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
            channels.add(sftp);
            return sftp;
        } catch (final JSchException e) {
            throw new RuntimeException("Failed to open SFTP channel.", e);
        }
    }

    @Override
    public ChannelShell getShell() {
        if (isClosed()) {
            throw new IllegalStateException("Connection is closed.");
        }

        try {
            final ChannelShell shell = (ChannelShell) session.openChannel("shell");
            channels.add(shell);
            return shell;
        } catch (final JSchException e) {
            throw new RuntimeException("Failed to open shell channel.", e);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((session == null) ? 0 : session.hashCode());
        return result;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    /**
     * Create a session using username/password authentication.
     *
     * @param username
     *            The username.
     * @param password
     *            The password.
     * @param serverAddress
     *            A {@link URI} object representing the location of the remote server.
     * @return A {@link Session} object representing a connection to the remote server.
     */
    private Session createSession(final String username, final String password, final URI serverAddress) {
        JSch.setLogger(new MyLogger());
        final JSch jsch = new JSch();
        try {
            System.out.println("username: " + username);
            System.out.println("password: " + password);
            System.out.println("serverAddress: " + serverAddress);
            System.out.println("serverAddress.getPath(): " + serverAddress.getPath());
            JSch.setConfig("PreferredAuthentications", "password");
            final Session session = jsch.getSession(username, serverAddress.getPath(), 22);
            session.setUserInfo(new SimpleUserInfo(password, null));
            session.connect();
            return session;
        } catch (final JSchException e) {
            throw new RuntimeException("Failed to establish connection with username/password authentication.", e);
        }
    }

    /**
     * JSch Logger
     *
     * @author Fred Eckertson
     *
     */
    public static class MyLogger implements com.jcraft.jsch.Logger {
        static java.util.Hashtable<Integer, String> name = new java.util.Hashtable<Integer, String>();
        static {
            name.put(new Integer(DEBUG), "DEBUG: ");
            name.put(new Integer(INFO), "INFO: ");
            name.put(new Integer(WARN), "WARN: ");
            name.put(new Integer(ERROR), "ERROR: ");
            name.put(new Integer(FATAL), "FATAL: ");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isEnabled(final int level) {
            // TODO: make this configurable.
            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void log(final int level, final String message) {
            System.err.print(name.get(new Integer(level)));
            System.err.println(message);
        }
    }

    /**
     * Create a session using username/private key authentication.
     *
     * @param username
     *            The username.
     * @param salt
     *            The salt used to create the private key.
     * @param privateKeyLocation
     *            A {@link URI} object representing the location of the private key.
     * @param serverAddress
     *            A {@link URI} object representing the location of the remote server.
     * @return A {@link Session} object representing a connnection to the remote server.
     */
    private Session createSession(final String username, final String salt, final URI privateKeyLocation,
            final URI serverAddress) {
        final JSch jsch = new JSch();
        try {
            JSch.setConfig("PreferredAuthentications", "publickey");
            jsch.addIdentity(new File(privateKeyLocation).getAbsolutePath());
            final Session session = jsch.getSession(username, serverAddress.getPath(), 22);
            session.setUserInfo(new SimpleUserInfo(null, salt));
            session.connect();
            return session;
        } catch (final JSchException e) {
            throw new RuntimeException("Failed to establish connection with private key authentication.", e);
        }
    }

    /**
     * Mark the connection as opened.
     *
     * @throws IllegalStateException
     *             If the connection wrapped by this object is either absent or has been physically closed.
     */
    void open() {
        if (session == null || !session.isConnected()) {
            throw new IllegalStateException("Connection is either unavailable or physically closed.");
        }

        closed = false;
    }

    /**
     * Simple implementation of {@link UserInfo}.
     *
     * @author Joshua Hyde
     *
     */
    private static class SimpleUserInfo implements UserInfo {
        private final String password;
        private final String salt;

        /**
         * Create a user info object.
         *
         * @param password
         *            The password.
         * @param salt
         *            The salt.
         */
        public SimpleUserInfo(final String password, final String salt) {
            this.password = password;
            this.salt = salt;
        }

        @Override
        public String getPassphrase() {
            return salt;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public boolean promptPassphrase(final String message) {
            return true;
        }

        @Override
        public boolean promptPassword(final String message) {
            return true;
        }

        @Override
        public boolean promptYesNo(final String message) {
            return true;
        }

        @Override
        public void showMessage(final String message) {
        }
    }

}
