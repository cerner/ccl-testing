package com.cerner.ccl.testing.maven.ccl.mojo;

import java.io.File;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.Subject;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.FileUtils;

import com.cerner.ccl.j4ccl.CclExecutor;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.testing.maven.ccl.data.UnitTest;
import com.cerner.ccl.testing.maven.ccl.exception.TestFailureException;
import com.cerner.ccl.testing.maven.ccl.util.ProgramListingWriter;
import com.cerner.ccl.testing.maven.ccl.util.TestResultScanner;
import com.cerner.ccl.testing.maven.ccl.util.TestResultWriter;
import com.cerner.ccl.testing.maven.ccl.util.factory.CclUnitRecordFactory;
import com.cerner.ccl.testing.maven.ccl.util.factory.ProgramListingWriterFactory;
import com.cerner.ccl.testing.maven.ccl.util.factory.TestResultScannerFactory;
import com.cerner.ccl.testing.maven.ccl.util.factory.TestResultWriterFactory;

/**
 * A mojo to execute tests.
 *
 * @author Mark Cummings
 * @author Joshua Hyde
 *
 */
@Mojo(name = "test")
public class TestMojo extends BaseCclMojo {
    private static final TestResultWriterFactory DEFAULT_RESULT_WRITER_FACTORY = new TestResultWriterFactory();
    private static final ProgramListingWriterFactory DEFAULT_LISTING_WRITER_FACTORY = new ProgramListingWriterFactory();
    private static final TestResultScannerFactory DEFAULT_RESULT_SCANNER_FACTORY = new TestResultScannerFactory();

    /**
     * Entry point for the CCLUnit framework
     */
    private static final String CCL_UNIT_PROGRAM = "cclut_execute_test_case";

    /**
     * The location within the project directory where the main CCL program file(s) can be found
     *
     */
    @Parameter(property = "ccl-sourceDirectory", defaultValue = "src/main/ccl")
    protected File cclSourceDirectory;

    /**
     * The location within the project directory where the test CCL program file(s) can be found
     *
     */
    @Parameter(property = "ccl-testSourceDirectory", defaultValue = "src/test/ccl")
    protected File cclTestSourceDirectory;

    /**
     * The location to which the output of the test data will be stored
     *
     */
    @Parameter(property = "ccl-outputDirectory", defaultValue = "${project.build.directory}")
    protected File outputDirectory;

    /**
     * When set to {@code true}, causes this mojo to skip processing.
     *
     * @since 1.0-alpha-3
     */
    @Parameter(property = "ccl-skipTest")
    protected boolean skipTest;

    /**
     * This sets the Oracle optimizer mode to be set while executing tests. If left unset, the default optimizer mode
     * within the environment will be used.
     * <p>
     * Be aware that this feature is only supported in the CCL Testing Framework from version 1.1.
     *
     * @since 1.3
     */
    @Parameter(property = "ccl-optimizerMode")
    protected String optimizerMode;

    /**
     * Optionally, a regular expressions can be used to specify which test cases will be compiled and executed and which
     * tests within them will be executed. The format is {@code <test_case_name_pattern>#<test name pattern>} and the
     * hash mark is optional if not specifying a test name pattern. The matching is case-insensitive. As examples,
     * specifying {@code unit_test_start_#.*get} will run all tests matching "get.*" within all test case files matching
     * "unit_test_start.*" and specifying {@code unit_test_start_batch} will execute all tests within all test case
     * files matching "unit_test_start_batch".
     *
     * <p>
     * If a value is specified without specifying a pattern for either the test case name or test name then ".*" will
     * used for the missing patterns. Specifying "#" will run all tests in all test case files, for example.
     *
     * <p>
     * The pattern for the test case should only be for the the test case name without the .inc file extension.
     *
     * <p>
     * Be aware that the ability to specify the test subroutine name is not available in versions of the CCL Testing
     * Framework prior to 1.4. Prior to version 1.3 only test name matching was supported and CCL patstring matching was
     * used. Starting with version 2.0 true regular expression matching is used and matching is available for both the
     * test case name and the test name.
     *
     * @since 1.3, 2.0
     */
    @Parameter(property = "ccl-testCase")
    protected String testCase;

