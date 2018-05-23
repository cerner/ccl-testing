package com.cerner.ccl.j4ccl.impl.adders;

import java.io.File;
import java.util.Locale;

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
    private final File sourceCode;
    private final CommandQueue queue;

    /**
     * Create an adder for a script compilation event.
     *
     * @param sourceCode
     *            The location of the source code.
     * @param queue
     *            A command queue to which the adder will contribute.
     */
    public ScriptCompilerAdderImpl(final File sourceCode, final CommandQueue queue) {
        if (sourceCode == null)
            throw new NullPointerException("Source code cannot be null.");

        if (queue == null)
            throw new NullPointerException("Command queue cannot be null.");

        if (!sourceCode.getName().toUpperCase(Locale.getDefault()).endsWith(".PRG"))
            throw new IllegalArgumentException("Source code file must be a .PRG file.");

        this.sourceCode = sourceCode;
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
                new ScriptCompilerCommand(sourceCode, getDependencies(), getListingLocation(), doDebugCompile()));
    }
}
