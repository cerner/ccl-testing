package com.cerner.ccl.parser.data.record;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Definition of an include file used to help define the structure of a record structure.
 * <p>
 * This object intentionally exposes no way to store documentation about the include file since, at this time, there is
 * no specified way to document included files.
 *
 * @author Joshua Hyde
 *
 */

public class RecordInclude implements RecordStructureMember {
    private final String filename;

    /**
     * Create an include file.
     *
     * @param filename
     *            The filename of the include file.
     */
    public RecordInclude(final String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null.");
        }

        this.filename = filename;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof RecordInclude)) {
            return false;
        }

        final RecordInclude other = (RecordInclude) obj;
        return new EqualsBuilder().append(filename, other.filename).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return filename;
    }

    @Override
    public int hashCode() {
        return filename.hashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
