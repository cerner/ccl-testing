package com.cerner.ccl.testing.maven.ccl.reports;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.reporting.MavenReportException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.cerner.ccl.testing.maven.ccl.reports.CCLCoverageMojoTest.InjectableMojo.LoadInvocation;
import com.cerner.ccl.testing.maven.ccl.reports.common.CCLCoverageProgram;
import com.cerner.ccl.testing.maven.ccl.reports.common.ReportErrorLogger;

/**
 * Unit tests for {@link CCLCoverageMojo}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { AbstractCCLMavenReport.class, CCLCoverageMojo.class, CCLCoverageReportGenerator.class,
        FileUtils.class, ReportErrorLogger.class })
public class CCLCoverageMojoTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Mock
    private Log log;
    @Mock
    private File testResultsDirectory;
    @Mock
    private File programListingsDirectory;
    @Mock
    private File reportErrorDirectory;
    @Mock
    private File outputDirectory;
    private CCLCoverageMojo mojo;

    /**
     * Set up the mojo for each test.
     *
     * @throws Exception
     *             If any errors occur during the setup.
     */
    @Before
    public void setUp() throws Exception {
        mojo = new CCLCoverageMojo();
        setUpMojo(mojo);
    }

    /**
     * If the test results and program listings directories both exist, then the report can be generated.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCanGenerateReport() throws Exception {
        when(testResultsDirectory.exists()).thenReturn(Boolean.TRUE);
        when(testResultsDirectory.isDirectory()).thenReturn(Boolean.TRUE);

        when(programListingsDirectory.exists()).thenReturn(Boolean.TRUE);
        when(programListingsDirectory.isDirectory()).thenReturn(Boolean.TRUE);

        assertThat(mojo.canGenerateReport()).isTrue();
    }

    /**
     * If the program listings directory does not exist, the report cannot be generated.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCanGenerateReportProgramListingsNotExist() throws Exception {
        when(testResultsDirectory.exists()).thenReturn(Boolean.TRUE);
        when(testResultsDirectory.isDirectory()).thenReturn(Boolean.TRUE);

        assertThat(mojo.canGenerateReport()).isFalse();
        verify(log).info("Cannot generate CCL Coverage report due to missing program-listings directory");
    }

    /**
     * If the program listings directory exists but is not, in fact, a directory, then the report cannot be generated.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCanGenerateReportProgramListingsNotDirectory() throws Exception {
        when(testResultsDirectory.exists()).thenReturn(Boolean.TRUE);
        when(testResultsDirectory.isDirectory()).thenReturn(Boolean.TRUE);

        when(programListingsDirectory.exists()).thenReturn(Boolean.TRUE);

        assertThat(mojo.canGenerateReport()).isFalse();
        verify(log).info("Cannot generate CCL Coverage report due to missing program-listings directory");
    }

    /**
     * If the test results directory does not exist, then the mojo cannot generate the report.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCanGenerateReportTestResultsNotExist() throws Exception {
        assertThat(mojo.canGenerateReport()).isFalse();
        verify(log).info("Cannot generate CCL Coverage report due to missing test-results directory");
    }

    /**
     * If the test results directory does exist but it is not, in fact, a directory, then the mojo cannot generate the
     * report.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCanGenerateReportTestResultsNotDirectory() throws Exception {
        when(programListingsDirectory.exists()).thenReturn(Boolean.TRUE);
        assertThat(mojo.canGenerateReport()).isFalse();
        verify(log).info("Cannot generate CCL Coverage report due to missing test-results directory");
    }

    /**
     * Test the execution of report generation.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExecuteReport() throws Exception {

        final ReportErrorLogger errorLogger = mock(ReportErrorLogger.class);
        whenNew(ReportErrorLogger.class).withArguments(reportErrorDirectory).thenReturn(errorLogger);

        final Map<String, CCLCoverageProgram> testPrograms = mock(Map.class);
        final Set<CCLCoverageProgram> testValues = mock(Set.class);
        when(testPrograms.values()).thenReturn(testValues);

        final Map<String, CCLCoverageProgram> programs = mock(Map.class);
        final Set<CCLCoverageProgram> programValues = mock(Set.class);
        when(programs.values()).thenReturn(programValues);

        final CCLCoverageReportGenerator generator = mock(CCLCoverageReportGenerator.class);
        whenNew(CCLCoverageReportGenerator.class).withArguments(outputDirectory, testValues, programValues, errorLogger)
                .thenReturn(generator);

        final InjectableMojo injectable = new InjectableMojo();
        setUpMojo(injectable);
        injectable.setSourcePrograms(programListingsDirectory, programs);
        injectable.setTestPrograms(testResultsDirectory, testPrograms);
        injectable.setCanGenerateReport(true);
        injectable.executeReport(Locale.getDefault());
        verify(generator).generateReport();

        final List<LoadInvocation> invocations = injectable.getInvocations();
        assertThat(invocations).hasSize(1);
        final LoadInvocation invocation = invocations.get(0);
        assertThat(invocation.getPrograms()).isEqualTo(programs);
        assertThat(invocation.getTestResultsDirectory()).isEqualTo(testResultsDirectory);
        assertThat(invocation.getTests()).isEqualTo(testPrograms);
    }

    /**
     * If the mojo indicates that the report cannot be generated, then the report should not be generated.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecuteReportCanNotGenerate() throws Exception {
        final InjectableMojo injectable = new InjectableMojo();
        setUpMojo(injectable);

        whenNew(ReportErrorLogger.class).withArguments(reportErrorDirectory)
                .thenThrow(new AssertionError("I shouldn't have been called"));
        // a lack of errors indicates that the report generation was aborted
        injectable.executeReport(Locale.getDefault());
    }

    /**
     * Test the retrieval of the description.
     */
    @Test
    public void testGetDescription() {
        assertThat(mojo.getDescription(Locale.getDefault())).isEqualTo("Reports code coverage for automated CCL tests");
    }

    /**
     * Test the retrieval of the name.
     */
    @Test
    public void testGetName() {
        assertThat(mojo.getName(Locale.getDefault())).isEqualTo("CCL Coverage Report");
    }

    /**
     * Test the retrieval of the output name.
     */
    @Test
    public void testGetOutputName() {
        assertThat(mojo.getOutputName()).isEqualTo("ccl-coverage-report");
    }

    /**
     * The mojo should indicate itself as an external report.
     */
    @Test
    public void testIsExternalReport() {
        assertThat(mojo.isExternalReport()).isTrue();
    }

    /**
     * Test the loading of coverage.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testLoadCoverage() throws Exception {
        final String testResultDirectoryName = "i am the test result directory";
        final String testCoverageXml = "i am the test coverage xml";
        final String programName = "a.program";
        final String programCoverageXml = "i am the program coverage xml";

        final File testResultDirectory = mock(File.class);
        when(testResultDirectory.getName()).thenReturn(testResultDirectoryName);

        final File listingXmlFile = mock(File.class);
        mockStatic(AbstractCCLMavenReport.class);
        when(AbstractCCLMavenReport.getDirectoryFile(testResultDirectory, "listing.xml")).thenReturn(listingXmlFile);

        // A directory to make sure that, if a directory has no listing.xml file, it will not be read
        final File noListingXmlDirectory = mock(File.class);

        // Create three files to exercise the iteration of the contents of the test result directory:
        // 1) to exercise the reading of the coverage contents;
        // 2) to make sure that a non-coverage directory is not read;
        // 3) to make sure a non-directory is not read

        final File coverageDirectory = mock(File.class);
        when(coverageDirectory.getName()).thenReturn("coverage");
        when(coverageDirectory.isDirectory()).thenReturn(Boolean.TRUE);

        final File notCoverageDirectory = mock(File.class);
        when(notCoverageDirectory.isDirectory()).thenReturn(Boolean.TRUE);
        when(notCoverageDirectory.getName()).thenReturn("notCoverage");

        final File notDirectory = mock(File.class);
        when(testResultDirectory.listFiles())
                .thenReturn(new File[] { coverageDirectory, notCoverageDirectory, notDirectory });

        // Set up four files in the coverage directory:
        // 1) a test-coverage file to exercise the reading of test coverage data;
        // 2) a program's coverage data;
        // 3) a file that is not an XML file;
        // 4) a non-file
        final File testCoverageFile = mock(File.class);
        when(testCoverageFile.isFile()).thenReturn(Boolean.TRUE);
        when(testCoverageFile.getName()).thenReturn("test-coverage.xml");

        final File programCoverageFile = mock(File.class);
        when(programCoverageFile.isFile()).thenReturn(Boolean.TRUE);
        when(programCoverageFile.getName()).thenReturn(programName + ".xml");

        final File notXmlFile = mock(File.class);
        when(notXmlFile.isFile()).thenReturn(Boolean.TRUE);
        when(notXmlFile.getName()).thenReturn("notXmlFile.txt");

        final File notFile = mock(File.class);
        when(notFile.isFile()).thenReturn(Boolean.FALSE);
        when(coverageDirectory.listFiles())
                .thenReturn(new File[] { testCoverageFile, programCoverageFile, notXmlFile, notFile });

        final File testResultsDirectory = mock(File.class);
        when(testResultsDirectory.listFiles()).thenReturn(new File[] { testResultDirectory, noListingXmlDirectory });

        final CCLCoverageProgram testProgram = mock(CCLCoverageProgram.class);
        final CCLCoverageProgram program = mock(CCLCoverageProgram.class);
        final Map<String, CCLCoverageProgram> programs = new HashMap<String, CCLCoverageProgram>();
        final Map<String, CCLCoverageProgram> testPrograms = new HashMap<String, CCLCoverageProgram>();
        testPrograms.put(testResultDirectoryName, testProgram);
        programs.put(programName, program);

        mockStatic(FileUtils.class);
        when(FileUtils.readFileToString(testCoverageFile, "utf-8")).thenReturn(testCoverageXml);
        when(FileUtils.readFileToString(programCoverageFile, "utf-8")).thenReturn(programCoverageXml);

        mojo.loadCoverage(testResultsDirectory, programs, testPrograms);

        verify(program).addCoverage(testProgram, programCoverageXml);
        verify(testProgram).addCoverage(testProgram, testCoverageXml);

        // Verify that the not-considered directories are never read
        verify(noListingXmlDirectory, never()).listFiles();
        verify(notCoverageDirectory, never()).listFiles();
        verify(notDirectory, never()).listFiles();

        // Verify that the non-coverage files are not read
        verifyStatic(FileUtils.class, never());
        FileUtils.readFileToString(notXmlFile, "utf-8");

        verifyStatic(FileUtils.class, never());
        FileUtils.readFileToString(notFile, "utf-8");
    }

    /**
     * If a test program is not found in the given map, then loading the coverage data should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testLoadCoverageTestNotFound() throws Exception {
        final String testResultDirectoryName = "i am the test result directory";

        final File testResultDirectory = mock(File.class);
        when(testResultDirectory.getName()).thenReturn(testResultDirectoryName);

        final File listingXmlFile = mock(File.class);
        mockStatic(AbstractCCLMavenReport.class);
        when(AbstractCCLMavenReport.getDirectoryFile(testResultDirectory, "listing.xml")).thenReturn(listingXmlFile);

        final File testResultsDirectory = mock(File.class);
        when(testResultsDirectory.listFiles()).thenReturn(new File[] { testResultDirectory });

        expected.expect(MavenReportException.class);
        expected.expectMessage("Failed to get the test program object for test folder " + testResultDirectoryName);
        mojo.loadCoverage(testResultsDirectory, Collections.<String, CCLCoverageProgram> emptyMap(),
                Collections.<String, CCLCoverageProgram> emptyMap());
    }

    /**
     * If an attempt is made to load coverage for a program that is not known to the mojo, then loading the coverage
     * should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testLoadCoverageProgramNotFound() throws Exception {
        final String testResultDirectoryName = "i am the test result directory";
        final String programName = "a.program";
        final String programAbsolutePath = "i am the absolute file path";

        final File testResultDirectory = mock(File.class);
        when(testResultDirectory.getName()).thenReturn(testResultDirectoryName);

        final File listingXmlFile = mock(File.class);
        mockStatic(AbstractCCLMavenReport.class);
        when(AbstractCCLMavenReport.getDirectoryFile(testResultDirectory, "listing.xml")).thenReturn(listingXmlFile);

        final File coverageDirectory = mock(File.class);
        when(coverageDirectory.getName()).thenReturn("coverage");
        when(coverageDirectory.isDirectory()).thenReturn(Boolean.TRUE);
        when(testResultDirectory.listFiles()).thenReturn(new File[] { coverageDirectory });

        final File testResultsDirectory = mock(File.class);
        when(testResultsDirectory.listFiles()).thenReturn(new File[] { testResultDirectory });

        final File programCoverageFile = mock(File.class);
        when(programCoverageFile.isFile()).thenReturn(Boolean.TRUE);
        when(programCoverageFile.getName()).thenReturn(programName + ".xml");
        when(programCoverageFile.getAbsolutePath()).thenReturn(programAbsolutePath);
        when(coverageDirectory.listFiles()).thenReturn(new File[] { programCoverageFile });

        final CCLCoverageProgram testProgram = mock(CCLCoverageProgram.class);
        final Map<String, CCLCoverageProgram> testPrograms = new HashMap<String, CCLCoverageProgram>();
        testPrograms.put(testResultDirectoryName, testProgram);

        mockStatic(FileUtils.class);
        when(FileUtils.readFileToString(programCoverageFile, "utf-8"))
                .thenReturn("this is just to prevent this call from failing");

        expected.expect(MavenReportException.class);
        expected.expectMessage("Expected a corresponding program listing for the coverage file " + programAbsolutePath);
        mojo.loadCoverage(testResultsDirectory, Collections.<String, CCLCoverageProgram> emptyMap(), testPrograms);
    }

    /**
     * Test the loading of source programs.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testLoadSourcePrograms() throws Exception {
        final String programName = "a.program.name";
        final String programListingXml = "a listing XML string";
        final File xmlFile = mock(File.class);
        when(xmlFile.getName()).thenReturn(programName + ".xml");

        mockStatic(FileUtils.class);
        when(FileUtils.readFileToString(xmlFile, "utf-8")).thenReturn(programListingXml);

        final File notXmlFile = mock(File.class);
        when(notXmlFile.getName()).thenReturn("notXmlFile.txt");

        final CCLCoverageProgram program = mock(CCLCoverageProgram.class);
        whenNew(CCLCoverageProgram.class).withArguments(programListingXml).thenReturn(program);

        when(programListingsDirectory.listFiles()).thenReturn(new File[] { xmlFile, notXmlFile });

        assertThat(mojo.loadSourcePrograms(programListingsDirectory)).hasSize(1).includes(entry(programName, program));
    }

    /**
     * Verify the loading of listing XML files.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testLoadTestPrograms() throws Exception {
        final String listingXmlDirectoryName = "i am the listing xml directory";
        final String listingXml = "i am the listing xml";
        final File listingXmlFile = mock(File.class);
        final File listingXmlDirectory = mock(File.class);
        when(listingXmlDirectory.getName()).thenReturn(listingXmlDirectoryName);

        mockStatic(AbstractCCLMavenReport.class);
        when(AbstractCCLMavenReport.getDirectoryFile(listingXmlDirectory, "listing.xml")).thenReturn(listingXmlFile);

        mockStatic(FileUtils.class);
        when(FileUtils.readFileToString(listingXmlFile, "utf-8")).thenReturn(listingXml);

        final File noListingXmlDirectory = mock(File.class);

        when(testResultsDirectory.listFiles()).thenReturn(new File[] { noListingXmlDirectory, listingXmlDirectory });

        final CCLCoverageProgram testProgram = mock(CCLCoverageProgram.class);
        whenNew(CCLCoverageProgram.class).withArguments(listingXml).thenReturn(testProgram);

        assertThat(mojo.loadTestPrograms(testResultsDirectory)).hasSize(1)
                .includes(entry(listingXmlDirectoryName, testProgram));
    }

    /**
     * Set up the fields within the given mojo.
     *
     * @param mojo
     *            The {@link CCLCoverageMojo} whose attributes are to be set.
     */
    private void setUpMojo(final CCLCoverageMojo mojo) {
        mojo.setLog(log);
        mojo.testResultsDirectory = testResultsDirectory;
        mojo.reportErrorDirectory = reportErrorDirectory;
        mojo.outputDirectory = outputDirectory;
        Whitebox.setInternalState(mojo, "programListingsDirectory", programListingsDirectory);
    }

    /**
     * An extension of {@link CCLCoverageMojo} that delegates the load methods to in-memory stores.
     *
     * @author Joshua Hyde
     *
     */
    public static class InjectableMojo extends CCLCoverageMojo {
        /**
         * A record of an invocation of {@link InjectableMojo#loadCoverage(File, Map, Map)}.
         *
         * @author Joshua Hyde
         *
         */
        @SuppressWarnings("javadoc")
        public static class LoadInvocation {
            private final File testResultsDirectory;
            private final Map<String, CCLCoverageProgram> programs;
            private final Map<String, CCLCoverageProgram> tests;

            /**
             * Create a record.
             *
             * @param testResultsDirectory
             *            The test results directory.
             * @param programs
             *            The programs.
             * @param tests
             *            The test programs.
             */
            public LoadInvocation(final File testResultsDirectory, final Map<String, CCLCoverageProgram> programs,
                    final Map<String, CCLCoverageProgram> tests) {
                this.testResultsDirectory = testResultsDirectory;
                this.programs = programs;
                this.tests = tests;
            }

            /**
             * Get the test results directory.
             *
             * @return The test results directory.
             */
            public File getTestResultsDirectory() {
                return testResultsDirectory;
            }

            /**
             * Get the programs.
             *
             * @return The programs.
             */
            public Map<String, CCLCoverageProgram> getPrograms() {
                return programs;
            }

            /**
             * Get the test programs.
             *
             * @return The test programs.
             */
            public Map<String, CCLCoverageProgram> getTests() {
                return tests;
            }

            @Override
            public String toString() {
                return ToStringBuilder.reflectionToString(this);
            }
        }

        private final Map<File, Map<String, CCLCoverageProgram>> sourcePrograms = new HashMap<File, Map<String, CCLCoverageProgram>>();
        private final Map<File, Map<String, CCLCoverageProgram>> testPrograms = new HashMap<File, Map<String, CCLCoverageProgram>>();
        private final List<LoadInvocation> invocations = new ArrayList<LoadInvocation>();
        private boolean canGenerateReport;

        @Override
        public boolean canGenerateReport() {
            return canGenerateReport;
        }

        /**
         * Get all invocations of {@link #loadCoverage(File, Map, Map)}.
         *
         * @return A {@link List} of {@link LoadInvocation} objects representing invocations of the method.
         */
        @SuppressWarnings("javadoc")
        public List<LoadInvocation> getInvocations() {
            return invocations;
        }

        /**
         * Set whether or not this mojo can generate reports.
         *
         * @param canGenerateReport
         *            A {@code boolean} dictating whether or not reports can be generated.
         */
        public void setCanGenerateReport(final boolean canGenerateReport) {
            this.canGenerateReport = canGenerateReport;
        }

        /**
         * Set the source programs for a directory.
         *
         * @param programListingsDirectory
         *            The directory whose source programs are to be set.
         * @param sourcePrograms
         *            The source programs to be set.
         */
        public void setSourcePrograms(final File programListingsDirectory, final Map<String, CCLCoverageProgram> sourcePrograms) {
            this.sourcePrograms.put(programListingsDirectory, sourcePrograms);
        }

        /**
         * Set the test programs for a directory.
         *
         * @param testResultsDirectory
         *            The directory whose test programs are to be set.
         * @param testPrograms
         *            The test programs to be set.
         */
        public void setTestPrograms(final File testResultsDirectory, final Map<String, CCLCoverageProgram> testPrograms) {
            this.testPrograms.put(testResultsDirectory, testPrograms);
        }

        @Override
        void loadCoverage(final File testResultsDirectory, final Map<String, CCLCoverageProgram> programs,
                final Map<String, CCLCoverageProgram> tests) {
            invocations.add(new LoadInvocation(testResultsDirectory, programs, tests));
        }

        @Override
        Map<String, CCLCoverageProgram> loadSourcePrograms(final File programListingsDirectory) {
            return sourcePrograms.get(programListingsDirectory);
        }

        @Override
        Map<String, CCLCoverageProgram> loadTestPrograms(final File testResultsDirectory) {
            return testPrograms.get(testResultsDirectory);
        }
    }
}
