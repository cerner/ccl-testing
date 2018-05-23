package com.cerner.ccl.j4ccl.impl.jaas;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A credential used to store a user's password to a Millennium backend node.
 *
 * @author Joshua Hyde
 *
 */

public class BackendNodePasswordCredential implements Serializable {
    private static final long serialVersionUID = 7138639888173261366L;
    private String password;

    /**
     * Create a backend node password credential.
     *
     * @param password
     *            The password.
     * @throws NullPointerException
     *             If the given password is {@code null}.
     */
    public BackendNodePasswordCredential(final String password) {
        if (password == null)
            throw new NullPointerException("Password cannot be null.");

        this.password = password;
    }

    /**
     * Get the password.
     *
     * @return The password.
     */
    public String getPassword() {
        return password;
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
        password = (String) in.readObject();
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
        out.writeObject(password);
    }
}
