package com.cerner.ccl.parser.text.record;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.record.HierarchicalStructureMember;
import com.cerner.ccl.parser.data.record.StructureMember;

/**
 * Definition of a structure in a CCL record structure that has children elements.
 *
 * @author Joshua Hyde
 *
 */

public abstract class AbstractParentStructure implements HierarchicalStructureMember {
    private final List<StructureMember> children = new ArrayList<StructureMember>();
    private final String name;
    private final int level;

    /**
     * Create a parent member.
     *
     * @param name
     *            The name of the member.
     * @param level
     *            The {@link #getLevel() level} of this member.
     * @throws IllegalArgumentException
     *             If the given name is {@code null} or the level is less than 1.
     */
    public AbstractParentStructure(final String name, final int level) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        if (level < 1) {
            throw new IllegalArgumentException("Level cannot be less than 1: " + Integer.toString(level));
        }

        this.name = name;
        this.level = level;
    }

    /**
     * Add a child member to this member.
     *
     * @param child
     *            A {@link StructureMember} to be added to the list of children members immediately under this member.
     * @throws IllegalArgumentException
     *             If the given child is {@code null}.
     */
    public void addChildMember(final StructureMember child) {
        if (child == null) {
            throw new IllegalArgumentException("Child cannot be null.");
        }

        getChildMembers().add(child);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof AbstractParentStructure)) {
            return false;
        }

        final AbstractParentStructure other = (AbstractParentStructure) obj;
        return new EqualsBuilder().append(name, other.name).append(children, other.children).isEquals();
    }

    /**
     * Get a child member.
     *
     * @param <T>
     *            The type of {@link StructureMember} expected to be returned.
     * @param index
     *            The index corresponding to the index of the child immediately underneath this member.
     * @return A {@link StructureMember} object representing the requested member.
     */
    @SuppressWarnings("unchecked")
    public <T extends StructureMember> T getChildMember(final int index) {
        return (T) getChildMembers().get(index);
    }

    /**
     * Get the number of children members immediately under this member.
     *
     * @return The number of children members immediately under this member.
     */
    public int getChildMemberCount() {
        return getChildMembers().size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLevel() {
        return level;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + children.hashCode();
        result = prime * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Get the children members immediately beneath this member.
     *
     * @return A {@link List} of {@link StructureMember} objects representing the children immediately beneath this
     *         member.
     */
    protected List<StructureMember> getChildMembers() {
        return children;
    }
}
