package com.cerner.ccl.parser.text.subroutine;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.FixedLengthDataTyped;

/**
 * Definition of a fixed-length character subroutine argument.
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineCharacterArgumentDeclaration extends SubroutineArgumentDeclaration
        implements FixedLengthDataTyped {
    private final int dataLength;

    /**
     * Create a fixed-length character subroutine argument.
     *
     * @param name
     *            The name of the argument.
     * @param dataLength
     *            The length of the subroutine argument.
     * @param isByRef
     *            A {@code boolean} indicating whether or not this is a by-ref argument; {@code true} means it is,
     *            {@code false} means it is merely passed by-reference.
     * @throws IllegalArgumentException
     *             If the given data length is less than one.
     */
    public SubroutineCharacterArgumentDeclaration(final String name, final int dataLength, final boolean isByRef) {
        super(name, isByRef, DataType.CHAR);

        if (dataLength < 1) {
            throw new IllegalArgumentException("Data length cannot be less than 1: " + Integer.toString(dataLength));
        }

        this.dataLength = dataLength;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SubroutineCharacterArgumentDeclaration)) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        final SubroutineCharacterArgumentDeclaration other = (SubroutineCharacterArgumentDeclaration) obj;
        return new EqualsBuilder().append(dataLength, other.dataLength).isEquals();
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
        return 31 * super.hashCode() + getDataLength();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
