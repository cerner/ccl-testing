package com.cerner.ccl.analysis.core.violations.internal;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.analysis.data.RecordStructureViolation;

/**
 * A skeleton definition of a test for a {@link RecordStructureViolation} implementation.
 * 
 * @author Joshua Hyde
 * 
 * @param <T>
 *            The {@link RecordStructureViolation} implementation to be tested.
 */

public abstract class AbstractRecordStructureViolationTest<T extends RecordStructureViolation> extends AbstractViolationTest<T> {
    private final String recordStructureName = "recordStructureName";
    private final Integer lineNumber = Integer.valueOf(2);

    /**
     * Construction with a {@code null} record structure name should fail.
     */
    @Test
    public void testConstructNullRecordStructureName() {
        expect(IllegalArgumentException.class, "Record structure name cannot be null.");
        createViolation(null, lineNumber);
    }

    /**
     * The comparison of two record structures with the same structure name - just different cases - should be equal.
     */
    @Test
    public void testEqualsCaseInsensitive() {
        final T first = createViolation(lineNumber);
        final T second = createViolation(StringUtils.swapCase(recordStructureName), lineNumber);
        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    /**
     * Two violations with different record structure names should be inequal.
     */
    @Test
    public void testEqualsDifferentRecordStructureName() {
        final T first = createViolation(lineNumber);
        final T second = createViolation(StringUtils.reverse(recordStructureName), lineNumber);
        assertThat(first).isNotEqualTo(second);
        assertThat(second).isNotEqualTo(first);
    }

    /**
     * Test the retrieval of the record structure name.
     */
    @Test
    public void testGetRecordStructureName() {
        assertThat(createViolation(lineNumber).getRecordStructureName()).isEqualTo(recordStructureName);
    }

    @Override
    protected T createViolation(Integer lineNumber) {
        return createViolation(recordStructureName, lineNumber);
    }

    /**
     * Create a violation.
     * 
     * @param recordStructureName
     *            The name of the record structure.
     * @param lineNumber
     *            The line number on which the violation occurred.
     * @return An instance of the violation.
     */
    protected abstract T createViolation(String recordStructureName, Integer lineNumber);
}
