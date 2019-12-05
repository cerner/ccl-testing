package com.cerner.ccl.testing.maven.ccl.reports;

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

import com.cerner.ccl.testing.maven.ccl.reports.common.ReportErrorLogger;
import com.cerner.ccl.testing.maven.ccl.reports.common.ResultsTest;
import com.cerner.ccl.testing.maven.ccl.reports.common.ResultsTest.TestResult;
import com.cerner.ccl.testing.maven.ccl.reports.common.ResultsTestCase;
import com.cerner.ccl.testing.maven.ccl.reports.common.ResultsTestSuite;
import com.cerner.ccl.testing.xsl.StringURIResolver;
import com.cerner.ccl.testing.xsl.XslAPI;
import com.cerner.ccl.testing.xsl.XslAPIException;

/**
 * Class to convert the files stored target into the Cerreal report. This report shows unit test results.
 *
 * @author Jeff Wiedemann
 * @author Fred Eckertson
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

        SinkEventAttributeSet topSectionAttributes = new SinkEventAttributeSet();
        topSectionAttributes.addAttribute(SinkEventAttributes.ID, "topSection");

        sink.section(1, topSectionAttributes);
        sink.sectionTitle(1, null);
        sink.text("Cerreal Report");
        sink.sectionTitle_(1);

        constructSummarySection(sink, suite);

        SinkEventAttributeSet testCasesSectionAttributes = new SinkEventAttributeSet();
        testCasesSectionAttributes.addAttribute(SinkEventAttributes.ID, "testCasesSection");
        sink.section(2, testCasesSectionAttributes);
        sink.sectionTitle(1, null);
        sink.text("Test Cases");
        sink.sectionTitle_(1);

        sink.rawText(
                "<input type=\"checkbox\" onclick=\"cerreal_toggleClassDisplay('div', 'testCasePassed')\" checked />");
        sink.text("Passed Cases ");

        sink.rawText(
                "<input type=\"checkbox\" onclick=\"cerreal_toggleClassDisplay('tr', 'unitTestPassed')\" checked />");
        sink.text("Passed Tests ");

        sink.rawText(
                "<input type=\"checkbox\" onclick=\"cerreal_toggleClassDisplay('tr', 'assertPassed')\" checked />");
        sink.text("Passed Asserts ");

        for (final ResultsTestCase tc : suite.getTestCases()) {
            constructTestCaseSection(sink, tc);
            writeStandaloneTestCasePage(tc);
        }

        sink.section_(2);
        sink.section_(1);

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

        final SinkEventAttributeSet attrs = new SinkEventAttributeSet();
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
        SinkEventAttributeSet summarySectionAttributes = new SinkEventAttributeSet();
        summarySectionAttributes.addAttribute(SinkEventAttributes.ID, "summarySection");

        sink.section(2, summarySectionAttributes);
        sink.sectionTitle(2, null);
        sink.text("Summary");
        sink.sectionTitle_(2);

        sink.table();

        sink.tableRow();
        tableHeader(sink, "Tests");
        tableHeader(sink, "Failures");
        tableHeader(sink, "Errors");
        tableHeader(sink, "Passed");
        tableHeader(sink, "Passed Percentage");
        sink.tableRow_();

        sink.tableRow();
        textCell(sink, String.valueOf(suite.getTestCount()));
        textCell(sink, String.valueOf(suite.getFailedCount()));
        textCell(sink, String.valueOf(suite.getErroredCount()));
        textCell(sink, String.valueOf(suite.getPassedCount()));
        textCell(sink,
                NumberFormat.getPercentInstance().format(suite.getPassedCount() / (double) suite.getTestCount()));
        sink.tableRow_();

        sink.table_();
        sink.section2_();

        if (suite.getCclEnvironment().isDataAvailable()) {
            SinkEventAttributeSet environmentSectionAttributes = new SinkEventAttributeSet();
            environmentSectionAttributes.addAttribute(SinkEventAttributes.ID, "environmentSection");
            sink.section(2, environmentSectionAttributes);
            sink.sectionTitle(2, null);
            sink.text("Environment");
            sink.sectionTitle_(2);

            sink.table();

            sink.tableRow();
            tableHeader(sink, "Host");
            tableHeader(sink, "Host User");
            tableHeader(sink, "Domain");
            tableHeader(sink, "Domain User");
            tableHeader(sink, "CCL Group");
            tableHeader(sink, "CCL Version");
            tableHeader(sink, "Framework Version");
            sink.tableRow_();

            sink.tableRow();
            textCell(sink, String.valueOf(suite.getCclEnvironment().getNodeName()));
            textCell(sink, String.valueOf(suite.getCclEnvironment().getOsUser()));
            textCell(sink, String.valueOf(suite.getCclEnvironment().getDomainName()));
            textCell(sink, String.valueOf(suite.getCclEnvironment().getCclUser()));
            textCell(sink, String.valueOf(suite.getCclEnvironment().getCclGroup()));
            textCell(sink, String.valueOf(suite.getCclEnvironment().getCclVersion()));
            textCell(sink, String.valueOf(suite.getCclEnvironment().getFrameworkVersion()));
            sink.tableRow_();

            sink.table_();
            sink.section_(2);
        }
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
        SinkEventAttributes testCaseAttributes = new SinkEventAttributeSet();
        if (testCase.getPassedTestCount() == testCase.getTestCount()) {
            testCaseAttributes.addAttribute(SinkEventAttributes.CLASS, "testCasePassed");
        } else {
            testCaseAttributes.addAttribute(SinkEventAttributes.CLASS, "testCaseNotPassed");
        }
        sink.section(3, testCaseAttributes);
        sink.sectionTitle(2, null);
        sink.text(testCase.getName());
        sink.link("./cerreal-reports/" + testCase.getName() + ".html");
        sink.text(" [Code] ");
        sink.link_();
        sink.sectionTitle_(2);

        sink.table();

        sink.tableRow();
        tableHeader(sink, "Tests");
        tableHeader(sink, "Failures");
        tableHeader(sink, "Errors");
        tableHeader(sink, "Passed");
        tableHeader(sink, "Passed Percentage");
        sink.tableRow_();

        sink.tableRow();
        textCell(sink, String.valueOf(testCase.getTestCount()));
        textCell(sink, String.valueOf(testCase.getFailedTestCount()));
        textCell(sink, String.valueOf(testCase.getErroredTestCount()));
        textCell(sink, String.valueOf(testCase.getPassedTestCount()));
        textCell(sink, NumberFormat.getPercentInstance()
                .format(testCase.getPassedTestCount() / (double) testCase.getTestCount()));
        sink.tableRow_();

        sink.table_();

        if (testCase.getTests().size() > 0) {
            sink.table();
            sink.tableRows(null, false);
        }
        boolean evenRow = false;
        boolean isToggleNeeded = false;
        for (final ResultsTest test : testCase.getTests()) {
            evenRow = !evenRow;
            String classname = new StringBuilder().append(evenRow ? "a " : "b ")
                    .append(isToggleNeeded ? "unitTestPassedToggle " : "")
                    .append(test.getResult().equals(TestResult.PASSED) ? "unitTestPassed" : "unitTestNotPassed")
                    .toString();
            if (test.getResult().equals(TestResult.PASSED)) {
                isToggleNeeded = !isToggleNeeded;
            }
            SinkEventAttributes testAttributes = new SinkEventAttributeSet();
            testAttributes.addAttribute(SinkEventAttributes.CLASS, classname);
            sink.tableRow(testAttributes);

            SinkEventAttributeSet attrs = new SinkEventAttributeSet();
            attrs.addAttribute(SinkEventAttributes.CLASS, "test-icon");
            sink.tableCell(attrs);
            resultIcon(sink, "test-icon", test.getResult());
            sink.tableCell_();

            sink.tableCell();

            sink.link("javascript:cerreal_toggleDisplay('" + testCase.getName() + test.getName() + "');");
            sink.text(test.getName());
            sink.link_();

            sink.tableCell_();
            sink.tableRow_();

            addTestDetails(sink, testCase, test);
        }
        if (testCase.getTests().size() > 0) {
            sink.tableRows_();
            sink.table_();
        }
        sink.section_(3);
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
    private void addTestDetails(final Sink sink, final ResultsTestCase testCase, final ResultsTest test) {
        SinkEventAttributes detailRowAttributes = new SinkEventAttributeSet();
        detailRowAttributes.addAttribute(SinkEventAttributes.ID, testCase.getName() + test.getName());
        detailRowAttributes.addAttribute(SinkEventAttributes.STYLE, "display: none");
        sink.tableRow(detailRowAttributes);

        SinkEventAttributes tableCellAttributes = new SinkEventAttributeSet();
        tableCellAttributes.addAttribute(SinkEventAttributes.COLSPAN, 2);
        sink.tableCell(tableCellAttributes);

        if (test.getErrorCount() > 0 || test.getAssertCount() > 0) {
            sink.table();
            sink.tableRows(null, false);
        }

        SinkEventAttributeSet iconCellAttributes = new SinkEventAttributeSet();
        iconCellAttributes.addAttribute(SinkEventAttributes.CLASS, "assert-icon");
        if (test.getResult() == TestResult.ERRORED) {
            for (int idx = 1; idx <= test.getErrorCount(); idx++) {
                sink.tableRow();

                sink.tableCell(iconCellAttributes);
                resultIcon(sink, "assert-icon", TestResult.ERRORED);
                sink.tableCell_();

                textCell(sink, String.valueOf(test.getErrorLineNumber(idx)));
                textCell(sink, test.getErrorText(idx));

                sink.tableRow_();
            }
        } else {
            boolean isToggleNeeded = false;
            for (int idx = 1; idx <= test.getAssertCount(); idx++) {
                SinkEventAttributes assertRowAttributes = new SinkEventAttributeSet();
                String classname = new StringBuilder().append(idx % 2 == 0 ? "b " : "a ")
                        .append(testCase.getName() + test.getName()).append(" ")
                        .append(test.getResult().equals(TestResult.PASSED) ? "unitTestPassed " : "")
                        .append(isToggleNeeded ? "assertPassedToggle " : "")
                        .append(test.getAssertResult(idx).equals(TestResult.PASSED) ? "assertPassed"
                                : "assertNotPassed")
                        .toString();
                if (test.getAssertResult(idx).equals(TestResult.PASSED)) {
                    isToggleNeeded = !isToggleNeeded;
                }
                assertRowAttributes.addAttribute(SinkEventAttributes.CLASS, classname);
                sink.tableRow(assertRowAttributes);

                sink.tableCell(iconCellAttributes);
                resultIcon(sink, "assert-icon", test.getAssertResult(idx));
                sink.tableCell_();

                textCell(sink, getAssertStatement(testCase, test, idx));
                textCell(sink, test.getAssertContext(idx));
                textCell(sink, test.getAssertTest(idx));

                sink.tableRow_();
            }
        }

        if (test.getErrorCount() > 0 || test.getAssertCount() > 0) {
            sink.tableRows_();
            sink.table_();
        }
        sink.tableCell_();
        sink.tableRow_();
    }

    private static String getAssertStatement(final ResultsTestCase testCase, final ResultsTest test,
            final int assertIndex) {
        final int assertLineNumber = test.getAssertSourceCodeLineNumber(assertIndex);
        int startLineNumber = assertLineNumber;
        int endLineNumber = assertLineNumber;
        if (testCase.getName().equals("ut_cclut_execute_test_case_file")) {
            logger.trace("getAssertStatement for testName: {}, assertIndex: {}, assertLineNumber: {}", test.getName(),
                    assertIndex, assertLineNumber);
        }

        String assertStatementMasked = testCase.getSourceByLineNumber(startLineNumber).toLowerCase(Locale.getDefault());
        String previousLine = testCase.getSourceByLineNumber(startLineNumber - 1).toLowerCase(Locale.getDefault());
        while (previousLine.endsWith("\\")) {
            assertStatementMasked = previousLine.substring(0, previousLine.length() - 1) + assertStatementMasked;
            startLineNumber--;
            previousLine = testCase.getSourceByLineNumber(startLineNumber - 1);
        }

        if (!assertStatementMasked.contains("curref")) {
            return assembleLines(testCase, startLineNumber, endLineNumber);
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
                        ? maskTextResponse.getResponse().length() - 1
                        : maskTextResponse.getResponse().length();
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
        return assembledLines.substring(0, lastParenPosRight + continuationCount + 1);
    }

    private static String assembleLines(final ResultsTestCase testCase, final int start, final int end) {
        StringBuilder response = new StringBuilder();
        for (int lineNumber = start; lineNumber <= end; lineNumber++) {
            response.append(testCase.getSourceByLineNumber(lineNumber));
        }
        return response.toString();
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

        public ExtendToRightParenResponse(final String response, final int finalLineNumber) {
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
        StringBuilder sbResponse = new StringBuilder(
                source.substring(0, startPos) + testCase.getSourceByLineNumber(++finalLineNumber));
        while (sbResponse.indexOf(")", startPos) == -1) {
            logger.trace("endingToRightParen cclutassert: {}", sbResponse.toString());
            if (sbResponse.length() > 0 && sbResponse.substring(sbResponse.length() - 1).equals("\\")) {
                sbResponse.setLength(sbResponse.length() - 1);
            }
            sbResponse.append(testCase.getSourceByLineNumber(++finalLineNumber));
        }
        String response = sbResponse.toString();
        logger.trace("found right paren: {}", response);
        MaskTextResponse maskTextResponse = maskText(response);
        response = maskTextResponse.getResponse();
        while (maskTextResponse.recheckLine() || maskTextResponse.continueLine()) {
            logger.trace("masking the response: {}", response);
            if (maskTextResponse.continueLine()) {
                int validLength = maskTextResponse.getResponse().endsWith("\\")
                        ? maskTextResponse.getResponse().length() - 1
                        : maskTextResponse.getResponse().length();
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

        public MaskTextResponse(final String response, final boolean continuation, final boolean recheck) {
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
    private void tableHeader(final Sink s, final String header) {
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
    private void textCell(final Sink s, final String text) {
        s.tableCell();
        s.text(text);
        s.tableCell_();
    }

    /**
     * Create a container for the icon representing the ending result of a test.
     *
     * @param sink
     *            The {@link Sink} to be used to generate the container.
     * @param classname
     *            The classname to set on the element.
     * @param result
     *            A {@link TestResult} representing the result of the test.
     */
    private void resultIcon(final Sink sink, final String classname, final TestResult result) {
        String imageName = result == TestResult.PASSED ? "images/icon_success_sml.gif"
                : result == TestResult.ERRORED ? "images/icon_error_sml.gif"
                        : result == TestResult.FAILED ? "images/icon_warning_sml.gif" : "";
        SinkEventAttributeSet attrs = new SinkEventAttributeSet();
        attrs.addAttribute(SinkEventAttributes.ALT, "");
        attrs.addAttribute(SinkEventAttributes.CLASS, classname);

        sink.figureGraphics(imageName, attrs);
        // if doxia-site-renderer get fixed, the next line might be necessary, but till then it add an extra </div>
        // sink.figure_();
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
            File siteCssFile = new File(outputDirectory.getAbsolutePath(), "/css/site.css");
            if (!siteCssFile.exists()) {
                FileUtils.writeStringToFile(siteCssFile, getResourceAsString("css/cerreal.css"), "utf-8");
            }
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
