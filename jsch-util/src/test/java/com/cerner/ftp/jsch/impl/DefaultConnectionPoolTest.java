package com.cerner.ftp.jsch.impl;

import static org.mockito.Mockito.verify;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.cerner.ftp.jsch.Connection;
import com.cerner.ftp.jsch.impl.DefaultConnectionPool.ConnectionLibrarian;

/**
 * Unit tests for {@link DefaultConnectionPoolTest}.
 *
 * @author Joshua Hyde
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class DefaultConnectionPoolTest {
    @Mock
    private ConnectionLibrarian<Connection> librarian;
    private DefaultConnectionPool connectionPool;

    /**
     * Create a new connection pool for each test.
     */
    @Before
    public void setUp() {
        connectionPool = new DefaultConnectionPool(librarian);
    }

    /**
     * Verify that calls for a username/password connection are passed to the underlying librarian.
     */
    @Test
    public void testGetConnectionUsernamePassword() {
        final String username = "username";
        final String password = "password";
        final URI serverAddress = URI.create("some.server");

        connectionPool.getConnection(username, password, serverAddress);
        verify(librarian).checkOut(username, password, serverAddress);
    }

    /**
     * Verify that calls for a username/private key connection are passed to the underlying librarian.
     */
    @Test
    public void testGetConnectionUsernamePrivateKey() {
        final String username = "username";
        final String salt = "salt";
        final URI privateKeyLocation = URI.create("private.key");
        final URI serverAddress = URI.create("remote.server");

        connectionPool.getConnection(username, salt, privateKeyLocation, serverAddress);
        verify(librarian).checkOut(username, salt, privateKeyLocation, serverAddress);
    }

}
