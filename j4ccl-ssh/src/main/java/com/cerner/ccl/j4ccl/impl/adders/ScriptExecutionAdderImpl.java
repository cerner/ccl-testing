package com.cerner.ccl.j4ccl.impl.adders;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.cerner.ccl.j4ccl.adders.ScriptExecutionAdder;
import com.cerner.ccl.j4ccl.adders.arguments.Argument;
import com.cerner.ccl.j4ccl.impl.CommandQueue;
import com.cerner.ccl.j4ccl.impl.commands.ScriptExecutionCommand;
import com.cerner.ccl.j4ccl.record.Record;

/**
 * Implementation of {@link ScriptExecutionAdder}.
 *
 * @author Joshua Hyde
 *
 */

public class ScriptExecutionAdderImpl implements ScriptExecutionAdder {
    private final Map<String, Record> records = new HashMap<String, Record>();
    private final String scriptName;
    private final CommandQueue queue;
    private List<Argument> arguments = Collections.<Argument> emptyList();
    private boolean authenticate;

    /**
     * Create an adder that contributes a command to execute a CCL script.
     *
     * @param scriptName
     *            The name of the script to be executed.
     * @param queue
     *            A {@link CommandQueue} to which the command will be contributed.
     * @throws IllegalArgumentException
     *             If the given script name is blank.
     * @throws NullPointerException
     *             If any of the given arguments are {@code null}.
     */
    public ScriptExecutionAdderImpl(final String scriptName, final CommandQueue queue) {
        if (scriptName == null)
            throw new NullPointerException("Script name cannot be null.");

        if (queue == null)
            throw new NullPointerException("Command queue cannot be null.");

        if (scriptName.trim().length() == 0)
            throw new IllegalArgumentException("Script name cannot be blank.");

        this.queue = queue;
        this.scriptName = scriptName;

    }

    /**
     * {@inheritDoc}
     */
    public void commit() {
        final ScriptExecutionCommand command = new ScriptExecutionCommand(scriptName, arguments, authenticate);

        if (!records.isEmpty())
            for (final Entry<String, Record> entry : records.entrySet())
                command.addWithReplace(entry.getKey(), entry.getValue());

        queue.addInCclSessionCommand(command);
    }

    /**
     * {@inheritDoc}
     */
    public ScriptExecutionAdder withReplace(final String recordName, final Record record) {
        if (recordName == null)
            throw new NullPointerException("Record name cannot be null.");

        if (record == null)
            throw new NullPointerException("Record cannot be null.");

        if (recordName.trim().length() == 0)
            throw new IllegalArgumentException("Record name cannot be blank.");

        final String upperName = recordName.toUpperCase(Locale.getDefault());
        records.put(upperName, record);

        return this;
    }

    /**
     * {@inheritDoc}
     */
    public ScriptExecutionAdder withArguments(final Argument... arguments) {
        if (arguments == null)
            throw new IllegalArgumentException("Arguments cannot be null.");

        if (arguments.length == 0)
            throw new IllegalArgumentException("At least one argument must be supplied.");

        // Verify that nothing is null
        for (final Argument argument : arguments)
            if (argument == null)
                throw new IllegalArgumentException("Null arguments are not allowed: " + Arrays.toString(arguments));

        this.arguments = Arrays.asList(arguments);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public ScriptExecutionAdder withAuthentication(final boolean authenticate) {
        this.authenticate = authenticate;
        return this;
    }
}
