package com.cerner.ccl.testing.maven.ccl.reports;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.commons.io.output.FileWriterWithEncoding;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.test.plugin.BuildTool;
import org.codehaus.plexus.util.FileUtils;
import org.fest.assertions.Condition;
import org.fest.assertions.Delta;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.RenderedWebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.cerner.ccl.testing.maven.ccl.reports.common.internal.jaxb.LISTING;
import com.cerner.ccl.testing.maven.ccl.reports.common.internal.jaxb.LISTING.LINES;
import com.cerner.ccl.testing.maven.ccl.reports.common.internal.jaxb.LISTING.LINES.LINE;
import com.cerner.ccl.testing.maven.ccl.reports.common.internal.jaxb.ObjectFactory;
import com.cerner.ccl.testing.maven.ccl.reports.common.internal.jaxb.TESTCASE;
import com.cerner.ccl.testing.maven.ccl.reports.common.internal.jaxb.TESTCASE.TESTS;
import com.cerner.ccl.testing.maven.ccl.reports.common.internal.jaxb.TESTCASE.TESTS.TEST;
import com.cerner.ccl.testing.maven.ccl.reports.common.internal.jaxb.TESTCASE.TESTS.TEST.ASSERTS;
import com.cerner.ccl.testing.maven.ccl.reports.common.internal.jaxb.TESTCASE.TESTS.TEST.ASSERTS.ASSERT;
import com.cerner.ccl.testing.maven.ccl.reports.common.internal.jaxb.TESTCASE.TESTS.TEST.ERRORS;
import com.cerner.ccl.testing.maven.ccl.reports.common.internal.jaxb.TESTCASE.TESTS.TEST.ERRORS.ERROR;
import com.cerner.ccl.testing.maven.ccl.reports.internal.AssertionFactory;
import com.cerner.ccl.testing.maven.ccl.reports.internal.AssertionTest;
import com.cerner.ccl.testing.maven.ccl.reports.internal.CclUnitTest;
import com.cerner.ccl.testing.maven.ccl.reports.internal.ExecutionResult;

/**
 * Integration tests for {@link CerrealMojo} and {@link CCLCoverageMojo}.
 * 
 * @author Joshua Hyde
 * 
 */

public class CerrealPluginITest {
    /**
     * A {@link Rule} used to retrieve the current test name.
     */
    @Rule
    public TestName testName = new TestName();
    private final File outputDirectory = new File("target/" + getClass().getSimpleName());
    private final ObjectFactory objectFactory = new ObjectFactory();
    private final List<String> siteGoal = Collections.singletonList("site");
    private final WebDriver driver = new HtmlUnitDriver(true);
    private final AssertionFactory factory = new AssertionFactory();
    private BuildTool build;

    /**
     * Set the {@code $maven.home} value in the system properties for the {@link BuildTool} object.
     * 
     * @throws Exception
     *             If any errors occur during the setup.
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        final ResourceBundle systemPropsBundle = ResourceBundle.getBundle("system");
        System.setProperty("maven.home", systemPropsBundle.getString("maven.home"));
    }

    /**
     * Ensure that the output directory exists and that the build tool is ready.
     * 
     * @throws Exception
     *             If any errors occur during the setup.
     */
    @Before
    public void setUp() throws Exception {
        FileUtils.forceMkdir(outputDirectory);

        build = new BuildTool();
        build.initialize();

        // Start with a clean build directory
        FileUtils.forceDelete(new File(getProjectDirectory(), "target"));
    }

    /**
     * Close the driver.
     */
    @After
    public void tearDown() {
        if (driver != null)
            driver.close();
    }

