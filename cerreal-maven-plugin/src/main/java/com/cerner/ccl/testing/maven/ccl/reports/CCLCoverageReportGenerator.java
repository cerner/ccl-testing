package com.cerner.ccl.testing.maven.ccl.reports;

import com.cerner.ccl.testing.maven.ccl.reports.common.CCLCoverageProgram;
import com.cerner.ccl.testing.maven.ccl.reports.common.CoverageLine;
import com.cerner.ccl.testing.maven.ccl.reports.common.CoveredStatus;
import com.cerner.ccl.testing.maven.ccl.reports.common.ReportErrorLogger;
import com.cerner.ccl.testing.xsl.XslAPI;
import com.cerner.ccl.testing.xsl.XslAPIException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Locale;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.reporting.MavenReportException;

/**
 * A utility class to generate the coverage report.
 *
 * @author Jeff Wiedemann
 */

// TODO: rewrite using jdom or, even better, JAXB?
public class CCLCoverageReportGenerator {
    private final File outputDirectory;
    private final Collection<CCLCoverageProgram> testPrograms;
    private final Collection<CCLCoverageProgram> sourcePrograms;
    private final ReportErrorLogger errorLogger;
    private boolean includeTestCaseSourceCoverage;

    /**
     * Create a report generator.
     *
     * @param outputDirectory
     *            The directory to which the output is to be written.
     * @param testPrograms
     *            A {@link Collection} of {@link CCLCoverageProgram} representing the test programs that were executed.
     * @param sourcePrograms
     *            A {@link Collection} of {@link CCLCoverageProgram} representing the programs that were tested.
     * @param errorLogger
     *            A {@link ReportErrorLogger} used to report errors.
     */
    public CCLCoverageReportGenerator(final File outputDirectory, final Collection<CCLCoverageProgram> testPrograms,
            final Collection<CCLCoverageProgram> sourcePrograms, final ReportErrorLogger errorLogger) {
        this.outputDirectory = outputDirectory;
        this.sourcePrograms = sourcePrograms;
        this.testPrograms = testPrograms;
        this.errorLogger = errorLogger;
        this.includeTestCaseSourceCoverage = true;
    }

    /**
     * Specify whether to include code coverage for test case source code in the coverage report.
     *
     * @param include
     *            A boolean value indicating whether to include the coverage (true) or not (false).
     * @return this pointer to support the builder pattern.
     */
    public CCLCoverageReportGenerator withTestCaseSourceCoverage(final boolean include) {
        this.includeTestCaseSourceCoverage = include;
        return this;
    }

    /**
     * Generate the report.
     *
     * @throws MavenReportException
     *             If any errors occur during the report generation.
     */
    public void generateReport() throws MavenReportException {
        // Creates the main dashboard view for the code coverage report
        createDashboardFile(new File(outputDirectory.getAbsolutePath() + "/ccl-coverage-report.html"));

        // Creates the various source files displaying coverage for certain scenarios linked from the
        // dashboard
        createSourceCoverageFiles();

        // Writes out the css files used by this report to render HTML pages
        deployStylesheets();
        deployJavascript();
    }

    /**
     * Create the dashboard file, which presents an overview and summary of all coverage data.
     *
     * @param dashboardFile
     *            A {@link File} representing the location on the hard disk to where the file is to be written.
     * @throws MavenReportException
     *             If any errors occur during the dashboard generation.
     */
    private void createDashboardFile(final File dashboardFile) throws MavenReportException {
        String summaryXML;
        String dashboardXSLT;
        String dashboardHTML;

        summaryXML = createSummaryXML();
        dashboardXSLT = getResourceAsString("xslt/codeCoverageDashboard.xslt");

        try {
            dashboardHTML = XslAPI.transform(summaryXML, dashboardXSLT, null);
        } catch (XslAPIException e) {
            errorLogger.logFailedTransformation(summaryXML, dashboardXSLT);
            throw new MavenReportException("Failed to get create dashboard html due to error", e);
        }

        try {
            FileUtils.writeStringToFile(dashboardFile, dashboardHTML, "utf-8");
        } catch (IOException e) {
            throw new MavenReportException("Failed to write to dashboard file due to error", e);
        }
    }

