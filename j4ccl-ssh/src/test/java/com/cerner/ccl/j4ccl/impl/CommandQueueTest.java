package com.cerner.ccl.j4ccl.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.cerner.ccl.j4ccl.impl.commands.AbstractCclCommand;
import com.cerner.ccl.j4ccl.ssh.CclCommandTerminal;

/**
 * Unit test of {@link CommandQueue}.
 *
 * @author Joshua Hyde
 *
 */

public class CommandQueueTest {
    @Mock
    private AbstractCclCommand command;
    private CommandQueue queue;

    /**
     * Create a new queue for each test.
     */
    @Before
    public void setUp() {
        queue = new CommandQueue();
    }

    /**
     * Verify the addition of an object to the in-CCL-session queue.
     */
    @Test
    public void testAddInCclSessionCommand() {
        final CommandQueue response = queue.addInCclSessionCommand(command);
        assertThat(queue.getInCclSessionCommands()).containsOnly(command);
        assertThat(response).isEqualTo(queue);
    }

    /**
     * Verify the addition of an object to the on-CCL-session-close queue.
     */
    @Test
    public void testAddOnCclCloseCommand() {
        final CommandQueue response = queue.addOnCclCloseCommand(command);
        assertThat(queue.getOnCclCloseCommands()).containsOnly(command);
        assertThat(response).isEqualTo(queue);
    }

    /**
     * Verify the addition of an object to the on-CCL-session-start queue.
     */
    @Test
    public void testAddOnCclStartCommand() {
        final CommandQueue response = queue.addOnCclStartCommand(command);
        assertThat(queue.getOnCclStartCommands()).containsOnly(command);
        assertThat(response).isEqualTo(queue);
    }

    /**
     * Test that commands in all queues are executed.
     */
    @Test
    public void testExecute() {
        final AbstractCclCommand onStartCommand = mock(AbstractCclCommand.class);
        final AbstractCclCommand inSessionCommand = mock(AbstractCclCommand.class);
        final AbstractCclCommand onCloseCommand = mock(AbstractCclCommand.class);

        final CclCommandTerminal terminal = mock(CclCommandTerminal.class);

        queue.addInCclSessionCommand(inSessionCommand).addOnCclCloseCommand(onCloseCommand)
                .addOnCclStartCommand(onStartCommand).execute(terminal);

        verify(onStartCommand).run(terminal);
        verify(inSessionCommand).run(terminal);
        verify(onCloseCommand).run(terminal);
    }

    /**
     * Verify that, if an in-session command fails, the on-close commands are still executed.
     */
    @Test()
    public void testExecuteInSessionError() {
        final CclCommandTerminal terminal = mock(CclCommandTerminal.class);

        final AbstractCclCommand inSessionCommand = mock(AbstractCclCommand.class);
        doThrow(new UnsupportedOperationException()).when(inSessionCommand).run(terminal);
        final AbstractCclCommand onCloseCommand = mock(AbstractCclCommand.class);

        queue.addInCclSessionCommand(inSessionCommand).addOnCclCloseCommand(onCloseCommand);
        try {
            queue.execute(terminal);
            fail("No exception thrown.");
        } catch (final UnsupportedOperationException e) {
            // This should happen
        }

        verify(onCloseCommand).run(terminal);
    }

    /**
     * Verify that, even if a session-start command fails, the on-close commands are still executed.
     */
    @Test
    public void testExecuteOnStartError() {
        final CclCommandTerminal terminal = mock(CclCommandTerminal.class);

        final AbstractCclCommand onStartCommand = mock(AbstractCclCommand.class);
        doThrow(new UnsupportedOperationException()).when(onStartCommand).run(terminal);
        final AbstractCclCommand onCloseCommand = mock(AbstractCclCommand.class);

        queue.addOnCclStartCommand(onStartCommand).addOnCclCloseCommand(onCloseCommand);
        try {
            queue.execute(terminal);
            fail("No exception thrown.");
        } catch (final UnsupportedOperationException e) {
            // This should happen
        }

        verify(onCloseCommand).run(terminal);
    }

    /**
     * Test that the queue accurately reflects empty status when
     * {@link CommandQueue#addInCclSessionCommand(AbstractCclCommand)} is invoked.
     */
    @Test
    public void testIsEmptyInCclSession() {
        assertThat(queue.isEmpty()).isTrue();
        queue.addInCclSessionCommand(command);
        assertThat(queue.isEmpty()).isFalse();
    }

    /**
     * Test that the queue accurately reflects empty status when
     * {@link CommandQueue#addOnCclCloseCommand(AbstractCclCommand)} is invoked.
     */
    @Test
    public void testIsEmptyOnCclClose() {
        assertThat(queue.isEmpty()).isTrue();
        queue.addOnCclCloseCommand(command);
        assertThat(queue.isEmpty()).isFalse();
    }

    /**
     * Test that the queue accurately reflects empty status when
     * {@link CommandQueue#addOnCclStartCommand(AbstractCclCommand)} is invoked.
     */
    @Test
    public void testIsEmptyOnCclStart() {
        assertThat(queue.isEmpty()).isTrue();
        queue.addOnCclStartCommand(command);
        assertThat(queue.isEmpty()).isFalse();
    }
}