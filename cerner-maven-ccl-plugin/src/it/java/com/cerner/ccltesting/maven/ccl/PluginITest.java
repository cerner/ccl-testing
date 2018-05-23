package com.cerner.ccltesting.maven.ccl;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.apache.maven.shared.invoker.PrintStreamHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.cerner.ccl.j4ccl.TerminalProperties;

/**
 * Integration tests for {@code cerner-maven-ccl-plugin}.
 * 
 * @author Joshua Hyde
 * 
 */
public class PluginITest {
    /**
     * A {@link Rule} used to obtain the current test name.
     */
    @Rule
    public TestName testName = new TestName();

    private Properties originalSystemProperties = null;
    private final List<String> compileGoal = Collections.singletonList("compile");
    private final List<String> validateGoal = Collections.singletonList("validate");
    private final List<String> testGoal = Collections.singletonList("test");

    /**
     * One time initialization.
     */
    @BeforeClass
    public static void setUpBeforeClass() {
        final ResourceBundle systemPropsBundle = ResourceBundle.getBundle("system");
        System.setProperty("maven.home", systemPropsBundle.getString("maven.home"));
    }

    /**
     * Pre-test initialization
     */
    @Before
    public void setUp() {
        originalSystemProperties = System.getProperties();
    }

    /**
     * post test cleanup.
     */
    @After
    public void tearDown() {
        if (originalSystemProperties != null) {
            System.setProperties(originalSystemProperties);
        }
    }

    /**
     * One time cleanup.
     */
    @AfterClass
    public static void tearDownAfterClass() {
    }

