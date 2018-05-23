package com.cerner.ccl.parser.text.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cerner.ccl.parser.data.record.AbstractParentRecordStructureMember;
import com.cerner.ccl.parser.data.record.FixedLengthRecordStructureList;
import com.cerner.ccl.parser.data.record.RecordInclude;
import com.cerner.ccl.parser.data.record.RecordRecord;
import com.cerner.ccl.parser.data.record.RecordStructure;
import com.cerner.ccl.parser.data.record.RecordStructureCharacterField;
import com.cerner.ccl.parser.data.record.RecordStructureField;
import com.cerner.ccl.parser.data.record.RecordStructureList;
import com.cerner.ccl.parser.data.record.RecordStructureMember;
import com.cerner.ccl.parser.data.record.StructureMember;
import com.cerner.ccl.parser.text.documentation.Field;
import com.cerner.ccl.parser.text.documentation.RecordStructureDocumentation;

/**
 * An aggregator that produces collections of {@link RecordStructure} objects out of structure definitions and
 * documentation objects.
 *
 * @author Joshua Hyde
 *
 */

public class RecordStructureAggregator {

    private final Logger logger = LoggerFactory.getLogger(RecordStructureAggregator.class);

    /**
     * Aggregate record structures together.
     *
     * @param structures
     *            A {@link List} of {@link Structure} objects representing the structures defined in the CCL script.
     * @param recordStructureDocumentation
     *            A {@link Map}; the keys are the names of the documented record structure and the values are
     *            {@link RecordStructureDocumentation} objects representing the documentation of each object. The keys
     *            are assumed to be case-insensitive.
     * @return A {@link List} of {@link RecordStructure} representing a unification of structure definitions with their
     *         documentation.
     * @throws IllegalArgumentException
     *             If any of the given objects are {@code null}.
     */
    public List<RecordStructure> aggregate(final List<Structure> structures,
            final Map<String, RecordStructureDocumentation> recordStructureDocumentation) {
        if (structures == null) {
            throw new IllegalArgumentException("Structures cannot be null.");
        }

        if (recordStructureDocumentation == null) {
            throw new IllegalArgumentException("Record structure documentation cannot be null.");
        }

        final List<RecordStructure> records = new ArrayList<RecordStructure>(structures.size());
        for (final Structure structure : structures) {
            final String structureName = structure.getName();
            if (recordStructureDocumentation.containsKey(structureName)) {
                final RecordStructureDocumentation documentation = recordStructureDocumentation.get(structureName);
                records.add(new RecordStructure(structureName, toRecordMembers(structure, documentation.getFields()),
                        documentation.getDescription(), documentation.getStructureType()));
            } else {
                records.add(new RecordStructure(structureName,
                        toRecordMembers(structure, Collections.<Field> emptyList())));
            }
        }

        return records;
    }

    /**
     * Create a record structure field.
     *
     * @param structureField
     *            A {@link StructureField} out of which a record structure field is to be made.
     * @param field
     *            A {@link Field} that represents the documentation of this field, if any is available. If {@code null},
     *            it is assumed that this field is undocumented.
     * @return A {@link RecordStructureField} representing the documented field.
     */
    private RecordStructureField toField(final StructureField structureField, final Field field) {
        if (structureField instanceof StructureCharacterField) {
            return field == null
                    ? new RecordStructureCharacterField(structureField.getName(), structureField.getLevel(),
                            ((StructureCharacterField) structureField).getDataLength())
                    : new RecordStructureCharacterField(structureField.getName(), structureField.getLevel(),
                            ((StructureCharacterField) structureField).getDataLength(), field.isOptional(),
                            field.getDescription(), field.getCodeSets(), field.getValues());
        }
        return field == null
                ? new RecordStructureField(structureField.getName(), structureField.getLevel(),
                        structureField.getDataType())
                : new RecordStructureField(structureField.getName(), structureField.getLevel(),
                        structureField.getDataType(), field.isOptional(), field.getDescription(), field.getCodeSets(),
                        field.getValues());
    }

    /**
     * Create a record structure list.
     *
     * @param list
     *            A {@link AbstractParentStructure} out of which a record structure list is to be made.
     * @param structureMembers
     *            A {@link List} of {@link RecordStructureMember} objects representing the documented members of the
     *            list to be created.
     * @param field
     *            A {@link Field} that represents the documentation of this list, if any is available. If {@code null},
     *            it is assumed that this list is undocumented.
     * @return A {@link AbstractParentRecordStructureMember} representing the documented list.
     */
    private AbstractParentRecordStructureMember toList(final AbstractParentStructure list,
            final List<RecordStructureMember> structureMembers, final Field field) {
        if (list instanceof FixedLengthStructureList) {
            return field == null
                    ? new FixedLengthRecordStructureList(list.getName(), list.getLevel(), structureMembers,
                            ((FixedLengthStructureList) list).getListSize())
                    : new FixedLengthRecordStructureList(list.getName(), list.getLevel(), field.getDescription(),
                            structureMembers, ((FixedLengthStructureList) list).getListSize());
        }
        return field == null ? new RecordStructureList(list.getName(), list.getLevel(), structureMembers)
                : new RecordStructureList(list.getName(), list.getLevel(), field.getDescription(), structureMembers);
    }

