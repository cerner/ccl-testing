package com.cerner.ccl.j4ccl.ssh;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit Tests for TerminalResponse
 *
 * @author Fred Eckertson
 *
 */
public class TerminalResponseTest {

    /**
     * Confirm that the getter methods return the values set in the constructor.
     */
    @Test
    public void testGettersMatchConstructor() {
        TerminalResponse terminalResponse = new TerminalResponse(-3, "test response");
        assertThat(terminalResponse.getExitStatus()).isEqualTo(-3);
        assertThat(terminalResponse.getOutput()).isEqualTo("test response");
    }
}
