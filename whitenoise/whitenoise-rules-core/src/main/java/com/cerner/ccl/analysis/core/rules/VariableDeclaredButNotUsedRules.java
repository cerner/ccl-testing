package com.cerner.ccl.analysis.core.rules;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.cerner.ccl.analysis.core.violations.VariableDeclaredButNotUsedViolation;
import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;

/**
 * An {@link AnalysisRule} that flags any references to variables that do not have a {@code declare} statement.
 *
 * @author Jeff Wiedemann
 *
 */

public class VariableDeclaredButNotUsedRules extends TimedDelegate {

    /**
     * @param document
     *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
     */
    public VariableDeclaredButNotUsedRules(final Document document) {
        super(document);
    }

    @Override
    protected Set<Violation> doMeasuredAnalysis() throws JDOMException {
        final Set<Violation> violations = new HashSet<Violation>();

        for (final Element variableDeclaration : getVariableDeclarations()) {
            // Allow constant variables to be declared and never used...
            // this is somewhat typical for large INC files that serve multiple purposes
            if (isConstantVariable(variableDeclaration)) {
                continue;
            }

            String variableName = getCclName(variableDeclaration);
            Element scope = getScope(variableDeclaration);
            if (isUsed(variableName, scope, new HashSet<Element>(), true)) {
                continue;
            }
            violations.add(new VariableDeclaredButNotUsedViolation(variableName, getLineNumber(variableDeclaration)));
        }

        return violations;
    }

    private boolean isUsed(final String variableName, final Element scope, final Set<Element> previouslyCheckedScopes,
            final boolean allowDeclaration) throws JDOMException {
        if (!allowDeclaration) {
            List<Element> localDeclarations = selectNodes(scope, ".//Z_DECLARE./NAME[@text='" + variableName + "']");
            if (localDeclarations.size() > 0) {
                return false;
            }
        }
        List<Element> uses = selectNodes(scope, ".//NAME[@text='" + variableName
                + "' and not(parent::Z_DECLARE.) and not(ancestor::Z_SET.[NAME[position()=1 and @text='" + variableName
                + "']]) and not(ancestor::IS.[NAME[position()=1 and @text='" + variableName + "']])]");
        for (Element use : uses) {
            if (getScope(use).equals(scope)) {
                return true;
            }
        }
        Set<Element> calledScopes = getCallGraph().get(scope);
        if (calledScopes != null) {
            for (Element calledScope : calledScopes) {
                if (!previouslyCheckedScopes.contains(calledScope)) {
                    previouslyCheckedScopes.add(calledScope);
                    if (isUsed(variableName, calledScope, previouslyCheckedScopes, false)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isConstantVariable(final Element declare) throws JDOMException {
        for (final Element option : selectNodesByName(declare, "OPTION.")) {
            if (!option.getParentElement().getName().equalsIgnoreCase("OPTIONS.")) {
                continue;
            }

            if (option.getChild("CALL.") != null && getCclName(option.getChild("CALL.")).equalsIgnoreCase("CONSTANT")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<Violation> getCheckedViolations() {
        final Set<Violation> violations = new HashSet<Violation>();
        violations.add(new VariableDeclaredButNotUsedViolation("variable", 1));
        return violations;
    }
}