    /**
     * Test a build failure caused by an if with no endif compilation error using the
     * {@code compilation-error-build-failure} project.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testBuildWithCompilationErrorNoEndIf() throws Exception {
        final File logFile = getLogFile();
        final InvocationResult result = executeMaven("compilation-error-build-failure", compileGoal, logFile);
        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isGreaterThan(0);
        assertThat(containsText(logFile, "Failure to compile code: %CCL-E")).as("A compliation failure was expected.")
                .isTrue();
    }

    /**
     * Test a build failure caused by a partial record structure definition out of place using the
     * {@code incomplete-include-file} project.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testBuildWithCompilationErrorIncompleteInc() throws Exception {
        final File logFile = getLogFile();
        final InvocationResult result = executeMaven("incomplete-include-file", compileGoal, logFile);
        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isGreaterThan(0);
        assertThat(containsText(logFile, "Failure to compile code: %CCL-E")).as("A compliation failure was expected.")
                .isTrue();
    }

    /**
     * If the {@code enforcePredeclare} property is active when a test executes code that does not declare its
     * variables, then the test build should fail.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testBuildWithEnforcePredeclare() throws Exception {
        final File logFile = getLogFile();
        final InvocationRequest request = getInvocationRequest("test-with-enforce-predeclare", testGoal, logFile);
        final InvocationResult result1 = new DefaultInvoker().execute(request);
        request.getProperties().put("ccl-enableFullDebug", "true");
        assertThat(result1.getExecutionException()).isNull();
        assertThat(result1.getExitCode()).isGreaterThan(0);
        assertThat(containsText(logFile, "1 test(s) failed: [TESTENFORCEPREDECLARE]"))
                .as("An undeclared variable failure was expected.").isTrue();
        FileWriter fw = new FileWriter(logFile);
        fw.close();

        // setting enforcePredeclare true should have the same effect
        request.getProperties().put("ccl-enforcePredeclare", "true");
        final InvocationResult result2 = new DefaultInvoker().execute(request);
        assertThat(result2.getExecutionException()).isNull();
        assertThat(result2.getExitCode()).isGreaterThan(0);
        assertThat(containsText(logFile, "1 test(s) failed: [TESTENFORCEPREDECLARE]"))
                .as("An undeclared variable failure was expected.").isTrue();
        fw = new FileWriter(logFile);
        fw.close();

        // The same test should succeed if we opt out of predeclare
        request.getProperties().put("ccl-enforcePredeclare", "false");
        final InvocationResult result3 = new DefaultInvoker().execute(request);
        assertThat(result3.getExecutionException()).isNull();
        assertThat(result3.getExitCode()).isZero();
        assertThat(containsText(logFile, "1 test(s) failed: [TESTENFORCEPREDECLARE]"))
                .as("did not expect to have an undeclared message in the log file.").isFalse();
    }

    /**
     * If the {@code expectationTimeout} property is set extremely low then all operations will fail because of a
     * timeout and the log will show the specified timeout value being applied.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testBuildWithLowExpectationTimeout() throws Exception {
        final File logFile = getLogFile();
        final InvocationRequest request = getInvocationRequest("test-with-low-expectation-timeout", compileGoal,
                logFile);
        request.setDebug(true);
        request.getProperties().put("ccl-expectationTimeout", "3");
        final InvocationResult result2 = new DefaultInvoker().execute(request);
        assertThat(result2.getExecutionException()).isNull();
        assertThat(result2.getExitCode()).isGreaterThan(0);
        assertThat(containsText(logFile, "Setting default timeout to 3"))
                .as("exected to see the exectationTimeout set to 3.").isTrue();
        assertThat(containsText(logFile, "sending command (envset")).as("exected to see an envset command.").isTrue();
        assertThat(containsText(logFile, "The expectation result was TIMEOUT (-2) for command envset"))
                .as("exected a timeout for the envset command.").isTrue();
    }

    /**
     * If the {@code ccl-skipEnvset} property is set, then there should be no envset command issued.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSkipEnvset() throws Exception {
        final File logFile = getLogFile();
        final InvocationRequest request = getInvocationRequest("test-skip-envset", testGoal, logFile);
        request.setDebug(true);
        request.getProperties().put("ccl-skipEnvset", "true");
        final InvocationResult result = new DefaultInvoker().execute(request);
        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isZero();
        assertThat(containsText(logFile, "sending command (envset"))
                .as("expect logFile to not contain any envset commands.").isFalse();
    }

    /**
     * If the {@code ccl-specifyDebugCcl} property is false, then there should be no $cer_exe/cclora_dbg command issued.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSpecifyDebugCcl() throws Exception {
        final File logFile = getLogFile();
        final InvocationRequest request = getInvocationRequest("test-no-specify-debug-ccl", testGoal, logFile);
        request.setDebug(true);
        request.getProperties().put("ccl-specifyDebugCcl", "false");
        final InvocationResult result = new DefaultInvoker().execute(request);
        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isZero();
        assertThat(containsText(logFile, "sending command ($cer_exe/cclora_dbg)"))
                .as("expect logFile to not contain any launch cclora_dbg commands.").isFalse();
        assertThat(containsText(logFile, "sending command (ccl)"))
                .as("expect logFile to contain some launch ccl commands.").isTrue();
    }

    /**
     * Test a maven build failure occurs if a runtime error occurs by builing the {@code build-with-runtime-ccl-error}
     * project.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testBuildWithRuntimeCclError() throws Exception {
        final File logFile = getLogFile();
        final InvocationRequest request = getInvocationRequest("build-with-runtime-ccl-error", testGoal, logFile);
        final InvocationResult result = new DefaultInvoker().execute(request);

        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isGreaterThan(0);
        assertThat(containsText(logFile, "1 test(s) failed: [TESTMULTIPLY]")).as("Test failure expected.").isTrue();
    }

    /**
     * Test the enabling of full debug mode produces debug info.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testEnableFullDebug() throws Exception {
        final File logFile = getLogFile();

        final InvocationRequest request = getInvocationRequest("enable-full-debug", testGoal, logFile);
        final Properties properties = request.getProperties();
        properties.put("ccl-enableFullDebug", "true");

        final InvocationResult result = new DefaultInvoker().execute(request);
        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isZero();

        assertThat(containsText(logFile, "envset ")).as("expected envset command to be issued.").isTrue();

        assertThat(containsText(logFile, "[INFO]")).as("expected log to contain [DEBUG] entries.").isTrue();
        assertThat(containsText(logFile, "[INFO] *** Begin cclstartup script ***"))
                .as("expected output from start of CCL startup.").isTrue();
        assertThat(containsText(logFile, "[INFO] *** End cclstartup script ***"))
                .as("expected output from end of CCL startup.").isTrue();
        assertThat(containsText(logFile, "%Banner")).as("expected output of CCL Banner display.").isTrue();
    }

    /**
     * If the framework version is outside of the range specified in the project POM, then the framework validation
     * should fail.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testFrameworkValidationFailure() throws Exception {
        final File logFile = getLogFile();
        final InvocationRequest request = getInvocationRequest("framework-version-nonmatching", validateGoal, logFile);
        final InvocationResult result = new DefaultInvoker().execute(request);

        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isGreaterThan(0);
        assertThat(containsText(logFile, "Unexpected CCL testing framework version:"))
                .as("expected framework version validation error.").isTrue();
    }

    /**
     * If the framework version is outside of the range specified in the project POM <i>and</i> validation is skipped,
     * then the build should not fail.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testFrameworkValidationSkipped() throws Exception {
        final File logFile = getLogFile();
        final InvocationResult result = executeMaven("framework-version-nonmatching-skipped", validateGoal, logFile);
        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isZero();
    }

    /**
     * If the framework version is within the range specified in the POM, then the framework validation should pass.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testFrameworkValidationSuccess() throws Exception {
        final File logFile = getLogFile();
        final InvocationResult result = executeMaven("framework-version-matching", validateGoal, logFile);
        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isZero();
    }

    /**
     * The presence of domain credentials should be wholly optional; thus, their absence should be of no problem
     * (assuming no test code requires CCL authentication AND and an osPromptPattern is provided or the constructed
     * default value works okay with out the domain name).
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testNoDomainCredentials() throws Exception {
        final File logFile = getLogFile();
        final InvocationRequest request = getInvocationRequest("no-domain-credentials", testGoal, logFile);

        final Properties properties = request.getProperties();
        properties.remove("ccl-domainUsername");
        properties.remove("ccl-domainPassword");
        properties.remove("ccl-domain");
        properties.remove("ccl-frontendCredentialsId");

        final InvocationResult result = new DefaultInvoker().execute(request);
        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isZero();
    }

    /**
     * Test that a build will fail if no host username or credentialsId is provided.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testNoHostUsername() throws Exception {
        final File logFile = getLogFile();
        final InvocationRequest request = getInvocationRequest("no-host-credentials", compileGoal, logFile);

        final Properties properties = request.getProperties();
        properties.remove("ccl-hostUsername");
        properties.remove("ccl-hostCredentialsId");

        final InvocationResult result = new DefaultInvoker().execute(request);

        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isGreaterThan(0);
        assertThat(containsText(logFile, "A valid host username must be provided."))
                .as("expected missing host usernamee error.").isTrue();
    }

    /**
     * Test that a build will fail if no host password or credentialsId is provided.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testNoHostPassword() throws Exception {
        final File logFile = getLogFile();
        final InvocationRequest request = getInvocationRequest("no-host-credentials", compileGoal, logFile);

        final Properties properties = request.getProperties();
        properties.setProperty("ccl-hostUsername", "some-user-name");
        properties.remove("ccl-hostPassword");
        properties.remove("ccl-hostCredentialsId");

        final InvocationResult result = new DefaultInvoker().execute(request);

        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isGreaterThan(0);
        assertThat(containsText(logFile, "Password cannot be null.")).as("expected missing host password error.")
                .isTrue();
    }

    /**
     * Test a build failure when the provided frontend credentials ID does not correspond to a known server.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testNonexistentFrontendCredentialsId() throws Exception {
        final File logFile = getLogFile();
        final InvocationRequest request = getInvocationRequest("nonexistent-frontend-credentials-id", compileGoal,
                logFile);

        final Properties properties = request.getProperties();

        properties.put("ccl-frontendCredentialsId", "iWillNeverEverEverBeAnIdForADomainLogin");

        final InvocationResult result = new DefaultInvoker().execute(request);

        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isGreaterThan(0);
        assertThat(containsText(logFile, "No frontend <server /> found by the given ID: "))
                .as("expected a failure message for no server found.").isTrue();
    }

    /**
     * Test a build failure when the provided host credentials ID does not correspond to a known server.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testNonexistentHostCredentialsId() throws Exception {
        final File logFile = getLogFile();
        final InvocationRequest request = getInvocationRequest("nonexistent-host-credentials-id", compileGoal, logFile);

        final Properties properties = request.getProperties();
        properties.put("ccl-hostCredentialsId", "iWillNeverEverExist23847823923479");

        final InvocationResult result = new DefaultInvoker().execute(request);
        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isGreaterThan(0);
        assertThat(containsText(logFile, "No backend <server /> found by the given ID: "))
                .as("expected failure message for no server ID .").isTrue();
    }

    /**
     * Test the execution of a test under CBO.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSetCboOptimizerMode() throws Exception {
        final File logFile = getLogFile();
        final InvocationRequest request = getInvocationRequest("set-optimizer-mode-cbo", testGoal, logFile);

        final Properties properties = request.getProperties();
        properties.setProperty("optimizerMode", "CBO");

        final InvocationResult result = new DefaultInvoker().execute(request);
        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isZero();
    }

    /**
     * Test the execution of a test under RBO.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSetRboOptimizerMode() throws Exception {
        final File logFile = getLogFile();
        final InvocationRequest request = getInvocationRequest("set-optimizer-mode-rbo", testGoal, logFile);

        final Properties properties = request.getProperties();
        properties.setProperty("ccl-optimizerMode", "RBO");

        final InvocationResult result = new DefaultInvoker().execute(request);
        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isZero();
    }

    /**
     * Test that specifying a testCase only runs the specified test case.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSpecifyTestName() throws Exception {
        final File logFile = getLogFile();
        final InvocationRequest request = getInvocationRequest("specify-test", testGoal, logFile);

        final Properties properties = request.getProperties();
        properties.setProperty("ccl-testCase", "cclplugin_do_run");

        final InvocationResult result = new DefaultInvoker().execute(request);
        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isZero();

        // Verify that only test results were written out for the
        // "cclplugin_do_run" test
        final File testResultsDirectory = new File(getProjectDirectory("specify-test"), "target/test-results");
        final File[] childrenDirectory = testResultsDirectory.listFiles();
        assertThat(childrenDirectory).hasSize(2);
        for (int index = 0; index < 2; index++) {
            String name = childrenDirectory[index].getName();
            boolean isDirectory = childrenDirectory[index].isDirectory();            
            assertThat(name).isEqualTo(isDirectory ? "cclplugin_do_run" : "environment.xml");
        }
    }

    /**
     * Test a successful build using the {@code successful-build} project. Verifiies that cclora_dbg gets invoked rather
     * than ccl.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSuccessfulBuild() throws Exception {
        final File logFile = getLogFile();
        final InvocationRequest request = getInvocationRequest("successful-build", testGoal, logFile);
        request.setDebug(true);

        final InvocationResult result = new DefaultInvoker().execute(request);
        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isZero();

        assertThat(containsText(logFile, "sending command ($cer_exe/cclora_dbg)"))
                .as("expect logFile to contain some launch cclora_dbg commands.").isTrue();
        assertThat(containsText(logFile, "sending command (ccl)"))
                .as("expect logFile to not contain any launch ccl commands.").isFalse();
    }

    /**
     * Test a successful build using a server ID to provide the domain username and password.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSuccessfulBuildFrontendCredentialsId() throws Exception {
        final File logFile = getLogFile();
        final InvocationResult result = executeMaven("/successful-build-frontend-credentials-id", compileGoal, logFile);
        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isZero();
    }

    /**
     * Test a successful build using a server ID to provide the username and password used to connect to the backend.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSuccessfulBuildHostCredentialsId() throws Exception {
        final File logFile = getLogFile();
        final InvocationResult result = executeMaven("/successful-build-host-credentials-id", compileGoal, logFile);
        assertThat(result.getExecutionException()).isNull();
        assertThat(result.getExitCode()).isZero();
    }

    /**
     * Determine whether or not a file contains a given line of text.
     * 
     * @param file
     *            The file to be read.
     * @param text
     *            The text to be looked for.
     * @return {@code true} if the file contains the given text; {@code false} if not.
     * @throws IOException
     *             If any errors occur during the file read-in.
     */
    private boolean containsText(final File file, final String text) throws IOException {
        for (final String line : FileUtils.readLines(file, "UTF-8")) {
            if (line.contains(text)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a log file to which a Maven invocation can write its output.
     * 
     * @return A {@link File} representing a location to which the Maven invocation can write its output.
     */
    private File getLogFile() {
        final File logDirectory = new File("target/logs/" + getClass().getSimpleName());
        if (!logDirectory.exists())
            try {
                FileUtils.forceMkdir(logDirectory);
            } catch (final IOException e) {
                throw new RuntimeException("Failed to create log directory.", e);
            }

        return new File(logDirectory, testName.getMethodName() + ".log");
    }

    /**
     * Facade for building an InvocationRequest for a test maven project
     * 
     * @param projectId
     *            The id (directory branch) for the test project.
     * @param goals
     *            The maven goals to execute.
     * @param logFile
     *            The file to which the build output should be written.
     * @return An InvocationRequest for the specified test project.
     * @throws URISyntaxException
     *             Bad things might happen
     * @throws FileNotFoundException
     *             Bad things might happen
     */
    private InvocationRequest getInvocationRequest(String projectId, List<String> goals, File logFile)
            throws FileNotFoundException, URISyntaxException {
        return new DefaultInvocationRequest().setPomFile(getPom(projectId)).setProperties(getProperties())
                .setGoals(goals).setOutputHandler(new PrintStreamHandler(new PrintStream(logFile), true));
    }

    /**
     * Facade for executing a test maven project
     * 
     * @param projectId
     *            The id (directory branch) for the test project.
     * @param goals
     *            The maven goals to execute.
     * @param logFile
     *            The file to which the build output should be written.
     * @return The InvocationResult from executing the specified test project
     * @throws URISyntaxException
     *             Bad things might happen
     * @throws FileNotFoundException
     *             Bad things might happen
     * @throws MavenInvocationException
     *             Bad things might happen
     */
    private InvocationResult executeMaven(String projectId, List<String> goals, File logFile)
            throws FileNotFoundException, URISyntaxException, MavenInvocationException {
        InvocationRequest request = getInvocationRequest(projectId, goals, logFile);
        return new DefaultInvoker().execute(request);
    }

    /**
     * Get a project's POM. It is assumed that the file is located beneath {@code /test-projects} on the classpath.
     * 
     * @param path
     *            The path, relative to {@code /test-projects}, at which the file can be located.
     * @return A {@link File} reference representing the desired POM file.
     * @throws FileNotFoundException
     *             If the given file cannot be found on the classpath.
     * @throws URISyntaxException
     *             If converting the {@link URL} representing the file on the classpath cannot be converted into a
     *             {@link URI}.
     */
    private File getPom(final String path) throws FileNotFoundException, URISyntaxException {
        final URL resourceUrl = getClass().getResource("/test-projects/" + path);
        if (resourceUrl == null)
            throw new FileNotFoundException("Resource not found: " + path);
        return FileUtils.toFile(resourceUrl);
    }

    /**
     * Get a project's project directory.
     * 
     * @param projectName
     *            The name of the project for which the project is to be retrieved.
     * @return A {@link File} representing the given project's project directory.
     * @throws FileNotFoundException
     *             If the given project's project directory cannot be found.
     */
    private File getProjectDirectory(final String projectName) throws FileNotFoundException {
        final URL projectUrl = getClass().getResource("/test-projects/" + projectName);
        if (projectUrl == null)
            throw new FileNotFoundException("Project not found: " + projectName);
        return FileUtils.toFile(projectUrl);
    }

    /**
     * Build the configured properties for CCL credentials.
     * 
     * @return A {@link Properties} of the passed-in system credentials.
     */
    private Properties getProperties() {
        final Properties mavenProperties = new Properties();
        final Properties systemProperties = System.getProperties();
        for (final Entry<Object, Object> systemProperty : systemProperties.entrySet())
            if (systemProperty.getKey() instanceof String && ((String) systemProperty.getKey()).startsWith("ccl-"))
                mavenProperties.put(systemProperty.getKey(), systemProperty.getValue());

        return mavenProperties;
    }
}
