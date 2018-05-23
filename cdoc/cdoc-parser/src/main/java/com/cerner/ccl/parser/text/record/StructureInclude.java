package com.cerner.ccl.parser.text.record;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.record.StructureMember;

/**
 * The definition of a bean representing when an include file (other than {@code status_block.inc}) is used within the
 * record structure definition.
 *
 * @author Joshua Hyde
 *
 */

public class StructureInclude implements StructureMember {
    private final String name;

    /**
     * Create an include file.
     *
     * @param name
     *            The filename of the include file.
     */
    public StructureInclude(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        this.name = name;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof StructureInclude)) {
            return false;
        }

        final StructureInclude other = (StructureInclude) obj;
        return new EqualsBuilder().append(name, other.name).isEquals();
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
        return name.hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
