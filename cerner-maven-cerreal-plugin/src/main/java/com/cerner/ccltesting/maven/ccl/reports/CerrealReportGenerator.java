package com.cerner.ccltesting.maven.ccl.reports;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.doxia.markup.HtmlMarkup;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.sink.SinkEventAttributes;
import org.apache.maven.doxia.sink.impl.SinkEventAttributeSet;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cerner.ccltesting.maven.ccl.reports.common.ReportErrorLogger;
import com.cerner.ccltesting.maven.ccl.reports.common.ResultsTest;
import com.cerner.ccltesting.maven.ccl.reports.common.ResultsTest.TestResult;
import com.cerner.ccltesting.maven.ccl.reports.common.ResultsTestCase;
import com.cerner.ccltesting.maven.ccl.reports.common.ResultsTestSuite;
import com.cerner.ccltesting.xsl.StringURIResolver;
import com.cerner.ccltesting.xsl.XslAPI;
import com.cerner.ccltesting.xsl.XslAPIException;

/**
 * Class to convert the files stored target into the Cerreal report. This report shows unit test results.
 *
 * @author Jeff Wiedemann
 */

public class CerrealReportGenerator {
    private final ResultsTestSuite suite;
    private final File outputDirectory;
    private final ReportErrorLogger errorLogger;

    private static final Logger logger = LoggerFactory.getLogger(CerrealReportGenerator.class);

    /**
     * Constructor for the report generator
     *
     * @param suite
     *            A {@link ResultsTestSuite} describing the suite of tests that were executed and their results.
     * @param outputDirectory
     *            A {@link File} representing the directory to which the output should be written.
     * @param errorLogger
     *            An instance of the ReportErrorLogger such that any errors can caught and better logged out
     */
    public CerrealReportGenerator(final ResultsTestSuite suite, final File outputDirectory,
            final ReportErrorLogger errorLogger) {
        this.outputDirectory = outputDirectory;
        this.suite = suite;
        this.errorLogger = errorLogger;
    }

    /**
     * Generates the unit test results reports based on the results of the CCL unit testing framework
     *
     * @param sink
     *            The sink to which the report will be dumped
     *
     * @throws MavenReportException
     *             When any unexpected errors occur which prevent the report files from being created correctly
     */
    public void generateReport(final Sink sink) throws MavenReportException {
        constructPageHeadSection(sink);

        constructSummarySection(sink, suite);

        sink.sectionTitle2();
        sink.text("Test Cases");
        sink.sectionTitle2_();
        sink.anchor("Test_Cases");
        sink.anchor_();

        for (final ResultsTestCase tc : suite.getTestCases()) {
            constructTestCaseSection(sink, tc);
            writeStandaloneTestCasePage(tc);
        }

        deployStylesheets();
        deployJavascript();
    }

    /**
     * Create the {@code <head />} portion of the page.
     *
     * @param sink
     *            The {@link Sink} to be used to generate the head.
     */
    private void constructPageHeadSection(final Sink sink) {
        sink.head();

        sink.title();
        sink.text("Cerreal Report");
        sink.title_();

        SinkEventAttributeSet attrs = new SinkEventAttributeSet();
        attrs.addAttribute(SinkEventAttributes.SRC, "cerreal-reports/js/cerreal.js");
        sink.unknown("script", new Object[] { Integer.valueOf(HtmlMarkup.TAG_TYPE_START) }, attrs);
        sink.unknown("script", new Object[] { Integer.valueOf(HtmlMarkup.TAG_TYPE_END) }, null);

        sink.head_();
    }

