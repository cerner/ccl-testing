package com.cerner.ccl.analysis.data;

/**
 * A {@link Violation} representing an erroneous usage of a record structure.
 * 
 * @author Joshua Hyde
 */

public interface RecordStructureViolation extends Violation {
    /**
     * Get the name of the record structure in question.
     * 
     * @return The name of the record structure in question.
     */
    String getRecordStructureName();
}
