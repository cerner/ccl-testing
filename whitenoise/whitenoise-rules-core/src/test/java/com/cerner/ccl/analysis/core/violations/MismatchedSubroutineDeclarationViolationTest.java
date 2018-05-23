package com.cerner.ccl.analysis.core.violations;

import com.cerner.ccl.analysis.core.violations.internal.AbstractSubroutineViolationTest;

/**
 * Unit tests for {@link MismatchedSubroutineDeclarationViolation}.
 * 
 * @author Joshua Hyde
 */

public class MismatchedSubroutineDeclarationViolationTest extends AbstractSubroutineViolationTest<MismatchedSubroutineDeclarationViolation> {

    @Override
    protected MismatchedSubroutineDeclarationViolation createViolation(final String subroutineName, final Integer lineNumber) {
        return new MismatchedSubroutineDeclarationViolation(subroutineName, lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "MISMATCHED_SUBROUTINE_DECLARATION";
    }

}
