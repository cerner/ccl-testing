package com.cerner.ccl.analysis.engine.j4ccl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.engine.AnalysisEngine;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * An {@link AnalysisEngine} that uses j4ccl to communicate with CCL and get the program XML to perform the static
 * analysis.
 *
 * @author Joshua Hyde
 *
 */

public class J4CclAnalysisEngine implements AnalysisEngine {
    private final ScriptTranslator translator;

    /**
     * Create an SSH-backed static analysis engine.
     *
     * @param productProvider
     *            An {@link FtpProductProvider} used to provide sufficient credentials to upload and download files.
     */
    public J4CclAnalysisEngine(final FtpProductProvider productProvider) {
        this(new ScriptTranslator(productProvider));
    }

    /**
     * Create an SSH-backed static analysis engine.
     *
     * @param translator
     *            A {@link ScriptTranslator} object used to translate scripts to their XML form.
     * @throws IllegalArgumentException
     *             If any of the given objects are {@code null}.
     */
    public J4CclAnalysisEngine(final ScriptTranslator translator) {
        if (translator == null) {
            throw new IllegalArgumentException("Script translator cannot be null.");
        }

        this.translator = translator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Set<Violation>> analyze(final Collection<String> programNames,
            final Collection<? extends AnalysisRule> rules) {
        final EtmPoint point = PointFactory.getPoint(getClass(), "analyze");
        try {
            if (programNames == null) {
                throw new IllegalArgumentException("Source files cannot be null.");
            }

            if (rules == null) {
                throw new IllegalArgumentException("Rules cannot be null.");
            }

            if (programNames.isEmpty() || rules.isEmpty()) {
                return Collections.emptyMap();
            }

            new TranslatorCompiler().compile();

            final Map<String, Set<Violation>> violations = new HashMap<String, Set<Violation>>(programNames.size());
            for (final Entry<String, String> translation : translator.getTranslations(programNames).entrySet()) {
                final Set<Violation> analysisViolations = new HashSet<Violation>();
                for (final AnalysisRule rule : rules) {
                    analysisViolations.addAll(rule.analyze(translation.getValue()));
                }

                if (!analysisViolations.isEmpty()) {
                    violations.put(translation.getKey(), analysisViolations);
                }
            }
            return violations;
        } finally {
            point.collect();
        }
    }
}
