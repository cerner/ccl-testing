package com.cerner.ccl.analysis.core.violations;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Locale;

import org.junit.Test;

/**
 * Unit tests for {@link CoreViolationId}.
 * 
 * @author Joshua Hyde
 */

public class CoreViolationIdTest {
    private final String identifier = "an.identifier";
    private final CoreViolationId violationId = new CoreViolationId(identifier);

    /**
     * Test the namespace of the violation ID.
     */
    @Test
    public void testGetNamespace() {
        assertThat(violationId.getNamespace()).isEqualTo("CORE");
    }

    /**
     * Test the storage retrieval of the namespaced identifier.
     */
    @Test
    public void testGetNamespacedIdentifier() {
        assertThat(violationId.getNamespacedIdentifier()).isEqualTo(identifier.toUpperCase(Locale.US));
    }
}
