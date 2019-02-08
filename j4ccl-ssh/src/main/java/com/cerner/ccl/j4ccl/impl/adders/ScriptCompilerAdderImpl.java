package com.cerner.ccl.j4ccl.impl.adders;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;

import com.cerner.ccl.j4ccl.adders.ScriptCompilerAdder;
import com.cerner.ccl.j4ccl.impl.CommandQueue;
import com.cerner.ccl.j4ccl.impl.commands.ScriptCompilerCommand;

/**
 * An implementation of {@link ScriptCompilerAdder}.
 *
 * @author Joshua Hyde
 *
 */

public class ScriptCompilerAdderImpl extends AbstractCompilerAdder
        implements ScriptCompilerAdder<ScriptCompilerAdderImpl> {
    private final File sourceCodeFile;
    private final CommandQueue queue;

    /**
     * Create an adder for a script compilation event.
     *
     * @param sourceCodeFile
     *            The location of the source code file.
     * @param queue
     *            A command queue to which the adder will contribute.
     */
    public ScriptCompilerAdderImpl(final File sourceCodeFile, final CommandQueue queue) {
        if (sourceCodeFile == null) {
            throw new NullPointerException("Source code cannot be null.");
        }

        if (queue == null) {
            throw new NullPointerException("Command queue cannot be null.");
        }

        if (!sourceCodeFile.getName().equals(sourceCodeFile.getName().toLowerCase(Locale.getDefault()))) {
            throw new IllegalArgumentException(
                    "Source code file name must be all lower case: " + sourceCodeFile.getName());
        }

        if (!sourceCodeFile.getName().endsWith(".prg")) {
            throw new IllegalArgumentException(
                    "Source code file must have a .prg extension: " + sourceCodeFile.getName());
        }

        try {
            String sourceCode = FileUtils.readFileToString(sourceCodeFile, Charset.forName("utf-8"));
            Pattern p = Pattern.compile(".*create\\s+program\\s+(\\w+).*",
                    Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
            Matcher m = p.matcher(sourceCode);
            if (m.matches()) {
                String programName = m.group(1);
                if (!programName.toLowerCase().equals(sourceCodeFile.getName().replaceAll("\\.prg", ""))) {
                    throw new IllegalArgumentException(
                            "Source code file name must match generated program name: " + sourceCodeFile.getName());
                }
            } else {
                throw new IllegalArgumentException("Source code must create a program: " + sourceCodeFile.getName());
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        this.sourceCodeFile = sourceCodeFile;
        this.queue = queue;
    }

    public ScriptCompilerAdderImpl withDebugModeEnabled(final boolean debugModeEnabled) {
        setDoDebugCompile(debugModeEnabled);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public ScriptCompilerAdderImpl withDependency(final File file) {
        addDependency(file);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public ScriptCompilerAdderImpl withListingOutput(final File file) {
        setListingLocation(file);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void commit() {
        queue.addInCclSessionCommand(
                new ScriptCompilerCommand(sourceCodeFile, getDependencies(), getListingLocation(), doDebugCompile()));
    }
}
