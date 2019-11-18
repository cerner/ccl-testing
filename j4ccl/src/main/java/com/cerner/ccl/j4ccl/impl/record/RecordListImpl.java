package com.cerner.ccl.j4ccl.impl.record;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.cerner.ccl.j4ccl.impl.record.factory.RecordImplFactory;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.RecordList;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * Implementation of {@link RecordList}
 *
 * @author Mark Cummings
 * @author Joshua Hyde
 */

public class RecordListImpl implements RecordList {
    private static final RecordImplFactory DEFAULT_RECORD_FACTORY = new RecordImplFactory();
    private final Structure structure;
    private final List<Record> records;

    /**
     * Create a record list.
     *
     * @param parent
     *            A {@link Record} object representing the record structure element that contains this list.
     * @param structure
     *            A {@link Structure} object representing the structure that backs this list.
     * @param listSize
     *            The size of the list.
     * @throws IllegalArgumentException
     *             If the given list size is 0 or less.
     * @throws NullPointerException
     *             If the given structure or parent is {@code null}.
     */
    public RecordListImpl(final Record parent, final Structure structure, final int listSize) {
        this(parent, structure, listSize, DEFAULT_RECORD_FACTORY);
    }

    /**
     * Create a record list.
     *
     * @param parent
     *            A {@link Record} object representing the record structure element that contains this list.
     * @param structure
     *            A {@link Structure} object representing the structure that backs this list.
     * @param listSize
     *            The size of the list.
     * @param recordFactory
     *            A {@link RecordImplFactory} object used to create {@link Record} objects.
     */
    RecordListImpl(final Record parent, final Structure structure, final int listSize,
            final RecordImplFactory recordFactory) {
        if (structure == null) {
            throw new NullPointerException("Structure cannot be null.");
        }

        if (parent == null) {
            throw new NullPointerException("Parent record structure cannot be null.");
        }

        if (listSize < 1) {
            throw new IllegalArgumentException("List size must be greater than zero.");
        }

        this.records = new ArrayList<Record>(listSize);
        this.structure = structure;
        for (int i = 0; i < listSize; i++) {
            records.add(recordFactory.createNestedRecord(structure, parent));
        }
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
    public Iterator<Record> iterator() {
        return records.iterator();
    }
}
