package com.cerner.ccl.analysis.jdom;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.discovery.resource.ClassLoaders;
import org.apache.commons.discovery.tools.SPInterface;
import org.apache.commons.discovery.tools.Service;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathFactory;

import com.cerner.ccl.analysis.data.AnalysisRule;
import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.exception.AnalysisRuleProvider;

/**
 * Skeleton definition of JDOM extension of {@link AnalysisRule}.
 * <p>
 * This implementation will take the XML given for {@link #analyze(String) analysis}, convert it to a {@link Document},
 * and then use an SPI lookup to find {@link Delegate delegates} and {@link Delegate#analyze() invoke} their analysis.
 * <p>
 * In order to for a {@link Delegate} to be used by this rule, include a file into your assembly called
 * {@code /META-INF/services/com.cerner.ccl.analysis.jdom.JdomAnalysisRule$Delegate}. Within that file, place the
 * canonical class names of each implementation you wish to be used by this analysis rule. At runtime, this rule will be
 * picked up using the {@link AnalysisRuleProvider} and, in turn, will pick up all provided implementations of
 * {@link Delegate}.
 * <p>
 * All implementations of {@link Delegate} to be used using this rule must implement a no-argument constructor, either
 * explicitly or implicitly.
 *
 * @author Joshua Hyde
 * @author Jeff Wiedemann
 * @author Fred Eckertson
 *
 */

