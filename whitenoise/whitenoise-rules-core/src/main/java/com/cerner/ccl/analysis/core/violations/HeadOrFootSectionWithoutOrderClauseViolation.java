package com.cerner.ccl.analysis.core.violations;

import java.util.Locale;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} used to describe a situation where a report writer section contains a head or foot section but
 * not the corresponding order by clause
 * <p>
 *
 * @author Jeff Wiedemann
 */

public class HeadOrFootSectionWithoutOrderClauseViolation implements Violation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("HEAD_FOOT_SECTION_NO_ORDER");

    private final String headFootOn;
    private final Integer lineNumber;

    /**
     * Create a violation.
     *
     * @param headerOrFooterField
     *            The name of the section which has a head or foot but no order by clause
     * @param lineNumber
     *            An {@link Integer} representing the line at which the violation was encountered, if applicable.
     * @throws IllegalArgumentException
     *             When headFootOn is not a valid string value
     */
    public HeadOrFootSectionWithoutOrderClauseViolation(final String headerOrFooterField, final Integer lineNumber)
            throws IllegalArgumentException {
        if (headerOrFooterField == null) {
            throw new IllegalArgumentException("Header/footer field name cannot be null.");
        }

        this.headFootOn = headerOrFooterField;
        this.lineNumber = lineNumber != null ? lineNumber : 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof HeadOrFootSectionWithoutOrderClauseViolation)) {
            return false;
        }

        final HeadOrFootSectionWithoutOrderClauseViolation other = (HeadOrFootSectionWithoutOrderClauseViolation) obj;
        return getHeaderOrFooterField().equalsIgnoreCase(other.getHeaderOrFooterField())
                && getLineNumber().equals(other.getLineNumber());
    }

    /**
     * Get the name of the field that is used in a {@code HEAD} or {@code FOOT} report writer section, but is not used
     * in an {@code ORDER BY} statement.FreedRecordStructureViolation
     *
     * @return The name of the field that is used in a {@code HEAD} or {@code FOOT} report writer section, but is not
     *         used in an {@code ORDER BY} statement.FreedRecordStructureViolation
     */
    public String getHeaderOrFooterField() {
        return headFootOn;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getLineNumber() {
        return lineNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViolationDescription() {
        return "Head [" + headFootOn + "] or Foot [" + headFootOn + "] does not have corresponding order by clause";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getViolationExplanation() {
        return "When using a head or foot section in report writer code, it is almost always required to include a corresponding "
                + "order by clause so that the head or foot section does not fire too frequently due to a result set which was not "
                + "returned in the expected order. Remember, that without an explicit order by clause, the result set order is "
                + "not guaranteed and can change based on various environmental factors.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ViolationId getViolationId() {
        return VIOLATION_ID;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(headFootOn.toLowerCase(Locale.US)).append(lineNumber).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
