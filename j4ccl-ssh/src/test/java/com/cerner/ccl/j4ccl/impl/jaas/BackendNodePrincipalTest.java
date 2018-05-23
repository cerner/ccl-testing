package com.cerner.ccl.j4ccl.impl.jaas;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;

import com.cerner.ccl.j4ccl.internal.AbstractUnitTest;

/**
 * Unit tests for {@link BackendNodePrincipal}.
 *
 * @author Joshua Hyde
 *
 */

public class BackendNodePrincipalTest extends AbstractUnitTest {
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
        expect(IllegalArgumentException.class);
        expect("Environment name cannot be blank.");
        new BackendNodePrincipal(username, hostName, " ");
    }

    /**
     * Construction with a blank host name should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructBlankHostName() {
        expect(IllegalArgumentException.class);
        expect("Host name cannot be blank.");
        new BackendNodePrincipal(username, " ", environmentName);
    }

    /**
     * Construction with a blank username should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructBlankUsername() {
        expect(IllegalArgumentException.class);
        expect("Username cannot be blank.");
        new BackendNodePrincipal(" ", hostName, environmentName);
    }

    /**
     * Construction with a {@code null} environment name should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructNullEnvironmentName() {
        expect(NullPointerException.class);
        expect("Environment name cannot be null.");
        new BackendNodePrincipal(username, hostName, null);
    }

    /**
     * Construction with a {@code null} host name should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructNullHostName() {
        expect(NullPointerException.class);
        expect("Host name cannot be null.");
        new BackendNodePrincipal(username, null, environmentName);
    }

    /**
     * Construction with a {@code null} username should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructNullUsername() {
        expect(NullPointerException.class);
        expect("Username cannot be null.");
        new BackendNodePrincipal(null, hostName, environmentName);
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
