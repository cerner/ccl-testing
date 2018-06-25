package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.RecordStructureViolation;
import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} used to describe a situation where a record structure is not explicitly declared with private or
 * protect.
 * <p>
 *
 * @author Jeff Wiedemann
 */

public class FreedRecordStructureViolation implements RecordStructureViolation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("FREED_RECORD_STRUCTURE");

    private final String recordStructureName;
    private final Integer lineNumber;

    /**
     * Create a violation.
     *
     * @param recordStructureName
     *            A {@link String} representing the name of the record structure with the violation
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     * @throws IllegalArgumentException
     *             If any of the given objects, except for the line number, are {@code null}.
     */
    public FreedRecordStructureViolation(final String recordStructureName, final Integer lineNumber) {
        if (recordStructureName == null)
            throw new IllegalArgumentException("Record structure name cannot be null.");

        this.recordStructureName = recordStructureName;
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof FreedRecordStructureViolation))
            return false;

        final FreedRecordStructureViolation other = (FreedRecordStructureViolation) obj;
        return getRecordStructureName().equalsIgnoreCase(other.getRecordStructureName())
                && getLineNumber().equals(other.getLineNumber());
    }

    public String getRecordStructureName() {
        return recordStructureName;
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
        return "Record [" + recordStructureName + "] was unnecessarily freed";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "Record structures do not need to be freed. The memory they consume is freed automatically when the structure goes out of scope. "
                + "If the structure you are freeing was declared by a parent script, this free statement could maliciously free the record while the "
                + "parent script still depends on it's existance. Use appropriate scoping of your structure declarations to avoid this questionable "
                + "programming practice.";
    }

    /**
     * {@inheritDoc}
     */
    public ViolationId getViolationId() {
        return VIOLATION_ID;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(recordStructureName.toLowerCase(Locale.US)).append(lineNumber).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
