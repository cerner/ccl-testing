package com.cerner.ccl.analysis.core.violations;

import com.cerner.ccl.analysis.core.violations.internal.AbstractRecordStructureViolationTest;

/**
 * Unit tests for {@link UnprotectedRecordStructureDefinitionViolation}.
 * 
 * @author Joshua Hyde
 */

public class UnprotectedRecordStructureDefinitionViolationTest extends AbstractRecordStructureViolationTest<UnprotectedRecordStructureDefinitionViolation> {

    @Override
    protected UnprotectedRecordStructureDefinitionViolation createViolation(String recordStructureName, Integer lineNumber) {
        return new UnprotectedRecordStructureDefinitionViolation(recordStructureName, lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "UNPROTECTED_RECORD_DEFINITION";
    }

}
