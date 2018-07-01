package com.cerner.ccl.testing.maven.ccl.mojo;

import java.io.File;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import com.cerner.ccl.j4ccl.CclExecutor;

/**
 * Skeleton definition of a CCL mojo related to code compilation.
 *
 * @author Joshua Hyde
 *
 */

public abstract class BaseCclCompilationMojo extends BaseCclMojo {
    /**
     * Queue up any and all include files to be dynamically compiled.
     *
     * @param executor
     *            The {@link CclExecutor} to which the dynamic compilations are to be contributed.
     * @param directory
     *            A {@link File} representing the directory to be scanned for include files to be compiled.
     * @throws MojoExecutionException
     *             If anything occurs that would cause a build error.
     * @throws MojoFailureException
     *             If anything occurs that would cause a build failure.
     */
    protected void queueIncludeCompilation(final CclExecutor executor, final File directory)
            throws MojoExecutionException, MojoFailureException {
        final List<File> files = getIncludeFiles(directory);

        if (files.isEmpty()) {
            getLog().info("No includes files to compile in " + directory.getAbsolutePath());
            return;
        }

        for (final File file : files) {
            getLog().info("Queueing include of " + file.getPath());
            executor.addDynamicCompiler(file).commit();
        }
    }

    /**
     * Queue up CCL script compilations.
     *
     * @param executor
     *            A {@link CclExecutor} to which the script compilation actions will be contributed.
     * @param directory
     *            A {@link File} object representing the directory to be scanned for CCL scripts to be compiled.
     */
    protected void queueScriptCompilation(final CclExecutor executor, final File directory) {
        final List<File> files = getFiles(directory, CCL_SCRIPTS);
        if (files.isEmpty()) {
            getLog().info("No scripts to compile in " + directory.getAbsolutePath());
            return;
        }

        for (final File file : files) {
            getLog().info("Queueing compile of " + file.getPath());
            executor.addScriptCompiler(file).commit();
        }

        getLog().info("Compiling all scripts in " + directory.getAbsolutePath());
    }
}
