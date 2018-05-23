package com.cerner.ccltesting.maven.ccl.mojo;

import static org.codehaus.plexus.util.StringUtils.isEmpty;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import javax.security.auth.Subject;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.settings.Server;
import org.apache.maven.settings.Settings;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcher;
import org.sonatype.plexus.components.sec.dispatcher.SecDispatcherException;

import com.cerner.ccl.j4ccl.CclExecutor;
import com.cerner.ccl.j4ccl.TerminalProperties;
import com.cerner.ccl.j4ccl.enums.OutputType;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePasswordCredential;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePrincipal;
import com.cerner.ccl.j4ccl.impl.jaas.MillenniumDomainPasswordCredential;
import com.cerner.ccl.j4ccl.impl.jaas.MillenniumDomainPrincipal;
import com.cerner.ccltesting.maven.ccl.util.CclLogFileOutputStream;
import com.cerner.ccltesting.maven.ccl.util.DelegatingOutputStream;
import com.cerner.ccltesting.maven.ccl.util.LogOutputStreamProxy;
import com.cerner.ccltesting.maven.ccl.util.LogOutputStreamProxy.LogProxy;

/**
 * Base properties for any CCL-related mojo.
 *
 * @author Mark Cummings
 * @author Joshua Hyde
 */
public abstract class BaseCclMojo extends AbstractMojo {
    /**
     * A regular expression pattern that filters down to CCL .PRG files
     */
    protected static final String CCL_SCRIPTS = ".*\\.prg";
    /**
     * A regular expression pattern that filters down to CCL .INC files
     */
    protected static final String CCL_INCLUDES = ".*\\.inc";
    /**
     * A regular expression pattern that filters down to CCL .SUB files
     */
    protected static final String CCL_SUBS = ".*\\.sub";

    private static final Pattern CCL_INCLUDES_PATTERN = Pattern.compile(CCL_INCLUDES.toUpperCase(Locale.getDefault()));
    private static final Pattern CCL_SCRIPTS_PATTERN = Pattern.compile(CCL_SCRIPTS.toUpperCase(Locale.getDefault()));
    private static final Pattern CCL_SUBS_PATTERN = Pattern.compile(CCL_SUBS.toUpperCase(Locale.getDefault()));

    /**
     * The destination of the CCL log file output. A suggested value for this could be
     * ${project.build.directory}/ccl.log, which would place the log file in your target/ directory (and would be
     * cleaned with any invocation of the "clean" lifecycle).
     *
     * @since 1.0.1
     */
    @Parameter(property = "ccl-logFile")
    protected File logFile;

    /**
     * The environment in which source files should be uploaded, compiled, and tested.
     *
     */
    @Parameter(property = "ccl-environment", required = true)
    protected String environment;

    /**
     * The DNS name or IP address of the remote server on which the goals of this plugin will be executed.
     *
     */
    @Parameter(property = "ccl-host", required = true)
    protected String host;

    /**
     * The ID of a {@code &lt;server /&gt;} tag within your settings.xml that provides the username and password used to
     * connect to the remote server. If present, this will override the usage of {@link #hostUsername} and
     * {@link #hostPassword}.
     *
     * @since 1.1
     * @see #hostPassword
     * @see #hostUsername
     */
    @Parameter(property = "ccl-hostCredentialsId")
    protected String hostCredentialsId;

    /**
     * The username for contacting the host.
     *
     * @see #hostCredentialsId
     */
    @Parameter(property = "ccl-hostUsername")
    protected String hostUsername;

    // used to construct the default OS prompt.
    private String defaultOSPromptUsername;

    /**
     * The password for contacting the host.
     *
     * @see #hostCredentialsId
     */
    @Parameter(property = "ccl-hostPassword")
    protected String hostPassword;

