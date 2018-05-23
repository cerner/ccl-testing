package com.cerner.ccl.j4ccl.impl.commands;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.TerminalProperties;
import com.cerner.ccl.j4ccl.impl.commands.util.CompileErrorValidator;
import com.cerner.ccl.j4ccl.impl.data.Environment;
import com.cerner.ccl.j4ccl.impl.util.AuthHelper;
import com.cerner.ccl.j4ccl.internal.AbstractUnitTest;
import com.cerner.ccl.j4ccl.ssh.CclCommandTerminal;
import com.cerner.ccl.j4ccl.ssh.JSchSshTerminal;
import com.cerner.ccl.j4ccl.util.CclResourceUploader;
import com.cerner.ftp.Downloader;
import com.cerner.ftp.data.FileRequest;
import com.cerner.ftp.data.FtpProduct;
import com.cerner.ftp.data.factory.FileRequestFactory;
import com.cerner.ftp.sftp.SftpDownloader;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * Unit tests for {@link ScriptCompilerCommand}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { AuthHelper.class, CclResourceUploader.class, CompileErrorValidator.class, Downloader.class,
        Environment.class, FileRequestFactory.class, JSchSshTerminal.class, ScriptCompilerCommand.class,
        SftpDownloader.class, PointFactory.class })
public class ScriptCompilerCommandTest extends AbstractUnitTest {
    @Mock
    private File sourceCodeLocation;
    @Mock
    private File dependency;
    @Mock
    private File localListingFile;
    private Collection<File> dependencies;
    private ScriptCompilerCommand command;

    /**
     * Set up the command for each test.
     */
    @Before
    public void setUp() {
        dependencies = Collections.singleton(dependency);
        command = new ScriptCompilerCommand(sourceCodeLocation, dependencies, localListingFile, false);
    }

    /**
     * Construction with {@code null} dependencies should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullDependencies() {
        expect(IllegalArgumentException.class);
        expect("Dependencies cannot be null.");
        new ScriptCompilerCommand(sourceCodeLocation, null, localListingFile, false);
    }

    /**
     * Construction with a {@code null} source code location should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullSourceCodeLocation() {
        expect(IllegalArgumentException.class);
        expect("Source code location cannot be null.");
        new ScriptCompilerCommand(null, dependencies, localListingFile, false);
    }

    /**
     * Test the execution of the command.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testRun() throws Exception {
        final EtmPoint point = mock(EtmPoint.class);
        mockStatic(PointFactory.class);
        when(PointFactory.getPoint(ScriptCompilerCommand.class, "run")).thenReturn(point);

        final String scriptName = "a_script";
        when(sourceCodeLocation.getName()).thenReturn(scriptName + ".prg");

        final URI listingLocationUri = URI.create("booo");
        when(localListingFile.toURI()).thenReturn(listingLocationUri);

        final URI sourceCodeTargetLocation = URI.create("sourceTarget");
        final CclResourceUploader uploader = prepareResourceUploader();
        when(uploader.upload()).thenReturn(Collections.singletonMap(sourceCodeLocation, sourceCodeTargetLocation));

        final String cerTemp = "cer_temp";
        prepareEnvironment(cerTemp);

        final JSchSshTerminal sshTerminal = prepareTerminal();

        final Downloader downloader = prepareDownloder();

        final FileRequest request = mock(FileRequest.class);
        final ArgumentCaptor<URI> listingOriginCaptor = prepareUploadRequest(request, listingLocationUri);

        final CompileErrorValidator validator = prepareValidator();

        final CclCommandTerminal cclTerminal = mock(CclCommandTerminal.class);
        final TerminalProperties basicTerminalProperties = TerminalProperties.getNewBuilder()
                .setOsPromptPattern("osPromptPattern").build();
        when(cclTerminal.getTerminalProperties()).thenReturn(basicTerminalProperties);
        command.run(cclTerminal);

        /*
         * Verify the uploading of files
         */
        verify(uploader).queueUpload(sourceCodeLocation);
        verify(uploader).queueUpload(dependency);
        verify(uploader).upload();

