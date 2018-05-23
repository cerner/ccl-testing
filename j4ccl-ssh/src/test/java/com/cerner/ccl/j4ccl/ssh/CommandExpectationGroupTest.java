package com.cerner.ccl.j4ccl.ssh;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;

import org.junit.Test;

/**
 * Unit Tests for the CommandExpectationGroup class.
 *
 * @author Fred Eckertson
 *
 */
public class CommandExpectationGroupTest {

    /**
     * Verifies that the commands are not masked by default
     */
    @Test
    public void testGettersMatchConstructor() {
        final CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        assertThat(commandExpectationGroup.maskCommands()).isFalse();
    }

    /**
     * Confirms that getCommands returns the commands added with addCommand and/or addCommands
     */
    @Test
    public void testGetCommands() {
        CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommand("solo command 1");
        commandExpectationGroup.addCommand("solo command 2");
        commandExpectationGroup.addCommands(
                Arrays.asList(new String[] { "grouped command 1", "grouped command 2", "grouped command 3" }));
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "grouped command 4" }));
        commandExpectationGroup.addCommand("solo command 3");
        commandExpectationGroup.addCommand("solo command 4");
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "grouped command 5", "grouped command 6" }));
        assertThat(commandExpectationGroup.getCommands()).isEqualTo(Arrays.asList(new String[] { "solo command 1",
                "solo command 2", "grouped command 1", "grouped command 2", "grouped command 3", "grouped command 4",
                "solo command 3", "solo command 4", "grouped command 5", "grouped command 6" }));

        commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "grouped command 1", "grouped command 2" }));
        commandExpectationGroup.addCommand("solo command 1");
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "grouped command 3", "grouped command 4" }));
        commandExpectationGroup.addCommand("solo command 2");
        assertThat(commandExpectationGroup.getCommands()).isEqualTo(Arrays.asList(new String[] { "grouped command 1",
                "grouped command 2", "solo command 1", "grouped command 3", "grouped command 4", "solo command 2" }));
    }

    /**
     * Confirms that getExpectations returns the expectations added with addExpectations and/or addExpectations
     */
    @Test
    public void testGetExpectations() {
        CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addExpectation("solo expectation 1");
        commandExpectationGroup.addExpectation("solo expectation 2");
        commandExpectationGroup.addExpectations(Arrays
                .asList(new String[] { "grouped expectation 1", "grouped expectation 2", "grouped expectation 3" }));
        commandExpectationGroup.addExpectations(Arrays.asList(new String[] { "grouped expectation 4" }));
        commandExpectationGroup.addExpectation("solo expectation 3");
        commandExpectationGroup.addExpectation("solo expectation 4");
        commandExpectationGroup
                .addExpectations(Arrays.asList(new String[] { "grouped expectation 5", "grouped command 6" }));
        assertThat(commandExpectationGroup.getExpectations()).isEqualTo(
                Arrays.asList(new String[] { "solo expectation 1", "solo expectation 2", "grouped expectation 1",
                        "grouped expectation 2", "grouped expectation 3", "grouped expectation 4", "solo expectation 3",
                        "solo expectation 4", "grouped expectation 5", "grouped command 6" }));

        commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup
                .addExpectations(Arrays.asList(new String[] { "grouped expectation 1", "grouped expectation 2" }));
        commandExpectationGroup.addExpectation("solo expectation 1");
        commandExpectationGroup
                .addExpectations(Arrays.asList(new String[] { "grouped expectation 3", "grouped expectation 4" }));
        commandExpectationGroup.addExpectation("solo expectation 2");
        assertThat(commandExpectationGroup.getExpectations()).isEqualTo(
                Arrays.asList(new String[] { "grouped expectation 1", "grouped expectation 2", "solo expectation 1",
                        "grouped expectation 3", "grouped expectation 4", "solo expectation 2" }));
    }

    /**
     * Validates the toString function shows all of the commands and expectations and does not mask any of the commands.
     */
    @Test
    public void testToString() {
        CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommand("solo command 1");
        commandExpectationGroup.addExpectation("solo expectation 1");
        commandExpectationGroup.addCommands(
                Arrays.asList(new String[] { "grouped command 1", "grouped command 2", "grouped command 3" }));
        commandExpectationGroup.addExpectations(Arrays.asList(new String[] { "grouped expectation 1" }));
        commandExpectationGroup.addExpectation("solo expectation 2");
        commandExpectationGroup.addCommand("solo command 2");
        commandExpectationGroup
                .addExpectations(Arrays.asList(new String[] { "grouped expectation 2", "grouped expectation 3" }));
        assertThat(commandExpectationGroup.toString())
                .isEqualTo(Arrays
                        .asList(new String[] { "solo command 1", "grouped command 1", "grouped command 2",
                                "grouped command 3", "solo command 2" })
                        .toString() + Arrays
                                .asList(new String[] { "solo expectation 1", "grouped expectation 1",
                                        "solo expectation 2", "grouped expectation 2", "grouped expectation 3" })
                                .toString());

        commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "grouped command 1", "grouped command 2" }));
        commandExpectationGroup.addExpectation("solo expectation 1");
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "grouped command 3", "grouped command 4" }));
        commandExpectationGroup
                .addExpectations(Arrays.asList(new String[] { "grouped expectation 1", "grouped expectation 2" }));
        commandExpectationGroup.addExpectation("solo expectation 2");
        commandExpectationGroup.addCommand("solo command 1");
        assertThat(commandExpectationGroup.toString()).isEqualTo(Arrays
                .asList(new String[] { "grouped command 1", "grouped command 2", "grouped command 3",
                        "grouped command 4", "solo command 1" })
                .toString()
                + Arrays.asList(new String[] { "solo expectation 1", "grouped expectation 1", "grouped expectation 2",
                        "solo expectation 2" }).toString());

        commandExpectationGroup
                .addExpectations(Arrays.asList(new String[] { "extra expectation 1g", "extra expectation 2g" }));
        commandExpectationGroup.addCommand("extra command 1");
        commandExpectationGroup.addExpectation("extra expectation 1");
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "extra command 1g", "extra command 2g" }));
        assertThat(commandExpectationGroup.toString()).isEqualTo(Arrays
                .asList(new String[] { "grouped command 1", "grouped command 2", "grouped command 3",
                        "grouped command 4", "solo command 1", "extra command 1", "extra command 1g",
                        "extra command 2g" })
                .toString()
                + Arrays.asList(new String[] { "solo expectation 1", "grouped expectation 1", "grouped expectation 2",
                        "solo expectation 2", "extra expectation 1g", "extra expectation 2g", "extra expectation 1" })
                        .toString());
    }
}
