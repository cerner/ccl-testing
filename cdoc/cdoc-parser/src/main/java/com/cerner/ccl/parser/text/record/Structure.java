package com.cerner.ccl.parser.text.record;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.Named;
import com.cerner.ccl.parser.data.record.StructureMember;

/**
 * Definition of an object representing the definition of the structure of a record structure.
 *
 * @author Joshua Hyde
 *
 */

public class Structure implements Named {
    private final String name;
    private final List<StructureMember> members;

    /**
     * Create a structure.
     *
     * @param name
     *            The name of the structure.
     * @param members
     *            A {@link List} of {@link StructureMember} objects representing the members of this structure.
     * @throws IllegalArgumentException
     *             If either of the given objects are {@code null}.
     */
    public Structure(final String name, final List<? extends StructureMember> members) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        if (members == null) {
            throw new IllegalArgumentException("Members cannot be null.");
        }

        this.name = name;
        this.members = Collections.unmodifiableList(members);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Structure)) {
            return false;
        }

        final Structure other = (Structure) obj;
        return new EqualsBuilder().append(name, other.name).append(members, other.members).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get a root-level member of this structure.
     *
     * @param <T>
     *            The type of {@link StructureMember} expected to be returned.
     * @param index
     *            The index within the root level of the desired element.
     * @return A {@link StructureMember} representing the desired structure member.
     */
    @SuppressWarnings("unchecked")
    public <T extends StructureMember> T getRootLevelMember(final int index) {
        return (T) members.get(index);
    }

    /**
     * Get the number of members at the root level of the structure.
     *
     * @return The number of members at the root level of the structure.
     */
    public int getRootLevelMemberCount() {
        return members.size();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + members.hashCode();
        result = prime * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Retrieve the root-level members.
     *
     * @return A {@link List} of {@link StructureMember} objects representing the members of the structure defined at
     *         the root level.
     */
    protected List<StructureMember> getRootLevelMembers() {
        return members;
    }
}
