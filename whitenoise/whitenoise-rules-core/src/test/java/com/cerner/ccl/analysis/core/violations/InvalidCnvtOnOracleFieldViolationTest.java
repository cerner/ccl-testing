package com.cerner.ccl.analysis.core.violations;

import com.cerner.ccl.analysis.core.violations.internal.AbstractViolationTest;

/**
 * Unit tests for {@link InvalidCnvtOnOracleFieldViolation}.
 * 
 * @author Joshua Hyde
 */

public class InvalidCnvtOnOracleFieldViolationTest extends AbstractViolationTest<InvalidCnvtOnOracleFieldViolation> {

    @Override
    protected InvalidCnvtOnOracleFieldViolation createViolation(final Integer lineNumber) {
        return new InvalidCnvtOnOracleFieldViolation(lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "INVALID_CNVT_ON_ORACLE_FIELD";
    }

}
