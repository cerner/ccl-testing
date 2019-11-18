package com.cerner.ccl.parser.data;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cerner.ccl.parser.AbstractBeanUnitTest;
import com.cerner.ccl.parser.data.record.RecordStructure;
import com.cerner.ccl.parser.data.subroutine.Subroutine;

/**
 * Unit tests for {@link CclScript}.
 *
 * @author Joshua Hyde
 *
 */

@SuppressWarnings("unused")
public class CclScriptTest extends AbstractBeanUnitTest<CclScript> {
    private final String scriptName = "script_name";
    @Mock
    private ScriptDocumentation documentation;
    @Mock
    private Subroutine subroutine;
    @Mock
    private RecordStructure recordStructure;
    private List<Subroutine> subroutines;
    private List<RecordStructure> recordStructures;
    private CclScript script;

    /**
     * Set up the CCL script for each test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        subroutines = Collections.singletonList(subroutine);
        recordStructures = Collections.singletonList(recordStructure);
        script = new CclScript(scriptName, documentation, subroutines, recordStructures);
    }

    /**
     * Construction with a {@code null} {@link ScriptDocumentation} should fail.
     */
    @Test
    public void testConstructNullScriptDocumentation() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new CclScript(scriptName, null, subroutines, recordStructures);
        });
        assertThat(e.getMessage()).isEqualTo("Script documentation cannot be null.");
    }

    /**
     * Construction with a {@code null} script name should fail.
     */
    @Test
    public void testConstructNullScriptName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new CclScript(null, documentation, subroutines, recordStructures);
        });
        assertThat(e.getMessage()).isEqualTo("Script name cannot be null.");
    }

    /**
     * Construction with a {@code null} {@link RecordStructure} list should fail.
     */
    @Test
    public void testConstructNullRecordStructures() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new CclScript(scriptName, documentation, subroutines, null);
        });
        assertThat(e.getMessage()).isEqualTo("Record structures cannot be null.");
    }

    /**
     * Construction with a {@code null} {@link Subroutine} list should fail.
     */
    @Test
    public void testConstructNullSubroutines() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new CclScript(scriptName, documentation, null, recordStructures);
        });
        assertThat(e.getMessage()).isEqualTo("Subroutines cannot be null.");
    }

    /**
     * Two scripts by different names should not be equal.
     */
    @Test
    public void testEqualsDifferentName() {
        final CclScript other = new CclScript(StringUtils.reverse(scriptName), documentation, subroutines,
                recordStructures);
        assertThat(script).isNotEqualTo(other);
    }

    /**
     * Two objects with different record structures should not be equal.
     */
    @Test
    public void testEqualsDifferentRecordStructures() {
        final CclScript other = new CclScript(scriptName, documentation, subroutines,
                Collections.<RecordStructure> emptyList());
        assertThat(script).isNotEqualTo(other);
    }

    /**
     * Two objects with different script documentation should not be equal.
     */
    @Test
    public void testEqualsDifferentScriptDocumentation() {
        final CclScript other = new CclScript(scriptName, mock(ScriptDocumentation.class), subroutines,
                recordStructures);
        assertThat(script).isNotEqualTo(other);
    }

    /**
     * Two objects with different subroutines should not be equal.
     */
    @Test
    public void testEqualsDifferentSubroutines() {
        final CclScript other = new CclScript(scriptName, documentation, Collections.<Subroutine> emptyList(),
                recordStructures);
        assertThat(script).isNotEqualTo(other);
    }

    /**
     * Test the retrieval of record structures.
     */
    @Test
    public void testGetRecordStructures() {
        assertThat(script.getRecordStructures()).isEqualTo(recordStructures);
    }

    /**
     * Test the retrieval of script documentation.
     */
    @Test
    public void testGetScriptDocumentation() {
        assertThat(script.getScriptDocumentation()).isEqualTo(documentation);
    }

    /**
     * Test the retrieval of the script name.
     */
    @Test
    public void testGetScriptName() {
        assertThat(script.getName()).isEqualTo(scriptName);
    }

    /**
     * Test the retrieval of the subroutines.
     */
    @Test
    public void testGetSubroutines() {
        assertThat(script.getSubroutines()).isEqualTo(subroutines);
    }

    @Override
    protected CclScript getBean() {
        return script;
    }

    @Override
    protected CclScript newBeanFrom(final CclScript otherBean) {
        return new CclScript(otherBean.getName(), otherBean.getScriptDocumentation(), otherBean.getSubroutines(),
                otherBean.getRecordStructures());
    }
}
