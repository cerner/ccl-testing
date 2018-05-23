package com.cerner.ccl.analysis.core.violations;

import com.cerner.ccl.analysis.core.violations.internal.AbstractViolationTest;

/**
 * Unit tests for {@link ModuloByOneViolation}.
 * 
 * @author Joshua Hyde
 */

public class ModuloByOneViolationTest extends AbstractViolationTest<ModuloByOneViolation> {

    @Override
    protected ModuloByOneViolation createViolation(Integer lineNumber) {
        return new ModuloByOneViolation(lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "MODULO_BY_ONE";
    }

}
