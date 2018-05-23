package com.cerner.ccl.cdoc.script;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.cerner.ccl.cdoc.AbstractBeanUnitTest;

/**
 * Unit tests for {@link ScriptExecutionDetails}.
 *
 * @author Joshua Hyde
 *
 */
@SuppressWarnings("unused")
public class ScriptExecutionDetailsTest extends AbstractBeanUnitTest<ScriptExecutionDetails> {
    private final Set<String> executedScripts = Collections.singleton("executed_script");
    private final List<ScriptExecutionWarning> warnings = Collections.singletonList(mock(ScriptExecutionWarning.class));
    private final ScriptExecutionDetails details = new ScriptExecutionDetails(executedScripts, warnings);

    /**
     * Construction with a {@code null} {@link Set} of executed scripts should fail.
     */
    @Test
    public void testConstructNullExecutedScripts() {
        expect(IllegalArgumentException.class);
        expect("Executed scripts cannot be null.");
        new ScriptExecutionDetails(null, warnings);
    }

    /**
     * Construction with a {@code null} {@link Set} of script execution warnings should fail.
     */
    @Test
    public void testConstructNullWarnings() {
        expect(IllegalArgumentException.class);
        expect("Warnings cannot be null.");
        new ScriptExecutionDetails(executedScripts, null);
    }

    /**
     * Two objects with different executed scripts should be inequal.
     */
    @Test
    public void testEqualsDifferentExecutedScripts() {
        final ScriptExecutionDetails other = new ScriptExecutionDetails(Collections.singleton("test"), warnings);
        assertThat(details).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(details);
    }

    /**
     * Two objects with different script execution warnings should be inequal.
     */
    @Test
    public void testEqualsDifferentScriptExecutionWarnings() {
        final ScriptExecutionDetails other = new ScriptExecutionDetails(executedScripts,
                Collections.singletonList(mock(ScriptExecutionWarning.class)));
        assertThat(details).isNotEqualTo(other);
        assertThat(other).isNotEqualTo(details);
    }

    /**
     * Test the retrieval of the executed scripts.
     */
    @Test
    public void testGetExecutedScripts() {
        assertThat(details.getExecutedScripts()).isEqualTo(executedScripts);
    }

    /**
     * Test the retrieval of the warnings.
     */
    @Test
    public void testGetWarnings() {
        assertThat(details.getWarnings()).isEqualTo(warnings);
    }

    @Override
    protected ScriptExecutionDetails getBean() {
        return details;
    }

    @Override
    protected ScriptExecutionDetails newBeanFrom(final ScriptExecutionDetails otherBean) {
        return new ScriptExecutionDetails(otherBean.getExecutedScripts(), otherBean.getWarnings());
    }

}