    /**
     * Configure the CCL Testing Framework to require that all variables used by scripts within the test declare be
     * declared. Be aware that this only works with the CCL Testing Framework v1.5 and above.
     *
     * @since 3.0
     */
    @Parameter(property = "ccl-enforcePredeclare", defaultValue = "true")
    protected boolean enforcePredeclare;

    /**
     * Configure the CCL Testing Framework to flag deprecated constructs with the indicated level: "E" (error), "W"
     * (warning), "L" (log), "I" (information), "D" (debug). The default value is "E".
     *
     * Be aware that this only works with the CCL Testing Framework v1.5 and above.
     *
     * @since 3.0
     */
    @Parameter(property = "ccl-deprecatedFlag", defaultValue = "E")
    protected String deprecatedFlag;

    /**
     * Configure the plugin to fail the maven build if any of the CCL unit tests fail.
     *
     * @since 3.0
     */
    @Parameter(property = "ccl-failOnTestFailures", defaultValue = "true")
    protected boolean failOnTestFailures;

    private final TestResultWriterFactory resultWriterFactory;
    private final ProgramListingWriterFactory listingWriterFactory;
    private final TestResultScannerFactory resultScannerFactory;

    /**
     * Create a mojo to execute tests.
     */
    public TestMojo() {
        super();
        this.resultWriterFactory = DEFAULT_RESULT_WRITER_FACTORY;
        this.listingWriterFactory = DEFAULT_LISTING_WRITER_FACTORY;
        this.resultScannerFactory = DEFAULT_RESULT_SCANNER_FACTORY;
    }

    /**
     * Create a mojo to execute tests.
     * <p>
     * This is purposefully kept at package-private visibility to expose it to tests but keep it off of the published
     * API.
     *
     * @param resultWriterFactory
     *            A {@link TestResultWriterFactory} object.
     * @param listingWriterFactory
     *            A {@link ProgramListingWriterFactory} object.
     * @param resultScannerFactory
     *            A {@link TestResultScannerFactory} object.
     */
    TestMojo(final TestResultWriterFactory resultWriterFactory, final ProgramListingWriterFactory listingWriterFactory,
            final TestResultScannerFactory resultScannerFactory) {
        this.resultWriterFactory = resultWriterFactory;
        this.listingWriterFactory = listingWriterFactory;
        this.resultScannerFactory = resultScannerFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (skipTest) {
            getLog().info("Skipping test goal");
            return;
        }

        final List<File> testSources = getFiles(cclTestSourceDirectory, CCL_INCLUDES);

        if (testSources.isEmpty()) {
            getLog().info("No tests to execute");
        } else {
            executeTests(getFiles(cclSourceDirectory, CCL_SCRIPTS), testSources, outputDirectory);
        }
    }

