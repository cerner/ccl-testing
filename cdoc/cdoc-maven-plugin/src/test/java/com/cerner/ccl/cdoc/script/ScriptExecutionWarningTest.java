package com.cerner.ccl.cdoc.script;

import static org.fest.assertions.Assertions.assertThat;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;

import com.cerner.ccl.cdoc.AbstractBeanUnitTest;

/**
 * Unit tests for {@link ScriptExecutionWarning}.
 *
 * @author Joshua Hyde
 *
 */
@SuppressWarnings("unused")
public class ScriptExecutionWarningTest extends AbstractBeanUnitTest<ScriptExecutionWarning> {
    private final int lineNumber = 1337;
    private final String sourceCode = "danger, will robinson";
    private final ScriptExecutionWarning warning = new ScriptExecutionWarning(lineNumber, sourceCode);

    /**
     * Construction with {@code null} source code should fail.
     */
    @Test
    public void testConstructNullSourceCode() {
        expect(IllegalArgumentException.class);
        expect("Source code cannot be null.");
        new ScriptExecutionWarning(lineNumber, null);
    }

    /**
     * Construction with a zero line number should fail.
     */
    @Test
    public void testConstructZeroLineNumber() {
        expect(IllegalArgumentException.class);
        expect("Line number must be a non-zero, positive integer: " + Integer.toString(0));
        new ScriptExecutionWarning(0, sourceCode);
    }

    /**
     * Two warnings of different line numbers should be inequal.
     */
    @Test
    public void testEqualsDifferentLineNumber() {
        final ScriptExecutionWarning other = new ScriptExecutionWarning(lineNumber + 1, sourceCode);
        assertThat(warning).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(warning);
    }

    /**
     * Two warnings of different source code should be inequal.
     */
    @Test
    public void testEqualsDifferentSourceCode() {
        final ScriptExecutionWarning other = new ScriptExecutionWarning(lineNumber, StringUtils.reverse(sourceCode));
        assertThat(warning).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(warning);
    }

    /**
     * Test the retrieval of the line number.
     */
    @Test
    public void testGetLineNumber() {
        assertThat(warning.getLineNumber()).isEqualTo(lineNumber);
    }

    /**
     * Test the retrieval of the source code.
     */
    @Test
    public void testGetSourceCode() {
        assertThat(warning.getSourceCode()).isEqualTo(sourceCode);
    }

    @Override
    protected ScriptExecutionWarning getBean() {
        return warning;
    }

    @Override
    protected ScriptExecutionWarning newBeanFrom(final ScriptExecutionWarning otherBean) {
        return new ScriptExecutionWarning(otherBean.getLineNumber(), otherBean.getSourceCode());
    }

}
