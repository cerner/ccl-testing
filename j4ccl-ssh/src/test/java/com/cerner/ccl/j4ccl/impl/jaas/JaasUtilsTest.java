package com.cerner.ccl.j4ccl.impl.jaas;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

import java.security.Principal;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;

import org.junit.Test;

/**
 * Unit tests for {@link JaasUtils}.
 *
 * @author Joshua Hyde
 *
 */

public class JaasUtilsTest {
    /**
     * Test the retrieval of the current subject.
     */
    @Test
    public void testGetCurrentSubject() {
        final Subject subject = new Subject();
        assertThat(Subject.doAs(subject, new PrivilegedAction<Subject>() {
            @Override
            public Subject run() {
                return JaasUtils.getCurrentSubject();
            }
        })).isEqualTo(subject);
    }

    /**
     * Retrieving the current subject should fail if no subject is in context.
     */
    @Test
    public void testGetCurrentSubjectNoSubject() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            JaasUtils.getCurrentSubject();
        });
        assertThat(e.getMessage()).isEqualTo("No subject found in current context.");
    }

    /**
     * Test the retrieval of a principal off of the current subject.
     */
    @Test
    public void testGetPrincipal() {
        final Principal principal = mock(Principal.class);
        final Subject subject = new Subject();
        subject.getPrincipals().add(principal);

        assertThat(Subject.doAs(subject, new PrivilegedAction<Principal>() {
            @Override
            public Principal run() {
                return JaasUtils.getPrincipal(principal.getClass());
            }
        })).isEqualTo(principal);
    }

    /**
     * If the subject has no principals of the request type, getting the principal should fail.
     */
    @Test
    public void testGetPrincipalNoPrincipals() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            Subject.doAs(new Subject(), new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    JaasUtils.getPrincipal(Principal.class);
                    return null;
                }
            });
        });
        assertThat(e.getMessage()).isEqualTo("Incorrect number of principals of type "
                + Principal.class.getCanonicalName() + " on subject: " + Integer.toString(0));
    }

    /**
     * Getting the principals for a {@code null} class should fail.
     */
    @Test
    public void testGetPrincipalNullPrincipalClass() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            JaasUtils.getPrincipal(null);
        });
        assertThat(e.getMessage()).isEqualTo("Principal class cannot be null.");
    }

    /**
     * Test the retrieval of a private credential off of the current subject.
     */
    @Test
    public void testGetPrivateCredential() {
        final Object credential = new Object();
        final Subject subject = new Subject();
        subject.getPrivateCredentials().add(credential);
        assertThat(Subject.doAs(subject, new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                return JaasUtils.getPrivateCredential(credential.getClass());
            }
        })).isEqualTo(credential);
    }

    /**
     * If the subject has no credentials of the request type, retrieval should fail.
     */
    @Test
    public void testGetPrivateCredentialNoCredential() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            Subject.doAs(new Subject(), new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    JaasUtils.getPrivateCredential(Integer.class);
                    return null;
                }
            });
        });
        assertThat(e.getMessage()).isEqualTo("Incorrect number of private credentials of type "
                + Integer.class.getCanonicalName() + " on subject : " + Integer.toString(0));
    }

    /**
     * Getting private credentials for a {@code null} class should fail.
     */
    @Test
    public void testGetPrivateCredentialNullClass() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            JaasUtils.getPrivateCredential(null);
        });
        assertThat(e.getMessage()).isEqualTo("Credential class cannot be null.");
    }

    /**
     * Test the determination of whether or not a subject has a principal on it.
     */
    @Test
    public void testHasPrincipal() {
        final Principal principal = mock(Principal.class);
        final Subject subject = new Subject();
        subject.getPrincipals().add(principal);
        assertThat(Subject.doAs(subject, new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return JaasUtils.hasPrincipal(principal.getClass());
            }
        })).isTrue();
        assertThat(Subject.doAs(subject, new PrivilegedAction<Boolean>() {
            @Override
            public Boolean run() {
                return JaasUtils.hasPrincipal(MillenniumDomainPrincipal.class);
            }
        })).isFalse();
    }

    /**
     * Determining the existence of a {@code null} principal should fail.
     */
    @Test
    public void testHasPrincipalNullClass() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            JaasUtils.hasPrincipal(null);
        });
        assertThat(e.getMessage()).isEqualTo("Principal class cannot be null.");
    }
}
