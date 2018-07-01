package com.cerner.ccl.analysis.core.violations;

import com.cerner.ccl.analysis.core.violations.internal.AbstractSubroutineViolationTest;

/**
 * Unit tests for {@link DuplicateSubroutineDefinitionViolation}.
 * 
 * @author Joshua Hyde
 */

public class DuplicateSubroutineDefinitionViolationTest
        extends AbstractSubroutineViolationTest<DuplicateSubroutineDefinitionViolation> {

    @Override
    protected DuplicateSubroutineDefinitionViolation createViolation(String subroutineName, Integer lineNumber) {
        return new DuplicateSubroutineDefinitionViolation(subroutineName, lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "DUPLICATE_SUBROUTINE_DEFINITION";
    }
}
