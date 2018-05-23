package com.cerner.ccl.analysis.core.violations;

import com.cerner.ccl.analysis.core.violations.internal.AbstractViolationTest;

/**
 * Unit tests for {@link InfiniteLoopViolation}.
 * 
 * @author Joshua Hyde
 */

public class InfiniteLoopViolationTest extends AbstractViolationTest<InfiniteLoopViolation> {

    @Override
    protected InfiniteLoopViolation createViolation(final Integer lineNumber) {
        return new InfiniteLoopViolation(lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "INFINITE_WHILE_LOOP";
    }

}
