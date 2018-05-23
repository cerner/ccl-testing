package com.cerner.ccl.parser.text.documentation;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.CodeSet;
import com.cerner.ccl.parser.data.Described;
import com.cerner.ccl.parser.data.EnumeratedValue;
import com.cerner.ccl.parser.data.Named;

/**
 * Definition of a bean corresponding to the {@code @field} documentation tag.
 *
 * @author Joshua Hyde
 *
 */

public class Field implements Described, Named {
    private final List<EnumeratedValue> values;
    private final List<CodeSet> codeSets;
    private final String name;
    private final String description;
    private final boolean optional;

    /**
     * Create a field.
     *
     * @param name
     *            The name of the field.
     * @param description
     *            The description of the field. If {@code null}, a blank string is stored internally.
     * @param isOptional
     *            A {@code boolean} indicator of the optionality of the population of this field; if {@code true}, the
     *            population of this field for a successful execution is optional; if {@code false} , its population is
     *            required.
     * @param values
     *            A {@link List} of {@link EnumeratedValue} objects representing the enumerated values associated with
     *            this field. If {@code null}, an empty list is stored internally.
     * @param codeSets
     *            A {@link List} of {@link CodeSet} objects representing the code values associated with this field. If
     *            {@code null}, an empty list is stored internally.
     */
    public Field(final String name, final String description, final boolean isOptional,
            final List<EnumeratedValue> values, final List<CodeSet> codeSets) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        this.name = name;
        this.description = description == null ? "" : description;
        this.optional = isOptional;
        this.values = values == null || values.isEmpty() ? Collections.<EnumeratedValue> emptyList()
                : Collections.unmodifiableList(values);
        this.codeSets = codeSets == null || codeSets.isEmpty() ? Collections.<CodeSet> emptyList()
                : Collections.unmodifiableList(codeSets);
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Field)) {
            return false;
        }

        final Field other = (Field) obj;
        return new EqualsBuilder()
                .append(name.toLowerCase(Locale.getDefault()), other.name.toLowerCase(Locale.getDefault()))
                .append(description, other.description)
                .append(optional, other.optional).append(codeSets, other.codeSets).append(values, other.values)
                .isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Get a list of all code sets used by this field.
     *
     * @return An immutable {@link List} of {@link CodeSet} objects representing the possible code sets associated with
     *         this field.
     */
    public List<CodeSet> getCodeSets() {
        return codeSets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get a list of all enumerated values used by this field.
     *
     * @return An immutable {@link List} of {@link EnumeratedValue} objects representing the possible enumerated values
     *         associated with this field.
     */
    public List<EnumeratedValue> getValues() {
        return values;
    }

    @Override
    public int hashCode() {
        return name.toLowerCase(Locale.getDefault()).hashCode();
    }

    /**
     * Determine whether or not this field's population is optional.
     *
     * @return {@code true} if this field's population is optional for a successful execution; {@code false} if not.
     */
    public boolean isOptional() {
        return optional;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
