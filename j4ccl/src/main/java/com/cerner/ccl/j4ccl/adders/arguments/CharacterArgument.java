package com.cerner.ccl.j4ccl.adders.arguments;

/**
 * An argument representing a string argument passed into a CCL script.
 *
 * @author Joshua Hyde
 *
 */

public class CharacterArgument implements Argument {
    private final String value;

    /**
     * Create a character argument.
     *
     * @param value
     *            The value of the character argument.
     * @throws IllegalArgumentException
     *             If the given value is {@code null}.
     */
    public CharacterArgument(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null.");
        }

        final boolean hasSingleQuote = value.contains("'");
        if (hasSingleQuote && value.contains("\"")) {
            throw new IllegalArgumentException(
                    "This does not support strings containing both single quote and double quotation marks.");
        }

        this.value = hasSingleQuote ? "\"" + value + "\"" : "'" + value + "'";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCommandLineValue() {
        return value;
    }
}