    /**
     * Create the summary section of the report.
     *
     * @param sink
     *            The {@link Sink} used to generate the section.
     * @param suite
     *            A {@link ResultsTestSuite} representing the data to be summarized.
     */
    private void constructSummarySection(final Sink sink, final ResultsTestSuite suite) {
        sink.section1();
        sink.sectionTitle1();
        sink.text("Cerreal Report");
        sink.sectionTitle1_();

        sink.sectionTitle2();
        sink.text("Summary");
        sink.sectionTitle2_();
        sink.anchor("Summary");
        sink.anchor_();

        sink.table();

        sink.tableRow();
        sinkHeader(sink, "Tests");
        sinkHeader(sink, "Failures");
        sinkHeader(sink, "Errors");
        sinkHeader(sink, "Passed");
        sinkHeader(sink, "Passed Percentage");
        sink.tableRow_();

        sink.tableRow();
        sinkCell(sink, String.valueOf(suite.getTestCount()));
        sinkCell(sink, String.valueOf(suite.getFailedCount()));
        sinkCell(sink, String.valueOf(suite.getErroredCount()));
        sinkCell(sink, String.valueOf(suite.getPassedCount()));
        sinkCell(sink,
                NumberFormat.getPercentInstance().format(suite.getPassedCount() / (double) suite.getTestCount()));
        sink.tableRow_();

        sink.table_();

        if (suite.getCclEnvironment().isDataAvailable()) {
            sink.sectionTitle2();
            sink.text("Environment");
            sink.sectionTitle2_();
            sink.anchor("Environment");
            sink.anchor_();

            sink.table();

            sink.tableRow();
            sinkHeader(sink, "Host");
            sinkHeader(sink, "Host User");
            sinkHeader(sink, "Domain");
            sinkHeader(sink, "Domain User");
            sinkHeader(sink, "CCL Group");
            sinkHeader(sink, "CCL Version");
            sinkHeader(sink, "Framework Version");
            sink.tableRow_();

            sink.tableRow();
            sinkCell(sink, String.valueOf(suite.getCclEnvironment().getNodeName()));
            sinkCell(sink, String.valueOf(suite.getCclEnvironment().getOsUser()));
            sinkCell(sink, String.valueOf(suite.getCclEnvironment().getDomainName()));
            sinkCell(sink, String.valueOf(suite.getCclEnvironment().getCclUser()));
            sinkCell(sink, String.valueOf(suite.getCclEnvironment().getCclGroup()));
            sinkCell(sink, String.valueOf(suite.getCclEnvironment().getCclVersion()));
            sinkCell(sink, String.valueOf(suite.getCclEnvironment().getFrameworkVersion()));
            sink.tableRow_();

            sink.table_();
        }


        sink.lineBreak();
        sink.lineBreak();

        sink.section1_();
    }

    /**
     * Create a report of an individual test case's execution.
     *
     * @param sink
     *            The {@link Sink} to be used to generate the report.
     * @param testCase
     *            A {@link ResultsTestCase} representing the test case for which the report is to be generated.
     */
    private void constructTestCaseSection(final Sink sink, final ResultsTestCase testCase) {
        sink.sectionTitle3();
        sink.text(testCase.getName());
        sink.link("./cerreal-reports/" + testCase.getName() + ".html");
        sink.text(" [Code] ");
        sink.link_();
        sink.sectionTitle3_();
        sink.anchor(testCase.getName());
        sink.anchor_();

        sink.table();

        sink.tableRow();
        sinkHeader(sink, "Tests");
        sinkHeader(sink, "Failures");
        sinkHeader(sink, "Errors");
        sinkHeader(sink, "Passed");
        sinkHeader(sink, "Passed Percentage");
        sink.tableRow_();

        sink.tableRow();
        sinkCell(sink, String.valueOf(testCase.getTestCount()));
        sinkCell(sink, String.valueOf(testCase.getFailedTestCount()));
        sinkCell(sink, String.valueOf(testCase.getErroredTestCount()));
        sinkCell(sink, String.valueOf(testCase.getPassedTestCount()));
        sinkCell(sink, NumberFormat.getPercentInstance()
                .format(testCase.getPassedTestCount() / (double) testCase.getTestCount()));
        sink.tableRow_();

        sink.table_();

        sink.lineBreak();

        sink.table();
        for (final ResultsTest test : testCase.getTests()) {
            sink.tableRow();

            SinkEventAttributeSet attrs = new SinkEventAttributeSet();
            attrs.addAttribute(SinkEventAttributes.WIDTH, "20px");
            sink.tableCell(attrs);
            sinkTestResultIcon(sink, test.getResult());
            sink.tableCell_();

            sink.tableCell();
            sink.link("javascript:cerreal_toggleDisplay('" + testCase.getName() + test.getName() + "');");
            sink.text(test.getName());
            sink.link_();
            sink.tableCell_();

            constructRawResultsTable(sink, testCase, test);

            sink.tableRow_();
        }
        sink.table_();

        sink.lineBreak();
        sink.lineBreak();

        sink.section1_();
    }

