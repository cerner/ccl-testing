package com.cerner.ccl.parser.data.record;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.cerner.ccl.parser.data.CodeSet;
import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.EnumeratedValue;
import com.cerner.ccl.parser.data.FixedLengthDataTyped;

/**
 * This class represents a fixed-length character field in a CCL record structure. For example:
 *
 * <pre>
 *   record request (
 *      1 person_id = f8
 *      1 status = c1
 *   )
 * </pre>
 *
 * ..."status" would be described this class.
 *
 * @author Joshua Hyde
 *
 */

public class RecordStructureCharacterField extends RecordStructureField implements FixedLengthDataTyped {
    private final int dataLength;

    /**
     * Create a non-fixed-length field without any documentation.
     *
     * @param name
     *            The name of the field.
     * @param level
     *            The {@link #getLevel() level} of this field.
     * @param dataLength
     *            The length of the character field.
     * @throws IllegalArgumentException
     *             If the given name is {@code null}.
     */
    public RecordStructureCharacterField(final String name, final int level, final int dataLength) {
        this(name, level, dataLength, false, null, null, null);
    }

    /**
     * Create a non-fixed-length record structure field.
     *
     * @param name
     *            The name of the field.
     * @param level
     *            The {@link #getLevel() level} of this field.
     * @param dataLength
     *            The length of the data field.
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
     *             If the given name is {@code null}.
     */
    public RecordStructureCharacterField(final String name, final int level, final int dataLength,
            final boolean optional, final String description, final List<CodeSet> codeSets,
            final List<EnumeratedValue> values) {
        super(name, level, DataType.CHAR, optional, description, codeSets, values);

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

        if (!(obj instanceof RecordStructureCharacterField)) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }
        final RecordStructureCharacterField other = (RecordStructureCharacterField) obj;
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
        return 31 * super.hashCode() + dataLength;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
