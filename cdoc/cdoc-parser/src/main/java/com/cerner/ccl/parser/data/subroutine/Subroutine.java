package com.cerner.ccl.parser.data.subroutine;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.DataTyped;
import com.cerner.ccl.parser.data.Described;
import com.cerner.ccl.parser.data.Named;

/**
 * This object describes a subroutine within a CCL script.
 * <p>
 * Equality of subroutines is driven by a combination of:
 * <ul>
 * <li>{@link #getName() name} (case-insensitive)</li>
 * <li>{@link #getArguments() arguments}</li>
 * <li>{@link #getReturnDataType() return data type} or {@link #returnsVoid() returning of 'void'}</li>
 * </ul>
 *
 * @author Joshua Hyde
 *
 */

public class Subroutine implements Described, Named {
    /**
     * Use this {@link DataTyped} object when it is needed to indicate that the return data type of the subroutine could
     * not be determined.
     */
    public static final DataTyped UNKNOWN_RETURN_TYPE = new UnknownReturnType();

    private final String name;
    private final DataTyped returnDataType;
    private final String description;
    private final String returnDescription;
    private final List<SubroutineArgument> arguments;

    /**
     * Create a subroutine with no documentation regarding the subroutine itself.
     *
     * @param name
     *            The name of the subroutine.
     * @param arguments
     *            A {@link List} of {@link SubroutineArgument} objects representing the arguments of the subroutine. If
     *            {@code null}, it is assumed this subroutine returns no data and an empty list is stored internally.
     * @param returnDataType
     *            A {@link DataTyped} object representing the return type of the subroutine. If {@code null}, this is
     *            assumed to be a subroutine that returns no data. If you wish to indicate that the return data type
     *            could not be determined, use {@link #UNKNOWN_RETURN_TYPE}.
     * @throws IllegalArgumentException
     *             If the given name is {@code null}.
     */
    public Subroutine(final String name, final List<SubroutineArgument> arguments, final DataTyped returnDataType) {
        this(name, arguments, returnDataType, null, null);
    }

    /**
     * Create a subroutine with documentation.
     *
     * @param name
     *            The name of the subroutine.
     * @param arguments
     *            A {@link List} of {@link SubroutineArgument} objects representing the arguments of the subroutine. If
     *            {@code null}, it is assumed this subroutine returns no data and an empty list is stored internally.
     * @param returnDataType
     *            A {@link DataTyped} object representing the return type of the subroutine. If {@code null}, this is
     *            assumed to be a subroutine that returns no data. If you wish to indicate that the return data type
     *            could not be determined, use {@link #UNKNOWN_RETURN_TYPE}.
     * @param description
     *            The description of the subroutine. If {@code null}, a blank string is stored internally.
     * @param returnDescription
     *            The description of the returned data. If {@code null}, a blank string is stored internally.
     * @throws IllegalArgumentException
     *             If the given name is {@code null}.
     */
    public Subroutine(final String name, final List<SubroutineArgument> arguments, final DataTyped returnDataType,
            final String description, final String returnDescription) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        this.name = name;
        this.returnDataType = returnDataType;
        this.arguments = arguments == null || arguments.isEmpty() ? Collections.<SubroutineArgument> emptyList()
                : Collections.unmodifiableList(arguments);
        this.description = description == null ? "" : description;
        this.returnDescription = returnDescription == null ? "" : returnDescription;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Subroutine)) {
            return false;
        }
        final Subroutine other = (Subroutine) obj;
        return new EqualsBuilder()
                .append(name.toLowerCase(Locale.getDefault()), other.name.toLowerCase(Locale.getDefault()))
                .append(arguments, other.arguments)
                .append(returnDataType != null ? returnDataType : UNKNOWN_RETURN_TYPE,
                        other.returnDataType != null ? other.returnDataType : UNKNOWN_RETURN_TYPE)
                .isEquals();
    }

    /**
     * Get the arguments of the subroutine.
     *
     * @return A {@link List} of {@link SubroutineArgument} objects representing the arguments of this subroutine.
     */
    public List<SubroutineArgument> getArguments() {
        return arguments;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Get the return data description.
     *
     * @return A description, if available, of the data returned by this subroutine.
     */
    public String getReturnDataDescription() {
        return returnDescription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the type of data returned by this subroutine, if any.
     *
     * @param <T>
     *            The type of {@link DataTyped} object to be returned.
     * @return {@code null} if this subroutine either returns no data, or there was insufficient evidence to determine
     *         its return type; otherwise, a {@link DataTyped} implementation representing the type of returned data.
     * @see #returnsVoid()
     */
    @SuppressWarnings("unchecked")
    public <T extends DataTyped> T getReturnDataType() {
        return (T) returnDataType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + arguments.hashCode();
        result = prime * result + name.toLowerCase(Locale.getDefault()).hashCode();
        result = prime * result + ((returnDataType == null) ? 0 : returnDataType.hashCode());
        return result;
    }

    /**
     * Determine whether or not this subroutine returns data.
     *
     * @return If {@code true}, this subroutine returns no data; {@code false} if it is known to return a certain type
     *         of data.
     * @throws UnsupportedOperationException
     *             If the {@link #getReturnDataType() return data type} is {@link #UNKNOWN_RETURN_TYPE unknown}.
     * @see #getReturnDataType()
     */
    public boolean returnsVoid() {
        if (returnDataType == UNKNOWN_RETURN_TYPE) {
            throw new UnsupportedOperationException("The return type of this subroutine cannot be determined.");
        }

        return returnDataType == null;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    /**
     * A {@link DataTyped} object representing when the return type of the subroutine could not be determined.
     * <p>
     * <b>Warning</b>: {@link #getDataType()} throws an {@link UnsupportedOperationException}.
     *
     * @author Joshua Hyde
     *
     */
    public static class UnknownReturnType implements DataTyped {
        /**
         * Package private constructor to prevent external instantiation.
         */
        UnknownReturnType() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public DataType getDataType() {
            throw new UnsupportedOperationException(
                    "This is a NULL return type and, thus, has no known return data type.");
        }
    }
}
