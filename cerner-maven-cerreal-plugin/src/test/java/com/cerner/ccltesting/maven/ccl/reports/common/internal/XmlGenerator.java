package com.cerner.ccltesting.maven.ccl.reports.common.internal;

import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;

import javax.xml.bind.JAXBContext;

import com.cerner.ccltesting.maven.ccl.reports.common.internal.jaxb.COVERAGE;
import com.cerner.ccltesting.maven.ccl.reports.common.internal.jaxb.COVERAGE.LINES;
import com.cerner.ccltesting.maven.ccl.reports.common.internal.jaxb.COVERAGE.LINES.LINE;
import com.cerner.ccltesting.maven.ccl.reports.common.internal.jaxb.LISTING;
import com.cerner.ccltesting.maven.ccl.reports.common.internal.jaxb.ObjectFactory;

/**
 * A utility class to generate XML data.
 * 
 * @author Joshua Hyde
 * 
 */

public class XmlGenerator {
    /**
     * A marker to indicate the start of an include file.
     */
    public static final String INC_START = "START_OF_INC";
    /**
     * A marker to indicate the end of an include file.
     */
    public static final String INC_END = "END_OF_INC";

    /**
     * Create an XML representation of a listing output.
     * 
     * @param listingName
     *            The name of the program for which the listing is generated.
     * @param source
     *            A {@link Collection} of {@code String} objects representing the source code. If you wish to represent code nested inside an in-line include file, use {@link #INC_START} and
     *            {@link #INC_END} to signify this. Each line should be followed by the inclusion statement. For example:
     * 
     *            <pre>
     * Arrays.asList(&quot;call echo('test')&quot;, incStart + &quot;%i cclsource:test.inc&quot;, &quot;call echo('nested in an include!')&quot;, incEnd + &quot;%i cclsource:test.inc&quot;, &quot;call echo('not nested!')&quot;)
     * </pre>
     * @return A {@link String} containing the listing XML.
     * @throws Exception
     *             If any errors occur while creating XML.
     */
    public static String createListingXml(String listingName, Collection<String> source) throws Exception {
        final ObjectFactory factory = new ObjectFactory();

        final LISTING.LINES lines = factory.createLISTINGLINES();
        int lineNumber = 1;
        for (String sourceLine : source) {
            final LISTING.LINES.LINE line = factory.createLISTINGLINESLINE();
            if (sourceLine.startsWith(INC_START))
                line.setSTARTOFINC(sourceLine.substring(INC_START.length()));
            else if (sourceLine.startsWith(INC_END))
                line.setENDOFINC(sourceLine.substring(INC_END.length()));
            else
                line.setTEXT(sourceLine);
            lines.getLINE().add(line);
            line.setNBR(BigInteger.valueOf(lineNumber++));
        }

        final LISTING listing = factory.createLISTING();
        listing.setLISTINGNAME(listingName);
        listing.setCOMPILEDATE(new Date().toString());
        listing.setLINES(lines);

        final JAXBContext context = JAXBContext.newInstance(LISTING.class);
        final StringWriter writer = new StringWriter();
        context.createMarshaller().marshal(listing, writer);
        return writer.toString();
    }
    /**
     * Create test coverage data.
     * 
     * @param scriptName
     *            The script that was tested.
     * @param coverageLines
     *            A {@link Collection} of {@link XmlCoverageLine} objects representing the test coverage.
     * @return An XML representation of the coverage data.
     * @throws Exception
     *             If any errors occur generating the XML.
     */
    public static String createTestCoverageXml(String scriptName, Collection<XmlCoverageLine> coverageLines) throws Exception {
        final ObjectFactory objectFactory = new ObjectFactory();

        final LINES lines = objectFactory.createCOVERAGELINES();
        for (XmlCoverageLine coverageLine : coverageLines) {
            final LINE line = objectFactory.createCOVERAGELINESLINE();
            line.setNBR(coverageLine.getLineNumber());
            line.setTYPE(coverageLine.getStatus().getCharacterRepresentation());
            lines.getLINE().add(line);
        }

        final COVERAGE coverage = objectFactory.createCOVERAGE();
        coverage.setCOVERAGENAME(scriptName);
        coverage.setLINES(lines);

        final JAXBContext context = JAXBContext.newInstance(COVERAGE.class);
        final StringWriter writer = new StringWriter();
        context.createMarshaller().marshal(coverage, writer);
        return writer.toString();
    }
}
