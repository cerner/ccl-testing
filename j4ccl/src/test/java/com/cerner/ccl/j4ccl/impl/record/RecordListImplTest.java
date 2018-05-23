package com.cerner.ccl.j4ccl.impl.record;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.cerner.ccl.j4ccl.impl.record.factory.RecordImplFactory;
import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.Field;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.RecordList;
import com.cerner.ccl.j4ccl.record.Structure;
import com.cerner.ccl.j4ccl.record.StructureBuilder;

/**
 * Unit test for {@link RecordListImpl}.
 *
 * @author Joshua Hyde
 *
 */

public class RecordListImplTest {
    private static final Structure MOCK_STRUCTURE = mock(Structure.class);
    private static final Record MOCK_RECORD = mock(Record.class);
    private static final int LIST_SIZE = 1;
    private static final RecordImplFactory MOCK_FACTORY = mock(RecordImplFactory.class);

    /**
     * Test that constructing a list with a negative list size fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testConstructionBadListSize() {
        new RecordListImpl(MOCK_RECORD, MOCK_STRUCTURE, -1);
    }

    /**
     * Test that constructing a list with a null structure fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void testConstructionNullStructure() {
        new RecordListImpl(MOCK_RECORD, null, LIST_SIZE);
    }

    /**
     * Test that construction with a null parent record fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void testConstructionNullParentRecord() {
        new RecordListImpl(null, MOCK_STRUCTURE, LIST_SIZE);
    }

    /**
     * Test the fetching of the underlying list elements.
     */
    @Test
    public void testGetAll() {
        final Record firstRecord = mock(Record.class);
        final Record secondRecord = mock(Record.class);
        final RecordImplFactory factory = mock(RecordImplFactory.class);
        when(factory.createNestedRecord(MOCK_STRUCTURE, MOCK_RECORD)).thenReturn(firstRecord).thenReturn(secondRecord);

        final RecordListImpl list = new RecordListImpl(MOCK_RECORD, MOCK_STRUCTURE, 2, factory);
        assertThat(list.getAll()).containsExactly(firstRecord, secondRecord);
    }

    /**
     * Test the getting of a list element by index.
     */
    @Test
    public void testGet() {
        final Record record = mock(Record.class);
        final RecordImplFactory factory = mock(RecordImplFactory.class);
        when(factory.createNestedRecord(MOCK_STRUCTURE, MOCK_RECORD)).thenReturn(record);

        final RecordListImpl list = new RecordListImpl(MOCK_RECORD, MOCK_STRUCTURE, LIST_SIZE, factory);
        assertThat(list.get(0)).isSameAs(record);
    }

    /**
     * Test the retrieval of the structure backing the list.
     */
    @Test
    public void testGetStructure() {
        final Structure structure = StructureBuilder.getBuilder().addDQ8("dq8_field").build();
        final RecordList list = new RecordListImpl(MOCK_RECORD, structure, 1);
        assertThat(list.getStructure()).isEqualTo(structure);
    }

    /**
     * Test getting the size of the list.
     */
    @Test
    public void testGetSize() {
        final RecordListImpl list = new RecordListImpl(MOCK_RECORD, MOCK_STRUCTURE, LIST_SIZE, MOCK_FACTORY);
        assertThat(list.getSize()).isEqualTo(LIST_SIZE);
    }

    /**
     * Test the fetching of an iterator to step through the underlying list of elements.
     */
    @Test
    public void testIterator() {
        final Record record = mock(Record.class);
        final RecordImplFactory factory = mock(RecordImplFactory.class);
        when(factory.createNestedRecord(MOCK_STRUCTURE, MOCK_RECORD)).thenReturn(record);

        final RecordListImpl list = new RecordListImpl(MOCK_RECORD, MOCK_STRUCTURE, LIST_SIZE, factory);
        final Iterator<Record> iterator = list.iterator();

        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isSameAs(record);
    }

    /**
     * Test the fetching of an iterator to step through the underlying list of elements when there actually are things
     * to step through.
     */
    @Test
    public void testIteratorForReal() {
        final Map<String, Field> listFields = new TreeMap<String, Field>();
        listFields.put("name_l", new PrimitiveFieldImpl("name_l", DataType.VC));
        listFields.put("id_l", new PrimitiveFieldImpl("id_l", DataType.F8));
        final Structure listStructure = new StructureImpl(listFields);

        final Map<String, Field> rootFields = new TreeMap<String, Field>();
        rootFields.put("name", new PrimitiveFieldImpl("name", DataType.VC));
        rootFields.put("id", new PrimitiveFieldImpl("id", DataType.F8));
        rootFields.put("relations", new FixedLengthListFieldImpl("relations", listStructure, 3));
        final Structure rootStructure = new StructureImpl(rootFields);

        final Record rootRecord = new RecordImpl("root", rootStructure, null);
        final Record listRecord = new RecordImpl("list", listStructure, rootRecord);

        assertThat(rootRecord.getList("relations").getStructure()).isEqualTo(listRecord.getStructure());
        assertThat(rootRecord.getList("relations").getSize()).isEqualTo(3);
        int iteratorCount = 0;
        final Iterator<Record> it = rootRecord.getList("relations").iterator();
        while (it.hasNext()) {
            iteratorCount++;
            assertThat(it.next().getStructure()).isEqualTo(listStructure);
        }
        assertThat(iteratorCount).isEqualTo(3);
    }
}
