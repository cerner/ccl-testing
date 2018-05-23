package com.cerner.ccl.analysis.core.violations;

import com.cerner.ccl.analysis.core.violations.internal.AbstractVariableViolationTest;

/**
 * Unit tests for {@link VariableDeclaredButNotUsedViolation}.
 * 
 * @author Joshua Hyde
 */

public class VariableDeclaredButNotUsedViolationTest extends AbstractVariableViolationTest<VariableDeclaredButNotUsedViolation> {

    @Override
    protected VariableDeclaredButNotUsedViolation createViolation(String variableName, Integer lineNumber) {
        return new VariableDeclaredButNotUsedViolation(variableName, lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "UNUSED_VARIABLE_DECLARATION";
    }

}