    /**
     * Create the individual reports of code coverage.
     *
     * @throws MavenReportException
     *             If any errors occur during the report generation.
     */
    private void createSourceCoverageFiles() throws MavenReportException {
        for (CCLCoverageProgram p : testPrograms) {
            createProgramReport(p, null, true);
        }

        for (CCLCoverageProgram p : sourcePrograms) {
            createProgramReport(p, null, true);
            createProgramReport(p, null, false);

            if (includeTestCaseSourceCoverage) {
                for (CCLCoverageProgram tp : testPrograms) {
                    if (p.wasTestedBy(tp)) {
                        createProgramReport(p, tp, true);
                        createProgramReport(p, tp, false);
                    }
                }
            }
        }
    }

    /**
     * Create the name of the file to which the coverage data file should be written.
     *
     * @param program
     *            A {@link CCLCoverageProgram} representing the program for which the filename is to be created.
     * @param testProgram
     *            A {@link CCLCoverageProgram} representing the test case, if any, for which the filename is to be
     *            generated. If {@code null}, then the filename of an aggregate report will be created.
     * @param withIncludes
     *            A {@code boolean} to indicate whether or not this is the filename of a report that includes include
     *            file source.
     * @return The name of a report for the given inputs.
     */
    private String createCoverageProgramFileName(final CCLCoverageProgram program, final CCLCoverageProgram testProgram,
            final boolean withIncludes) {
        String fileName = "ccl-coverage-reports/" + program.getName().toLowerCase(Locale.getDefault())
                + (withIncludes == true ? "-wi-" : "-woi-");

        if (testProgram == null) {
            fileName += "aggregate.html";
        } else {
            fileName += testProgram.getName().toLowerCase(Locale.getDefault()) + ".html";
        }

        return fileName;
    }

    /**
     * Create the report of a program's coverage.
     *
     * @param program
     *            A {@link CCLCoverageProgram} representing the program for which a coverage report is to be generated.
     * @param testProgram
     *            A {@link CCLCoverageProgram} representing the test program for which the coverage report is to be
     *            generated; if {@code null}, this will be an aggregate report.
     * @param withIncludes
     *            A {@code boolean} value; if {@code true}, then include source files will be considered in the report;
     *            if {@code false}, they will not be considered.
     * @throws MavenReportException
     *             If any errors occur in the report generation.
     */
    private void createProgramReport(final CCLCoverageProgram program, final CCLCoverageProgram testProgram,
            final boolean withIncludes) throws MavenReportException {
        File programFile = new File(outputDirectory.getAbsolutePath() + "/"
                + createCoverageProgramFileName(program, testProgram, withIncludes));

        String programXML;
        String programXSLT;
        String programHTML;

        programXML = createProgramCoverageXML(program, testProgram, withIncludes);
        programXSLT = getResourceAsString("xslt/codeCoverageProgram.xslt");

        try {
            programHTML = XslAPI.transform(programXML, programXSLT, null);
        } catch (XslAPIException e) {
            errorLogger.logFailedTransformation(programXML, programXSLT);
            throw new MavenReportException("Failed to get create coverage program html due to error", e);
        }

        try {
            FileUtils.writeStringToFile(programFile, programHTML, "utf-8");
        } catch (IOException e) {
            throw new MavenReportException("Failed to write to coverage program file due to error", e);
        }
    }

