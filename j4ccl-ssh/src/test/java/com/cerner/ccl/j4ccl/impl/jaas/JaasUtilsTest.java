package com.cerner.ccl.j4ccl.impl.jaas;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.security.Principal;
import java.security.PrivilegedAction;

import javax.security.auth.Subject;

import org.junit.Test;

import com.cerner.ccl.j4ccl.internal.AbstractUnitTest;

/**
 * Unit tests for {@link JaasUtils}.
 *
 * @author Joshua Hyde
 *
 */

public class JaasUtilsTest extends AbstractUnitTest {
    /**
     * Test the retrieval of the current subject.
     */
    @Test
    public void testGetCurrentSubject() {
        final Subject subject = new Subject();
        assertThat(Subject.doAs(subject, new PrivilegedAction<Subject>() {
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
        expect(IllegalStateException.class);
        expect("No subject found in current context.");
        JaasUtils.getCurrentSubject();
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
        expect(IllegalArgumentException.class);
        expect("Incorrect number of principals of type " + Principal.class.getCanonicalName() + " on subject: "
                + Integer.toString(0));
        Subject.doAs(new Subject(), new PrivilegedAction<Void>() {
            public Void run() {
                JaasUtils.getPrincipal(Principal.class);
                return null;
            }
        });
    }

    /**
     * Getting the principals for a {@code null} class should fail.
     */
    @Test
    public void testGetPrincipalNullPrincipalClass() {
        expect(IllegalArgumentException.class);
        expect("Principal class cannot be null.");
        JaasUtils.getPrincipal(null);
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
        expect(IllegalArgumentException.class);
        expect("Incorrect number of private credentials of type " + Integer.class.getCanonicalName() + " on subject : "
                + Integer.toString(0));
        Subject.doAs(new Subject(), new PrivilegedAction<Void>() {
            public Void run() {
                JaasUtils.getPrivateCredential(Integer.class);
                return null;
            }
        });
    }

    /**
     * Getting private credentials for a {@code null} class should fail.
     */
    @Test
    public void testGetPrivateCredentialNullClass() {
        expect(IllegalArgumentException.class);
        expect("Credential class cannot be null.");
        JaasUtils.getPrivateCredential(null);
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
            public Boolean run() {
                return JaasUtils.hasPrincipal(principal.getClass());
            }
        })).isTrue();
        assertThat(Subject.doAs(subject, new PrivilegedAction<Boolean>() {
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
        expect(IllegalArgumentException.class);
        expect("Principal class cannot be null.");
        JaasUtils.hasPrincipal(null);
    }
}