    /**
     * Test the generation of a report.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testReportGenerationErroredTest() throws Exception {
        final AssertionTest passedAssertion = factory.passedAssertion("this is a passed assertion", "passed == true");
        final AssertionTest erroredAssertion = factory.erroredAssertion("i errored", "errored == true");
        erroredAssertion.setErrorText("this is an error");

        final String testDirectoryName = testName.getMethodName();
        final String cclUnitTestName = testName.getMethodName() + "-a";

        final CclUnitTest test = new CclUnitTest(cclUnitTestName, Arrays.asList(passedAssertion, erroredAssertion));
        writeListing(test);
        writeTestResults(test);

        final InvocationResult result = build.executeMaven(getPomFile(), null, siteGoal, getLogFile());
        assertThat(result.getExitCode()).isZero();

        WebDriver driver = navigateToCerrealReport();
        validateSummary(driver, test);
        final RenderedWebElement toToggleElement = (RenderedWebElement) driver
                .findElement(By.id(testDirectoryName + cclUnitTestName));
        validateExpandableElement(driver.findElement(By.linkText(cclUnitTestName)), toToggleElement,
                new Verification<RenderedWebElement>() {
                    public void verify(RenderedWebElement object) throws Exception {
                        validateAssertionRowText(toToggleElement.findElement(By.tagName("tr")),
                                erroredAssertion.getResult(), Integer.toString(erroredAssertion.getLineNumber()),
                                erroredAssertion.getErrorText());
                    }
                });
        validateCodeCoverage(driver, testDirectoryName, test);
    }

    /**
     * Verify the report generated for a failed test.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testReportGenerationFailedTest() throws Exception {
        final AssertionTest passedAssertion = factory.passedAssertion("this is a passed assertion", "passed == true");
        final AssertionTest failedAssertion = factory.failedAssertion("this is a failed assertion", "passed == false");

        final String testDirectoryName = testName.getMethodName();
        final String cclUnitTestName = testName.getMethodName() + "-a";

        final CclUnitTest test = new CclUnitTest(cclUnitTestName, Arrays.asList(passedAssertion, failedAssertion));
        writeTestResults(test);
        writeListing(test);

        final InvocationResult result = build.executeMaven(getPomFile(), null, siteGoal, getLogFile());
        assertThat(result.getExitCode()).isZero();

        final WebDriver driver = navigateToCerrealReport();
        validateSummary(driver, test);
        final RenderedWebElement toToggleElement = (RenderedWebElement) driver
                .findElement(By.id(testDirectoryName + cclUnitTestName));
        validateExpandableElement(driver.findElement(By.linkText(cclUnitTestName)), toToggleElement,
                new Verification<RenderedWebElement>() {
                    public void verify(RenderedWebElement object) throws Exception {
                        final List<WebElement> rows = object.findElements(By.tagName("tr"));
                        final Collection<AssertionTest> tests = test.getTests();
                        assertThat(rows).hasSize(tests.size());

                        int columnIndex = 0;
                        for (AssertionTest assertion : tests)
                            validateAssertionRowText(rows.get(columnIndex++), assertion.getResult(),
                                    assertion.getSourceCode(), assertion.getContext(), assertion.getTestExecuted());
                    }
                });
        validateCodeCoverage(driver, testDirectoryName, test);

    }

    /**
     * Test the generation of a report for a test that succeeded.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testReportGenerationPassedTest() throws Exception {
        final AssertionTest passedAssertionA = factory.passedAssertion("this is a passed assertion", "passed == true");
        final AssertionTest passedAssertionB = factory.passedAssertion("this is another passed assertion",
                "passed == true, again!");
        final AssertionTest passedAssertionC = factory.passedAssertion("i didn't error, nor did I fail!",
                "errored == false, failed == false");
        final String testDirectoryName = testName.getMethodName();
        final String cclUnitTestName = testName.getMethodName() + "-a";

        final CclUnitTest test = new CclUnitTest(cclUnitTestName,
                Arrays.asList(passedAssertionA, passedAssertionB, passedAssertionC));
        writeTestResults(test);
        writeListing(test);

        final InvocationResult result = build.executeMaven(getPomFile(), null, siteGoal, getLogFile());
        assertThat(result.getExitCode()).isZero();

        WebDriver driver = navigateToCerrealReport();
        validateSummary(driver, test);
        final RenderedWebElement toToggleElement = (RenderedWebElement) driver
                .findElement(By.id(testDirectoryName + cclUnitTestName));
        validateExpandableElement(driver.findElement(By.linkText(cclUnitTestName)), toToggleElement,
                new Verification<RenderedWebElement>() {
                    public void verify(RenderedWebElement object) throws Exception {
                        final List<WebElement> rows = object.findElements(By.tagName("tr"));
                        final Collection<AssertionTest> tests = test.getTests();
                        assertThat(rows).hasSize(tests.size());

                        int columnIndex = 0;
                        for (AssertionTest assertion : tests)
                            validateAssertionRowText(rows.get(columnIndex++), assertion.getResult(),
                                    assertion.getSourceCode(), assertion.getContext(), assertion.getTestExecuted());
                    }
                });
        validateCodeCoverage(driver, testDirectoryName, test);
    }

    /**
     * Get a log file to which output from a Maven invocation can be written to.
     * 
     * @return A {@link File} representing the directory to which the log data can be written.
     * @throws IOException
     *             If any errors occur while creating the file.
     */
    private File getLogFile() throws IOException {
        return new File(getTestDirectory(), "maven-" + Long.toString(System.currentTimeMillis()) + ".log");
    }

