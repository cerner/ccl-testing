package com.cerner.ccl.j4ccl.record;

import java.util.List;

/**
 * Represents a fixed-length list within a parent record structure.
 *
 * @author Mark Cummings
 * @author Joshua Hyde
 */
public interface RecordList extends Iterable<Record> {

    /**
     * Get the size of this list.
     *
     * @return The size of this list.
     */
    int getSize();

    /**
     * Returns the elements of this list as an unmodifiable {@link List}
     *
     * @return an unmodifiable {@link List} containing the elements of this list field.
     */
    List<Record> getAll();

    /**
     * Returns the record at the specified index within this list.
     *
     * @param index
     *            the index
     * @return the record at the specified index
     */
    Record get(int index);

    /**
     * Get the structure backing the list.
     *
     * @return A {@link Structure} object representing the structure that backs this list.
     */
    Structure getStructure();
}
