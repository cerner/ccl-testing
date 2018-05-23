package com.cerner.ccl.j4ccl.impl.record;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Iterator;

import org.junit.Test;

import com.cerner.ccl.j4ccl.impl.record.factory.RecordImplFactory;
import com.cerner.ccl.j4ccl.record.DynamicRecordList;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.Structure;
import com.cerner.ccl.j4ccl.record.StructureBuilder;

/**
 * Unit test of {@link DynamicRecordListImpl}.
 *
 * @author Joshua Hyde
 *
 */

public class DynamicRecordListImplTest {
    private static final Structure STRUCTURE = mock(Structure.class);
    private static final Record PARENT = mock(Record.class);

    /**
     * Test that constructing a record list with a null structure fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void testConstructionNullStructure() {
        new DynamicRecordListImpl(null, PARENT);
    }

    /**
     * Test that constructing a record list with a null parent record fails.
     */
    @SuppressWarnings("unused")
    @Test(expected = NullPointerException.class)
    public void testConstructionNullParentRecord() {
        new DynamicRecordListImpl(STRUCTURE, null);
    }

    /**
     * Test the addition of a record to a list.
     */
    @Test
    public void testAddItem() {
        final Record record = mock(Record.class);
        final RecordImplFactory factory = mock(RecordImplFactory.class);
        when(factory.createNestedRecord(STRUCTURE, PARENT)).thenReturn(record);

        final DynamicRecordListImpl list = new DynamicRecordListImpl(STRUCTURE, PARENT, factory);
        assertThat(list.addItem()).isEqualTo(record);
    }

    /**
     * Test the fetching of all items.
     */
    @Test
    public void testGetAll() {
        final Record firstRecord = mock(Record.class);
        final Record secondRecord = mock(Record.class);
        final RecordImplFactory factory = mock(RecordImplFactory.class);
        when(factory.createNestedRecord(STRUCTURE, PARENT)).thenReturn(firstRecord).thenReturn(secondRecord);

        final DynamicRecordListImpl list = new DynamicRecordListImpl(STRUCTURE, PARENT, factory);
        assertThat(list.addItem()).isEqualTo(firstRecord);
        assertThat(list.addItem()).isEqualTo(secondRecord);

        assertThat(list.getAll()).containsExactly(firstRecord, secondRecord);
    }

    /**
     * Test the fetching of an individual item.
     */
    @Test
    public void testGet() {
        final Record record = mock(Record.class);
        final RecordImplFactory factory = mock(RecordImplFactory.class);
        when(factory.createNestedRecord(STRUCTURE, PARENT)).thenReturn(record);

        final DynamicRecordListImpl list = new DynamicRecordListImpl(STRUCTURE, PARENT, factory);
        assertThat(list.addItem()).isEqualTo(record);
        assertThat(list.get(0)).isEqualTo(record);
    }

    /**
     * Test that the list correctly reflects its size.
     */
    @Test
    public void testGetSize() {
        final Record record = mock(Record.class);
        final RecordImplFactory factory = mock(RecordImplFactory.class);
        when(factory.createNestedRecord(STRUCTURE, PARENT)).thenReturn(record);

        final DynamicRecordListImpl list = new DynamicRecordListImpl(STRUCTURE, PARENT, factory);
        assertThat(list.addItem()).isEqualTo(record);
        assertThat(list.getSize()).isEqualTo(1);
    }

    /**
     * Test that the structure backing a dynamic record list can be retrieved.
     */
    @Test
    public void testGetStructure() {
        final Structure structure = StructureBuilder.getBuilder().addDQ8("dq8_field").build();
        final DynamicRecordList list = new DynamicRecordListImpl(structure, PARENT);
        assertThat(list.getStructure()).isEqualTo(structure);
    }

    /**
     * Test that removing an item by record succeeds.
     */
    @Test
    public void testRemoveItemRecord() {
        final Record record = mock(Record.class);
        final RecordImplFactory factory = mock(RecordImplFactory.class);
        when(factory.createNestedRecord(STRUCTURE, PARENT)).thenReturn(record);

        final DynamicRecordListImpl list = new DynamicRecordListImpl(STRUCTURE, PARENT, factory);
        assertThat(list.addItem()).isEqualTo(record);
        list.removeItem(record);
        assertThat(list.getSize()).isZero();
    }

    /**
     * Test that removing an item by index succeeds.
     */
    @Test
    public void testRemoveItemInt() {
        final Record record = mock(Record.class);
        final RecordImplFactory factory = mock(RecordImplFactory.class);
        when(factory.createNestedRecord(STRUCTURE, PARENT)).thenReturn(record);

        final DynamicRecordListImpl list = new DynamicRecordListImpl(STRUCTURE, PARENT, factory);
        assertThat(list.addItem()).isEqualTo(record);
        list.removeItem(0);
        assertThat(list.getSize()).isZero();
    }

    /**
     * Test the retrieval of an iterator.
     */
    @Test
    public void testIterator() {
        final Record record = mock(Record.class);
        final RecordImplFactory factory = mock(RecordImplFactory.class);
        when(factory.createNestedRecord(STRUCTURE, PARENT)).thenReturn(record);

        final DynamicRecordListImpl list = new DynamicRecordListImpl(STRUCTURE, PARENT, factory);
        assertThat(list.addItem()).isEqualTo(record);

        final Iterator<Record> iterator = list.iterator();
        assertThat(iterator.hasNext()).isTrue();
        assertThat(iterator.next()).isSameAs(record);
    }

}
