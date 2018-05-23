package com.cerner.ccl.analysis.core.rules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.cerner.ccl.analysis.core.violations.LargeSequenceConvertedToIntegerViolation;
import com.cerner.ccl.analysis.core.violations.LargeSequenceStoredToNonFloatingPointNumberViolation;
import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;

/**
 * A {@link AnalysisRule} that identifies the following issues with large sequences:
 *   1. Instances where cnvtint was used on a seq() function instead of cnvtreal
 *   2. Instances where cnvtreal was attempted to be stored into an I4 variable
 *
 * @author Jeff Wiedemann
 */
//TODO: create unit tests for this class
public class LargeSequenceRules extends TimedDelegate {

    /**
     * @param document
     *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
     */
    public LargeSequenceRules(final Document document) {
        super(document);
    }

    @Override
    protected Set<Violation> doMeasuredAnalysis() throws JDOMException {
        final Set<Violation> violations = new HashSet<Violation>();

        //Loop through each SEQ() function call and inspect the behavior of the code for proper handling/storage of
        //result to accommodate large sequence issues
        for (final Element seqCall : selectNodesByName( "CALL.", "[NAME[1]/@text = 'SEQ']")) {
            //First thing to check is if the seq call is immediately being wrapped by a cnvtint. This is a problem and is the easiest and
        	//fastest to find
            if (!selectNodes(seqCall, ".[name(..) = 'CALL.' and ../NAME[1]/@text = 'CNVTINT']").isEmpty()) {
        		violations.add(new LargeSequenceConvertedToIntegerViolation(getLineNumber(seqCall)));
        		continue;
        	}

        	//Next, if the seq call is appearing in the set clause of an update or insert statement, and is not being wrapped
        	//by a cnvtint then everything is fine, we can move to the next item
        	if (!selectNodes(seqCall, ".[ancestor::Z_INSERT. or ancestor::Z_UPDATE.]").isEmpty())
        		continue;

        	//Simple safety check here to ensure that we can compute the seqVariableAssignment in the following code, this rule is not
        	//equipped to encounter usage of the seq command beyond the following XPath expression
        	if (selectNodes(seqCall, "./ancestor::IS.[1]").isEmpty())
        		continue;

        	//Store off some helpful variables
        	String seqVariableAssignmentName = selectAttributes(seqCall, "./ancestor::IS.[1]/NAME[1]/@text").get(0).getValue();
        	Element selectStatement = selectNodes(seqCall, "./ancestor::Z_SELECT.").get(0);

            // Check to see if the resulting variable the data is stored in is being wrapped with a cnvtint, because
            // that is once again,
        	//a problem
        	if (!selectNodes(selectStatement, ".//CALL.[NAME[1]/@text='CNVTINT' and NAME[2]/@text='" + seqVariableAssignmentName + "']").isEmpty()) {
        		violations.add(new LargeSequenceConvertedToIntegerViolation(seqVariableAssignmentName, getLineNumber(seqCall)));
        		continue;
        	}

        	//Select all instances where the variable holding the result of the seq() function is stored to another variable.
        	//All such variables must somehow be CCL F8s.
        	List<Element> mustBeF8s = selectNodes(selectStatement, ".//IS.[NAME[1]/@text != '" + seqVariableAssignmentName + "' and .//NAME/@text = '" + seqVariableAssignmentName + "']/NAME[1]");

        	//It should be rare that the seq() variable is not used in the select anywhere, but if not, just move on
        	if (mustBeF8s.isEmpty())
        		continue;

        	//Store off all known and common ways to instantiate a variable to a valid F8 value
        	//Currently this include declaring the variable as f8, or somehow setting it to a real value (presumably 0.0)
        	SortedSet<String> allValidF8VariableNames = new TreeSet<String>();
        	for (Element e : selectNodesByName("Z_DECLARE.", "[NAME[2]/@text='F8']"))
        		allValidF8VariableNames.add(e.getChild("NAME").getAttributeValue("text"));
        	for (Element e : selectNodesByName("Z_SET.", "[NAME and REAL]"))
        		allValidF8VariableNames.add(e.getChild("NAME").getAttributeValue("text"));
        	for (Element e : selectNodesByName( "IS.", "[count(NAME) = 1 and count(REAL) = 1]"))
        		allValidF8VariableNames.add(e.getChild("NAME").getAttributeValue("text"));

        	for (Element variable : mustBeF8s) {
        		//If variable is declared in an F8 declaration statement, it's good to go
        		if (allValidF8VariableNames.contains(variable.getAttributeValue("text")))
        			continue;

        		violations.add(new LargeSequenceStoredToNonFloatingPointNumberViolation(seqVariableAssignmentName, variable.getAttributeValue("text"), getLineNumber(seqCall)));
        	}
        }

        return violations;
    }

    @Override
    public Set<Violation> getCheckedViolations() {
		Set<Violation> violations = new HashSet<Violation>();
		violations.add(new LargeSequenceConvertedToIntegerViolation("variable", 1));
		violations.add(new LargeSequenceStoredToNonFloatingPointNumberViolation("variable1", "variable2", 1));
		return violations;
	}
}