package com.cerner.ccl.analysis.data;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * Unit tests for {@link ViolationId}.
 *
 * @author Joshua Hyde
 */

@SuppressWarnings("unused")
public class ViolationIdTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    private final String namespace = "testNamespace";
    private final String identifier = "identifier";
    private final ViolationId violationId = new ViolationId(namespace, identifier);

    /**
     * Construction with a blank namespaced identifier should fail.
     */
    @Test
    public void testConstructBlankIdentifier() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Invalid namespaced identifier; given identifier was [ ]");
        new ViolationId(namespace, " ");
    }

    /**
     * Construction with a blank name space should fail.
     */
    @Test
    public void testConstructBlankNamespace() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Invalid namespace; given namespace was [ ]");
        new ViolationId(" ", identifier);
    }

    /**
     * Construction with a {@code null} identifier should fail.
     */
    @Test
    public void testConstructNullIdentifier() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Invalid namespaced identifier; given identifier was [<null>]");
        new ViolationId(namespace, null);
    }

    /**
     * Construction with a {@code null} namespace should fail.
     */
    @Test
    public void testConstructNullNamespace() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Invalid namespace; given namespace was [<null>]");
        new ViolationId(null, identifier);
    }

    /**
     * Test the equality of two violation IDs.
     */
    @Test
    public void testEquals() {
        final ViolationId other = new ViolationId(violationId.getNamespace(), violationId.getNamespacedIdentifier());
        assertThat(other).isEqualTo(violationId);
        assertThat(violationId).isEqualTo(other);
        assertThat(violationId.hashCode()).isEqualTo(other.hashCode());
    }

    /**
     * Two violation IDs with different IDs (but the same namespace) should be inequal.
     */
    @Test
    public void testEqualsDifferentIdentifier() {
        final ViolationId other = new ViolationId(violationId.getNamespace(), StringUtils.reverse(violationId.getNamespacedIdentifier()));
        assertThat(other).isNotEqualTo(violationId);
        assertThat(violationId).isNotEqualTo(other);
    }

    /**
     * Two violation IDs with different namespaces (but the same identifier) should be inequal.
     */
    @Test
    public void testEqualsDifferentNamespace() {
        final ViolationId other = new ViolationId(StringUtils.reverse(violationId.getNamespace()), violationId.getNamespacedIdentifier());
        assertThat(other).isNotEqualTo(violationId);
        assertThat(violationId).isNotEqualTo(other);
    }

    /**
     * Equality should be case-insensitive.
     */
    @Test
    public void testEqualsIgnoreCase() {
        final ViolationId other = new ViolationId(StringUtils.swapCase(violationId.getNamespace()), StringUtils.swapCase(violationId.getNamespacedIdentifier()));
        assertThat(violationId).isEqualTo(other);
        assertThat(other).isEqualTo(violationId);
        assertThat(violationId.hashCode()).isEqualTo(other.hashCode());
    }

    /**
     * A violation ID should be equal to itself.
     */
    @Test
    public void testEqualsSelf() {
        assertThat(violationId).isEqualTo(violationId);
    }

    /**
     * Test the retrieval of the identifier.
     */
    @Test
    public void testGetIdentifier() {
        assertThat(violationId.getIdentifier()).isEqualTo(namespace.toUpperCase(Locale.US) + "." + identifier.toUpperCase(Locale.US));
    }

    /**
     * Test the retrieval of the namespace.
     */
    @Test
    public void testGetNamespace() {
        assertThat(violationId.getNamespace()).isEqualTo(namespace.toUpperCase(Locale.US));
    }

    /**
     * Test the retrieval of the identifier.
     */
    @Test
    public void testGetNamespacedIdentifier() {
        assertThat(violationId.getNamespacedIdentifier()).isEqualTo(identifier.toUpperCase(Locale.US));
    }
}
