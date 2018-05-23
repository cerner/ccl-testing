package com.cerner.ccl.parser.data;

/**
 * Definition of anything that is defined, in part or whole, by a data type.
 * 
 * @author Joshua Hyde
 * 
 */

public interface DataTyped {
    /**
     * Get the data type of the argument.
     * 
     * @return A {@link DataType} enum representing the returned data type; if {@code null}, it indicates that the data
     *         type could not be determined.
     */
    DataType getDataType();
}
