package com.cerner.ccl.j4ccl.impl.adders;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.cerner.ccl.j4ccl.adders.DynamicCompilerAdder;
import com.cerner.ccl.j4ccl.exception.CclCommandException;
import com.cerner.ccl.j4ccl.impl.CommandQueue;
import com.cerner.ccl.j4ccl.impl.commands.DropScriptCommand;
import com.cerner.ccl.j4ccl.impl.commands.ScriptCompilerCommand;
import com.cerner.ccl.j4ccl.impl.util.ScriptRegistrar;

/**
 * An implementation of {@link DynamicCompilerAdder}.
 *
 * @author Joshua Hyde
 *
 */

public class DynamicCompilerAdderImpl extends AbstractCompilerAdder
        implements DynamicCompilerAdder<DynamicCompilerAdderImpl> {
    private static final int MAX_SCRIPT_NAME_LENGTH = 30;
    private static int objectCount = 0;
    private final File sourceCodeLocation;
    private final CommandQueue queue;
    private String scriptName;

    /**
     * Create an object that adds a dynamic include file compiler command to the given queue.
     *
     * @param sourceFile
     *            A {@link File} object representing the location of the source code to be compiled.
     * @param queue
     *            A {@link CommandQueue} object to which this will contribute a script compilation call.
     * @throws IllegalArgumentException
     *             If the given source code location does not end in a ".inc" or ".sub" file extension.
     */
    public DynamicCompilerAdderImpl(File sourceFile, final  CommandQueue queue )  
    {
        if (sourceFile == null)
            throw new NullPointerException("Source code cannot be null.");

        if (queue == null)
            throw new NullPointerException("Command queue cannot be null.");

        final String caselessName = sourceFile.getName().toUpperCase(Locale.getDefault());
        if (!caselessName.endsWith(".INC") && !caselessName.endsWith(".SUB"))
            throw new IllegalArgumentException("Source code file must be a .INC or .SUB file.");

        this.sourceCodeLocation = sourceFile;
        this.queue = queue;
    }

    public DynamicCompilerAdderImpl withDebugModeEnabled(final boolean debugModeEnabled) {
        setDoDebugCompile(debugModeEnabled);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DynamicCompilerAdderImpl withDependency(final File file) {
        addDependency(file);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DynamicCompilerAdderImpl withListingOutput(final File file) {
        setListingLocation(file);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public DynamicCompilerAdderImpl withScriptName(final String scriptName) {
        if (scriptName == null)
            throw new NullPointerException("Script name cannot be null.");

        if (scriptName.trim().length() == 0)
            throw new IllegalArgumentException("Script name cannot be blank.");

        if (scriptName.length() > MAX_SCRIPT_NAME_LENGTH)
            throw new IllegalArgumentException(String.format("Script name exceeds max length: %d > %d",
                    scriptName.length(), MAX_SCRIPT_NAME_LENGTH));

        this.scriptName = scriptName;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public void commit() {
        final String tempScriptName = getScriptName();
        final File temporaryFile = new File(System.getProperty("java.io.tmpdir"), tempScriptName + ".prg");
        try {
            FileUtils.writeLines(temporaryFile, "utf-8",
                    Arrays.asList("drop program " + tempScriptName + " go", "create program " + tempScriptName,
                            "%i cclsource:" + sourceCodeLocation.getName().toLowerCase(Locale.US), "end go"));
        } catch (final IOException e) {
            throw new CclCommandException("Failed to create source for wrapper script.", e);
        }

        ScriptRegistrar.registerDynamicScript(temporaryFile.getName());

        queue.addInCclSessionCommand(new ScriptCompilerCommand(temporaryFile, getCompositeDependencies(),
                getListingLocation(), doDebugCompile()));
        queue.addOnCclCloseCommand(new DropScriptCommand(tempScriptName));
    }

    /**
     * Create a random object name in which to wrap the *.inc or *.sub file. FIXME: this won't produce unique names if
     * the user name is long
     *
     * @return A random script name.
     */
    private synchronized String createRandomObjectName() {
        final String name = String.format("j4ccl_%s_%d%d",
                System.getProperty("user.name").toLowerCase(Locale.getDefault()).replace("$", ""), ++objectCount,
                System.currentTimeMillis() % 10000000);
        return name.substring(0, Math.min(name.length(), MAX_SCRIPT_NAME_LENGTH));
    }

    /**
     * Collect a list of dependencies that also includes the source INC file.
     *
     * @return A {@link Collection} of {@code File} objects representing the dependencies of the dynamic-generated
     *         script.
     */
    private Collection<File> getCompositeDependencies() {
        final Set<File> composite = new HashSet<File>(super.getDependencies());
        composite.add(sourceCodeLocation);
        return composite;
    }

    /**
     * Get the name of the script to be created. <br>
     * This is purposefully kept at package-level visibility so that it is only visible to tests.
     *
     * @return The name of the script to be created.
     */
    private String getScriptName() {
        return scriptName == null ? createRandomObjectName() : scriptName;
    }
}
