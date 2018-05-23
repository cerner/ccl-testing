package com.cerner.ccltesting.maven.ccl.exception;

import java.util.Collection;
import java.util.Iterator;

import org.apache.maven.plugin.MojoExecutionException;

import com.cerner.ccltesting.maven.ccl.data.UnitTest;

/**
 * An exception to represent that a test run has failed.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("serial")
public class TestFailureException extends MojoExecutionException {
    /**
     * Create an exception.
     *
     * @param failedTests
     *            A {@link Collection} of {@link UnitTest} objects representing the tests that failed.
     */
    public TestFailureException(final Collection<UnitTest> failedTests) {
        super(String.format("%d test(s) failed: %s", failedTests.size(), buildTestList(failedTests)));
    }

    /**
     * Build the message to present the list of tests that failed.
     *
     * @param failedTests
     *            A {@link Collection} of {@link UnitTest} objects representing the tests that failed.
     * @return A {@code String} that is a list of the failed tests' names in brackets.
     */
    private static String buildTestList(final Collection<UnitTest> failedTests) {
        if (failedTests.isEmpty())
            return "[]";

        final StringBuilder listBuilder = new StringBuilder();
        listBuilder.append('[');
        final Iterator<UnitTest> it = failedTests.iterator();
        listBuilder.append(it.next().getName());
        while (it.hasNext())
            listBuilder.append(',').append(it.next().getName());
        return listBuilder.append(']').toString();
    }
}
