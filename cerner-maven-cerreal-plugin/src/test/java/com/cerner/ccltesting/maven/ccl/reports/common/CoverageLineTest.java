package com.cerner.ccltesting.maven.ccl.reports.common;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.cerner.ccltesting.maven.ccl.reports.common.CCLProgram.ProgramLine;

/**
 * Unit tests for {@link CoverageLine}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(MockitoJUnitRunner.class)
public class CoverageLineTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Mock
    private ProgramLine programLine;
    @Mock
    private CCLCoverageProgram program;
    private CoverageLine line;

    /**
     * Set up the coverage line for each test.
     */
    @Before
    public void setUp() {
        line = new CoverageLine(programLine);
    }

    /**
     * Construction with a {@code null} {@link ProgramLine} should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullProgramLine() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Line cannot be null.");
        new CoverageLine(null);
    }

    /**
     * Test the addition of code coverage for a given test.
     */
    @Test
    public void testAddTestCoverage() {
        final CoveredStatus status = CoveredStatus.COVERED;
        line.addTestCoverage(program, status);
        assertThat(line.getCoveredStatusByTestCase(program)).isEqualTo(status);
    }

    /**
     * Adding a {@code null} {@link CoveredStatus} should fail.
     */
    @Test
    public void testAddTestCoverageNullCoverage() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("The covered status cannot be null.");
        line.addTestCoverage(program, null);
    }

    /**
     * Adding a {@code null} {@link CCLCoverageProgram} should fail.
     */
    @Test
    public void testAddTestCoverageNullProgram() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Coverage program cannot be null.");
        line.addTestCoverage(null, CoveredStatus.COVERED);
    }

    /**
     * If any of the programs indicate that the line was covered, then that should be the returned state.
     */
    @Test
    public void testGetAggregateCoveredStatusCovered() {
        final CCLCoverageProgram covered = mock(CCLCoverageProgram.class);
        final CCLCoverageProgram notCovered = mock(CCLCoverageProgram.class);
        line.addTestCoverage(covered, CoveredStatus.COVERED);
        line.addTestCoverage(notCovered, CoveredStatus.NOT_COVERED);
        assertThat(line.getAggregateCoveredStatus()).isEqualTo(CoveredStatus.COVERED);
    }

    /**
     * If nothing indicates coverage, nor that it was not executable, but at least one indicates that it was not covered, then that should be the returned aggregate state.
     */
    @Test
    public void testGetAggregateCoveredStatusNotCovered() {
        final CCLCoverageProgram notCovered = mock(CCLCoverageProgram.class);
        line.addTestCoverage(notCovered, CoveredStatus.NOT_COVERED);
        assertThat(line.getAggregateCoveredStatus()).isEqualTo(CoveredStatus.NOT_COVERED);
    }

    /**
     * If at least one test indicates that the code could not be executed, then that should be its aggregated status.
     */
    @Test
    public void testGetAggregateCoveredStatusNotExecutable() {
        final CCLCoverageProgram notCovered = mock(CCLCoverageProgram.class);
        final CCLCoverageProgram notExecutable = mock(CCLCoverageProgram.class);
        line.addTestCoverage(notExecutable, CoveredStatus.NOT_EXECUTABLE);
        line.addTestCoverage(notCovered, CoveredStatus.NOT_COVERED);
        assertThat(line.getAggregateCoveredStatus()).isEqualTo(CoveredStatus.NOT_EXECUTABLE);
    }

    /**
     * If there is nothing to indicate whether or not anything was executed, then the aggregate state should be "undefined".
     */
    @Test
    public void testGetAggregateCoveredStatusUndefined() {
        assertThat(line.getAggregateCoveredStatus()).isEqualTo(CoveredStatus.UNDEFINED);
    }

    /**
     * Test the retrieval of coverage data.
     */
    @Test
    public void testGetCoverage() {
        final CCLCoverageProgram covered = mock(CCLCoverageProgram.class);
        final CCLCoverageProgram notCovered = mock(CCLCoverageProgram.class);
        line.addTestCoverage(covered, CoveredStatus.COVERED);
        line.addTestCoverage(notCovered, CoveredStatus.NOT_COVERED);
        assertThat(line.getCoverage()).includes(entry(covered, CoveredStatus.COVERED), entry(notCovered, CoveredStatus.NOT_COVERED)).hasSize(2);
    }

    /**
     * Verify the retrieval of code coverage for a specific test.
     */
    @Test
    public void testGetCoveredStatusByTestCase() {
        final CCLCoverageProgram covered = mock(CCLCoverageProgram.class);
        line.addTestCoverage(covered, CoveredStatus.COVERED);
        assertThat(line.getCoveredStatusByTestCase(covered)).isEqualTo(CoveredStatus.COVERED);
    }

    /**
     * If a {@code null} test case is given, then the aggregate coverage should be returned.
     */
    @Test
    public void testGetCoveredStatusByTestCaseNull() {
        final CoverageLine mockLine = mock(CoverageLine.class);
        when(mockLine.getAggregateCoveredStatus()).thenReturn(CoveredStatus.COVERED);
        when(mockLine.getCoveredStatusByTestCase(null)).thenCallRealMethod();

        assertThat(mockLine.getCoveredStatusByTestCase(null)).isEqualTo(CoveredStatus.COVERED);
    }

    /**
     * Test the retrieval of the line number.
     */
    @Test
    public void testGetLineNumber() {
        final int lineNumber = 4783;
        when(programLine.getLineNumber()).thenReturn(Integer.valueOf(lineNumber));
        assertThat(line.getLineNumber()).isEqualTo(lineNumber);
    }

    /**
     * Test the retrieval of the source code.
     */
    @Test
    public void testGetSourceCode() {
        final String sourceCode = "i am the source code";
        when(programLine.getSourceCode()).thenReturn(sourceCode);
        assertThat(line.getSourceCode()).isEqualTo(sourceCode);
    }

    /**
     * Test the retrieval of the origin of the source code line.
     */
    @Test
    public void testGetSourceCodeOrigin() {
        final String origin = "alpha";
        when(programLine.getOrigin()).thenReturn(origin);
        assertThat(line.getSourceCodeOrigin()).isEqualTo(origin);
    }
}
