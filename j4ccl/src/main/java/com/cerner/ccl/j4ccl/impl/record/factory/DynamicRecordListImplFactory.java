package com.cerner.ccl.j4ccl.impl.record.factory;

import com.cerner.ccl.j4ccl.impl.record.DynamicRecordListImpl;
import com.cerner.ccl.j4ccl.record.DynamicRecordList;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * A factory to create {@link DynamicRecordList} objects.
 *
 * @author Joshua Hyde
 *
 */

public class DynamicRecordListImplFactory {
    /**
     * Create a dynamic record list object.
     *
     * @param structure
     *            A {@link Structure} object that represents the skeleton that backs an element in the list.
     * @param parent
     *            A {@link Record} object representing the record structure element that contains the created list.
     * @return A {@link DynamicRecordList} object.
     */
    public DynamicRecordList create(final Structure structure, final Record parent) {
        return new DynamicRecordListImpl(structure, parent);
    }
}
