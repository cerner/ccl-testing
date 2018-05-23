package com.cerner.ccl.j4ccl.impl.jaas;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Principal;

import org.apache.commons.lang.StringUtils;

/**
 * A {@link Principal} used to store the public credentials used to access the backend node of a Cerner Millennium
 * domain.
 *
 * @author Joshua Hyde
 *
 */

public class BackendNodePrincipal implements Principal, Serializable {
    private static final long serialVersionUID = 4222329534520721714L;
    private String username;
    private String hostName;
    private String environmentName;

    /**
     * Create a backend node principal.
     *
     * @param username
     *            The username.
     * @param hostName
     *            The name of the node.
     * @param environmentName
     *            The name of the Millennium environment.
     * @throws IllegalArgumentException
     *             If any of the given strings are blank.
     * @throws NullPointerException
     *             If any of the given strings are {@code null}.
     */
    public BackendNodePrincipal(final String username, final String hostName, final String environmentName) {
        if (username == null)
            throw new NullPointerException("Username cannot be null.");

        if (StringUtils.isBlank(username))
            throw new IllegalArgumentException("Username cannot be blank.");

        if (hostName == null)
            throw new NullPointerException("Host name cannot be null.");

        if (StringUtils.isBlank(hostName))
            throw new IllegalArgumentException("Host name cannot be blank.");

        if (environmentName == null)
            throw new NullPointerException("Environment name cannot be null.");

        if (StringUtils.isBlank(environmentName))
            throw new IllegalArgumentException("Environment name cannot be blank.");

        this.username = username;
        this.hostName = hostName;
        this.environmentName = environmentName;
    }

    /**
     * Get the name of the environment.
     *
     * @return The name of the environment.
     */
    public String getEnvironmentName() {
        return environmentName;
    }

    /**
     * Get the host name.
     *
     * @return The name of the host node.
     */
    public String getHostname() {
        return hostName;
    }

    /**
     * {@inheritDoc}
     */
    public String getName() {
        return username + "@" + environmentName + "@" + hostName;
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
     * Read the object in from a serialized state.
     *
     * @param in
     *            The {@link ObjectInputStream} from which to read the serialized object.
     * @throws ClassNotFoundException
     *             If the class of the object does not exist on the classpath.
     * @throws IOException
     *             If any other errors occur while reading the serialized object.
     */
    private void readObject(final ObjectInputStream in) throws ClassNotFoundException, IOException {
        username = (String) in.readObject();
        hostName = (String) in.readObject();
        environmentName = (String) in.readObject();

        assert !StringUtils.isBlank(username);
        assert !StringUtils.isBlank(hostName);
        assert !StringUtils.isBlank(environmentName);
    }

    /**
     * Serialize this object.
     *
     * @param out
     *            The {@link ObjectOutputStream} to which the object is to be serialized.
     * @throws IOException
     *             If any errors occur during the write-out.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeObject(username);
        out.writeObject(hostName);
        out.writeObject(environmentName);
    }
}
