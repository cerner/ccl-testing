package com.cerner.ccl.testing.maven.ccl.reports;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.reporting.MavenReportException;

import com.cerner.ccl.testing.maven.ccl.reports.common.CCLCoverageProgram;
import com.cerner.ccl.testing.maven.ccl.reports.common.ReportErrorLogger;

/**
 * Goal that generates the code coverage for tests executed through the CCL Automated Testing Framework
 *
 * @author Jeff Wiedemann
 */
@Mojo(name = "coverage-report", defaultPhase = LifecyclePhase.SITE)

public class CCLCoverageMojo extends AbstractCCLMavenReport {
    /**
     * The parent directory for the XML report files for program listings that will be parsed and rendered to HTML
     * format.
     *
     */
    @Parameter(defaultValue = "${project.basedir}/target/program-listings/", required = true)
    private File programListingsDirectory;

    /**
     * Requires that the test-results and program-listings folders be present before the report can be generated.
     */
    @Override
    public boolean canGenerateReport() {
        if (!testResultsDirectory.exists() || !testResultsDirectory.isDirectory()) {
            getLog().info("Cannot generate CCL Coverage report due to missing test-results directory");
            return false;
        }

        if (!programListingsDirectory.exists() || !programListingsDirectory.isDirectory()) {
            getLog().info("Cannot generate CCL Coverage report due to missing program-listings directory");
            return false;
        }

        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription(final Locale locale) {
        return "Reports code coverage for automated CCL tests";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName(final Locale locale) {
        return "CCL Coverage Report";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getOutputName() {
        return "ccl-coverage-report";
    }

    @Override
    public boolean isExternalReport() {
        return true;
    }

    /**
     * Entry point to the Mojo which generates the ccl-coverage report files.
     */
    @Override
    protected void executeReport(final Locale local) throws MavenReportException {
        if (!canGenerateReport()) {
            return;
        }

        // Instantiate the error logger object which will be passed to the report generator to log any
        // errors to a special folder
        final ReportErrorLogger e = new ReportErrorLogger(reportErrorDirectory);

        // Parse the listing XMLs for the test program(s) into objects to be queried later
        final Map<String, CCLCoverageProgram> testPrograms = loadTestPrograms(testResultsDirectory);

        // Parse the listing XMLs for the source code program(s) into objects to be queried later
        final Map<String, CCLCoverageProgram> sourcePrograms = loadSourcePrograms(programListingsDirectory);

        // For each source program, locate all tests that have tested that source program and associate that
        // tests coverage to the source program
        loadCoverage(testResultsDirectory, sourcePrograms, testPrograms);

        // Generate the coverage report given the source and test programs
        new CCLCoverageReportGenerator(outputDirectory, testPrograms.values(), sourcePrograms.values(), e)
                .generateReport();
    }

    /**
     * This routine performs the (less than straight forward) task of marrying the coverage xml files with the programs
     * that they test and then subsequently add the coverage to the program via the addCoverage() routine. Once all of
     * the coverage information is added the programs can be later queried for coverage information <br>
     * This method is purposefully left as package-private to expose it for testing.
     *
     * @param testResultsDirectory
     *            The directory which stores test results as well as coverage information for the source programs
     * @param programs
     *            The collection of source programs from /src/main/ccl
     * @param tests
     *            The collection of test program from /src/test/ccl
     * @throws MavenReportException
     *             When the xml files cannot be read or parsed and interpreted as coverage data
     */
    void loadCoverage(final File testResultsDirectory, final Map<String, CCLCoverageProgram> programs,
            final Map<String, CCLCoverageProgram> tests) throws MavenReportException {
        File[] directoryList = testResultsDirectory.listFiles();
        if (directoryList == null) {
            return;
        }
        for (File dir : directoryList) {
            // If this directory contains a listing.xml file then I know it's a valid unit test directory
            if (AbstractCCLMavenReport.getDirectoryFile(dir, "listing.xml") != null) {
                // Lets get the appropriate testProgram for this test-result folder
                CCLCoverageProgram testProgram = tests.get(dir.getName());

                if (testProgram == null) {
                    throw new MavenReportException(
                            "Failed to get the test program object for test folder " + dir.getName());
                }

                // Find the coverage sub-directory
                File[] subdirectoryList = dir.listFiles();
                if (subdirectoryList == null) {
                    continue;
                }
                for (File subdir : subdirectoryList) {
                    if (subdir.isDirectory() && subdir.getName().equals("coverage")) {
                        // Okay, so we now have the coverage sub-directory, lets spin through it and grab
                        // all of the coverage xml files and try to associate them to the program they
                        // are covering
                        File[] fileList = subdir.listFiles();
                        if (fileList == null) {
                            continue;
                        }
                        for (File f : fileList) {
                            if (f.isFile() && f.getName().endsWith(".xml")) {
                                String coverageXML;
                                try {
                                    coverageXML = FileUtils.readFileToString(f, "utf-8");
                                } catch (IOException e) {
                                    throw new MavenReportException("Failed to read xml file " + f.getName(), e);
                                }

                                if (f.getName().equals("test-coverage.xml")) {
                                    // If this is the special coverage file for the test program then
                                    // add the coverage info to the test program
                                    testProgram.addCoverage(testProgram, coverageXML);
                                } else {
                                    // This is a coverage file for a source program, so find the correct
                                    // source program and add this coverage to it
                                    CCLCoverageProgram sourceProgram = programs.get(f.getName().replace(".xml", ""));
                                    if (sourceProgram == null) {
                                        throw new MavenReportException(
                                                "Expected a corresponding program listing for the coverage file "
                                                        + f.getAbsolutePath());
                                    }

                                    // Add the coverage information to the source program
                                    sourceProgram.addCoverage(testProgram, coverageXML);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Given the programListingsDirectory, this routine will scan all xml files and attempt to parse them into
     * CCLCoveragePrograms where they can later be queried for coverage statistics <br>
     * This method is purposefully left as package-private to expose it for testing.
     *
     * @param programListingsDirectory
     *            The directory holding one or more program_name.xml files
     * @return The collection of programs to be returned
     * @throws MavenReportException
     *             When an XML file cannot be read or parsed into a CCLCoverageProgram object
     */
    Map<String, CCLCoverageProgram> loadSourcePrograms(final File programListingsDirectory)
            throws MavenReportException {
        final Map<String, CCLCoverageProgram> programs = new HashMap<String, CCLCoverageProgram>();
        File[] fileList = programListingsDirectory.listFiles();
        if (fileList != null) {
            for (File program : fileList) {
                if (program.getName().endsWith(".xml")) {
                    String listingXML;
                    try {
                        listingXML = FileUtils.readFileToString(program, "utf-8");
                    } catch (IOException e) {
                        throw new MavenReportException("Failed to read program listing XML due to error", e);
                    }

                    programs.put(program.getName().replace(".xml", ""), new CCLCoverageProgram(listingXML));
                }
            }
        }
        return programs;
    }

    /**
     * Given the testResultsDirectory, this routine will scan all xml files and attempt to parse them into
     * CCLCoveragePrograms where they can later be queried for coverage statistics <br>
     * This method is purposefully left as package-private to expose it for testing.
     *
     * @param testResultsDirectory
     *            The directory holding the listings xml files for the test programs
     * @return The collection of test programs to be returned
     * @throws MavenReportException
     *             When an XML file cannot be read or parsed into a CCLCoverageProgram object
     */
    Map<String, CCLCoverageProgram> loadTestPrograms(final File testResultsDirectory) throws MavenReportException {
        Map<String, CCLCoverageProgram> tests = new HashMap<String, CCLCoverageProgram>();

        File[] directoryList = testResultsDirectory.listFiles();
        if (directoryList == null) {
            return tests;
        }
        for (File dir : directoryList) {
            final File listingFile = AbstractCCLMavenReport.getDirectoryFile(dir, "listing.xml");
            if (listingFile != null) {
                String listingXML;
                try {
                    listingXML = FileUtils.readFileToString(listingFile, "utf-8");
                } catch (IOException e) {
                    throw new MavenReportException(
                            "Failed to read test program listing XML from directory " + dir.getName() + " due to error",
                            e);
                }
                tests.put(dir.getName(), new CCLCoverageProgram(listingXML));
            }
        }

        return tests;
    }
}
