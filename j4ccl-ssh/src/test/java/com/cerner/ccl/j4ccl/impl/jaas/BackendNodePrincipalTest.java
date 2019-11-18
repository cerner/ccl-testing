package com.cerner.ccl.j4ccl.impl.jaas;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;

/**
 * Unit tests for {@link BackendNodePrincipal}.
 *
 * @author Joshua Hyde
 *
 */

public class BackendNodePrincipalTest {
    private final String username = "a.user";
    private final String hostName = "a.host";
    private final String environmentName = "an.environment";
    private final BackendNodePrincipal principal = new BackendNodePrincipal(username, hostName, environmentName);

    /**
     * Construction with a blank environment name should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructBlankEnvironmentName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new BackendNodePrincipal(username, hostName, " ");
        });
        assertThat(e.getMessage()).isEqualTo("Environment name cannot be blank.");
    }

    /**
     * Construction with a blank host name should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructBlankHostName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new BackendNodePrincipal(username, " ", environmentName);
        });
        assertThat(e.getMessage()).isEqualTo("Host name cannot be blank.");
    }

    /**
     * Construction with a blank username should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructBlankUsername() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new BackendNodePrincipal(" ", hostName, environmentName);
        });
        assertThat(e.getMessage()).isEqualTo("Username cannot be blank.");
    }

    /**
     * Construction with a {@code null} environment name should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructNullEnvironmentName() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            new BackendNodePrincipal(username, hostName, null);
        });
        assertThat(e.getMessage()).isEqualTo("Environment name cannot be null.");
    }

    /**
     * Construction with a {@code null} host name should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructNullHostName() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            new BackendNodePrincipal(username, null, environmentName);
        });
        assertThat(e.getMessage()).isEqualTo("Host name cannot be null.");
    }

    /**
     * Construction with a {@code null} username should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructNullUsername() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            new BackendNodePrincipal(null, hostName, environmentName);
        });
        assertThat(e.getMessage()).isEqualTo("Username cannot be null.");
    }

    /**
     * Test the retrieval of the environment name.
     */
    @Test
    public void testGetEnvironmentName() {
        assertThat(principal.getEnvironmentName()).isEqualTo(environmentName);
    }

    /**
     * Test the retrieval of the host name.
     */
    @Test
    public void testGetHostname() {
        assertThat(principal.getHostname()).isEqualTo(hostName);
    }

    /**
     * Test the retrieval of the name.
     */
    @Test
    public void testGetName() {
        assertThat(principal.getName()).isEqualTo(username + "@" + environmentName + "@" + hostName);
    }

    /**
     * Test the retrieval of the username.
     */
    @Test
    public void testGetUsername() {
        assertThat(principal.getUsername()).isEqualTo(username);
    }

    /**
     * Test the serialization of the principal.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSerialization() throws Exception {
        final BackendNodePrincipal restored = (BackendNodePrincipal) SerializationUtils
                .deserialize(SerializationUtils.serialize(principal));
        assertThat(restored.getUsername()).isEqualTo(username);
        assertThat(restored.getEnvironmentName()).isEqualTo(environmentName);
        assertThat(restored.getHostname()).isEqualTo(hostName);
    }
}
