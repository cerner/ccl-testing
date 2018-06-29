package com.cerner.ccl.testing.maven.ccl.mojo;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.CclExecutor;
import com.cerner.ccl.j4ccl.adders.ScriptExecutionAdder;
import com.cerner.ccl.j4ccl.enums.OutputType;
import com.cerner.ccl.j4ccl.record.DynamicRecordList;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.RecordList;
import com.cerner.ccl.testing.maven.ccl.data.UnitTest;
import com.cerner.ccl.testing.maven.ccl.exception.TestFailureException;
import com.cerner.ccl.testing.maven.ccl.mojo.TestMojo;
import com.cerner.ccl.testing.maven.ccl.util.DelegatingOutputStream;
import com.cerner.ccl.testing.maven.ccl.util.ProgramListingWriter;
import com.cerner.ccl.testing.maven.ccl.util.TestResultScanner;
import com.cerner.ccl.testing.maven.ccl.util.TestResultWriter;
import com.cerner.ccl.testing.maven.ccl.util.factory.CclUnitRecordFactory;
import com.cerner.ccl.testing.maven.ccl.util.factory.ProgramListingWriterFactory;
import com.cerner.ccl.testing.maven.ccl.util.factory.TestResultScannerFactory;
import com.cerner.ccl.testing.maven.ccl.util.factory.TestResultWriterFactory;

