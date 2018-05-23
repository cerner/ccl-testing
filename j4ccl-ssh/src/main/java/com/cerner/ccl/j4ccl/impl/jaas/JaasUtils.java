package com.cerner.ccl.j4ccl.impl.jaas;

import java.security.AccessController;
import java.security.Principal;
import java.util.Set;

import javax.security.auth.Subject;

/**
 * Utilities to make it easier to retrieve information about the current subject.
 *
 * @author Joshua Hyde
 *
 */

public class JaasUtils {
    /**
     * Get the current subject.
     *
     * @return The current, in-context {@link Subject}.
     * @throws IllegalStateException
     *             If there is no subject within the current context.
     */
    public static Subject getCurrentSubject() {
        final Subject subject = Subject.getSubject(AccessController.getContext());
        if (subject == null)
            throw new IllegalStateException("No subject found in current context.");

        return subject;
    }

    /**
     * Get a principal off of the current subject.
     *
     * @param <P>
     *            The type of the principal.
     *
     * @param principalClass
     *            The {@link Class} of the {@link Principal} to be retrieved.
     * @return The request principal off of the current subject.
     * @throws IllegalArgumentException
     *             If the given class is {@code null} or no principals of the requested type are found on the current
     *             subject.
     * @see #getCurrentSubject()
     */
    public static <P extends Principal> P getPrincipal(final Class<P> principalClass) {
        if (principalClass == null)
            throw new IllegalArgumentException("Principal class cannot be null.");

        final Set<P> principals = getCurrentSubject().getPrincipals(principalClass);
        if (principals.size() != 1)
            throw new IllegalArgumentException("Incorrect number of principals of type "
                    + principalClass.getCanonicalName() + " on subject: " + Integer.toString(principals.size()));

        return principals.iterator().next();
    }

    /**
     * Get a private credential off of the current subject.
     * 
     * @param <T>
     *            The type of the credential to get.
     *
     * @param credentialClass
     *            The {@link Class} of the credential to be retrieved.
     * @return The request credential off of the current subject.
     * @throws IllegalArgumentException
     *             If the given credential class is {@code null} or the current subject has no credentials of the given
     *             class.
     * @see #getCurrentSubject()
     */
    public static <T> T getPrivateCredential(final Class<T> credentialClass) {
        if (credentialClass == null)
            throw new IllegalArgumentException("Credential class cannot be null.");

        final Set<T> credentials = getCurrentSubject().getPrivateCredentials(credentialClass);
        if (credentials.size() != 1)
            throw new IllegalArgumentException("Incorrect number of private credentials of type "
                    + credentialClass.getCanonicalName() + " on subject : " + Integer.toString(credentials.size()));

        return credentials.iterator().next();
    }

    /**
     * Determine whether or not the given principal is one the current subject.
     *
     * @param principalClass
     *            The {@link Class} of the principal whose presence is to be determined.
     * @return {@code true} if the current subject has the request principal attached to it; {@code false} if not.
     */
    public static boolean hasPrincipal(final Class<? extends Principal> principalClass) {
        if (principalClass == null)
            throw new IllegalArgumentException("Principal class cannot be null.");

        return !getCurrentSubject().getPrincipals(principalClass).isEmpty();
    }
}
