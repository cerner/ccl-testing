package com.cerner.ccl.parser.text.record;

/**
 * Definition of an object representing a dynamically-sized list in a CCL record structure.
 * 
 * @author Joshua Hyde
 * 
 */

public class StructureList extends AbstractParentStructure {
    /**
     * Create a list.
     * 
     * @param name
     *            The name of the list.
     * @param level
     *            The {@link #getLevel() level} of this member.
     */
    public StructureList(final String name, final int level) {
        super(name, level);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof StructureList && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
