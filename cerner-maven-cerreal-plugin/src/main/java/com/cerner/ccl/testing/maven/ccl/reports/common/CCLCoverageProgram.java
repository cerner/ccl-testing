package com.cerner.ccl.testing.maven.ccl.reports.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.reporting.MavenReportException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.cerner.ccl.testing.maven.ccl.reports.common.CCLProgram.ProgramLine;
import com.cerner.ccl.testing.xsl.XslAPI;
import com.cerner.ccl.testing.xsl.XslAPIException;

/**
 * Definition of a CCL script's coverage data.
 *
 * @author Jeff Wiedemann
 *
 */
/*
 * TODO: would it make sense to separate this from the concept of test coverage *by* a test script and the script that is covered? It's a bit of a cognitive dissonance to have one object represent
 * both concepts.
 */
public class CCLCoverageProgram {
    private final List<CoverageLine> coverageLines = new ArrayList<CoverageLine>();
    private final List<CCLCoverageProgram> testPrograms = new ArrayList<CCLCoverageProgram>();
    private final CCLProgram program;

    /**
     * Create an object representing the coverage of a CCL script.
     *
     * @param listingXML
     *            The XML to be parsed into coverage data.
     * @throws IllegalArgumentException
     *             If the given listing XML is {@code null}.
     * @throws MavenReportException
     *             If any errors occur in parsing the data.
     */
    public CCLCoverageProgram(String listingXML) throws MavenReportException {
        if (listingXML == null)
            throw new IllegalArgumentException("Listing XML cannot be null.");

        program = new CCLProgram(listingXML);

        for (ProgramLine l : program.getProgramLines()) {
            coverageLines.add(new CoverageLine(l));
        }
    }

    /**
     * Add coverage data for a test program.
     *
     * @param testProgram
     *            The {@link CCLCoverageProgram} for which the coverage data is to be added.
     * @param coverageXML
     *            The XML of the coverage data to be added.
     * @throws MavenReportException
     *             If any errors occur while parsing the coverage XML.
     */
    public void addCoverage(CCLCoverageProgram testProgram, String coverageXML) throws MavenReportException {
        Document coverageDOM;
        try {
            coverageDOM = XslAPI.getDocumentFromString(coverageXML);
        } catch (XslAPIException e) {
            throw new MavenReportException("Failed to add coverage to program due to error", e);
        }

        // Spin through each coverage line and store off into a map the pairing of a line
        // number and that lines covered status
        Map<Integer, String> coverageMap = new HashMap<Integer, String>();
        NodeList nodes = XslAPI.getXPathNodeList(coverageDOM, "/COVERAGE/LINES/LINE");
        for (int i = 0; i < nodes.getLength(); i++) {
            Integer lineNumber = null;
            String coveredStatus = null;

            // Iterate through the LINE element and find it's child element values
            for (int j = 0; j < nodes.item(i).getChildNodes().getLength(); j++) {
                if (nodes.item(i).getChildNodes().item(j).getNodeName().equals("NBR"))
                    lineNumber = Integer.valueOf(nodes.item(i).getChildNodes().item(j).getFirstChild().getNodeValue());
                else if (nodes.item(i).getChildNodes().item(j).getNodeName().equals("TYPE")
                        && nodes.item(i).getChildNodes().item(j).hasChildNodes()) {
                        coveredStatus = nodes.item(i).getChildNodes().item(j).getFirstChild().getNodeValue();
                }
            }

            coverageMap.put(lineNumber, coveredStatus);
        }

        // Now that we can quickly determine the covered status of a given program line,
        // loop through all of the program lines and indicate what it's covered status is
        // for this particular testCase
        for (CoverageLine l : coverageLines) {
            final String status = coverageMap.get(Integer.valueOf(l.getLineNumber()));
            final CoveredStatus coveredStatus = status == null ? CoveredStatus.UNDEFINED : CoveredStatus.forCharacterRepresentation(status);
            l.addTestCoverage(testProgram, coveredStatus);
        }

        testPrograms.add(testProgram);
    }

    /**
     * Get the name of the CCL program.
     *
     * @return The name of the CCL program.
     */
    public String getName() {
        return program.getName();
    }

    /**
     * Get the code coverage executed by each test.
     *
     * @return A {@link List} of {@link CoverageLine} objects representing the lines of code coverage.
     */
    public List<CoverageLine> getCoverageLines() {
        return Collections.unmodifiableList(coverageLines);
    }

    /**
     * Determine the number of lines in this program whose coverage matches the given coverage.
     *
     * @param status
     *            A {@link CoveredStatus} enum representing the coverage type for which a line count is to be obtained.
     * @param withIncludes
     *            A {@code boolean} value indicating whether or not source code from include files is to be included in the count; if {@code false}, then anything that does not originate from a
     *            program will not be considered.
     * @param byTestCase
     *            A {@link CCLCoverageProgram} for which the specific coverage is to be determined; if {@code null}, then the aggregate coverage of the line will be used; otherwise, a line count for
     *            lines whose coverage match the coverage by the given test case will be calculated.
     * @return A count of the lines whose coverage matches for the given status and test case.
     */
    public int getCoverageTotalOf(CoveredStatus status, boolean withIncludes, CCLCoverageProgram byTestCase) {
        int total = 0;
        for (CoverageLine c : this.coverageLines) {
            CoveredStatus lineStatus;
            if (byTestCase == null) {
                lineStatus = c.getAggregateCoveredStatus();
            } else {
                lineStatus = c.getCoverage().get(byTestCase);
            }

            if (status == lineStatus && (withIncludes || c.getSourceCodeOrigin().equals("PROGRAM"))) {
                total++;
            }
        }

        return total;
    }

    /**
     * Get the total number of lines in the program.
     *
     * @param withIncludes
     *            A {@code boolean}; if {@code true}, then lines from include files will be counted; if {@code false}, then only lines in the PRG source code will be counted.
     * @return The number of lines in the source code.
     */
    public int getTotalProgramLines(boolean withIncludes) {
        if (withIncludes)
            return coverageLines.size();

        int total = 0;
        for (CoverageLine c : this.coverageLines) {
            if (c.getSourceCodeOrigin().equals("PROGRAM"))
                total++;
        }
        return total;
    }

    /**
     * Determine whether or not this program was tested by the given program.
     *
     * @param testProgram
     *            A {@link CCLCoverageProgram} representing the test program for which test coverage is to be determined.
     * @return {@code true} if the given test program tested this program; otherwise, {@code false}.
     */
    public boolean wasTestedBy(CCLCoverageProgram testProgram) {
        for (CCLCoverageProgram p : testPrograms) {
            // TODO: this relies on an implementation of equals(), but the class does not implement it
            if (p.equals(testProgram))
                return true;
        }
        return false;
    }
}
