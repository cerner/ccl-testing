package com.cerner.ccl.analysis.core.violations;

import com.cerner.ccl.analysis.core.violations.internal.AbstractViolationTest;

/**
 * Unit tests for {@link ReversedModuloParametersViolation}.
 * 
 * @author Joshua Hyde
 */

public class ReversedModuloParametersViolationTest extends AbstractViolationTest<ReversedModuloParametersViolation> {

    @Override
    protected ReversedModuloParametersViolation createViolation(Integer lineNumber) {
        return new ReversedModuloParametersViolation(lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "REVERSED_MODULO_PARAMETERS";
    }

}
