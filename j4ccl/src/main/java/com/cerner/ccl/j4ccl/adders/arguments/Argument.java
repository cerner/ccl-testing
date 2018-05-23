package com.cerner.ccl.j4ccl.adders.arguments;

/**
 * A definition of a command-line argument to be passed into the execution of a CCL script.
 *
 * @author Joshua Hyde
 *
 */

public interface Argument {
    /**
     * Get this argument as a command-line value, such that it can be used in an invocation of a CCL script.
     *
     * @return A form of the value that can be used as a command-line argument. For example, if this were to represent a
     *         varchar argument with a value of "abc123", it should return ""abc123"" (with the quotes, as this is a
     *         string argument.
     */
    String getCommandLineValue();
}
