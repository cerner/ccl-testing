package com.cerner.ccl.j4ccl.adders.arguments;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit tests for {@link IntegerArgument}.
 *
 * @author Joshua Hyde
 *
 */

public class IntegerArgumentTest {
    /**
     * Test the transformation of an integer argument into a usable command-line argument.
     */
    @Test
    public void testGetCommandLineValue() {
        assertThat(new IntegerArgument(1).getCommandLineValue()).isEqualTo("1");
    }
}
