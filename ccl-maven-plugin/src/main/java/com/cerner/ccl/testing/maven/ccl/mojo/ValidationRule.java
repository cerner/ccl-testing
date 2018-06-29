package com.cerner.ccl.testing.maven.ccl.mojo;

/**
 * A data object to be used in concert with {@link ValidateMojo}.
 *
 * @author Joshua Hyde
 *
 */

public class ValidationRule {
    private String testFrameworkVersion;

    /**
     * Set the CCL testing framework version to be enforced.
     *
     * @param testFrameworkVersion
     *            The framework version to be enforced.
     */
    public void setTestFrameworkVersion(final String testFrameworkVersion) {
        this.testFrameworkVersion = testFrameworkVersion;
    }

    /**
     * Get the CCL testing framework version to be enforced.
     *
     * @return The framework version to be enforced.
     */
    public String getTestFrameworkVersion() {
        return testFrameworkVersion;
    }
}
