package com.cerner.ftp.sftp.jsch.processor;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.cerner.ftp.data.sftp.UserPassBuilder.UserPassProduct;
import com.cerner.ftp.jsch.Connection;
import com.cerner.ftp.sftp.jsch.processor.internal.StubbedConnectionPool;

/**
 * Unit tests for {@link UserPassSftpProcessor}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class UserPassSftpProcessorTest {
    private static final String USERNAME = "user.name";
    private static final String PASSWORD = "key.salt";
    private static final URI SERVER_ADDRESS = URI.create("http://www.google.com");

    @Mock
    private StubbedConnectionPool pool;
    @Mock
    private Connection connection;
    @Mock
    private UserPassProduct product;
    private UserPassSftpProcessor processor;

    /**
     * Set up the connection pool and product for each test.
     */
    @Before
    public void setUp() {
        when(product.getUsername()).thenReturn(USERNAME);
        when(product.getPassword()).thenReturn(PASSWORD);
        when(product.getServerAddress()).thenReturn(SERVER_ADDRESS);

        when(pool.getConnection(USERNAME, PASSWORD, SERVER_ADDRESS)).thenReturn(connection);

        processor = new UserPassSftpProcessor(product, pool);
    }

    /**
     * Test that the retrieval of a connection works.
     */
    @Test
    public void testGetConnection() {
        assertThat(processor.getConnection()).isEqualTo(connection);
    }

}
