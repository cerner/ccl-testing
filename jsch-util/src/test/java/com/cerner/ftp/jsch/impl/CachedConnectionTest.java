package com.cerner.ftp.jsch.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ftp.jsch.impl.DefaultConnectionPool.ConnectionLibrarian;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * Unit tests for {@link CachedConnection}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CachedConnection.class, JSch.class })
public class CachedConnectionTest {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String KEY_SALT = "key.salt";
    private static final URI SERVER_ADDRESS = URI.create("server.address");

    @Mock
    private ConnectionLibrarian<CachedConnection> librarian;
    @Mock
    private ChannelShell shell;
    @Mock
    private ChannelSftp sftp;
    @Mock
    private Session usernamePasswordSession;
    @Mock
    private Session privateKeySession;
    @Mock
    private JSch jsch;
    private URI privateKey;

    /**
     * Set up a private key file for each test.
     *
     * @throws Exception
     *             If any errors occur during the setup.
     */
    @Before
    public void setUp() throws Exception {
        privateKey = File.createTempFile("private.key", null).toURI();
    }

    /**
     * Test that a connection is created using username/password authentication.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructUsernamePassword() throws Exception {
        prepareUsernamePasswordSession();
        final ArgumentCaptor<UserInfo> userInfoCaptor = ArgumentCaptor.forClass(UserInfo.class);

        new CachedConnection(USERNAME, PASSWORD, SERVER_ADDRESS, librarian);
        verify(jsch).getSession(USERNAME, SERVER_ADDRESS.getPath(), 22);
        verify(usernamePasswordSession).setUserInfo(userInfoCaptor.capture());
        assertThat(userInfoCaptor.getValue().getPassword()).isEqualTo(PASSWORD);
        verify(usernamePasswordSession).connect();
    }

    /**
     * Test that a connection can be established using private key authentication.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructPrivateKey() throws Exception {
        preparePrivateKeySession();
        final ArgumentCaptor<UserInfo> userInfoCaptor = ArgumentCaptor.forClass(UserInfo.class);

        new CachedConnection(USERNAME, KEY_SALT, privateKey, SERVER_ADDRESS, librarian);
        verify(jsch).getSession(USERNAME, SERVER_ADDRESS.getPath(), 22);
        verify(privateKeySession).setUserInfo(userInfoCaptor.capture());
        assertThat(userInfoCaptor.getValue().getPassphrase()).isEqualTo(KEY_SALT);
        verify(privateKeySession).connect();
    }

    /**
     * Two connections with the same backing session should have the same hash code.
     */
    @Test
    public void testHashCode() {
        prepareUsernamePasswordSession();
        final CachedConnection connection = new CachedConnection(USERNAME, PASSWORD, SERVER_ADDRESS, librarian);

        assertThat(new CachedConnection(USERNAME, PASSWORD, SERVER_ADDRESS, librarian).hashCode())
                .isEqualTo(connection.hashCode());
    }

    /**
     * Test the closing of a connection.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testClose() throws Exception {
        prepareUsernamePasswordSession();
        final CachedConnection connection = new CachedConnection(USERNAME, PASSWORD, SERVER_ADDRESS, librarian);

        // By default, it should be open
        assertThat(connection.isClosed()).isFalse();

        /*
         * Open two sessions - they should be closed upon closing the connection (but only if they're already open)
         */
        final ChannelSftp closed = mock(ChannelSftp.class);
        when(closed.isClosed()).thenReturn(Boolean.TRUE);

        when(usernamePasswordSession.openChannel("sftp")).thenReturn(sftp).thenReturn(closed);
        when(usernamePasswordSession.openChannel("shell")).thenReturn(shell);

        // Queue up some created channels
        connection.getSFtp();
        connection.getSFtp();
        connection.getShell();

        connection.close();
        assertThat(connection.isClosed()).isTrue();

        verify(shell).disconnect();
        verify(sftp).disconnect();
        verify(closed, never()).disconnect();