public class JdomAnalysisRule implements AnalysisRule {
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Set<Violation> analyze(final String prgXml) {
        final Set<Violation> violations = new HashSet<Violation>();
        try {
            final ClassLoaders classLoaders = new ClassLoaders();
            final Class<Document>[] classes = new Class[] { Document.class };
            final Document[] objects = new Document[] { new SAXBuilder().build(new StringReader(prgXml)) };
            classLoaders.put(Document.class.getClassLoader());
            final Enumeration<Delegate> delegates = Service.providers(new SPInterface(Delegate.class, classes, objects),
                    classLoaders);
            if (delegates != null) {
                while (delegates.hasMoreElements()) {
                    violations.addAll(delegates.nextElement().analyze());
                }
            }
        } catch (final JDOMException | IOException e) {
            throw new JdomException("Failed to analyze XML.", e);
        }
        return violations;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Set<Violation> getCheckedViolations() {
        final Set<Violation> violations = new HashSet<Violation>();

        final ClassLoaders classLoaders = new ClassLoaders();
        final Class<Document>[] classes = new Class[] { Document.class };
        final Document[] objects = new Document[] { new Document() };
        classLoaders.put(Document.class.getClassLoader());
        final Enumeration<Delegate> delegates = Service.providers(new SPInterface(Delegate.class, classes, objects),
                classLoaders);
        while (delegates.hasMoreElements()) {
            violations.addAll(delegates.nextElement().getCheckedViolations());
        }

        return violations;
    }

    /**
     * A delegate to which this analysis rule delegates the actual work of analysis.
     *
     * @author Joshua Hyde
     * @author Fred Eckertson
     *
     */
    public static abstract class Delegate {
        private final Document document;
        private List<Element> declaredVariables = null;
        private List<Element> definedSubroutines = null;
        private Map<String, Element> definedSubroutineMap = null;
        private Map<Element, Set<Element>> callGraph = null;
        private Map<Element, Set<Element>> inverseCallGraph = null;
        private Map<String, Set<String>> nameCallGraph = null;
        private Map<String, Set<String>> inverseNameCallGraph = null;

        /**
         * @param document
         *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
         */
        public Delegate(final Document document) {
            this.document = document;
        }

        /**
         * Returns the set of violations which will be checked by this rule during analysis regardless of whether or not
         * the violation is identified during analysis
         *
         * @return A {@link Set} of {@link Violation} objects representing the violations which were checked during
         *         analysis
         */
        public abstract Set<Violation> getCheckedViolations();

        /**
         * Analyze the document for potential errors in code.
         *
         * @return A {@link Set} of {@link Violation} objects to be added to the sum of all delegates' reported
         *         violations.
         * @throws JDOMException
         *             If any errors occur during the analysis of the XML.
         */
        protected abstract Set<Violation> analyze() throws JDOMException;

        /**
         * Returns the string associated with the elements NAME/@text child attribute which is used heavily in CCL XML
         * to quickly locate the name of a variable, subroutine, etc.
         *
         * @param e
         *            An {@link Element} representing the item to return the name of
         * @return The CCL name as defined by the 'text' attribute of the elements first 'NAME' child
         */
        protected String getCclName(final Element e) {
            StringBuilder sb = new StringBuilder();
            Element namespace = e.getChild("NAMESPACE.");
            if (namespace != null) {
                Iterator<Element> it = namespace.getChildren("NAME").iterator();
                while (it.hasNext()) {
                    Element child = it.next();
                    sb.append(child.getAttributeValue("text"));
                    sb.append("::");
                }
                return sb.length() > 0 ? sb.substring(0, sb.length() - 2) : "";

            }
            Element nameElement = e.getChild("NAME");
            return nameElement != null ? nameElement.getAttributeValue("text") : "";
        }

        /**
         * Returns the list of all {@code SUBROUTINE.} elements in the program.
         *
         * @return A {@link List} of {@link Element} objects representing all subroutine implementations defined in the
         *         program
         * @throws JDOMException
         *             If any errors occur during the analysis.
         */
        protected List<Element> getDefinedSubroutines() throws JDOMException {
            if (this.definedSubroutines == null) {
                List<Element> definedSubroutines = new ArrayList<Element>();
                for (final Element e : selectNodesByName("SUBROUTINE.")) {
                    definedSubroutines.add(e);
                }
                this.definedSubroutines = Collections.unmodifiableList(definedSubroutines);
            }

            return definedSubroutines;
        }

        /**
         * Retrieves the map of subroutine names to subroutine definition elements for a program.
         *
         * @return The map of subroutine names to subroutine definition elements for a program.
         * @throws JDOMException
         *             The exception thrown if errors occur while parsing the document.
         */
        protected Map<String, Element> getSubroutineMap() throws JDOMException {
            if (this.definedSubroutineMap == null) {
                definedSubroutineMap = new HashMap<String, Element>();
                for (final Element e : getDefinedSubroutines()) {
                    definedSubroutineMap.put(getCclName(e), e);
                }
                this.definedSubroutineMap = Collections.unmodifiableMap(definedSubroutineMap);
            }

            return this.definedSubroutineMap;
        }

        /**
         * Retrieves the subroutine definition corresponding to a given subroutineName.
         *
         * @param subroutineName
         *            The name of the subroutine whose definition should be retrieved.
         * @return The element defining the subroutine or null if there is no definition for the subroutine.
         * @throws JDOMException
         *             The exception thrown if errors occur while parsing the document.
         */
        protected Element getSubroutineDefinition(final String subroutineName) throws JDOMException {
            Map<String, Element> subroutineMap = getSubroutineMap();
            if (subroutineMap.containsKey(subroutineName)) {
                return subroutineMap.get(subroutineName);
            }
            return subroutineMap.get("PUBLIC::" + subroutineName);
        }

        /**
         * Returns the call graph for a program, that is a map from the program element and all defined subroutines in
         * the program to the set of subroutines invoked by that element.
         *
         * @return The {@link Map} of {@link Element} representing the program and all subroutine implementations
         *         defined in the program to the subroutine elements invoked by that element.
         * @throws JDOMException
         *             If any errors occur during the analysis.
         */
        protected Map<Element, Set<Element>> getCallGraph() throws JDOMException {
            if (this.callGraph == null) {
                callGraph = new HashMap<Element, Set<Element>>();
                callGraph.put(document.getRootElement(),
                        getInvokedElements(document.getRootElement(), "[not(ancestor::SUBROUTINE.)]"));
                for (Element invokingElement : getDefinedSubroutines()) {
                    callGraph.put(invokingElement, getInvokedElements(invokingElement, ""));
                }
                this.callGraph = Collections.unmodifiableMap(callGraph);
            }
            return this.callGraph;
        }

        /**
         * Returns the inverse call graph for a program, that is a map from each defined subroutines in the program to
         * the set of subroutines that invoke that element.
         *
         * @return The {@link Map} of {@link Element} representing all subroutine implementations defined in the program
         *         to the subroutine elements that invoke that element.
         * @throws JDOMException
         *             If any errors occur during the analysis.
         */
        protected Map<Element, Set<Element>> getInverseCallGraph() throws JDOMException {
            if (this.inverseCallGraph == null) {
                Map<Element, Set<Element>> inverseCallGraph = new HashMap<Element, Set<Element>>();
                Map<Element, Set<Element>> callGraph = getCallGraph();
                for (Entry<Element, Set<Element>> entry : callGraph.entrySet()) {
                    for (Element invokee : entry.getValue()) {
                        if (!inverseCallGraph.containsKey(invokee)) {
                            inverseCallGraph.put(invokee, new HashSet<Element>());
                        }
                        inverseCallGraph.get(invokee).add(entry.getKey());
                    }
                }
                this.inverseCallGraph = Collections.unmodifiableMap(inverseCallGraph);
            }
            return this.inverseCallGraph;
        }

        /**
         * Returns the call graph for a program, that is a map from the program and all defined subroutines in the
         * program to the set of subroutine names invoked by that element.
         *
         * @return The {@link Map} of {@link String} representing the program and all subroutine implementations defined
         *         in the program to the names of the subroutine elements invoked by that element.
         * @throws JDOMException
         *             If any errors occur during the analysis.
         */
        protected Map<String, Set<String>> getNameCallGraph() throws JDOMException {
            if (this.nameCallGraph == null) {
                nameCallGraph = new HashMap<String, Set<String>>();
                nameCallGraph.put("ZC_PROGRAM.",
                        getInvokedElementNames(document.getRootElement(), "[not(ancestor::SUBROUTINE.)]"));
                Map<String, Element> subroutineMap = getSubroutineMap();
                for (Entry<String, Element> entry : subroutineMap.entrySet()) {
                    nameCallGraph.put(entry.getKey(), getInvokedElementNames(entry.getValue(), ""));
                }
                this.nameCallGraph = Collections.unmodifiableMap(nameCallGraph);
            }
            return this.nameCallGraph;
        }

        /**
         * Returns the inverse call graph for a program, that is a map of all subroutines defined in the program to the
         * set of call graph nodes that invoke that subroutine.
         *
         * @return The {@link Map} of {@link String} representing the program and all subroutine implementations defined
         *         in the program to the subroutine element invoked by that element.
         * @throws JDOMException
         *             If any errors occur during the analysis.
         */
        protected Map<String, Set<String>> getInverseNameCallGraph() throws JDOMException {
            if (this.inverseNameCallGraph == null) {
                inverseNameCallGraph = new HashMap<String, Set<String>>();
                Map<String, Set<String>> sourceCallGraph = getNameCallGraph();
                for (Entry<String, Set<String>> entry : sourceCallGraph.entrySet()) {
                    for (String invokee : entry.getValue()) {
                        if (!inverseNameCallGraph.containsKey(invokee)) {
                            inverseNameCallGraph.put(invokee, new HashSet<String>());
                        }
                        inverseNameCallGraph.get(invokee).add(entry.getKey());
                    }
                }
                inverseNameCallGraph.remove("ZC_PROGRAM.");
                this.inverseNameCallGraph = Collections.unmodifiableMap(inverseNameCallGraph);
            }
            return this.inverseNameCallGraph;
        }

        private Set<String> getInvokedElementNames(final Element invokingElement, final String predicate)
                throws JDOMException {
            List<Element> invokedElements = selectNodes(invokingElement,
                    "descendant::Z_CALL.[NAME]|descendant::CALL.[not(parent::Z_DECLARE.)]" + predicate);
            Set<String> invokedElementNames = new HashSet<String>();
            for (Element invokedElement : invokedElements) {
                String cclName = getCclName(invokedElement);
                if (!cclName.equals("CONSTANT") && !cclName.equals("NOCONSTANT")) {
                    invokedElementNames.add(cclName);
                }
            }
            return invokedElementNames;
        }

        private Set<Element> getInvokedElements(final Element invokingElement, final String predicate)
                throws JDOMException {
            getSubroutineMap();
            List<Element> callElements = selectNodes(invokingElement,
                    "descendant::Z_CALL.[NAME]|descendant::CALL.[not(parent::Z_DECLARE.)]" + predicate);
            Set<Element> invokedElements = new HashSet<Element>();
            Iterator<Element> it = callElements.iterator();
            while (it.hasNext()) {
                Element invokedElement = it.next();
                String cclName = getCclName(invokedElement);
                if (!cclName.equals("CONSTANT") && !cclName.equals("NOCONSTANT")) {
                    invokedElements.add(getSubroutineDefinition(cclName));
                }
            }
            invokedElements.remove(null);
            return invokedElements;
        }

        /**
         * Retrieve the line number, if any, in the given element.
         *
         * @param element
         *            The {@link Element} from which the line number is to be parsed.
         * @return {@code null} if there is no or insufficient information about the line number; otherwise, an
         *         {@link Integer} representing the line number.
         */
        protected Integer getLineNumber(final Element element) {
            final Attribute attribute = element.getAttribute("loc");
            if (attribute == null) {
                return null;
            }

            final String attributeText = attribute.getValue();
            // If debug mode is enabled, then loc will be something like "0.0"
            if (attributeText.startsWith("0")) {
                return null;
            }

            return Integer.parseInt(attributeText.substring(0, attributeText.lastIndexOf('.')));
        }

        /**
         * Returns the list of all {@code Z_DECLARE.} elements in the program which are declaring variables (as opposed
         * to subroutines).
         *
         * @return A {@link List} of {@link Element} objects representing all variable declarations in the program
         * @throws JDOMException
         *             If any errors occur during the analysis.
         */
        protected List<Element> getVariableDeclarations() throws JDOMException {
            if (this.declaredVariables == null) {
                declaredVariables = new ArrayList<Element>();
                for (final Element e : selectNodesByName("Z_DECLARE.", "[NAME and not(CALL.)]")) {
                    declaredVariables.add(e);
                }
                this.declaredVariables = Collections.unmodifiableList(declaredVariables);
            }

            return this.declaredVariables;
        }

        /**
         * Select all attributes matching a given XPath expression within the XML document.
         *
         * @param expression
         *            The XPath expression to be evaluated.
         * @return A {@link List} of {@link Attribute} objects representing all XML element attributes that match the
         *         given XPath expression.
         * @throws JDOMException
         *             If any errors occur during the analysis.
         */
        protected List<Attribute> selectAttributes(final String expression) throws JDOMException {
            return selectAttributeInternal(document, expression);
        }

        /**
         * Select all attributes matching a given XPath expression starting at the given element.
         *
         * @param root
         *            An {@link Element} representing the XML element from which the XPath expression should start
         *            searching.
         * @param expression
         *            The XPath expression to be evaluated.
         * @return A {@link List} of {@link Attribute} objects representing all XML element attributes that match the
         *         given XPath expression.
         * @throws JDOMException
         *             If any errors occur during the analysis.
         */
        protected List<Attribute> selectAttributes(final Element root, final String expression) throws JDOMException {
            return selectAttributeInternal(root, expression);
        }

        /**
         * Select all nodes matching an XPath expression with the XML document.
         *
         * @param expression
         *            The XPath expression to be evaluated.
         * @return A {@link List} of {@link Element} objects representing all elements that match the given XPath
         *         expression.
         * @throws JDOMException
         *             If any errors occur during the analysis.
         */
        protected List<Element> selectNodes(final String expression) throws JDOMException {
            return selectNodesInternal(document, expression);
        }

        /**
         * Select all nodes matching an XPath expression starting at the given element.
         *
         * @param root
         *            An {@link Element} representing the XML element from which the XPath expression should start
         *            searching.
         * @param expression
         *            The XPath expression to be evaluated.
         * @return A {@link List} of {@link Element} objects representing all elements that match the given XPath
         *         expression.
         * @throws JDOMException
         *             If any errors occur during the analysis.
         */
        protected List<Element> selectNodes(final Element root, final String expression) throws JDOMException {
            return selectNodesInternal(root, expression);
        }

        /**
         * Select all element nodes with the specified element name. This routine traverses the tree in a manner which
         * does not employ recursion, and therefore hopefully performs better for large XML files.
         *
         * @param element
         *            A {@link Element} against which the XPath expression is to be evaluated.
         * @param name
         *            The name of the node to search
         * @return A {@link List} of {@link Element} objects representing all elements in the document with the
         *         specified node name
         * @throws JDOMException
         *             If any errors occur during the analysis.
         */
        protected List<Element> selectNodesByName(final Element element, final String name) throws JDOMException {
            return selectNodesByName(element, name, null);
        }

        /**
         * Select all element nodes with the specified element name. This routine traverses the tree in a manner which
         * does not employ recursion, and therefore hopefully performs better for large XML files.
         *
         * @param name
         *            The name of the node to search
         * @return A {@link List} of {@link Element} objects representing all elements in the document with the
         *         specified node name
         * @throws JDOMException
         *             If any errors occur during the analysis.
         */
        protected List<Element> selectNodesByName(final String name) throws JDOMException {
            return selectNodesByName(name, null);
        }

        /**
         * Select all element nodes with the specified element name. This routine traverses the tree in a manner which
         * does not employ recursion, and therefore hopefully performs better for large XML files.
         *
         * @param name
         *            The name of the node to search
         * @param predicate
         *            The XPath predicate expression to apply to the name filter to further filter down the elements
         *            requested
         * @return A {@link List} of {@link Element} objects representing all elements in the document with the
         *         specified node name
         * @throws JDOMException
         *             If any errors occur during the analysis.
         */
        protected List<Element> selectNodesByName(final String name, final String predicate) throws JDOMException {
            return selectNodesByName(document.getRootElement(), name, predicate);
        }

        /**
         * Identifies the scope for a given element.
         *
         * @param element
         *            The element.
         * @return The scope element for the specified element.
         */
        protected Element getScope(final Element element) {
            Element ancestor = element.getParentElement();
            while (ancestor != null) {
                String ancestorName = ancestor.getName();
                if (ancestorName.equals("SUBROUTINE.") || ancestorName.equals("ZC_PROGRAM.")) {
                    return ancestor;
                }
                ancestor = ancestor.getParentElement();
            }
            return null;
        }

        /**
         * Identifies the list of scopes that contain a given element.
         *
         * @param element
         *            The element.
         * @return A list of elements which contain the specified element in their scope.
         */
        protected Set<Element> getScopes(final Element element) {
            Set<Element> scopes = new HashSet<Element>();
            Element ancestor = element.getParentElement();
            while (ancestor != null) {
                String ancestorName = ancestor.getName();
                if (ancestorName.equals("SUBROUTINE.") || ancestorName.equals("ZC_PROGRAM.")) {
                    scopes.add(ancestor);
                }
                ancestor = ancestor.getParentElement();
            }
            return scopes;
        }

        /**
         * Select all element nodes with the specified element name and XPath predicate.
         *
         * @param element
         *            An {@link Element} against which the XPath expression is to be evaluated.
         * @param name
         *            The name of the node to search
         * @param predicate
         *            The XPath predicate expression to apply to the name filter to further filter down the elements
         *            requested
         * @return A {@link List} of {@link Element} objects representing all elements in the document with the
         *         specified node name
         * @throws JDOMException
         *             If any errors occur during the analysis.
         */
        protected List<Element> selectNodesByName(final Element element, final String name, final String predicate)
                throws JDOMException {
            return selectNodes(element, ".//" + name + (predicate != null ? predicate : ""));
        }

        /**
         * Select all attributes matching a given expression within the given object.
         *
         * @param root
         *            The object from which the expression is to begin its application.
         * @param expression
         *            The XPath expression to be evaluated.
         * @return A {@link List} of {@link Attribute} objects representing all XML element attributes that match the
         *         given XPath expression.
         * @throws JDOMException
         *             If any errors occur during the analysis.
         */
        private List<Attribute> selectAttributeInternal(final Object root, final String expression)
                throws JDOMException {
            return XPathFactory.instance().compile(expression, Filters.attribute()).evaluate(root);
        }

        /**
         * Select all elements matching a given expression within the given object.
         *
         * @param root
         *            The object from which the expression is to begin its application.
         * @param expression
         *            The XPath expression to be evaluated.
         * @return A {@link List} of {@link Element} objects representing all elements that match the given XPath
         *         expression.
         * @throws JDOMException
         *             If any errors occur during the analysis.
         */
        private List<Element> selectNodesInternal(final Object root, final String expression) throws JDOMException {
            return XPathFactory.instance().compile(expression, Filters.element()).evaluate(root);
        }
    }
}