    /**
     * Get the project directory for this test.
     * 
     * @return A {@link File} representing the project directory for this test.
     */
    private File getProjectDirectory() {
        final String resourcePath = "/poms/" + getClass().getSimpleName();
        final URL directoryUrl = getClass().getResource(resourcePath);
        if (directoryUrl == null)
            throw new IllegalStateException("Unable to locate resource: " + resourcePath);
        return FileUtils.toFile(directoryUrl);
    }

    /**
     * Get the directory containing the POM files for the given test.
     * 
     * @return A {@link File} representing the POM directory for this test.
     */
    private File getPomFile() {
        return new File(getProjectDirectory(), "pom.xml");
    }

    /**
     * Create a test directory - a directory into which a test can dump all of its byproducts.
     * 
     * @return A {@link File} representing a test for this directory.
     * @throws IOException
     *             If any errors occur during the folder creation.
     */
    private File getTestDirectory() throws IOException {
        final File testDirectory = new File(outputDirectory, testName.getMethodName());
        FileUtils.forceMkdir(testDirectory);
        return testDirectory;
    }

    /**
     * Get the {@code test-results} directory within the build directory of the project.
     * 
     * @return A {@link File} representing the {@code test-results} directory.
     * @throws IOException
     *             If any errors occur while creating the directory (if it does not exist).
     */
    private File getTestResultsDirectory() throws IOException {
        final File testResultsDirectory = new File(getProjectDirectory(),
                "target/test-results/" + testName.getMethodName());
        FileUtils.forceMkdir(testResultsDirectory);
        return testResultsDirectory;
    }

    /**
     * Determine whether or not the CSS of the given element makes it visible on the page.
     * 
     * @param element
     *            The {@link RenderedWebElement} whose visibility is to be determined.
     * @return {@code true} if the CSS of the given element permits it to be visible.
     */
    private boolean isVisible(RenderedWebElement element) {
        return !"none".equals(element.getValueOfCssProperty("display"));
    }

    /**
     * Navigate to the Cerreal Report.
     * 
     * @return A {@link WebDriver} object that can be used to interact with the page.
     * @throws IOException
     *             If any errors occur during the test run.
     */
    private WebDriver navigateToCerrealReport() throws IOException {
        driver.get(new File(getProjectDirectory(), "/target/site/index.html").toURI().toURL().toExternalForm());
        driver.findElement(By.linkText("Project Reports")).click();
        driver.findElement(By.linkText("Cerreal Report")).click();
        return driver;
    }

    /**
     * Navigating {@link #navigateToCerrealReport() from the Cerreal Report}, open a program's code coverage
     * information.
     * 
     * @param driver
     *            The {@link WebDriver} to be used to interact with the Maven site.
     * @param testDirectoryName
     *            The name of the test directory whose code coverage is to be viewed.
     * @return A {@link WebDriver} to be used to interact with the code coverage page.
     */
    private WebDriver navigateToCodeCoverage(WebDriver driver, String testDirectoryName) {
        for (WebElement element : driver.findElements(By.tagName("a"))) {
            final String hrefAttribute = element.getAttribute("href");
            if (hrefAttribute != null && hrefAttribute.endsWith(testDirectoryName + ".html")) {
                element.click();
                return driver;
            }
        }

        throw new IllegalArgumentException("No code coverage link found for test: " + testDirectoryName);
    }

