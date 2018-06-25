package com.cerner.ccl.analysis.core.rules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.cerner.ccl.analysis.core.violations.SizeOfRecordMemberViolation;
import com.cerner.ccl.analysis.core.violations.SizeOfRecordMissingTrimViolation;
import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;

/**
 * An {@link AnalysisRule} that flags any instance where the size() function is in the following incorrect ways 1.
 * size() on record list without the appropriate size option of 5 2. size() on any string variable without nesting
 * within a trim() function
 *
 * @author Jeff Wiedemann
 *
 */

public class SizeFunctionRules extends TimedDelegate {
    private List<Element> allRecords = null;
    private List<Element> allMembers = null;

    /**
     * @param document
     *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
     */
    public SizeFunctionRules(final Document document) {
        super(document);
    }

    @Override
    protected Set<Violation> doMeasuredAnalysis() throws JDOMException {
        final Set<Violation> violations = new HashSet<Violation>();

        for (final Element sizeFunction : selectNodesByName("CALL.")) {
            // If the call is not to the size function then continue
            if (!getCclName(sizeFunction).equalsIgnoreCase("SIZE")) {
                continue;
            }

            // Store off the coded size option for this size call
            final String sizeOption = getSizeFunctionOptionNumber(sizeFunction);

            if (isElementARecordList(sizeFunction)) {
                if (!sizeOption.equalsIgnoreCase("5")) {
                    violations.add(new SizeOfRecordMemberViolation(sizeOption, getLineNumber(sizeFunction)));
                }
            } else {
                if (sizeFunction.getChildren().size() < 3 || sizeOption.equalsIgnoreCase("1")) {
                    final Element firstParam = sizeFunction.getChildren().get(1);

                    if (!firstParam.getName().equalsIgnoreCase("CALL.")
                            && !getCclName(firstParam).equalsIgnoreCase("TRIM")
                            && firstParam.getName().equalsIgnoreCase("NAME")) {
                        violations.add(new SizeOfRecordMissingTrimViolation(getLineNumber(sizeFunction)));
                    }
                }
            }
        }

        return violations;
    }

    private String getSizeFunctionOptionNumber(final Element sizeFunction) {
        if (sizeFunction.getChildren().size() < 3) {
            return "1";
        }

        // The the second param should store the contents of the size function option number
        final Element secondParam = sizeFunction.getChildren().get(2);

        return secondParam.getAttributeValue("text");
    }

    // An Element is a record list member if the following is true
    private boolean isElementARecordList(final Element sizeFunction) throws JDOMException {
        // Obtain the element that represents the first parameter of the size function
        final Element firstParam = sizeFunction.getChildren().get(1);

        // If the first param is not MEMBER then it is impossible for this to be a list item. So, lets check for that
        // and return out of here
        if (!firstParam.getName().equalsIgnoreCase("MEMBER.")) {
            return false;
        }

        // Lazy load all records now that we need it
        if (allRecords == null) {
            allRecords = selectNodesByName("RECORD.");
        }

        // Now that we know we are dealing with a MEMBER. element, the next thing we need to look at is the final
        // item within the MEMBER structure that they are ultimately calling size on. Once we have that item, we
        // need to do more logic to determine whether or not it's a list item
        final Element lastMemberName = firstParam.getChildren().get(firstParam.getChildren().size() - 1);

        // Does the last member name appear in the declaration of a record structure of a list item?
        // The following XPath locates the following:
        // A record structure declaration whose record name matches the name of the first MEMBER./NAME element.
        // This should be the name of the record structure, and ensures that that record structure has a field element
        // whose name matches the last MEMBER./NAME element which is the item the size function is actually operating
        // on, and lastly ensuring that that field element is defines as having an OCCUR which should identify it as a
        // list.
        for (final Element record : allRecords) {
            if (selectNodes(record, ".[COMMA./NAME/@text = '" + getCclName(firstParam)
                    + "' and .//FIELD.[NAME/@text = '" + lastMemberName.getAttributeValue("text") + "' and OCCUR.]]")
                            .size() > 0) {
                return true;
            }
        }

        // Lazy load all members now that we need it
        if (allMembers == null) {
            allMembers = selectNodesByName("MEMBER.");
        }

        // If we were not able to find a record structure declaration for the item then we must assume that the record
        // was globally defined, and we will make one last ditch effort to scan the source code for instances where the
        // member is referenced as a record list item. If we find such an occurrence then we should assume it's a list
        // and therefore must be accompanied by a second size parameter of 5
        for (final Element member : allMembers) {
            if (selectNodes(member, ".[NAME[1]/@text = '" + getCclName(firstParam) + "' and ARRAY.[NAME[1]/@text = '"
                    + lastMemberName.getAttributeValue("text") + "']]").size() > 0) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Set<Violation> getCheckedViolations() {
        final Set<Violation> violations = new HashSet<Violation>();
        violations.add(new SizeOfRecordMemberViolation("1", 1));
        violations.add(new SizeOfRecordMissingTrimViolation(1));
        return violations;
    }
}
