package com.cerner.ccl.j4ccl.record.factory;

import com.cerner.ccl.j4ccl.impl.record.factory.RecordImplFactory;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.Structure;
import com.cerner.ccl.j4ccl.record.StructureBuilder;

/**
 * A factory object to create {@link Record} objects out of a structure.
 * <br>
 * For more information on the relationships of records and structures, see {@link StructureBuilder}.
 *
 * @author Mark Cummings
 *
 */

public final class RecordFactory {
    private static final RecordImplFactory RECORD_FACTORY = new RecordImplFactory();

    /**
     * Create a record object out of a given structure.
     *
     * @param name
     *            The name of the record structure to be created.
     * @param structure
     *            A {@link Structure} that backs the layout of the created record.
     * @return A {@link Record} that represents an instance of the given structure.
     * @throws NullPointerException
     *             If the given name or structure is {@code null}.
     */
    public static Record create(final String name, final Structure structure) {
        if (name == null)
            throw new NullPointerException("Name must not be null");

        if (structure == null)
            throw new NullPointerException("Structure must not be null");

        return RECORD_FACTORY.createRootRecord(name, structure);
    }
}
