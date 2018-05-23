package com.cerner.ccl.parser.data;

/**
 * Definition of an object that represents a fixed-length data typed object, such as a fixed-length character field.
 * 
 * @author Joshua Hyde
 * 
 */

public interface FixedLengthDataTyped extends DataTyped {
    /**
     * Get the size of the data type.
     * 
     * @return The size of the data type.
     */
    int getDataLength();
}
