package com.cerner.ccl.parser.text.subroutine;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.DataTyped;

/**
 * A bean object representing a subroutine argument without any documentation surrounding it.
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineArgumentDeclaration implements DataTyped {
    private final String name;
    private final boolean isByRef;
    private final DataType dataType;

    /**
     * Create an untyped subroutine argument. This typically occurs when a subroutine has been <i>defined</i>, but not
     * <i>declared</i>.
     *
     * @param name
     *            The name of the argument.
     * @throws IllegalArgumentException
     *             If the given name is {@code null}.
     */
    public SubroutineArgumentDeclaration(final String name) {
        this(name, false, null);
    }

    /**
     * Create a subroutine argument that is not by-reference.
     *
     * @param name
     *            The name of the subroutine argument.
     * @param dataType
     *            A {@link DataType} enum representing the data type of the subroutine argument.
     */
    public SubroutineArgumentDeclaration(final String name, final DataType dataType) {
        this(name, false, dataType);
    }

    /**
     * Create a subroutine argument.
     *
     * @param name
     *            The name of the subroutine argument.
     * @param isByRef
     *            A {@code boolean} indicating whether or not this is a by-reference argument.
     * @param dataType
     *            A {@link DataType} enum representing the data type of the subroutine argument.
     * @throws IllegalArgumentException
     *             If the given name or data type is {@code null}.
     */
    public SubroutineArgumentDeclaration(final String name, final boolean isByRef, final DataType dataType) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        this.name = name;
        this.isByRef = isByRef;
        this.dataType = dataType;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof SubroutineArgumentDeclaration)) {
            return false;
        }

        final SubroutineArgumentDeclaration other = (SubroutineArgumentDeclaration) obj;
        return new EqualsBuilder().append(name, other.name).append(dataType, other.dataType)
                .append(isByRef, other.isByRef).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Get the name of the subroutine argument.
     *
     * @return The name of the subroutine argument.
     */
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
        result = prime * result + (isByRef ? 1231 : 1237);
        result = prime * result + name.hashCode();
        return result;
    }

    /**
     * Determine whether or not this argument is a by-reference argument.
     *
     * @return {@code true} if it is a by-reference argument; {@code false} if it is not.
     */
    public boolean isByRef() {
        return isByRef;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
