package com.cerner.ccl.j4ccl.record;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.cerner.ccl.j4ccl.impl.record.StructureImpl;
import com.cerner.ccl.j4ccl.impl.record.factory.FieldImplFactory;
import com.cerner.ccl.j4ccl.record.factory.RecordFactory;

/**
 * Builder for constructing instances of {@link Structure}. <br>
 * A structure is defined as the skeleton that backs a record structure's framework. For example, take the two following
 * record structures:
 *
 * <pre>
 * record recordOne (
 *   1 int_var = i2
 * )
 *
 * record recordTwo (
 *   1 int_var = i2
 * )
 * </pre>
 *
 * Each is an <i>instance</i> of the same structure - in terms of this API, each is a {@link Record} built out of the
 * same {@link Structure}. To construct each instance, a {@code Structure} must be constructed comprised of the I2
 * field. {@code recordOne} and {@code recordTwo} are then constructed using
 * {@link RecordFactory#create(String, Structure)}.
 *
 * @author Joshua Hyde
 * @author Mark Cummings
 */
public abstract class StructureBuilder {
    private static final FieldImplFactory DEFAULT_FIELD_FACTORY = new FieldImplFactory();

    /**
     * Get a new builder instance.
     *
     * @return A {@link StructureBuilder} object that can be used to construct {@link Structure} objects.
     */
    public static StructureBuilder getBuilder() {
        return new DefaultStructureBuilder(DEFAULT_FIELD_FACTORY);
    }

    /**
     * Get a structure builder out of an existing structure.
     *
     * @param existingStructure
     *            The {@link Structure} from which a builder is to be created.
     * @return A {@link StructureBuilder} object that can be used to construct {@link Structure} objects.
     */
    public static StructureBuilder getBuilder(final Structure existingStructure) {
        return new DefaultStructureBuilder(DEFAULT_FIELD_FACTORY, existingStructure);
    }

    /**
     * Creates a new {@link Structure structure}.
     *
     * @return the new structure
     */
    public abstract Structure build();

    /**
     * Adds a {@link DataType#CHARACTER CHARACTER} field with the given name.
     *
     * @param name
     *            The name of the field.
     * @param length
     *            The length of (or number of characters within) the data field.
     * @return This object.
     * @throws IllegalArgumentException
     *             If a field with the given name already exists within this structure.
     */
    public abstract StructureBuilder addChar(String name, int length);

    /**
     * Adds a {@link DataType#DQ8 DQ8} field with the given name.
     *
     * @param name
     *            the field name
     * @return This object.
     * @throws IllegalArgumentException
     *             if a field with the given name already exists within this structure
     */
    public abstract StructureBuilder addDQ8(String name);

    /**
     * Adds a dynamic list field with the given name.
     *
     * @param name
     *            the field name
     * @param structure
     *            a {@link Structure structure} describing the structure of the list elements
     * @return This object.
     * @throws IllegalArgumentException
     *             if a field with the given name already exists within this structure
     */
    public abstract StructureBuilder addDynamicList(String name, Structure structure);

    /**
     * Adds a {@link DataType#F8 F8} field with the given name.
     *
     * @param name
     *            the field name
     * @return This object.
     * @throws IllegalArgumentException
     *             if a field with the given name already exists within this structure
     */
    public abstract StructureBuilder addF8(String name);

    /**
     * Adds a {@link DataType#I2 I2} field with the given name.
     *
     * @param name
     *            the field name
     * @return This object.
     * @throws IllegalArgumentException
     *             if a field with the given name already exists within this structure
     */
    public abstract StructureBuilder addI2(String name);

    /**
     * Adds a {@link DataType#I4 I4} field with the given name.
     *
     * @param name
     *            the field name
     * @return This object.
     * @throws IllegalArgumentException
     *             if a field with the given name already exists within this structure
     */
    public abstract StructureBuilder addI4(String name);

    /**
     * Adds a fixed-size list field with the given name.
     *
     * @param name
     *            the field name
     * @param structure
     *            a {@link Structure structure} describing the structure of the list elements
     * @param size
     *            the list size
     * @return This object.
     * @throws IllegalArgumentException
     *             if a field with the given name already exists within this structure, or size is less than 1
     */
    public abstract StructureBuilder addList(String name, Structure structure, int size);

    /**
     * Adds a {@link Record record} field with the given name.
     *
     * @param name
     *            the field name a {@link StructureBuilder builder} describing the structure of the field
     * @param structure
     *            the {@link Structure structure} structure describing this field
     * @return This object.
     * @throws IllegalArgumentException
     *             if a field with the given name already exists within this structure
     */
    public abstract StructureBuilder addRecord(String name, Structure structure);

    /**
     * Add a status_data element to the structure. <br>
     * The structure of the record added will be:
     *
     * <pre>
     * 1 status_data
     *     2 status = c1
     *     2 subeventstatus[1]
     *       3 OperationName = c25
     *       3 OperationStatus = c1
     *       3 TargetObjectName = c25
     *       3 TargetObjectValue = vc
     * </pre>
     *
     * @return This object.
     */
    public abstract StructureBuilder addStatusData();

