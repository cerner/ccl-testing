package com.cerner.ccl.parser.data.record;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This class represents a member of a record structure that has child structure members.
 *
 * @author Joshua Hyde
 *
 */

public abstract class AbstractParentRecordStructureMember implements HierarchicalRecordStructureMember {
    private final List<RecordStructureMember> children;
    private final String name;
    private final String description;
    private final int level;

    /**
     * Create a parent member with documentation.
     *
     * @param name
     *            The name of the member.
     * @param level
     *            The {@link #getLevel() level} of this field.
     * @param description
     *            A description of the member.
     * @param children
     *            A {@link List} of {@link RecordStructureMember} objects representing the child members of this member.
     * @throws IllegalArgumentException
     *             If the given name or list of members is {@code null} or the level is less than 1.
     */
    public AbstractParentRecordStructureMember(final String name, final int level, final String description,
            final List<? extends RecordStructureMember> children) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        if (children == null) {
            throw new IllegalArgumentException("Children members cannot be null.");
        }

        if (level < 1) {
            throw new IllegalArgumentException("Level cannot be less than 1: " + Integer.toString(level));
        }

        this.name = name;
        this.description = description == null ? "" : description;
        this.children = Collections.unmodifiableList(children);
        this.level = level;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        final AbstractParentRecordStructureMember other = (AbstractParentRecordStructureMember) obj;
        return new EqualsBuilder()
                .append(name.toLowerCase(Locale.getDefault()), other.name.toLowerCase(Locale.getDefault()))
                .append(children, other.children).isEquals();
    }

    /**
     * Get a member that is a child to this member.
     *
     * @param <T>
     *            The type of {@link RecordStructureMember} to be returned.
     * @param index
     *            The index of the element as it appears beneath this member.
     * @return A {@link RecordStructureMember} representing the requested child member beneath this member.
     */
    @SuppressWarnings("unchecked")
    public <T extends RecordStructureMember> T getChildMember(final int index) {
        return (T) getChildren().get(index);
    }

    /**
     * Get the number of child elements of this member.
     *
     * @return The number of child elements directly beneath this member.
     */
    public int getChildMemberCount() {
        return getChildren().size();
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
        result = prime * result + name.toLowerCase(Locale.getDefault()).hashCode();
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * Get the child elements of this member.
     *
     * @return A {@link List} of {@link RecordStructureMember} objects representing the children of this member.
     */
    protected List<RecordStructureMember> getChildren() {
        return children;
    }
}
