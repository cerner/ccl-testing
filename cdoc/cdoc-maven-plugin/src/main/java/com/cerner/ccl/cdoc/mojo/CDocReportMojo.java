package com.cerner.ccl.cdoc.mojo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.FileUtils;

import com.cerner.ccl.cdoc.mojo.data.Documentation;
import com.cerner.ccl.cdoc.mojo.data.DocumentationNameComparator;
import com.cerner.ccl.cdoc.script.ScriptExecutionDetails;
import com.cerner.ccl.cdoc.script.ScriptExecutionDetailsParser;
import com.cerner.ccl.cdoc.velocity.IncludeDocGenerator;
import com.cerner.ccl.cdoc.velocity.ScriptDocGenerator;
import com.cerner.ccl.cdoc.velocity.SummaryGenerator;
import com.cerner.ccl.cdoc.velocity.navigation.Navigation;
import com.cerner.ccl.parser.data.CclScript;
import com.cerner.ccl.parser.data.IncludeFile;
import com.cerner.ccl.parser.exception.CDocParsingException;
import com.cerner.ccl.parser.text.TextParser;

/**
 * This mojo generates the CDoc report.
 *
 * @author Joshua Hyde
 *
 */
@Mojo(name = "cdoc-report", defaultPhase = LifecyclePhase.SITE)
public class CDocReportMojo extends AbstractMavenReport {
    /**
     * A set of files to exclude from consideration of being documented. This is largely intended to help facilitate the
     * transition of existing projects with multiple scripts to CDoc, preventing a requirement of a complete overhaul of
     * documentation in all of the scripts in the project to CDoc in order to use this plugin.
     * <p>
     * These exclusions can be expressed as Ant file pattern.
     */
    @Parameter()
    protected String[] excludes;

    /**
     * The output directory.
     */
    @Parameter(property = "project.reporting.outputDirectory", readonly = true, required = true)
    @SuppressWarnings("hiding")
    protected File outputDirectory;

    /**
     * The encoding to be used when writing the report out.
     */
    @Parameter(property = "project.reporting.outputEncoding", defaultValue = "utf-8", required = true)
    protected String outputEncoding;

    /**
     * <i>Maven Internal</i>: The Project descriptor.
     *
     */
    @SuppressWarnings("hiding")
    @Parameter(property = "project", readonly = true, required = true)
    protected MavenProject project;

    /**
     * The location within the project directory where the main CCL program file(s) can be found.
     *
     */
    @Parameter(defaultValue = "src/main/resources", required = true)
    protected File resourcesDirectory;

    /**
     * The location within the project directory where the main CCL program file(s) can be found.
     *
     */
    @Parameter(property = "ccl-sourceDirectory", defaultValue = "src/main/ccl", required = true)
    protected File sourceDirectory;

    /**
     * <i>Maven Internal</i>: The Doxia Site Renderer.
     */
    @SuppressWarnings("hiding")
    @Component()
    protected Renderer siteRenderer;

    private final TextParser textParser;
    private final ScriptExecutionDetailsParser detailsParser;

    /**
     * Create a mojo.
     */
    public CDocReportMojo() {
        this(new TextParser(), new ScriptExecutionDetailsParser());
    }

    /**
     * Create a mojo.
     *
     * @param textParser
     *            A {@link TextParser}.
     * @param detailsParser
     *            A {@link ScriptExecutionDetailsParser}.
     * @throws IllegalArgumentException
     *             If any of the given parsers are {@code null}.
     */
    public CDocReportMojo(final TextParser textParser, final ScriptExecutionDetailsParser detailsParser) {
        super();
        getLog().info("entering CDocReportMojo constructor");
        if (textParser == null) {
            throw new IllegalArgumentException("Text parser cannot be null.");
        }

        if (detailsParser == null) {
            throw new IllegalArgumentException("Script execution details parser cannot be null.");
        }

        this.textParser = textParser;
        this.detailsParser = detailsParser;
    }

