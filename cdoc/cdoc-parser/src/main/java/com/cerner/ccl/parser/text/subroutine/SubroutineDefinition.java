package com.cerner.ccl.parser.text.subroutine;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * A bean representing a subroutine definition, e.g.:
 *
 * <pre>
 *   [...]
 *   subroutine some_subroutine(person_id)
 *   [...]
 * </pre>
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineDefinition {
    private final String name;
    private final List<String> argumentNames;

    /**
     * Create a subroutine definition.
     *
     * @param name
     *            The name of the subroutine.
     * @param argumentNames
     *            A {@link List} of {@link String} objects representing the argument names. If the subroutine takes no
     *            arguments (i.e., it receives "NULL"), then this list should be empty.
     * @throws IllegalArgumentException
     *             If the given name or list of arguments is {@code null}.
     */
    public SubroutineDefinition(final String name, final List<String> argumentNames) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        if (argumentNames == null) {
            throw new IllegalArgumentException("Argument names cannot be null.");
        }

        this.name = name;
        this.argumentNames = Collections.unmodifiableList(argumentNames);
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof SubroutineDefinition)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        final SubroutineDefinition other = (SubroutineDefinition) obj;
        return new EqualsBuilder().append(name, other.name).append(argumentNames, other.argumentNames).isEquals();
    }

    /**
     * Get the names of all the arguments in the subroutine.
     *
     * @return A {@link List} of {@link String} objects representing all of the argument names in the definition.
     */
    public List<String> getArgumentNames() {
        return argumentNames;
    }

    /**
     * Get the name of the subroutine.
     *
     * @return The name of the subroutine.
     */
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + argumentNames.hashCode();
        result = prime * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
