package com.cerner.ccltesting.maven.ccl.util.factory;

import com.cerner.ccltesting.maven.ccl.util.TestResultScanner;

/**
 * A factory to create {@link TestResultScanner} objects.
 *
 * @author Joshua Hyde
 *
 */

public class TestResultScannerFactory {
    /**
     * Create a test result scanner.
     *
     * @return A {@link TestResultScanner} object.
     */
    public TestResultScanner create() {
        return new TestResultScanner();
    }
}