    /**
     * Execute tests.
     *
     * @param sources
     *            A {@link List} of {@link File} objects representing the source files for which the tests are to be
     *            executed.
     * @param testSources
     *            A {@link List} of {@link File} objects representing the test include files to be used to test the
     *            source programs.
     * @param outputDirectory
     *            The directory to which the test result XML should be written.
     * @throws MojoExecutionException
     *             If any errors occur during the execution of tests.
     * @throws MojoFailureException
     *             If there exist any issues with the mojo configuration.
     */
    private void executeTests(final List<File> sources, final List<File> testSources, final File outputDirectory)
            throws MojoExecutionException, MojoFailureException {
        final ProgramListingWriter listingWriter = listingWriterFactory.create(outputDirectory);
        final TestResultScanner resultScanner = resultScannerFactory.create();
        final List<String> scriptNames = new ArrayList<String>(sources.size());
        final Collection<UnitTest> failedTests = new ArrayList<UnitTest>();

        /*
         * If the test name is set, remove all but the specified test
         */
        String testSubroutineName = null;
        if (testCase != null) {
            String testFilename = null;
            final int hashPos = testCase.indexOf('#');
            if (hashPos >= 0) {
                testSubroutineName = testCase.substring(hashPos + 1);
                testFilename = testCase.substring(0, hashPos);
            } else {
                testFilename = testCase;
            }

            Iterator<File> it = testSources.iterator();
            while (it.hasNext()) {
                File testSource = it.next();
                if (!testSource.getName().replace(".inc", "").matches("(?i)" + testFilename)) {
                    it.remove();
                }
            }
        }

        /*
         * Build the list of source script names by removing the source file extensions
         */
        for (final File sourceFile : sources) {
            scriptNames.add(sources.indexOf(sourceFile), FileUtils.removeExtension(sourceFile.getName()));
        }

        // Run unit tests for each test include file
        boolean firstTest = true;
        for (final File testFile : testSources) {
            final Record request = CclUnitRecordFactory.createRequest(scriptNames, testFile.getName(),
                    testSources.indexOf(testFile) == 0, optimizerMode, enforcePredeclare, deprecatedFlag,
                    testSubroutineName);
            final Record reply = CclUnitRecordFactory.createReply();

            if (isDebugging()) {
                getLog().debug("Executing " + CCL_UNIT_PROGRAM + " on " + host + " (" + environment + ")");
            }

            Subject subject = getSubject();
            final CclExecutor cclExecutor = createCclExecutor();
            cclExecutor.addScriptExecution(CCL_UNIT_PROGRAM).withReplace("cclutRequest", request)
                    .withReplace("cclutReply", reply).withAuthentication(true).commit();

            Subject.doAs(subject, new PrivilegedAction<Void>() {
                @Override
                public Void run() {
                    getLog().info("");
                    getLog().info("executing test case " + testFile.getName());
                    cclExecutor.execute();
                    return null;
                }
            });

            // Check for failure
            if ("F".equalsIgnoreCase(reply.getRecord("status_data").getChar("status"))) {
                throw new MojoFailureException("CCL Testing Framework has reported a failure: "
                        + reply.getRecord("status_data").getList("subeventstatus").get(0).getVC("TargetObjectValue"));
            }

            try {
                writeTestOutput(outputDirectory, testFile, reply);
            } catch (final IOException e) {
                throw new MojoExecutionException("Failed to write test result data for " + testFile.getName() + ".", e);
            }

            if (firstTest) {
                // Write program listings to target directory
                for (final Record program : reply.getDynamicList("programs")) {
                    try {
                        getLog().info("writing listing for " + program.getVC("programName"));
                        listingWriter.writeListing(program.getVC("programName"), program.getVC("listingXML"));
                    } catch (final IOException e) {
                        throw new MojoExecutionException(
                                "Failed to write listing data for " + program.getVC("programName"), e);
                    }
                }
                firstTest = false;
            }

            // Check for test failures
            failedTests.addAll(resultScanner.scanForFailures(reply.getVC("testINCResultsXML")));
        }

        if (!failedTests.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            Iterator<UnitTest> it = failedTests.iterator();
            while (it.hasNext()) {
                UnitTest ut = it.next();
                sb.append(ut.getName()).append(", ");
            }
            getLog().error("Test failures detected: " + sb.substring(0, sb.length() - 2));
            if (failOnTestFailures) {
                throw new TestFailureException(failedTests);
            }
        }
    }

    /**
     * Writes the test output to the local disk.
     *
     * @param outputDirectory
     *            The directory under which all of the data should be written. This should be
     *            ${project.build.outputDirectory}.
     * @param testFile
     *            A {@link File} object representing the test file for which test data is to be written out.
     * @param reply
     *            A {@link Record} object containing the reply data from the CCL unit testing framework.
     * @throws IOException
     *             If any errors occur during the test run.
     */
    private void writeTestOutput(final File outputDirectory, final File testFile, final Record reply)
            throws IOException {
        final String nonSuffixedName = testFile.getName().substring(0, testFile.getName().lastIndexOf('.'));
        final TestResultWriter writer = resultWriterFactory.create(nonSuffixedName, outputDirectory);

        writer.writeEnvironmentXml(reply.getVC("environmentXml"));
        getLog().info("writing listing for " + testFile.getName());
        writer.writeTestListing(reply.getVC("testINCListingXML"));
        getLog().info("writing results for " + testFile.getName());
        writer.writeTestResults(reply.getVC("testINCResultsXML"));
        getLog().info("writing coverage for " + testFile.getName());
        writer.writeTestCoverage(reply.getVC("testINCCoverageXML"));

        for (final Record record : reply.getDynamicList("programs")) {
            getLog().info("writing test case coverage for " + record.getVC("programName"));
            writer.writeTestProgramCoverage(record.getVC("programName"), record.getVC("coverageXML"));
        }
    }
}
