package com.cerner.ccl.parser.text.record;

import com.cerner.ccl.parser.data.record.RecordRecord;

/**
 * A bean representing a record member within a record structure.
 * 
 * @author Joshua Hyde
 * @see RecordRecord
 */

public class StructureRecord extends AbstractParentStructure {
    /**
     * Create a record.
     * 
     * @param name
     *            The name of the record.
     * @param level
     *            The {@link #getLevel() level} of this member.
     */
    public StructureRecord(final String name, final int level) {
        super(name, level);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof StructureRecord && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
