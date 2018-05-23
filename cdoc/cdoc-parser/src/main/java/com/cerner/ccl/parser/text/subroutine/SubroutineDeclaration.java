package com.cerner.ccl.parser.text.subroutine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.DataTyped;
import com.cerner.ccl.parser.data.subroutine.SubroutineArgument;

/**
 * Definition of a subroutine declaration within a CCL script, e.g.:
 *
 * <pre>
 *   declare some_subroutine(person_id = f8, alive_ind = i2(REF)) = i4
 * </pre>
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineDeclaration {
    private final List<SubroutineArgumentDeclaration> arguments = new ArrayList<SubroutineArgumentDeclaration>();
    private final String name;
    private final DataTyped returnType;

    /**
     * Create a subroutine definition.
     *
     * @param name
     *            The name of the subroutine.
     * @param returnType
     *            A {@link DataTyped} representing the return data type of the subroutine. If {@code null}, the
     *            subroutine is assumed to be a {@code void} subroutine.
     * @param arguments
     *            A {@link List} of {@link SubroutineArgumentDeclaration} objects representing the subroutine arguments
     *            in order.
     * @throws IllegalArgumentException
     *             If the given collection of subroutine arguments is {@code null}.
     */
    public SubroutineDeclaration(final String name, final DataTyped returnType,
            final List<SubroutineArgumentDeclaration> arguments) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        if (arguments == null) {
            throw new IllegalArgumentException("Arguments cannot be null.");
        }

        this.name = name;
        this.returnType = returnType;
        this.arguments.addAll(arguments);
    }

    /**
     * Get the arguments of the subroutine.
     *
     * @return A {@link List}; of the {@link SubroutineArgument} objects representing the arguments of the subroutine.
     */
    public List<SubroutineArgumentDeclaration> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

    /**
     * Get the name of the subroutine.
     *
     * @return The name of the subroutine.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the data type of the returned data for this subroutine.
     *
     * @return {@code null} if this is a {@code void} subroutine; otherwise, a {@link DataType} enum representing the
     *         return data type of this subroutine.
     */
    public DataTyped getReturnType() {
        return returnType;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