    @Override
    public boolean canGenerateReport() {
        try {
            return !getFiles().isEmpty();
        } catch (final MavenReportException e) {
            getLog().error("Unable to determine if report can run; report will not generate.", e);
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription(final Locale locale) {
        return "A report of documentation surrounding CCL scripts.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName(final Locale locale) {
        return "CDoc";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOutputName() {
        return "cdoc-report";
    }

    @Override
    public boolean isExternalReport() {
        return true;
    }

    @Override
    protected void executeReport(final Locale locale) throws MavenReportException {
        final List<File> scriptFiles = getFiles();

        final File cssDirectory = new File(getDestinationDirectory(), "css");
        if (!cssDirectory.exists()) {
            try {
                FileUtils.forceMkdir(cssDirectory);
            } catch (final IOException e) {
                throw new MavenReportException("Failed to create CSS directory.", e);
            }
        }

        copyCss(cssDirectory, "cdoc.css");
        copyCss(cssDirectory, "script-doc.css");

        final List<Documentation> docs = toDocumentation(scriptFiles);
        if (docs.size() == 1) {
            final Writer indexWriter = getIndexWriter();
            try {
                writeObjectDocumentation(cssDirectory, docs.get(0), indexWriter,
                        new Navigation("Back to Maven Site", "./project-reports.html"));
            } finally {
                IOUtils.closeQuietly(indexWriter);
            }
        } else {
            copyCss(cssDirectory, "script-doc-summary.css");
            writeDocumentationSummary(cssDirectory, docs);
            writeObjectDocumentation(cssDirectory, docs, new Navigation("Back to Summary", "../cdoc-report.html"));
        }
    }

    @Override
    protected String getOutputDirectory() {
        return outputDirectory.getAbsolutePath();
    }

    @Override
    protected MavenProject getProject() {
        return project;
    }

    @Override
    protected Renderer getSiteRenderer() {
        return siteRenderer;
    }

    /**
     * Copy a CSS file.
     *
     * @param cssDirectory
     *            A {@link File} representing the directory to which the CSS file should be copied.
     * @param cssFilename
     *            The name of the CSS file to be copied.
     * @throws MavenReportException
     *             If any errors occur during the copying.
     */
    void copyCss(final File cssDirectory, final String cssFilename) throws MavenReportException {
        final File cssFile = new File(cssDirectory, cssFilename);
        final InputStream scriptCssStream = getClass().getResourceAsStream("/css/" + cssFilename);
        Writer scriptCssOutput = null;
        try {
            scriptCssOutput = new OutputStreamWriter(new FileOutputStream(cssFile),
                    Charset.forName(getOutputEncoding()));
            IOUtils.copy(scriptCssStream, scriptCssOutput, "utf-8");
        } catch (final IOException e) {
            throw new MavenReportException("Failed to copy summary CSS file: " + cssFilename, e);
        } finally {
            IOUtils.closeQuietly(scriptCssOutput);
            IOUtils.closeQuietly(scriptCssStream);
        }
    }

    /**
     * Get the sub-directory to which generated documentation can be written.
     *
     * @return A {@link File} object representing the directory within the site directory to which generated
     *         documentation can be written.
     */
    File getDestinationDirectory() {
        return new File(getOutputDirectory(), getOutputName());
    }

    /**
     * Get all the files to be read for documentation.
     *
     * @return A {@link List} of {@link File} objects representing the files that are to be parsed for documentation.
     * @throws MavenReportException
     *             If any errors occur during the collection of files.
     */
    List<File> getFiles() throws MavenReportException {
        try {
            final String excludePattern = excludes == null ? "" : StringUtils.join(excludes, ',');
            final List<File> sourceFiles = new ArrayList<File>();
            if (sourceDirectory.exists()) {
                sourceFiles.addAll(FileUtils.getFiles(sourceDirectory, "*.prg,*.inc,*.sub", excludePattern));
            }

            if (resourcesDirectory.exists()) {
                sourceFiles.addAll(FileUtils.getFiles(resourcesDirectory, "*.prg,*.inc,*.sub", excludePattern));
            }

            return sourceFiles;
        } catch (final IOException e) {
            throw new MavenReportException("Failed to locate source files.", e);
        }
    }

    /**
     * Parse the documentation for an include file.
     *
     * @param filename
     *            The name of the include file.
     * @param sourceCode
     *            A {@link List} of {@link String} objects representing the source of the include file.
     * @return An {@link IncludeFile} object representing the given file.
     * @throws MavenReportException
     *             If any errors occur during the parsing.
     */
    IncludeFile getIncludeFile(final String filename, final List<String> sourceCode) throws MavenReportException {
        try {
            return textParser.parseIncludeFile(filename.toLowerCase(Locale.US), sourceCode);
        } catch (final CDocParsingException e) {
            throw new MavenReportException("Failed to parse include file " + filename, e);
        }
    }

    /**
     * Get a writer to write to the index file for this report.
     *
     * @return A {@link Writer} representing a way to write to the index file of this report.
     * @throws MavenReportException
     *             If any errors occur while opening the writer.
     */
    Writer getIndexWriter() throws MavenReportException {
        try {
            return new OutputStreamWriter(
                    new FileOutputStream(new File(getOutputDirectory(), getOutputName() + ".html")),
                    Charset.forName(getOutputEncoding()));
        } catch (final IOException e) {
            throw new MavenReportException("Failed to open writer to index file.", e);
        }
    }

    /**
     * Get the encoding to be used to output the reports.
     *
     * @return A {@link Charset} representing the output encoding of the reports.
     */
    @Override
    protected String getOutputEncoding() {
        return StringUtils.isBlank(outputEncoding) ? "utf-8" : outputEncoding;
    }

    /**
     * Parse the documentation for a script.
     *
     * @param filename
     *            The name of the CCL script PRG file.
     * @param sourceCode
     *            A {@link List} of {@link String} objects representing the source of the script.
     * @return A {@link CclScript} object representing the given file.
     * @throws MavenReportException
     *             If any errors occur during the parsing.
     */
    CclScript getScript(final String filename, final List<String> sourceCode) throws MavenReportException {
        try {
            return textParser.parseCclScript(filename.substring(0, filename.lastIndexOf('.')).toLowerCase(Locale.US),
                    sourceCode);
        } catch (final CDocParsingException e) {
            throw new MavenReportException("Failed to parse script " + filename, e);
        }
    }

    /**
     * Get a writer to which the documentation will be written.
     *
     * @param doc
     *            A {@link Documentation} object representing the object to be documented.
     * @return A {@link Writer} to which documentation should be written for the given object.
     * @throws MavenReportException
     *             If any errors occur while creating the writer.
     */
    Writer getWriter(final Documentation doc) throws MavenReportException {
        try {
            final File destinationFile = new File(getDestinationDirectory(), doc.getDestinationFilename());
            // Make sure that the output directory exists
            if (!destinationFile.getParentFile().exists()) {
                FileUtils.forceMkdir(destinationFile.getParentFile());
            }
            return new OutputStreamWriter(new FileOutputStream(destinationFile), Charset.forName(getOutputEncoding()));
        } catch (final IOException e) {
            throw new MavenReportException(
                    "Failed to prepare output file for file " + doc.getSourceFile().getAbsolutePath(), e);
        }
    }

    /**
     * Read the contents of a file.
     *
     * @param file
     *            A {@link File} representing the file to be read.
     * @return A {@link List} of {@link String} objects representing the text of the source.
     * @throws MavenReportException
     *             If any errors occur while reading the file.
     */
    List<String> readSource(final File file) throws MavenReportException {
        try {
            return org.apache.commons.io.FileUtils.readLines(file, getInputEncoding());
        } catch (final IOException e) {
            throw new MavenReportException("Failed to read source for file " + file, e);
        }
    }

    /**
     * Convert a list of files to a list of documentation objects.
     *
     * @param files
     *            A {@link List} of {@link File} objects that are to be converted.
     * @return A {@link List} of {@link Documentation} objects representing the given list of files.
     */
    List<Documentation> toDocumentation(final List<File> files) {
        final List<Documentation> documentation = new ArrayList<Documentation>(files.size());
        for (final File file : files) {
            documentation.add(new Documentation(file));
        }
        Collections.sort(documentation, new DocumentationNameComparator());
        return documentation;
    }

    /**
     * Write the summary page for all documentation.
     *
     * @param cssDirectory
     *            A {@link File} object representing the directory to which CSS files should be written.
     * @param documentation
     *            A {@link List} of {@link Documentation} objects to be used in the summary.
     * @throws MavenReportException
     *             If any errors occur during the generation.
     */
    void writeDocumentationSummary(final File cssDirectory, final List<Documentation> documentation)
            throws MavenReportException {
        final SummaryGenerator generator = new SummaryGenerator();
        Writer destination = null;
        try {
            destination = getIndexWriter();
            generator.generate(getProject(), documentation, cssDirectory, destination);
        } finally {
            IOUtils.closeQuietly(destination);
        }
    }

    /**
     * Write documentation for an object.
     *
     * @param cssDirectory
     *            A {@link File} representing the directory to which CSS files should be written.
     * @param documentation
     *            A {@link List} of {@link Documentation} objects representing the objects to be documented.
     * @param backNavigation
     *            A {@link Navigation} object representing the anchor text and destination for the "back" link in the
     *            generated report.
     * @throws MavenReportException
     *             If any errors occur during the documentation generation.
     */
    void writeObjectDocumentation(final File cssDirectory, final List<Documentation> documentation,
            final Navigation backNavigation) throws MavenReportException {
        /*
         * Parse the scripts and generate the output
         */
        for (final Documentation doc : documentation) {
            final Writer writer = getWriter(doc);
            try {
                writeObjectDocumentation(cssDirectory, doc, writer, backNavigation);
            } finally {
                IOUtils.closeQuietly(writer);
            }
        }
    }

    /**
     * Write out an object's documentation.
     *
     * @param cssDirectory
     *            A {@link File} representing the directory to which CSS files should be written.
     * @param documentation
     *            A {@link Documentation} object representing the object to be documented.
     * @param output
     *            A {@link Writer} representing the destination of the documentation report.
     * @param backNavigation
     *            A {@link Navigation} object representing the anchor text and destination for the "back" link in the
     *            generated report.
     * @throws MavenReportException
     *             If any errors occur while generating the report.
     */
    void writeObjectDocumentation(final File cssDirectory, final Documentation documentation, final Writer output,
            final Navigation backNavigation) throws MavenReportException {
        final List<String> source = readSource(documentation.getSourceFile());
        final ScriptExecutionDetails executionDetails = detailsParser.getDetails(source);
        switch (documentation.getObjectType()) {
        case PRG:
            final CclScript script = getScript(documentation.getSourceFile().getName(), source);
            final ScriptDocGenerator scriptGenerator = new ScriptDocGenerator(script, cssDirectory, executionDetails,
                    output, backNavigation);
            scriptGenerator.generate();
            break;
        case INC:
        case SUB:
            final IncludeFile include = getIncludeFile(documentation.getSourceFile().getName(), source);
            final IncludeDocGenerator includeGenerator = new IncludeDocGenerator(include, cssDirectory,
                    executionDetails, output, backNavigation);
            includeGenerator.generate();
            break;
        default:
            throw new IllegalArgumentException("Unrecognized object type: " + documentation);
        }
    }
}
