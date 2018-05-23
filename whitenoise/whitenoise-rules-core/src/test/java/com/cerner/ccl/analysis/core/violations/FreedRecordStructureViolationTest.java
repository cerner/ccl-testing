package com.cerner.ccl.analysis.core.violations;

import com.cerner.ccl.analysis.core.violations.internal.AbstractRecordStructureViolationTest;

/**
 * Unit tests for {@link FreedRecordStructureViolation}.
 * 
 * @author Joshua Hyde
 */

public class FreedRecordStructureViolationTest extends AbstractRecordStructureViolationTest<FreedRecordStructureViolation> {

    @Override
    protected FreedRecordStructureViolation createViolation(String recordStructureName, Integer lineNumber) {
        return new FreedRecordStructureViolation(recordStructureName, lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "FREED_RECORD_STRUCTURE";
    }

}
