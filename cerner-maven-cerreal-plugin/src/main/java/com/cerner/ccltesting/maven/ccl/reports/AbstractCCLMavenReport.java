package com.cerner.ccltesting.maven.ccl.reports;

import java.io.File;

import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;

/**
 * An abstract definition of a Maven reporting mojo used to build reports around CCL code.
 *
 * @author Jeff Wiedemann
 *
 */

public abstract class AbstractCCLMavenReport extends AbstractMavenReport {
    /**
     * The output directory.
     *
     */
    @Parameter(defaultValue = "${project.reporting.outputDirectory}", required = true)
    @SuppressWarnings("hiding")
    protected File outputDirectory;

    /**
     * <i>Maven Internal</i>: The Doxia Site Renderer.
     *
     */
    @Component()
    @SuppressWarnings("hiding")
    protected Renderer siteRenderer;

    /**
     * <i>Maven Internal</i>: The Project descriptor.
     *
     */
    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    @SuppressWarnings("hiding")
    protected MavenProject project;

    /**
     * The parent directory for the XML report files for test results that will be parsed and rendered to HTML format.
     */
    @Parameter(defaultValue = "${project.Basedir}/target/test-results/")
    protected File testResultsDirectory;

    /**
     * The directory where error reports will be written.
     */
    @Parameter(defaultValue = "${project.basedir}/target/cerreal-report-errors/", required = true, readonly = true)
    protected File reportErrorDirectory;

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getOutputDirectory() {
        return outputDirectory.getAbsolutePath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Renderer getSiteRenderer() {
        return siteRenderer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MavenProject getProject() {
        return project;
    }

    /**
     * Helper routine for Mojos inheriting this class which gets the requested File from the specified directory by the files name
     *
     * @param directory
     *            The directory to scan for the file
     * @param fileName
     *            The name of the file to scan for by File.getName()
     * @return The requested file if it exists, or null otherwise
     */
    static public File getDirectoryFile(File directory, String fileName) {
        if (!directory.isDirectory())
            return null;

        final File requestedFile = new File(directory, fileName);
        return requestedFile.exists() && requestedFile.isFile() ? requestedFile : null;
    }
}
