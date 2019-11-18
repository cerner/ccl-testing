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

import com.cerner.ccl.parser.data.CclScript;
import com.cerner.ccl.parser.data.ScriptDocumentation;
import com.cerner.ccl.parser.data.record.RecordStructure;
import com.cerner.ccl.parser.data.subroutine.Subroutine;

/**
 * Unit tests for {@link CclScriptFactory}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { CclScript.class, CclScriptFactory.class, ScriptDocumentation.class })
public class CclScriptFactoryTest {
    private final CclScriptFactory factory = new CclScriptFactory();
    private final String objectName = "object_name";
    @Mock
    private List<Subroutine> subroutines;
    @Mock
    private List<RecordStructure> recordStructures;

    /**
     * Test the building of a CCL script object.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testBuild() throws Exception {
        final ScriptDocumentation doc = mock(ScriptDocumentation.class);

        final CclScript script = mock(CclScript.class);
        whenNew(CclScript.class).withArguments(objectName, doc, subroutines, recordStructures).thenReturn(script);

        assertThat(factory.build(objectName, doc, subroutines, recordStructures)).isEqualTo(script);
    }

    /**
     * Test the building of a CCL script with {@code null} top-level documentation.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testBuildNullDocumentation() throws Exception {
        final ScriptDocumentation doc = mock(ScriptDocumentation.class);
        whenNew(ScriptDocumentation.class).withNoArguments().thenReturn(doc);

        final CclScript script = mock(CclScript.class);
        whenNew(CclScript.class).withArguments(objectName, doc, subroutines, recordStructures).thenReturn(script);

        assertThat(factory.build(objectName, null, subroutines, recordStructures)).isEqualTo(script);
    }
}
