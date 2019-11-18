package com.cerner.ccl.analysis.core.violations;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.analysis.core.violations.internal.AbstractRecordStructureViolationTest;

/**
 * Unit tests for {@link EmptyListOrStructureDefinitionViolation}.
 *
 * @author Joshua Hyde
 */

public class EmptyListOrStructureDefinitionViolationTest
        extends AbstractRecordStructureViolationTest<EmptyListOrStructureDefinitionViolation> {
    private final String fieldName = "a.field";

    /**
     * Construction with a {@code null} field name should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullFieldName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new EmptyListOrStructureDefinitionViolation("okay", null, null);
        });
        assertThat(e.getMessage()).isEqualTo("Field name cannot be null.");
    }

    /**
     * If two violations have the same field name - just with different casing - they should still be equal.
     */
    @Test
    public void testEqualsCaseInsensitiveFieldName() {
        final String recordStructureName = "record";
        final EmptyListOrStructureDefinitionViolation first = new EmptyListOrStructureDefinitionViolation(
                recordStructureName, fieldName, null);
        final EmptyListOrStructureDefinitionViolation second = new EmptyListOrStructureDefinitionViolation(
                recordStructureName, StringUtils.swapCase(fieldName), null);
        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    /**
     * Two violations with different field names - all other things being equal - should be inequal.
     */
    @Test
    public void testEqualsDifferentFieldName() {
        final String recordStructureName = "record";
        final EmptyListOrStructureDefinitionViolation first = new EmptyListOrStructureDefinitionViolation(
                recordStructureName, fieldName, null);
        final EmptyListOrStructureDefinitionViolation second = new EmptyListOrStructureDefinitionViolation(
                recordStructureName, StringUtils.reverse(fieldName), null);
        assertThat(first).isNotEqualTo(second);
        assertThat(second).isNotEqualTo(first);
    }

    @Override
    protected EmptyListOrStructureDefinitionViolation createViolation(final String recordStructureName,
            final Integer lineNumber) {
        return new EmptyListOrStructureDefinitionViolation(recordStructureName, fieldName, lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "EMPTY_LIST_OR_STRUCTURE_DEFINITION";
    }

}
