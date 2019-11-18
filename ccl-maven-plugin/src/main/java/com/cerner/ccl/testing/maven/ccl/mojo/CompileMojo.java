package com.cerner.ccl.testing.maven.ccl.mojo;

import java.io.File;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.security.auth.Subject;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import com.cerner.ccl.j4ccl.CclExecutor;
import com.cerner.ccl.j4ccl.util.CclResourceUploader;

/**
 * A mojo used to compile CCL code.
 *
 * @author Mark Cummings
 * @author Joshua Hyde
 *
 */
@Mojo(name = "compile")
public class CompileMojo extends BaseCclCompilationMojo {

    /**
     * The location within the project directory where the main CCL program file(s) can be found.
     *
     */
    @Parameter(property = "ccl-sourceDirectory", defaultValue = "src/main/ccl")
    protected File sourceDirectory;

    /**
     * When set to {@code true}, causes this mojo to skip processing.
     *
     * @since 1.0-alpha-3
     */
    @Parameter(property = "ccl-skipCompile", defaultValue = "${ccl-skipProcessing}")
    protected boolean skipCompile;

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipCompile) {
            getLog().info("Skipping compile goal");
            return;
        }

        if (!sourceDirectory.exists()) {
            throw new MojoFailureException("Source directory does not exist: " + sourceDirectory.getAbsolutePath());
        }

        uploadIncludeFiles(sourceDirectory);
        final CclExecutor executor = createCclExecutor();
        queueScriptCompilation(executor, sourceDirectory);
        Subject.doAs(getSubject(), new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                executor.execute();
                return null;
            }
        });
    }

    private List<File> uploadIncludeFiles(final File directory) throws MojoExecutionException, MojoFailureException {
        final List<File> includeFiles = new ArrayList<File>(super.getIncludeFiles(directory));
        final CclResourceUploader uploader = CclResourceUploader.getUploader();
        for (final File includeFile : includeFiles) {
            getLog().info("Queueing upload of file " + includeFile.getName());
            uploader.queueUpload(includeFile);
        }

        Subject.doAs(getSubject(), new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                uploader.upload();
                return null;
            }
        });
        return Collections.<File> emptyList();
    }
}
