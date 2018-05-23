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
 * Integration tests for {@link ForLoopIteratorRules}.
 *
 * @author Jeff Wiedemann
 *
 */
public class ForLoopRulesTest extends AbstractJDomTest {
    /**
     * This test is designed to ensure that for various permutations of how a for loop iterator might be accessed the
     * rule does not find any false positives
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testReferenceToForLoopIterator() throws Exception {
        final Set<Violation> violations = new ForLoopIteratorRules(toDocument("referenced-for-loop-iterator.xml"))
                .doMeasuredAnalysis();
        assertThat(violations).hasSize(0);
    }

    /**
     * This test is designed to ensure that for various permutations of how for loop iterators might be accidently
     * missed from the loop body, the rule appropriately identifies the problem
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testUnreferencedForLoopIterator() throws Exception {
        final Set<Violation> violations = new ForLoopIteratorRules(toDocument("unreferenced-for-loop-iterator.xml"))
                .doMeasuredAnalysis();
        assertThat(violations).hasSize(4);

        final List<Integer> failures = new ArrayList<Integer>(Arrays.asList(16, 20, 30, 34));

        for (Violation v : violations) {
            assertThat(failures).contains(v.getLineNumber());
        }
    }

    /**
     * This test is designed to ensure that for various permutations of how a for loop iterators might be accidently
     * overwritten with a new value, the rule appropriate identifies the problem
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testOverwrittenForLoopIterator() throws Exception {
        final Set<Violation> violations = new ForLoopIteratorRules(toDocument("overwritten-for-loop-iterator.xml"))
                .doMeasuredAnalysis();
        assertThat(violations).hasSize(3);

        final List<Integer> failures = new ArrayList<Integer>(Arrays.asList(18, 23, 34));

        for (Violation v : violations) {
            assertThat(failures).contains(v.getLineNumber());
        }
    }
}
