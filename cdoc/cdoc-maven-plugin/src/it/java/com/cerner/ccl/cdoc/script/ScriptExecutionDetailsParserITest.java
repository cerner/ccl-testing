package com.cerner.ccl.cdoc.script;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.cerner.ccl.cdoc.AbstractSourceReadingITest;

/**
 * Integration tests for {@link ScriptExecutionDetailsParser}.
 * 
 * @author Joshua Hyde
 * 
 */

public class ScriptExecutionDetailsParserITest extends AbstractSourceReadingITest {
    private final ScriptExecutionDetailsParser parser = new ScriptExecutionDetailsParser();

    /**
     * If the source script has a call to {@code checkprg}, then it should up as a warning.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetDetailsCallPrg() throws Exception {
        final ScriptExecutionDetails details = parser.getDetails(readCclScript("cdoc_callprg"));
        assertThat(details.getExecutedScripts()).isEmpty();
        assertThat(details.getWarnings()).hasSize(1);

        final ScriptExecutionWarning warning = details.getWarnings().iterator().next();
        assertThat(warning.getLineNumber()).isEqualTo(4);
        assertThat(warning.getSourceCode()).isEqualTo("set stat = callprg(\"some_script\")");
    }

    /**
     * If the source script has a {@code execute value()} invocation in it, then it should show up as a warning.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetDetailsExecuteValue() throws Exception {
        final ScriptExecutionDetails details = parser.getDetails(readCclScript("cdoc_execute_value"));
        assertThat(details.getExecutedScripts()).isEmpty();
        assertThat(details.getWarnings()).hasSize(1);

        final ScriptExecutionWarning warning = details.getWarnings().iterator().next();
        assertThat(warning.getLineNumber()).isEqualTo(5);
        assertThat(warning.getSourceCode()).isEqualTo("execute value(script_name)");
    }

    /**
     * Test the parsing of executed scripts.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetDetailsExecutedScripts() throws Exception {
        final ScriptExecutionDetails details = parser.getDetails(readCclScript("cdoc_execute_script"));
        assertThat(details.getWarnings()).isEmpty();
        assertThat(details.getExecutedScripts()).containsOnly("scripta", "scriptb");
    }

    /**
     * If there's excessive whitespace between the {@code execute} keyword and the name of the script, it should be ignored during parsing.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetDetailsIgnoreWhitespace() throws Exception {
        final ScriptExecutionDetails details = parser.getDetails(readCclScript("cdoc_execute_script_spaces"));
        assertThat(details.getWarnings()).isEmpty();
        assertThat(details.getExecutedScripts()).containsOnly("test_script");
    }
}
