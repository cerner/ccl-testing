package com.cerner.ccl.parser.data.subroutine;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.FixedLengthDataTyped;

/**
 * A fixed-length-character argument for a subroutine.
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineCharacterArgument extends SubroutineArgument implements FixedLengthDataTyped {
    private final int dataLength;

    /**
     * Create a fixed-length character argument with no documentation.
     *
     * @param name
     *            The name of the argument.
     * @param dataLength
     *            The size of the character argument.
     * @throws IllegalArgumentException
     *             If the given length is less than one.
     */
    public SubroutineCharacterArgument(final String name, final int dataLength) {
        this(name, dataLength, null);
    }

    /**
     * Create a fixed-length character argument with a description.
     *
     * @param name
     *            The name of the argument.
     * @param dataLength
     *            The size of the character argument.
     * @param description
     *            The description of the argument.
     * @throws IllegalArgumentException
     *             If the given length is less than one.
     */
    public SubroutineCharacterArgument(final String name, final int dataLength, final String description) {
        this(name, dataLength, false, description);
    }

    /**
     * Create a fixed-length character argument.
     *
     * @param name
     *            The name of the argument.
     * @param dataLength
     *            The size of the character argument.
     * @param isByRef
     *            A {@code boolean} value indicating the referenceability of the field (i.e., "by-value" versus
     *            "by-reference").
     * @param description
     *            The description of the argument.
     * @throws IllegalArgumentException
     *             If the given length is less than one.
     */
    public SubroutineCharacterArgument(final String name, final int dataLength, final boolean isByRef,
            final String description) {
        super(name, DataType.CHAR, isByRef, description);

        if (dataLength < 1) {
            throw new IllegalArgumentException("Data length must be at least 1: " + Integer.toString(dataLength));
        }

        this.dataLength = dataLength;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SubroutineCharacterArgument)) {
            return false;
        }

        return super.equals(obj) && getDataLength() == ((SubroutineCharacterArgument) obj).getDataLength();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getDataLength() {
        return dataLength;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + getDataLength();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
