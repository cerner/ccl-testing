package com.cerner.ccl.parser.data.record;

/**
 * Definition of a structure member that explicitly participates in the hierarchical makeup of a record structure. For
 * example, given the following record:
 * 
 * <pre>
 *  record struct (
 *      1 person_id = f8
 *  %i cclsource:status_block.inc
 *  )
 * </pre>
 * 
 * ...both {@code status_block.inc} and "person_id" are {@link StructureMember structure members}, but only "person_id"
 * is a hierarchical member since it explicitly participates in the hierarchy of the structure.
 * 
 * @author Joshua Hyde
 * 
 */

public interface HierarchicalStructureMember extends StructureMember {
    /**
     * Get the hierarchy level of this member.
     * 
     * @return The level at which this member participates.
     */
    int getLevel();
}
