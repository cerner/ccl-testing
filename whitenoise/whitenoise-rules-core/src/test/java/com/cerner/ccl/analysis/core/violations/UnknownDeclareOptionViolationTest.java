package com.cerner.ccl.analysis.core.violations;

import com.cerner.ccl.analysis.core.violations.internal.AbstractVariableViolationTest;

/**
 * Unit tests for {@link UnknownDeclareOptionViolation}.
 * 
 * @author Dee Adesanwo
 */

public class UnknownDeclareOptionViolationTest extends AbstractVariableViolationTest<UnknownDeclareOptionViolation> {

    @Override
    protected UnknownDeclareOptionViolation createViolation(String variableName, Integer lineNumber) {
        return new UnknownDeclareOptionViolation(variableName, lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "UNKNOWN_DECLARE_OPTION";
    }

}
