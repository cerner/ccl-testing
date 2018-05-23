package com.cerner.ftp.sftp.jsch;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import com.cerner.ftp.Uploader;
import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.sftp.jsch.internal.StubbedJschSftpProcessor;
import com.cerner.ftp.sftp.jsch.processor.JschSftpProcessor;
import com.cerner.ftp.util.PermissionsBuilder;

/**
 * Unit tests for {@link JschUploader}.
 *
 * @author Joshua Hyde
 *
 */

public class JschUploaderTest {
    /**
     * Test that uploading is executed.
     */
    @Test
    public void testUpload() {
        final JschSftpProcessor processor = mock(StubbedJschSftpProcessor.class);

        final FileRequest request = mock(FileRequest.class);
        new JschUploader(processor).upload(Arrays.asList(request));

        verify(processor, times(1)).queueUpload(request, false, Uploader.GOD_PERMISSIONS);
        verify(processor, times(1)).upload();
    }

    /**
     * Test that, in the event of an empty queue, the processor's upload method is not invoked.
     */
    @Test
    public void testUploadEmptyQueue() {
        final JschSftpProcessor processor = mock(StubbedJschSftpProcessor.class);

        final Collection<FileRequest> requests = Collections.<FileRequest> emptyList();
        new JschUploader(processor).upload(requests);

        verify(processor, never()).upload();
    }

    /**
     * Verify that permissions are passed through during the upload.
     */
    @Test
    public void testSetPermissionsPassthrough() {
        final JschSftpProcessor processor = mock(StubbedJschSftpProcessor.class);

        final int permissions = PermissionsBuilder.build(6, 0, 0);

        final FileRequest request = mock(FileRequest.class);
        final JschUploader uploader = new JschUploader(processor);
        uploader.setPermissions(permissions);
        uploader.upload(Arrays.asList(request));

        verify(processor, times(1)).queueUpload(request, false, permissions);
    }

    /**
     * Verify that ignoring chmod errors is passed through during the upload.
     */
    @Test
    public void testIgnoreChmodErrorsPassthrough() {
        final JschSftpProcessor processor = mock(StubbedJschSftpProcessor.class);

        final FileRequest request = mock(FileRequest.class);
        final JschUploader uploader = new JschUploader(processor);
        uploader.ignoreChmodErrors(true);
        uploader.upload(Arrays.asList(request));

        verify(processor, times(1)).queueUpload(request, true, Uploader.GOD_PERMISSIONS);
    }
}