    /**
     * Convert a structure definition's fields to a set of documented record structure members. This will attempt to
     * match structure members, as they are found in sequence within the structure, to the documented fields as they are
     * encountered in the list of {@code @field} objects. This means that, if the sequences do not match, then the
     * documentation will become very, very broken - any members starting at and after the first mismatch between
     * structure definition and documented fields will have no documentation. The conversion won't fail, but it will be
     * somewhat useless.
     *
     * @param structure
     *            A {@link Structure} object representing the structure definition of the record structure.
     * @param fields
     *            A {@link List} of {@link Field} objects representing the documented fields for the record structure.
     * @return A {@link List} of {@link RecordStructureMember} objects representing the documented structure members.
     */
    private List<RecordStructureMember> toRecordMembers(final Structure structure, final List<Field> fields) {
        final IntegerTracker tracker = new IntegerTracker();
        final List<RecordStructureMember> recordMembers = new ArrayList<RecordStructureMember>();
        for (int i = 0, size = structure.getRootLevelMemberCount(); i < size; i++) {
            recordMembers.add(toRecordMember(structure.getRootLevelMember(i), fields, tracker));
        }
        if (tracker.get() < fields.size()) {
            logger.warn("CDOC warning");
            logger.warn("Structure '" + structure.getName() + "' has fewer fields than are documented for it.");
        }

        return recordMembers;
    }

    /**
     * Convert a structure definition member into a documented record structure member.
     *
     * @param member
     *            A {@link StructureMember} representing the member of the record structure definition to be converted.
     * @param fields
     *            A {@link List} of {@link Field} objects representing the documented fields for the record structure.
     * @param tracker
     *            An {@link IntegerTracker} used to track where within the list of field documentation the documentation
     *            was last found for a given definition member.
     * @return A {@link RecordStructureMember} object representing the documented member.
     */
    private RecordStructureMember toRecordMember(final StructureMember member, final List<Field> fields,
            final IntegerTracker tracker) {
        Field field = null;
        if (!fields.isEmpty()) {
            if (tracker.get() < fields.size()) {
                if (fields.get(tracker.get()).getName().equalsIgnoreCase(member.getName())) {
                    field = fields.get(tracker.get());
                } else {
                    logger.warn("CDOC warning");
                    logger.warn("Documented name '" + fields.get(tracker.get()).getName()
                            + "' does not match record structure member name '" + member.getName() + "'.");
                }
                tracker.add(1);
            } else {
                logger.warn("CDOC warning");
                logger.warn(
                        "Documentation for record structure member '" + member.getName() + "' is missing or invalid.");
            }
        }

        if (member instanceof AbstractParentStructure) {
            final AbstractParentStructure structureList = (AbstractParentStructure) member;
            final List<RecordStructureMember> structureMembers = new ArrayList<RecordStructureMember>();
            for (int i = 0, size = structureList.getChildMemberCount(); i < size; i++) {
                structureMembers.add(toRecordMember(structureList.getChildMember(i), fields, tracker));
            }

            return member instanceof StructureRecord ? toRecord((StructureRecord) member, structureMembers, field)
                    : toList((AbstractParentStructure) member, structureMembers, field);
        } else if (member instanceof StructureField) {
            return toField((StructureField) member, field);
        }
        return new RecordInclude(((StructureInclude) member).getName());
    }

    /**
     * Create a record.
     *
     * @param record
     *            A {@link StructureRecord}.
     * @param members
     *            A {@link List} of {@link RecordStructureMember} objects that is to make up the children of the created
     *            record.
     * @param field
     *            A {@link Field} object that potentially documents the structure to be created; if {@code null}, then
     *            the record is treated as undocumented.
     * @return A {@link RecordRecord}.
     */
    private RecordRecord toRecord(final StructureRecord record, final List<RecordStructureMember> members,
            final Field field) {
        return field == null ? new RecordRecord(record.getName(), record.getLevel(), members)
                : new RecordRecord(record.getName(), record.getLevel(), field.getDescription(), members);
    }

    /**
     * A class used to track the value of an integer.
     *
     * @author Joshua Hyde
     *
     */
    private static class IntegerTracker {
        private int integer;

        IntegerTracker() {
        }

        /**
         * Add a value to the integer.
         *
         * @param delta
         *            The amount to add.
         */
        public void add(final int delta) {
            this.integer += delta;
        }

        /**
         * Get the current value of the integer.
         *
         * @return The current value of the integer.
         */
        public int get() {
            return integer;
        }
    }
}
