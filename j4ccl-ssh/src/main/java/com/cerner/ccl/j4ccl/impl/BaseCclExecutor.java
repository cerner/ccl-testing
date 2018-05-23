package com.cerner.ccl.j4ccl.impl;

import java.io.File;
import java.io.OutputStream;

import javax.security.auth.Subject;

import org.apache.commons.lang.StringUtils;

import com.cerner.ccl.j4ccl.CclExecutor;
import com.cerner.ccl.j4ccl.TerminalProperties;
import com.cerner.ccl.j4ccl.adders.ScriptDropAdder;
import com.cerner.ccl.j4ccl.adders.ScriptExecutionAdder;
import com.cerner.ccl.j4ccl.enums.OutputType;
import com.cerner.ccl.j4ccl.impl.adders.DynamicCompilerAdderImpl;
import com.cerner.ccl.j4ccl.impl.adders.ScriptCompilerAdderImpl;
import com.cerner.ccl.j4ccl.impl.adders.ScriptDropAdderImpl;
import com.cerner.ccl.j4ccl.impl.adders.ScriptExecutionAdderImpl;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePasswordCredential;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePrincipal;
import com.cerner.ccl.j4ccl.impl.jaas.MillenniumDomainPasswordCredential;
import com.cerner.ccl.j4ccl.impl.jaas.MillenniumDomainPrincipal;
import com.cerner.ccl.j4ccl.impl.util.OutputStreamConfiguration;
import com.cerner.ccl.j4ccl.ssh.CclCommandTerminal;

/**
 * An implementation of {@link CclExecutor}. <br>
 * In order for this class - and its extensions - to function, there must be a {@link Subject} in
 * {@link Subject#getSubject(java.security.AccessControlContext) context}. It must have, at a minimum, the following:
 * <ul>
 * <li>A {@link BackendNodePrincipal} in the subject's {@link Subject#getPrincipals() principals}</li>
 * <li>A {@link BackendNodePasswordCredential} in the subjects {@link Subject#getPrivateCredentials() private
 * credentials}</li>
 * </ul>
 * <br>
 * In addition, if this executor is to log into Millennium, it must have the following:
 * <ul>
 * <li>A {@link MillenniumDomainPrincipal} in the subject's {@link Subject#getPrincipals() principals}</li>
 * <li>A {@link MillenniumDomainPasswordCredential} in the subjects {@link Subject#getPrivateCredentials() private
 * credentials}</li>
 * </ul>
 *
 * @author Joshua Hyde
 * @author Fred Eckertson
 *
 */

public class BaseCclExecutor extends CclExecutor {
    private final CommandQueue commandQueue;
    private OutputStreamConfiguration outputStreamConfiguration;
    private TerminalProperties terminalProperties;

    /**
     * Create a CCL command executor.
     */
    public BaseCclExecutor() {
        this(null, new CommandQueue());
    }

    /**
     * Create a CCL command executor that uses the given factories to generate required objects. <br>
     * This constructor exists primarily as a means of rough dependency injection. Typically, {@link #BaseCclExecutor()}
     * should be used. <br>
     * This constructor is purposefully left as package-level visibility to prevent it from being part of the published
     * API.
     *
     * @param terminalProperties
     *            The TerminalProperties to apply.
     *
     * @param commandQueue
     *            A {@link CommandQueue} managed by this executor.
     * @throws NullPointerException
     *             If the given command queue is {@code null}.
     */
    public BaseCclExecutor(final TerminalProperties terminalProperties, final CommandQueue commandQueue) {
        if (commandQueue == null)
            throw new NullPointerException("Command queue cannot be null.");

        this.commandQueue = commandQueue;
        this.terminalProperties = terminalProperties != null ? terminalProperties
                : TerminalProperties.getGlobalTerminalProperties();
    }

    @Override
    public DynamicCompilerAdderImpl addDynamicCompiler(final File includeFile) {
        if (includeFile == null)
            throw new NullPointerException("File cannot be null.");

        if (!includeFile.isFile())
            throw new IllegalArgumentException("Given file pointer is not actually a file.");

        return new DynamicCompilerAdderImpl(includeFile, getCommandQueue());
    }

    @Override
    public ScriptCompilerAdderImpl addScriptCompiler(final File file) {
        if (file == null)
            throw new NullPointerException("File cannot be null.");

        if (!file.isFile())
            throw new IllegalArgumentException("Given file pointer is not actually a file.");

        return new ScriptCompilerAdderImpl(file, getCommandQueue());
    }

    @Override
    public ScriptDropAdder addScriptDropper(final String scriptName) {
        if (scriptName == null)
            throw new NullPointerException("Script name cannot be null.");

        if (StringUtils.isEmpty(scriptName))
            throw new IllegalArgumentException("Script name cannot be blank.");

        return new ScriptDropAdderImpl(scriptName, getCommandQueue());
    }

    @Override
    public ScriptExecutionAdder addScriptExecution(final String scriptName) {
        if (scriptName == null)
            throw new NullPointerException("Script name cannot be null.");

        if (StringUtils.isEmpty(scriptName))
            throw new IllegalArgumentException("Script name cannot be blank.");

        return new ScriptExecutionAdderImpl(scriptName, getCommandQueue());
    }

    @Override
    public void execute() {
        if (getCommandQueue().isEmpty())
            return;

        final CclCommandTerminal cclTerminal = new CclCommandTerminal(terminalProperties, outputStreamConfiguration);
        getCommandQueue().execute(cclTerminal);
    }

    @Override
    public void setOutputStream(final OutputStream stream, final OutputType outputType) {
        this.outputStreamConfiguration = new OutputStreamConfiguration(stream, outputType);
    }

    @Override
    public void setTerminalProperties(final TerminalProperties terminalProperties) {
        this.terminalProperties = terminalProperties;
    }

    /**
     * Get the command queue that backs this executor.
     *
     * @return A {@link CommandQueue}.
     */
    protected CommandQueue getCommandQueue() {
        return commandQueue;
    }

    /**
     * Get the output stream configuration, if any.
     *
     * @return {@code null} if there is no set output stream configuration; otherwise, an
     *         {@link OutputStreamConfiguration} representing the configured output stream.
     */
    protected OutputStreamConfiguration getOutputStreamConfiguration() {
        return outputStreamConfiguration;
    }

    /**
     * Get the TerminalProperties configuration, if any.
     *
     * @return The TerminalProperties configuration fpr this executor.
     */
    protected TerminalProperties getTerminalProperties() {
        return terminalProperties;
    }
}
