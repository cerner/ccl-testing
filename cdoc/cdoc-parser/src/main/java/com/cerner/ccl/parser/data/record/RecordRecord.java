package com.cerner.ccl.parser.data.record;

import java.util.List;

/**
 * Definition of a class representing a CCL record structure record. For example, in the following definition:
 * 
 * <pre>
 * 1 status_data 
 *     2 status = c1 
 *     2 subeventstatus[1] 
 *       3 OperationName = c25
 * </pre>
 * 
 * ..."status_data" is a record.
 * 
 * @author Joshua Hyde
 * 
 */

public class RecordRecord extends AbstractParentRecordStructureMember {
    /**
     * Create a record without documentation.
     * 
     * @param name
     *            The name of the record.
     * @param level
     *            The {@link #getLevel() level} of this member.
     * @param children
     *            A {@link List} of {@link RecordStructureMember} objects representing the child members of this record.
     */
    public RecordRecord(final String name, final int level, final List<? extends RecordStructureMember> children) {
        this(name, level, null, children);
    }

    /**
     * Create a record.
     * 
     * @param name
     *            The name of the record.
     * @param level
     *            The {@link #getLevel() level} of this member.
     * @param description
     *            The description of the record.
     * @param children
     *            A {@link List} of {@link RecordStructureMember} objects representing the child members of this record.
     */
    public RecordRecord(final String name, final int level, final String description,
            final List<? extends RecordStructureMember> children) {
        super(name, level, description, children);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof RecordRecord && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
