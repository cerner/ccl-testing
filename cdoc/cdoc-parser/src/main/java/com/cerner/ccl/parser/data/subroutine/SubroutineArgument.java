package com.cerner.ccl.parser.data.subroutine;

import java.util.Locale;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.DataTyped;
import com.cerner.ccl.parser.data.Described;
import com.cerner.ccl.parser.data.Named;

/**
 * A class that represents an argument within the signature of a CCL subroutine.
 * <p>
 * The equality of a subroutine argument is assumed to be measured only within the context of its owning subroutine. As
 * such, only the {@link #getName()} - which is case-insensitive - is used to determine equality of subroutine
 * arguments.
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineArgument implements DataTyped, Described, Named {
    private final String name;
    private final boolean isByRef;
    private final String description;
    private final DataType dataType;

    /**
     * Create a subroutine argument with no documentation or data type.
     *
     * @param name
     *            The name of the argument.
     * @throws IllegalArgumentException
     *             If the given name is {@code null}.
     */
    public SubroutineArgument(final String name) {
        this(name, null);
    }

    /**
     * Create a subroutine argument with a description and no data type.
     *
     * @param name
     *            The name of the subroutine argument.
     * @param description
     *            A description of the subroutine argument.
     * @throws IllegalArgumentException
     *             If the given name is {@code null}.
     */
    public SubroutineArgument(final String name, final String description) {
        this(name, null, false, description);
    }

    /**
     * Create a subroutine argument.
     *
     * @param name
     *            The name of the subroutine argument.
     * @param dataType
     *            A {@link DataType} enum representing the data type of this argument. If this is {@code null}, it is
     *            assumed that there is insufficient information about the data type to determine its type.
     * @param isByRef
     *            A {@code boolean} indicating whether or not this is a by-reference argument; if {@code true}, then
     *            this is a by-reference argument; if {@code false}, it is not.
     * @param description
     *            A description of the subroutine argument. If {@code null}, a blank string is stored internally.
     * @throws IllegalArgumentException
     *             If the given name is {@code null}.
     */
    public SubroutineArgument(final String name, final DataType dataType, final boolean isByRef,
            final String description) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        this.name = name;
        this.description = description == null ? "" : description;
        this.isByRef = isByRef;
        this.dataType = dataType;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SubroutineArgument)) {
            return false;
        }
        final SubroutineArgument other = (SubroutineArgument) obj;
        return new EqualsBuilder()
                .append(name.toLowerCase(Locale.getDefault()), other.name.toLowerCase(Locale.getDefault())).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DataType getDataType() {
        return dataType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return getName().toLowerCase(Locale.getDefault()).hashCode();
    }

    /**
     * Determine whether or not this is a by-reference argument.
     *
     * @return {@code true} if this is a by-reference argument; {@code false} if it is not.
     */
    public boolean isByRef() {
        return isByRef;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
