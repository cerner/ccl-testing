package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.RecordStructureViolation;
import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} used to describe a situation where a list or record structure is defined with no child elements.
 * <p>
 *
 * @author Jeff Wiedemann
 */

public class EmptyListOrStructureDefinitionViolation implements RecordStructureViolation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("EMPTY_LIST_OR_STRUCTURE_DEFINITION");
    private final String recordStructureName;
    private final String fieldName;
    private final Integer lineNumber;

    /**
     * Create a violation.
     *
     * @param recordStructureName
     *            A {@link String} representing the name of the record structure with the violation
     * @param fieldName
     *            A {@link String} representing the name of the record field at which the violation was encountered
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     * @throws IllegalArgumentException
     *             If any of the given objects, except for the line number, are {@code null}.
     */
    public EmptyListOrStructureDefinitionViolation(final String recordStructureName, final String fieldName,
            final Integer lineNumber) {
        if (recordStructureName == null)
            throw new IllegalArgumentException("Record structure name cannot be null.");

        if (fieldName == null)
            throw new IllegalArgumentException("Field name cannot be null.");

        this.recordStructureName = recordStructureName;
        this.fieldName = fieldName;
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof EmptyListOrStructureDefinitionViolation))
            return false;

        final EmptyListOrStructureDefinitionViolation other = (EmptyListOrStructureDefinitionViolation) obj;
        return getRecordStructureName().equalsIgnoreCase(other.getRecordStructureName())
                && getFieldName().equalsIgnoreCase(other.getFieldName())
                && getLineNumber().equals(other.getLineNumber());
    }

    /**
     * {@inheritDoc}
     */
    public String getRecordStructureName() {
        return recordStructureName;
    }

    /**
     * @return The name of the fieldName variable representing the list or structure
     */
    public String getFieldName() {
        return fieldName;
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
        return "Record [" + recordStructureName + "] contains list or struct member [" + fieldName
                + "] which does not have any child elements";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "If a record structure declaration contains either a list or structure member with no child elements, there is almost certainly"
                + " a problem with the definition of the record. Either the depth of the child elements was defined incorrectly or the list/struct"
                + " is not needed";
    }

    /**
     * {@inheritDoc}
     */
    public ViolationId getViolationId() {
        return VIOLATION_ID;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(recordStructureName.toLowerCase(Locale.US))
                .append(fieldName.toLowerCase(Locale.US)).append(lineNumber).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
