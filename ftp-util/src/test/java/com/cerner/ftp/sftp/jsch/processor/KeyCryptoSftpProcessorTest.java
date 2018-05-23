package com.cerner.ftp.sftp.jsch.processor;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.cerner.ftp.data.sftp.KeyCryptoBuilder.KeyCryptoProduct;
import com.cerner.ftp.jsch.Connection;
import com.cerner.ftp.sftp.jsch.processor.internal.StubbedConnectionPool;

/**
 * Unit tests for {@link KeyCryptoSftpProcessor}.
 *
 * @author Joshua Hyde
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class KeyCryptoSftpProcessorTest {
    private static final String USERNAME = "user.name";
    private static final String KEY_SALT = "key.salt";
    private static final URI PRIVATE_KEY_URI = URI.create("i.am.a.private.key");
    private static final URI SERVER_ADDRESS = URI.create("http://www.google.com");

    @Mock
    private StubbedConnectionPool pool;
    @Mock
    private Connection connection;
    @Mock
    private KeyCryptoProduct product;
    private KeyCryptoSftpProcessor processor;

    /**
     * Set up the product for each test.
     */
    @Before
    public void setUp() {
        when(product.getUsername()).thenReturn(USERNAME);
        when(product.getKeySalt()).thenReturn(KEY_SALT);
        when(product.getPrivateKey()).thenReturn(PRIVATE_KEY_URI);
        when(product.getServerAddress()).thenReturn(SERVER_ADDRESS);

        when(pool.getConnection(USERNAME, KEY_SALT, PRIVATE_KEY_URI, SERVER_ADDRESS)).thenReturn(connection);

        processor = new KeyCryptoSftpProcessor(product, pool);
    }

    /**
     * Verify that the connection pool is correctly invoked.
     */
    @Test
    public void testGetConnection() {
        assertThat(processor.getConnection()).isEqualTo(connection);
    }

}
