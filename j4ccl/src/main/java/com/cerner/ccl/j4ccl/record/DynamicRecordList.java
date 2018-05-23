package com.cerner.ccl.j4ccl.record;

/**
 * Represents a variable-length list within a parent record structure.
 *
 * @author Mark Cummings
 */

public interface DynamicRecordList extends RecordList {

    /**
     * Adds a new item to this list.
     *
     * @return the new {@link Structure} item that was added.
     */
    Record addItem();

    /**
     * Removes the given item from this list. If the item is not currently in this list, nothing will happen.
     *
     * @param item
     *            the item to remove.
     * @throws IllegalArgumentException
     *             if item is {@code null}
     */
    void removeItem(Record item);

    /**
     * Removes the item at the given index.
     *
     * @param index
     *            the index of the item to remove.
     * @throws IndexOutOfBoundsException
     *             if index is less than zero or greater than the list size
     */
    void removeItem(int index);
}
