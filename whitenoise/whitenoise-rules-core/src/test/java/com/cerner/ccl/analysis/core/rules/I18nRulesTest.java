package com.cerner.ccl.analysis.core.rules;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.cerner.ccl.analysis.core.AbstractJDomTest;
import com.cerner.ccl.analysis.core.violations.DuplicateI18nKeyViolation;
import com.cerner.ccl.analysis.data.Violation;

/**
 * Integration tests for {@link I18nRules}.
 *
 * @author Jeff Wiedemann
 *
 */

public class I18nRulesTest extends AbstractJDomTest {
    /**
     * This test is designed to ensure that for various permutations of calls to uar_i18nGetMessage appropriately finds
     * instances where the message is duplicated
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testNoReferenceToConditionals() throws Exception {
        final Set<Violation> violations = new I18nRules(toDocument("duplicate-keys.xml")).doMeasuredAnalysis();
        assertThat(violations).hasSize(1);

        assertThat(violations).contains(new DuplicateI18nKeyViolation(15, "Val1"));

    }
}