    /**
     * Create an individual report for a test case.
     *
     * @param testCase
     *            A {@link ResultsTestCase} representing the test case for which a report is to be generated.
     * @throws MavenReportException
     *             If any errors occur during the report generation.
     */
    private void writeStandaloneTestCasePage(final ResultsTestCase testCase) throws MavenReportException {
        final File testCaseReportFile = new File(
                outputDirectory.getAbsolutePath() + "/cerreal-reports/" + testCase.getName() + ".html");
        final StringURIResolver resolver = XslAPI.getNewResolver();

        resolver.addResource("", "listing.xml", testCase.getListingXML());

        String reportHTML;
        try {
            reportHTML = XslAPI.transform(testCase.getTestResultsXML(),
                    getResourceAsString("xslt/testResultsProgram.xslt"), resolver);
        } catch (final XslAPIException e) {
            errorLogger.logFailedTransformation(testCase.getTestResultsXML(),
                    getResourceAsString("xslt/testResultsProgram.xslt"));
            throw new MavenReportException("Failed to get create standalone test case html page due to error", e);
        }

        try {
            FileUtils.writeStringToFile(testCaseReportFile, reportHTML, "utf-8");
        } catch (final IOException e) {
            throw new MavenReportException("Failed to write to standalone test case file due to error", e);
        }
    }

    /**
     * Create a table containing the individual tests within a test case.
     *
     * @param sink
     *            The {@link Sink} to be used to generate the report.
     * @param testCase
     *            A {@link ResultsTestCase} representing the test case whose tests are to be reported.
     * @param test
     *            A {@link ResultsTest} representing the individual test for which a report is to be generated.
     */
    private void constructRawResultsTable(final Sink sink, final ResultsTestCase testCase, final ResultsTest test) {
        sink.tableRow();
        sink.rawText("<td colspan=2>");

        sink.rawText("<span id=\"" + testCase.getName() + test.getName() + "\" style=\"display:none\">");

        sink.table();

        SinkEventAttributeSet attrs = new SinkEventAttributeSet();
        attrs.addAttribute(SinkEventAttributes.WIDTH, "15px");
        if (test.getResult() == TestResult.ERRORED) {
            for (int i = 1; i <= test.getErrorCount(); i++) {
                // Output the results of the individual assert statement
                sink.tableRow();

                // Output the assert result icon
                sink.tableCell(attrs);
                sinkTestResultIcon(sink, TestResult.ERRORED);
                sink.tableCell_();

                sinkCell(sink, String.valueOf(test.getErrorLineNumber(i)));
                sinkCell(sink, test.getErrorText(i));

                sink.tableRow_();
            }
        } else {
            for (int i = 1; i <= test.getAssertCount(); i++) {
                // Output the results of the individual assert statement
                sink.tableRow();

                // Output the assert result icon
                sink.tableCell(attrs);
                sinkTestResultIcon(sink, test.getAssertResult(i));
                sink.tableCell_();
                sinkCell(sink, getAssertStatement(testCase, test, i));
                sinkCell(sink, test.getAssertContext(i));
                sinkCell(sink, test.getAssertTest(i));

                sink.tableRow_();
            }
        }

        sink.table_();

        sink.rawText("</span>");

        sink.rawText("</td>");
        sink.tableRow_();
    }

