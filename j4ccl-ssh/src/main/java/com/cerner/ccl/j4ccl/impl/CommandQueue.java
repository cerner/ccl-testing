package com.cerner.ccl.j4ccl.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.cerner.ccl.j4ccl.impl.commands.AbstractCclCommand;
import com.cerner.ccl.j4ccl.ssh.CclCommandTerminal;

/**
 * An object that can be used to store contributions of commands to be executed during and outside of a CCL session.
 *
 * @author Joshua Hyde
 *
 */

public class CommandQueue {
    private final List<AbstractCclCommand> inSessionCommands = new ArrayList<AbstractCclCommand>();
    private final List<AbstractCclCommand> onCclCloseCommands = new ArrayList<AbstractCclCommand>();
    private final List<AbstractCclCommand> onCclStartCommands = new ArrayList<AbstractCclCommand>();

    /**
     * Add a command to be run during an open CCL session.
     *
     * @param command
     *            A {@link AbstractCclCommand} object.
     * @return This object.
     */
    public CommandQueue addInCclSessionCommand(final AbstractCclCommand command) {
        inSessionCommands.add(command);
        return this;
    }

    /**
     * Add a command to be executed immediately prior to the closing of the CCL session.
     *
     * @param command
     *            A {@link AbstractCclCommand} object.
     * @return This object.
     */
    public CommandQueue addOnCclCloseCommand(final AbstractCclCommand command) {
        onCclCloseCommands.add(command);
        return this;
    }

    /**
     * Add a command to be executed immediately after the opening of the CCL session.
     *
     * @param command
     *            A {@link AbstractCclCommand} object.
     * @return This object.
     */
    public CommandQueue addOnCclStartCommand(final AbstractCclCommand command) {
        onCclStartCommands.add(command);
        return this;
    }

    /**
     * Execute a set of commands.
     *
     * @param cclTerminal
     *            A {@link CclCommandTerminal} object representing the terminal in which the commands are to be
     *            executed.
     */
    public void execute(final CclCommandTerminal cclTerminal) {
        if (isEmpty())
            return;

        try {
            for (final AbstractCclCommand command : getOnCclStartCommands())
                command.run(cclTerminal);

            for (final AbstractCclCommand command : getInCclSessionCommands())
                command.run(cclTerminal);
        } finally {
            /*
             * Run end-of-session commands, such as cleanup commands, even if the other commands fail
             */
            for (final AbstractCclCommand command : getOnCclCloseCommands())
                command.run(cclTerminal);
        }
    }

    /**
     * Get all commands that are to be executed within an open CCL session.
     *
     * @return An immutable {@link Collection} of objects that extend {@link AbstractCclCommand}.
     */
    public Collection<? extends AbstractCclCommand> getInCclSessionCommands() {
        return Collections.unmodifiableCollection(inSessionCommands);
    }

    /**
     * Get all commands that are to be executed immediately prior to the closing of a CCL session.
     *
     * @return An immutable {@link Collection} of objects that extend {@link AbstractCclCommand}.
     */
    public Collection<? extends AbstractCclCommand> getOnCclCloseCommands() {
        return Collections.unmodifiableCollection(onCclCloseCommands);
    }

    /**
     * Get all commands that are to be executed immediately prior to the opening of a CCL session.
     *
     * @return An immutable {@link Collection} of objects that extend {@link AbstractCclCommand}.
     */
    public Collection<? extends AbstractCclCommand> getOnCclStartCommands() {
        return Collections.unmodifiableCollection(onCclStartCommands);
    }

    /**
     * Determine whether or not this queue contains any commands to be executed.
     *
     * @return {@code true} if this queue contains no commands to be, {@code
     *         false} if there are commands to be run.
     */
    public boolean isEmpty() {
        return inSessionCommands.isEmpty() && onCclCloseCommands.isEmpty() && onCclStartCommands.isEmpty();
    }
}
