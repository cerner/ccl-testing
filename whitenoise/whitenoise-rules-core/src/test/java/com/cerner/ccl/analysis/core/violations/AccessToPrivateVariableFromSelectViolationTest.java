package com.cerner.ccl.analysis.core.violations;

import com.cerner.ccl.analysis.core.violations.internal.AbstractVariableViolationTest;

/**
 * Unit tests for {@link AccessToPrivateVariableFromSelectViolation}.
 * 
 * @author Joshua Hyde
 */

public class AccessToPrivateVariableFromSelectViolationTest
        extends AbstractVariableViolationTest<AccessToPrivateVariableFromSelectViolation> {

    @Override
    protected AccessToPrivateVariableFromSelectViolation createViolation(String variableName, Integer lineNumber) {
        return new AccessToPrivateVariableFromSelectViolation(variableName, lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "PRIVATE_VARIABLE_IN_SELECT";
    }

}
