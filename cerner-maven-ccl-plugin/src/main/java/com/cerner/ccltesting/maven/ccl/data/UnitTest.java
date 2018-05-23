package com.cerner.ccltesting.maven.ccl.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.cerner.ccltesting.maven.ccl.data.enums.UnitTestStatus;

/**
 * A data object to represent a unit test.
 *
 * @author Joshua Hyde
 *
 */

public class UnitTest {
    private final Collection<Assertion> passedAssertions = new ArrayList<Assertion>();
    private final Collection<Assertion> failedAssertions = new ArrayList<Assertion>();
    private final String name;
    private final UnitTestStatus status;

    /**
     * Create a unit test.
     *
     * @param name
     *            The name of the test.
     * @param status
     *            A {@link UnitTestStatus} enum representing the status of the unit test.
     */
    public UnitTest(final String name, final UnitTestStatus status) {
        this.name = name;
        this.status = status;
    }

    /**
     * Add an assertion.
     *
     * @param assertion
     *            An {@link Assertion} data object.
     * @throws IllegalArgumentException
     *             If the status of the given assertion is not
     *             {@link com.cerner.ccltesting.maven.ccl.data.enums.AssertionStatus#PASSED} or
     *             {@link com.cerner.ccltesting.maven.ccl.data.enums.AssertionStatus#FAILED}.
     */
    public void addAssertion(final Assertion assertion) {
        switch (assertion.getStatus()) {
        case PASSED:
            passedAssertions.add(assertion);
            break;
        case FAILED:
            failedAssertions.add(assertion);
            break;
        default:
            throw new IllegalArgumentException("Unrecognized assertion status: " + assertion.getStatus());
        }
    }

    /**
     * Get a list of all assertions that failed for this test.
     *
     * @return A {@link Collection} of {@link Assertion} data objects representing the failed assertions within this
     *         test.
     */
    public Collection<Assertion> getFailedAssertions() {
        if (failedAssertions.isEmpty())
            return Collections.<Assertion> emptyList();

        return Collections.unmodifiableCollection(failedAssertions);
    }

    /**
     * Get the name of the test.
     *
     * @return The name of the test.
     */
    public String getName() {
        return name;
    }

    /**
     * Get a list of all assertions that passed for this test.
     *
     * @return A {@link Collection} of {@link Assertion} data objects representing the passed assertions within this
     *         test.
     */
    public Collection<Assertion> getPassedAssertions() {
        if (passedAssertions.isEmpty())
            return Collections.<Assertion> emptyList();

        return Collections.unmodifiableCollection(passedAssertions);
    }

    /**
     * Get the status of this unit test.
     *
     * @return A {@link UnitTestStatus} enum representing the status of this unit test.
     */
    public UnitTestStatus getStatus() {
        return status;
    }
}
