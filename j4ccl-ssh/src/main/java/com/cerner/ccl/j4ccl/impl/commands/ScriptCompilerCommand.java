package com.cerner.ccl.j4ccl.impl.commands;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cerner.ccl.j4ccl.exception.CclCommandException;
import com.cerner.ccl.j4ccl.exception.CclCompilationTimeoutException;
import com.cerner.ccl.j4ccl.impl.commands.util.CompileErrorValidator;
import com.cerner.ccl.j4ccl.impl.data.Environment;
import com.cerner.ccl.j4ccl.impl.util.AuthHelper;
import com.cerner.ccl.j4ccl.ssh.CclCommandTerminal;
import com.cerner.ccl.j4ccl.ssh.JSchSshTerminal;
import com.cerner.ccl.j4ccl.ssh.exception.SshException;
import com.cerner.ccl.j4ccl.ssh.exception.SshTimeoutException;
import com.cerner.ccl.j4ccl.util.CclResourceUploader;
import com.cerner.ftp.Downloader;
import com.cerner.ftp.data.factory.FileRequestFactory;
import com.cerner.ftp.sftp.SftpDownloader;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A CCL command that compiles a specified script.
 *
 * @author Joshua Hyde
 *
 */

public class ScriptCompilerCommand extends AbstractCclCommand {
    private final File sourceCodeLocation;
    private final Collection<File> dependencies;
    private final File localListingDestination;
    private final boolean doDebugCompile;

    private final Logger logger = LoggerFactory.getLogger(ScriptCompilerCommand.class);

    /**
     * Create a script compiler command.
     *
     * @param sourceCodeLocation
     *            A {@link File} object representing the location of the source PRG file to be compiled.
     * @param dependencies
     *            A {@link Collection} of {@link File} objects that must be uploaded to their respective locations prior
     *            to the successful compilation of the given source code.
     * @param localListingDestination
     *            A {@link File} representing the location on the local disk to where the listing output should be
     *            downloaded; if {@code null}, then a temporary location will be used.
     * @param doDebugCompile
     *            {@code true} if the script should be compiled in debug mode; {@code false} if the script should not be
     *            compiled in debug mode.
     * @throws IllegalArgumentException
     *             If the given source code location or dependencies are {@code null}.
     */
    public ScriptCompilerCommand(final File sourceCodeLocation, final Collection<File> dependencies,
            final File localListingDestination, final boolean doDebugCompile) {

        if (sourceCodeLocation == null)
            throw new IllegalArgumentException("Source code location cannot be null.");

        if (dependencies == null)
            throw new IllegalArgumentException("Dependencies cannot be null.");

        this.sourceCodeLocation = sourceCodeLocation;
        this.dependencies = Collections.unmodifiableCollection(dependencies);
        this.localListingDestination = localListingDestination;
        this.doDebugCompile = doDebugCompile;
    }

    @Override
    public void run(final CclCommandTerminal terminal) {
        final EtmPoint point = PointFactory.getPoint(getClass(), "run");
        try {
            final String objectName = parseObjectName(sourceCodeLocation);

            final CclResourceUploader uploader = CclResourceUploader.getUploader();
            uploader.queueUpload(sourceCodeLocation);
            for (final File dependency : dependencies)
                uploader.queueUpload(dependency);
            final URI sourceCodeTargetLocation = uploader.upload().get(sourceCodeLocation);

            final String listingOutputFilename = createListingOutputFilename(objectName);
            final File finalListingDestination = localListingDestination == null
                    ? createTemporaryFile(listingOutputFilename)
                    : localListingDestination;

            final Environment environment = Environment.getEnvironment();
            final URI listingTargetLocation = URI.create(environment.getCerTemp() + "/" + listingOutputFilename);

            /*
             * CCL can't handle too long of lines, so put each parameter on its own line
             */
            try {
                final List<String> commands = new ArrayList<String>();
                if (doDebugCompile)
                    commands.add("SET COMPILE = DEBUG go");
                commands.addAll(Arrays.asList("call compile(", "'" + sourceCodeTargetLocation.getPath() + "',",
                        "'" + listingTargetLocation.getPath() + "'", ") go"));
                if (doDebugCompile)
                    commands.add("SET COMPILE = NODEBUG go");
                terminal.executeCommands(new JSchSshTerminal(), commands, false);
            } catch (final SshTimeoutException e) {
                throw new CclCompilationTimeoutException("Compilation of " + objectName + " timed out.", e);
            } catch (final SshException e) {
                throw new CclCommandException("Compilation of " + objectName + " failed.", e);
            }

            // Download and verify the listing output
            final Downloader downloader = SftpDownloader.createDownloader(AuthHelper.fromCurrentSubject());
            downloader.download(Collections
                    .singleton(FileRequestFactory.create(listingTargetLocation, finalListingDestination.toURI())));
            logger.info("validating compile {}", listingOutputFilename);
            CompileErrorValidator.getInstance().validate(finalListingDestination);
        } finally {
            point.collect();
        }
    }

    /**
     * Create the name of the listing output file as it will exist on the remote system.
     *
     * @param objectName
     *            The name of the script (sans file extension) for which the output is to be generated.
     * @return The name of the file to contain the listing output.
     */
    private String createListingOutputFilename(final String objectName) {
        return String.format("%s_%d.out", objectName, System.currentTimeMillis());
    }

    /**
     * Create a temporary file.
     *
     * @param fileNamePrefix
     *            The prefix to be used for the filename.
     * @return A {@link File} representing a temporary file.
     */
    private File createTemporaryFile(final String fileNamePrefix) {
        try {
            return File.createTempFile(fileNamePrefix, null);
        } catch (final IOException e) {
            throw new CclCommandException("Failed to create temporary listing file.", e);
        }
    }

    /**
     * Parse the name of the object to be compiled from the given filename.
     *
     * @param file
     *            A {@link File} object representing the location on the hard disk of the source to be compiled.
     * @return The name of the object to be compiled.
     */
    private String parseObjectName(final File file) {
        final String fileName = file.getName();
        // Chop off .PRG
        return fileName.substring(0, fileName.length() - 4).toLowerCase(Locale.getDefault());
    }
}
