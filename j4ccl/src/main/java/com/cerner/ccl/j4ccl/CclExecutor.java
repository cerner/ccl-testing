package com.cerner.ccl.j4ccl;

import java.io.File;
import java.io.OutputStream;
import java.util.Enumeration;

import org.apache.commons.discovery.tools.Service;

import com.cerner.ccl.j4ccl.adders.DynamicCompilerAdder;
import com.cerner.ccl.j4ccl.adders.ScriptCompilerAdder;
import com.cerner.ccl.j4ccl.adders.ScriptDropAdder;
import com.cerner.ccl.j4ccl.adders.ScriptExecutionAdder;
import com.cerner.ccl.j4ccl.enums.OutputType;
import com.cerner.ccl.j4ccl.exception.CclException;

/**
 * A gateway to executing commands in a remote CCL session.
 *
 * @author Joshua Hyde
 *
 */

public abstract class CclExecutor {
    /**
     * Get an executor.
     *
     * @return An implementation of {@link CclExecutor}.
     * @throws IllegalStateException
     *             If no implementations are found.
     */
    public static CclExecutor getExecutor() {
        @SuppressWarnings("unchecked")
        final Enumeration<CclExecutor> providers = Service.providers(CclExecutor.class);
        if (!providers.hasMoreElements())
            throw new IllegalStateException("No implementations found of: " + CclExecutor.class.getName());

        return providers.nextElement();
    }

    /**
     * Add a dynamic compiler action to the command queue.
     *
     * @param includeFile
     *            A {@link File} object representing the include file to be compiled.
     * @return A {@link DynamicCompilerAdder} which can be used to specify the nature of the dynamic script compilation.
     * @throws IllegalArgumentException
     *             If the given {@link File} object is not a file.
     * @throws NullPointerException
     *             If the given file is {@code null}.
     */
    @SuppressWarnings("rawtypes")
    public abstract DynamicCompilerAdder addDynamicCompiler(File includeFile);

    /**
     * Add a script compilation action to the command queue.
     *
     * @param file
     *            A {@link File} object representing the CCL script to be compiled.
     * @return A {@link ScriptCompilerAdder} which can be used to specify the nature of the script compilation.
     * @throws IllegalArgumentException
     *             If the given {@link File} object is not a file.
     * @throws NullPointerException
     *             If the given file is {@code null}.
     */
    @SuppressWarnings("rawtypes")
    public abstract ScriptCompilerAdder addScriptCompiler(File file);

    /**
     * Add a command to drop a script on the close of the CCL execution.
     *
     * @param scriptName
     *            The name of the script to be dropped. This script must have been compiled or set to compile using a
     *            CclExecutor object in this VM.
     * @return A {@link ScriptDropAdder} object that can be used to drop a script.
     * @throws IllegalArgumentException
     *             If the given script name is blank.
     * @throws NullPointerException
     *             If the given script name is {@code null}.
     */
    public abstract ScriptDropAdder addScriptDropper(String scriptName);

    /**
     * Add a CCL script execution action to the command queue.
     *
     * @param scriptName
     *            The name of the CCL script to be executed.
     * @return A {@link ScriptExecutionAdder} object which can be used to specify the nature of the script execution.
     * @throws IllegalArgumentException
     *             If the given script name is blank.
     * @throws NullPointerException
     *             If the given script name is null.
     */
    public abstract ScriptExecutionAdder addScriptExecution(String scriptName);

    /**
     * Execute all queued commands.
     *
     * @throws CclException
     *             If any errors occur during the processing of the commands.
     */
    public abstract void execute();

    /**
     * Pipe the output to a given output stream. <br>
     * Only the last given stream is retained; invoking this multiple times will have no effect except to set the
     * last-given output stream as the used output stream.
     *
     * @param stream
     *            An {@link OutputStream} object to which the output should be piped.
     * @param outputType
     *            An {@link OutputType} enum representing the type of output to be piped.
     */
    public abstract void setOutputStream(OutputStream stream, OutputType outputType);

    /**
     * Sets the {@link TerminalProperties} that will be employed by the CclExecutor. <br>
     * Only the last given value is retained; invoking this multiple times will displace any previously set value.
     *
     * @param terminalProperties
     *            The {@link TerminalProperties} object to be used.
     */
    public abstract void setTerminalProperties(TerminalProperties terminalProperties);
}
