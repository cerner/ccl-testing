package com.cerner.ccl.j4ccl.impl.jaas;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

/**
 * Unit tests for {@link MillenniumDomainPrincipal}.
 *
 * @author Joshua Hyde
 *
 */

public class MillenniumDomainPrincipalTest {
    private final String username = "bob";
    private final String domainName = "der_domain";
    private final MillenniumDomainPrincipal principal = new MillenniumDomainPrincipal(username, domainName);

    /**
     * Construction with a blank domain name should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructBlankDomainName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new MillenniumDomainPrincipal(username, " ");
        });
        assertThat(e.getMessage()).isEqualTo("Domain name cannot be blank.");
    }

    /**
     * Construction with a blank username should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructBlankUsername() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new MillenniumDomainPrincipal(" ", domainName);
        });
        assertThat(e.getMessage()).isEqualTo("Username cannot be blank.");
    }

    /**
     * Construction with a {@code null} domain name should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructNullDomainName() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            new MillenniumDomainPrincipal(username, null);
        });
        assertThat(e.getMessage()).isEqualTo("Domain name cannot be null.");
    }

    /**
     * Construction with a {@code null} username should fail.
     */
    @Test
    @SuppressWarnings("unused")
    public void testConstructNullUsername() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            new MillenniumDomainPrincipal(null, domainName);
        });
        assertThat(e.getMessage()).isEqualTo("Username cannot be null.");
    }

    /**
     * Test the retrieval of the domain name.
     */
    @Test
    public void testGetDomainName() {
        assertThat(principal.getDomainName()).isEqualTo(domainName);
    }

    /**
     * Test the retrieval of the principal's name.
     */
    @Test
    public void testGetName() {
        assertThat(principal.getName()).isEqualTo(username + "@" + domainName);
    }

    /**
     * Test the retrieval of the username.
     */
    @Test
    public void testGetUsername() {
        assertThat(principal.getUsername()).isEqualTo(username);
    }

    /**
     * Validate the writeObject method
     *
     * @throws Exception
     *             Sometimes bad things happen
     *
     */
    @Test
    public void testWriteObject() throws Exception {
        final MillenniumDomainPrincipal principal = new MillenniumDomainPrincipal(username, domainName);
        final ObjectOutputStream out = new ObjectOutputStream(
                new FileOutputStream("target/test-classes/testWriteObject.out"));
        out.writeObject(principal);
        out.close();
        final ObjectInputStream in = new ObjectInputStream(
                new FileInputStream("target/test-classes/testWriteObject.out"));
        final MillenniumDomainPrincipal p = (MillenniumDomainPrincipal) in.readObject();
        in.close();
        assertThat(p.getUsername()).isEqualTo(principal.getUsername());
        assertThat(p.getDomainName()).isEqualTo(principal.getDomainName());
    }
}
