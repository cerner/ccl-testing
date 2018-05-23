package com.cerner.ccltesting.maven.ccl.util;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import com.cerner.ccltesting.maven.ccl.data.Assertion;
import com.cerner.ccltesting.maven.ccl.data.UnitTest;
import com.cerner.ccltesting.maven.ccl.data.enums.AssertionStatus;
import com.cerner.ccltesting.maven.ccl.data.enums.UnitTestStatus;

/**
 * Unit test for {@link TestResultScanner}.
 *
 * @author Joshua Hyde
 *
 */

public class TestResultScannerTest {
    /**
     * Read an input stream as XML.
     *
     * @param stream
     *            The {@link InputStream} to be converted to XML.
     * @return The XML in the given stream.
     */
    private static String readXml(final InputStream stream) {
        List<?> xml;
        try {
            xml = IOUtils.readLines(stream, "UTF-8");
        } catch (final IOException e) {
            throw new RuntimeException("Failed to read XML", e);
        }
        final StringBuilder builder = new StringBuilder();
        for (final Object o : xml)
            builder.append(o);
        return builder.toString();
    }

    /**
     * Verify that a test failure is handled appropriately.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testScanForFailures() throws Exception {
        final String xml = readXml(TestResultScannerTest.class.getResourceAsStream("failedTestResults.xml"));
        final Collection<UnitTest> tests = new TestResultScanner().scanForFailures(xml);
        assertThat(tests).hasSize(1);
        final UnitTest test = tests.iterator().next();
        assertThat(test.getName()).isEqualTo("TESTDIVIDEBYZERO");
        assertThat(test.getStatus()).isEqualTo(UnitTestStatus.FAILED);

        assertThat(test.getPassedAssertions()).isEmpty();
        final Assertion line810Failure = new Assertion("1000.000000=-1.000000", "testDivide#failed",
                AssertionStatus.FAILED, 810);
        final Assertion line812Failure = new Assertion("2.000000=-1.000000", "testDivide#anotherFailed",
                AssertionStatus.FAILED, 812);
        assertThat(test.getFailedAssertions()).containsOnly(line810Failure, line812Failure);
    }

    /**
     * Test that the scanner does not indicate that a failure occurs when none is present in the XML file.
     *
     * @throws Exception
     *             If an error occurs during the test run.
     */
    @Test
    public void testScanForFailuresNone() throws Exception {
        final String xml = readXml(TestResultScannerTest.class.getResourceAsStream("passedTestResults.xml"));
        assertThat(new TestResultScanner().scanForFailures(xml)).isEmpty();
    }

    /**
     * Verify that the scanner returns an empty collection if the {@code <TEST
     * />} elements have no child elements.
     */
    @Test
    public void testBachelorTest() {
        final String xml = "<TESTCASE><TEST /></TESTCASE>";
        assertThat(new TestResultScanner().scanForFailures(xml)).isEmpty();
    }

    /**
     * Verify that, if no name for the test is present in the XML, a default is assumed.
     */
    @Test
    public void testNoNameTest() {
        final String xml = readXml(getClass().getResourceAsStream("failedNoNameTest.xml"));
        final Collection<UnitTest> tests = new TestResultScanner().scanForFailures(xml);

        assertThat(tests).hasSize(1);
        final UnitTest test = tests.iterator().next();
        assertThat(test.getName()).isEqualTo("<Test Name Unavailable>");
    }

    /**
     * Test that a test with no {@code <ASSERTS />} tags, it returns the correct data.
     */
    @Test
    public void testNoAsserts() {
        final String xml = readXml(getClass().getResourceAsStream("failedNoAsserts.xml"));
        final Collection<UnitTest> tests = new TestResultScanner().scanForFailures(xml);

        assertThat(tests).hasSize(1);
        final UnitTest test = tests.iterator().next();
        assertThat(test.getFailedAssertions()).isEmpty();
        assertThat(test.getPassedAssertions()).isEmpty();
    }
}
