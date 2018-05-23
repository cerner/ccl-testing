package com.cerner.ccl.j4ccl.ssh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates a list of commands together with regex patterns for the set of potential responses to any of the
 * commands. The intention is for the system to send the commands to a terminal in order, one command at a time, waiting
 * after each command is sent to receive one of the expected responses before sending the next command. In practice the
 * expectations will be checked in order so it is best to list the expectations with the most likely ones and the ones
 * easiest to check listed first.
 *
 * @author Fred Eckertson
 *
 */
public class CommandExpectationGroup {
    private final List<String> commands = new ArrayList<String>();
    private final List<String> expectations = new ArrayList<String>();

    private final boolean maskCommands;
    private List<String> maskedCommands;

    /**
     * Constructs a CommandExpectationGroup without masked commands.
     */
    public CommandExpectationGroup() {
        this(false);
    }

    /**
     * Constructs a CommandExpectationGroup with commands masked as specified.
     * 
     * @param maskCommands
     *            A boolean value indicating whether to mask the commands.
     */
    protected CommandExpectationGroup(final boolean maskCommands) {
        this.maskCommands = maskCommands;
    }

    /**
     * Adds a single command to the end of the current list of commands.
     *
     * @param command
     *            The command to add.
     */
    public void addCommand(final String command) {
        commands.add(command);
    }

    /**
     * Appends a list of commands to the end of the current list of commands.
     *
     * @param commands
     *            The list of command to add.
     */
    public void addCommands(final List<String> commands) {
        this.commands.addAll(commands);
    }

    /**
     * Adds a single expectation to the end of the current list of expectations.
     *
     * @param expectation
     *            The expectation to add.
     */
    public void addExpectation(final String expectation) {
        expectations.add(expectation);
    }

    /**
     * Appends a list of expectations to the end of the current list of expectations.
     *
     * @param expectations
     *            The list of expectations to add.
     */
    public void addExpectations(final List<String> expectations) {
        this.expectations.addAll(expectations);
    }

    /**
     * Retrieves the current list of commands.
     *
     * @return The current list of commands.
     */
    public List<String> getCommands() {
        return Collections.unmodifiableList(commands);
    }

    /**
     * Retrieves the current list of expectations.
     *
     * @return The current list of expectations.
     */
    public List<String> getExpectations() {
        return Collections.unmodifiableList(expectations);
    }

    /**
     * Retrieves the mask commands indicator.
     *
     * @return The mask commands indicator.
     */
    public boolean maskCommands() {
        return maskCommands;
    }

    /**
     * Overrides the toString method. Note that commands added after toString has been invoked will not be represented
     * in future invocations.
     */
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();

        if (maskCommands) {
            if (maskedCommands == null) {
                maskedCommands = new ArrayList<String>(commands.size());
                for (int idx = 0; idx < commands.size(); idx++) {
                    maskedCommands.add("********");
                }
            }
            sb.append(maskedCommands).append(expectations);
        } else {
            sb.append(commands).append(expectations);
        }

        return sb.toString();
    }
}
