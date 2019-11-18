package com.cerner.ccl.j4ccl.impl.jaas;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;

/**
 * Unit tests for {@link BackendNodePasswordCredential}.
 *
 * @author Joshua Hyde
 *
 */

public class BackendNodePasswordCredentialTest {
    private final String password = "pass.word";
    private final BackendNodePasswordCredential credential = new BackendNodePasswordCredential(password);

    /**
     * Construction with a {@code null} password should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructNullPassword() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            new BackendNodePasswordCredential(null);
        });
        assertThat(e.getMessage()).isEqualTo("Password cannot be null.");
    }

    /**
     * Test the retrieval of the password.
     */
    @Test
    public void testGetPassword() {
        assertThat(credential.getPassword()).isEqualTo(password);
    }

    /**
     * Test the serialization of the credential.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSerialize() throws Exception {
        assertThat(((BackendNodePasswordCredential) SerializationUtils
                .deserialize(SerializationUtils.serialize(credential))).getPassword()).isEqualTo(password);
    }
}
