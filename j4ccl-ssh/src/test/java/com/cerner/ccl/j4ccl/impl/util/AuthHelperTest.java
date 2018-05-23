package com.cerner.ccl.j4ccl.impl.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.net.URI;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePasswordCredential;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePrincipal;
import com.cerner.ccl.j4ccl.impl.jaas.JaasUtils;
import com.cerner.ftp.data.sftp.UserPassBuilder;
import com.cerner.ftp.data.sftp.UserPassBuilder.UserPassProduct;

/**
 * Unit tests for {@link AuthHelper}.
 *
 * @author Joshua Hyde
 *
 */

@PowerMockIgnore("javax.security.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { AuthHelper.class, JaasUtils.class, UserPassBuilder.class })
public class AuthHelperTest {
    /**
     * Test the creation of an FTP product from the current subject.
     */
    @Test
    public void testFromCurrentSubject() {
        final String username = "username";
        final String password = "********";
        final String hostName = "host";

        final BackendNodePrincipal principal = mock(BackendNodePrincipal.class);
        when(principal.getUsername()).thenReturn(username);
        when(principal.getHostname()).thenReturn(hostName);

        final BackendNodePasswordCredential credential = mock(BackendNodePasswordCredential.class);
        when(credential.getPassword()).thenReturn(password);

        mockStatic(JaasUtils.class);
        when(JaasUtils.getPrincipal(BackendNodePrincipal.class)).thenReturn(principal);
        when(JaasUtils.getPrivateCredential(BackendNodePasswordCredential.class)).thenReturn(credential);

        final UserPassBuilder builder = mock(UserPassBuilder.class);
        when(builder.setServerAddress(URI.create(hostName))).thenReturn(builder);
        when(builder.setPassword(password)).thenReturn(builder);
        when(builder.setUsername(username)).thenReturn(builder);

        mockStatic(UserPassBuilder.class);
        when(UserPassBuilder.getBuilder()).thenReturn(builder);

        final UserPassProduct product = mock(UserPassProduct.class);
        when(builder.build()).thenReturn(product);

        assertThat(AuthHelper.fromCurrentSubject()).isEqualTo(product);

        verify(builder).setPassword(password);
        verify(builder).setServerAddress(URI.create(hostName));
        verify(builder).setUsername(username);
    }
}
