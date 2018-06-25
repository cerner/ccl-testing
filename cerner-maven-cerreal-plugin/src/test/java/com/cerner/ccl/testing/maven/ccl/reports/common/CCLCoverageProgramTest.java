package com.cerner.ccl.testing.maven.ccl.reports.common;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.testing.maven.ccl.reports.common.CCLCoverageProgram;
import com.cerner.ccl.testing.maven.ccl.reports.common.CCLProgram;
import com.cerner.ccl.testing.maven.ccl.reports.common.CoverageLine;
import com.cerner.ccl.testing.maven.ccl.reports.common.CoveredStatus;
import com.cerner.ccl.testing.maven.ccl.reports.common.CCLProgram.ProgramLine;
import com.cerner.ccl.testing.maven.ccl.reports.common.internal.XmlCoverageLine;
import com.cerner.ccl.testing.maven.ccl.reports.common.internal.XmlGenerator;

/**
 * Unit tests for {@link CCLCoverageProgram}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { CCLProgram.class, CCLCoverageProgram.class, CoverageLine.class })
public class CCLCoverageProgramTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    private final String listingXml = "i am listing xml";
    @Mock
    private CCLProgram program;
    @Mock
    private ProgramLine programLineA;
    @Mock
    private ProgramLine programLineB;
    @Mock
    private ProgramLine programLineC;
    @Mock
    private CoverageLine coverageLineA;
    @Mock
    private CoverageLine coverageLineB;
    @Mock
    private CoverageLine coverageLineC;
    private CCLCoverageProgram coverageProgram;

    /**
     * Set up the coverage program for each test.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Before
    public void setUp() throws Exception {
        whenNew(CCLProgram.class).withArguments(listingXml).thenReturn(program);

        whenNew(CoverageLine.class).withArguments(programLineA).thenReturn(coverageLineA);
        whenNew(CoverageLine.class).withArguments(programLineB).thenReturn(coverageLineB);
        whenNew(CoverageLine.class).withArguments(programLineC).thenReturn(coverageLineC);

        when(coverageLineA.getLineNumber()).thenReturn(Integer.valueOf(1));
        when(coverageLineB.getLineNumber()).thenReturn(Integer.valueOf(2));
        when(coverageLineC.getLineNumber()).thenReturn(Integer.valueOf(3));

        when(program.getProgramLines()).thenReturn(Arrays.asList(programLineA, programLineB, programLineC));
        coverageProgram = new CCLCoverageProgram(listingXml);
    }

    /**
     * Test the addition of coverage data.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddCoverage() throws Exception {
        final String coverageXml = XmlGenerator.createTestCoverageXml("addedCoverage",
                Arrays.asList(line(CoveredStatus.COVERED, 1), line(CoveredStatus.NOT_COVERED, 2), line(CoveredStatus.NOT_EXECUTABLE, 3)));
        final CCLCoverageProgram testProgram = mock(CCLCoverageProgram.class);
        coverageProgram.addCoverage(testProgram, coverageXml);

        verify(coverageLineA).addTestCoverage(testProgram, CoveredStatus.COVERED);
        verify(coverageLineB).addTestCoverage(testProgram, CoveredStatus.NOT_COVERED);
        verify(coverageLineC).addTestCoverage(testProgram, CoveredStatus.NOT_EXECUTABLE);
    }

    /**
     * If the source code contains a reference to a line not in the coverage data, then {@link CoveredStatus#UNDEFINED} should be added to that line's coverage.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddCoverageMissingLine() throws Exception {
        final String coverageXml = XmlGenerator.createTestCoverageXml("addedCoverage", Arrays.asList(line(CoveredStatus.COVERED, 1), line(CoveredStatus.NOT_EXECUTABLE, 3)));
        final CCLCoverageProgram testProgram = mock(CCLCoverageProgram.class);
        coverageProgram.addCoverage(testProgram, coverageXml);

        verify(coverageLineA).addTestCoverage(testProgram, CoveredStatus.COVERED);
        verify(coverageLineB).addTestCoverage(testProgram, CoveredStatus.UNDEFINED);
        verify(coverageLineC).addTestCoverage(testProgram, CoveredStatus.NOT_EXECUTABLE);
    }

    /**
     * Construction with {@code null} listing XML should fail.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullListingXml() throws Exception {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Listing XML cannot be null.");
        new CCLCoverageProgram(null);
    }

    /**
     * Test the retrieval of the test name.
     */
    @Test
    public void testGetName() {
        final String name = "i am the test name";
        when(program.getName()).thenReturn(name);
        assertThat(coverageProgram.getName()).isEqualTo(name);
    }

    /**
     * Test the retrieval of the program lines.
     */
    @Test
    public void testGetProgramLines() {
        assertThat(coverageProgram.getCoverageLines()).containsExactly(coverageLineA, coverageLineB, coverageLineC);
    }

    /**
     * Test the counting of coverage. The setup is as follows:
     * <ul>
     * <li><b>coverageLineA</b>: This will match the coverage and have a source of "PROGRAM".</li>
     * <li><b>coverageLineB</b>: This will not match the coverage and will have a source of "PROGRAM".</li>
     * <li><b>coverageLineC</b>: This will match the coverage, but will not come from a "PROGRAM" source.</li>
     * </ul>
     */
    @Test
    public void testGetCoverageTotalOfAggregate() {
        final CoveredStatus requested = CoveredStatus.COVERED;

        when(coverageLineA.getAggregateCoveredStatus()).thenReturn(requested);
        when(coverageLineA.getSourceCodeOrigin()).thenReturn("PROGRAM");

        // Fancy way of finding a status other than the requested
        when(coverageLineB.getAggregateCoveredStatus()).thenReturn(getOtherStatus(requested));
        when(coverageLineB.getSourceCodeOrigin()).thenReturn("PROGRAM");

        when(coverageLineC.getAggregateCoveredStatus()).thenReturn(requested);
        when(coverageLineC.getSourceCodeOrigin()).thenReturn("%i cclsource:whatever.inc");

        // Only coverageLineA should qualify
        assertThat(coverageProgram.getCoverageTotalOf(requested, false, null)).isEqualTo(1);
    }

    /**
     * If the option to account for includes has been set, then the origin of the line should be disregarded. The setup is as follows:
     * <ul>
     * <li><b>coverageLineA</b>: This will match the requested status and be from "PROGRAM".</li>
     * <li><b>coverageLineB</b>: This will not match the given status.</li>
     * <li><b>coverageLineC</b>: This will match the requested status and have a source other than "PROGRAM".</li>
     * </ul>
     */
    @Test
    public void testGetCoverageTotalOfAggregateWithIncludes() {
        final CoveredStatus requested = CoveredStatus.COVERED;

        when(coverageLineA.getAggregateCoveredStatus()).thenReturn(requested);
        when(coverageLineA.getSourceCodeOrigin()).thenReturn("PROGRAM");

        // Fancy way of finding a status other than the requested
        when(coverageLineB.getAggregateCoveredStatus()).thenReturn(getOtherStatus(requested));
        when(coverageLineB.getSourceCodeOrigin()).thenReturn("PROGRAM");

        when(coverageLineC.getAggregateCoveredStatus()).thenReturn(requested);
        when(coverageLineC.getSourceCodeOrigin()).thenReturn("%i cclsource:whatever.inc");

        // Only coverageLineA and coverageLineC should qualify
        assertThat(coverageProgram.getCoverageTotalOf(requested, true, null)).isEqualTo(2);
    }

    /**
     * Test the counting of coverage for a specific test. The setup is as follows:
     * <ul>
     * <li><b>coverageLineA</b>: This will be covered by the given test and have a source of "PROGRAM".</li>
     * <li><b>coverageLineB</b>: This will be covered by the given test, but with a coverage different from the request, and will have a source of "PROGRAM".</li>
     * <li><b>coverageLineC</b>: This will be covered by the given test with a matching coverage, but will not come from a "PROGRAM" source.</li>
     * </ul>
     */
    @Test
    public void testGetCoverageTotalOfSpecificTest() {
        final CoveredStatus requested = CoveredStatus.COVERED;
        final CCLCoverageProgram testCase = mock(CCLCoverageProgram.class);

        when(coverageLineA.getCoverage()).thenReturn(Collections.singletonMap(testCase, requested));
        when(coverageLineA.getSourceCodeOrigin()).thenReturn("PROGRAM");

        when(coverageLineB.getCoverage()).thenReturn(Collections.singletonMap(testCase, getOtherStatus(requested)));
        when(coverageLineB.getSourceCodeOrigin()).thenReturn("PROGRAM");

        when(coverageLineC.getCoverage()).thenReturn(Collections.singletonMap(testCase, requested));
        when(coverageLineC.getSourceCodeOrigin()).thenReturn("%i cclsource:somewhere_else.sub");

        // Only coverageLineA should qualify
        assertThat(coverageProgram.getCoverageTotalOf(requested, false, testCase)).isEqualTo(1);
    }

    /**
     * Verify that counting the coverage by a specific test accounts for lines not covered at all by the given test. It will be set up as follows:
     * <ul>
     * <li><b>coverageLineA</b>: This will be covered by the given test and have a source of "PROGRAM".</li>
     * <li><b>coverageLineB</b>: This will not be covered by the given test.</li>
     * <li><b>coverageLineC</b>: This will be covered by the given test and have a source of "PROGRAM".</li>
     * </ul>
     */
    @Test
    public void testGetCoverageTotalOfSpecificTestNotCovered() {
        final CoveredStatus requested = CoveredStatus.COVERED;
        final CCLCoverageProgram testCase = mock(CCLCoverageProgram.class);

        when(coverageLineA.getCoverage()).thenReturn(Collections.singletonMap(testCase, requested));
        when(coverageLineA.getSourceCodeOrigin()).thenReturn("PROGRAM");

        when(coverageLineB.getCoverage()).thenReturn(Collections.<CCLCoverageProgram, CoveredStatus> emptyMap());

        when(coverageLineC.getCoverage()).thenReturn(Collections.singletonMap(testCase, requested));
        when(coverageLineC.getSourceCodeOrigin()).thenReturn("PROGRAM");

        // only coverageLineA and coverageLineC should qualify
        assertThat(coverageProgram.getCoverageTotalOf(requested, false, testCase)).isEqualTo(2);
    }

    /**
     * Test the counting of lines from source code outside of include files.
     */
    @Test
    public void testGetTotalProgramLines() {
        when(coverageLineA.getSourceCodeOrigin()).thenReturn("PROGRAM");
        when(coverageLineB.getSourceCodeOrigin()).thenReturn("%i cclsource:elsewhere.inc");
        when(coverageLineC.getSourceCodeOrigin()).thenReturn("PROGRAM");

        assertThat(coverageProgram.getTotalProgramLines(false)).isEqualTo(2);
    }

    /**
     * When counting the program lines, the source should not be considered if the {@code withIncludes} parameter is {@code true}.
     */
    @Test
    public void testGetTotalProgramLinesWithIncludes() {
        when(coverageLineA.getSourceCodeOrigin()).thenReturn("PROGRAM");
        when(coverageLineB.getSourceCodeOrigin()).thenReturn("%i cclsource:elsewhere.inc");
        when(coverageLineC.getSourceCodeOrigin()).thenReturn("PROGRAM");

        assertThat(coverageProgram.getTotalProgramLines(true)).isEqualTo(3);
    }

    /**
     * Verify that the program correctly reflects the tests it was and was not tested by.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWasTestedBy() throws Exception {
        final CCLCoverageProgram wasTested = mock(CCLCoverageProgram.class);
        final CCLCoverageProgram notTested = mock(CCLCoverageProgram.class);

        coverageProgram.addCoverage(wasTested, XmlGenerator.createTestCoverageXml("scriptName", Collections.<XmlCoverageLine> emptySet()));
        assertThat(coverageProgram.wasTestedBy(wasTested)).isTrue();
        assertThat(coverageProgram.wasTestedBy(notTested)).isFalse();
    }

    /**
     * Get a {@link CoveredStatus} other than the given.
     *
     * @param requested
     *            The {@link CoveredStatus} that is not to be returned.
     * @return A {@link CoveredStatus} other than the given.
     */
    private CoveredStatus getOtherStatus(CoveredStatus requested) {
        return CoveredStatus.values()[requested.ordinal() == 0 ? 1 : requested.ordinal() - 1];
    }

    /**
     * Create a line.
     *
     * @param status
     *            The coverage status of the line.
     * @param lineNumber
     *            The line number of the line.
     * @return A {@link XmlCoverageLine}.
     */
    private XmlCoverageLine line(CoveredStatus status, int lineNumber) {
        return new XmlCoverageLine(status, lineNumber);
    }
}
