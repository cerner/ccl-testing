package com.cerner.ccl.j4ccl.impl.util;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.cerner.ccl.j4ccl.adders.DynamicCompilerAdder;

/**
 * A registrar of CCL scripts to share information about them across multiple objects that are not necessarily directly
 * related.
 *
 * @author Joshua Hyde
 *
 */

public class ScriptRegistrar {
    private static final Set<String> dynamicScripts = new HashSet<String>();
    private static final Set<String> compiledScripts = new HashSet<String>();

    /**
     * Mark a script as either compiled or set to be compiled.
     *
     * @param scriptName
     *            The name of the script to be marked as either compiled or ready for compilation.
     */
    public static void registerCompiledScript(final String scriptName) {
        compiledScripts.add(getCasedName(scriptName));
    }

    /**
     * Register a script as a dynamically-generated script. A dynamically-generated script is defined as a script whose
     * sole purpose is to be a wrapper script around an include file.
     *
     * @param scriptName
     *            The name of the dynamically-generated script.
     * @see DynamicCompilerAdder
     */
    public static void registerDynamicScript(final String scriptName) {
        dynamicScripts.add(getCasedName(scriptName));
        registerCompiledScript(scriptName);
    }

    /**
     * Determine whether or not a script has been marked for compilation.
     *
     * @param scriptName
     *            The name of the script whose compiled status is to be determined.
     * @return {@code true} if the given script name is marked for compilation.
     */
    public static boolean isCompiledScript(final String scriptName) {
        return compiledScripts.contains(getCasedName(scriptName));
    }

    /**
     * Determine whether or not a script has been marked as being a dynamic script.
     *
     * @param scriptName
     *            The name of the script whose dynamic nature is to be determined.
     * @return {@code true} if the given script name has been marked as being a dynamic script.
     * @see #registerDynamicScript(String)
     */
    public static boolean isDynamicScript(final String scriptName) {
        return dynamicScripts.contains(getCasedName(scriptName));
    }

    /**
     * Convert the given name to all-upper-case.
     *
     * @param name
     *            The name to be cased.
     * @return {@code null} if the given name is {@code null}; otherwise, the upper-case version of the given script
     *         name.
     */
    private static String getCasedName(final String name) {
        return name == null ? null : name.toUpperCase(Locale.getDefault());
    }
}
