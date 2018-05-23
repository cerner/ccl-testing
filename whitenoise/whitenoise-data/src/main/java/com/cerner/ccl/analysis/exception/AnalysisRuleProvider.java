package com.cerner.ccl.analysis.exception;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.discovery.tools.Service;

import com.cerner.ccl.analysis.data.AnalysisRule;

/**
 * Skeleton definition of a class used to provision implementations of {@link AnalysisRule}.
 *
 * @author Joshua Hyde
 */

public class AnalysisRuleProvider {
    private Set<AnalysisRule> rules;

    /**
     * Get the rules to be used.
     *
     * @return A {@link Set} of {@link AnalysisRule} implementations.
     */
    @SuppressWarnings("unchecked")
    public Set<AnalysisRule> getRules() {
        if (rules == null) {
            rules = new HashSet<AnalysisRule>();

            final Enumeration<AnalysisRule> providersEnum = Service.providers(AnalysisRule.class);
            while (providersEnum.hasMoreElements())
                rules.add(providersEnum.nextElement());
        }

        return rules;
    }
}
