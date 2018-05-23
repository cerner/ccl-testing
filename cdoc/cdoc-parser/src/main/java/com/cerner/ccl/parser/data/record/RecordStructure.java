package com.cerner.ccl.parser.data.record;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.Described;
import com.cerner.ccl.parser.data.Named;

/**
 * Definition of a record structure within a CCL script.
 *
 * @author Joshua Hyde
 *
 */

public class RecordStructure implements Described, Named {
    private final String name;
    private final List<RecordStructureMember> members;
    private final String description;
    private final InterfaceStructureType structureType;

    /**
     * Construct a record structure without any documentation.
     *
     * @param name
     *            The name of the record structure.
     * @param members
     *            A {@link List} of {@link RecordStructureMember} objects representing the members of the record
     *            structure.
     * @throws IllegalArgumentException
     *             If the name or list of members is {@code null}.
     */
    public RecordStructure(final String name, final List<? extends RecordStructureMember> members) {
        this(name, members, null, null);
    }

    /**
     * Construct a record structure.
     *
     * @param name
     *            The name of the record structure.
     * @param members
     *            A {@link List} of {@link RecordStructureMember} objects representing the members of the record
     *            structure.
     * @param description
     *            The description of the record structure. If {@code null}, a blank string is stored internally.
     * @param structureType
     *            An {@link InterfaceStructureType} enum representing the type of interface record structure this is. If
     *            {@code null}, it assumed that either there is insufficient information to determine this or it simply
     *            is not involved in the interface definition of a CCL script.
     * @throws IllegalArgumentException
     *             If the name or list of members is {@code null}.
     */
    public RecordStructure(final String name, final List<? extends RecordStructureMember> members,
            final String description, final InterfaceStructureType structureType) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        if (members == null) {
            throw new IllegalArgumentException("Members cannot be null.");
        }

        this.name = name;
        this.members = Collections.unmodifiableList(members);
        this.description = description == null ? "" : description;
        this.structureType = structureType;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof RecordStructure)) {
            return false;
        }

        final RecordStructure other = (RecordStructure) obj;
        return new EqualsBuilder()
                .append(name.toLowerCase(Locale.getDefault()), other.name.toLowerCase(Locale.getDefault()))
                .append(members, other.members).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get a member of the record structure.
     *
     * @param <T>
     *            The type of {@link RecordStructureMember} that is expected to be returned.
     * @param index
     *            The index corresponding where in the structure the requested member appears.
     * @return A {@code T} instance of an object representing the requested member.
     */
    @SuppressWarnings("unchecked")
    public <T extends RecordStructureMember> T getRootLevelMember(final int index) {
        return (T) members.get(index);
    }

    /**
     * Get the number of members of the record structure. This count does not reflect all elements within the structure
     * down to the leaf nodes of its lists, but just the ones at the top-most level.
     *
     * @return The number of members at the root level of the record structure.
     */
    public int getRootLevelMemberCount() {
        return members.size();
    }

    /**
     * Get the interface structure type of this record structure.
     *
     * @return {@code null} if there is either insufficient information to determine this attribute of the record
     *         structure or if it does not at all participate in the interface definition of the CCL script; otherwise,
     *         an {@link InterfaceStructureType} enum representing the participation of this record structure in the CCL
     *         script's behavior as an interface.
     */
    public InterfaceStructureType getStructureType() {
        return structureType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + members.hashCode();
        result = prime * result + name.toLowerCase(Locale.getDefault()).hashCode();
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Get the members of this record structure.
     *
     * @return A {@link List} of {@link RecordStructureMember} objects representing the members of this record
     *         structure.
     */
    protected List<RecordStructureMember> getRootLevelMembers() {
        return members;
    }
}
