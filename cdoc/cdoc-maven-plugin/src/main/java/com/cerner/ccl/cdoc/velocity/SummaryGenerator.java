package com.cerner.ccl.cdoc.velocity;

import java.io.File;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReportException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.cerner.ccl.cdoc.mojo.data.Documentation;

/**
 * This generator creates a list view of all documented files.
 *
 * @author Joshua Hyde
 *
 */

public class SummaryGenerator extends AbstractVelocityGenerator {
    /**
     * Generate the summary.
     *
     * @param project
     *            A {@link MavenProject} object representing the Maven project for which the report is being generated.
     * @param documentation
     *            A {@link List} of {@link Documentation} objects representing the objects that were documented.
     * @param cssDirectory
     *            A {@link File} representing the directory containing the CSS files.
     * @param destination
     *            A {@link Writer} to which the generated summary is to be written.
     * @throws IllegalArgumentException
     *             If any of the given objects is {@code null}.
     * @throws MavenReportException
     *             If any errors occur while generating the summary.
     */
    public void generate(final MavenProject project, final List<Documentation> documentation, final File cssDirectory,
            final Writer destination) throws MavenReportException {
        if (project == null) {
            throw new IllegalArgumentException("Maven project cannot be null.");
        }

        if (documentation == null) {
            throw new IllegalArgumentException("Documentation cannot be null.");
        }

        if (cssDirectory == null) {
            throw new IllegalArgumentException("CSS directory cannot be null.");
        }

        if (destination == null) {
            throw new IllegalArgumentException("Destination writer cannot be null.");
        }

        final VelocityContext context = new VelocityContext();
        context.put("projectName", project.getName());
        context.put("projectVersion", project.getVersion());
        context.put("docs", documentation);
        try {
            context.put("cssDirectory", cssDirectory.toURI().toURL().toExternalForm());
        } catch (final MalformedURLException e) {
            throw new MavenReportException("Failed to convert CSS directory to URL.", e);
        }

        final VelocityEngine engine = getEngine();
        try {
            final Template template = engine.getTemplate("/velocity/script-doc-summary.vm", "utf-8");
            template.merge(context, destination);
        } catch (final Exception e) {
            throw new MavenReportException("Failed to initialize and merge template.", e);
        }
    }
}
