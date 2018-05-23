package com.cerner.ccl.j4ccl.impl.adders;

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
import com.cerner.ccl.j4ccl.internal.AbstractUnitTest;

/**
 * Unit tests for {@link ScriptDropAdderImpl}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { DropScriptCommand.class, ScriptDropAdderImpl.class })
public class ScriptDropAdderImplTest extends AbstractUnitTest {
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
        expect(IllegalArgumentException.class);
        expect("Script name cannot be blank.");
        new ScriptDropAdderImpl("", queue);
    }

    /**
     * Construction with a {@code null} {@link CommandQueue} should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullCommandQueue() {
        expect(NullPointerException.class);
        expect("Command queue cannot be null.");
        new ScriptDropAdderImpl(scriptName, null);
    }

    /**
     * Construction with a {@code null} script name should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullScriptName() {
        expect(NullPointerException.class);
        expect("Script name cannot be null.");
        new ScriptDropAdderImpl(null, queue);
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
