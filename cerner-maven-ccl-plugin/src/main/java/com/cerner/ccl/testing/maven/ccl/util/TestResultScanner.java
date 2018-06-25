package com.cerner.ccl.testing.maven.ccl.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cerner.ccl.testing.maven.ccl.data.Assertion;
import com.cerner.ccl.testing.maven.ccl.data.UnitTest;
import com.cerner.ccl.testing.maven.ccl.data.enums.AssertionStatus;
import com.cerner.ccl.testing.maven.ccl.data.enums.UnitTestStatus;
import com.cerner.ccl.testing.maven.ccl.util.factory.DocumentFactory;

/**
 * An object that scans an XML string for indicates that a test failed.
 *
 * @author Joshua Hyde
 *
 */

public class TestResultScanner {
    /**
     * Scan an XML string for indications of a test failure.
     *
     * @param xml
     *            The XML string to be scanned.
     * @return A {@link Collection} of {@link UnitTest} objects representing the unit tests that indicate failure within
     *         the given XML.
     */
    public Collection<UnitTest> scanForFailures(final String xml) {
        final Document document = DocumentFactory.create(xml);
        final NodeList nodes = document.getElementsByTagName("TEST");

        final Collection<UnitTest> failedTests = new ArrayList<UnitTest>();
        for (int i = 0, size = nodes.getLength(); i < size; i++)
            failedTests.addAll(collectFailures(nodes.item(i)));
        return failedTests;
    }

    /**
     * Assert that a <TEST/> tag has no assertions within it that are marked as failed.
     *
     * @param testNode
     *            A {@link Node} object representing a <TEST/> node in a test execution XML document from the CCL unit
     *            testing framework.
     * @return A {@link Collection} of {@link UnitTest} objects representing the unit tests that indicate failure within
     *         the given XML.
     */
    private Collection<UnitTest> collectFailures(final Node testNode) {
        if (!testNode.hasChildNodes())
            return Collections.<UnitTest>emptyList();

        final Collection<UnitTest> tests = new ArrayList<UnitTest>();
        final NodeList children = testNode.getChildNodes();
        Node resultNode = null;
        Node testNameNode = null;
        for (int i = 0, size = children.getLength(); i < size; i++) {
            final Node currentNode = children.item(i);
            if ("RESULT".equalsIgnoreCase(currentNode.getNodeName()))
                resultNode = currentNode;
            else if ("NAME".equalsIgnoreCase(currentNode.getNodeName()))
                testNameNode = currentNode;
        }

        if (resultNode != null && !assertPassed(resultNode)) {
            final String testName = testNameNode == null ? "<Test Name Unavailable>" : testNameNode.getTextContent();
            final UnitTest test = new UnitTest(testName, UnitTestStatus.FAILED);
            tests.add(test);

            for (final Assertion failure : getFailedAssertions(testNode))
                test.addAssertion(failure);
        }

        return tests;
    }

    /**
     * Determine whether a result node indicates that a step has failed.
     *
     * @param resultNode
     *            A {@link Node} object representing <RESULT/> tag.
     * @return {@code true} if the node represents a PASSED status.
     */
    private boolean assertPassed(final Node resultNode) {
        return "PASSED".equalsIgnoreCase(resultNode.getTextContent());
    }

    /**
     * Get all assertions within a test that indicate they have failed.
     *
     * @param testNode
     *            A {@link Node} data object representing an <TEST/> tag.
     * @return A {@link Collection} of {@link AssertionFailure} objects representing the assertions that failed within
     *         the given tag.
     */
    private Collection<Assertion> getFailedAssertions(final Node testNode) {
        final NodeList kids = testNode.getChildNodes();
        for (int i = 0, size = kids.getLength(); i < size; i++)
            if ("ASSERTS".equalsIgnoreCase(kids.item(i).getNodeName()))
                return getFailedAssertions(kids.item(i).getChildNodes());

        return Collections.<Assertion> emptyList();
    }

    /**
     * Get all assertions within a test that have failed.
     *
     * @param nodeList
     *            A {@link NodeList} object representing a set of <ASSERT/> tags beneath an <ASSERTS/> tag.
     * @return A {@link Collection} of {@link AssertionFailure} object representing the assertions that failed.
     */
    private Collection<Assertion> getFailedAssertions(final NodeList nodeList) {
        final List<Assertion> failures = new ArrayList<Assertion>();
        for (int i = 0, size = nodeList.getLength(); i < size; i++) {
            final Node currentNode = nodeList.item(i);

            if (!"ASSERT".equalsIgnoreCase(currentNode.getNodeName()) || !currentNode.hasChildNodes())
                continue;

            Node lineNode = null;
            Node contextNode = null;
            Node resultNode = null;
            Node testNode = null;

            final NodeList assertList = currentNode.getChildNodes();
            for (int j = 0, kidSize = assertList.getLength(); j < kidSize; j++) {
                final Node currentKid = assertList.item(j);
                if ("RESULT".equalsIgnoreCase(currentKid.getNodeName()))
                    resultNode = currentKid;
                else if ("LINENUMBER".equalsIgnoreCase(currentKid.getNodeName()))
                    lineNode = currentKid;
                else if ("CONTEXT".equalsIgnoreCase(currentKid.getNodeName()))
                    contextNode = currentKid;
                else if ("TEST".equalsIgnoreCase(currentKid.getNodeName()))
                    testNode = currentKid;
            }

            // If no result was found, move on
            if (resultNode != null && !assertPassed(resultNode)) {
                final int lineNumber = lineNode == null ? -1 : Integer.parseInt(lineNode.getTextContent());
                final String context = contextNode == null ? "null" : contextNode.getTextContent();
                failures.add(new Assertion(testNode.getTextContent(), context, AssertionStatus.FAILED, lineNumber));
            }
        }

        if (failures.isEmpty()) {
            return Collections.<Assertion> emptyList();
        }
        return failures;
    }
}
