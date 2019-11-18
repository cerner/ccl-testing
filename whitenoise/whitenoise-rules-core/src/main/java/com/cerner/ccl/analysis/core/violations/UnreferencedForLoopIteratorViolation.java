package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} used to describe a situation where a for loop is created but the iterator variable is not
 * reference anywhere within the for loop code
 * <p>
 *
 * @author Jeff Wiedemann
 */

public class UnreferencedForLoopIteratorViolation implements Violation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("UNREFERENCED_FOR_LOOP_ITERATOR");

    private final String iterator;
    private final Integer lineNumber;

    /**
     * Create a violation.
     *
     * @param iterator
     *            The name of the for loop variable.
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     * @throws IllegalArgumentException
     *             If any of the given objects, except for the line number, are {@code null}.
     */
    public UnreferencedForLoopIteratorViolation(final String iterator, final Integer lineNumber) {
        if (iterator == null) {
            throw new IllegalArgumentException("Iterator cannot be null.");
        }

        this.iterator = iterator;
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof UnreferencedForLoopIteratorViolation)) {
            return false;
        }

        final UnreferencedForLoopIteratorViolation other = (UnreferencedForLoopIteratorViolation) obj;
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
    @Override
    public Integer getLineNumber() {
        return lineNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViolationDescription() {
        return "For loop with no reference to iterator [" + getIterator() + "] within loop code";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViolationExplanation() {
        return "If for loop for(idx = 1 to 10) is created and idx is not referenced anywhere in the for loop, the"
                + " loop is most likely incorrectly coded";
    }

    /**
     * {@inheritDoc}
     */
    @Override
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
