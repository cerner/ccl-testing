package com.cerner.ccl.j4ccl.impl.commands.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import com.cerner.ccl.j4ccl.adders.arguments.Argument;
import com.cerner.ccl.j4ccl.record.Record;

/**
 * A class to construct a step to execute a script.
 *
 * @author Joshua Hyde
 *
 */

public class ScriptExecutionBuilder {
    /**
     * Get a builder that can be used to execute a CCL script.
     *
     * @param scriptName
     *            The name of the script to be executed.
     * @return A {@link ScriptExecutionBuilder} object that can be used to execute a CCL script on a server.
     * @throws IllegalArgumentException
     *             If the given script name is blank.
     * @throws NullPointerException
     *             If the given script name is {@code null}.
     */
    public static ScriptExecutionBuilder getBuilder(final String scriptName) {
        if (scriptName == null)
            throw new NullPointerException("Script name cannot be null.");

        if (scriptName.trim().length() == 0)
            throw new IllegalArgumentException("Script name cannot be blank.");

        return new ScriptExecutionBuilder(scriptName);
    }

    private final Map<String, Record> records = new HashMap<String, Record>();
    private final String scriptName;
    private List<Argument> arguments = Collections.<Argument> emptyList();

    /**
     * A private constructor to prevent direct instantiation of this object.
     *
     * @param scriptName
     *            The name of the script to be executed.
     */
    private ScriptExecutionBuilder(final String scriptName) {
        this.scriptName = scriptName;
    }

    /**
     * Add a record structure to be referenced by the script.
     *
     * @param recordName
     *            The name of the record structure.
     * @param record
     *            A {@link Record} object representing the record structure to be used.
     * @throws IllegalArgumentException
     *             If the given record structure name is blank.
     * @throws NullPointerException
     *             If the given record name is null or the record structure reference is null.
     */
    public void addWithReplace(final String recordName, final Record record) {
        if (recordName == null)
            throw new NullPointerException("Record name cannot be null.");

        if (record == null)
            throw new NullPointerException("Record cannot be null.");

        if (recordName.trim().length() == 0)
            throw new IllegalArgumentException("Record name cannot be blank.");

        final String upperName = recordName.toUpperCase(Locale.getDefault());
        records.put(upperName, record);
    }

    /**
     * Construct a set of commands that can be used to execute a given script.
     *
     * @return A {@link Collection} of {@code String} objects representing the commands needed to execute the script
     *         with the appropriate record structure references.
     */
    public Collection<String> build() {
        final List<String> commands = new ArrayList<String>();
        commands.add(String.format("execute %s", getScriptName()));

        if (!arguments.isEmpty()) {
            final Iterator<Argument> argumentIterator = arguments.iterator();
            commands.add(argumentIterator.next().getCommandLineValue());
            while (argumentIterator.hasNext())
                commands.add("," + argumentIterator.next().getCommandLineValue());
        }

        if (!records.isEmpty()) {
            final Iterator<Entry<String, Record>> iterator = getRecordStructures().entrySet().iterator();
            Entry<String, Record> entry = iterator.next();
            commands.add(String.format("WITH REPLACE(\"%s\", \"%s\")", entry.getKey(),
                    entry.getValue().getName().toUpperCase(Locale.getDefault())));
            while (iterator.hasNext()) {
                entry = iterator.next();
                commands.add(String.format(", REPLACE(\"%s\", \"%s\")", entry.getKey(),
                        entry.getValue().getName().toUpperCase(Locale.getDefault())));
            }
        }

        commands.add("go;" + getScriptName());
        return commands;
    }

    /**
     * Get the name of the script to be executed.
     *
     * @return The name of the script to be executed.
     */
    public String getScriptName() {
        return scriptName;
    }

    /**
     * Create a builder that uses the given arguments.
     *
     * @param arguments
     *            A {@link List} of {@link Argument} objects representing the arguments to be used in the script
     *            execution.
     * @return This builder.
     * @throws NullPointerException
     *             If the given list of arguments is {@code null}.
     */
    public ScriptExecutionBuilder withArguments(final List<Argument> arguments) {
        if (arguments == null)
            throw new NullPointerException("Arguments cannot be null.");

        this.arguments = arguments.isEmpty() ? Collections.<Argument> emptyList()
                : Collections.unmodifiableList(arguments);
        return this;
    }

    /**
     * Get the record structures to be used with the executed script.
     *
     * @return A {@link Map}. The keys are the names of the record structures to be replaced and the values are
     *         {@link Record} objects representing the structures to replace the referenced record structure names.
     */
    Map<String, Record> getRecordStructures() {
        return records;
    }
}