    /**
     * Creates the xml representation of this program given the passed in test program and withIncludes variable.
     * Generated XML will be in the form program name lines line number text coveredStatus
     *
     * @param program
     *            The CCL program object with coverage information to create the XML for
     * @param testProgram
     *            This can either be valued or null. If null the coverage XML will indicate coverage for all tests which
     *            are testing the program instead of just coverage for a single test
     * @param withIncludes
     *            When set to true, lines/line will include source code that was included into the programs source via a
     *            %i include, otherwise when false, %i code is omitted from the coverage XML
     * @return The coverage XML for the program given the context of the testProgram and withIncludes parameters
     */
    private String createProgramCoverageXML(final CCLCoverageProgram program, final CCLCoverageProgram testProgram,
            final boolean withIncludes) {

        StringBuilder xml = new StringBuilder("<program><lines>");

        writeElement(xml, "name", program.getName());
        String lastOrigin = "PROGRAM";
        for (CoverageLine l : program.getCoverageLines()) {
            if (withIncludes || l.getSourceCodeOrigin().equals("PROGRAM")) {
                xml.append("<line>");
                writeElement(xml, "number", l.getLineNumber());
                writeCDataElement(xml, "text", l.getSourceCode());
                writeElement(xml, "coveredStatus", l.getCoveredStatusByTestCase(testProgram).toString());
                xml.append("</line>");
            } else {
                if (!l.getSourceCodeOrigin().equals(lastOrigin)) {
                    // Include a bogus program line for the %i include and give it a status of not covered.
                    // After
                    // that, all lines from that include will be skipped until the origin is reverted back to
                    // the main
                    // source program
                    xml.append("<line>");
                    writeElement(xml, "number", l.getLineNumber());
                    writeCDataElement(xml, "text", l.getSourceCodeOrigin());
                    writeElement(xml, "coveredStatus", CoveredStatus.NOT_EXECUTABLE.toString());
                    xml.append("</line>");
                }
                lastOrigin = l.getSourceCodeOrigin();
            }
        }
        xml.append("</lines>");

        xml.append("</program>");

        return xml.toString();
    }

    /**
     * Creates an XML representation of all programs, and tests which have been tested and what the code coverage for
     * those tests are. The XML is returned in the following format
     *
     * <p>
     * coverageSummary testPrograms testProgram name totalLines coverage covered notCovered linkURL sourcePrograms
     * sourceProgram name withIncludes totalLines coverage aggregate covered notCovered linkURL tests test name covered
     * notCovered linkURL withoutIncludes totalLines coverage aggregate covered notCovered linkURL tests test name
     * covered notCovered linkURL
     *
     * @return The coverage summary XML
     */
    // TODO: this could be done using jdom or JAXB
    private String createSummaryXML() {
        StringBuilder xml = new StringBuilder();

        xml.append("<coverageSummary>");
        xml.append("<skipTestSourceCoverage>").append(!includeTestCaseSourceCoverage)
                .append("</skipTestSourceCoverage>");

        xml.append("<testPrograms>");
        for (CCLCoverageProgram p : testPrograms) {
            xml.append("<testProgram>");

            writeElement(xml, "name", p.getName());
            writeElement(xml, "totalLines", p.getTotalProgramLines(true));

            xml.append("<coverage>");
            writeElement(xml, "covered", p.getCoverageTotalOf(CoveredStatus.COVERED, true, null));
            writeElement(xml, "notCovered", p.getCoverageTotalOf(CoveredStatus.NOT_COVERED, true, null));
            writeElement(xml, "linkURL", createCoverageProgramFileName(p, null, true));
            xml.append("</coverage>");

            xml.append("</testProgram>");
        }
        xml.append("</testPrograms>");

        xml.append("<sourcePrograms>");
        for (CCLCoverageProgram p : sourcePrograms) {
            xml.append("<sourceProgram>");

            writeElement(xml, "name", p.getName());

            // Basically, loop twice, once to generate the withIncludes XML and once
            // to generate the withoutIncludes XML... it's so similar it can be done
            // in a for loop with slight flexing per iteration
            for (int i = 0; i < 2; i++) {
                xml.append(i == 0 ? "<withIncludes>" : "<withoutIncludes>");

                writeElement(xml, "totalLines", p.getTotalProgramLines(i == 0));

                xml.append("<coverage>");
                xml.append("<aggregate>");
                writeElement(xml, "covered", p.getCoverageTotalOf(CoveredStatus.COVERED, i == 0, null));
                writeElement(xml, "notCovered", p.getCoverageTotalOf(CoveredStatus.NOT_COVERED, i == 0, null));
                writeElement(xml, "linkURL", createCoverageProgramFileName(p, null, i == 0));
                xml.append("</aggregate>");

                xml.append("<tests>");
                for (CCLCoverageProgram tp : testPrograms) {
                    if (p.wasTestedBy(tp)) {
                        xml.append("<test>");
                        writeElement(xml, "name", tp.getName());
                        writeElement(xml, "covered", p.getCoverageTotalOf(CoveredStatus.COVERED, i == 0, tp));
                        writeElement(xml, "notCovered", p.getCoverageTotalOf(CoveredStatus.NOT_COVERED, i == 0, tp));
                        writeElement(xml, "linkURL", createCoverageProgramFileName(p, tp, i == 0));
                        xml.append("</test>");
                    }
                }
                xml.append("</tests>");

                xml.append("</coverage>");

                xml.append(i == 0 ? "</withIncludes>" : "</withoutIncludes>");
            }

            xml.append("</sourceProgram>");
        }
        xml.append("</sourcePrograms>");

        xml.append("</coverageSummary>");
        return xml.toString();
    }

