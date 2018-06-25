package com.cerner.ccl.analysis.core.rules;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.cerner.ccl.analysis.core.AbstractJDomTest;
import com.cerner.ccl.analysis.core.violations.SizeOfRecordMemberViolation;
import com.cerner.ccl.analysis.core.violations.SizeOfRecordMissingTrimViolation;
import com.cerner.ccl.analysis.data.Violation;

/**
 * Integration tests for {@link SizeFunctionRules}.
 *
 * @author Jeff Wiedemann
 *
 */

public class SizeFunctionRulesTest extends AbstractJDomTest {
    /**
     * Test to ensure that the appropriate violations are caught when the CCL record structure that doesn't have a
     * declaration is accessed various ways
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testNoDeclaredRecordStructure() throws Exception {
        final Set<Violation> violations = new SizeFunctionRules(toDocument("size-function-use-no-record-declared.xml"))
                .analyze();
        assertThat(violations).hasSize(4);

        assertThat(violations).contains(new SizeOfRecordMissingTrimViolation(6));
        assertThat(violations).contains(new SizeOfRecordMemberViolation("1", 12));
        assertThat(violations).contains(new SizeOfRecordMemberViolation("2", 17));
        assertThat(violations).contains(new SizeOfRecordMemberViolation("1", 26));
    }

    /**
     * Test to ensure that the appropriate violations are caught when the CCL record structure that is declared is
     * accessed various ways
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testDeclaredRecordStructure() throws Exception {
        final Set<Violation> violations = new SizeFunctionRules(toDocument("size-function-use-record-declared.xml"))
                .analyze();
        assertThat(violations).hasSize(4);

        assertThat(violations).contains(new SizeOfRecordMemberViolation("1", 13));
        assertThat(violations).contains(new SizeOfRecordMemberViolation("1", 18));
        assertThat(violations).contains(new SizeOfRecordMemberViolation("2", 23));
        assertThat(violations).contains(new SizeOfRecordMemberViolation("3", 28));

    }
}
