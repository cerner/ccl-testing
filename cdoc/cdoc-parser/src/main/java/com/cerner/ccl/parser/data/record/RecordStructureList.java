package com.cerner.ccl.parser.data.record;

import java.util.List;

/**
 * This class represents a list within a record structure. For example, given the following structure:
 * 
 * <pre>
 *   record request (
 *      1 requested[*]
 *          2 person_id = f8
 *   )
 * </pre>
 * 
 * ...then "requested" would be represented by this class. This should be assumed to be a dynamically-sized list.
 * 
 * @author Joshua Hyde
 * 
 */
public class RecordStructureList extends AbstractParentRecordStructureMember {
    /**
     * Create a record structure list with no documentation.
     * 
     * @param name
     *            The name of the record structure.
     * @param level
     *            The {@link #getLevel() level} of this member.
     * @param children
     *            A {@link List} of {@link RecordStructureMember} objects representing the child members of this list.
     */
    public RecordStructureList(final String name, final int level,
            final List<? extends RecordStructureMember> children) {
        this(name, level, null, children);
    }

    /**
     * Create a list with documentation.
     * 
     * @param name
     *            The name of the list.
     * @param level
     *            The {@link #getLevel() level} of this member.
     * @param description
     *            A description of the list.
     * @param children
     *            A {@link List} of {@link RecordStructureMember} objects representing the child members of this list.
     */
    public RecordStructureList(final String name, final int level, final String description,
            final List<? extends RecordStructureMember> children) {
        super(name, level, description, children);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof RecordStructureList && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