    /**
     * Write an XML element.
     *
     * @param xml
     *            A {@link StringBuilder} to which the XML tag will be written.
     * @param name
     *            The name of the XML tag to be written.
     * @param value
     *            The value to be written.
     */
    private void writeElement(final StringBuilder xml, final String name, final int value) {
        writeElement(xml, name, String.valueOf(value));
    }

    /**
     * Write an XML element.
     *
     * @param xml
     *            A {@link StringBuilder} to which the XML tag will be written.
     * @param name
     *            The name of the XML tag to be written.
     * @param value
     *            The value to be written.
     */
    private void writeElement(final StringBuilder xml, final String name, final String value) {
        xml.append("<");
        xml.append(name);
        xml.append(">");

        xml.append(value);

        xml.append("</");
        xml.append(name);
        xml.append(">");
    }

    /**
     * Write an XML element, with the value contained in a CDATA tag.
     *
     * @param xml
     *            A {@link StringBuilder} to which the XML tag will be written.
     * @param name
     *            The name of the XML tag to be written.
     * @param value
     *            The value to be written.
     */
    private void writeCDataElement(final StringBuilder xml, final String name, final String value) {
        xml.append("<");
        xml.append(name);
        xml.append(">");

        xml.append("<![CDATA[");

        xml.append(value);

        xml.append("]]>");

        xml.append("</");
        xml.append(name);
        xml.append(">");
    }

    /**
     * Get a classpath resource as a string of data.
     *
     * @param name
     *            The name of the resource to be read.
     * @return The request resource as a string.
     * @throws MavenReportException
     *             If any errors occur during the reading of the resource.
     */
    private String getResourceAsString(final String name) throws MavenReportException {
        try {
            InputStream resourceStream = getClass().getResourceAsStream("/" + name);
            return IOUtils.toString(resourceStream, "utf-8");
        } catch (IOException e) {
            throw new MavenReportException("Failed to get the resource " + name + " due to error", e);
        }
    }

    /**
     * Write the CSS stylesheets to the hard disk.
     *
     * @throws MavenReportException
     *             If any errors occur during the write-out.
     */
    private void deployStylesheets() throws MavenReportException {
        File cssFile;

        try {
            cssFile = new File(
                    outputDirectory.getAbsolutePath() + "/ccl-coverage-reports/css/codeCoverageDashboard.css");
            FileUtils.writeStringToFile(cssFile, getResourceAsString("css/codeCoverageDashboard.css"), "utf-8");

            cssFile = new File(outputDirectory.getAbsolutePath() + "/ccl-coverage-reports/css/codeCoverageProgram.css");
            FileUtils.writeStringToFile(cssFile, getResourceAsString("css/codeCoverageProgram.css"), "utf-8");

            cssFile = new File(outputDirectory.getAbsolutePath() + "/ccl-coverage-reports/css/progress-polyfill.css");
            FileUtils.writeStringToFile(cssFile, getResourceAsString("css/progress-polyfill.css"), "utf-8");
        } catch (IOException e) {
            throw new MavenReportException("Failed to deploy CCL Code Coverage CSS files due to error", e);
        }
    }

    /**
     * Copy the javascript to the target.
     *
     * @throws MavenReportException
     *             If any errors occur while copying the files.
     */
    private void deployJavascript() throws MavenReportException {
        File file;

        try {
            file = new File(outputDirectory.getAbsolutePath() + "/ccl-coverage-reports/js/progress-polyfill.js");
            FileUtils.writeStringToFile(file, getResourceAsString("js/progress-polyfill.js"), "utf-8");
        } catch (final IOException e) {
            throw new MavenReportException("Failed to deploy Cerreal JS files", e);
        }
    }
}
