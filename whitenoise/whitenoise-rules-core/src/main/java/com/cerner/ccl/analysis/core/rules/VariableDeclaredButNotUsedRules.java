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

            if (isUsed(variableName, scope)) {
                continue;
            }
            violations.add(new VariableDeclaredButNotUsedViolation(variableName, getLineNumber(variableDeclaration)));
        }

        return violations;
    }

    /**
     * Checks if a variable name declared is used at or below a given subroutine scope. The intended use is for the
     * variable to be declared within the provided subroutine.
     *
     * @param variableName
     *            The name of the variable to check.
     * @param scope
     *            The target subroutine scope.
     * @return A boolean flag indicating whether the variable gets used.
     * @throws JDOMException
     */
    private boolean isUsed(final String variableName, final Element scope) throws JDOMException {
        String namespace = variableName.contains("::") ? variableName.substring(0, variableName.indexOf("::")) : "";
        String simpleName = variableName.replaceAll("\\w+::", "");
        Set<Element> usages = new HashSet<Element>();
        if (namespace.equals("PUBLIC") || namespace.isEmpty()) {
            // //NAME[
            // @text='THE_NAME'
            // and not(parent::Z_DECLARE.)
            // and not(parent::NAMESPACE.)
            // and not(ancestor::Z_SET.[NAME[@text='THE_NAME' and not(preceding-sibling::*)]])
            // and not(ancestor::IS.[NAME[@text='THE_NAME' and not(preceding-sibling::*)]])
            // and not(ancestor::Z_SET.[NAMESPACE.[NAME[position()=1 and @text='PUBLIC'] and NAME[position()=2 and
            // @text='THE_NAME'] and not(preceding-sibling::*)]])
            // and not(ancestor::IS.[NAMESPACE.[NAME[position()=1 and @text='PUBLIC'] and NAME[position()=2 and
            // @text='THE_NAME'] and not(preceding-sibling::*)]])
            // ]
            usages.addAll(selectNodes("//NAME[@text='" + simpleName + "' and not(parent::Z_DECLARE.)"
                    + " and not(parent::NAMESPACE.)" + " and not(ancestor::Z_SET.[NAME[@text='" + simpleName
                    + "' and not(preceding-sibling::*)]]) and not(ancestor::IS.[NAME[@text='" + simpleName
                    + "' and not(preceding-sibling::*)]])"
                    + " and not(ancestor::Z_SET.[NAMESPACE.[NAME[position()=1 and @text='PUBLIC'] and NAME[position()=2 and @text='"
                    + simpleName + "'] and not(preceding-sibling::*)]])"
                    + " and not(ancestor::IS.[NAMESPACE.[NAME[position()=1 and @text='PUBLIC'] and NAME[position()=2 and @text='"
                    + simpleName + "'] and not(preceding-sibling::*)]])]"));
        }
        if (!namespace.isEmpty()) {
            // //NAME[
            // @text='THE_NAME'
            // and parent::NAMESPACE.[NAME[position()=1 and @text='THE_NAMESPACE'] and NAME[position()=2 and
            // @text='THE_NAME']]
            // and not(../parent::Z_DECLARE.)
            // and not(ancestor::Z_SET.[NAMESPACE.[NAME[position()=1 and @text='THE_NAMESPACE'] and NAME[position()=2
            // and @text='THE_NAME'] and not(preceding-sibling::*)]])
            // and not(ancestor::IS.[NAMESPACE.[NAME[position()=1 and @text='THE_NAMESPACE'] and NAME[position()=2 and
            // @text='THE_NAME'] and not(preceding-sibling::*)]])
            // and not(ancestor::Z_SET.[NAME[@text='THE_NAME' and not(preceding-sibling::*)]]) //only for PUBLIC
            // namespace
            // and not(ancestor::IS.[NAME[@text='THE_NAME' and not(preceding-sibling::*)]]) //only for PUBLIC namespace
            // ]
            String nakedCheck = namespace.equals("PUBLIC") ? " and not(ancestor::Z_SET.[NAME[@text='" + simpleName
                    + "' and not(preceding-sibling::*)]]) and not(ancestor::IS.[NAME[@text='" + simpleName
                    + "' and not(preceding-sibling::*)]])" : "";
            usages.addAll(
                    selectNodes("//NAME[@text='" + simpleName + "' and parent::NAMESPACE.[NAME[position()=1 and @text='"
                            + namespace + "'] and NAME[position()=2 and @text='" + simpleName
                            + "']] and not(../parent::Z_DECLARE.) and not(ancestor::Z_SET.[NAMESPACE.[NAME[position()=1 and @text='"
                            + namespace + "'] and NAME[position()=2 and @text='" + simpleName
                            + "'] and not(preceding-sibling::*)]]) and not(ancestor::IS.[NAMESPACE.[NAME[position()=1 and @text='"
                            + namespace + "'] and NAME[position()=2 and @text='" + simpleName
                            + "'] and not(preceding-sibling::*)]])" + nakedCheck + "]"));
        }
        Set<Element> usageScopes = new HashSet<Element>();
        for (Element usage : usages) {
            usageScopes.add(getScope(usage));
        }
        return isUsedByScope(variableName, scope, usageScopes, new HashSet<Element>());
    }

    /**
     * A recursive function that checks if a given target subroutine uses a specified variable by checking if the target
     * subroutine belongs to a set of subroutines whose downward closure in the call graph contains all uses of the
     * variable which have not been show to be outside of the calling scope of the target subroutine by a given set of
     * complimentary scopes owning or leading to the declaration of any other variable with the same name. <br/>
     * In practice this method should be passed the name of a variable, the scope for a subroutine which declares a
     * variable of that name, the set scopes for subroutines which use a variable of that name and an empty exclusion
     * list. The method will invoke itself recursively looking upward in the call graph till it finds the target scope
     * or runs out of options decreasing the usage scopes and exclusion scopes as it goes along.
     *
     * @param variableName
     *            The name of the variable.
     * @param scope
     *            The scope of the target subroutine.
     * @param usageScopes
     *            The set of subroutine scopes containing or leading to uses of a variable with the given name whose
     *            declaring scope is not yet known.
     * @param previousScopes
     *            A list of subroutine scopes known to contain or lead to the declaration for any usage of the given
     *            variable name that is not in the closure of the usage scopes.
     * @return A boolean flag indicating whether the variable gets used by the subroutine.
     * @throws JDOMException
     */
    private boolean isUsedByScope(final String variableName, final Element scope, final Set<Element> usageScopes,
            final Set<Element> previousScopes) throws JDOMException {
        if (usageScopes.isEmpty()) {
            return false;
        }
        if (usageScopes.contains(scope)) {
            return true;
        }
        for (Element usageScope : usageScopes) {
            List<Element> localDeclarations = selectNodes(usageScope,
                    ".//Z_DECLARE./NAME[@text='" + variableName + "']");
            if (localDeclarations.size() > 0) {
                // don't look upwards from this scope since it declares its own version and it is not the target scope.
                previousScopes.add(usageScope);
            }
        }
        usageScopes.removeAll(previousScopes);
        Set<Element> callingScopes = new HashSet<Element>();
        for (Element usageScope : usageScopes) {
            Set<Element> scopeCallers = getInverseCallGraph().get(usageScope);
            if (scopeCallers != null) {
                callingScopes.addAll(scopeCallers);
            }
        }
        previousScopes.addAll(usageScopes);
        callingScopes.removeAll(previousScopes);
        return isUsedByScope(variableName, scope, callingScopes, previousScopes);
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