/**
 * Unit tests of {@link TestMojo}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { CclUnitRecordFactory.class, TestMojo.class, CclExecutor.class })
public class TestMojoTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    private static final TestResultWriterFactory RESULT_WRITER_FACTORY = new MockResultWriterFactory();
    private static final ProgramListingWriterFactory LISTING_WRITER_FACTORY = new MockListingWriterFactory();
    private static final TestResultScannerFactory RESULT_SCANNER_FACTORY = new MockResultScannerFactory();

    private static final File CCLSOURCE_DIR = new File("target/unit/src/main/ccl");
    private static final File CCLTEST_DIR = new File("target/unit/src/test/ccl");
    private static final File OUTPUT_DIR = new File("target/unit/target");

    private static final String ENVIRONMENT_NAME = "env";
    private static final String HOST_NAME = "my.host.name";
    private static final String HOST_USERNAME = "host.username";
    private static final String HOST_PASSWORD = "host.password";

    @Captor
    private ArgumentCaptor<ScriptExecutionAdder> argumentCaptorScriptExecutionAdder;

    /**
     * Make sure that the directories exist on the hard disk.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Before
    public void setUp() throws Exception {
        FileUtils.forceMkdir(CCLSOURCE_DIR);
        FileUtils.forceMkdir(CCLTEST_DIR);
        FileUtils.forceMkdir(OUTPUT_DIR);
    }

    /**
     * Make sure that no hard-disk artifacts are left over from each test.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(CCLSOURCE_DIR);
        FileUtils.deleteDirectory(CCLTEST_DIR);
        FileUtils.deleteDirectory(OUTPUT_DIR);
    }

    /**
     * Verify that, if a scanner reports a failure in a test, that the build fails.
     *
     * @throws Exception
     *             If any errors occur during the test.
     */
    @Test
    public void testExecuteBuildFailure() throws Exception {
        final String testName = "testName!";

        // Build the unit test
        final UnitTest test = mock(UnitTest.class);
        when(test.getName()).thenReturn(testName);

        // Build the scanner
        final TestResultScanner scanner = mock(TestResultScanner.class);
        when(scanner.scanForFailures(ExecutionAdderReplyWriter.RESULTS_XML)).thenReturn(Collections.singleton(test));
        final TestResultScannerFactory scannerFactory = new MockResultScannerFactory(scanner);

        // Build the script execution adder
        final ExecutionAdderReplyWriter answer = new ExecutionAdderReplyWriter();
        final ScriptExecutionAdder adder = mock(ScriptExecutionAdder.class, answer);
        doNothing().when(adder).commit();
        answer.setAdder(adder);

        // Build the script execution adder
        final CclExecutor executor = mock(CclExecutor.class);
        when(executor.addScriptExecution("cclut_execute_test_case")).thenReturn(adder);

        mockStatic(CclExecutor.class);
        when(CclExecutor.getExecutor()).thenReturn(executor);

        final File sourceFile = new File(CCLSOURCE_DIR, "test.prg");
        FileUtils.touch(sourceFile);

        final File testFile = new File(CCLTEST_DIR, "test.inc");
        FileUtils.touch(testFile);

        final TestMojo mojo = new TestMojo(RESULT_WRITER_FACTORY, LISTING_WRITER_FACTORY, scannerFactory);
        setParameters(mojo);

        expected.expect(TestFailureException.class);
        mojo.execute();
    }

    /**
     * Verify that, if the testing frameworks a failure, that the proper exception is thrown.
     *
     * @throws Exception
     *             If any errors occur during the test.
     */
    @Test
    public void testExecuteTestingFrameworkFailure() throws Exception {
        final String expectedErrorMessage = "an error message!!! oh noes!";

        final Record subeventStatusItem = mock(Record.class);
        final RecordList subeventStatusList = mock(RecordList.class);
        when(subeventStatusList.get(0)).thenReturn(subeventStatusItem);
        when(subeventStatusItem.getVC("TargetObjectValue")).thenReturn(expectedErrorMessage);

        final Record statusData = mock(Record.class);
        when(statusData.getChar("status")).thenReturn("F");
        when(statusData.getList("subeventstatus")).thenReturn(subeventStatusList);

        final Record reply = mock(Record.class);
        when(reply.getRecord("status_data")).thenReturn(statusData);
        mockStatic(CclUnitRecordFactory.class);
        when(CclUnitRecordFactory.createReply()).thenReturn(reply);

        // Build the script execution adder
        final ScriptExecutionAdder adder = mock(ScriptExecutionAdder.class, new Answer<ScriptExecutionAdder>() {
            public ScriptExecutionAdder answer(final InvocationOnMock invocation) throws Throwable {
                return (ScriptExecutionAdder) invocation.getMock();
            }
        });
        doNothing().when(adder).commit();

        // Build the script execution adder
        final CclExecutor executor = mock(CclExecutor.class);
        when(executor.addScriptExecution("cclut_execute_test_case")).thenReturn(adder);

        mockStatic(CclExecutor.class);
        when(CclExecutor.getExecutor()).thenReturn(executor);

        final File sourceFile = new File(CCLSOURCE_DIR, "test.prg");
        FileUtils.touch(sourceFile);

        final File testFile = new File(CCLTEST_DIR, "test.inc");
        FileUtils.touch(testFile);

        final TestMojo mojo = new TestMojo(RESULT_WRITER_FACTORY, LISTING_WRITER_FACTORY, RESULT_SCANNER_FACTORY);
        setParameters(mojo);

        expected.expect(MojoFailureException.class);
        expected.expectMessage("CCL Testing Framework has reported a failure: " + expectedErrorMessage);
        mojo.execute();
    }

    /**
     * Test that executing with no includes does not invoke the executor.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecuteNoIncludes() throws Exception {
        final Log log = mock(Log.class);
        final TestMojo mojo = new TestMojo(RESULT_WRITER_FACTORY, LISTING_WRITER_FACTORY, RESULT_SCANNER_FACTORY);
        setParameters(mojo);

        mojo.setLog(log);
        mojo.execute();

        verify(log).info("No tests to execute");
    }

    /**
     * Verify that the mojo invokes the CCL unit testing framework.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecuteRunCclUnitTest() throws Exception {
        final ExecutionAdderRequestArchiver answer = new ExecutionAdderRequestArchiver();
        final ScriptExecutionAdder adder = mock(ScriptExecutionAdder.class, answer);
        answer.setAdder(adder);
        doNothing().when(adder).commit();

        final CclExecutor executor = mock(CclExecutor.class);
        when(executor.addScriptExecution("cclut_execute_test_case")).thenReturn(adder);

        mockStatic(CclExecutor.class);
        when(CclExecutor.getExecutor()).thenReturn(executor);

        final File sourceFile = new File(CCLSOURCE_DIR, "test.prg");
        FileUtils.touch(sourceFile);

        final File testFile = new File(CCLTEST_DIR, "test.inc");
        FileUtils.touch(testFile);

        final TestMojo mojo = new TestMojo(RESULT_WRITER_FACTORY, LISTING_WRITER_FACTORY, RESULT_SCANNER_FACTORY);
        setParameters(mojo);
        mojo.execute();

        verify(executor).addScriptExecution("cclut_execute_test_case");
        verify(executor).execute();

        /*
         * Verify that the contents of the request record structure were correct
         */
        final Record request = answer.getRequest();
        assertThat(request.getVC("testINCName")).isEqualTo("test.inc");

        final DynamicRecordList list = request.getDynamicList("programs");
        assertThat(list.getSize()).isEqualTo(1);
        final Record record = list.get(0);
        assertThat(record.getVC("programName")).isEqualTo("test");
        assertThat(record.getI2Boolean("compile")).isTrue();
    }

    /**
     * If the user specifies a test subroutine name, then it should be sent to the testing framework.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecuteRunCclUnitTestSetSubroutineName() throws Exception {
        final ExecutionAdderRequestArchiver answer = new ExecutionAdderRequestArchiver();
        final ScriptExecutionAdder adder = mock(ScriptExecutionAdder.class, answer);
        answer.setAdder(adder);
        doNothing().when(adder).commit();

        final CclExecutor executor = mock(CclExecutor.class);
        when(executor.addScriptExecution("cclut_execute_test_case")).thenReturn(adder);

        mockStatic(CclExecutor.class);
        when(CclExecutor.getExecutor()).thenReturn(executor);

        final File sourceFile = new File(CCLSOURCE_DIR, "test.prg");
        FileUtils.touch(sourceFile);

        final File testFile = new File(CCLTEST_DIR, "test.inc");
        FileUtils.touch(testFile);

        final File test2Inc = new File(CCLTEST_DIR, "test2.inc");
        test2Inc.createNewFile();

        final File retestInc = new File(CCLTEST_DIR, "retest.inc");
        retestInc.createNewFile();

        final TestMojo mojo = new TestMojo(RESULT_WRITER_FACTORY, LISTING_WRITER_FACTORY, RESULT_SCANNER_FACTORY);
        setParameters(mojo);
        mojo.testCase = "test.*#testNamePattern.*";
        mojo.execute();

        verify(executor, times(2)).addScriptExecution("cclut_execute_test_case");
        verify(executor, times(2)).execute();

        /*
         * Verify that the contents of the request record structure were correct
         */
        final List<Record> requests = answer.getRequests();
        assertThat(requests.size()).isEqualTo(2);
        assertThat(requests.get(0).getVC("testINCName")).isNotEqualTo(requests.get(1).getVC("testINCName"));
        for (int index = 0; index < 2; index++) {
            String testCaseName = requests.get(index).getVC("testINCName");
            assertThat(testCaseName.equals("test.inc") || testCaseName.equals("test2.inc")).isEqualTo(true);
            assertThat(requests.get(index).getVC("testSubroutineName")).isEqualTo("testNamePattern.*");
        }
    }

    /**
     * Test that the writer is invoked for each test.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecuteWriteTestOutput() throws Exception {
        // Build the test results writer
        final TestResultWriter writer = mock(TestResultWriter.class);
        final TestResultWriterFactory resultsWriterFactory = new MockResultWriterFactory(writer);

        // Build the script execution adder
        final ExecutionAdderReplyWriter answer = new ExecutionAdderReplyWriter();
        final ScriptExecutionAdder adder = mock(ScriptExecutionAdder.class, answer);
        answer.setAdder(adder);
        doNothing().when(adder).commit();

        // Build the script execution adder
        final CclExecutor executor = mock(CclExecutor.class);
        when(executor.addScriptExecution("cclut_execute_test_case")).thenReturn(adder);

        mockStatic(CclExecutor.class);
        when(CclExecutor.getExecutor()).thenReturn(executor);

        final File sourceFile = new File(CCLSOURCE_DIR, "test.prg");
        FileUtils.touch(sourceFile);

        final File testFile = new File(CCLTEST_DIR, "test.inc");
        FileUtils.touch(testFile);

        final TestMojo mojo = new TestMojo(resultsWriterFactory, LISTING_WRITER_FACTORY, RESULT_SCANNER_FACTORY);
        setParameters(mojo);
        mojo.execute();

        verify(writer, times(1)).writeTestCoverage(ExecutionAdderReplyWriter.COVERAGE_XML);
        verify(writer, times(1)).writeTestListing(ExecutionAdderReplyWriter.LISTING_XML);
        verify(writer, times(1)).writeTestResults(ExecutionAdderReplyWriter.RESULTS_XML);
        verify(writer, times(1)).writeTestProgramCoverage(ExecutionAdderReplyWriter.PROGRAM_NAME,
                ExecutionAdderReplyWriter.PROGRAM_COVERAGE_XML);
    }

    /**
     * Verify that the program listing is written.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecuteWriteListing() throws Exception {
        // Build the program listing writer
        final ProgramListingWriter writer = mock(ProgramListingWriter.class);
        final ProgramListingWriterFactory writerFactory = new MockListingWriterFactory(writer);

        // Build the script execution adder
        final ExecutionAdderReplyWriter answer = new ExecutionAdderReplyWriter();
        final ScriptExecutionAdder adder = mock(ScriptExecutionAdder.class, answer);
        answer.setAdder(adder);
        doNothing().when(adder).commit();

        // Build the script execution adder
        final CclExecutor executor = mock(CclExecutor.class);
        when(executor.addScriptExecution("cclut_execute_test_case")).thenReturn(adder);

        mockStatic(CclExecutor.class);
        when(CclExecutor.getExecutor()).thenReturn(executor);

        final File sourceFile = new File(CCLSOURCE_DIR, "test.prg");
        FileUtils.touch(sourceFile);

        final File testFile = new File(CCLTEST_DIR, "test.inc");
        FileUtils.touch(testFile);

        final TestMojo mojo = new TestMojo(RESULT_WRITER_FACTORY, writerFactory, RESULT_SCANNER_FACTORY);
        setParameters(mojo);
        mojo.execute();

        verify(writer, times(1)).writeListing(ExecutionAdderReplyWriter.PROGRAM_NAME,
                ExecutionAdderReplyWriter.PROGRAM_LISTING_XML);
    }

    /**
     * Verify that, if debugging is enabled, the CCL session output of the executor is forwarded.
     *
     * @throws Exception
     *             If any errors occur during the test.
     */
    @Test
    public void testSetCclSessionOutput() throws Exception {
        final Log log = mock(Log.class);
        when(log.isDebugEnabled()).thenReturn(Boolean.TRUE);

        final ExecutionAdderRequestArchiver answer = new ExecutionAdderRequestArchiver();
        final ScriptExecutionAdder adder = mock(ScriptExecutionAdder.class, answer);
        answer.setAdder(adder);
        doNothing().when(adder).commit();

        final CclExecutor executor = mock(CclExecutor.class);
        when(executor.addScriptExecution("cclut_execute_test_case")).thenReturn(adder);

        mockStatic(CclExecutor.class);
        when(CclExecutor.getExecutor()).thenReturn(executor);

        final File sourceFile = new File(CCLSOURCE_DIR, "test.prg");
        FileUtils.touch(sourceFile);

        final File testFile = new File(CCLTEST_DIR, "test.inc");
        FileUtils.touch(testFile);

        final TestMojo mojo = new TestMojo(RESULT_WRITER_FACTORY, LISTING_WRITER_FACTORY, RESULT_SCANNER_FACTORY);
        setParameters(mojo);
        mojo.setLog(log);
        mojo.execute();

        final ArgumentCaptor<OutputStream> streamCaptor = ArgumentCaptor.forClass(OutputStream.class);
        verify(executor).setOutputStream(streamCaptor.capture(), eq(OutputType.CCL_SESSION));

        final OutputStream caught = streamCaptor.getValue();
        assertThat(caught).isInstanceOf(DelegatingOutputStream.class);
        caught.close();
    }

    /**
     * Test setting of the {@code ccl-skipTest} property.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSkipTest() throws Exception {
        final TestMojo mojo = new TestMojo();
        final Log log = mock(Log.class);

        mojo.skipTest = true;

        mojo.setLog(log);
        mojo.execute();

        verify(log, times(1)).info("Skipping test goal");
    }

    /**
     * Set the parameters necessary for the mojo to function.
     *
     * @param mojo
     *            A {@link TestMojo} whose parameters are to be set.
     */
    private void setParameters(final TestMojo mojo) {
        mojo.cclSourceDirectory = CCLSOURCE_DIR;
        mojo.cclTestSourceDirectory = CCLTEST_DIR;
        mojo.outputDirectory = OUTPUT_DIR;

        mojo.environment = ENVIRONMENT_NAME;
        mojo.host = HOST_NAME;
        mojo.hostUsername = HOST_USERNAME;
        mojo.hostPassword = HOST_PASSWORD;

        mojo.logFile = new File("target/unit/ccl.log");
        mojo.failOnTestFailures = true;
        mojo.deprecatedFlag = "E";
    }

    /**
     * An implementation of {@link Answer} that sets the reply XML in the record structure.
     *
     * @author Joshua Hyde
     *
     */
    private static class ExecutionAdderReplyWriter implements Answer<Object> {
        public static final String LISTING_XML = "<listing />";
        public static final String RESULTS_XML = "<results />";
        public static final String COVERAGE_XML = "<coverage />";
        public static final String PROGRAM_COVERAGE_XML = "<program_coverage />";
        public static final String PROGRAM_LISTING_XML = "<program_listing_xml />";

        public static final String PROGRAM_NAME = "test";

        private ScriptExecutionAdder adder;

        public ExecutionAdderReplyWriter() {
        }

        public Object answer(final InvocationOnMock invocation) throws Throwable {
            if ("commit".equals(invocation.getMethod().getName())) {
                System.out.println("ExecutionAdderReplyWriter commit called.");
            }
            if (!"withReplace".equals(invocation.getMethod().getName()))
                return adder;

            final String recordName = (String) invocation.getArguments()[0];
            final Record record = (Record) invocation.getArguments()[1];

            if ("cclutReply".equals(recordName)) {
                record.setVC("testINCListingXML", LISTING_XML);
                record.setVC("testINCResultsXML", RESULTS_XML);
                record.setVC("testINCCoverageXML", COVERAGE_XML);

                final Record program = record.getDynamicList("programs").addItem();
                program.setVC("programName", PROGRAM_NAME);
                program.setVC("coverageXML", PROGRAM_COVERAGE_XML);
                program.setVC("listingXML", PROGRAM_LISTING_XML);
            }

            return adder;
        }

        /**
         * Set the adder to be returned by this object.
         *
         * @param adder
         *            The adder to be returned.
         */
        public void setAdder(final ScriptExecutionAdder adder) {
            this.adder = adder;
        }

    }

    /**
     * Implementation of {@link Answer} to back {@link ScriptExecutionAdder} objects.
     *
     * @author Joshua Hyde
     *
     */
    private static class ExecutionAdderRequestArchiver implements Answer<Object> {
        private ScriptExecutionAdder adder;
        private Record request;
        private final List<Record> requests = new ArrayList<Record>();

        public ExecutionAdderRequestArchiver() {
        }

        public Object answer(final InvocationOnMock invocation) throws Throwable {
            if (!"withReplace".equals(invocation.getMethod().getName()))
                return adder;

            final String recordName = (String) invocation.getArguments()[0];
            final Record record = (Record) invocation.getArguments()[1];

            if ("cclutRequest".equals(recordName)) {
                request = record;
                requests.add(request);
            }

            return adder;
        }

        /**
         * Return the request record structure (if any) passed to the adder.
         *
         * @return The request record structure.
         */
        public Record getRequest() {
            return request;
        }

        /**
         * Returns the list of all requests that have been captured as an unmodifiable list.
         *
         * @return The list of all requests that have been captured as an unmodifiable list.
         */
        public List<Record> getRequests() {
            return Collections.unmodifiableList(requests);
        }

        /**
         * Set the adder to be returned by this object.
         *
         * @param adder
         *            The adder to be returned.
         */
        public void setAdder(final ScriptExecutionAdder adder) {
            this.adder = adder;
        }
    }

    /**
     * A mock implementation of {@link TestResultWriterFactory} that returns a specific {@link TestResultWriter},
     * regardless of the given parameters.
     *
     * @author Joshua Hyde
     *
     */
    private static class MockResultWriterFactory extends TestResultWriterFactory {
        private final TestResultWriter writer;

        /**
         * Create a factory that creates a mock test result writer.
         */
        public MockResultWriterFactory() {
            this(mock(TestResultWriter.class));
        }

        /**
         * Create a factory that creates the given test result writer.
         *
         * @param writer
         *            The writer to be "created".
         */
        public MockResultWriterFactory(final TestResultWriter writer) {
            this.writer = writer;
        }

        @Override
        public TestResultWriter create(final String testName, final File outputDirectory) {
            return writer;
        }
    }

    /**
     * A mock implementation of {@link ProgramListingWriterFactory} that returns a specified
     * {@link ProgramListingWriter}, regardless of the given parameters.
     *
     * @author Joshua Hyde
     *
     */
    private static class MockListingWriterFactory extends ProgramListingWriterFactory {
        private final ProgramListingWriter writer;

        /**
         * Create a factory that returns a mock writer.
         */
        public MockListingWriterFactory() {
            this(mock(ProgramListingWriter.class));
        }

        /**
         * Create a factory that returns the given writer.
         *
         * @param writer
         *            The writer to be "created" by this factory.
         */
        public MockListingWriterFactory(final ProgramListingWriter writer) {
            this.writer = writer;
        }

        @Override
        public ProgramListingWriter create(final File outputDirectory) {
            return writer;
        }
    }

    /**
     * A mock implementation of {@link TestResultScannerFactory} that always returns a specified
     * {@link TestResultScanner}, regardless of the given parameters.
     *
     * @author Joshua Hyde
     *
     */
    private static class MockResultScannerFactory extends TestResultScannerFactory {
        private final TestResultScanner scanner;

        /**
         * Create a factory to return a mock scanner.
         */
        public MockResultScannerFactory() {
            this(mock(TestResultScanner.class));
        }

        /**
         * Create a factory to return the given scanner.
         *
         * @param scanner
         *            The scanner to be "created" by this factory.
         */
        public MockResultScannerFactory(final TestResultScanner scanner) {
            this.scanner = scanner;
        }

        @Override
        public TestResultScanner create() {
            return scanner;
        }
    }
}
