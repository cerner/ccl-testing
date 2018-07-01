package com.cerner.ccl.analysis.core.violations;

import com.cerner.ccl.analysis.core.violations.internal.AbstractSubroutineViolationTest;

/**
 * Unit tests for {@link MissingSubroutineDeclarationViolation}.
 * 
 * @author Joshua Hyde
 */

public class MissingSubroutineDeclarationViolationTest
        extends AbstractSubroutineViolationTest<MissingSubroutineDeclarationViolation> {

    @Override
    protected MissingSubroutineDeclarationViolation createViolation(String subroutineName, Integer lineNumber) {
        return new MissingSubroutineDeclarationViolation(subroutineName, lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "MISSING_SUBROUTINE_DECLARATION";
    }

}
