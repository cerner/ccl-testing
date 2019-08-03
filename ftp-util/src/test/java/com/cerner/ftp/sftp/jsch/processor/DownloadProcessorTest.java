package com.cerner.ftp.sftp.jsch.processor;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.exception.TransferException;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.SftpException;

/**
 * Unit tests of {@link DownloadProcessor}.
 *
 * @author Joshua Hyde
 *
 */

public class DownloadProcessorTest {
    /**
     * Test that the processor "downloads" a file.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testRun() throws Exception {
        final File unitDirectory = new File("target/unit");
        FileUtils.forceMkdir(unitDirectory);

        final File targetFile = new File(unitDirectory, "download.test");
        if (targetFile.exists()) {
			assertThat(targetFile.delete()).isTrue();
		}

        final FileRequest mockRequest = mock(FileRequest.class);
        when(mockRequest.getSourceFile()).thenReturn(URI.create("target/unit/dummy.src"));
        when(mockRequest.getTargetFile()).thenReturn(targetFile.toURI());

        final ChannelSftp mockChannel = mock(ChannelSftp.class);
        when(mockChannel.get(mockRequest.getSourceFile().getPath()))
                .thenReturn(DownloadProcessorTest.class.getResourceAsStream("test.txt"));

        final DownloadProcessor processor = new DownloadProcessor(mockRequest);
        processor.run(mockChannel);

        assertThat(targetFile.exists()).isTrue();
        assertThat(FileUtils.readLines(targetFile, "UTF-8")).containsOnly("This is a test.");
    }

    /**
     * Test that a {@link TransferException} is thrown to catch a {@link SftpException}.
     */
    @Test(expected = TransferException.class)
    public void testGetFailure() {
        final Answer<Object> exceptionThrower = new Answer<Object>() {
			@Override
			public Object answer(final InvocationOnMock invocation) throws Throwable {
                throw mock(SftpException.class);
            }
        };
        // Make a channel that throws exceptions regardless
        final ChannelSftp mockChannel = mock(ChannelSftp.class, exceptionThrower);

        final FileRequest mockRequest = mock(FileRequest.class);
        when(mockRequest.getSourceFile()).thenReturn(URI.create("target/unit/dummy.src"));
        when(mockRequest.getTargetFile()).thenReturn(new File("target/unit/dummy.out").toURI());

        new DownloadProcessor(mockRequest).run(mockChannel);
    }
}
