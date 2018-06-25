package com.cerner.ccl.j4ccl.impl.commands;

import com.cerner.ccl.j4ccl.exception.CclCommandException;
import com.cerner.ccl.j4ccl.ssh.CclCommandTerminal;

/**
 * Definition of a command to be run in a CCL session. <br>
 * Every implementation of this class should be wholly atomic - i.e., there should be <b>no</b> functional dependencies
 * on other commands. Each command is run within its own CCL session to help enforce this principle of atomicity.
 *
 * @author Joshua Hyde
 *
 */

public abstract class AbstractCclCommand {
    /**
     * Create a command.
     */
    public AbstractCclCommand() {
    }

    /**
     * Run the command. <br>
     * This assumes that the given {@link CclCommandTerminal} object is connected and has an open CCL session.
     *
     * @param terminal
     *            The {@link CclCommandTerminal} object through which CCL commands will be issued.
     * @throws CclCommandException
     *             If executing commands through the given terminal fails.
     */
    public abstract void run(CclCommandTerminal terminal);
}
