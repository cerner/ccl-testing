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
 * A {@link AnalysisRule} that identifies the following issues with select statements: 1. A head or foot section within
 * the report writer which is missing the corresponding order by clause 2. A filesort used in conjunction with a maxqual
 * statement
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
        for (final Element select : selectStatements) {

            // scan for head and foot sections without a matching order by
            final List<Element> headsAndFoots = selectNodes(select,
                    "descendant::HEADBREAK./ATTR.[position()=1] | descendant::HEADBREAK./NAME[position()=1] "
                            + "| descendant::FOOTBREAK./ATTR.[position()=1] | descendant::FOOTBREAK./NAME[position()=1]");
            final List<Element> orderClauses = selectNodes(select, "ORDERLIST.");
            List<Element> orderItems = new ArrayList<Element>();
            for (Element orderClause : orderClauses) {
                orderItems.addAll(selectNodes(orderClause, "ORDER./ATTR. | ORDER./NAME"));
            }
            for (Element headOrFoot : headsAndFoots) {
                boolean hasAMatch = false;
                for (Element orderItem : orderItems) {
                    if (elementMatches(headOrFoot, orderItem)) {
                        hasAMatch = true;
                        break;
                    }
                }
                if (!hasAMatch) {
                    violations.add(new HeadOrFootSectionWithoutOrderClauseViolation(constructName(headOrFoot),
                            getLineNumber(headOrFoot)));
                }
            }
        }

        // Scan for the use of filesort and maxqual simultaneously
        for (final Element select : selectStatements) {
            boolean hasFilesort = false;
            boolean hasMaxqual = false;

            // Look at each option within the select
            for (final Element option : selectNodesByName(select, "OPTIONS./OPTION.")) {
                if (getCclName(option).equalsIgnoreCase("FILESORT")) {
                    hasFilesort = true;
                }
                if (option.getChild("CALL.") != null
                        && getCclName(option.getChild("CALL.")).equalsIgnoreCase("MAXQUAL")) {
                    hasMaxqual = true;
                }
                if (hasMaxqual && hasFilesort) {
                    violations.add(new FilesortAndMaxqualViolation(getLineNumber(option)));
                    break;
                }
            }
        }

        // Scan for attempts to read or write to a private variable.
        for (final Element declare : getVariableDeclarations()) {
            if (isPrivateDeclare(declare)) {
                for (final Element select : selectStatements) {
                    for (final Element name : selectNodesByName(select, "NAME")) {
                        if (name.getAttributeValue("text").equalsIgnoreCase(getCclName(declare))) {
                            violations.add(new AccessToPrivateVariableFromSelectViolation(getCclName(declare),
                                    getLineNumber(name)));
                        }
                    }
                }
            }
        }

        // Scan for the use of cnvtint() or cnvtreal() on an oracle field
        for (final Element select : selectStatements) {
            for (final Element qual : selectNodesByName(select, "QUAL.")) {
                for (final Element attr : selectNodesByName(qual, "ATTR.")) {
                    if (getCclName(attr.getParentElement()).equalsIgnoreCase("CNVTINT")
                            || getCclName(attr.getParentElement()).equalsIgnoreCase("CNVTREAL")) {
                        violations.add(new InvalidCnvtOnOracleFieldViolation(getLineNumber(attr)));
                    }
                }
            }
        }

        return violations;
    }

    private boolean nameElementMatches(final Element elementOne, final Element elementTwo) {
        if (elementOne.getAttributeValue("text").equals(elementTwo.getAttributeValue("text"))) {
            return true;
        }
        return false;
    }

    private boolean elementMatches(final Element elementOne, final Element elementTwo) {
        if (!elementOne.getName().equals(elementTwo.getName())) {
            return false;
        }
        if (elementOne.getName().equals("NAME")) {
            return nameElementMatches(elementOne, elementTwo);
        }
        if (elementOne.getName().equals("ATTR.")) {
            List<Element> childrenOne = elementOne.getChildren();
            List<Element> childrenTwo = elementTwo.getChildren();
            if (childrenOne.size() != childrenTwo.size()) {
                return false;
            }
            for (int idx = 0; idx < childrenOne.size(); idx++) {
                Element childOne = childrenOne.get(idx);
                Element childTwo = childrenTwo.get(idx);
                if (!childOne.getName().equals(childTwo.getName())) {
                    return false;
                }
                if (childOne.getName().equals("NAME")) {
                    if (!nameElementMatches(childOne, childTwo)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private boolean isPrivateDeclare(final Element declare) throws JDOMException {
        for (final Element option : selectNodesByName(declare, "OPTIONS./OPTION.")) {
            if (getCclName(option).equalsIgnoreCase("PRIVATE")) {
                return true;
            }
        }
        return false;
    }

    private String constructName(final Element headOrFoot) {
        if (headOrFoot.getName().equalsIgnoreCase("NAME")) {
            return headOrFoot.getAttributeValue("text");
        }
        String name = "";
        for (final Element nameElement : headOrFoot.getChildren()) {
            if (name.equalsIgnoreCase("")) {
                name = nameElement.getAttributeValue("text");
            } else {
                name = name + "." + nameElement.getAttributeValue("text");
            }
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