package com.cerner.ccl.j4ccl.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.impl.util.AuthHelper;
import com.cerner.ccl.j4ccl.impl.util.FileAssistant;
import com.cerner.ftp.Uploader;
import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.data.FtpProduct;
import com.cerner.ftp.data.factory.FileRequestFactory;
import com.cerner.ftp.sftp.SftpUploader;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * Unit tests for {@link CclResourceUploaderImpl}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { AuthHelper.class, CclResourceUploaderImpl.class, FileAssistant.class,
        FileRequestFactory.class, SftpUploader.class, com.google.code.jetm.reporting.ext.PointFactory.class })
public class CclResourceUploaderImplTest {
    @Mock
    private FtpProduct product;
    @Mock
    private Uploader uploader;
    @Mock
    private EtmPoint jetmMock;

    private final CclResourceUploaderImpl cclUploader = new CclResourceUploaderImpl();

    /**
     * Set up the CCL uploader for each test.
     */
    @Before
    public void setUp() {
        mockStatic(AuthHelper.class);
        when(AuthHelper.fromCurrentSubject()).thenReturn(product);

        mockStatic(SftpUploader.class);
        when(SftpUploader.createUploader(product)).thenReturn(uploader);

        mockStatic(PointFactory.class);
        when(PointFactory.getPoint(CclResourceUploaderImpl.class, "upload")).thenReturn(jetmMock);
    }

    /**
     * Test the uploading of a file.
     */
    @Test
    public void testUpload() {
        final URI fileUri = URI.create("some.uri");
        final File file = mock(File.class);
        when(file.toURI()).thenReturn(fileUri);

        final URI remoteUri = URI.create("remoteUri");
        mockStatic(FileAssistant.class);
        when(FileAssistant.createRemotePath(file)).thenReturn(remoteUri);

        final FileRequest fileRequest = mock(FileRequest.class);
        mockStatic(FileRequestFactory.class);
        when(FileRequestFactory.create(fileUri, remoteUri)).thenReturn(fileRequest);

        cclUploader.queueUpload(file);
        final Map<File, URI> uploadMap = cclUploader.upload();
        assertThat(uploadMap).isNotNull();
        assertThat(uploadMap).includes(entry(file, remoteUri));

        verify(uploader).ignoreChmodErrors(true);
        verify(uploader).upload(Collections.singleton(fileRequest));
        verify(jetmMock).collect();
    }

    /**
     * If nothing is queued up for upload, then nothing should be uploaded.
     */
    @Test
    public void testUploadNothingQueued() {
        final Map<File, URI> uploadMap = cclUploader.upload();
        assertThat(uploadMap).isNotNull();
        assertThat(uploadMap).isEmpty();
        verifyZeroInteractions(uploader);
        verify(jetmMock).collect();
    }
}
