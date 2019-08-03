package com.cerner.ftp.sftp.jsch.processor;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URI;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.cerner.ftp.Uploader;
import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.exception.TransferException;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

/**
 * Automated unit test of {@link UploadProcessor}.
 *
 * @author Joshua Hyde
 *
 */

public class UploadProcessorTest {
    /**
     * Test the capability to upload files.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testRun() throws Exception {
        final File targetFile = new File("target/unit/upload.test");

        final ChannelSftp mockChannel = mock(ChannelSftp.class);
        final FileRequest mockRequest = mock(FileRequest.class);
        when(mockRequest.getSourceFile()).thenReturn(new File("target/unit/dummy.src").toURI());
        when(mockRequest.getTargetFile()).thenReturn(targetFile.toURI());

        final UploadProcessor processor = new UploadProcessor(mockRequest, false, Uploader.GOD_PERMISSIONS);
        processor.run(mockChannel);

        verify(mockChannel, times(1)).put(new File(mockRequest.getSourceFile()).getAbsolutePath(),
                mockRequest.getTargetFile().getPath());
    }

    /**
     * Test that a {@link TransferException} is thrown when the underlying {@link ChannelSftp} object throws a
     * {@link SftpException}.
     */
    @Test(expected = TransferException.class)
    public void testPutFailure() {
        final Answer<Object> exceptionThrower = new Answer<Object>() {
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable {
                throw mock(SftpException.class);
            }
        };
        // Make a channel that throws exceptions regardless
        final ChannelSftp mockChannel = mock(ChannelSftp.class, exceptionThrower);

        final FileRequest mockRequest = mock(FileRequest.class);
        when(mockRequest.getSourceFile()).thenReturn(new File("target/unit/dummy.src").toURI());
        when(mockRequest.getTargetFile()).thenReturn(new File("target/unit/dummy.out").toURI());

        new UploadProcessor(mockRequest, false, Uploader.GOD_PERMISSIONS).run(mockChannel);
    }

    /**
     * Test that, if chmod'ing the file fails, the exception is swallowed.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSwallowsChmodException() throws Exception {
        final URI targetFile = new File("target/unit/dummy.out").toURI();
        final FileRequest mockRequest = mock(FileRequest.class);
        when(mockRequest.getSourceFile()).thenReturn(new File("target/unit/dummy.src").toURI());
        when(mockRequest.getTargetFile()).thenReturn(targetFile);

        final ChannelSftp channel = mock(ChannelSftp.class);
        doThrow(new SftpException(1, "I am an exception")).when(channel).chmod(Uploader.GOD_PERMISSIONS,
                targetFile.getPath());

        new UploadProcessor(mockRequest, true, Uploader.GOD_PERMISSIONS).run(channel);
    }

    /**
     * Verify that the processor passes the exception through if configured to do such.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test(expected = TransferException.class)
    public void testPassesChmodException() throws Exception {
        final URI targetFile = new File("target/unit/dummy.out").toURI();
        final FileRequest mockRequest = mock(FileRequest.class);
        when(mockRequest.getSourceFile()).thenReturn(new File("target/unit/dummy.src").toURI());
        when(mockRequest.getTargetFile()).thenReturn(targetFile);

        final ChannelSftp channel = mock(ChannelSftp.class);
        doThrow(new SftpException(1, "I am an exception")).when(channel).chmod(Uploader.GOD_PERMISSIONS,
                targetFile.getPath());

        new UploadProcessor(mockRequest, false, Uploader.GOD_PERMISSIONS).run(channel);
    }
}
