package com.cerner.ccl.j4ccl.impl.adders;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.adders.arguments.Argument;
import com.cerner.ccl.j4ccl.adders.arguments.FloatArgument;
import com.cerner.ccl.j4ccl.impl.CommandQueue;
import com.cerner.ccl.j4ccl.impl.commands.ScriptExecutionCommand;
import com.cerner.ccl.j4ccl.impl.commands.util.ScriptExecutionBuilder;
import com.cerner.ccl.j4ccl.record.Record;

/**
 * Unit tests for {@link ScriptExecutionAdderImpl}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { ScriptExecutionAdderImpl.class, ScriptExecutionCommand.class })
@SuppressWarnings("nls")
public class ScriptExecutionAdderImplTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    private static final String THE_SCRIPT_NAME = "script_name";
    @Mock
    private CommandQueue commandQueue;
    private ScriptExecutionAdderImpl adder;

    @Captor
    private ArgumentCaptor<List<Argument>> argumentCaptorListArgument;

    /**
     * Set up the adder for each test.
     */
    @Before
    public void setUp() {
        adder = new ScriptExecutionAdderImpl(THE_SCRIPT_NAME, commandQueue);
    }

    /**
     * Test the contribution of a script execution command.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCommit() throws Exception {
        final CommandQueue queue = mock(CommandQueue.class);
        final ScriptExecutionCommand command = mock(ScriptExecutionCommand.class);

        whenNew(ScriptExecutionCommand.class).withArguments(THE_SCRIPT_NAME, Collections.<String> emptyList(), false)
                .thenReturn(command);

        adder = new ScriptExecutionAdderImpl(THE_SCRIPT_NAME, queue);
        adder.commit();

        // Verify that the command was added to the queue
        verify(queue).addInCclSessionCommand(command);
    }

    /**
     * Test the contribution of a script execution command with a record structure.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCommitWithRecordStructure() throws Exception {
        final CommandQueue queue = mock(CommandQueue.class);
        final ScriptExecutionCommand command = mock(ScriptExecutionCommand.class);

        final String recordName = "request";
        final Record record = mock(Record.class);

        whenNew(ScriptExecutionCommand.class).withArguments(THE_SCRIPT_NAME, Collections.<String> emptyList(), false)
                .thenReturn(command);

        adder = new ScriptExecutionAdderImpl(THE_SCRIPT_NAME, queue);
        adder.withReplace(recordName, record).commit();

        verify(command, times(1)).addWithReplace(recordName.toUpperCase(), record);
    }

    /**
     * Test construction of an adder with a blank script name.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructionBlankScriptName() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Script name cannot be blank.");
        new ScriptExecutionAdderImpl("  ", commandQueue);
    }

    /**
     * Test construction of an adder with a null script name.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructionNullScriptName() {
        expected.expect(NullPointerException.class);
        expected.expectMessage("Script name cannot be null.");
        new ScriptExecutionAdderImpl(null, commandQueue);
    }

    /**
     * Test construction with a null command queue.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructionNullQueue() {
        expected.expect(NullPointerException.class);
        expected.expectMessage("Command queue cannot be null.");
        new ScriptExecutionAdderImpl(THE_SCRIPT_NAME, null);
    }

    /**
     * Test that the {@link ScriptExecutionBuilder} is constructed with the given arguments.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWithArguments() throws Exception {
        final ArgumentCaptor<Object> argumentCaptorAll = ArgumentCaptor.forClass(Object.class);
        final ScriptExecutionCommand command = mock(ScriptExecutionCommand.class);
        whenNew(ScriptExecutionCommand.class)
                .withArguments(argumentCaptorAll.capture(), argumentCaptorListArgument.capture(), eq(false))
                .thenReturn(command);

        final Argument arg1 = mock(Argument.class);
        final Argument arg2 = mock(Argument.class);
        adder.withArguments(arg1, arg2).commit();
        verify(commandQueue).addInCclSessionCommand(command);

        final String scriptName = (String) argumentCaptorAll.getAllValues().get(0);
        assertThat(scriptName).isEqualTo(THE_SCRIPT_NAME);
        final List<Argument> args = argumentCaptorListArgument.getAllValues().get(0);
        assertThat(args).containsOnly(arg1, arg2);
    }

    /**
     * Test that an exception is thrown if a null argument is specified.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWithArgumentsNull() throws Exception {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Null arguments are not allowed: ");

        FloatArgument floatArgumentNull = null;
        adder.withArguments(floatArgumentNull).commit();
    }

    /**
     * Test the {@link ScriptExecutionBuilder} with authentication.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testWithAuthentication() throws Exception {
        final ArgumentCaptor<Object> argumentCaptorAll = ArgumentCaptor.forClass(Object.class);
        final ScriptExecutionCommand command = mock(ScriptExecutionCommand.class);
        whenNew(ScriptExecutionCommand.class)
                .withArguments(argumentCaptorAll.capture(), argumentCaptorListArgument.capture(), eq(true))
                .thenReturn(command);

        final Argument arg1 = mock(Argument.class);
        final Argument arg2 = mock(Argument.class);
        adder.withArguments(arg1, arg2).withAuthentication(true).commit();
        verify(commandQueue).addInCclSessionCommand(command);

        final String scriptName = (String) argumentCaptorAll.getAllValues().get(0);
        assertThat(scriptName).isEqualTo(THE_SCRIPT_NAME);
        final List<Argument> args = argumentCaptorListArgument.getAllValues().get(0);
        assertThat(args).containsOnly(arg1, arg2);
    }

    /**
     * Test that adding an empty vararg array fails.
     */
    @Test
    public void testWithEmptyArguments() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("At least one argument must be supplied.");
        adder.withArguments();
    }

    /**
     * Test that adding a {@code null} argument fails.
     */
    @Test
    public void testWithNullArgument() {
        final Argument[] args = new Argument[] { mock(Argument.class), null, mock(Argument.class) };
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Null arguments are not allowed: " + Arrays.toString(args));
        adder.withArguments(args);
    }

    /**
     * Adding a {@code null} vararg array should fail.
     */
    @Test
    public void testWithNullArguments() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Arguments cannot be null.");
        adder.withArguments((Argument[]) null);
    }

    /**
     * Test that adding a record with a blank name that fails.
     */
    @Test
    public void testWithReplaceBlankRecordName() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Record name cannot be blank.");
        adder.withReplace("  ", mock(Record.class));
    }

    /**
     * Test that adding a null record fails.
     */
    @Test
    public void testWithReplaceNullRecord() {
        expected.expect(NullPointerException.class);
        expected.expectMessage("Record cannot be null.");
        adder.withReplace("recordName", null);
    }

    /**
     * Test that adding a record with a null name fails.
     */
    @Test
    public void testWithReplaceNullRecordName() {
        expected.expect(NullPointerException.class);
        expected.expectMessage("Record name cannot be null.");
        adder.withReplace(null, mock(Record.class));
    }
}