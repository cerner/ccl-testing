package com.cerner.ccl.j4ccl.impl.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.Field;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * Implementation of {@link Structure}.
 *
 * @author Mark Cummings
 */
public class StructureImpl implements Structure {
    private final Map<String, Field> fieldsMap;

    /**
     * Create a structure.
     *
     * @param fields
     *            A {@link Map}. The key values are the names of the fields; the values are {@link Field} objects
     *            representing the fields within this structure.
     * @throws NullPointerException
     *             If the given map is {@code null}
     */
    public StructureImpl(final Map<String, Field> fields) {
        if (fields == null) {
            throw new NullPointerException("Fields map cannot be null.");
        }

        this.fieldsMap = new HashMap<String, Field>(fields.size());
        for (final Entry<String, Field> entry : fields.entrySet()) {
            final String upperName = entry.getKey() == null ? null : entry.getKey().toUpperCase(Locale.getDefault());
            fieldsMap.put(upperName, entry.getValue());
        }
    }

    @Override
    public Field getField(final String fieldName) {
        if (fieldName == null) {
            throw new NullPointerException("Field name cannot be null.");
        }

        if (!hasMember(fieldName)) {
            throw new IllegalArgumentException("No such field: " + fieldName);
        }

        return fieldsMap.get(fieldName.toUpperCase(Locale.getDefault()));
    }

    @Override
    public List<Field> getFields() {
        final List<Field> fieldList = new ArrayList<Field>(fieldsMap.size());
        fieldList.addAll(fieldsMap.values());
        return Collections.unmodifiableList(fieldList);
    }

    @Override
    public DataType getType(final String fieldName) {
        return getField(fieldName).getType();
    }

    @Override
    public boolean hasMember(final String fieldName) {
        final String upperName = fieldName == null ? null : fieldName.toUpperCase(Locale.getDefault());
        return fieldsMap.containsKey(upperName);
    }

    /**
     * Add the declaration of this structure to a builder. <br>
     * This is intentionally kept at package-private to avoid it being part of the published API.
     *
     * @param builder
     *            A {@link StringBuilder} to which the declaration will be contributed.
     * @param level
     *            The level within the record structure at which the structure is nested. The root element (the one
     *            immediately below the record structure declaration) is at position 1.
     */
    void addDeclaration(final StringBuilder builder, final int level) {
        for (final Field field : getFields()) {
            builder.append(StringUtils.repeat(" ", level * 2));
            builder.append(level).append(" ");
            builder.append(field.getDeclaration());
            builder.append("\n");

            if (field.getType().isComplexType()) {
                ((StructureImpl) field.getStructure()).addDeclaration(builder, level + 1);
            }
        }
    }
}
