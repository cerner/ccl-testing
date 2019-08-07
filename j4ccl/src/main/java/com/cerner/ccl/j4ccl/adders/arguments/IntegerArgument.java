package com.cerner.ccl.j4ccl.adders.arguments;

/**
 * An {@link Argument} representing an integer argument.
 *
 * @author Joshua Hyde
 *
 */

public class IntegerArgument implements Argument {
    private final int value;

    /**
     * Create an argument.
     *
     * @param value
     *            The value of the integer argument.
     */
    public IntegerArgument(final int value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandLineValue() {
        return Integer.toString(value);
    }
}