    /**
     * Convert a {@link AssertionTest} object to an {@link ASSERT} object.
     * 
     * @param test
     *            The {@link AssertionTest} to be converted.
     * @return An {@link ASSERT} object representing the given assertion test object.
     */
    private ASSERT toAssert(AssertionTest test) {
        final ASSERT xmlAssert = objectFactory.createTESTCASETESTSTESTASSERTSASSERT();
        xmlAssert.setCONTEXT(test.getContext());
        xmlAssert.setLINENUMBER(test.getLineNumber());
        xmlAssert.setRESULT(test.getResult().name());
        xmlAssert.setTEST(test.getTestExecuted());
        return xmlAssert;
    }

    /**
     * Convert an {@link AssertionError} object to an {@link ERROR} object.
     * 
     * @param error
     *            The {@link AssertionError} object to be converted.
     * @return An {@link ERROR} object representing the given assertion error.
     */
    private ERROR toError(AssertionTest error) {
        final ERROR xmlError = objectFactory.createTESTCASETESTSTESTERRORSERROR();
        xmlError.setLINENUMBER(error.getLineNumber());
        xmlError.setERRORTEXT(error.getErrorText());
        return xmlError;
    }

    /**
     * Create a listing.
     * 
     * @param listingName
     *            The name of the listing.
     * @param lines
     *            A vararg array of {@link AssertionTest} objects that will compose the lines of the source code.
     * @return A {@link LISTING}.
     */
    private LISTING toListing(String listingName, AssertionTest... lines) {
        final LISTING listing = objectFactory.createLISTING();
        listing.setLISTINGNAME(listingName);

        final LINES listingLines = objectFactory.createLISTINGLINES();
        if (lines != null) {
            int lineNumber = 0;
            for (AssertionTest line : lines) {
                final LINE listingLine = objectFactory.createLISTINGLINESLINE();
                listingLine.setNBR(BigInteger.valueOf(++lineNumber));
                listingLine.setTEXT(line.getSourceCode());
                listingLines.getLINE().add(listingLine);
            }
        }

        listing.setLINES(listingLines);
        listing.setCOMPILEDATE(new Date().toString());
        return listing;
    }

    /**
     * Create a JAXB {@link TESTCASE} element.
     * 
     * @param testCaseName
     *            The name of the test case.
     * @param tests
     *            The {@link Test} objects to be converted.
     * @return A {@link TESTCASE} object representing the given tests.
     */
    private TESTCASE toTestCase(String testCaseName, CclUnitTest... tests) {
        final TESTS xmlTests = objectFactory.createTESTCASETESTS();
        for (CclUnitTest test : tests) {
            final ASSERTS xmlAsserts = objectFactory.createTESTCASETESTSTESTASSERTS();
            final ERRORS xmlErrors = objectFactory.createTESTCASETESTSTESTERRORS();
            for (AssertionTest assertionTest : test.getTests())
                switch (assertionTest.getResult()) {
                case PASSED:
                case FAILED:
                    xmlAsserts.getASSERT().add(toAssert(assertionTest));
                    break;
                case ERRORED:
                    xmlErrors.getERROR().add(toError(assertionTest));
                }

            final TEST xmlTest = objectFactory.createTESTCASETESTSTEST();
            xmlTest.setASSERTS(xmlAsserts);
            xmlTest.setERRORS(xmlErrors);
            xmlTest.setRESULT(test.getResult().name());
            xmlTest.setNAME(test.getTestName());

            xmlTests.getTEST().add(xmlTest);
        }

        final TESTCASE xmlTestCase = objectFactory.createTESTCASE();
        xmlTestCase.setTESTS(xmlTests);
        xmlTestCase.setNAME(testCaseName);

        return xmlTestCase;
    }

