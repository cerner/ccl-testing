package com.cerner.ccl.j4ccl.impl.jaas;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.Principal;

import org.apache.commons.lang.StringUtils;

/**
 * A {@link Principal} used to store the file name for private key access to an HNAM node.
 *
 * @author Fred Eckertson
 *
 */

public class PrivateKeyPrincipal implements Principal, Serializable {
    private static final long serialVersionUID = -7701998407324069150L;
    private String fileName;

    /**
     * Create a private key principal.
     *
     * @param fileName
     *            The name of the private key file.
     * @throws NullPointerException
     *             If any of the given strings are {@code null}.
     */
    public PrivateKeyPrincipal(final String fileName) {
        if (fileName == null) {
            throw new NullPointerException("fileName cannot be null.");
        }
        this.fileName = fileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return fileName;
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
        fileName = (String) in.readObject();
        assert !StringUtils.isBlank(fileName);
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
        out.writeObject(fileName);
    }
}
