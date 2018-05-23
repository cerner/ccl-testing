package com.cerner.ccl.analysis.core.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.cerner.ccl.analysis.core.violations.AccessToPrivateVariableFromSelectViolation;
import com.cerner.ccl.analysis.core.violations.FilesortAndMaxqualViolation;
import com.cerner.ccl.analysis.core.violations.HeadOrFootSectionWithoutOrderClauseViolation;
import com.cerner.ccl.analysis.core.violations.InvalidCnvtOnOracleFieldViolation;
import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;

/**
 * A {@link AnalysisRule} that identifies the following issues with select statements:
 *   1. A head or foot section within the report writer which is missing the corresponding order by clause
 *   2. A filesort used in conjunction with a maxqual statement
 *
 * @author Jeff Wiedemann
 */

public class SelectStatementRules extends TimedDelegate {

    /**
     * @param document
     *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
     */
    public SelectStatementRules(final Document document) {
        super(document);
    }

    @Override
    protected Set<Violation> doMeasuredAnalysis() throws JDOMException {
        final Set<Violation> violations = new HashSet<Violation>();

        final List<Element> selectStatements = selectNodesByName("Z_SELECT.");

        //Scan the select statement for matching order by statements for all head and foot sections
        for (final Element select : selectStatements) {
            final List<Element> headsAndFoots = new ArrayList<Element>();
            final List<Element> orderClause = new ArrayList<Element>();

            for (final Element e : selectNodesByName(select, "ATTR.")) {
                Element parent = e.getParentElement();
                String parentName = e.getParentElement().getName();
                if (parentName.equalsIgnoreCase("HEADBREAK.") || parentName.equalsIgnoreCase("FOOTBREAK.")) {
                    headsAndFoots.add(e);
                }
                if (parentName.equalsIgnoreCase("ORDER.")
                        && parent.getParentElement().getName().equalsIgnoreCase("ORDERLIST.")) {
                    orderClause.add(e);
                }
            }

            for (final Element e : selectNodesByName(select, "NAME")) {
                Element parent = e.getParentElement();
                String parentName = e.getParentElement().getName();
                if (parentName.equalsIgnoreCase("HEADBREAK.") || parentName.equalsIgnoreCase("FOOTBREAK.")) {
                    headsAndFoots.add(e);
                }
                if (parentName.equalsIgnoreCase("ORDER.")
                        && parent.getParentElement().getName().equalsIgnoreCase("ORDERLIST.")) {
                    orderClause.add(e);
                }
            }


            for (final Element headOrFoot : headsAndFoots) {
                boolean hasMatchingOrderBy = false;
                for (final Element orderItem : orderClause) {
                    //If the number of children isn't the same then this can't be a matching order by item
                    if (headOrFoot.getChildren().size() != orderItem.getChildren().size())
                        continue;

                    //Loop through the name elements and see if is particular order clause item matches the head or foot clause
                    for (int i = 0; i < headOrFoot.getChildren().size(); i++) {
                        hasMatchingOrderBy = true;

                        final Element hf = headOrFoot.getChildren().get(i);
                        final Element o = orderItem.getChildren().get(i);

                        if (!hf.getAttributeValue("text").equalsIgnoreCase(o.getAttributeValue("text"))) {
                            hasMatchingOrderBy = false;
                            break;
                        }
                    }
                    //If we found a matching order by statement then break out of the loop of order bys cause we are done
                    if (hasMatchingOrderBy)
                        break;
                }

                if (!hasMatchingOrderBy)
                    violations.add(new HeadOrFootSectionWithoutOrderClauseViolation(constructName(headOrFoot), getLineNumber(headOrFoot)));
            }
        }

        //Scan the select statement for the use of filesort and maxqual simultaneously
        for (final Element select : selectStatements) {
            boolean hasFilesort = false;
            boolean hasMaxqual = false;

            //Look at each option within the select
            for (final Element option : selectNodesByName(select, "OPTION.")) {
                //Very simple safety check to not process element if his parent is not an options element
                if (!option.getParentElement().getName().equalsIgnoreCase("OPTIONS."))
                    continue;

                if (getCclName(option).equalsIgnoreCase("FILESORT"))
                    hasFilesort = true;

                if (option.getChild("CALL.") != null && getCclName(option.getChild("CALL.")).equalsIgnoreCase("MAXQUAL"))
                    hasMaxqual = true;

                if (hasMaxqual && hasFilesort) {
                    violations.add(new FilesortAndMaxqualViolation(getLineNumber(option)));
                    break;
                }
            }
        }

        //Scan the select statement for attempts to read or write to a variable which was declared private.
        for (final Element declare : getVariableDeclarations()) {
            //Check to see if this declare is a private variable
            if (isPrivateDeclare(declare)) {
                //Scan each select statement to determine if there is an instance where the private variable is either
                //being read or written
                for (final Element select : selectStatements) {
                    for (final Element name : selectNodesByName(select, "NAME")) {
                        if (name.getAttributeValue("text").equalsIgnoreCase(getCclName(declare)))
                            violations.add(new AccessToPrivateVariableFromSelectViolation(getCclName(declare), getLineNumber(name)));
                    }
                }
            }
        }


        //Scan the select statement for the use of cnvtint() or cnvtreal() on an oracle field
        for (final Element select : selectStatements) {
            //Look at each option within the select
            for (final Element qual : selectNodesByName(select, "QUAL.")) {
                for (final Element attr : selectNodesByName(qual, "ATTR.")) {
                    if (getCclName(attr.getParentElement()).equalsIgnoreCase("CNVTINT") || getCclName(attr.getParentElement()).equalsIgnoreCase("CNVTREAL"))
                        violations.add(new InvalidCnvtOnOracleFieldViolation(getLineNumber(attr)));
                }
            }
        }

        return violations;
    }

    private boolean isPrivateDeclare(final Element declare) throws JDOMException {
        for (final Element option : selectNodesByName(declare, "OPTION.")) {
            if (!option.getParentElement().getName().equalsIgnoreCase("OPTIONS."))
                continue;

            if (getCclName(option).equalsIgnoreCase("PRIVATE"))
                return true;
        }
        return false;
    }

    private String constructName(final Element headOrFoot) {
        if (headOrFoot.getName().equalsIgnoreCase("NAME")) {
            return headOrFoot.getAttributeValue("text");
        }
        String name = "";
        for (final Element nameElement : headOrFoot.getChildren()) {
            if (name.equalsIgnoreCase(""))
                name = nameElement.getAttributeValue("text");
            else
                name = name + "." + nameElement.getAttributeValue("text");
        }

        return name;
    }

    @Override
    public Set<Violation> getCheckedViolations() {
        final Set<Violation> violations = new HashSet<Violation>();
        violations.add(new FilesortAndMaxqualViolation(1));
        violations.add(new HeadOrFootSectionWithoutOrderClauseViolation("p.person_id", 1));
        violations.add(new AccessToPrivateVariableFromSelectViolation("variable", 1));
        violations.add(new InvalidCnvtOnOracleFieldViolation(1));
        return violations;
    }
}