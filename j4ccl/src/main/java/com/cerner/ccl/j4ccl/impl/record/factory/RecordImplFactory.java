package com.cerner.ccl.j4ccl.impl.record.factory;

import com.cerner.ccl.j4ccl.impl.record.RecordImpl;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.Structure;

/**
 * A factory object to create {@link Record} objects.
 *
 * @author Joshua Hyde
 *
 */

public class RecordImplFactory {
    /**
     * Create an instance of a record structure that is nested within a parent record structure.
     *
     * @param structure
     *            A {@link Structure} that backs the record structure instance.
     * @param parent
     *            A {@link RecordImpl} that represents the parent of this record.
     * @return A {@link Record} object.
     */
    public Record createNestedRecord(final Structure structure, final Record parent) {
        return new RecordImpl(null, structure, parent);
    }

    /**
     * Create an instance of an element within a record structure that is at the root of a record structure.
     *
     * @param name
     *            The name of the record structure instance.
     * @param structure
     *            A {@link Structure} object representing the structure that backs this record structure instance.
     * @return A {@link Record} object.
     */
    public Record createRootRecord(final String name, final Structure structure) {
        return new RecordImpl(name, structure, null);
    }
}
