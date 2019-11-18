package com.cerner.ccl.j4ccl.impl.adders;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.impl.CommandQueue;
import com.cerner.ccl.j4ccl.impl.commands.DropScriptCommand;

/**
 * Unit tests for {@link ScriptDropAdderImpl}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { DropScriptCommand.class, ScriptDropAdderImpl.class })
public class ScriptDropAdderImplTest {
    private final String scriptName = "a.script.name";
    @Mock
    private CommandQueue queue;
    private ScriptDropAdderImpl adder;

    /**
     * Set up the adder for each test.
     */
    @Before
    public void setUp() {
        adder = new ScriptDropAdderImpl(scriptName, queue);
    }

    /**
     * Construction with a blank script name should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructBlankScriptName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new ScriptDropAdderImpl("", queue);
        });
        assertThat(e.getMessage()).isEqualTo("Script name cannot be blank.");
    }

    /**
     * Construction with a {@code null} {@link CommandQueue} should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullCommandQueue() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            new ScriptDropAdderImpl(scriptName, null);
        });
        assertThat(e.getMessage()).isEqualTo("Command queue cannot be null.");
    }

    /**
     * Construction with a {@code null} script name should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullScriptName() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            new ScriptDropAdderImpl(null, queue);
        });
        assertThat(e.getMessage()).isEqualTo("Script name cannot be null.");
    }

    /**
     * Test the addition of a script dropper command.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCommit() throws Exception {
        final DropScriptCommand command = mock(DropScriptCommand.class);
        whenNew(DropScriptCommand.class).withArguments(scriptName).thenReturn(command);

        adder.commit();

        verify(queue).addOnCclCloseCommand(command);
    }
}
