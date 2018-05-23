package com.cerner.ccl.analysis.core.violations;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} indicating that a select statement is detected where filesort and maxqual options have been found together
 * in the select
 *
 * @author Jeff Wiedemann
 */

public class FilesortAndMaxqualViolation implements Violation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("FILESORT_AND_MAXQUAL");
    private final Integer lineNumber;

    /**
     * Create a violation.
     *
     * @param lineNumber
     *            An {@link Integer} representing the line of the beginning of the while loop with the violation, if applicable.
     */
    public FilesortAndMaxqualViolation(final Integer lineNumber) {
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof FilesortAndMaxqualViolation))
            return false;

        final FilesortAndMaxqualViolation other = (FilesortAndMaxqualViolation) obj;
        return getLineNumber().equals(other.getLineNumber());
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
        return "Select with conflicting options, filesort and maxqual, used together";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "The filesort option causes all order by statements for this select to be performed on the result set in CCL as " +
        		"opposed to Oracle; therefore the maxqual will limit the result set by the specified number, but will do so in an arbitrary " +
        		"fashion. If you require your data to be ordered prior to limiting the result set with the maxqual option, the filesort " +
        		"cannot be used. This behavior might become more apparent in CBO than RBO.";
    }

    /**
     * {@inheritDoc}
     */
    public ViolationId getViolationId() {
        return VIOLATION_ID;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(lineNumber).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
