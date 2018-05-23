package com.cerner.ccl.analysis.core.rules;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;

import com.cerner.ccl.analysis.core.AbstractJDomTest;
import com.cerner.ccl.analysis.core.violations.EmptyListOrStructureDefinitionViolation;
import com.cerner.ccl.analysis.core.violations.FreedRecordStructureViolation;
import com.cerner.ccl.analysis.core.violations.UnprotectedRecordStructureDefinitionViolation;
import com.cerner.ccl.analysis.data.Violation;

/**
 * Integration tests for {@link RecordStructureDeclarationRules}.
 *
 * @author Jeff Wiedemann
 *
 */

public class RecordStructureDeclarationRulesTest extends AbstractJDomTest {
    /**
     * This test is designed to ensure that for variously well defined CCL record structures, the new
     * RecordStructureDeclarationRules() does not identify false positives
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testWellDefinedRecordDeclarations() throws Exception {
        final Set<Violation> violations = new RecordStructureDeclarationRules(
                toDocument("well-defined-record-structures.xml")).doMeasuredAnalysis();
        assertThat(violations).hasSize(0);
    }

    /**
     * This test is designed to ensure that for various permutations of record structures define with a list or struct
     * which does not have any child members, the new RecordStructureDeclarationRules() appropriately identifies the
     * problems
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testEmptyListOrStructureDeclarations() throws Exception {
        final Set<Violation> violations = new RecordStructureDeclarationRules(
                toDocument("empty-list-or-structure-declarations.xml")).doMeasuredAnalysis();
        assertThat(violations).hasSize(4);

        assertThat(violations).contains(new EmptyListOrStructureDefinitionViolation("BADRECORD1", "LIST1", 5));
        assertThat(violations).contains(new EmptyListOrStructureDefinitionViolation("BADRECORD2", "STRUCT2", 11));
        assertThat(violations).contains(new EmptyListOrStructureDefinitionViolation("BADRECORD3", "SUBSTRUCT3", 18));
        assertThat(violations).contains(new EmptyListOrStructureDefinitionViolation("BADRECORD4", "STRUCT4", 24));

    }

    /**
     * This test is designed to ensure that for various permutations of record defined in this script being freed the
     * new RecordStructureDeclarationRules() appropriately identifies the problem
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testFreedRecordStructures() throws Exception {
        final Set<Violation> violations = new RecordStructureDeclarationRules(toDocument("freed-record-structures.xml"))
                .doMeasuredAnalysis();
        assertThat(violations).hasSize(3);

        assertThat(violations).contains(new FreedRecordStructureViolation("MYREC", 3));
        assertThat(violations).contains(new FreedRecordStructureViolation("ANOTHERREC", 25));
        assertThat(violations).contains(new FreedRecordStructureViolation("ATHIRDREC", 24));
    }

    /**
     * This test is designed to ensure that for various permutations of record defined with no explicit scoping the new
     * RecordStructureDeclarationRules() appropriately identifies the problem
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testUnprotectedRecordStructures() throws Exception {
        final Set<Violation> violations = new RecordStructureDeclarationRules(
                toDocument("unprotected-record-structure-declarations.xml")).doMeasuredAnalysis();
        assertThat(violations).hasSize(2);

        assertThat(violations).contains(new UnprotectedRecordStructureDefinitionViolation("REC1", 3));
        assertThat(violations).contains(new UnprotectedRecordStructureDefinitionViolation("REC2", 8));
    }
}
