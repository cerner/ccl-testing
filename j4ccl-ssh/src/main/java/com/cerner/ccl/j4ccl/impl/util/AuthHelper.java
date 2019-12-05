package com.cerner.ccl.j4ccl.impl.util;

import java.net.URI;

import javax.security.auth.login.Configuration;

import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePasswordCredential;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePrincipal;
import com.cerner.ccl.j4ccl.impl.jaas.JaasUtils;
import com.cerner.ccl.j4ccl.impl.jaas.PrivateKeyPrincipal;
import com.cerner.ftp.data.FtpProduct;
import com.cerner.ftp.data.sftp.KeyCryptoBuilder;
import com.cerner.ftp.data.sftp.UserPassBuilder;

/**
 * A utility class to help unify {@link Configuration} objects with the various authentication objects used by this
 * project.
 *
 * @author Joshua Hyde
 *
 */

public final class AuthHelper {
    /**
     * Private constructor to prevent instantiation.
     */
    private AuthHelper() {
    }

    /**
     * Build an FTP product from the current subject.
     *
     * @return An {@link FtpProduct} object constructed out of the current subject's principals and credentials.
     */
    public static FtpProduct fromCurrentSubject() {
        final BackendNodePrincipal principal = JaasUtils.getPrincipal(BackendNodePrincipal.class);
        final BackendNodePasswordCredential credential = JaasUtils
                .getPrivateCredential(BackendNodePasswordCredential.class);
        if (JaasUtils.hasPrincipal(PrivateKeyPrincipal.class)) {
            PrivateKeyPrincipal keyPrincipal = JaasUtils.getPrincipal(PrivateKeyPrincipal.class);
            return KeyCryptoBuilder.getBuilder().setKeySalt(credential.getPassword())
                    .setPrivateKey(URI.create(keyPrincipal.getName()))
                    .setServerAddress(URI.create(principal.getHostname())).setUsername(principal.getUsername()).build();
        }
        return UserPassBuilder.getBuilder().setServerAddress(URI.create(principal.getHostname()))
                .setUsername(principal.getUsername()).setPassword(credential.getPassword()).build();
    }
}
