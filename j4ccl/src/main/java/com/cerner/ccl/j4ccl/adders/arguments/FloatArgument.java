package com.cerner.ccl.j4ccl.adders.arguments;

/**
 * An argument representing a floating-point value.
 *
 * @author Joshua Hyde
 *
 */

public class FloatArgument implements Argument {
    private final String value;

    /**
     * Create a float argument.
     *
     * @param integer
     *            The number preceding the decimal point; for example, in the value "123.456", "123" is the integer. The
     *            sign of this value will drive the sign of the floating point value; e.g., if this value is "-123",
     *            then the produced floating point value will be negative, too.
     * @param fractional
     *            The number following the decimal point; for example, in the value "123.456", "456" is the fractional.
     */
    public FloatArgument(final long integer, final long fractional) {
        this.value = new StringBuilder().append(integer).append(".").append(fractional).toString();
    }

    /**
     * {@inheritDoc}
     */
    public String getCommandLineValue() {
        return value;
    }
}
