package com.cerner.ccl.j4ccl.impl.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.cerner.ccl.j4ccl.impl.record.factory.RecordImplFactory;
import com.cerner.ccl.j4ccl.record.DynamicRecordList;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * Implementation of {@link DynamicRecordList}
 *
 * @author Mark Cummings
 * @author Joshua Hyde
 */

public class DynamicRecordListImpl implements DynamicRecordList {
    private static final RecordImplFactory DEFAULT_RECORD_FACTORY = new RecordImplFactory();

    private final List<Record> records = new ArrayList<Record>();
    private final RecordImplFactory recordFactory;
    private final Structure structure;
    private final Record parent;

    /**
     * Create a dynamic record list.
     *
     * @param structure
     *            A {@link Structure} object representing the structure that backs the list.
     * @param parent
     *            A {@link Record} object that represents the record structure element in which this list is nested.
     */
    public DynamicRecordListImpl(final Structure structure, final Record parent) {
        this(structure, parent, DEFAULT_RECORD_FACTORY);
    }

    /**
     * Create a dynamic record list.
     *
     * @param structure
     *            A {@link Structure} object representing the structure that backs the list.
     * @param parent
     *            A {@link Record} object that represents the record structure element in which this list is nested.
     * @param recordFactory
     *            A {@link RecordImplFactory} object that can create {@link Record} objects.
     */
    DynamicRecordListImpl(final Structure structure, final Record parent, final RecordImplFactory recordFactory) {
        if (structure == null) {
            throw new NullPointerException("Structure cannot be null.");
        }

        if (parent == null) {
            throw new NullPointerException("Parent record cannot be null.");
        }

        this.structure = structure;
        this.parent = parent;
        this.recordFactory = recordFactory;
    }

    @Override
    public Record addItem() {
        final Record element = recordFactory.createNestedRecord(structure, parent);
        records.add(element);
        return element;
    }

    @Override
    public List<Record> getAll() {
        return Collections.unmodifiableList(records);
    }

    @Override
    public Record get(final int index) {
        return records.get(index);
    }

    @Override
    public int getSize() {
        return records.size();
    }

    @Override
    public Structure getStructure() {
        return structure;
    }

    @Override
    public void removeItem(final Record item) {
        records.remove(item);
    }

    @Override
    public void removeItem(final int index) {
        records.remove(index);
    }

    @Override
    public Iterator<Record> iterator() {
        return records.iterator();
    }
}
