package com.cerner.ccl.j4ccl.impl.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.cerner.ccl.j4ccl.adders.arguments.Argument;
import com.cerner.ccl.j4ccl.exception.CclCommandException;
import com.cerner.ccl.j4ccl.impl.commands.util.RecordDataExtractor;
import com.cerner.ccl.j4ccl.impl.commands.util.ScriptExecutionBuilder;
import com.cerner.ccl.j4ccl.impl.util.RecordSetter;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.ssh.CclCommandTerminal;
import com.cerner.ccl.j4ccl.ssh.JSchSshTerminal;
import com.cerner.ccl.j4ccl.ssh.exception.SshException;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A command to execute a script in a CCL terminal.
 *
 * @author Joshua Hyde
 *
 */

public class ScriptExecutionCommand extends AbstractCclCommand {
    private final Map<String, Record> records = new HashMap<String, Record>();
    private final ScriptExecutionBuilder builder;
    private final boolean authenticate;

    /**
     * Create a script execution command.
     *
     * @param scriptName
     *            The name of the script to be executed.
     * @param arguments
     *            A {@link List} of {@link Argument} objects representing the arguments (if any) to be used in the
     *            script execution.
     * @param authenticate
     *            Boolean flag indicating whether to authenticate with CCL when executing this script.
     * @throws NullPointerException
     *             If any of the given arguments is {@code null}.
     */
    public ScriptExecutionCommand(final String scriptName, final List<Argument> arguments, final boolean authenticate) {
        this(ScriptExecutionBuilder.getBuilder(scriptName).withArguments(arguments), authenticate);
    }

    /**
     * Create a script execution command.
     *
     * @param executionBuilder
     *            The {@link ScriptExecutionBuilder} used to construct the internals of this command.
     * @param authenticate
     *            Boolean flag indicating whether to authenticate with CCL when executing this script.
     * @throws NullPointerException
     *             If the given builder or arguments are {@code null}.
     */
    public ScriptExecutionCommand(final ScriptExecutionBuilder executionBuilder, final boolean authenticate) {
        if (executionBuilder == null)
            throw new NullPointerException("Builder cannot be null.");

        this.builder = executionBuilder;
        this.authenticate = authenticate;
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
        builder.addWithReplace(recordName, record);
    }

    @Override
    public void run(final CclCommandTerminal terminal) {
        final EtmPoint point = PointFactory.getPoint(getClass(), "run");
        try {
            final Collection<String> declarations = getRecordStructureDeclarations();
            final Collection<String> setters = getRecordStructureSetterCommands();
            final Collection<String> executionCommands = builder.build();
            final Collection<RecordDataExtractor> extractors = getDataExtractors();

            /*
             * Create a command queue that is the sum of all objects to be executed
             */
            final List<String> commandQueue = new ArrayList<String>(executionCommands.size() + 3 * extractors.size());
            commandQueue.addAll(declarations);
            commandQueue.addAll(setters);
            commandQueue.addAll(executionCommands);
            for (final RecordDataExtractor extractor : extractors)
                commandQueue.addAll(extractor.getExtractionCommands());

            try {
                terminal.executeCommands(new JSchSshTerminal(), commandQueue, authenticate);
            } catch (final SshException e) {
                throw new CclCommandException("Execution of script " + builder.getScriptName() + " failed.", e);
            }

            for (final RecordDataExtractor extractor : extractors)
                try {
                    extractor.extractRecordData();
                } catch (final IOException e) {
                    throw new CclCommandException(String.format("Failed to extract record data for record %s.",
                            extractor.getRecord().getName()), e);
                }
        } finally {
            point.collect();
        }
    }

    /**
     * Create a set of objects to extract record structure information into record structure data objects.
     *
     * @return A {@link Collection} of {@link RecordDataExtractor} objects.
     */
    private Collection<RecordDataExtractor> getDataExtractors() {
        final Collection<Record> records = getRecordStructures().values();
        final List<RecordDataExtractor> extractors = new ArrayList<RecordDataExtractor>(records.size());

        for (final Record record : records)
            extractors.add(new RecordDataExtractor(record));

        return extractors;
    }

    /**
     * Create a set of commands that represent the declarations of all record structures referenced by this command.
     *
     * @return A {@link Collection} of {@code String} objects representing the CCL commands that declare a record
     *         structure.
     */
    private Collection<String> getRecordStructureDeclarations() {
        final List<String> declarations = new ArrayList<String>();

        final Set<Entry<String, Record>> entries = records.entrySet();
        for (final Entry<String, Record> entry : entries) {
            final Record record = entry.getValue();
            declarations.add(String.format("free record %s go", record.getName()));
            declarations.add(record.getDeclaration());
            declarations.add("go");
        }

        if (declarations.isEmpty())
            return Collections.<String> emptyList();

        return declarations;
    }

    /**
     * Create a set of CCL commands that are assignment statements for the record structures associated with this
     * execution command.
     *
     * @return A {@link Collection} of {@code String} objects representing CCL commands to assign values to a record
     *         structure.
     */
    private Collection<String> getRecordStructureSetterCommands() {
        final List<String> setters = new ArrayList<String>();
        for (final Record record : getRecordStructures().values())
            setters.addAll(RecordSetter.getSetterCommands(record));
        return setters;
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
