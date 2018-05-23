package com.cerner.ftp.sftp.jsch.processor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.cerner.ftp.Uploader;
import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.jsch.Connection;
import com.cerner.ftp.sftp.jsch.internal.StubbedJschSftpProcessor;
import com.jcraft.jsch.ChannelSftp;

/**
 * Automated unit test of {@link JschSftpProcessor}.
 *
 * @author Joshua Hyde
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class JschSftpProcessorTest {
    @Mock
    private ChannelSftp channel;
    @Mock
    private Connection conn;
    @Mock
    private InputStream serverStream;

    /**
     * Set up the mocked connection to return the mocked SFTP channel.
     *
     * @throws Exception
     *             If any errors occur during the setup.
     */
    @Before
    public void setUp() throws Exception {
        when(serverStream.read(ArgumentMatchers.<byte[]> any())).thenReturn(-1);

        when(conn.getSFtp()).thenReturn(channel);
    }

    /**
     * Test of {@link JschSftpProcessor#download()}.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testDownload() throws Exception {
        final File unitDirectory = new File("target/unit");
        FileUtils.forceMkdir(unitDirectory);

        final File sourceFile = new File(unitDirectory, "not.exists");
        final File targetFile = new File(unitDirectory, "does.exist");

        final FileRequest request = createMockFileRequest(sourceFile.toURI(), targetFile.toURI());

        when(channel.get(ArgumentMatchers.<String> any())).thenReturn(serverStream);
        final StubbedJschSftpProcessor processor = new StubbedJschSftpProcessor();
        processor.setConnection(conn);
        processor.queueDownload(request);
        processor.download();

        verify(channel, times(1)).get(sourceFile.toURI().getPath());
    }

    /**
     * Test of {@link JschSftpProcessor#upload()}.
     *
     * @throws Exception
     *             If any errors occur while running the test.
     */
    @Test
    public void testUpload() throws Exception {
        final File unitDirectory = new File("target/unit");
        FileUtils.forceMkdir(unitDirectory);

        final File sourceFile = new File(unitDirectory, "not.exists");
        final File targetFile = new File(unitDirectory, "does.exist");

        final FileRequest request = createMockFileRequest(sourceFile.toURI(), targetFile.toURI());

        final StubbedJschSftpProcessor processor = new StubbedJschSftpProcessor();
        processor.setConnection(conn);
        processor.queueUpload(request, false, Uploader.GOD_PERMISSIONS);
        processor.upload();

        verify(channel, times(1)).put(sourceFile.getAbsolutePath(), targetFile.toURI().getPath());
    }

    /**
     * Create a mock file request.
     *
     * @param sourceLocation
     *            The location of the source file.
     * @param targetLocation
     *            The location of the target file.
     * @return A mock {@link FileRequest} object.
     */
    private FileRequest createMockFileRequest(final URI sourceLocation, final URI targetLocation) {
        final FileRequest request = mock(FileRequest.class);
        when(request.getSourceFile()).thenReturn(sourceLocation);
        when(request.getTargetFile()).thenReturn(targetLocation);
        return request;
    }
}
