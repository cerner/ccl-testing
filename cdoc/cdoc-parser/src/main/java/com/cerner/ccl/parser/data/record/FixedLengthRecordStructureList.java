package com.cerner.ccl.parser.data.record;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This extension of {@link AbstractParentRecordStructureMember} represents a fixed length list, such as the following
 * featured in this example record structure:
 *
 * <pre>
 *  record request (
 *      1 fixed_length_list[1]
 *          2 status = c1
 *  )
 * </pre>
 *
 * @author Joshua Hyde
 *
 */

public class FixedLengthRecordStructureList extends RecordStructureList {
    private final int listSize;

    /**
     * Create a list without documentation.
     *
     * @param name
     *            The name of the list.
     * @param level
     *            The {@link #getLevel() level} of this member.
     * @param children
     *            A {@link List} of {@link RecordStructureMember} objects representing the members of this list.
     * @param listSize
     *            The size of the list.
     * @throws IllegalArgumentException
     *             If the given list size is negative.
     */
    public FixedLengthRecordStructureList(final String name, final int level,
            final List<RecordStructureMember> children, final int listSize) {
        this(name, level, null, children, listSize);
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
     *            A {@link List} of {@link RecordStructureMember} objects representing the members of this list.
     * @param listSize
     *            The size of the list.
     * @throws IllegalArgumentException
     *             If the given list size is negative.
     */
    public FixedLengthRecordStructureList(final String name, final int level, final String description,
            final List<RecordStructureMember> children, final int listSize) {
        super(name, level, description, children);

        if (listSize < 0) {
            throw new IllegalArgumentException("List size cannot be negative: " + Integer.toString(listSize));
        }

        this.listSize = listSize;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof FixedLengthRecordStructureList)) {
            return false;
        }

        final FixedLengthRecordStructureList other = (FixedLengthRecordStructureList) obj;
        return new EqualsBuilder().append(listSize, other.listSize).isEquals();
    }

    /**
     * Get the size of the list.
     *
     * @return The size of the list.
     */
    public int getListSize() {
        return listSize;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + listSize;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