    /**
     * The optional Millennium domain to authenticate against when running unit tests. <br>
     * If this value is present, then a valid {@link #frontendCredentialsId} value or a valid combination of
     * {@link #domainUsername} and {@link #domainPassword} must be present.
     *
     */
    @Parameter(property = "ccl-domain")
    protected String domain;

    /**
     * The username for authenticating against a Millennium domain.
     *
     */
    @Parameter(property = "ccl-domainUsername")
    protected String domainUsername;

    /**
     * The password for authenticating against a Millennium domain.
     *
     */
    @Parameter(property = "ccl-domainPassword")
    protected String domainPassword;

    /**
     * A {@code boolean} indicator indicating whether or not full debug output should be enabled. When enabled (as
     * opposed to the normal debug output enabled by Maven's {@code -X} flag), all output from interactions with the
     * remote server will be dumped to the screen.
     *
     * @since 1.1
     */
    @Parameter(property = "ccl-enableFullDebug")
    protected boolean enableFullDebug;

    /**
     * The server ID corresponding to the username and password that can be used to log into a Millennium domain. If
     * present, this will override any values provided in {@link #domainUsername} or {@link #domainPassword}.
     *
     * @since 1.1
     */
    @Parameter(property = "ccl-frontendCredentialsId")
    protected String frontendCredentialsId;

    /**
     * A {@link SecDispatcher} used to decrypt encrypted passwords supplied via a {@code &lt;server /&gt;} setting in
     * the settings.xml.
     *
     * @see #frontendCredentialsId
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
     * When set to {@code true}, causes this plugin's mojos to skip processing.
     *
     * This option can be overridden for each goal by setting the configuration option specific to that goal's mojo.
     *
     * @since 1.0-alpha-3
     */
    @Parameter(property = "ccl-skipProcessing", defaultValue = "false")
    protected boolean skipProcessing;

    /**
     * When set to {@code true}, causes this plugin to not issue an envset to the environment in each session. This is
     * an unnecessary performance price if the default environment for the supplied credentials is the target
     * environment.
     *
     * @since 3.0
     */
    @Parameter(property = "ccl-skipEnvset", defaultValue = "false")
    protected boolean skipEnvset;

    /**
     * A regular expression that will match the back end operating system prompt for the configured user. This is used
     * to recognize when the back end operating system has finished processing and is ready for another command The
     * following value is constructed by default: "$ccl-domainUsername:ccl-environment@$ccl-host:[^@gt;]*@gt;\s*"
     *
     * @since 3.0
     */
    @Parameter(property = "ccl-osPromptPattern", defaultValue = "")
    protected String osPromptPattern;

    /**
     * A regular expression that will match the CCL operating system prompt for the configured user. This is used to
     * recognize when the CCL operating system has finished processing and is ready for another command The following
     * value is constructed by default: "\n\s*\d+\)\s*$"
     *
     * @since 3.0
     */
    @Parameter(property = "ccl-cclPromptPattern", defaultValue = "")
    protected String cclPromptPattern;

    /**
     * A regular expression that will match the ending of the login prompt displayed when CCL is first launched. This is
     * used to recognize when the CCL operating system has finished displaying the login prompt and is ready to receive
     * the login credentials or have the login prompt dismissed. The following value is constructed by default: "\(Hit
     * PF3 or RETURN to skip security login; this will disable Uar functions\)"
     *
     * @since 3.0
     */
    @Parameter(property = "ccl-cclLoginPromptPattern", defaultValue = "")
    protected String cclLoginPromptPattern;

    /**
     * A regular expression that will match the ending of the prompt displayed when CCL has successfully authenticated
     * the entered credentials. This is used to recognize when the CCL operating system has finished authenticating the
     * credentials and is ready to receive commands. The following value is constructed by default: "Enter Y to
     * continue"
     *
     * @since 3.0
     */
    @Parameter(property = "ccl-cclLoginSuccessPromptPattern", defaultValue = "")
    protected String cclLoginSuccessPromptPattern;

