package com.cerner.ccl.parser.data;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Definition of a bean representing a Cerner Millennium code set.
 *
 * @author Joshua Hyde
 *
 */

public class CodeSet implements Described {
    private final int codeSet;
    private final String description;

    /**
     * Create a code set with no description.
     *
     * @param codeSet
     *            The code set number.
     * @throws IllegalArgumentException
     *             If the given code set is 0 or less.
     */
    public CodeSet(final int codeSet) {
        this(codeSet, null);
    }

    /**
     * Create a code set with a description.
     *
     * @param codeSet
     *            The code set number.
     * @param description
     *            The description of the code set.
     * @throws IllegalArgumentException
     *             If the code set is 0 or less.
     */
    public CodeSet(final int codeSet, final String description) {
        if (codeSet <= 0) {
            throw new IllegalArgumentException(
                    "Code set must be a non-zero, positive integer: " + Integer.toString(codeSet));
        }

        this.codeSet = codeSet;
        this.description = description == null ? "" : description;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof CodeSet)) {
            return false;
        }

        final CodeSet other = (CodeSet) obj;
        return new EqualsBuilder().append(codeSet, other.codeSet).isEquals();
    }

    /**
     * Get the code set value.
     *
     * @return The code set value.
     */
    public int getCodeSet() {
        return codeSet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public int hashCode() {
        return codeSet;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
