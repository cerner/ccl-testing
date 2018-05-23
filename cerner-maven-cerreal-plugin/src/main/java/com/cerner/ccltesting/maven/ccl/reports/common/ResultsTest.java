package com.cerner.ccltesting.maven.ccl.reports.common;

import org.w3c.dom.Node;

import com.cerner.ccltesting.xsl.XslAPI;

/**
 * Stores the results associated with a single test within a test case
 *
 * @author Jeff Wiedemann
 *
 */
public class ResultsTest {
    /**
     * Enumerations of the possible test results.
     *
     * @author Jeff Wiedemann
     *
     */
    public enum TestResult {
        /**
         * The test passed.
         */
        PASSED,
        /**
         * The test failed.
         */
        FAILED,
        /**
         * The test errored.
         */
        ERRORED;
    }

    private final Node testNode;
    private final TestResult result;
    private final String name;
    private final int totalAsserts;
    private final int totalErrors;

    /**
     * Constructor for a test case test
     *
     * @param testNode
     *            The xml test node representing the data for this test
     */
    public ResultsTest(Node testNode) {
        this.testNode = testNode;
        name = XslAPI.getNodeXPathValue(testNode, "NAME");
        totalAsserts = Integer.parseInt(XslAPI.getNodeXPathValue(testNode, "count(./ASSERTS/ASSERT)"));
        totalErrors = Integer.parseInt(XslAPI.getNodeXPathValue(testNode, "count(./ERRORS/ERROR)"));
        result = TestResult.valueOf(XslAPI.getNodeXPathValue(testNode, "RESULT"));

    }

    /**
     * @return The total number of assert statements for the test
     */
    public int getAssertCount() {
        return totalAsserts;
    }

    /**
     * @return The total number of errors for the test (if the test result was ERRORED)
     */
    public int getErrorCount() {
        return totalErrors;
    }

    /**
     * @return The result of the test: Passed, Failed, or Errored
     */
    public TestResult getResult() {
        return result;
    }

    /**
     * @return The name of the test
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the result of an assert statement: Passed, Failed, or Errored
     *
     * @param assertIdx
     *            The index into the list of asserts for the desired assert
     * @return The result for the assert
     */
    public TestResult getAssertResult(int assertIdx) {
        return TestResult.valueOf(XslAPI.getNodeXPathValue(testNode, "./ASSERTS/ASSERT[" + assertIdx + "]/RESULT"));
    }

    /**
     * Gets the actual test that was performed for an assert statement
     *
     * @param assertIdx
     *            The index into the list of asserts for the desired assert
     * @return The assert test as a string. example: 1.0000000 &lt; 2.0000000
     */
    public String getAssertTest(int assertIdx) {
        return XslAPI.getNodeXPathValue(testNode, "./ASSERTS/ASSERT[" + assertIdx + "]/TEST");
    }

    /**
     * Gets the user-defined context string of an assert specified in the test.inc source code
     *
     * @param assertIdx
     *            The index into the list of asserts for the desired assert
     * @return The assert context string
     */
    public String getAssertContext(int assertIdx) {
        return XslAPI.getNodeXPathValue(testNode, "./ASSERTS/ASSERT[" + assertIdx + "]/CONTEXT");
    }

    /**
     * Gets the source code line number where this assert is located
     *
     * @param assertIdx
     *            The index into the list of asserts for the desired assert
     * @return The assert source code line number
     */
    public int getAssertSourceCodeLineNumber(int assertIdx) {
        return Integer.parseInt(XslAPI.getNodeXPathValue(testNode, "./ASSERTS/ASSERT[" + assertIdx + "]/LINENUMBER"));
    }

    /**
     * Gets the source code line number where this error occurred
     *
     * @param errorIdx
     *            The index into the list of errors for the desired error
     * @return The source code line number of the error
     */
    public int getErrorLineNumber(int errorIdx) {
        return Integer.parseInt(XslAPI.getNodeXPathValue(testNode, "./ERRORS/ERROR[" + errorIdx + "]/LINENUMBER"));
    }

    /**
     * Gets the CCL-E text of this error
     *
     * @param errorIdx
     *            The index into the list of errors for the desired error
     * @return The error text of the CCL-E error
     */
    public String getErrorText(int errorIdx) {
        return XslAPI.getNodeXPathValue(testNode, "./ERRORS/ERROR[" + errorIdx + "]/ERRORTEXT");
    }
}
