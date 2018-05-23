package com.cerner.ccl.analysis.core.rules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.cerner.ccl.analysis.core.violations.EmptyListOrStructureDefinitionViolation;
import com.cerner.ccl.analysis.core.violations.FreedRecordStructureViolation;
import com.cerner.ccl.analysis.core.violations.UnprotectedRecordStructureDefinitionViolation;
import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;

/**
 * A {@link AnalysisRule} that identifies the following issues with record structure declarations:
 *   1. A STRUCT or LIST structure where the subsequent field member is not a child of the structure but accidently a sibling.
 *   For example:
 *   record badStructure
 *   (
 *     1 struct
 *       1 value = i4
 *   )
 *
 * @author Jeff Wiedemann
 */

public class RecordStructureDeclarationRules extends TimedDelegate {

    /**
     * @param document
     *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
     */
    public RecordStructureDeclarationRules(final Document document) {
        super(document);
    }

    @Override
    protected Set<Violation> doMeasuredAnalysis() throws JDOMException {
        final Set<Violation> violations = new HashSet<Violation>();

        final List<Element> records = selectNodesByName("RECORD.");
        final List<Element> frees = selectNodesByName("Z_FREE.");

        for (final Element record : records) {
            final String recordName = getCclName(record.getChild("COMMA."));

            //Identify all fields within the record that appear to be lists or structures
            for (final Element listOrStruct : selectNodesByName(record, "FIELD.", "[OCCUR. or not(FORMAT.)]")) {
                if (listOrStruct.getChildren("FIELD.").size() == 0)
                    violations.add(new EmptyListOrStructureDefinitionViolation(recordName, getCclName(listOrStruct), getLineNumber(listOrStruct)));
            }

            //If record structure is declared and not protected, then add the violation
            if (recordStructureHasNoDefinedScoping(record))
                violations.add(new UnprotectedRecordStructureDefinitionViolation(recordName, getLineNumber(record)));

            //search for an instance where a defined record is freed and add the violation
            for (final Element free : frees) {
                if (getCclName(free).equalsIgnoreCase(recordName)) {
                    violations.add(new FreedRecordStructureViolation(recordName, getLineNumber(free)));
                    break;
                }
            }
        }

        return violations;
    }

    private boolean recordStructureHasNoDefinedScoping(final Element record) {
        if (record.getChild("OPTIONS.") == null)
            return true;

        for (final Element option : record.getChild("OPTIONS.").getChildren("OPTION.")) {
            if (getCclName(option).equalsIgnoreCase("PROTECT"))
                return false;
            if (getCclName(option).equalsIgnoreCase("PRIVATE"))
                return false;
            if (getCclName(option).equalsIgnoreCase("PUBLIC"))
                return false;
            if (getCclName(option).equalsIgnoreCase("PERSIST"))
                return false;
            if (getCclName(option).equalsIgnoreCase("PERSISTSCRIPT"))
                return false;
        }

        return true;
    }

    @Override
    public Set<Violation> getCheckedViolations() {
        final Set<Violation> violations = new HashSet<Violation>();
        violations.add(new EmptyListOrStructureDefinitionViolation("recordName", "listOrStructure", 1));
        violations.add(new UnprotectedRecordStructureDefinitionViolation("recordName", 1));
        violations.add(new FreedRecordStructureViolation("recordName", 1));
        return violations;
    }
}