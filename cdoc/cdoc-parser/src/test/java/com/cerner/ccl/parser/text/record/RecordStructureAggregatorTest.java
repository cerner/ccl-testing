package com.cerner.ccl.parser.text.record;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.cerner.ccl.parser.data.CodeSet;
import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.EnumeratedValue;
import com.cerner.ccl.parser.data.record.AbstractParentRecordStructureMember;
import com.cerner.ccl.parser.data.record.FixedLengthRecordStructureList;
import com.cerner.ccl.parser.data.record.InterfaceStructureType;
import com.cerner.ccl.parser.data.record.RecordRecord;
import com.cerner.ccl.parser.data.record.RecordStructure;
import com.cerner.ccl.parser.data.record.RecordStructureCharacterField;
import com.cerner.ccl.parser.data.record.RecordStructureField;
import com.cerner.ccl.parser.text.data.util.CaseInsensitiveComparator;
import com.cerner.ccl.parser.text.documentation.Field;
import com.cerner.ccl.parser.text.documentation.RecordStructureDocumentation;

/**
 * Unit tests for {@link RecordStructureAggregator}.
 *
 * @author Joshua Hyde
 *
 */

public class RecordStructureAggregatorTest {
    private final RecordStructureAggregator aggregator = new RecordStructureAggregator();

