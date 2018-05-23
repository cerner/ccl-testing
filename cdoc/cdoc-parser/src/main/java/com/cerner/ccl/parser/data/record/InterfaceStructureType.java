package com.cerner.ccl.parser.data.record;

/**
 * Enumerations of the possible interface elements that a record structure can be when acting as the input or output of
 * a CCL script.
 * 
 * @author Joshua Hyde
 * 
 */

public enum InterfaceStructureType {
    /**
     * The record structure is a reply record structure.
     */
    REPLY,
    /**
     * The record structure is a request record structure.
     */
    REQUEST;
}
