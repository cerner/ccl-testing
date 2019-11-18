package com.cerner.ccl.analysis.mojo;

import static org.codehaus.plexus.util.StringUtils.isEmpty;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.security.auth.Subject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.codehaus.plexus.util.FileUtils;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;

import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.engine.AnalysisEngine;
import com.cerner.ccl.analysis.engine.j4ccl.FtpProductProvider;
import com.cerner.ccl.analysis.engine.j4ccl.J4CclAnalysisEngine;
import com.cerner.ccl.analysis.exception.AnalysisRuleProvider;
import com.cerner.ccl.analysis.mojo.exclusions.ViolationFilterEngine;
import com.cerner.ccl.analysis.mojo.exclusions.jaxb.Exclusions;
import com.cerner.ccl.analysis.mojo.util.J4CclFtpProductProvider;
import com.cerner.ccl.j4ccl.CclExecutor;
import com.cerner.ccl.j4ccl.TerminalProperties;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePasswordCredential;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePrincipal;

/**
 * A mojo to generate static analysis reports on CCL code.
 *
 * @author Joshua Hyde
 */

// TODO: test this class
@Mojo(name = "whitenoise-report", defaultPhase = LifecyclePhase.SITE)
public class WhitenoiseMojo extends AbstractMavenReport {
    /**
     * The output directory.
     */
    @Parameter(property = "project.reporting.outputDirectory", readonly = true, required = true)
    @SuppressWarnings("hiding")
    protected File outputDirectory;

    /**
     * <i>Maven Internal</i>: The Doxia Site Renderer.
     */
    @SuppressWarnings("hiding")
    @Component()
    protected Renderer siteRenderer;

    /**
     * <i>Maven Internal</i>: The Project descriptor.
     */
    @SuppressWarnings("hiding")
    @Parameter(property = "project", readonly = true, required = true)
    protected MavenProject project;

    /**
     * The username to be used to log into the remote server.
     *
     * @see #hostCredentialsId
     */
    @Parameter(property = "ccl-hostUsername")
    protected String username;

    /**
     * The password to be used to log into the remote server.
     *
     * @see #hostCredentialsId
     */
    @Parameter(property = "ccl-hostPassword")
    protected String password;

    /**
     * The host to which the plugin is to connect to compile and translate the script.
     */
    @Parameter(property = "ccl-host", required = true)
    protected String hostAddress;

    /**
     * The environment to which the plugin will {@code envset} upon connecting to the remote server.
     */
    @Parameter(property = "ccl-environment", required = true)
    protected String environmentName;

    /**
     * The ID of a {@code &lt;server /&gt;} tag within your settings.xml that provides the username and password used to
     * connect to the remote server. If present, this will override the usage of {@link #username} and
     * {@link #password}.
     *
     * @since 2.0
     * @see #password
     * @see #username
     */
    @Parameter(property = "ccl-hostCredentialsId")
    protected String hostCredentialsId;

    /**
     * A {@link SecDispatcher} used to decrypt encrypted passwords supplied via a {@code &lt;server /&gt;} setting in
     * the settings.xml.
     *
     * @see #hostCredentialsId
     */
    @Component(hint = "mng-4384")
    protected SecDispatcher securityDispatcher;

    /**
     * The settings provided by the {@code settings.xml} file.
     *
     */
    @Parameter(defaultValue = "${settings}", readonly = true)
    protected Settings settings;

    /**
     * The timeout in milliseconds when waiting for a back-end command to complete. Does not apply to the execution of
     * CCL commands using "go". Those will always be given unlimited time to execute.
     *
     * @since 2.0
     */
    @Parameter(property = "ccl-expectationTimeout", defaultValue = "20000")
    protected long expectationTimeout;

    /**
     * A regular expression that will match the back end operating system prompt for the configured user. This is used
     * to recognize when the back end operating system has finished processing and is ready for another command The
     * following value is constructed by default: "$ccl-domainUsername:ccl-environment@$ccl-host:[^@gt;]*@gt;\s*"
     *
     * @since 2.0
     */
    @Parameter(property = "ccl-osPromptPattern", defaultValue = "")
    protected String osPromptPattern;

    /**
     * The source directory from which CCL code is to be read.
     */
    @Parameter(property = "ccl-sourceDirectory", defaultValue = "src/main/ccl", required = true)
    protected File sourceDirectory;