    /**
     * Test the aggregation of structures and documentation into a record structure.
     */
    @Test
    public void testAggregate() {
        /*
         * Create a record
         */
        final StructureField recordField = new StructureField("recordField", 1, DataType.I2);
        final StructureRecord structureRecord = new StructureRecord("structureRecord", 1);
        structureRecord.addChildMember(recordField);

        /*
         * Create a nested list
         */
        final StructureCharacterField nestedListCharField = new StructureCharacterField("nestedListCharField", 1, 23);
        final AbstractParentStructure nestedList = new StructureList("nestedList", 1);
        nestedList.addChildMember(nestedListCharField);

        final StructureField rootLevelListField = new StructureField("rootLevelListField", 1, DataType.F8);
        final FixedLengthStructureList rootLevelList = new FixedLengthStructureList("rootLevelList", 1, 2);
        rootLevelList.addChildMember(rootLevelListField);
        rootLevelList.addChildMember(nestedList);

        final StructureField rootLevelField = new StructureField("rootLevelField", 1, DataType.I2);

        final Structure structure = new Structure("structure",
                Arrays.asList(rootLevelField, rootLevelList, structureRecord));

        /*
         * Create the documentation
         */
        final Field rootLevelFieldDoc = new Field(rootLevelField.getName(),
                "This is the description of the root level field", false,
                Arrays.asList(new EnumeratedValue("0", "The value is false"),
                        new EnumeratedValue("1", "The value is true")),
                Collections.<CodeSet> emptyList());
        final Field rootLevelListDoc = new Field(rootLevelList.getName(), "This is the root-level list.", false,
                Collections.<EnumeratedValue> emptyList(), Collections.<CodeSet> emptyList());
        final Field rootLevelListFieldDoc = new Field(rootLevelListField.getName(),
                "This is a field within the root-level list", true, Collections.<EnumeratedValue> emptyList(),
                Collections.singletonList(new CodeSet(1234, "This is a code set")));
        final Field nestedListDoc = new Field(nestedList.getName(), "This is a nested list", false,
                Collections.<EnumeratedValue> emptyList(), Collections.<CodeSet> emptyList());
        final Field nestedListCharFieldDoc = new Field(nestedListCharField.getName(),
                "This is a character field within a nested list.", true, Collections.<EnumeratedValue> emptyList(),
                Collections.<CodeSet> emptyList());
        final Field recordRecordDoc = new Field(structureRecord.getName(), "This is a record", false,
                Collections.<EnumeratedValue> emptyList(), Collections.<CodeSet> emptyList());
        final Field recordFieldDoc = new Field(recordField.getName(), "This is a field inside a record.", true,
                Collections.<EnumeratedValue> emptyList(), Collections.<CodeSet> emptyList());
        final RecordStructureDocumentation recordDocumentation = new RecordStructureDocumentation(
                "This is the description of the record structure", InterfaceStructureType.REPLY,
                Arrays.asList(rootLevelFieldDoc, rootLevelListDoc, rootLevelListFieldDoc, nestedListDoc,
                        nestedListCharFieldDoc, recordRecordDoc, recordFieldDoc));

        final Map<String, RecordStructureDocumentation> documentation = new TreeMap<String, RecordStructureDocumentation>(
                CaseInsensitiveComparator.getInstance());
        documentation.put(structure.getName(), recordDocumentation);

        final List<RecordStructure> records = aggregator.aggregate(Collections.singletonList(structure), documentation);
        assertThat(records).hasSize(1);

        final RecordStructure record = records.get(0);
        assertThat(record.getName()).isEqualTo("structure");
        assertThat(record.getStructureType()).isEqualTo(recordDocumentation.getStructureType());
        assertThat(record.getDescription()).isEqualTo(recordDocumentation.getDescription());
        assertThat(record.getRootLevelMemberCount()).isEqualTo(3); // just three root-level fields

        /*
         * Root-level members
         */
        final RecordStructureField recordRootField = record.getRootLevelMember(0);
        assertThat(recordRootField.getName()).isEqualTo(rootLevelField.getName());
        assertThat(recordRootField.getDataType()).isEqualTo(rootLevelField.getDataType());
        assertThat(recordRootField.getDescription()).isEqualTo(rootLevelFieldDoc.getDescription());
        assertThat(recordRootField.getValues()).isEqualTo(rootLevelFieldDoc.getValues());
        assertThat(recordRootField.getCodeSets()).isEqualTo(rootLevelFieldDoc.getCodeSets());
        assertThat(recordRootField.isOptional()).isEqualTo(rootLevelFieldDoc.isOptional());

        final FixedLengthRecordStructureList recordRootList = record.getRootLevelMember(1);
        assertThat(recordRootList.getName()).isEqualTo(rootLevelList.getName());
        assertThat(recordRootList.getDescription()).isEqualTo(rootLevelListDoc.getDescription());
        assertThat(recordRootList.getListSize()).isEqualTo(2);
        assertThat(recordRootList.getChildMemberCount()).isEqualTo(2); // just two members at this level

        final RecordRecord recordRootRecord = record.getRootLevelMember(2);
        assertThat(recordRootRecord.getName()).isEqualTo(structureRecord.getName());
        assertThat(recordRootRecord.getDescription()).isEqualTo(recordRecordDoc.getDescription());
        assertThat(recordRootRecord.getChildMemberCount()).isEqualTo(1);

        /*
         * Members beneath the root-level list
         */
        final RecordStructureField recordRootListField = recordRootList.getChildMember(0);
        assertThat(recordRootListField.getName()).isEqualTo(rootLevelListField.getName());
        assertThat(recordRootListField.getDataType()).isEqualTo(rootLevelListField.getDataType());
        assertThat(recordRootListField.getDescription()).isEqualTo(rootLevelListFieldDoc.getDescription());
        assertThat(recordRootListField.getCodeSets()).isEqualTo(rootLevelListFieldDoc.getCodeSets());
        assertThat(recordRootListField.getValues()).isEqualTo(rootLevelListFieldDoc.getValues());
        assertThat(recordRootListField.isOptional()).isEqualTo(rootLevelListFieldDoc.isOptional());

        final AbstractParentRecordStructureMember recordNestedList = recordRootList.getChildMember(1);
        assertThat(recordNestedList.getName()).isEqualTo(nestedList.getName());
        assertThat(recordNestedList.getDescription()).isEqualTo(nestedListDoc.getDescription());
        assertThat(recordNestedList.getChildMemberCount()).isEqualTo(1);

        /*
         * Members within the nested list
         */
        final RecordStructureCharacterField recordNestedField = recordNestedList.getChildMember(0);
        assertThat(recordNestedField.getName()).isEqualTo(nestedListCharField.getName());
        assertThat(recordNestedField.getDescription()).isEqualTo(nestedListCharFieldDoc.getDescription());
        assertThat(recordNestedField.getDataType()).isEqualTo(nestedListCharField.getDataType());
        assertThat(recordNestedField.getDataLength()).isEqualTo(nestedListCharField.getDataLength());
        assertThat(recordNestedField.isOptional()).isEqualTo(nestedListCharFieldDoc.isOptional());
        assertThat(recordNestedField.getCodeSets()).isEqualTo(nestedListCharFieldDoc.getCodeSets());
        assertThat(recordNestedField.getValues()).isEqualTo(nestedListCharFieldDoc.getValues());

        /*
         * Members within the record
         */
        final RecordStructureField recordRecordField = recordRootRecord.getChildMember(0);
        assertThat(recordRecordField.getName()).isEqualTo(recordField.getName());
        assertThat(recordRecordField.getDescription()).isEqualTo(recordFieldDoc.getDescription());
        assertThat(recordRecordField.getCodeSets()).isEmpty();
        assertThat(recordRecordField.getValues()).isEmpty();
    }

