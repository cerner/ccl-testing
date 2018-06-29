package com.cerner.ccl.cdoc.mojo.data;

/**
 * Enumerations of the possible CCL object types.
 * 
 * @author Joshua Hyde
 * 
 */

public enum ObjectType {
    /**
     * The object is a CCL INC include file.
     */
    INC,
    /**
     * The object is a PRG CCL script.
     */
    PRG,
    /**
     * The object is a CCL SUB include file.
     */
    SUB;
}
