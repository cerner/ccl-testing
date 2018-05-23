package com.cerner.ccl.j4ccl.impl.record.factory;

import com.cerner.ccl.j4ccl.impl.record.RecordListImpl;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.RecordList;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * A factory to create {@link RecordList} objects.
 *
 * @author Joshua Hyde
 *
 */

public class RecordListImplFactory {
    /**
     * Create a record list.
     *
     * @param parent
     *            A {@link Record} object representing the record structure that encapsulates the created list.
     * @param structure
     *            A {@link Structure} object representing the structure that backs the created list.
     * @param listSize
     *            The size of the created list.
     * @return A {@link RecordList} object.
     */
    public RecordList create(final Record parent, final Structure structure, final int listSize) {
        return new RecordListImpl(parent, structure, listSize);
    }
}
