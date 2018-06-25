package com.cerner.ccl.analysis.data;

import java.util.Set;

import com.cerner.ccl.analysis.exception.AnalysisException;

/**
 * Definition of a rule used to perform static analysis.
 * 
 * @author Joshua Hyde
 * 
 */

public interface AnalysisRule {
    /**
     * Analyze a CCL script for violations of a static analysis rule.
     * 
     * @param prgXml
     *            A string containing the code of the CCL script, translated to XML.
     * @return A {@link Set} of {@link Violation} objects representing the violations specific to this rule encountered
     *         by this analysis.
     * @throws AnalysisException
     *             If any errors occur during the analysis of code.
     */
    Set<Violation> analyze(String prgXml);

    /**
     * Returns the set of violations which will be checked by this rule during analysis regardless of whether or not the
     * violation is identified during analysis
     * 
     * @return A {@link Set} of {@link Violation} objects representing the violations which were checked during analysis
     */
    Set<Violation> getCheckedViolations();
}
