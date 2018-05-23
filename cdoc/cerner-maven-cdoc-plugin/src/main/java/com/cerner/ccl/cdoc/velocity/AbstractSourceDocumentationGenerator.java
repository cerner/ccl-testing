package com.cerner.ccl.cdoc.velocity;

import java.io.File;
import java.io.Writer;
import java.net.MalformedURLException;
import java.util.List;

import org.apache.maven.reporting.MavenReportException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.cerner.ccl.cdoc.script.ScriptExecutionDetails;
import com.cerner.ccl.cdoc.velocity.navigation.Navigation;
import com.cerner.ccl.cdoc.velocity.structure.RecordStructureFormatter;
import com.cerner.ccl.parser.data.record.InterfaceStructureType;
import com.cerner.ccl.parser.data.record.RecordStructure;

/**
 * Skeleton definition of an object that generates documentation for an object.
 *
 * @author Joshua Hyde
 *
 * @param <T>
 *            The type of object for which this generates documentation.
 */

public abstract class AbstractSourceDocumentationGenerator<T> extends AbstractVelocityGenerator {
    private final Writer writer;
    private final File cssDirectory;
    private final ScriptExecutionDetails details;
    private final Navigation backNavigation;

    /**
     * Create a documentation generator.
     *
     * @param writer
     *            The {@link Writer} to which the documentation output will be written.
     * @param cssDirectory
     *            A {@link File} object representing the directory in which the CSS files reside.
     * @param details
     *            A {@link ScriptExecutionDetails} object for the object that is to be documented.
     * @param backNavigation
     *            A {@link Navigation} object representing the anchor text and destination for the "back" link in the
     *            generated report.
     * @throws IllegalArgumentException
     *             If any of the given objects are {@code null}.
     */
    public AbstractSourceDocumentationGenerator(final Writer writer, final File cssDirectory,
            final ScriptExecutionDetails details, final Navigation backNavigation) {
        if (writer == null) {
            throw new IllegalArgumentException("Writer cannot be null.");
        }

        if (cssDirectory == null) {
            throw new IllegalArgumentException("CSS directory cannot be null.");
        }

        if (details == null) {
            throw new IllegalArgumentException("Script execution details cannot be null.");
        }

        if (backNavigation == null) {
            throw new IllegalArgumentException("Back navigation cannot be null.");
        }

        this.writer = writer;
        this.cssDirectory = cssDirectory;
        this.details = details;
        this.backNavigation = backNavigation;
    }

    /**
     * Generate the documentation.
     *
     * @throws MavenReportException
     *             If any errors occur during the documentation generation.
     */
    public void generate() throws MavenReportException {
        final VelocityEngine engine = getEngine();
        final VelocityContext context = new VelocityContext();
        context.put("object", getObject());
        context.put("objectFilename", getObjectFilename());
        context.put("objectName", getObjectName());
        context.put("executionDetails", details);
        context.put("backNavigation", backNavigation);

        try {
            context.put("cssDirectory", cssDirectory.toURI().toURL().toExternalForm());
        } catch (final MalformedURLException e) {
            throw new MavenReportException("Failed to convert CSS directory to URL.", e);
        }

        final RecordStructureFormatter structureFormatter = new RecordStructureFormatter(engine);
        for (final RecordStructure structure : getRecordStructures()) {
            if (InterfaceStructureType.REQUEST.equals(structure.getStructureType())) {
                context.put("requestDefinition", structureFormatter.format(structure));
                context.put("requestRecordStructure", structure);
            } else if (InterfaceStructureType.REPLY.equals(structure.getStructureType())) {
                context.put("replyDefinition", structureFormatter.format(structure));
                context.put("replyRecordStructure", structure);
            }
        }

        try {
            final Template template = engine.getTemplate("/velocity/source-doc.vm", "utf-8");
            template.merge(context, writer);
        } catch (final Exception e) {
            throw new MavenReportException("Failed to initialize and merge template.", e);
        }
    }

    /**
     * Get the object for which documentation is to be generated.
     *
     * @return The object for which documentation is to be generated.
     */
    protected abstract T getObject();

    /**
     * Get the filename of the object for which documentation is to be generated.
     *
     * @return The filename of the object for which documentation is to be generated.
     */
    protected abstract String getObjectFilename();

    /**
     * Get the name of the object for which documentation is to be generated.
     *
     * @return The name of the object for which documentation is to be generated.
     */
    protected abstract String getObjectName();

    /**
     * Get the record structures documented as part of this object.
     *
     * @return A {@link List} of {@link RecordStructure} objects representing the record structures to be documented for
     *         this object.
     */
    protected abstract List<RecordStructure> getRecordStructures();
}