        /*
         * Verify the commands issued
         */
        final ArgumentCaptor<List> commandsCaptor = ArgumentCaptor.forClass(List.class);
        verify(cclTerminal).executeCommands(eq(sshTerminal), commandsCaptor.capture(), eq(false));
        final Iterator<String> commandsIterator = commandsCaptor.getValue().iterator();
        assertThat(commandsIterator.next()).isEqualTo("call compile(");
        assertThat(commandsIterator.next()).startsWith("'" + sourceCodeTargetLocation.getPath() + "',");
        assertThat(commandsIterator.next()).startsWith("'" + cerTemp + "/" + scriptName).endsWith(".out'")
                .isEqualTo("'" + listingOriginCaptor.getValue().getPath() + "'");
        assertThat(commandsIterator.next()).isEqualTo(") go");

        verify(downloader).download(Collections.singleton(request));
        verify(validator).validate(localListingFile);
        verify(point).collect();
    }

    /**
     * Test execution of the script compilation in debug mode.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testRunWithDebug() throws Exception {
        final String scriptName = "a_script";
        when(sourceCodeLocation.getName()).thenReturn(scriptName + ".prg");

        final URI listingLocationUri = URI.create("booo");
        when(localListingFile.toURI()).thenReturn(listingLocationUri);

        final URI sourceCodeTargetLocation = URI.create("sourceTarget");
        final CclResourceUploader uploader = prepareResourceUploader();
        when(uploader.upload()).thenReturn(Collections.singletonMap(sourceCodeLocation, sourceCodeTargetLocation));

        final String cerTemp = "cer_temp";
        prepareEnvironment(cerTemp);

        final JSchSshTerminal sshTerminal = prepareTerminal();

        prepareDownloder();
        prepareUploadRequest(mock(FileRequest.class), listingLocationUri);
        prepareValidator();

        final CclCommandTerminal cclTerminal = mock(CclCommandTerminal.class);

        final ScriptCompilerCommand toTest = new ScriptCompilerCommand(sourceCodeLocation, dependencies,
                localListingFile, true);
        toTest.run(cclTerminal);

        /*
         * Verify that debug mode was set
         */
        final ArgumentCaptor<List> commandsCaptor = ArgumentCaptor.forClass(List.class);
        verify(cclTerminal).executeCommands(eq(sshTerminal), commandsCaptor.capture(), eq(false));
        final LinkedList<String> capturedCommands = new LinkedList<String>(commandsCaptor.getValue());
        assertThat(capturedCommands.getFirst()).isEqualTo("SET COMPILE = DEBUG go");
        assertThat(capturedCommands.getLast()).isEqualTo("SET COMPILE = NODEBUG go");
    }

    /**
     * Test execution of the script compilation when no localListingLocation is provided.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testRunWithoutSpecifiedLocalListingFile() throws Exception {
        final String scriptName = "a_script";
        when(sourceCodeLocation.getName()).thenReturn(scriptName + ".prg");

        final URI listingLocationUri = URI.create("booo");
        when(localListingFile.toURI()).thenReturn(listingLocationUri);

        final URI sourceCodeTargetLocation = URI.create("sourceTarget");
        final CclResourceUploader uploader = prepareResourceUploader();
        when(uploader.upload()).thenReturn(Collections.singletonMap(sourceCodeLocation, sourceCodeTargetLocation));

        final String cerTemp = "cer_temp";
        prepareEnvironment(cerTemp);

        final JSchSshTerminal sshTerminal = prepareTerminal();

        prepareDownloder();

        final ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        mockStatic(FileRequestFactory.class);
        when(FileRequestFactory.create(uriCaptor.capture(), any(URI.class))).thenReturn(mock(FileRequest.class));

        prepareValidator();

        final CclCommandTerminal cclTerminal = mock(CclCommandTerminal.class);

        final ScriptCompilerCommand toTest = new ScriptCompilerCommand(sourceCodeLocation, dependencies, null, true);
        toTest.run(cclTerminal);

        /*
         * Verify that debug mode was set
         */
        final ArgumentCaptor<List> commandsCaptor = ArgumentCaptor.forClass(List.class);
        verify(cclTerminal).executeCommands(eq(sshTerminal), commandsCaptor.capture(), eq(false));
        final LinkedList<String> capturedCommands = new LinkedList<String>(commandsCaptor.getValue());
        assertThat(capturedCommands.getFirst()).isEqualTo("SET COMPILE = DEBUG go");
        assertThat(capturedCommands.getLast()).isEqualTo("SET COMPILE = NODEBUG go");
    }

    /**
     * Prepare a downloader.
     *
     * @return A {@link Downloader} that will be created with the next invocation of the
     *         {@link SftpDownloader#createDownloader(FtpProduct)} method.
     */
    private Downloader prepareDownloder() {
        final FtpProduct ftpProduct = mock(FtpProduct.class);
        mockStatic(AuthHelper.class);
        when(AuthHelper.fromCurrentSubject()).thenReturn(ftpProduct);

        final Downloader downloader = mock(Downloader.class);
        mockStatic(SftpDownloader.class);
        when(SftpDownloader.createDownloader(ftpProduct)).thenReturn(downloader);
        return downloader;
    }

    /**
     * Prepare an environment object to be created.
     *
     * @param cerTempDirectory
     *            The directory to be returned as the {@code cer_temp} directory.
     */
    private void prepareEnvironment(final String cerTempDirectory) {
        final Environment environment = mock(Environment.class);
        when(environment.getCerTemp()).thenReturn(cerTempDirectory);
        mockStatic(Environment.class);
        when(Environment.getEnvironment()).thenReturn(environment);
    }

    /**
     * Prepare the instantation of a {@link CclResourceUploader} object.
     *
     * @return A {@link CclResourceUploader} that will be returned by the static getter.
     */
    private CclResourceUploader prepareResourceUploader() {
        final CclResourceUploader uploader = mock(CclResourceUploader.class);
        mockStatic(CclResourceUploader.class);
        when(CclResourceUploader.getUploader()).thenReturn(uploader);
        return uploader;
    }

    /**
     * Prepare the creation of a terminal.
     *
     * @param maximumRuntime
     *            The maximum runtime to be used when constructing the terminal.
     * @return A {@link JSchSshTerminal} that will be constructed.
     * @throws Exception
     *             If any errors occur during the preparation.
     */
    private JSchSshTerminal prepareTerminal() throws Exception {
        final JSchSshTerminal sshTerminal = mock(JSchSshTerminal.class);
        whenNew(JSchSshTerminal.class).withNoArguments().thenReturn(sshTerminal);
        return sshTerminal;
    }

    /**
     * Prepare a return by a {@link FileRequestFactory}.
     *
     * @param request
     *            The {@link FileRequest} to be created by the factory.
     * @param remoteUri
     *            A {@link URI} representing the remote location for which the request is to be created.
     * @return An {@link ArgumentCaptor} used to capture the local location used in creation of the request.
     */
    private ArgumentCaptor<URI> prepareUploadRequest(final FileRequest request, final URI remoteUri) {
        final ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        mockStatic(FileRequestFactory.class);
        when(FileRequestFactory.create(uriCaptor.capture(), eq(remoteUri))).thenReturn(request);
        return uriCaptor;
    }

    /**
     * Prepare a {@link CompileErrorValidator} for testing.
     *
     * @return The {@link CompileErrorValidator} that was prepared.
     */
    private CompileErrorValidator prepareValidator() {
        final CompileErrorValidator validator = mock(CompileErrorValidator.class);
        mockStatic(CompileErrorValidator.class);
        when(CompileErrorValidator.getInstance()).thenReturn(validator);
        return validator;
    }
}
