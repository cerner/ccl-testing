package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.RecordStructureViolation;
import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} used to describe a situation where a record structure is not explicitly declared with
 * private or protect.
 * <p>
 *
 * @author Jeff Wiedemann
 */

public class UnprotectedRecordStructureDefinitionViolation implements RecordStructureViolation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("UNPROTECTED_RECORD_DEFINITION");

    private final String  recordStructureName;
    private final Integer lineNumber;

    /**
     * Create a violation.
     *
     * @param recordStructureName
     * 		A {@link String} representing the name of the record structure with the violation
     * @param lineNumber
     *      An {@link Integer} representing the line at which the violation was encountered, if applicable.
     * @throws IllegalArgumentException
     *      If any of the given objects, except for the line number, are {@code null}.
     */
    public UnprotectedRecordStructureDefinitionViolation(final String recordStructureName, final Integer lineNumber) {
        if (recordStructureName == null)
            throw new IllegalArgumentException("Record structure name cannot be null.");

        this.recordStructureName = recordStructureName;
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof UnprotectedRecordStructureDefinitionViolation))
            return false;

        final UnprotectedRecordStructureDefinitionViolation other = (UnprotectedRecordStructureDefinitionViolation) obj;
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
        return "Record [" + recordStructureName + "] was not declared with explicit scoping";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "Much like variables, record structure declarations are almost always required to be protected or private so that instances " +
                "where multiple structures of the same name containing different data are appropriately protected by their defined scope. Structures " +
                "defined in scripts which are recursively called as well as commonly named record structures are expecially prone to this scoping " +
                "problem. If you truely desire a globally scoped record structure, explicitly declare it with public, persist, or persistscript " +
                "and do so with caution.";
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