        verify(librarian).checkIn(connection);
    }

    /**
     * Test the retrieval of an SFTP object.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetSFtp() throws Exception {
        prepareUsernamePasswordSession();
        final CachedConnection connection = new CachedConnection(USERNAME, PASSWORD, SERVER_ADDRESS, librarian);

        when(usernamePasswordSession.openChannel("sftp")).thenReturn(sftp);
        assertThat(connection.getSFtp()).isEqualTo(sftp);
    }

    /**
     * If any attempt is made to retrieve an SFTP channel on a closed connection, then it should fail.
     */
    @Test
    public void testGetSFtpClosed() {
        prepareUsernamePasswordSession();
        final CachedConnection connection = new CachedConnection(USERNAME, PASSWORD, SERVER_ADDRESS, librarian);

        connection.close();
        try {
            connection.getSFtp();
            fail("No exception thrown.");
        } catch (final IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("Connection is closed.");
        }
    }

    /**
     * Test the retrieval of a shell object.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetShell() throws Exception {
        prepareUsernamePasswordSession();
        final CachedConnection connection = new CachedConnection(USERNAME, PASSWORD, SERVER_ADDRESS, librarian);

        when(usernamePasswordSession.openChannel("shell")).thenReturn(shell);
        assertThat(connection.getShell()).isEqualTo(shell);
    }

    /**
     * Test that retrieving a shell from a closed connection fails.
     */
    @Test
    public void testGetShellClosed() {
        prepareUsernamePasswordSession();
        final CachedConnection connection = new CachedConnection(USERNAME, PASSWORD, SERVER_ADDRESS, librarian);

        connection.close();
        try {
            connection.getShell();
            fail("No exception thrown.");
        } catch (final IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("Connection is closed.");
        }
    }

    /**
     * Test a reflection of the closed state of a connection.
     */
    @Test
    public void testIsClosed() {
        prepareUsernamePasswordSession();
        final CachedConnection connection = new CachedConnection(USERNAME, PASSWORD, SERVER_ADDRESS, librarian);

        assertThat(connection.isClosed()).isFalse();
        connection.close();
        assertThat(connection.isClosed()).isTrue();
    }

    /**
     * Test the re-opening of a connection.
     */
    @Test
    public void testOpen() {
        prepareUsernamePasswordSession();
        final CachedConnection connection = new CachedConnection(USERNAME, PASSWORD, SERVER_ADDRESS, librarian);

        when(usernamePasswordSession.isConnected()).thenReturn(Boolean.TRUE);

        connection.close();
        assertThat(connection.isClosed()).isTrue();
        connection.open();
        assertThat(connection.isClosed()).isFalse();
    }

    /**
     * If the underlying session is closed, trying to open a connection should fail.
     */
    @Test
    public void testOpenClosedSession() {
        prepareUsernamePasswordSession();
        final CachedConnection connection = new CachedConnection(USERNAME, PASSWORD, SERVER_ADDRESS, librarian);

        when(usernamePasswordSession.isConnected()).thenReturn(Boolean.FALSE);
        try {
            connection.open();
            fail("No exception thrown.");
        } catch (final IllegalStateException e) {
            assertThat(e.getMessage()).isEqualTo("Connection is either unavailable or physically closed.");
        }
    }

    /**
     * Test a hard close of the connection.
     */
    @Test
    public void testClosePhysical() {
        prepareUsernamePasswordSession();
        final CachedConnection connection = new CachedConnection(USERNAME, PASSWORD, SERVER_ADDRESS, librarian);

        assertThat(connection.isClosed()).isFalse();
        connection.closePhysical();
        assertThat(connection.isClosed()).isTrue();
    }

    /**
     * Test that, if establish a connection using username/password authentication, that the appropriate exception is
     * thrown.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unused")
    @Test
    public void testCreateConnectionUsernamePasswordFails() throws Exception {
        whenNew(JSch.class).withNoArguments().thenReturn(jsch);
        when(jsch.getSession(USERNAME, SERVER_ADDRESS.getPath(), 22)).thenThrow(new JSchException());

        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            new CachedConnection(USERNAME, PASSWORD, SERVER_ADDRESS, librarian);
        });
        assertThat(e.getMessage()).isEqualTo("Failed to establish connection with username/password authentication.");
    }

    /**
     * Test that, if creating a connection using private key authentication fails, then the appropriate exception is
     * thrown.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unused")
    @Test
    public void testCreateConnectionPrivateKeyFails() throws Exception {
        whenNew(JSch.class).withNoArguments().thenReturn(jsch);
        doThrow(new JSchException()).when(jsch).addIdentity(anyString());

        RuntimeException e = assertThrows(RuntimeException.class, () -> {
            new CachedConnection(USERNAME, KEY_SALT, privateKey, SERVER_ADDRESS, librarian);
        });
        assertThat(e.getMessage()).isEqualTo("Failed to establish connection with private key authentication.");
    }

    /**
     * Prepare mock objects for creating a username/password connection.
     *
     * @throws RuntimeException
     *             If any errors occur during the mock set-up.
     */
    private void prepareUsernamePasswordSession() {
        try {
            whenNew(JSch.class).withNoArguments().thenReturn(jsch);
            when(jsch.getSession(USERNAME, SERVER_ADDRESS.getPath(), 22)).thenReturn(usernamePasswordSession);
        } catch (final Exception e) {
            throw new RuntimeException("Failed to prepare a username/password session.", e);
        }
    }

    /**
     * Prepare mock objects for creating a private key connection.
     *
     * @throws RuntimeException
     *             If any errors occur during the mock set-up.
     */
    private void preparePrivateKeySession() {
        try {
            whenNew(JSch.class).withNoArguments().thenReturn(jsch);
            when(jsch.getSession(USERNAME, SERVER_ADDRESS.getPath(), 22)).thenReturn(privateKeySession);
        } catch (final Exception e) {
            throw new RuntimeException("Failed to prepare a private key session.", e);
        }
    }
}
