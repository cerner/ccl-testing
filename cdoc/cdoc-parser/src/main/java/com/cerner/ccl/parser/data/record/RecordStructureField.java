package com.cerner.ccl.parser.data.record;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.cerner.ccl.parser.data.CodeSet;
import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.EnumeratedValue;
import com.cerner.ccl.parser.data.SimpleDataTyped;

/**
 * Definition of an object representing a field within a record structure, be it an element in a list or just a member
 * of the structure. As an example, consider the following structures:
 *
 * <pre>
 *   record request (
 *      1 requested[*]
 *          2 person_id = f8
 *   )
 *
 *   record reply (
 *      1 status = vc
 *   )
 * </pre>
 *
 * ..."status" and "person_id" would be considered fields; "requested" is not.
 * <p>
 * The equality of two fields is currently assumed to be limited to the scope of the immediate hierarchy; as such,
 * equality of two fields is determined by the (case-insensitive) comparison of their names and data types.
 *
 * @author Joshua Hyde
 *
 */

public class RecordStructureField extends SimpleDataTyped implements HierarchicalRecordStructureMember {
    private final String name;
    private final String description;
    private final List<CodeSet> codeSets;
    private final List<EnumeratedValue> values;
    private final boolean optional;
    private final int level;

    /**
     * Create a non-fixed-length field without any documentation.
     *
     * @param name
     *            The name of the field.
     * @param level
     *            The {@link #getLevel() level} of this field.
     * @param dataType
     *            The data type of the field.
     * @throws IllegalArgumentException
     *             If the given name is {@code null} or the level is less than 1.
     */
    public RecordStructureField(final String name, final int level, final DataType dataType) {
        this(name, level, dataType, false, null, null, null);
    }

    /**
     * Create a non-fixed-length record structure field.
     *
     * @param name
     *            The name of the field.
     * @param level
     *            The {@link #getLevel() level} of this field.
     * @param dataType
     *            A {@link DataType} enum representing the data type of the field.
     * @param optional
     *            A {@code boolean} value that corresponds to the presence of the {@code @optional} documentation tag.
     * @param description
     *            The description of the field.
     * @param codeSets
     *            A {@link List} of {@link CodeSet} objects representing the {@code @codeSet} values, if any, associated
     *            with this field.
     * @param values
     *            A {@link List} of {@link EnumeratedValue} objects representing the {@code @value} values, if any,
     *            associated with this field.
     * @throws IllegalArgumentException
     *             If the given name is {@code null} or the level is less than 1.
     */
    public RecordStructureField(final String name, final int level, final DataType dataType, final boolean optional,
            final String description, final List<CodeSet> codeSets, final List<EnumeratedValue> values) {
        super(dataType);

        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null.");
        }

        if (level < 1) {
            throw new IllegalArgumentException("Level cannot be less than 1: " + Integer.toString(level));
        }

        this.name = name;
        this.level = level;
        this.description = description == null ? "" : description;
        this.optional = optional;
        this.codeSets = codeSets == null || codeSets.isEmpty() ? Collections.<CodeSet> emptyList()
                : Collections.unmodifiableList(codeSets);
        this.values = values == null || values.isEmpty() ? Collections.<EnumeratedValue> emptyList()
                : Collections.unmodifiableList(values);
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof RecordStructureField)) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        final RecordStructureField other = (RecordStructureField) obj;
        return new EqualsBuilder()
                .append(name.toLowerCase(Locale.getDefault()), other.name.toLowerCase(Locale.getDefault())).isEquals();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * Get the code sets, if any, associated with this field.
     *
     * @return An immutable {@link List} of {@link CodeSet} objects representing the code sets associated with this
     *         field, if any.
     */
    public List<CodeSet> getCodeSets() {
        return codeSets;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getLevel() {
        return level;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Get the enumerated values, if any, associated with this field.
     *
     * @return An immutable {@link List} of {@link EnumeratedValue} objects representing the enumerated values
     *         associated with this field, if any.
     */
    public List<EnumeratedValue> getValues() {
        return values;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + name.toLowerCase(Locale.getDefault()).hashCode();
    }

    /**
     * Determine whether or not the population of this field is required to be populated.
     *
     * @return {@code true} if this field is not required to be populated; {@code false} if it must be populated.
     */
    public boolean isOptional() {
        return optional;
    }
}