    /**
     * Verify that the given test assertion row ({@code 
     * <tr />
     * }) has the given expected text.
     * 
     * @param row
     *            A {@link WebElement} representing the row to be inspected. This is assumed to be a {@code <tr />}
     *            element.
     * @param expectedTexts
     *            The expected text within the row.
     */
    private void validateAssertionRowText(WebElement row, ExecutionResult assertionResult, String... expectedTexts) {
        final List<WebElement> cells = row.findElements(By.tagName("td"));
        assertThat(cells).hasSize(expectedTexts.length + 1); // column 0 is the result indicator

        // Find the image in first cell
        final WebElement statusImage = cells.get(0).findElement(By.tagName("img"));
        final String srcAttribute = statusImage.getAttribute("src");
        assertThat(srcAttribute).isNotNull();
        switch (assertionResult) {
        case ERRORED:
            assertThat(srcAttribute).contains("icon_error_sml.gif");
            break;
        case FAILED:
            assertThat(srcAttribute).contains("icon_warning_sml.gif");
            break;
        case PASSED:
            assertThat(srcAttribute).contains("icon_success_sml.gif");
            break;
        default:
            throw new IllegalArgumentException("Unrecognized result: " + assertionResult);
        }

        int textIndex = 1;
        for (String expectedText : expectedTexts)
            assertThat(cells.get(textIndex++).getText()).as("Invalid text in column " + textIndex)
                    .isEqualTo(expectedText);
    }

    /**
     * Validate that code coverage data is properly displayed.
     * 
     * @param driver
     *            A {@link WebDriver} to be used to interact with the Maven site.
     * @param testDirectoryName
     *            The name of the test directory.
     * @param test
     *            The {@link CclUnitTest} to be validated.
     */
    private void validateCodeCoverage(WebDriver driver, String testDirectoryName, CclUnitTest test) {
        driver = navigateToCodeCoverage(driver, testDirectoryName);

        final Map<Integer, AssertionTest> assertions = new HashMap<Integer, AssertionTest>();
        for (AssertionTest assertion : test.getTests())
            assertions.put(Integer.valueOf(assertion.getLineNumber()), assertion);

        int elementIndex = 0;
        for (WebElement element : driver.findElements(By.className("code"))) {
            final AssertionTest assertion = assertions.get(Integer.valueOf(++elementIndex));
            assertThat(element.getText()).isEqualTo(assertion.getSourceCode());
            switch (assertion.getResult()) {
            case FAILED:
                assertThat(element.getAttribute("class")).isEqualTo("code failed-assert");
                break;
            case PASSED:
                assertThat(element.getAttribute("class")).isEqualTo("code passed-assert");
                break;
            case ERRORED:
                assertThat(element.getAttribute("class")).isEqualTo("code");
                break;
            default:
                throw new IllegalArgumentException("Unknown result: " + assertion);
            }
        }
    }

    /**
     * Verify that an element in a Cerreal Report can be expanded and collapsed.
     * 
     * @param parentElement
     *            The {@link WebElement} whose visibility is to be verified.
     * @param toToggleElement
     *            The {@link RenderedWebElement} whose visibility is to be toggled.
     * @param verificationCondition
     *            The {@link Condition} to be {@link Verification#verify(Object) evaluated} to true when the togglable
     *            element is visible. The value given to the {@code verify(Object)} method will be
     *            {@code toToggleElement}.
     * @throws Exception
     *             If any errors occur during the verification of the condition.
     */
    private void validateExpandableElement(WebElement parentElement, RenderedWebElement toToggleElement,
            Verification<RenderedWebElement> verificationCondition) throws Exception {
        final boolean initiallyVisible = isVisible(toToggleElement);
        final VisibilityToggleCondition condition = new VisibilityToggleCondition(initiallyVisible);

        if (initiallyVisible)
            verificationCondition.verify(toToggleElement);

        parentElement.click();
        assertThat(toToggleElement).is(condition);

        if (!initiallyVisible)
            verificationCondition.verify(toToggleElement);

        parentElement.click();
        assertThat(toToggleElement).is(condition);
    }

