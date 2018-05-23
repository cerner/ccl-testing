package com.cerner.ccl.analysis.core.rules;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.cerner.ccl.analysis.core.AbstractJDomTest;
import com.cerner.ccl.analysis.data.Violation;

/**
 * Integration tests for {@link WhileLoopRules}.
 *
 * @author Jeff Wiedemann
 *
 */

public class WhileLoopRulesTest extends AbstractJDomTest {
    /**
     * This test is designed to ensure that for various permutations of while loops where conditional variables are not
     * referenced within the body of the while loop, the loops are identified as having violations
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testNoReferenceToConditionals() throws Exception {
        final Set<Violation> violations = new WhileLoopRules(toDocument("no-reference-to-conditionals.xml"))
                .doMeasuredAnalysis();
    	assertThat(violations).hasSize(6);

    	final List<Integer> failedLines = new ArrayList<Integer>(Arrays.asList(23,19,15,11,7,3));

    	for (Violation v : violations) {
    		assertThat(failedLines).contains(v.getLineNumber());
    	}
    }

    /**
     * This test is designed to ensure that for various permutations of how to alter while loop conditional variables
     * within a while loop, there are no false positives found
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testReferenceToConditionals() throws Exception {
        final Set<Violation> violations = new WhileLoopRules(toDocument("reference-to-conditionals.xml"))
                .doMeasuredAnalysis();
    	assertThat(violations).hasSize(0);
    }
}
