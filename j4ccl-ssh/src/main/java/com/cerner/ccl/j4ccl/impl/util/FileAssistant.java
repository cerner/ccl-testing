package com.cerner.ccl.j4ccl.impl.util;

import java.io.File;
import java.net.URI;
import java.util.Locale;

import com.cerner.ccl.j4ccl.impl.data.Environment;

/**
 * A utility class to assist in using of files.
 *
 * @author Joshua Hyde
 *
 */

public final class FileAssistant {
    private FileAssistant() {

    }

    /**
     * Determine the location a file should reside on on the remote environment. <br>
     * This requires an {@link Environment#getEnvironment()} to be in scope.
     *
     * @param localFile
     *            A {@link File} reference to a local file whose location on the remote environment is to be determined.
     * @return A {@link URI} object representing the location on the remote server to which the given local file would
     *         be uploaded. All files placed in CCLSOURCE will be automatically converted to lower-case; everything else
     *         will retain its given casing.
     * @throws IllegalArgumentException
     *             If the given file is not actually a file.
     * @throws NullPointerException
     *             If the given file is {@code null}.
     * @see #createRemotePath(File, Environment)
     */
    public static URI createRemotePath(final File localFile) {
        return createRemotePath(localFile, Environment.getEnvironment());
    }

    /**
     * Determine the location on the remote server to which a file is to be uploaded.
     *
     * @param localFile
     *            A {@link File} object representing the local file to be uploaded.
     * @param environment
     *            A {@link Environment} object describing the remote environment to which the file will eventually be
     *            uploaded.
     * @return A {@link URI} object representing the location on the remote server to which the given local file would
     *         be uploaded. All files placed in CCLSOURCE will be automatically converted to lower-case; everything else
     *         will retain its given casing.
     * @throws IllegalArgumentException
     *             If the given file is not actually a file.
     * @throws NullPointerException
     *             If the given file or environment is {@code null}.
     */
    public static URI createRemotePath(final File localFile, final Environment environment) {
        if (localFile == null) {
            throw new NullPointerException("Local file cannot be null.");
        }

        if (environment == null) {
            throw new NullPointerException("Environment cannot be null.");
        }

        if (localFile.exists() && !localFile.isFile()) {
            throw new IllegalArgumentException("File must be an actual file.");
        }

        final String fileName = isCclSource(localFile.getName()) ? localFile.getName().toLowerCase(Locale.getDefault())
                : localFile.getName();
        final String path = getRemoteDirectory(fileName, environment);

        return URI.create(path + "/" + fileName);
    }

    /**
     * Determine the remote directory into which the file should be uploaded.
     *
     * @param fileName
     *            The filename of the file to be uploaded. This is assumed to be lower-case.
     * @param environment
     *            A {@link Environment} object that describes the remote environment.
     * @return For the given environment:
     *         <ul>
     *         <li>cclsource if the given file is a .PRG, .INC, or .SUB</li>
     *         <li>cer_proc if the given file is a .KSH or .COM</li>
     *         <li>All else defaults to cer_install</li>
     *         </ul>
     */
    private static String getRemoteDirectory(final String fileName, final Environment environment) {
        if (isCclSource(fileName)) {
            if (isDynamicScript(fileName)) {
                return environment.getCerTemp();
            }
            return environment.getCclSource();
        }

        if (isShellScript(fileName)) {
            return environment.getCerProc();
        }

        return environment.getCerInstall();
    }

    /**
     * Determine whether or not the given filename represents a file that belongs in CCLSOURCE.
     *
     * @param fileName
     *            The name of the file whose placement is to be determined.
     * @return {@code true} if the given filename represents a file to be uploaded to CCL source.
     */
    private static boolean isCclSource(final String fileName) {
        final String lowerName = fileName.toLowerCase(Locale.getDefault());
        return lowerName.endsWith(".prg") || lowerName.endsWith(".inc") || lowerName.endsWith(".sub");
    }

    /**
     * Determine whether or not the given filename represents a dynamically-generated script.
     *
     * @param fileName
     *            The name of the file whose dynamic state is to be determined.
     * @return {@code true} if the given script name represents a dynamic script.
     */
    private static boolean isDynamicScript(final String fileName) {
        return ScriptRegistrar.isDynamicScript(fileName);
    }

    /**
     * Determine whether or not the given filename represents a file that is a shell script.
     *
     * @param fileName
     *            The name of the file whose placement is to be determined.
     * @return {@code true} if the given filename represents a file that is a shell script.
     */
    private static boolean isShellScript(final String fileName) {
        final String lowerName = fileName.toLowerCase(Locale.getDefault());
        return lowerName.endsWith(".ksh") || lowerName.endsWith(".com");
    }
}
