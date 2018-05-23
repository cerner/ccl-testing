package com.cerner.ccl.j4ccl.impl.jaas;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Principal;

import org.apache.commons.lang.StringUtils;

/**
 * A principal used to represent a user's public Millennium identifier.
 *
 * @author Joshua Hyde
 *
 */

public class MillenniumDomainPrincipal implements Principal, Serializable {
    private static final long serialVersionUID = -5293863202716058874L;
    private String username;
    private String domainName;

    /**
     * Create a principal.
     *
     * @param username
     *            The username of the Millennium user.
     * @param domainName
     *            The name of the Millennium domain to which the user belongs.
     * @throws IllegalArgumentException
     *             If either of the given objects are blank.
     * @throws NullPointerException
     *             If either of the given objects are {@code null}.
     */
    public MillenniumDomainPrincipal(final String username, final String domainName) {
        if (username == null)
            throw new NullPointerException("Username cannot be null.");

        if (StringUtils.isBlank(username))
            throw new IllegalArgumentException("Username cannot be blank.");

        if (domainName == null)
            throw new NullPointerException("Domain name cannot be null.");

        if (StringUtils.isBlank(domainName))
            throw new IllegalArgumentException("Domain name cannot be blank.");

        this.username = username;
        this.domainName = domainName;
    }

    /**
     * Get the name of the Millennium domain.
     *
     * @return The name of the Millennium domain.
     */
    public String getDomainName() {
        return domainName;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return username + "@" + domainName;
    }

    /**
     * Get the username.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Read in the object from a serialized form.
     *
     * @param in
     *            The {@link ObjectInputStream} from which the object is to read in its serialized form.
     * @throws ClassNotFoundException
     *             If the class of any read-in object does not exist on the classpath.
     * @throws IOException
     *             If any errors occur during the read in.
     */
    private void readObject(final ObjectInputStream in) throws ClassNotFoundException, IOException {
        username = (String) in.readObject();
        domainName = (String) in.readObject();
    }

    /**
     * Write out the object to a serialized form.
     *
     * @param out
     *            The {@link ObjectOutputStream} to which the object is to be written.
     * @throws IOException
     *             If any errors occur during the write-out.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeObject(username);
        out.writeObject(domainName);
    }
}
