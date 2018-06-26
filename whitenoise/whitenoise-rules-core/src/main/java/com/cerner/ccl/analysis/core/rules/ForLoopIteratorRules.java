package com.cerner.ccl.analysis.core.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;

import com.cerner.ccl.analysis.core.violations.OverwrittenForLoopIteratorViolation;
import com.cerner.ccl.analysis.core.violations.UnreferencedForLoopIteratorViolation;
import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;

/**
 * An {@link AnalysisRule} that identifies for loops which do not reference the running variable or that increment or
 * decrement the running variable. It is acceptable to reference an alias which references the running variable rather
 * than directly referencing the running variable. If a name space is used it must be used consistently.
 *
 * @author Joshua Hyde
 * @author Jeff Wiedemann
 * @author Fred Eckertson
 */

public class ForLoopIteratorRules extends TimedDelegate {
    private List<Element> documentAliases;

    /**
     * @param document
     *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
     */
    public ForLoopIteratorRules(final Document document) {
        super(document);
    }

    @Override
    protected Set<Violation> doMeasuredAnalysis() throws JDOMException {
        final Set<Violation> violations = new HashSet<Violation>();

        final List<Element> forLoops = selectNodesByName("FOR.");

        for (final Element forLoop : forLoops) {
            String iteratorName = getCclName(selectNodes(forLoop, "./COMMA.").iterator().next());
            String[] nameParts = iteratorName.split("::");
            boolean isNamespaced = nameParts.length == 1 ? false : true;
            String name = isNamespaced ? nameParts[1] : nameParts[0];
            String namespace = isNamespaced ? nameParts[0] : "";

            // determine if the for loop references either the iterator or an alias that references the iterator.
            List<Element> references = isNamespaced
                    ? selectNodes(forLoop,
                            "./*[position() > 1]/descendant::NAMESPACE.[NAME[position()=1 and @text = '" + namespace
                                    + "'] and NAME[position()=2 and @text = '" + name + "']]")
                    : selectNodes(forLoop, "./*[position() > 1]/descendant::NAME[@text = '" + name + "']");

            if (references.isEmpty() && !forLoopContainsAlias(forLoop, getAliasesForVariable(name, namespace))) {
                violations.add(new UnreferencedForLoopIteratorViolation(iteratorName, getLineNumber(forLoop)));
            }

            // determine if the iterator gets incremented or decremented inside the for loop.
            List<Element> iteratorIncrements = isNamespaced
                    ? selectNodes(forLoop,
                            "./descendant::PLUS.[preceding-sibling::NAMESPACE.[NAME[position()=1 and @text = '"
                                    + namespace + "'] and NAME[position()=2 and @text = '" + name
                                    + "']] and NAMESPACE.[NAME[position()=1 and @text = '" + namespace
                                    + "'] and NAME[position()=2 and @text = '" + name + "']]]")
                    : selectNodes(forLoop, "./descendant::PLUS.[preceding-sibling::NAME/@text = '" + iteratorName
                            + "' and (NAME/@text = '" + iteratorName + "')]");
            if (!iteratorIncrements.isEmpty()) {
                violations.add(new OverwrittenForLoopIteratorViolation(iteratorName,
                        getLineNumber(iteratorIncrements.iterator().next())));
            } else {
                List<Element> iteratorDecrements = isNamespaced
                        ? selectNodes(forLoop,
                                "./descendant::MINUS.[preceding-sibling::NAMESPACE.[NAME[position()=1 and @text = '"
                                        + namespace + "'] and NAME[position()=2 and @text = '" + name
                                        + "']] and NAMESPACE.[NAME[position()=1 and @text = '" + namespace
                                        + "'] and NAME[position()=2 and @text = '" + name + "']]]")
                        : selectNodes(forLoop, "./descendant::MINUS.[preceding-sibling::NAME/@text = '" + iteratorName
                                + "' and (NAME/@text = '" + iteratorName + "')]");
                if (!iteratorDecrements.isEmpty()) {
                    violations.add(new OverwrittenForLoopIteratorViolation(iteratorName,
                            getLineNumber(iteratorDecrements.iterator().next())));
                }
            }

        }

        return violations;
    }

    /**
     * Determines if a for loop references an alias from a given list of alias elements.
     *
     * @param forLoop
     *            The for loop element.
     * @param aliases
     *            The list of aliases.
     * @return A boolean flag indicating whether the for loop references one of the aliases.
     * @throws JDOMException
     *             A jdom exception could occur if there are issues with the document.
     */
    private boolean forLoopContainsAlias(Element forLoop, List<Element> aliases) throws JDOMException {
        for (Element alias : aliases) {
            String aliasName = getCclName(selectNodes(alias, "./IS.").iterator().next());
            String nameParts[] = aliasName.split("::");
            boolean isNamespaced = nameParts.length == 1 ? false : true;
            String name = isNamespaced ? nameParts[1] : nameParts[0];
            String namespace = isNamespaced ? nameParts[0] : "";

            if (isNamespaced) {
                if (!selectNodes(forLoop, ".//MEMBER./NAMESPACE.[NAME[position()=1 and @text = '" + namespace
                        + "'] and NAME[position()=2 and @text = '" + name + "']]").isEmpty()) {
                    return true;
                }
            } else {
                if (!selectNodes(forLoop, ".//MEMBER.[NAME[1]/@text = '" + name + "']").isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Identifies the document aliases which reference a given variable.
     *
     * @param variableName
     *            The name of the variable to check for.
     * @param variableNamespace
     *            The namespace for the variable if it has one.
     * @return The list of document aliases which reference the variable.
     * @throws JDOMException
     *             A jdom exception could occur if there are issues with the document.
     */
    private List<Element> getAliasesForVariable(String variableName, String variableNamespace) throws JDOMException {
        List<Element> result = new ArrayList<Element>();
        List<Element> aliases = lazyLoadAliases();

        if (aliases.isEmpty())
            return result;

        for (Element alias : aliases) {
            if (variableNamespace.isEmpty()) {
                if (!selectNodes(alias, "./IS./MEMBER./*[NAME/@text = '" + variableName + "']").isEmpty()) {
                    result.add(alias);
                }
            } else {

                if (!selectNodes(alias, "./IS./MEMBER./*[NAMESPACE.[NAME[position()=1 and @text = '" + variableNamespace
                        + "'] and NAME[position()=2 and @text = '" + variableName + "']]]").isEmpty()) {
                    result.add(alias);
                }
            }
        }
        return result;
    }

    /**
     * Identifies all aliases defined within a given CCL program. The results are cached to avoid repeating the search
     * multiple times.
     *
     * @param document
     *            The translation of the CCL program in XML format.
     * @return A list of all the aliases defined in the program.
     * @throws JDOMException
     */
    private List<Element> lazyLoadAliases() throws JDOMException {
        if (documentAliases != null)
            return documentAliases;

        documentAliases = new ArrayList<Element>();
        for (Element name : selectNodesByName("NAME", "[@text='CURALIAS']"))
            documentAliases.add(name.getParentElement());

        return documentAliases;
    }

    @Override
    public Set<Violation> getCheckedViolations() {
        Set<Violation> violations = new HashSet<Violation>();
        violations.add(new UnreferencedForLoopIteratorViolation("i", 1));
        violations.add(new OverwrittenForLoopIteratorViolation("i", 1));
        return violations;
    }

}