    /**
     * If no documentation is available, then an undocumented record structure should be returned.
     */
    @Test
    public void testAggregateNoDocumentation() {
        final StructureField structureField = new StructureField("structureField", 1, DataType.DQ8);
        final Structure structure = new Structure("structure-nodoc", Collections.singletonList(structureField));

        final List<RecordStructure> aggregates = aggregator.aggregate(Collections.singletonList(structure),
                Collections.<String, RecordStructureDocumentation> emptyMap());
        assertThat(aggregates).hasSize(1);

        final RecordStructure aggregate = aggregates.get(0);
        assertThat(aggregate.getName()).isEqualTo(structure.getName());
        assertThat(aggregate.getDescription()).isEmpty();
        assertThat(aggregate.getStructureType()).isNull();
        assertThat(aggregate.getRootLevelMemberCount()).isEqualTo(1);

        final RecordStructureField aggregateField = aggregate.getRootLevelMember(0);
        assertThat(aggregateField.getName()).isEqualTo(structureField.getName());
        assertThat(aggregateField.getDataType()).isEqualTo(structureField.getDataType());
        assertThat(aggregateField.getDescription()).isEmpty();
        assertThat(aggregateField.getCodeSets()).isEmpty();
        assertThat(aggregateField.getValues()).isEmpty();
        assertThat(aggregateField.isOptional()).isFalse();
    }

