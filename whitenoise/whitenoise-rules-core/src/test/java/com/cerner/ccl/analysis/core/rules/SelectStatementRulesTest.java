package com.cerner.ccl.analysis.core.rules;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.cerner.ccl.analysis.core.AbstractJDomTest;
import com.cerner.ccl.analysis.core.violations.AccessToPrivateVariableFromSelectViolation;
import com.cerner.ccl.analysis.core.violations.FilesortAndMaxqualViolation;
import com.cerner.ccl.analysis.core.violations.HeadOrFootSectionWithoutOrderClauseViolation;
import com.cerner.ccl.analysis.core.violations.InvalidCnvtOnOracleFieldViolation;
import com.cerner.ccl.analysis.data.Violation;

/**
 * Integration tests for {@link SelectStatementRules}.
 *
 * @author Jeff Wiedemann
 *
 */

public class SelectStatementRulesTest extends AbstractJDomTest {
    /**
     * Test to ensure that the filesort and maxqual options are never used together
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testFilesortAndMaxqual() throws Exception {
        final Set<Violation> violations = new SelectStatementRules(toDocument("filesort-and-maxqual.xml")).analyze();
    	assertThat(violations).hasSize(1);

    	assertThat(violations).contains(new FilesortAndMaxqualViolation(9));
    }

    /**
     * Test to ensure that the appropriate violations are caught when the CCL record structure that is declared is
     * accessed various ways
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testHeadAndFootWithNoOrderByClause() throws Exception {
        final Set<Violation> violations = new SelectStatementRules(toDocument("head-foot-and-no-order-by.xml"))
                .analyze();
        assertThat(violations).hasSize(4);

    	assertThat(violations).contains(new HeadOrFootSectionWithoutOrderClauseViolation("P.PERSON_ID", 21));
    	assertThat(violations).contains(new HeadOrFootSectionWithoutOrderClauseViolation("A.ADDRESS_ID", 32));
        assertThat(violations).contains(new HeadOrFootSectionWithoutOrderClauseViolation("PERSONID", 44));
        assertThat(violations).contains(new HeadOrFootSectionWithoutOrderClauseViolation("PERSONID", 53));
    }

    /**
     * Test to ensure that a variable which was declared as private and accessed from the body of a select statement are
     * caught during analysis
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testPrivateVariableAccess() throws Exception {
        final Set<Violation> violations = new SelectStatementRules(toDocument("private-variable-access.xml")).analyze();
    	assertThat(violations).hasSize(4);

    	assertThat(violations).contains(new AccessToPrivateVariableFromSelectViolation("S1_VAR", 10));
    	assertThat(violations).contains(new AccessToPrivateVariableFromSelectViolation("S4_VAR", 39));
    	assertThat(violations).contains(new AccessToPrivateVariableFromSelectViolation("S5_VAR", 52));
    	assertThat(violations).contains(new AccessToPrivateVariableFromSelectViolation("S6_VAR", 62));
    }

    /**
     * Test to ensure that uses of cnvtint and cnvtreal within a select statement qualification are caught
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testCnvtintOrCnvtrealOnOracleField() throws Exception {
        final Set<Violation> violations = new SelectStatementRules(toDocument("cnvt-function-on-oracle-field.xml"))
                .analyze();
    	assertThat(violations).hasSize(3);

    	assertThat(violations).contains(new InvalidCnvtOnOracleFieldViolation(6));
    	assertThat(violations).contains(new InvalidCnvtOnOracleFieldViolation(14));
    	assertThat(violations).contains(new InvalidCnvtOnOracleFieldViolation(22));
    }
}
