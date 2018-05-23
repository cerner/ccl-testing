package com.cerner.ccltesting.maven.ccl.reports;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.util.Locale;

import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.plugin.logging.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.cerner.ccltesting.maven.ccl.reports.common.ReportErrorLogger;
import com.cerner.ccltesting.maven.ccl.reports.common.ResultsTestSuite;

/**
 * Unit tests for {@link CerrealMojo}.
 * 
 * @author Joshua Hyde
 * 
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { CerrealMojo.class, CerrealReportGenerator.class, ReportErrorLogger.class, ResultsTestSuite.class })
public class CerrealMojoTest {
    private CerrealMojo mojo = new CerrealMojo();

    /**
     * If the testing directory exists and is a directory, then the mojo should be able to generate the report.
     */
    @Test
    public void testCanGenerateReport() {
        final File directory = mock(File.class);
        when(directory.exists()).thenReturn(Boolean.TRUE);
        when(directory.isDirectory()).thenReturn(Boolean.TRUE);
        mojo.testResultsDirectory = directory;
        assertThat(mojo.canGenerateReport()).isTrue();
    }

    /**
     * If the test-results directory exists but is not a directory, then the mojo should not be able to generate the report.
     */
    @Test
    public void testCanGenerateReportNotDirectory() {
        final Log log = mock(Log.class);
        final File directory = mock(File.class);
        when(directory.exists()).thenReturn(Boolean.TRUE);
        when(directory.isDirectory()).thenReturn(Boolean.FALSE);
    
        mojo.testResultsDirectory = directory;
        mojo.setLog(log);
    
        assertThat(mojo.canGenerateReport()).isFalse();
        verify(log).info("Cannot generate Cerreal report due to missing test-results directory");
    }

    /**
     * If the set test results directory does not exist, then the mojo cannot generate the report.
     */
    @Test
    public void testCanGenerateReportNotExists() {
        final Log log = mock(Log.class);
        final File directory = mock(File.class);
        when(directory.exists()).thenReturn(Boolean.FALSE);
    
        mojo.testResultsDirectory = directory;
        mojo.setLog(log);
    
        assertThat(mojo.canGenerateReport()).isFalse();
        verify(log).info("Cannot generate Cerreal report due to missing test-results directory");
    }

    /**
     * Test the retrieval of the description.
     */
    @Test
    public void testGetDescription() {
        assertThat(mojo.getDescription(Locale.getDefault())).isEqualTo("Reports test results for automated CCL tests");
    }

    /**
     * Test the retrieval of the name.
     */
    @Test
    public void testGetName() {
        assertThat(mojo.getName(Locale.getDefault())).isEqualTo("Cerreal Report");
    }

    /**
     * Test the retrieval of the output name.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetOutputName() throws Exception {
        final String outputName = "output name";
        Whitebox.setInternalState(mojo, "outputName", outputName);
        assertThat(mojo.getOutputName()).isEqualTo(outputName);
    }

    /**
     * Test the generation of the report.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecuteReport() throws Exception {
        final File reportErrorDirectory = mock(File.class);
        final File outputDirectory = mock(File.class);
        final File testResultsDirectory = mock(File.class);
        when(testResultsDirectory.exists()).thenReturn(Boolean.TRUE);
        when(testResultsDirectory.isDirectory()).thenReturn(Boolean.TRUE);

        final ReportErrorLogger logger = mock(ReportErrorLogger.class);
        whenNew(ReportErrorLogger.class).withArguments(reportErrorDirectory).thenReturn(logger);

        final ResultsTestSuite suite = mock(ResultsTestSuite.class);
        whenNew(ResultsTestSuite.class).withArguments(testResultsDirectory).thenReturn(suite);

        final CerrealReportGenerator generator = mock(CerrealReportGenerator.class);
        whenNew(CerrealReportGenerator.class).withArguments(suite, outputDirectory, logger).thenReturn(generator);

        mojo.reportErrorDirectory = reportErrorDirectory;
        mojo.outputDirectory = outputDirectory;
        mojo.testResultsDirectory = testResultsDirectory;
        
        final Sink sink = mock(Sink.class);
        Whitebox.setInternalState(mojo, "sink", sink);

        mojo.executeReport(Locale.getDefault());
        
        verify(generator).generateReport(sink);
    }

    /**
     * If the mojo cannot generate the report, then it should not attempt.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecuteReportCannotGenerate() throws Exception {
        final File reportErrorDirectory = mock(File.class);
        final File testResultsDirectory = mock(File.class);
        when(testResultsDirectory.exists()).thenReturn(Boolean.FALSE);

        whenNew(ReportErrorLogger.class).withArguments(reportErrorDirectory).thenThrow(new AssertionError("The report error logger should never be instantiated."));

        mojo.reportErrorDirectory = reportErrorDirectory;
        mojo.testResultsDirectory = testResultsDirectory;

        // The absence of an AssertionError can be understood to mean that the report execution was aborted
        mojo.executeReport(Locale.getDefault());
    }
}
