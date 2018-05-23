package com.cerner.ccl.j4ccl.adders;

import java.io.File;

/**
 * An {@link Adder} implementation that adds a command to compile a script to a CCL command queue.
 *
 * @author Joshua Hyde
 * @param <S>
 *            The type of {@link ScriptCompilerAdder} to be returned by this adder as part of its usage.
 *
 */

@SuppressWarnings("rawtypes")
public interface ScriptCompilerAdder<S extends ScriptCompilerAdder> extends Adder {
    /**
     * Set whether or not the script should be compiled in debug mode; by default, a compiler does not compile in debug
     * mode.
     *
     * @param debugModeEnabled
     *            {@code true} if the script should be compiled in debug mode; {@code false} if the script should not be
     *            compiled in debug mode.
     * @return This adder.
     */
    S withDebugModeEnabled(boolean debugModeEnabled);

    /**
     * Define a dependent file that must be present in the target server before this file can be compiled.
     *
     * @param file
     *            A {@link File} object representing the file that must be present on the remote server.
     * @return This object.
     * @throws NullPointerException
     *             If the given file is {@code null}.
     */
    S withDependency(File file);

    /**
     * Set the location on the local harddisk to where the listing output will be downloaded.
     * <br>
     * This is an optional action.
     *
     * @param file
     *            A {@link File} object representing where on the hard disk the file should be downloaded. If this is a
     *            file, then such a file with the listing contents will be created; if this is a directory, then the
     *            file will be copied to the directory, retaining its original name.
     * @return This object.
     * @throws NullPointerException
     *             If the given file is {@code null}.
     */
    S withListingOutput(File file);
}