    private static String getAssertStatement(final ResultsTestCase testCase, final ResultsTest test, final int assertIndex) {
        final int assertLineNumber = test.getAssertSourceCodeLineNumber(assertIndex);
        int startLineNumber = assertLineNumber;
        int endLineNumber = assertLineNumber;
        if (testCase.getName().equals("ut_cclut_execute_test_case_file")) {
            logger.trace(
                    "getAssertStatement for testName: {}, assertIndex: {}, assertLineNumber: {}", test.getName(),
                    assertIndex, assertLineNumber);
        }

        String assertStatementMasked = testCase.getSourceByLineNumber(startLineNumber)
                .toLowerCase(Locale.getDefault());
        String previousLine = testCase.getSourceByLineNumber(startLineNumber - 1).toLowerCase(Locale.getDefault());
        while (previousLine.endsWith("\\")) {
            assertStatementMasked = previousLine.substring(0, previousLine.length() - 1) + assertStatementMasked;
            startLineNumber--;
            previousLine = testCase.getSourceByLineNumber(startLineNumber - 1);
        }

        if (!assertStatementMasked.contains("curref")) {
            return (assembleLines(testCase, startLineNumber, endLineNumber));
        }

        int currefPosition = assertStatementMasked.indexOf("curref");
        int cclutAssertPosition = assertStatementMasked.indexOf("cclutassert");
        while (cclutAssertPosition < 0 || cclutAssertPosition > currefPosition) {
            logger.trace("searching for cclutassert: {}", assertStatementMasked);
            logger.trace("cclutAssertPosition = {}. currefPosition = {}", cclutAssertPosition, currefPosition);
            String currentLine = testCase.getSourceByLineNumber(--startLineNumber).toLowerCase(Locale.getDefault());
            if (currentLine.endsWith("\\")) {
                currentLine = currentLine.substring(0, currentLine.length() - 1);
            }
            assertStatementMasked = currentLine + assertStatementMasked;
            currefPosition += currentLine.length();
            cclutAssertPosition = assertStatementMasked.indexOf("cclutassert");
        }

        logger.trace("done searching for cclutassert: {}", assertStatementMasked);
        MaskTextResponse maskTextResponse = maskText(assertStatementMasked);
        assertStatementMasked = maskTextResponse.getResponse();
        while (maskTextResponse.recheckLine() || maskTextResponse.continueLine()) {
            if (maskTextResponse.continueLine()) {
                int validLength = maskTextResponse.getResponse().endsWith("\\")
                        ? maskTextResponse.getResponse().length() - 1 : maskTextResponse.getResponse().length();
                assertStatementMasked = maskTextResponse.getResponse().substring(0, validLength)
                        + testCase.getSourceByLineNumber(++endLineNumber);
            } else {
                assertStatementMasked = maskTextResponse.getResponse();
            }
            maskTextResponse = maskText(assertStatementMasked);
            assertStatementMasked = maskTextResponse.getResponse();
            logger.trace("masking text: {}", assertStatementMasked);
        }
        assertStatementMasked = maskTextResponse.getResponse();
        logger.trace("done masking : {}", assertStatementMasked);

        int parenCountLeft = 1;
        int parenCountRight = 0;
        int lastParenPosLeft = assertStatementMasked.indexOf("(", cclutAssertPosition);
        int lastParenPosRight = -1;
        int nextParenPosRight = -1;
        while (parenCountRight != parenCountLeft) {
            logger.trace("start of final paren search loop: {}", assertStatementMasked);
            nextParenPosRight = assertStatementMasked.indexOf(")", 1 + lastParenPosRight);
            while (nextParenPosRight < 0) {
                ExtendToRightParenResponse extendToRightParenResponse = extendToRightParen(testCase,
                        assertStatementMasked, endLineNumber);
                endLineNumber = extendToRightParenResponse.getFinalLineNumber();
                assertStatementMasked = extendToRightParenResponse.getResponse();
                nextParenPosRight = assertStatementMasked.indexOf(")", 1 + lastParenPosRight);
                logger.trace("searching for next right paren: {}", assertStatementMasked);
            }
            parenCountRight++;
            lastParenPosRight = nextParenPosRight;
            lastParenPosLeft = assertStatementMasked.indexOf("(", 1 + lastParenPosLeft);
            if (lastParenPosLeft > -1 && lastParenPosLeft < lastParenPosRight) {
                parenCountLeft++;
            }
            logger.trace("end of final paren search loop: {}", assertStatementMasked);
        }

        logger.trace("assembling lines: {}, {}", startLineNumber, endLineNumber);
        String assembledLines = assembleLines(testCase, startLineNumber, endLineNumber);
        int continuationCount = getContinuationCount(testCase, startLineNumber, endLineNumber);
        logger.trace("assembled lines: {}, lastParenPosRight {}, continuationCount {}", assembledLines,
                lastParenPosRight, continuationCount);
        return (assembledLines.substring(0, lastParenPosRight + continuationCount + 1));
    }

