package com.cerner.ccltesting.maven.ccl.reports;

import java.util.Locale;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.reporting.MavenReportException;

import com.cerner.ccltesting.maven.ccl.reports.common.ReportErrorLogger;
import com.cerner.ccltesting.maven.ccl.reports.common.ResultsTestSuite;

/**
 * Goal that generates the test results report for tests executed through the CCL Automated Testing Framework
 *
 * @author Jeff Wiedemann
 *
 */
@Mojo(name = "test-report", defaultPhase = LifecyclePhase.SITE)
public class CerrealMojo extends AbstractCCLMavenReport {
    /**
     * The filename to use for the report.
     *
     */
    @Parameter(property = "outputName", defaultValue = "cerreal-report", required = true)
    private String outputName;

    /**
     * This method will return true if the test-results directory exists. If the directory does not exist, it is most likely because the test phase was not run, or was cleaned since the last run
     *
     * @see org.apache.maven.reporting.AbstractMavenReport#canGenerateReport()
     */
    @Override
    public boolean canGenerateReport() {
        if (!testResultsDirectory.exists() || !testResultsDirectory.isDirectory()) {
            getLog().info("Cannot generate Cerreal report due to missing test-results directory");
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription(Locale locale) {
        return "Reports test results for automated CCL tests";
    }

    /**
     * {@inheritDoc}
     */
    public String getName(Locale locale) {
        return "Cerreal Report";
    }

    /**
     * {@inheritDoc}
     */
    public String getOutputName() {
        return outputName;
    }

    /**
     * Entry point to the Mojo which generates the cerreal report files
     *
     * @see org.apache.maven.reporting.AbstractMavenReport#executeReport(java.util.Locale)
     * @throws MavenReportException
     *             When the reports cannot be generated correctly
     */
    @Override
    protected void executeReport(Locale local) throws MavenReportException {
        if (!canGenerateReport())
            return;

        // Instantiate the error logger object which will be passed to the report generator to log any
        // errors to a special folder
        final ReportErrorLogger e = new ReportErrorLogger(reportErrorDirectory);

        // Using the test-results folder and files returned from testing, parse all of the files and turn
        // them into a suite object which contains test case objects representing the results of the test
        final ResultsTestSuite suite = new ResultsTestSuite(testResultsDirectory);

        // Generate the report for the given test results
        new CerrealReportGenerator(suite, outputDirectory, e).generateReport(getSink());
    }
}
