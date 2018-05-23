package com.cerner.ccltesting.maven.ccl.reports.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.maven.reporting.MavenReportException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.cerner.ccltesting.xsl.XslAPI;
import com.cerner.ccltesting.xsl.XslAPIException;

/**
 * A class representing a CCL program.
 *
 * @author Jeff Wiedemann
 *
 */

public class CCLProgram {
    /**
     * Representation of a line within a CCL program.
     *
     * @author Jeff Wiedemann
     *
     */
    public static class ProgramLine {
        private final int lineNumber;
        private final String sourceCode;
        private final String origin;

        /**
         * Create a program line.
         *
         * @param node
         *            A {@link Node} representing a {@code <LINE />} element to be parsed for its data.
         * @param origin
         *            The origin of the line.
         */
        public ProgramLine(Node node, String origin) {
            int lineNumber = 0;
            String sourceCode = null;
            for (int i = 0; i < node.getChildNodes().getLength(); i++) {
                if (node.getChildNodes().item(i).getNodeName().equals("NBR"))
                    lineNumber = Integer.parseInt(node.getChildNodes().item(i).getFirstChild().getNodeValue());
                else if (node.getChildNodes().item(i).getNodeName().equals("TEXT")) {
                    if (node.getChildNodes().item(i).hasChildNodes())
                        sourceCode = node.getChildNodes().item(i).getFirstChild().getNodeValue();
                    else
                        sourceCode = "";
                }
            }

            this.origin = origin;
            this.lineNumber = lineNumber;
            this.sourceCode = sourceCode;
        }

        /**
         * Get the line number.
         *
         * @return The line number.
         */
        public int getLineNumber() {
            return lineNumber;
        }

        /**
         * Get the origin of the source code.
         *
         * @return The origin of the source code.
         */
        public String getOrigin() {
            return origin;
        }

        /**
         * Get the line of text for the source code.
         *
         * @return The line of text.
         */
        public String getSourceCode() {
            return sourceCode;
        }
    }

    private final List<ProgramLine> programLines = new ArrayList<ProgramLine>();
    private final String listingXML;
    private final Document listingDOM;
    private final String name;

    /**
     * Create a CCL program.
     *
     * @param listingXML
     *            The XML to be parsed for information about the program.
     * @throws IllegalArgumentException
     *             If the given listing XML is {@code null}.
     * @throws MavenReportException
     *             If any errors occur during the parsing.
     */
    public CCLProgram(String listingXML) throws MavenReportException {
        if (listingXML == null)
            throw new IllegalArgumentException("Listing XML cannot be null.");

        this.listingXML = listingXML;
        try {
            this.listingDOM = XslAPI.getDocumentFromString(listingXML);
        } catch (XslAPIException e) {
            throw new MavenReportException("Failed to create program due to exception", e);
        }

        this.name = XslAPI.getNodeXPathValue(this.listingDOM, "/LISTING/LISTING_NAME");

        // TODO: why not an enum?
        String origin = "PROGRAM";
        NodeList lines = XslAPI.getXPathNodeList(listingDOM, "/LISTING/LINES/LINE");
        for (int i = 0; i < lines.getLength(); i++) {
            String newOrigin = origin;
            // Determine if this code is from an include file
            for (int j = 0; j < lines.item(i).getChildNodes().getLength(); j++) {
                if (origin.equals("PROGRAM") && lines.item(i).getChildNodes().item(j).getNodeName().equals("START_OF_INC"))
                    newOrigin = lines.item(i).getChildNodes().item(j).getFirstChild().getNodeValue();
                else if (lines.item(i).getChildNodes().item(j).getNodeName().equals("END_OF_INC"))
                    newOrigin = "PROGRAM";
            }

            // If the origin did not change then add the line, otherwise this is a specially inserted line
            // that tells me what include file the source code comes from and we don't want to add it.
            if (newOrigin.equals(origin))
                programLines.add(new ProgramLine(lines.item(i), origin));
            else
                origin = newOrigin;
        }
    }

    /**
     * Get the XML of the listing.
     *
     * @return The XML of the listing.
     */
    public String getListingXML() {
        return listingXML;
    }

    /**
     * Get the DOM representation of the listing XML.
     *
     * @return A {@link Document} representing the listing DOM.
     */
    public Document getListingDOM() {
        return listingDOM;
    }

    /**
     * Get the name of the program.
     *
     * @return The name of the program.
     */
    public String getName() {
        return name;
    }

    /**
     * Get the lines of the source code.
     *
     * @return A {@link List} of {@link ProgramLine} objects representing the lines of the source code and their metadata.
     */
    public List<ProgramLine> getProgramLines() {
        return Collections.unmodifiableList(this.programLines);
    }

    /**
     * Get a line of a source code corresponding to the given line number.
     *
     * @param lineNumber
     *            The number of the line to be retrieved.
     * @return A blank string if the given number corresponds to no known line number; otherwise, the text of the requested line number.
     */
    @SuppressWarnings("synthetic-access")
    public String getSourceCodeAtLine(int lineNumber) {
        // TODO: why not random access of programLines list? Is sequence not guaranteed?
        for (ProgramLine l : programLines) {
            if (l.lineNumber == lineNumber)
                return l.sourceCode;
        }

        return "";
    }
}
