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

import com.cerner.ccl.analysis.core.violations.MismatchedSubroutineDeclarationViolation;
import com.cerner.ccl.analysis.core.violations.MismatchedSubroutineInvocationViolation;
import com.cerner.ccl.analysis.core.violations.MissingSubroutineDeclarationViolation;
import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;

/**
 * A {@link AnalysisRule} that flags any defined subroutines that do not have an accompany {@code declare} statement.
 *
 * @author Joshua Hyde
 * @author Jeff Wiedemann
 * @author Albert Ponraj
 * @author Fred Eckertson
 *
 */

public class SubroutineDeclarationRules extends TimedDelegate {

    /**
     * @param document
     *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
     */
    public SubroutineDeclarationRules(final Document document) {
        super(document);
    }

    @Override
    protected Set<Violation> doMeasuredAnalysis() throws JDOMException {
        final Map<String, Element> definedSubroutines = new HashMap<String, Element>();
        final Map<String, Element> declaredSubroutines = new HashMap<String, Element>();
        final Map<String, Element> inlineDeclaredSubroutines = new HashMap<String, Element>();

        final Set<Violation> violations = new HashSet<Violation>();

        // Loop through all subroutine implementations and store off for later checks
        for (final Element subroutine : getDefinedSubroutines()) {
            final String subroutineName = getCclName(subroutine);
            definedSubroutines.put(subroutineName, subroutine);
            List<Element> names = subroutine.getChildren("NAME");
            List<Element> namespace = subroutine.getChildren("NAMESPACE.");
            if (names.size() + namespace.size() == 2) {
                inlineDeclaredSubroutines.put(subroutineName, subroutine);
            }
        }

        // Loop through all subroutine declarations and store off for later checks
        List<Element> subroutineDeclares = new ArrayList<Element>();
        subroutineDeclares
                .addAll(selectNodesByName("Z_DECLARE.", "[CALL./NAME and not(CALL./NAMESPACE./NAME)]"));
        subroutineDeclares.addAll(selectNodesByName("Z_DECLARE.", "[CALL./NAMESPACE./NAME[1]/@text]"));
        for (final Element subroutineDeclare : subroutineDeclares) {
            final String subroutineName = getCclName(subroutineDeclare.getChild("CALL."));

            if (definedSubroutines.get(subroutineName) == null) {
                // TODO - Add subroutine declared but not implemented violation
                System.out.printf("subroutine %s declared but not implemented.%n", subroutineName);
            }

            if (declaredSubroutines.get(subroutineName) != null) {
                // TODO - Duplicate subroutine declaration
                System.out.printf("subroutine %s declared more than once.%n", subroutineName);
            }

            declaredSubroutines.put(subroutineName, subroutineDeclare.getChild("CALL."));
        }

        // Loop through all subroutine invocation compare against it's implementation
        for (final Element subroutineInvocation : selectNodesByName("Z_CALL.", "[CALL./NAME[1]/@text]")) {
            final String subroutineName = getCclName(subroutineInvocation.getChild("CALL."));

            // FIXME: This is not correct. We are looking for bad invocations not declaration/implementation mismatches.
            // if declared in line, it can't be wrong.
            if (definedSubroutines.containsKey(subroutineName)
                    || definedSubroutines.containsKey("PUBLIC::" + subroutineName)) {
                // Find the invocation statement that matches the implementation name
                final Element matchingSubroutineDefined = definedSubroutines.containsKey(subroutineName)
                        ? definedSubroutines.get(subroutineName) : definedSubroutines.get("PUBLIC::" + subroutineName);

                final Element matchingSubroutineInvocation = subroutineInvocation.getChild("CALL.");

                // does the number of parameters even match? If not add the violation
                if (matchingSubroutineDefined.getChild("COMMA.").getChildren()
                        .size() != matchingSubroutineInvocation.getChildren().size() - 1) {
                    violations.add(new MismatchedSubroutineInvocationViolation(subroutineName,
                            getLineNumber(matchingSubroutineInvocation)));
                    continue;
                }
            }
        }

        // Loop through each defined subroutine and ensure that the parameters of the declaration match
        for (final Entry<String, Element> definedSubroutine : definedSubroutines.entrySet()) {
            final String subroutineName = definedSubroutine.getKey();

            if (!inlineDeclaredSubroutines.containsKey(subroutineName)) { // if declared in line, it can't be wrong.
                // Find the declare statement that matches the implementation name
                final Element matchingSubroutineDeclare = declaredSubroutines.get(subroutineName);

                // If the match could not be found then we are missing a declare
                if (matchingSubroutineDeclare == null) {
                    violations.add(new MissingSubroutineDeclarationViolation(subroutineName,
                            getLineNumber(definedSubroutine.getValue())));
                    continue;
                }

                // Otherwise we found the matching declare, does the number of parameters even match? If not add the
                // violation
                if (matchingSubroutineDeclare.getChildren().size() - 1 != definedSubroutine.getValue()
                        .getChild("COMMA.").getChildren().size()) {
                    violations.add(new MismatchedSubroutineDeclarationViolation(subroutineName,
                            getLineNumber(definedSubroutine.getValue())));
                    continue;
                }

                // The number of parameters matches, so start scanning the params and make sure each one matches. If not
                // add the violation
                boolean mismatchFound = false;
                for (int idx = 0; idx < matchingSubroutineDeclare.getChildren().size() - 1; idx++) {
                    final Element declareParam = matchingSubroutineDeclare.getChildren().get(idx + 1);
                    final Element definitionParam = definedSubroutine.getValue().getChild("COMMA.").getChildren()
                            .get(idx);

                    if (declareParam.getName().equalsIgnoreCase("NAME") && !declareParam.getAttributeValue("text")
                            .equalsIgnoreCase(definitionParam.getAttributeValue("text"))) {
                        mismatchFound = true;
                    }

                    if (declareParam.getName().equalsIgnoreCase("EQL.")
                            && !getCclName(declareParam).equalsIgnoreCase(definitionParam.getAttributeValue("text"))) {
                        mismatchFound = true;
                    }
                }

                if (mismatchFound) {
                    violations.add(new MismatchedSubroutineDeclarationViolation(subroutineName,
                            getLineNumber(definedSubroutine.getValue())));
                }
            }

        }

        return violations;
    }

    @Override
    public Set<Violation> getCheckedViolations() {
        final Set<Violation> violations = new HashSet<Violation>();
        violations.add(new MismatchedSubroutineDeclarationViolation("subroutine", 1));
        violations.add(new MissingSubroutineDeclarationViolation("subroutine", 1));
        violations.add(new MismatchedSubroutineInvocationViolation("subroutine", 1));
        return violations;
    }
}
