package com.cerner.ccl.j4ccl.impl.jaas;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;

/**
 * Unit tests for {@link MillenniumDomainPasswordCredential}.
 *
 * @author Joshua Hyde
 *
 */

public class MillenniumDomainPasswordCredentialTest {
    private final String password = "1337hax0r";
    private final MillenniumDomainPasswordCredential credential = new MillenniumDomainPasswordCredential(password);

    /**
     * Construction with a {@code null} password should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructNullPassword() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            new MillenniumDomainPasswordCredential(null);
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
    public void testSerialization() throws Exception {
        assertThat(((MillenniumDomainPasswordCredential) SerializationUtils
                .deserialize(SerializationUtils.serialize(credential))).getPassword()).isEqualTo(password);
    }
}
