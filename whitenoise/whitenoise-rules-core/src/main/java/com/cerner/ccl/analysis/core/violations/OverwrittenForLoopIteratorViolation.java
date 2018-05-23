package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} used to describe a situation where a standard for loop iterator increment appears to be overwritten
 * by an increment statement (i = i + 1) within the for loop
 * <p>
 *
 * @author Jeff Wiedemann
 */

public class OverwrittenForLoopIteratorViolation implements Violation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("OVERWRITTEN_LOOP_ITERATOR");

    private final String  iterator;
    private final Integer lineNumber;

    /**
     * Create a violation.
     *
     * @param iterator
     * 	The name of the iterator variable in the for loop whose value is being overwritten
     * @param lineNumber
     *            An {@link Integer} representing the line number where the violation was encountered, if applicable.
     * @throws IllegalArgumentException
     *             If any of the given objects, except for the line number, are {@code null}.
     */
    public OverwrittenForLoopIteratorViolation(final String iterator, final Integer lineNumber) {
        if (iterator == null)
            throw new IllegalArgumentException("Iterator cannot be null.");

        this.iterator = iterator;
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof OverwrittenForLoopIteratorViolation))
            return false;

        final OverwrittenForLoopIteratorViolation other = (OverwrittenForLoopIteratorViolation) obj;
        return getIterator().equalsIgnoreCase(other.getIterator()) && getLineNumber().equals(other.getLineNumber());
    }


    /**
     * @return The name of the iterator variable used in the for loop
     */
    public String getIterator() {
        return iterator;
    }

    /**
     * {@inheritDoc}
     */
    public Integer getLineNumber() {
        return lineNumber;
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationDescription() {
        return "For loop with suspicious overwriting of iterator [" + getIterator() + "] value";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "The iterator variable of a for loop is automatically incremented with each iteration of the loop. It is usually only recommended" +
        		" to modify the iterator variable to break from the loop, any other modification is usually not correct";
    }

    /**
     * {@inheritDoc}
     */
    public ViolationId getViolationId() {
        return VIOLATION_ID;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(iterator.toLowerCase(Locale.US)).append(lineNumber).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
