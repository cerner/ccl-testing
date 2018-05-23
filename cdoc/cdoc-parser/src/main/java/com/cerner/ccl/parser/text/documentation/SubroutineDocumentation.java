package com.cerner.ccl.parser.text.documentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Documentation of a CCL subroutine.
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineDocumentation extends AbstractDocumentation {
    private final List<Parameter> parameters = new ArrayList<Parameter>();
    private final String returnDescription;

    /**
     * Create a subroutine documentation.
     *
     * @param description
     *            The description of the subroutine.
     * @param parameters
     *            A {@link List} of {@link Parameter} objects representing the parameters of the subroutine. If
     *            {@code null}, an empty list is stored internally.
     * @param returnDescription
     *            The description of the nature of the data, if returned, by the subroutine. This corresponds to the
     *            {@code @returns} documentation. If {@code null}, then a blank string is stored internally.
     */
    public SubroutineDocumentation(final String description, final List<Parameter> parameters,
            final String returnDescription) {
        super(description);

        if (parameters != null) {
            this.parameters.addAll(parameters);
        }
        this.returnDescription = returnDescription == null ? "" : returnDescription;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SubroutineDocumentation)) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        final SubroutineDocumentation other = (SubroutineDocumentation) obj;
        return new EqualsBuilder().append(parameters, other.parameters)
                .append(returnDescription, other.returnDescription).isEquals();
    }

    /**
     * Get the parameters of the subroutine.
     *
     * @return An immutable {@link List} of {@link Parameter} objects representing the documented parameters of this
     *         subroutine, if any.
     */
    public List<Parameter> getParameters() {
        return Collections.<Parameter> unmodifiableList(parameters);
    }

    /**
     * Get the description of the return data, if any.
     *
     * @return The description of the return data, if any.
     */
    public String getReturnDescription() {
        return returnDescription;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + parameters.hashCode();
        result = prime * result + returnDescription.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
