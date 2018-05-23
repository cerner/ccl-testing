package com.cerner.ccl.parser.text.record;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.SimpleDataTyped;
import com.cerner.ccl.parser.data.record.HierarchicalStructureMember;

/**
 * Definition of a typed field within a record structure.
 *
 * @author Joshua Hyde
 *
 */

public class StructureField extends SimpleDataTyped implements HierarchicalStructureMember {
    private final String name;
    private final int level;

    /**
     * Create a structure field.
     *
     * @param name
     *            The name of the field.
     * @param level
     *            The {@link #getLevel() level} of this member.
     * @param dataType
     *            The data type of the field.
     * @throws IllegalArgumentException
     *             If the given name is {@code null} or the level is less than 1.
     */
    public StructureField(final String name, final int level, final DataType dataType) {
        super(dataType);

        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        if (level < 1) {
            throw new IllegalArgumentException("Level cannot be less than 1: " + Integer.toString(level));
        }

        this.name = name;
        this.level = level;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof StructureField)) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        final StructureField other = (StructureField) obj;
        return new EqualsBuilder().append(name, other.name).isEquals();
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
        return 31 * super.hashCode() + name.hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