    private static String assembleLines(final ResultsTestCase testCase, final int start, final int end) {
        String response = "";
        for (int lineNumber = start; lineNumber <= end; lineNumber++) {
            response += testCase.getSourceByLineNumber(lineNumber);
        }
        return response;
    }

    private static int getContinuationCount(final ResultsTestCase testCase, final int start, final int end) {
        int continuationCount = 0;
        for (int lineNumber = start; lineNumber <= end; lineNumber++) {
            if (testCase.getSourceByLineNumber(lineNumber).endsWith("\\")) {
                continuationCount++;
            }
        }
        return continuationCount;
    }

    static class ExtendToRightParenResponse {
        String response;
        int finalLineNumber;

        public ExtendToRightParenResponse(String response, int finalLineNumber) {
            this.response = response;
            this.finalLineNumber = finalLineNumber;
        }

        public String getResponse() {
            return response;
        }

        public int getFinalLineNumber() {
            return finalLineNumber;
        }
    }

    static ExtendToRightParenResponse extendToRightParen(final ResultsTestCase testCase, final String source,
            final int lineNumber) {
        int startPos = source.endsWith("\\") ? source.length() - 1 : source.length();
        int finalLineNumber = lineNumber;
        String response = source.substring(0, startPos) + testCase.getSourceByLineNumber(++finalLineNumber);
        while (response.indexOf(")", startPos) == -1) {
            logger.trace("endingToRightParen cclutassert: {}", response);
            response = (response.endsWith("\\") ? response.substring(0, response.length() - 1) : response)
                    + testCase.getSourceByLineNumber(++finalLineNumber);
        }
        logger.trace("found right paren: {}", response);
        MaskTextResponse maskTextResponse = maskText(response);
        response = maskTextResponse.getResponse();
        while (maskTextResponse.recheckLine() || maskTextResponse.continueLine()) {
            logger.trace("masking the response: {}", response);
            if (maskTextResponse.continueLine()) {
                int validLength = maskTextResponse.getResponse().endsWith("\\")
                        ? maskTextResponse.getResponse().length() - 1 : maskTextResponse.getResponse().length();
                response = maskTextResponse.getResponse().substring(0, validLength)
                        + testCase.getSourceByLineNumber(++finalLineNumber);
            } else {
                response = maskTextResponse.getResponse();
            }
            maskTextResponse = maskText(response);
            response = maskTextResponse.getResponse();
        }
        response = maskTextResponse.getResponse();
        return new ExtendToRightParenResponse(response, finalLineNumber);
    }

    static class MaskTextResponse {
        private final String response;
        private final boolean continuation;
        private final boolean recheck;

        public MaskTextResponse(String response, boolean continuation, boolean recheck) {
            this.response = response;
            this.continuation = continuation;
            this.recheck = recheck;
        }

        public String getResponse() {
            return response;
        }

        public boolean continueLine() {
            return continuation;
        }

        public boolean recheckLine() {
            return recheck;
        }
    }

