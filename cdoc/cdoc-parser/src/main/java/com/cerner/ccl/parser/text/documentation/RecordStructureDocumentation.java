package com.cerner.ccl.parser.text.documentation;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.record.InterfaceStructureType;

/**
 * Documentation of a record structure.
 *
 * @author Joshua Hyde
 *
 */

public class RecordStructureDocumentation extends AbstractDocumentation {
    private final List<Field> fields;
    private final InterfaceStructureType structureType;

    /**
     * Create documentation of a record structure.
     *
     * @param description
     *            The description of the record structure.
     * @param structureType
     *            A {@link InterfaceStructureType} enum representing the type of participation of this record structure
     *            in the input and output of the CCL script. If {@code null}, it is considered to not participate in
     *            such a relationship.
     * @param fields
     *            A {@link List} of {@link Field} objects representing the fields documented for the record structure.
     *            If {@code null}, an empty list is stored internally.
     */
    public RecordStructureDocumentation(final String description, final InterfaceStructureType structureType,
            final List<Field> fields) {
        super(description);

        this.structureType = structureType;
        this.fields = fields == null || fields.isEmpty() ? Collections.<Field> emptyList()
                : Collections.unmodifiableList(fields);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof RecordStructureDocumentation)) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        final RecordStructureDocumentation other = (RecordStructureDocumentation) obj;
        return new EqualsBuilder().append(structureType, other.structureType).append(fields, other.fields).isEquals();
    }

    /**
     * Get the list of documented fields.
     *
     * @return An immutable {@link List} of {@link Field} objects representing the fields of the record structure.
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * Get the participation of this record structure in the input/output of the CCL script.
     *
     * @return {@code null} if this record structure is not used in the interfacing of the CCL script with other
     *         scripts; otherwise, an {@link InterfaceStructureType} indicating the role this record structure plays.
     */
    public InterfaceStructureType getStructureType() {
        return structureType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + fields.hashCode();
        result = prime * result + ((structureType == null) ? 0 : structureType.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
