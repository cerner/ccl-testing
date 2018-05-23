package com.cerner.ccl.analysis.core.violations;

import com.cerner.ccl.analysis.core.violations.internal.AbstractViolationTest;

/**
 * Unit tests for {@link FilesortAndMaxqualViolation}.
 * 
 * @author Joshua Hyde
 */

public class FilesortAndMaxqualViolationTest extends AbstractViolationTest<FilesortAndMaxqualViolation> {

    @Override
    protected FilesortAndMaxqualViolation createViolation(Integer lineNumber) {
        return new FilesortAndMaxqualViolation(lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "FILESORT_AND_MAXQUAL";
    }

}