    /**
     * Validate that the summary of test executions.
     * 
     * @param driver
     *            The {@link WebDriver} used to interact with the Maven site.
     * @param tests
     *            A vararg array of {@link CclUnitTest} objects representing the tests expected to be in the summary.
     */
    private void validateSummary(WebDriver driver, CclUnitTest... tests) {
        int passedCount = 0;
        int failedCount = 0;
        int erroredCount = 0;
        for (CclUnitTest test : tests)
            switch (test.getResult()) {
            case ERRORED:
                erroredCount++;
                break;
            case FAILED:
                failedCount++;
                break;
            case PASSED:
                passedCount++;
                break;
            default:
                throw new IllegalArgumentException("Unknown test result: " + test);
            }

        final WebElement summaryTable = driver.findElements(By.className("bodyTable")).get(0);
        final WebElement tableRow = summaryTable.findElements(By.tagName("tr")).get(1); // ignore first row, as that's
                                                                                        // header
        final List<WebElement> summaryCells = tableRow.findElements(By.tagName("td"));
        assertThat(summaryCells.get(0).getText()).isEqualTo(Integer.toString(tests.length));
        assertThat(summaryCells.get(1).getText()).isEqualTo(Integer.toString(failedCount));
        assertThat(summaryCells.get(2).getText()).isEqualTo(Integer.toString(erroredCount));
        assertThat(summaryCells.get(3).getText()).isEqualTo(Integer.toString(passedCount));

        // Verify the calculation of passed percentages
        final double passedPercentage = Double.parseDouble(summaryCells.get(4).getText().replaceAll("%", ""));
        assertThat(passedPercentage).isEqualTo((((double) passedCount) / tests.length) * 100, Delta.delta(0.1));
    }

    /**
     * Write out listing data.
     * 
     * @param test
     *            The test whose listing data is to be written out.
     * @throws IOException
     *             If any errors occur while writing out the data.
     * @throws JAXBException
     *             If any errors occur while marshaling the data to XML.
     */
    private void writeListing(CclUnitTest test) throws IOException, JAXBException {
        final LISTING listingXml = toListing(getTestDirectory().getName(),
                test.getTests().toArray(new AssertionTest[] {}));

        final File listingXmlFile = new File(getTestResultsDirectory(), "listing.xml");
        final FileWriterWithEncoding listingWriter = new FileWriterWithEncoding(listingXmlFile, "utf-8");
        try {
            final JAXBContext context = JAXBContext.newInstance(LISTING.class);
            context.createMarshaller().marshal(listingXml, listingWriter);
        } finally {
            listingWriter.close();
        }
    }

    /**
     * Write test results to an XML file.
     * 
     * @param test
     *            The test whose listing data is to be written out.
     * @throws IOException
     *             If any errors occur while writing out the data.
     * @throws JAXBException
     *             If any errors occur while marshaling the data to XML.
     */
    private void writeTestResults(CclUnitTest test) throws IOException, JAXBException {
        final TESTCASE xmlTestCase = toTestCase(getTestDirectory().getName(), test);
        final File testCmlFile = new File(getTestResultsDirectory(), "test-results.xml");
        final FileWriterWithEncoding testResultsWriter = new FileWriterWithEncoding(testCmlFile, "utf-8");
        try {
            final JAXBContext context = JAXBContext.newInstance(TESTCASE.class);
            context.createMarshaller().marshal(xmlTestCase, testResultsWriter);
        } finally {
            testResultsWriter.close();
        }
    }

    /**
     * A simple interface designed to allow the verification of a condition.
     * 
     * @author Joshua Hyde
     * 
     */
    private interface Verification<T> {
        /**
         * Verify that a condition holds true.
         * 
         * @param object
         *            The object to be verified.
         * @throws Exception
         *             If any errors occur during the verification.
         */
        void verify(T object) throws Exception;
    }

    /**
     * A {@link Condition} that helps verify the toggle-able visibility of an element.
     * 
     * @author Joshua Hyde
     * 
     */
    private static class VisibilityToggleCondition extends Condition<Object> {
        private boolean wasVisible;

        /**
         * Create a condition.
         * 
         * @param isVisible
         *            A {@code boolean} indicating the initial visibility of the element.
         */
        public VisibilityToggleCondition(boolean isVisible) {
            this.wasVisible = isVisible;
        }

        @Override
        public boolean matches(Object value) {
            if (!(value instanceof RenderedWebElement))
                throw new IllegalArgumentException("Bad value: " + value);

            final RenderedWebElement element = (RenderedWebElement) value;
            try {
                return wasVisible ? !elementIsVisible(element) : elementIsVisible(element);
            } finally {
                wasVisible = !wasVisible;
            }
        }

        /**
         * Determine whether or not an element is visible.
         * 
         * @param toToggle
         *            The {@link RenderedWebElement} whose visibility is to be determined.
         * @return {@code true} if the element is visible; {@code false} if not.
         */
        private boolean elementIsVisible(RenderedWebElement toToggle) {
            return !"none".equals(toToggle.getValueOfCssProperty("display"));
        }

    }
}
