package com.cerner.ccl.analysis.engine;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.exception.AnalysisException;

/**
 * Definition of an engine that performs static analysis of CCL code.
 * 
 * @author Joshua Hyde
 * 
 */

public interface AnalysisEngine {

    /**
     * Perform static analysis.
     * 
     * @param programNames
     *            A {@link Collection} of {@link String} objects that are the names of the CCL programs to be statically
     *            analyzed.
     * @param rules
     *            A {@link Collection} of {@link AnalysisRule} objects to be used to analyze the given source files.
     * @return A {@link Map}; the keys are {@link File} references to the files that were analyzed; the values are
     *         {@link Set Sets} of {@link Violation} objects representing the violations incurred by this script, if
     *         any.
     * @throws AnalysisException
     *             If any errors occur during analysis of code.
     * @throws IllegalArgumentException
     *             If the given source file or rules collection are {@code null}.
     */
    Map<String, Set<Violation>> analyze(final Collection<String> programNames,
            final Collection<? extends AnalysisRule> rules);
}
