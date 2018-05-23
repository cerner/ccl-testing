package com.cerner.ccl.analysis.core.violations;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.data.ViolationId;

/**
 * A {@link Violation} indicating that a call to uar_18ngetmessage was invoked from the script which contained a
 * duplicate key value for another i18ngetmessage call within the script. These keys need to be unique per unique string
 *
 * @author Jeff Wiedemann
 */

public class DuplicateI18nKeyViolation implements Violation {
    private static final ViolationId VIOLATION_ID = new CoreViolationId("DUP_I18N_KEY_VALUE");
    private final Integer lineNumber;
    private final String key;

    /**
     * Create a violation.
     *
     * @param lineNumber
     *            An {@link Integer} representing the line of the beginning of the while loop with the violation, if
     *            applicable.
     * @param key
     *            An {@link String} representing the Key which was duplicated
     */
    public DuplicateI18nKeyViolation(final Integer lineNumber, final String key) {
        if (key == null)
            throw new IllegalArgumentException("key cannot be null");

        this.lineNumber = lineNumber != null ? lineNumber : 0;
        this.key = key;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;

        if (!(obj instanceof DuplicateI18nKeyViolation))
            return false;

        final DuplicateI18nKeyViolation other = (DuplicateI18nKeyViolation) obj;
        return getLineNumber().equals(other.getLineNumber()) && getKey().equalsIgnoreCase(other.getKey());
    }

    /**
     * {@inheritDoc}
     */
    public Integer getLineNumber() {
        return lineNumber;
    }

    /**
     * @return The key for this violation.
     */
    public String getKey() {
        return key;
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationDescription() {
        return "uar_i18ngetmessage key [" + getKey() + "] is not unique for script";
    }

    /**
     * {@inheritDoc}
     */
    public String getViolationExplanation() {
        return "To ensure that each i18n string is uniquely identified and internationalized by the internationalization team, there cannot exist "
                + "two calls to uar_i18ngetmessage with the same 'key' value and different 'text' value";
    }

    /**
     * {@inheritDoc}
     */
    public ViolationId getViolationId() {
        return VIOLATION_ID;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(lineNumber).append(getKey()).toHashCode();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