    /**
     * A list of regular expression that will match the prompts displayed when CCL fails to authenticate the entered
     * credentials. This is used to recognize when the CCL operating system has finished authenticating the credentials
     * and is ready to receive commands (in an unauthenticated session). The following values are constructed by
     * default: "V500 SECURITY LOGIN FAILURE", "V500 SECURITY LOGIN WARNING", "Retry \(Y/N\)", "Repeat New Password:"
     *
     * @since 3.0
     */
    @Parameter(property = "ccl-cclLoginFailurePromptPatterns", defaultValue = "")
    protected List<String> cclLoginFailurePromptPatterns;

    /**
     * The timeout in milliseconds when waiting for a back-end command to complete. Does not apply to the execution of
     * CCL commands using "go". Those will always be given unlimited time to execute.
     *
     * @since 3.0
     */
    @Parameter(property = "ccl-expectationTimeout", defaultValue = "20000")
    protected long expectationTimeout;

    /**
     * When set to {@code true}, causes this plugin to use cclora_dbg rather than cclora as the ccl executable.
     *
     * @since 3.0
     */
    @Parameter(property = "ccl-specifyDebugCcl", defaultValue = "true")
    protected boolean specifyDebugCcl;

    /**
     * Add the credentials necessary to authenticate against the backend node.
     *
     * @param subject
     *            The {@link Subject} to which the credentials are to be added.
     * @throws MojoExecutionException
     *             If any errors occur while adding the credentials.
     * @throws NullPointerException
     *             If the given subject is {@code null}.
     */
    protected void addBackendInformation(final Subject subject) throws MojoExecutionException {
        if (subject == null)
            throw new NullPointerException("Subject cannot be null.");

        if (isEmpty(hostCredentialsId)) {
            if (isEmpty(hostUsername)) {
                throw new MojoExecutionException("A valid host username must be provided.");
            }
            defaultOSPromptUsername = hostUsername;
            subject.getPrincipals().add(new BackendNodePrincipal(hostUsername, host, environment));
            subject.getPrivateCredentials().add(new BackendNodePasswordCredential(hostPassword));
        } else {
            if (!isEmpty(hostUsername) || !isEmpty(hostPassword)) {
                getLog().warn(
                        "A host username or password was provided as well as a credentials ID; the ID will be used over the username and password.");
            }
            final Server server = settings.getServer(hostCredentialsId);
            if (server == null) {
                throw new MojoExecutionException("No backend <server /> found by the given ID: " + hostCredentialsId);
            }
            defaultOSPromptUsername = server.getUsername();
            subject.getPrincipals().add(new BackendNodePrincipal(server.getUsername(), host, environment));
            try {
                subject.getPrivateCredentials()
                        .add(new BackendNodePasswordCredential(securityDispatcher.decrypt(server.getPassword())));
            } catch (final SecDispatcherException e) {
                throw new MojoExecutionException("Failed to decrypt user password.", e);
            }
        }
    }

    /**
     * Add Millennium domain login information, if available.
     *
     * @param subject
     *            The {@link Subject} to which the Millennium domain credentials may be added.
     * @throws MojoExecutionException
     *             If any errors occur during the creation of the credentials.
     * @throws NullPointerException
     *             If the given subject is {@code null}.
     */
    protected void addDomainLoginInformation(final Subject subject) throws MojoExecutionException {
        if (subject == null)
            throw new NullPointerException("Subject cannot be null.");

        if (isEmpty(domain))
            return;

        if (!isEmpty(frontendCredentialsId)) {
            if (!isEmpty(domainUsername) || !isEmpty(domainPassword))
                getLog().warn(
                        "A frontend username or password was provided as well as a credentials ID; the ID will be used over the username and password.");

            final Server server = settings.getServer(frontendCredentialsId);
            if (server == null)
                throw new MojoExecutionException(
                        "No frontend <server /> found by the given ID: " + frontendCredentialsId);

            try {
                subject.getPrincipals().add(new MillenniumDomainPrincipal(server.getUsername(), domain));
                subject.getPrivateCredentials()
                        .add(new MillenniumDomainPasswordCredential(securityDispatcher.decrypt(server.getPassword())));
            } catch (final SecDispatcherException e) {
                throw new MojoExecutionException("Failed to decrypt Millennium domain password.", e);
            }
        } else {
            if (isEmpty(domainUsername))
                throw new MojoExecutionException(
                        "A valid frontend username must be provided when domain is specified.");

            subject.getPrincipals().add(new MillenniumDomainPrincipal(domainUsername, domain));
            subject.getPrivateCredentials().add(new MillenniumDomainPasswordCredential(domainPassword));
        }

    }

