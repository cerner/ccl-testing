package com.cerner.ftp.data.sftp.impl;

import static org.fest.assertions.Assertions.assertThat;

import java.net.URI;

import org.junit.Before;
import org.junit.Test;

import com.cerner.ftp.data.sftp.UserPassBuilder;
import com.cerner.ftp.data.sftp.UserPassBuilder.UserPassProduct;

/**
 * Automated unit test of {@link SimpleUserPassBuilder}.
 *
 * @author Joshua Hyde
 *
 */

public class SimpleUserPassBuilderTest {
    private UserPassBuilder builder;

    /**
     * Create a new instance of the {@link SimpleUserPassBuilder}.
     */
    @Before
    public void setUp() {
        builder = new SimpleUserPassBuilder();
    }

    /**
     * Test that the construction of a product works.
     */
    @Test
    public void testBuild() {
        final String username = "Joshua Hyde";
        final URI serverAddress = URI.create("http://www.google.com");
        final String password = "password";

        final UserPassProduct product = builder.setPassword(password).setServerAddress(serverAddress)
                .setUsername(username).build();

        assertThat(product).isNotNull();
        assertThat(product.getUsername()).isEqualTo(username);
        assertThat(product.getServerAddress().toString()).isEqualTo(serverAddress.toString());
        assertThat(product.getPassword()).isEqualTo(password);
    }

    /**
     * Test that building fails with an incomplete builder.
     */
    @Test(expected = IllegalStateException.class)
    public void testBuildIncomplete() {
        builder.build();
    }

    /**
     * Test that setting a null username fails.
     */
    @Test(expected = NullPointerException.class)
    public void testSetNullUsername() {
        builder.setUsername(null);
    }

    /**
     * Test that setting a null password fails.
     */
    @Test(expected = NullPointerException.class)
    public void testSetNullPassword() {
        builder.setPassword(null);
    }

    /**
     * Test that setting a null server address fails.
     */
    @Test(expected = NullPointerException.class)
    public void testSetNullServerAddress() {
        builder.setServerAddress(null);
    }
}
