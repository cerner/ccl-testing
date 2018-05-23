package com.cerner.ftp.sftp.jsch;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.Test;

import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.sftp.jsch.internal.StubbedJschSftpProcessor;
import com.cerner.ftp.sftp.jsch.processor.JschSftpProcessor;

/**
 * Unit tests of {@link JschDownloader}.
 *
 * @author Joshua Hyde
 *
 */

public class JschDownloaderTest {
    /**
     * Test that the files are attempted to download.
     */
    @Test
    public void testDownload() {
        final JschSftpProcessor processor = mock(StubbedJschSftpProcessor.class);

        final FileRequest request = mock(FileRequest.class);
        new JschDownloader(processor).download(Arrays.asList(request));

        verify(processor, times(1)).queueDownload(request);
        verify(processor, times(1)).download();
    }

    /**
     * Test that, given an empty request, no downloads are initiated.
     */
    @Test
    public void testDownloadEmptyRequests() {
        final JschSftpProcessor processor = mock(StubbedJschSftpProcessor.class);

        final Collection<FileRequest> requests = Collections.<FileRequest> emptyList();
        new JschDownloader(processor).download(requests);

        verify(processor, never()).download();
    }
}