    /**
     * Returns a new instance of {@link CclExecutor} configured according to the environment, host, username, password,
     * and plugin parameters.
     *
     * @return a new {@link CclExecutor}
     * @throws MojoExecutionException
     *             if the CclExecutor can not be constructed
     * @throws MojoFailureException
     *             If there exist any issues with the mojo configuration.
     */
    protected CclExecutor createCclExecutor() throws MojoExecutionException, MojoFailureException {
        final CclExecutor executor = CclExecutor.getExecutor();
        final OutputStream outputStream = getLoggingOutputStream();
        if (outputStream != null) {
            executor.setOutputStream(outputStream, enableFullDebug ? OutputType.FULL_DEBUG : OutputType.CCL_SESSION);
        }
        executor.setTerminalProperties(TerminalProperties.getGlobalTerminalProperties());

        return executor;
    }

    /**
     * Initializes the singleton TerminalProperties object using the plugin's configuration settings if it has not
     * already been initialized.
     */
    protected void setGlobalTerminalProperties() {
        String logfileLocation = "";
        if (logFile != null) {
            try {
                logfileLocation = logFile.getCanonicalPath();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        TerminalProperties.setGlobalTerminalProperties(TerminalProperties.getNewBuilder().setSkipEnvset(skipEnvset)
                .setOsPromptPattern(osPromptPattern != null && !osPromptPattern.isEmpty() ? osPromptPattern
                        : TerminalProperties.constructDefaultOsPromptPattern(host, environment,
                                defaultOSPromptUsername))
                .setCclPromptPattern(cclPromptPattern).setCclLoginPromptPattern(cclLoginPromptPattern)
                .setCclLoginSuccessPromptPattern(cclLoginSuccessPromptPattern)
                .setCclLoginFailurePromptPatterns(cclLoginFailurePromptPatterns)
                .setExpectationTimeout(expectationTimeout).setLogfileLocation(logfileLocation)
                .setSpecifyDebugCcl(specifyDebugCcl).build());
    }

    /**
     * Get a list of files for a given directory.
     *
     * @param directory
     *            A {@link File} object representing the directory in which the resources exist.
     * @param regex
     *            The regular expression pattern to be used to filter down to only files wanted to be returned.
     * @return An array of {@link File} objects representing the list of resource files found in the resources
     *         directory.
     */
    final protected List<File> getFiles(final File directory, final String regex) {
        if (directory.exists() && directory.isDirectory()) {
            final List<File> files = new ArrayList<File>();

            final Pattern pattern = getPattern(regex);
            String[] directoryFiles = directory.list();
            if (directoryFiles != null) {
                for (final String file : directoryFiles) {
                    if (pattern.matcher(file.toUpperCase(Locale.getDefault())).matches())
                        files.add(new File(directory, file));
                }
            }
            return files;
        }

        return Collections.<File> emptyList();
    }

    /**
     * Get all CCL include files from the given directory.
     *
     * @param directory
     *            A {@link File} object representing the directory whose include files are to be fetched.
     * @return A {@link List} of {@link File} objects.
     * @throws MojoExecutionException
     *             If any errors that would interrupt the build occur.
     * @throws MojoFailureException
     *             If any build failures occur.
     */
    protected List<File> getIncludeFiles(final File directory) throws MojoExecutionException, MojoFailureException {
        final List<File> files = new ArrayList<File>(getFiles(directory, CCL_INCLUDES));
        files.addAll(getFiles(directory, CCL_SUBS));
        return files;
    }

    /**
     * Create a subject that can be used to communicate with the backend node.
     *
     * @return A {@link Subject} that can be used to communicate with the backend node.
     * @throws MojoExecutionException
     *             If any errors occur while creating the subject.
     */
    protected Subject getSubject() throws MojoExecutionException {
        final Subject subject = new Subject();
        addBackendInformation(subject);
        setGlobalTerminalProperties();
        addDomainLoginInformation(subject);
        return subject;
    }

    /**
     * Determine whether or not debug mode is enabled for this plugin.
     *
     * @return {@code true} if debugging is enabled; {@code false} if it is not.
     */
    final protected boolean isDebugging() {
        return getLog().isDebugEnabled();
    }

    /**
     * Get the stream (if any) used for writing out debug output.
     *
     * @return {@code null} if this mojo is not configured to write debug information; otherwise, an
     *         {@link OutputStream} that can be used to write out debug information.
     */
    final protected OutputStream getLoggingOutputStream() {
        if (logFile != null || isDebugging() || enableFullDebug) {
            final DelegatingOutputStream delegate = new DelegatingOutputStream();
            if (logFile != null) {
                try {
                    FileUtils.forceMkdirParent(logFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                delegate.addStream(new CclLogFileOutputStream(logFile));
            }
            if (enableFullDebug) {
                delegate.addStream(new LogOutputStreamProxy(new InfoLogProxy(getLog())));
            } else if (isDebugging()) {
                delegate.addStream(new LogOutputStreamProxy(new DebugLogProxy(getLog())));
            }
            return delegate;
        }

        return null;
    }

    /**
     * Get a regular expression object matching the given regular expression.
     *
     * @param regex
     *            The regular expression for which a pattern object will be created.
     * @return A {@link Pattern} object representing the given regular expression.
     */
    private Pattern getPattern(final String regex) {
        if (CCL_SCRIPTS.equals(regex))
            return CCL_SCRIPTS_PATTERN;
        else if (CCL_INCLUDES.equals(regex))
            return CCL_INCLUDES_PATTERN;
        else if (CCL_SUBS.equals(regex))
            return CCL_SUBS_PATTERN;
        else
            return Pattern.compile(regex);
    }

    /**
     * An implementation of {@link LogProxy} that writes everything out to {@link Log#debug(CharSequence)}.
     *
     * @author Joshua Hyde
     *
     */
    private static class DebugLogProxy implements LogProxy {
        private final Log log;

        /**
         * Create a log proxy.
         *
         * @param log
         *            The {@link Log} to whom all debug output will be written.
         */
        public DebugLogProxy(final Log log) {
            this.log = log;
        }

        /**
         * {@inheritDoc}
         */
        public Log getLog() {
            return log;
        }

        /**
         * {@inheritDoc}
         */
        public void log(final String text) {
            log.debug(text);
        }
    }

    /**
     * An implementation of {@link LogProxy} that writes everything out to {@link Log#info(CharSequence)}.
     *
     * @author Joshua Hyde
     *
     */
    private static class InfoLogProxy implements LogProxy {
        private final Log log;

        /**
         * Create a log proxy.
         *
         * @param log
         *            The {@link Log} to whom all debug output will be written.
         */
        public InfoLogProxy(final Log log) {
            this.log = log;
        }

        /**
         * {@inheritDoc}
         */
        public Log getLog() {
            return log;
        }

        /**
         * {@inheritDoc}
         */
        public void log(final String text) {
            log.info(text);
        }
    }
}