    static MaskTextResponse maskText(final String source) {
        final Matcher matcher = Pattern.compile("(?i:@\\d+\\:|\\\"|\\^|~|'|\\||;|![^=]|/\\*)").matcher(source);
        if (matcher.find()) {
            final int startPosition = matcher.start();
            final String matchGroup = matcher.group(0);
            final int matchGroupLength = matchGroup.length();
            if (matchGroup.equals("/*")) {
                int endPosition = source.indexOf("*/", startPosition + 1);
                if (endPosition > -1) {
                    return new MaskTextResponse(source.substring(0, startPosition)
                            + StringUtils.repeat("x", 2 + endPosition - startPosition)
                            + source.substring(endPosition + 2, source.length()), false, true);
                }
                return new MaskTextResponse(source, true, false);
            }
            if (matchGroup.equals(";") || matchGroup.startsWith("!")) {
                if (source.endsWith("\\")) {
                    return new MaskTextResponse(source, true, false);
                }
                return new MaskTextResponse(
                        source.substring(0, startPosition) + StringUtils.repeat("x", source.length() - startPosition),
                        false, false); // no need to re-check.
            }
            // if it is not an @, then the CCL compile should fail
            if (matchGroup.startsWith("@")) {
                int quoteLength = Integer.parseInt(matchGroup.substring(1, matchGroupLength - 1));
                if (source.length() < startPosition + matchGroupLength + quoteLength + 1) {
                    return new MaskTextResponse(source, true, false);
                }
                if (source.substring(startPosition + matchGroupLength + quoteLength,
                        startPosition + matchGroupLength + quoteLength + 1).equals("@")) {
                    return new MaskTextResponse(source.substring(0, matcher.start())
                            + StringUtils.repeat("x", matchGroupLength + quoteLength + 1)
                            + source.substring(startPosition + matchGroupLength + quoteLength + 1, source.length()),
                            false, true);
                }
            }
            int endPosition = source.indexOf(matchGroup, startPosition + 1);
            if (endPosition > -1) {
                return new MaskTextResponse(
                        source.substring(0, startPosition) + StringUtils.repeat("x", 1 + endPosition - startPosition)
                                + source.substring(endPosition + 1, source.length()),
                        false, true);
            }
            return new MaskTextResponse(source, true, false);
        }
        return new MaskTextResponse(source, false, false);
    }

    /**
     * Create a header cell.
     *
     * @param s
     *            The {@link Sink} to be used to create the header cell.
     * @param header
     *            The text of the header.
     */
    private void sinkHeader(final Sink s, final String header) {
        s.tableHeaderCell();
        s.text(header);
        s.tableHeaderCell_();
    }

    /**
     * Create a table cell.
     *
     * @param s
     *            The {@link Sink} to be used to create the table cell.
     * @param text
     *            The content of the table cell.
     */
    private void sinkCell(final Sink s, final String text) {
        s.tableCell();
        s.text(text);
        s.tableCell_();
    }

    /**
     * Create a container for the icon representing the ending result of a test.
     *
     * @param sink
     *            The {@link Sink} to be used to generate the container.
     * @param result
     *            A {@link TestResult} representing the result of the test.
     */
    private void sinkTestResultIcon(final Sink sink, final TestResult result) {
        sink.figure();

        if (result == TestResult.PASSED) {
            sink.figureGraphics("images/icon_success_sml.gif");
        } else if (result == TestResult.ERRORED) {
            sink.figureGraphics("images/icon_error_sml.gif");
        } else if (result == TestResult.FAILED) {
            sink.figureGraphics("images/icon_warning_sml.gif");
        }

        sink.figure_();
    }

    /**
     * Copy the stylesheets to the hard disk.
     *
     * @throws MavenReportException
     *             If any errors occur while copying the stylesheet.
     */
    private void deployStylesheets() throws MavenReportException {
        File cssFile;

        try {
            cssFile = new File(outputDirectory.getAbsolutePath() + "/cerreal-reports/css/testResultsProgram.css");
            FileUtils.writeStringToFile(cssFile, getResourceAsString("css/testResultsProgram.css"), "utf-8");
        } catch (final IOException e) {
            throw new MavenReportException("Failed to deploy Cerreal CSS files due to error", e);
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
            file = new File(outputDirectory.getAbsolutePath() + "/cerreal-reports/js/cerreal.js");
            FileUtils.writeStringToFile(file, getResourceAsString("js/cerreal.js"), "utf-8");
        } catch (final IOException e) {
            throw new MavenReportException("Failed to deploy Cerreal JS files", e);
        }
    }

    /**
     * Get a resource off of the classpath as a string.
     *
     * @param name
     *            The name of the resource to be read.
     * @return The request resource, as a string.
     * @throws MavenReportException
     *             If any errors occur while retrieving the classpath resource.
     */
    private String getResourceAsString(final String name) throws MavenReportException {
        try {
            final InputStream resourceStream = getClass().getResourceAsStream("/" + name);
            return IOUtils.toString(resourceStream, "utf-8");
        } catch (final IOException e) {
            throw new MavenReportException("Failed to get resource " + name + " due to error", e);
        }
    }
}