    /**
     * Adds a {@link DataType#VC VC} field with the given name.
     *
     * @param name
     *            the field name
     * @return This object.
     * @throws IllegalArgumentException
     *             if a field with the given name already exists within this structure
     */
    public abstract StructureBuilder addVC(String name);

    /**
     * Default implementation of {@link StructureBuilder}.
     *
     * @author Mark Cummings
     * @author Joshua Hyde
     */
    private static class DefaultStructureBuilder extends StructureBuilder {
        private final Map<String, Field> fields = new HashMap<String, Field>();
        private final FieldImplFactory fieldFactory;

        /**
         * Create a structure builder.
         *
         * @param fieldFactory
         *            A {@link FieldImplFactory} used to construct instances of {@link Field} objects.
         */
        public DefaultStructureBuilder(final FieldImplFactory fieldFactory) {
            this.fieldFactory = fieldFactory;
        }

        /**
         * Create a structure builder out of an existing structure.
         *
         * @param fieldFactory
         *            A {@link FieldImplFactory} used to construct instances of {@link Field} objects.
         * @param existingStructure
         *            The {@link Structure} whose structure is to be copied and be used in this builder's
         *            initialization.
         */
        public DefaultStructureBuilder(final FieldImplFactory fieldFactory, final Structure existingStructure) {
            this(fieldFactory);

            for (final Field field : existingStructure.getFields())
                deepCopyField(field);
        }

        @Override
        public StructureBuilder addChar(final String name, final int length) {
            addField(name, fieldFactory.createCharacterField(name, length));
            return this;
        }

        @Override
        public StructureBuilder addDQ8(final String name) {
            addField(name, fieldFactory.createSimpleField(name, DataType.DQ8));
            return this;
        }

        @Override
        public StructureBuilder addDynamicList(final String name, final Structure structure) {
            addField(name, fieldFactory.createDynamicListField(name, structure));
            return this;
        }

        @Override
        public StructureBuilder addF8(final String name) {
            addField(name, fieldFactory.createSimpleField(name, DataType.F8));
            return this;
        }

        @Override
        public StructureBuilder addI2(final String name) {
            addField(name, fieldFactory.createSimpleField(name, DataType.I2));
            return this;
        }

        @Override
        public StructureBuilder addI4(final String name) {
            addField(name, fieldFactory.createSimpleField(name, DataType.I4));
            return this;
        }

        @Override
        public StructureBuilder addList(final String name, final Structure structure, final int size) {
            addField(name, fieldFactory.createListField(name, structure, size));
            return this;
        }

        @Override
        public StructureBuilder addRecord(final String name, final Structure structure) {
            addField(name, fieldFactory.createRecordField(name, structure));
            return this;
        }

        @Override
        public StructureBuilder addStatusData() {
            final Structure subeventStructure = StructureBuilder.getBuilder().addChar("OperationName", 25)
                    .addChar("OperationStatus", 1).addChar("TargetObjectName", 25).addVC("TargetObjectValue").build();
            final Structure statusData = StructureBuilder.getBuilder().addChar("status", 1)
                    .addList("subeventstatus", subeventStructure, 1).build();
            addField("status_data", fieldFactory.createRecordField("status_data", statusData));
            return this;
        }

        @Override
        public StructureBuilder addVC(final String name) {
            addField(name, fieldFactory.createSimpleField(name, DataType.VC));
            return this;
        }

        @Override
        public Structure build() {
            return new StructureImpl(fields);
        }

        /**
         * Add a field to the structure.
         *
         * @param name
         *            The name of the field to be added.
         * @param field
         *            A {@link Field} object to be added to the structure.
         */
        private void addField(final String name, final Field field) {
            if (name == null)
                throw new NullPointerException("Field name cannot be null.");

            final String upperName = name.toUpperCase(Locale.getDefault());
            if (fields.containsKey(upperName))
                throw new IllegalArgumentException("field with name " + name + " already exists");

            fields.put(upperName, field);
        }

        /**
         * Perform a deep copy of a field into this builder.
         *
         * @param field
         *            The {@link Field} to be deep-copied into this builder.
         */
        private void deepCopyField(final Field field) {
            switch (field.getType()) {
            case CHARACTER:
                addChar(field.getName(), (int) field.getDataLength());
                break;
            case DQ8:
                addDQ8(field.getName());
                break;
            case DYNAMIC_LIST:
                addDynamicList(field.getName(),
                        new DefaultStructureBuilder(fieldFactory, field.getStructure()).build());
                break;
            case F8:
                addF8(field.getName());
                break;
            case I2:
                addI2(field.getName());
                break;
            case I4:
                addI4(field.getName());
                break;
            case LIST:
                addList(field.getName(), new DefaultStructureBuilder(fieldFactory, field.getStructure()).build(),
                        field.getListSize());
                break;
            case RECORD:
                addRecord(field.getName(), new DefaultStructureBuilder(fieldFactory, field.getStructure()).build());
                break;
            case VC:
                addVC(field.getName());
                break;
            default:
                throw new IllegalArgumentException(
                        "Unrecognized data type " + field.getType() + " for field " + field.getName());
            }
        }
    }
}
