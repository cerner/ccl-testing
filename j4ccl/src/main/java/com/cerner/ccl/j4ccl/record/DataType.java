package com.cerner.ccl.j4ccl.record;

import java.util.EnumSet;
import java.util.Set;

/**
 * Supported CCL data types.
 * <br>
 * The following are lists (see {@link #isList()}):
 * <ul>
 * <li>{@link #DYNAMIC_LIST}</li>
 * <li>{@link #LIST}</li>
 * </ul>
 * <br>
 * The following are fixed-length primitive data types (see {@link #isFixedLengthPrimitive()}):
 * <ul>
 * <li>{@link #CHARACTER}</li>
 * <li>{@link #DQ8}</li>
 * <li>{@link #F8}</li>
 * <li>{@link #I2}</li>
 * <li>{@link #I4}</li>
 * </ul>
 *
 * @author Mark Cummings
 * @author Joshua Hyde
 */

public enum DataType {
    /**
     * A fixed-length character datatype
     */
    CHARACTER,
    /**
     * A primitive field representing a DQ8 value
     */
    DQ8,
    /**
     * A variable-length list
     */
    DYNAMIC_LIST,
    /**
     * A primitive field representing an F8 value
     */
    F8,
    /**
     * A primitive field representing an I2 value
     */
    I2,
    /**
     * A primitive field representing an I4 value
     */
    I4,
    /**
     * A fixed-length list
     */
    LIST,
    /**
     * A record structure
     */
    RECORD,
    /**
     * A primitive field representing a VC value
     */
    VC;

    private static final Set<DataType> LIST_TYPES = EnumSet.of(LIST, DYNAMIC_LIST);
    private static final Set<DataType> VARIABLE_PRIMITIVES = EnumSet.of(VC);

    /**
     * Determine whether or not this element represents a non-primitive datatype.
     *
     * @return {@code true} if this does not represent a primitive datatype.
     */
    public boolean isComplexType() {
        return isList() || this.equals(RECORD);
    }

    /**
     * Determine whether or not this datatype is a list.
     *
     * @return {@code true} if this represents a list.
     */
    public boolean isList() {
        return LIST_TYPES.contains(this);
    }

    /**
     * Determine whether or not this data type represents a fixed-length primitive.
     *
     * @return {@code true} if this represents a primitive data type that has a fixed length.
     */
    public boolean isFixedLengthPrimitive() {
        return !this.isComplexType() && !VARIABLE_PRIMITIVES.contains(this);
    }
}