    /**
     * A boolean indicator to control whether or not the source code is to be compiled before attempting to analyze it.
     * This defaults {@code false} because the common use case is to use this in tandem with, or shortly after, the
     * compilation of a CCL script. As such, to compile it again is needless; if, however, you are not compiling in a
     * sufficiently-recent time relative to the generation of this report, then consider changing this parameter to
     * {@code true}.
     */
    @Parameter(defaultValue = "false", property = "doCompile", required = true)
    protected boolean doCompile;

    /**
     * A boolean indicator to control whether or not the raw violation data is written to disk. Currently the format is
     * fixed.
     *
     * @since 2.2
     */
    @Parameter(defaultValue = "false", property = "outputRawData", required = true)
    protected boolean outputRawData;

    /**
     * The directory where the raw data will be written..
     *
     * @since 2.2
     */
    @Parameter(defaultValue = "${project.build.directory}/whitenoise/", property = "whitenoiseDataDirectory", required = true)
    protected File dataDirectory;

    /**
     * The location of the file of filters that can be optionally excluded from the report.
     */
    @Parameter()
    private File filterFile;

    private final FtpProductProvider productProvider;

    // used to construct the default OS prompt.
    private String defaultOSPromptUsername;

    /**
     * Create a Whitenoise report mojo.
     */
    public WhitenoiseMojo() {
        this(new J4CclFtpProductProvider());
    }

    /**
     * Create a Whitenoise report mojo.
     *
     * @param productProvider
     *            An {@link FtpProductProvider} used to provide authentication for uploading and downloading files.
     * @throws IllegalArgumentException
     *             If the given provider is {@code null}.
     */
    public WhitenoiseMojo(final FtpProductProvider productProvider) {
        if (productProvider == null) {
            throw new IllegalArgumentException("FTP product provider cannot be null.");
        }

        this.productProvider = productProvider;
    }

