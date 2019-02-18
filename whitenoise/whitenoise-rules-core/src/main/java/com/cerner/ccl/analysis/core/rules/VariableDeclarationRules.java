package com.cerner.ccl.analysis.core.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.cerner.ccl.analysis.core.violations.MissingVariableDeclarationViolation;
import com.cerner.ccl.analysis.core.violations.UnknownDeclareOptionViolation;
import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;

/**
 * An {@link AnalysisRule} that flags any references to variables that do not have an accompany {@code declare}
 * statement. Also identifies invalid declare options used for variables
 *
 * @author Joshua Hyde
 * @author Dee Adesanwo
 * @author Fred Eckertson
 */

public class VariableDeclarationRules extends TimedDelegate {

    /**
     * @param document
     *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
     */
    public VariableDeclarationRules(final Document document) {
        super(document);
    }

    @Override
    protected Set<Violation> doMeasuredAnalysis() throws JDOMException {
        final Set<Violation> violations = new HashSet<Violation>();
        final Map<String, List<Element>> variableUsages = new HashMap<String, List<Element>>();
        final Map<String, Set<Element>> variableDeclarationScopes = new HashMap<String, Set<Element>>();

        for (final Element e : getVariableDeclarations()) {
            String variableName = getCclName(e);
            if (!variableDeclarationScopes.containsKey(variableName)) {
                variableDeclarationScopes.put(variableName, new HashSet<Element>());
            }
            variableDeclarationScopes.get(variableName).add(getScope(e));
            checkHasUnknownDeclaredOption(e, variableName, violations);
        }
        for (final Element e : getSubroutineParameterDeclarations()) {
            String variableName = e.getAttributeValue("text");
            if (!variableDeclarationScopes.containsKey(variableName)) {
                variableDeclarationScopes.put(variableName, new HashSet<Element>());
            }
            variableDeclarationScopes.get(variableName).add(getScope(e));
        }

        List<Element> setVariables = selectNodesByName(
                "Z_SET./NAME[" + "not(preceding-sibling::*) and not(@text='TRACE') and not(@text='MODIFY')"
                        + " and not(@text='MESSAGE') and not(@text='STAT') and not(@text='COMPILE')"
                        + " and not(@text='LOGICAL') and not(@text='CURALIAS')]");
        for (final Element setVariable : setVariables) {
            String variableName = setVariable.getAttributeValue("text");

            if (!variableUsages.containsKey(variableName)) {
                variableUsages.put(variableName, new ArrayList<Element>());
            }
            variableUsages.get(variableName).add(setVariable);
        }

        for (final Entry<String, List<Element>> usedVariableInstances : variableUsages.entrySet()) {
            String variableName = usedVariableInstances.getKey();
            for (Element usage : usedVariableInstances.getValue()) {
                if (!variableDeclarationScopes.containsKey(variableName)) {
                    violations.add(new MissingVariableDeclarationViolation(variableName, getLineNumber(usage)));
                } else {
                    Set<Element> declaringScopes = new HashSet<Element>();
                    declaringScopes.addAll(variableDeclarationScopes.get(variableName));
                    declaringScopes.retainAll(getScopes(usage));
                    if (declaringScopes.isEmpty()) {
                        violations.add(new MissingVariableDeclarationViolation(variableName, getLineNumber(usage)));
                    }
                }
            }
        }
        return violations;
    }

    private void checkHasUnknownDeclaredOption(final Element record, final String variableName,
            final Set<Violation> violations) {
        if (record.getChild("OPTIONS.") == null) {
            return;
        }

        List<Element> optionvalues = record.getChild("OPTIONS.").getChildren("OPTION.");

        for (Element option : optionvalues) {
            String declaredOption = "";
            if (option.getChild("CALL.") == null) {
                declaredOption = option.getChild("NAME").getAttributeValue("text");
            } else {
                declaredOption = option.getChild("CALL.").getChild("NAME").getAttributeValue("text");
            }

            if (evaluateDecalredOption(declaredOption)) {
                violations.add(new UnknownDeclareOptionViolation(variableName, getLineNumber(record)));
            }
        }
    }

    private boolean evaluateDecalredOption(final String value) {
        if (value.equalsIgnoreCase("PROTECT") || value.equalsIgnoreCase("NOCONSTANT")
                || value.equalsIgnoreCase("CONSTANT") || value.equalsIgnoreCase("PUBLIC")
                || value.equalsIgnoreCase("PERSISTSCRIPT") || value.equalsIgnoreCase("PERSIST")
                || value.equalsIgnoreCase("PRIVATE") || value.equalsIgnoreCase("PRIVATEPROTECT")
                || value.equalsIgnoreCase("NOPERSIST")) {
            return false;
        }

        return true;
    }

    @Override
    public Set<Violation> getCheckedViolations() {
        final Set<Violation> violations = new HashSet<Violation>();
        violations.add(new UnknownDeclareOptionViolation("variable", 1));
        violations.add(new MissingVariableDeclarationViolation("variable", 1));
        return violations;
    }
}
