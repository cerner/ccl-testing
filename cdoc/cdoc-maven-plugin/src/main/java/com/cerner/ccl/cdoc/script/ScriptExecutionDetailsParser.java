package com.cerner.ccl.cdoc.script;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

/**
 * A parser to retrieve the script execution details of the given source code.
 *
 * @author Joshua Hyde
 *
 */

public class ScriptExecutionDetailsParser {
    /**
     * Parse the script execution details from the given source.
     *
     * @param source
     *            A {@link List} of {@link String} objects representing the source to be parsed.
     * @return A {@link ScriptExecutionDetails} object representing the script execution of the given source.
     */
    public ScriptExecutionDetails getDetails(final List<String> source) {
        final List<ScriptExecutionWarning> warnings = new ArrayList<ScriptExecutionWarning>();
        final Set<String> executedScripts = new TreeSet<String>(CaseInsensitiveComparator.getInstance());
        int lineNumber = 1;

        for (final String line : source) {
            final String spaceless = StringUtils.remove(line, ' ').toUpperCase(Locale.US);
            if (spaceless.contains("EXECUTEVALUE(") || spaceless.contains("CALLPRG(")) {
                warnings.add(new ScriptExecutionWarning(lineNumber, line));
            } else if (StringUtils.startsWithIgnoreCase(StringUtils.strip(line), "execute ")) {
                // Normalize out any double spaces
                String normalized = line.toLowerCase(Locale.US);
                while (normalized.contains("  "))
                    normalized = StringUtils.remove(normalized, "  ");

                final int executeEndPos = normalized.indexOf("execute ") + "execute ".length();
                final int nextSpacePos = normalized.indexOf(' ', executeEndPos + 1);
                final String scriptName = StringUtils
                        .trim((String) (nextSpacePos < 0 ? normalized.substring(executeEndPos)
                                : normalized.subSequence(executeEndPos, nextSpacePos)))
                        .toLowerCase(Locale.US);
                executedScripts.add(scriptName);
            }

            lineNumber++;
        }

        return new ScriptExecutionDetails(executedScripts, warnings);
    }

    /**
     * A {@link Comparator} that case-insensitively compares two strings.
     *
     * @author Joshua Hyde
     *
     */
    private static class CaseInsensitiveComparator implements Comparator<String>, Serializable {
        private static final long serialVersionUID = 2096670796476440942L;
        private static final CaseInsensitiveComparator INSTANCE = new CaseInsensitiveComparator();

        /**
         * Get a singleton instance of the comparator.
         *
         * @return An instance of {@link CaseInsensitiveComparator}.
         */
        public static CaseInsensitiveComparator getInstance() {
            return INSTANCE;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(final String o1, final String o2) {
            return o1.compareToIgnoreCase(o2);
        }

    }
}
