package com.cerner.ccl.j4ccl.impl.jaas;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * A private credential representing a user's Millennium password.
 *
 * @author Joshua Hyde
 *
 */

public class MillenniumDomainPasswordCredential implements Serializable {
    private static final long serialVersionUID = -4983610563867489531L;
    private String password;

    /**
     * Create a password credential.
     *
     * @param password
     *            The password.
     * @throws NullPointerException
     *             If the given password is {@code null}.
     */
    public MillenniumDomainPasswordCredential(final String password) {
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
     * Read in a password credential from its serialized form.
     *
     * @param in
     *            The {@link ObjectInputStream} from which to read in the serialized object.
     * @throws ClassNotFoundException
     *             If the class of any read-in object is not found on the classpath.
     * @throws IOException
     *             If any errors occur while writing out the object.
     */
    private void readObject(final ObjectInputStream in) throws ClassNotFoundException, IOException {
        password = (String) in.readObject();
    }

    /**
     * Write out this object to a serialized form.
     *
     * @param out
     *            The {@link ObjectOutputStream} to which the object is to be written.
     * @throws IOException
     *             If any errors occur during the write-out.
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeObject(password);
    }
}
