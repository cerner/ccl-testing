package com.cerner.ccl.parser.text;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.parser.data.IncludeDocumentation;
import com.cerner.ccl.parser.data.IncludeFile;
import com.cerner.ccl.parser.data.record.RecordStructure;
import com.cerner.ccl.parser.data.subroutine.Subroutine;

/**
 * Unit tests for {@link IncludeFileFactory}.
 * 
 * @author Joshua Hyde
 * 
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { IncludeDocumentation.class, IncludeFile.class, IncludeFileFactory.class })
public class IncludeFileFactoryTest {
    private final IncludeFileFactory factory = new IncludeFileFactory();
    private final String objectName = "object.inc";
    @Mock
    private List<Subroutine> subroutines;
    @Mock
    private List<RecordStructure> recordStructures;

    /**
     * Test the construction of an include file object.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testBuild() throws Exception {
        final IncludeDocumentation doc = mock(IncludeDocumentation.class);

        final IncludeFile include = mock(IncludeFile.class);
        whenNew(IncludeFile.class).withArguments(objectName, doc, subroutines, recordStructures).thenReturn(include);
        assertThat(factory.build(objectName, doc, subroutines, recordStructures)).isEqualTo(include);
    }

    /**
     * Test the construction of an include file object.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testBuildNullDocumentation() throws Exception {
        final IncludeDocumentation doc = mock(IncludeDocumentation.class);
        whenNew(IncludeDocumentation.class).withNoArguments().thenReturn(doc);

        final IncludeFile include = mock(IncludeFile.class);
        whenNew(IncludeFile.class).withArguments(objectName, doc, subroutines, recordStructures).thenReturn(include);
        assertThat(factory.build(objectName, null, subroutines, recordStructures)).isEqualTo(include);
    }
}
