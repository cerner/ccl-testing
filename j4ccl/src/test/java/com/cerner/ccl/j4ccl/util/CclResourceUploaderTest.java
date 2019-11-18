package com.cerner.ccl.j4ccl.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Enumeration;

import org.apache.commons.discovery.tools.Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit tests for {@link CclResourceUploader}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = Service.class)
public class CclResourceUploaderTest {
    /**
     * Test the retrieval of an uploader.
     */
    @Test
    public void testGetUploader() {
        final CclResourceUploader uploader = mock(CclResourceUploader.class);

        @SuppressWarnings("unchecked")
        final Enumeration<CclResourceUploader> uploaders = mock(Enumeration.class);
        when(uploaders.hasMoreElements()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        when(uploaders.nextElement()).thenReturn(uploader);

        mockStatic(Service.class);
        when(Service.providers(CclResourceUploader.class)).thenReturn(uploaders);

        assertThat(CclResourceUploader.getUploader()).isEqualTo(uploader);
    }

    /**
     * Retrieving an uploader when there are no implementations on the classpath should fail.
     */
    @Test
    public void testGetUploaderNoImplementation() {
        @SuppressWarnings("unchecked")
        final Enumeration<CclResourceUploader> uploaders = mock(Enumeration.class);

        mockStatic(Service.class);
        when(Service.providers(CclResourceUploader.class)).thenReturn(uploaders);

        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            CclResourceUploader.getUploader();
        });
        assertThat(e.getMessage()).isEqualTo("No implementations found of: " + CclResourceUploader.class.getName());
    }
}
