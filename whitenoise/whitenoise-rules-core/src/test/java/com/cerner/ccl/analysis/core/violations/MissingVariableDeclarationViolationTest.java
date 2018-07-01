package com.cerner.ccl.analysis.core.violations;

import com.cerner.ccl.analysis.core.violations.internal.AbstractVariableViolationTest;

/**
 * Unit tests for {@link MissingVariableDeclarationViolation}.
 * 
 * @author Joshua Hyde
 */

public class MissingVariableDeclarationViolationTest
        extends AbstractVariableViolationTest<MissingVariableDeclarationViolation> {

    @Override
    protected MissingVariableDeclarationViolation createViolation(String variableName, Integer lineNumber) {
        return new MissingVariableDeclarationViolation(variableName, lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "MISSING_VARIABLE_DECLARATION";
    }

}
