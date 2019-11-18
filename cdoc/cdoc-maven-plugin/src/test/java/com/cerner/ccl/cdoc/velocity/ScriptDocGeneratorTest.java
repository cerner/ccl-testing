package com.cerner.ccl.cdoc.velocity;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.cerner.ccl.cdoc.script.ScriptExecutionDetails;
import com.cerner.ccl.cdoc.velocity.navigation.Navigation;
import com.cerner.ccl.parser.data.CclScript;
import com.cerner.ccl.parser.data.record.RecordStructure;

/**
 * Unit tests for {@link ScriptDocGenerator}.
 *
 * @author Joshua Hyde
 *
 */
@SuppressWarnings("unused")
public class ScriptDocGeneratorTest {
    @Mock
    private CclScript script;
    @Mock
    private Writer writer;
    @Mock
    private File cssDirectory;
    @Mock
    private ScriptExecutionDetails executionDetails;
    @Mock
    private Navigation backNavigation;
    private ScriptDocGenerator generator;

    /**
     * Set up the generator for each test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        generator = new ScriptDocGenerator(script, cssDirectory, executionDetails, writer, backNavigation);
    }

    /**
     * Construction with a {@code null} CCL script should fail.
     */
    @Test
    public void testConstructNullCclScript() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new ScriptDocGenerator(null, cssDirectory, executionDetails, writer, backNavigation);
        });
        assertThat(e.getMessage()).isEqualTo("CCL script cannot be null.");
    }

    /**
     * Test the retrieval of the object.
     */
    @Test
    public void testGetObject() {
        assertThat(generator.getObject()).isEqualTo(script);
    }

    /**
     * Test the retrieval of the object filename.
     */
    @Test
    public void testGetObjectFilename() {
        final String objectName = "objECT_name";
        when(script.getName()).thenReturn(objectName);
        assertThat(generator.getObjectFilename()).isEqualTo(objectName.toLowerCase(Locale.US) + ".prg");
    }

    /**
     * Test the retrieval of the object name.
     */
    @Test
    public void testGetObjectName() {
        final String objectName = "object_name";
        when(script.getName()).thenReturn(objectName);
        assertThat(generator.getObjectName()).isEqualTo(objectName);
    }

    /**
     * Test the retrieval of the record structures.
     */
    @Test
    public void testGetRecordStructures() {
        @SuppressWarnings("unchecked")
        final List<RecordStructure> structures = mock(List.class);
        when(script.getRecordStructures()).thenReturn(structures);
        assertThat(generator.getRecordStructures()).isEqualTo(structures);
    }
}
