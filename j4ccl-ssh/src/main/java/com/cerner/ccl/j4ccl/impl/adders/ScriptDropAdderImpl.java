package com.cerner.ccl.j4ccl.impl.adders;

import org.apache.commons.lang.StringUtils;

import com.cerner.ccl.j4ccl.adders.ScriptDropAdder;
import com.cerner.ccl.j4ccl.impl.CommandQueue;
import com.cerner.ccl.j4ccl.impl.commands.DropScriptCommand;

/**
 * Implementation of {@link ScriptDropAdder}.
 *
 * @author Joshua Hyde
 *
 */
public class ScriptDropAdderImpl implements ScriptDropAdder {
    private final String scriptName;
    private final CommandQueue queue;

    /**
     * Create an adder to contribute a command to drop a script.
     *
     * @param scriptName
     *            The name of the script to be dropped.
     * @param queue
     *            The {@link CommandQueue} into which the script-drop command is to be contributed.
     * @throws IllegalArgumentException
     *             If the given script name is blank.
     * @throws NullPointerException
     *             If the given command queue or script name are {@code null}.
     */
    public ScriptDropAdderImpl(final String scriptName, final CommandQueue queue) {
        if (scriptName == null)
            throw new NullPointerException("Script name cannot be null.");

        if (StringUtils.isBlank(scriptName))
            throw new IllegalArgumentException("Script name cannot be blank.");

        if (queue == null)
            throw new NullPointerException("Command queue cannot be null.");

        this.scriptName = scriptName;
        this.queue = queue;
    }

    /**
     * {@inheritDoc}
     */
    public void commit() {
        queue.addOnCclCloseCommand(new DropScriptCommand(scriptName));
    }

}