    @Override
    public boolean canGenerateReport() {
        if (!sourceDirectory.exists()) {
            getLog().info("sourceDirectory " + sourceDirectory.getPath() + " does not exist.");
            return false;
        }
        try {
            return !getFiles().isEmpty();
        } catch (final MavenReportException e) {
            throw new RuntimeException("Failed to determine whether or not the report can be generated.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription(final Locale locale) {
        return "A static analysis report of CCL code.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName(final Locale locale) {
        return "Whitenoise";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOutputName() {
        return "whitenoise-report";
    }

    @Override
    protected void executeReport(final Locale locale) throws MavenReportException {
        final Sink sink = getSink();
        sink.head();
        sink.title();
        sink.text(getName(Locale.getDefault()));
        sink.title_();
        sink.head_();

        sink.body();
        sink.sectionTitle1();
        sink.text("Whitenoise Report");
        sink.sectionTitle1_();

        sink.paragraph();
        sink.text(
                "The Whitenoise Report is a static analysis of CCL code. It aims to find preventable, common issues that arise during development "
                        + "of CCL.");
        sink.link_();
        sink.paragraph_();

        try {
            final Subject subject = getSubject();
            TerminalProperties.setGlobalTerminalProperties(TerminalProperties.getNewBuilder()
                    .setOsPromptPattern(osPromptPattern != null && !osPromptPattern.isEmpty() ? osPromptPattern
                            : TerminalProperties.constructDefaultOsPromptPattern(hostAddress, environmentName,
                                    defaultOSPromptUsername))
                    .setExpectationTimeout(expectationTimeout).setSpecifyDebugCcl(false).build());
            final List<File> files = getFiles();
            if (doCompile) {
                getLog().info(
                        "Mojo has been configured to compile scripts prior to analysis; preparing to compile scripts.");
                doCompile(subject, files);
            } else {
                getLog().debug("Mojo has been configured to not compile code prior to analysis.");
            }

            final AnalysisEngine engine = new J4CclAnalysisEngine(productProvider);
            final Map<String, List<Violation>> violations = Subject.doAs(subject,
                    new PrivilegedAction<Map<String, List<Violation>>>() {
                        @Override
                        public Map<String, List<Violation>> run() {
                            @SuppressWarnings("synthetic-access")
                            final Map<String, Set<Violation>> rootViolations = engine.analyze(getProgramNames(files),
                                    getRules());
                            final Map<String, List<Violation>> listViolations = new HashMap<String, List<Violation>>(
                                    rootViolations.size());
                            for (final Entry<String, Set<Violation>> entry : rootViolations.entrySet()) {
                                listViolations.put(entry.getKey(), new ArrayList<Violation>(entry.getValue()));
                            }
                            return listViolations;
                        }
                    });

            if (filterFile != null) {
                final ViolationFilterEngine filterEngine = new ViolationFilterEngine(getExclusions());
                for (final Entry<String, List<Violation>> entry : violations.entrySet()) {
                    System.out.println(entry);
                    final List<Violation> list = entry.getValue();
                    Iterator<Violation> it = list.iterator();
                    while (it.hasNext()) {
                        Violation violation = it.next();
                        if (filterEngine.remove(entry.getKey(), violation)) {
                            it.remove();
                        }
                    }
                }

                // If any of the scripts were completely filtered, then remove the script from being mentioned at all
                final Set<String> scriptNames = new HashSet<String>(violations.keySet());
                for (final String scriptName : scriptNames) {
                    if (violations.get(scriptName).isEmpty()) {
                        violations.remove(scriptName);
                    }
                }
            }

            writeViolations(violations);

            for (final Entry<String, List<Violation>> entry : violations.entrySet()) {
                writeResultTable(sink, entry.getKey(), entry.getValue());
            }
        } finally {

            sink.paragraph();
            sink.horizontalRule();
            sink.text("To display the complete list of violations that were evaluated as a part of this analysis run ");
            sink.rawText(
                    "<a href=\"#\" onClick=\"document.getElementById('violationTable').style.display=''; return false;\">");
            sink.text("click here!");
            sink.link_();
            sink.paragraph_();

            writeViolationTable(sink, getRules());

            sink.body_();

            sink.flush();
            sink.close();
        }
    }

    private void writeViolations(final Map<String, List<Violation>> violations) {
        if (!outputRawData) {
            return;
        }
        File dataFile = org.apache.commons.io.FileUtils.getFile(dataDirectory, "violations.txt");
        try {
            StringBuilder sb = new StringBuilder();
            for (Entry<String, List<Violation>> entry : violations.entrySet()) {
                for (Violation violation : entry.getValue()) {
                    sb.append("<").append(entry.getKey()).append(",").append(violation.getViolationId().getIdentifier())
                            .append(",").append(violation.getViolationDescription()).append(",")
                            .append(violation.getLineNumber()).append(">").append("\n");
                }
            }
            org.apache.commons.io.FileUtils.write(dataFile, sb.toString(), Charset.forName("utf-8"));
        } catch (IOException e) {
            getLog().error(e.getMessage());
        }
    }

    private void writeResultTable(final Sink sink, final String programName, final List<Violation> violations) {
        sink.sectionTitle2();
        sink.text(programName);
        sink.sectionTitle2_();

        sink.table();
        sink.tableRows(null, false);
        sink.tableRow();
        sink.tableHeaderCell();
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text("Violation Description");
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text("Line");
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text("Violation ID");
        sink.tableHeaderCell_();
        sink.tableRow_();

        // Counter for each reported violation
        Integer lineCount = 0;

        // Sort the list of violations by line number, then violation type, then hash code
        final TreeSet<Violation> sortedViolations = new TreeSet<Violation>(new Comparator<Violation>() {
            @Override
            public int compare(final Violation o1, final Violation o2) {
                if (!o1.getLineNumber().equals(o2.getLineNumber())) {
                    return o1.getLineNumber() - o2.getLineNumber();
                }

                return o1.getViolationId().toString().equalsIgnoreCase(o2.getViolationId().toString())
                        ? o1.hashCode() - o2.hashCode()
                        : o1.getViolationId().toString().compareTo(o2.getViolationId().toString());
            }
        });

        sortedViolations.addAll(violations);

        for (final Violation violation : sortedViolations) {
            sink.tableRow();
            sink.tableCell();
            sink.text((++lineCount).toString());
            sink.tableCell_();
            sink.tableCell();
            sink.text(violation.getViolationDescription());
            sink.tableCell_();
            sink.tableCell();
            sink.text(violation.getLineNumber() == null ? "" : Integer.toString(violation.getLineNumber()));
            sink.tableCell_();
            sink.tableCell();
            sink.rawText("<a href=\"#\" title=\"" + violation.getViolationExplanation() + "\">"
                    + violation.getViolationId().getIdentifier() + "</a>");
            sink.tableCell_();
            sink.tableRow_();
        }

        sink.table_();
    }

    private void writeViolationTable(final Sink sink, final Collection<AnalysisRule> rules) {
        sink.rawText("<div id=\"violationTable\" style=\"display:none\">");
        sink.sectionTitle1();
        sink.text("Violations Analyzed");
        sink.sectionTitle1_();

        sink.table();
        sink.tableRows(null, true);
        sink.tableRow();
        sink.tableHeaderCell();
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text("Violation Id");
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text("Violation Description");
        sink.tableHeaderCell_();
        sink.tableHeaderCell();
        sink.text("Violation Detail");
        sink.tableHeaderCell_();
        sink.tableRow_();

        Integer violationCount = 0;
        for (final AnalysisRule rule : rules) {
            for (final Violation v : rule.getCheckedViolations()) {
                violationCount++;

                sink.tableRow();
                sink.tableCell();
                sink.text(violationCount.toString());
                sink.tableCell_();
                sink.tableCell();
                sink.text(v.getViolationId().getIdentifier());
                sink.tableCell_();
                sink.tableCell();
                sink.text(v.getViolationDescription());
                sink.tableCell_();
                sink.tableCell();
                sink.text(v.getViolationExplanation());
                sink.tableCell_();
                sink.tableRow_();
            }
        }

        sink.tableRows_();
        sink.table_();
        sink.rawText("</div>");
    }

    private Exclusions getExclusions() throws MavenReportException {
        try {
            final JAXBContext context = JAXBContext.newInstance(Exclusions.class.getPackage().getName(),
                    Exclusions.class.getClassLoader());
            final Unmarshaller unmarshaller = context.createUnmarshaller();
            @SuppressWarnings("unchecked")
            final JAXBElement<Exclusions> element = (JAXBElement<Exclusions>) unmarshaller.unmarshal(filterFile);
            return element.getValue();
        } catch (final Exception e) {
            getLog().error(e);
            throw new MavenReportException("Failed to parse for exclusions.", e);
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

    private void doCompile(final Subject subject, final Collection<File> files) {
        final CclExecutor executor = CclExecutor.getExecutor();
        executor.setTerminalProperties(TerminalProperties.getGlobalTerminalProperties());
        for (final File file : files) {
            executor.addScriptCompiler(file).withDebugModeEnabled(true).commit();
        }

        Subject.doAs(subject, new PrivilegedAction<Void>() {
            @Override
            public Void run() {
                executor.execute();
                return null;
            }
        });
    }

    private Collection<AnalysisRule> getRules() {
        return new AnalysisRuleProvider().getRules();
    }

    private Subject getSubject() throws MavenReportException {
        final Subject subject = new Subject();

        if (isEmpty(hostCredentialsId)) {
            if (isEmpty(username)) {
                throw new MavenReportException("A valid host username must be provided.");
            }
            defaultOSPromptUsername = username;
            subject.getPrincipals().add(new BackendNodePrincipal(username, hostAddress, environmentName));
            subject.getPrivateCredentials().add(new BackendNodePasswordCredential(password));
        } else {
            if (!isEmpty(username) || !isEmpty(password)) {
                getLog().warn(
                        "A host username or password was provided as well as a credentials ID; the ID will be used over the username and password.");
            }
            final Server server = settings.getServer(hostCredentialsId);
            if (server == null) {
                throw new MavenReportException("No backend <server /> found by the given ID: " + hostCredentialsId);
            }

            defaultOSPromptUsername = server.getUsername();
            subject.getPrincipals().add(new BackendNodePrincipal(server.getUsername(), hostAddress, environmentName));
            try {
                subject.getPrivateCredentials()
                        .add(new BackendNodePasswordCredential(securityDispatcher.decrypt(server.getPassword())));
            } catch (final SecDispatcherException e) {
                throw new MavenReportException("Failed to decrypt user password.", e);
            }
        }
        return subject;
    }

    private List<File> getFiles() throws MavenReportException {
        try {
            return FileUtils.getFiles(sourceDirectory, "*.prg", "", true);
        } catch (final IOException e) {
            throw new MavenReportException("Failed to retrieve list of PRG files.", e);
        }
    }

    private Set<String> getProgramNames(final Collection<File> files) {
        final TreeSet<String> programNames = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        for (final File file : files) {
            final String filename = file.getName();
            programNames.add(filename.substring(0, filename.lastIndexOf('.')));
        }
        return programNames;
    }

}
