package com.cerner.ccl.parser.text.record;

/**
 * Definition of a fixed-length list.
 *
 * @author Joshua Hyde
 *
 */

public class FixedLengthStructureList extends AbstractParentStructure {
    private final int listSize;

    /**
     * Create a fixed-length list.
     *
     * @param name
     *            The name of the list.
     * @param level
     *            The {@link #getLevel() level} of this member.
     * @param listSize
     *            The length of the list.
     * @throws IllegalArgumentException
     *             If the given list size is negative.
     */
    public FixedLengthStructureList(final String name, final int level, final int listSize) {
        super(name, level);

        if (listSize < 0) {
            throw new IllegalArgumentException("List size cannot be negative: " + Integer.toBinaryString(listSize));
        }

        this.listSize = listSize;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof FixedLengthStructureList)) {
            return false;
        }

        return getListSize() == ((FixedLengthStructureList) obj).getListSize();
    }

    /**
     * Get the size of the list.
     *
     * @return The size of the list.
     */
    public int getListSize() {
        return listSize;
    }

    @Override
    public int hashCode() {
        return 31 * super.hashCode() + listSize;
    }
}