    /**
     * If the ordering of the documentation is wrong, then, starting with the mismatched field, the documentation
     * shouldn't match (and the aggregation shouldn't fail).
     */
    @Test
    public void testAggregateMismatchedDocumentation() {
        final StructureField fieldA = new StructureField("fieldA", 1, DataType.I2);
        final StructureField fieldB = new StructureField("fieldB", 1, DataType.I4);
        final StructureField fieldC = new StructureField("fieldC", 1, DataType.DQ8);
        final StructureField fieldD = new StructureField("fieldD", 1, DataType.VC);
        final Structure structure = new Structure("mismatched-doc", Arrays.asList(fieldA, fieldB, fieldC, fieldD));

        final Field fieldADoc = new Field("fieldA", "this is field a", false,
                Collections.singletonList(new EnumeratedValue("a value", "a description")),
                Collections.<CodeSet> emptyList());
        final Field fieldBDoc = new Field("fieldD", "this is field b", false, Collections.<EnumeratedValue> emptyList(),
                Collections.<CodeSet> emptyList());
        final Field fieldCDoc = new Field("fieldC", "this is field c", false, Collections.<EnumeratedValue> emptyList(),
                Collections.<CodeSet> emptyList());
        final Field fieldDDoc = new Field("fieldB", "this is field d", true, Collections.<EnumeratedValue> emptyList(),
                Collections.singletonList(new CodeSet(4758, "a code set")));
        // The field documentation is intentionally mis-organized
        final RecordStructureDocumentation documentation = new RecordStructureDocumentation(
                "this is a description of the record structure", null,
                Arrays.asList(fieldADoc, fieldBDoc, fieldCDoc, fieldDDoc));

        final List<RecordStructure> aggregates = aggregator.aggregate(Collections.singletonList(structure),
                Collections.singletonMap(structure.getName(), documentation));
        assertThat(aggregates).hasSize(1);

        final RecordStructure aggregate = aggregates.get(0);
        assertThat(aggregate.getName()).isEqualTo(structure.getName());
        assertThat(aggregate.getDescription()).isEqualTo(documentation.getDescription());
        assertThat(aggregate.getStructureType()).isEqualTo(documentation.getStructureType());
        assertThat(aggregate.getRootLevelMemberCount()).isEqualTo(4);

        final RecordStructureField recordFieldA = aggregate.getRootLevelMember(0);
        assertThat(recordFieldA.getName()).isEqualTo(fieldA.getName());
        assertThat(recordFieldA.getDataType()).isEqualTo(fieldA.getDataType());
        assertThat(recordFieldA.getDescription()).isEqualTo(fieldADoc.getDescription());
        assertThat(recordFieldA.getCodeSets()).isEqualTo(fieldADoc.getCodeSets());
        assertThat(recordFieldA.getValues()).isEqualTo(fieldADoc.getValues());
        assertThat(recordFieldA.isOptional()).isEqualTo(fieldADoc.isOptional());

        final RecordStructureField recordFieldB = aggregate.getRootLevelMember(1);
        assertThat(recordFieldB.getName()).isEqualTo(fieldB.getName());
        assertThat(recordFieldB.getDataType()).isEqualTo(fieldB.getDataType());
        assertThat(recordFieldB.getDescription()).isEmpty();
        assertThat(recordFieldB.getCodeSets()).isEmpty();
        assertThat(recordFieldB.getValues()).isEmpty();
        assertThat(recordFieldB.isOptional()).isFalse();

        final RecordStructureField recordFieldC = aggregate.getRootLevelMember(2);
        assertThat(recordFieldC.getName()).isEqualTo(fieldC.getName());
        assertThat(recordFieldC.getDataType()).isEqualTo(fieldC.getDataType());
        assertThat(recordFieldC.getDescription()).isEqualTo(fieldCDoc.getDescription());
        assertThat(recordFieldC.getCodeSets()).isEqualTo(fieldCDoc.getCodeSets());
        assertThat(recordFieldC.getValues()).isEqualTo(fieldCDoc.getValues());
        assertThat(recordFieldC.isOptional()).isEqualTo(fieldCDoc.isOptional());

        final RecordStructureField recordFieldD = aggregate.getRootLevelMember(3);
        assertThat(recordFieldD.getName()).isEqualTo(fieldD.getName());
        assertThat(recordFieldD.getDataType()).isEqualTo(fieldD.getDataType());
        assertThat(recordFieldD.getDescription()).isEmpty();
        assertThat(recordFieldD.getCodeSets()).isEmpty();
        assertThat(recordFieldD.getValues()).isEmpty();
        assertThat(recordFieldD.isOptional()).isFalse();
    }

    /**
     * Aggregating with {@code null} documentation should fail.
     */
    @Test
    public void testAggregateNullDocumentation() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            aggregator.aggregate(Collections.<Structure> emptyList(), null);
        });
        assertThat(e.getMessage()).isEqualTo("Record structure documentation cannot be null.");
    }

    /**
     * Aggregating a {@code null} list of structures should fail.
     */
    @Test
    public void testAggregateNullStructures() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            aggregator.aggregate(null, Collections.<String, RecordStructureDocumentation> emptyMap());
        });
        assertThat(e.getMessage()).isEqualTo("Structures cannot be null.");
    }
}
