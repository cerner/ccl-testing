package com.cerner.ccl.cdoc.velocity;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.cerner.ccl.cdoc.script.ScriptExecutionDetails;
import com.cerner.ccl.cdoc.velocity.navigation.Navigation;
import com.cerner.ccl.parser.data.IncludeFile;
import com.cerner.ccl.parser.data.record.RecordStructure;
import java.io.File;
import java.io.Writer;
import java.util.List;
import java.util.Locale;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Unit tests for {@link IncludeDocGenerator}.
 *
 * @author Joshua Hyde
 */
@SuppressWarnings("unused")
public class IncludeDocGeneratorTest {
    @Mock
    private IncludeFile includeFile;
    @Mock
    private Writer writer;
    @Mock
    private File cssDirectory;
    @Mock
    private ScriptExecutionDetails details;
    @Mock
    private Navigation backNavigation;
    private IncludeDocGenerator generator;

    /** Set up the generator for each test. */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        generator = new IncludeDocGenerator(includeFile, cssDirectory, details, writer, backNavigation);
    }

    /** Construction with a {@code null} {@link IncludeFile} object should fail. */
    @Test
    public void testConstructNullIncludeFile() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new IncludeDocGenerator(null, cssDirectory, details, writer, backNavigation);
        });
        assertThat(e.getMessage()).isEqualTo("Include file cannot be null.");
    }

    /** Test the retrieval of the object. */
    @Test
    public void testGetObject() {
        assertThat(generator.getObject()).isEqualTo(includeFile);
    }

    /** Test the retrieval of the filename. */
    @Test
    public void testGetObjectFilename() {
        final String includeFilename = "TEST.INC";
        when(includeFile.getName()).thenReturn(includeFilename);
        assertThat(generator.getObjectName()).isEqualTo(includeFilename.toLowerCase(Locale.US));
    }

    /** Test the retrieval of the object name. */
    @Test
    public void testGetObjectName() {
        final String includeFilename = "TEST.INC";
        when(includeFile.getName()).thenReturn(includeFilename);
        assertThat(generator.getObjectName()).isEqualTo(includeFilename.toLowerCase(Locale.US));
    }

    /** Test the retrieval of the record structures. */
    @Test
    public void testGetRecordStructures() {
        @SuppressWarnings("unchecked")
        final List<RecordStructure> structures = mock(List.class);
        when(includeFile.getRecordStructures()).thenReturn(structures);
        assertThat(generator.getRecordStructures()).isEqualTo(structures);
    }
}
