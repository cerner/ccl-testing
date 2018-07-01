package com.cerner.ccl.analysis.core.violations;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.analysis.core.violations.internal.AbstractViolationTest;

/**
 * Unit tests for {@link HeadOrFootSectionWithoutOrderClauseViolation}.
 *
 * @author Joshua Hyde
 */

public class HeadOrFootSectionWithoutOrderClauseViolationTest
        extends AbstractViolationTest<HeadOrFootSectionWithoutOrderClauseViolation> {
    private final String fieldName = "o.order_id";
    private final Integer lineNumber = Integer.valueOf(23);

    /**
     * Construction with a {@code null} field name should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullFieldName() {
        expect(IllegalArgumentException.class, "Header/footer field name cannot be null.");
        new HeadOrFootSectionWithoutOrderClauseViolation(null, lineNumber);
    }

    /**
     * Comparison of two violations should be case-insensitive as far as their field names are concerned.
     */
    @Test
    public void testEqualsCaseInsensitiveFieldName() {
        final HeadOrFootSectionWithoutOrderClauseViolation first = createViolation(lineNumber);
        final HeadOrFootSectionWithoutOrderClauseViolation second = new HeadOrFootSectionWithoutOrderClauseViolation(
                StringUtils.swapCase(fieldName), lineNumber);
        assertThat(first).isEqualTo(second);
        assertThat(second).isEqualTo(first);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    /**
     * Two violations with different field names should be inequal.
     */
    @Test
    public void testEqualsDifferentFieldName() {
        final HeadOrFootSectionWithoutOrderClauseViolation first = createViolation(lineNumber);
        final HeadOrFootSectionWithoutOrderClauseViolation second = new HeadOrFootSectionWithoutOrderClauseViolation(
                StringUtils.reverse(fieldName), lineNumber);
        assertThat(first).isNotEqualTo(second);
        assertThat(second).isNotEqualTo(first);
    }

    /**
     * Test the retrieval of the header/footer field.
     */
    @Test
    public void testGetHeaderOrFooterField() {
        assertThat(new HeadOrFootSectionWithoutOrderClauseViolation(fieldName, lineNumber).getHeaderOrFooterField())
                .isEqualTo(fieldName);
    }

    @Override
    protected HeadOrFootSectionWithoutOrderClauseViolation createViolation(final Integer lineNumber) {
        return new HeadOrFootSectionWithoutOrderClauseViolation(fieldName, lineNumber);
    }

    @Override
    protected String getNamespacedIdentifier() {
        return "HEAD_FOOT_SECTION_NO_ORDER";
    }

}
