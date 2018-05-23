package com.cerner.ccl.j4ccl.impl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareOnlyThisForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.TerminalProperties;
import com.cerner.ccl.j4ccl.adders.ScriptDropAdder;
import com.cerner.ccl.j4ccl.enums.OutputType;
import com.cerner.ccl.j4ccl.impl.adders.DynamicCompilerAdderImpl;
import com.cerner.ccl.j4ccl.impl.adders.ScriptCompilerAdderImpl;
import com.cerner.ccl.j4ccl.impl.adders.ScriptDropAdderImpl;
import com.cerner.ccl.j4ccl.impl.adders.ScriptExecutionAdderImpl;
import com.cerner.ccl.j4ccl.impl.commands.ScriptExecutionCommand;
import com.cerner.ccl.j4ccl.impl.util.OutputStreamConfiguration;
import com.cerner.ccl.j4ccl.internal.AbstractUnitTest;
import com.cerner.ccl.j4ccl.ssh.CclCommandTerminal;

/**
 * Unit tests for {@link BaseCclExecutor}.
 *
 * @author Joshua Hyde
 * @author Fred Eckertson
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareOnlyThisForTest(value = { DynamicCompilerAdderImpl.class, ScriptCompilerAdderImpl.class,
        CclCommandTerminal.class, BaseCclExecutor.class })
public class BaseCclExecutorTest extends AbstractUnitTest {
    private static final TerminalProperties basicTerminalProperties = TerminalProperties.getNewBuilder()
            .setOsPromptPattern("osPromptPattern").build();
    @Mock
    private CommandQueue queue;
    private BaseCclExecutor executor;

    /**
     * One time initialization
     */
    @BeforeClass
    public static void setupOnce() {
        TerminalProperties.setGlobalTerminalProperties(
                TerminalProperties.getNewBuilder().setOsPromptPattern("osPromptPattern").build());
    }

    /**
     * Set up the execut for each test.
     */
    @Before
    public void setUp() {
        executor = new BaseCclExecutor(TerminalProperties.getGlobalTerminalProperties(), queue);
    }

    /**
     * Confirms that the default constructor creates a BaseCclExecutor with blank TerminalProperties and CommandQueue.
     */
    @Test
    public void testNullaryConstructor() {
        final BaseCclExecutor baseCclExecutor = new BaseCclExecutor();
        assertThat(baseCclExecutor.getTerminalProperties()).isEqualTo(TerminalProperties.getGlobalTerminalProperties());
        assertThat(baseCclExecutor.getCommandQueue()).isNotNull();
        assertThat(baseCclExecutor.getCommandQueue().getInCclSessionCommands()).isEmpty();
        assertThat(baseCclExecutor.getCommandQueue().getOnCclCloseCommands()).isEmpty();
        assertThat(baseCclExecutor.getCommandQueue().getOnCclStartCommands()).isEmpty();
    }

    /**
     * Confirms that a NullPointerException is thrown if no command queue is provided for construction.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructionRequiresCommandQueue() {
        expect(NullPointerException.class);
        expect("Command queue cannot be null.");
        new BaseCclExecutor(TerminalProperties.getGlobalTerminalProperties(), null);
    }

    /**
     * Test the adding of a dynamic compiler.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddDynamicCompiler() throws Exception {
        final File file = mock(File.class);
        when(file.isFile()).thenReturn(true);

        final DynamicCompilerAdderImpl adder = mock(DynamicCompilerAdderImpl.class);
        whenNew(DynamicCompilerAdderImpl.class).withArguments(file, queue).thenReturn(adder);

        final DynamicCompilerAdderImpl returnedAdder = executor.addDynamicCompiler(file);
        assertThat(returnedAdder).isNotNull();
        assertThat(returnedAdder).isEqualTo(adder);
    }

    /**
     * Adding a dynamic compiler with a non-file should fail.
     *
     * @throws Exception
     *             Not expected but sometimes bad things happen.
     */
    @SuppressWarnings("unused")
    @Test
    public void testAddDynamicCompilerNotFile() throws Exception {
        expect(IllegalArgumentException.class);
        expect("Given file pointer is not actually a file.");
        final File file = mock(File.class);
        when(file.isFile()).thenReturn(false);

        final DynamicCompilerAdderImpl adder = mock(DynamicCompilerAdderImpl.class);
        whenNew(DynamicCompilerAdderImpl.class).withArguments(file, queue).thenReturn(adder);

        final DynamicCompilerAdderImpl returnedAdder = executor.addDynamicCompiler(file);
    }

    /**
     * Adding a dynamic compiler with a {@code null} file should fail.
     */
    @Test
    public void testAddDynamicCompilerNullFile() {
        expect(NullPointerException.class);
        expect("File cannot be null.");
        executor.addDynamicCompiler(null);
    }

    /**
     * Test the addition of a script compiler.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddScriptCompiler() throws Exception {
        final File file = mock(File.class);
        when(file.isFile()).thenReturn(Boolean.TRUE);

        final ScriptCompilerAdderImpl adder = mock(ScriptCompilerAdderImpl.class);
        whenNew(ScriptCompilerAdderImpl.class).withArguments(file, queue).thenReturn(adder);

        final ScriptCompilerAdderImpl returnedAdder = executor.addScriptCompiler(file);
        assertThat(returnedAdder).isEqualTo(adder);
        assertThat(returnedAdder).isNotNull();
    }

    /**
     * Adding a script with a non-file should fail.
     */
    @Test
    public void testAddScriptCompilerNotFile() {
        expect(IllegalArgumentException.class);
        expect("Given file pointer is not actually a file.");
        final File file = mock(File.class);
        when(file.isFile()).thenReturn(Boolean.FALSE);
        executor.addScriptCompiler(file);
    }

    /**
     * Adding a script compiler with a {@code null} file should fail.
     */
    @Test
    public void testAddScriptCompilerNullFile() {
        expect(NullPointerException.class);
        expect("File cannot be null.");
        executor.addScriptCompiler(null);
    }

    /**
     * Test the addition of a script dropper.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddScriptDropper() throws Exception {
        final String scriptName = "asdf";

        final ScriptDropAdderImpl adder = mock(ScriptDropAdderImpl.class);
        whenNew(ScriptDropAdderImpl.class).withArguments(scriptName, queue).thenReturn(adder);

        final ScriptDropAdder returnedAdder = executor.addScriptDropper(scriptName);
        assertThat(returnedAdder).isNotNull();
        assertThat(returnedAdder).isEqualTo(adder);
    }

    /**
     * The addition of a script dropper with a blank name should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testAddScriptDropperBlankScriptName() {
        expect(IllegalArgumentException.class);
        expect("Script name cannot be blank.");
        final ScriptDropAdder returnedAdder = executor.addScriptDropper("");
    }

    /**
     * Adding a script dropper for a null name should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testAddScriptDropperNullScriptName() {
        expect(NullPointerException.class);
        expect("Script name cannot be null.");
        final ScriptDropAdder returnedAdder = executor.addScriptDropper(null);
    }

    /**
     * Test the addition of a script execution.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testAddScriptExecution() throws Exception {
        final String scriptName = "a script";

        final ScriptExecutionAdderImpl adder = mock(ScriptExecutionAdderImpl.class);
        whenNew(ScriptExecutionAdderImpl.class).withArguments(scriptName, queue).thenReturn(adder);

        assertThat(executor.addScriptExecution(scriptName)).isEqualTo(adder);
    }

    /**
     * Adding a script execution with a blank name should fail.
     */
    @Test
    public void testAddScriptExecutionBlankName() {
        expect(IllegalArgumentException.class);
        expect("Script name cannot be blank.");
        executor.addScriptExecution("");
    }

    /**
     * Adding a script execution with a {@code null} name should fail.
     */
    @Test
    public void testAddScriptExecutionNullName() {
        expect(NullPointerException.class);
        expect("Script name cannot be null.");
        executor.addScriptExecution(null);

    }

    /**
     * Construction with a {@code null} {@link CommandQueue} should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullCommandQueue() {
        expect(NullPointerException.class);
        expect("Command queue cannot be null.");
        new BaseCclExecutor(null, null);
    }

    /**
     * Test that the execution an executor calls run on its commands.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecute() throws Exception {
        final CclCommandTerminal terminal = mock(CclCommandTerminal.class);
        whenNew(CclCommandTerminal.class).withAnyArguments().thenReturn(terminal);

        final ScriptExecutionCommand scriptExecutionCommandA0 = mock(ScriptExecutionCommand.class);
        final ScriptExecutionCommand scriptExecutionCommandA1 = mock(ScriptExecutionCommand.class);
        final ScriptExecutionCommand scriptExecutionCommandA2 = mock(ScriptExecutionCommand.class);

        final ScriptExecutionCommand scriptExecutionCommandB0 = mock(ScriptExecutionCommand.class);
        final ScriptExecutionCommand scriptExecutionCommandB1 = mock(ScriptExecutionCommand.class);
        final ScriptExecutionCommand scriptExecutionCommandB2 = mock(ScriptExecutionCommand.class);

        final ScriptExecutionCommand scriptExecutionCommandC0 = mock(ScriptExecutionCommand.class);
        final ScriptExecutionCommand scriptExecutionCommandC1 = mock(ScriptExecutionCommand.class);
        final ScriptExecutionCommand scriptExecutionCommandC2 = mock(ScriptExecutionCommand.class);

        final CommandQueue commandQueue = new CommandQueue();

        commandQueue.addOnCclStartCommand(scriptExecutionCommandA0).addOnCclStartCommand(scriptExecutionCommandA1)
                .addOnCclStartCommand(scriptExecutionCommandA2);

        commandQueue.addInCclSessionCommand(scriptExecutionCommandB0).addInCclSessionCommand(scriptExecutionCommandB1)
                .addInCclSessionCommand(scriptExecutionCommandB2);

        commandQueue.addOnCclCloseCommand(scriptExecutionCommandC0).addOnCclCloseCommand(scriptExecutionCommandC1)
                .addOnCclCloseCommand(scriptExecutionCommandC2);

        new BaseCclExecutor(null, commandQueue).execute();

        verify(scriptExecutionCommandA0).run(terminal);
        verify(scriptExecutionCommandA1).run(terminal);
        verify(scriptExecutionCommandA2).run(terminal);

        verify(scriptExecutionCommandB0).run(terminal);
        verify(scriptExecutionCommandB1).run(terminal);
        verify(scriptExecutionCommandB2).run(terminal);

        verify(scriptExecutionCommandC0).run(terminal);
        verify(scriptExecutionCommandC1).run(terminal);
        verify(scriptExecutionCommandC2).run(terminal);

        final ArgumentCaptor<OutputStreamConfiguration> oscCaptor = ArgumentCaptor
                .forClass(OutputStreamConfiguration.class);
        final ArgumentCaptor<TerminalProperties> tpCaptor = ArgumentCaptor.forClass(TerminalProperties.class);
        PowerMockito.verifyNew(CclCommandTerminal.class).withArguments(tpCaptor.capture(), oscCaptor.capture());
        assertThat(tpCaptor.getAllValues().get(0)).isEqualTo(basicTerminalProperties);
        assertThat(oscCaptor.getAllValues().get(0)).isEqualTo(null);
    }

    /**
     * If the queue is empty, then the executor shouldn't attempt to execute anything.
     */
    @Test
    public void testExecuteEmptyQueue() {
        when(queue.isEmpty()).thenReturn(Boolean.TRUE);
        executor.execute();
        verify(queue, never()).execute(any(CclCommandTerminal.class));
    }

    /**
     * Test the execution of the executor.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecuteWithOutputConfiguration() throws Exception {
        final OutputStream stream = mock(OutputStream.class);
        final OutputType type = OutputType.CCL_SESSION;

        final OutputStreamConfiguration config = mock(OutputStreamConfiguration.class);
        whenNew(OutputStreamConfiguration.class).withArguments(stream, type).thenReturn(config);

        final CclCommandTerminal terminal = mock(CclCommandTerminal.class);
        whenNew(CclCommandTerminal.class).withAnyArguments().thenReturn(terminal);

        executor.setOutputStream(stream, type);
        executor.execute();

        verify(queue).execute(terminal);
        final ArgumentCaptor<OutputStreamConfiguration> oscCaptor = ArgumentCaptor
                .forClass(OutputStreamConfiguration.class);
        final ArgumentCaptor<TerminalProperties> tpCaptor = ArgumentCaptor.forClass(TerminalProperties.class);
        PowerMockito.verifyNew(CclCommandTerminal.class).withArguments(tpCaptor.capture(), oscCaptor.capture());
        assertThat(tpCaptor.getAllValues().get(0)).isEqualTo(basicTerminalProperties);
        assertThat(oscCaptor.getAllValues().get(0)).isEqualTo(config);
    }

    /**
     * Test the retrieval of the command queue.
     */
    @Test
    public void testGetCommandQueue() {
        assertThat(executor.getCommandQueue()).isEqualTo(queue);
    }

    /**
     * Test the setting of the output stream.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSetOutputStream() throws Exception {
        final OutputStream stream = mock(OutputStream.class);
        final OutputType type = OutputType.CCL_SESSION;

        final OutputStreamConfiguration config = mock(OutputStreamConfiguration.class);
        whenNew(OutputStreamConfiguration.class).withArguments(stream, type).thenReturn(config);

        assertThat(executor.getOutputStreamConfiguration()).isNull();
        executor.setOutputStream(stream, type);
        assertThat(executor.getOutputStreamConfiguration()).isEqualTo(config);
    }

    /**
     * Test the setting of the terminal properties.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSetTerminalProperties() throws Exception {
        assertThat(executor.getTerminalProperties()).isEqualTo(TerminalProperties.getGlobalTerminalProperties());
        final TerminalProperties terminalProperties = TerminalProperties.getNewBuilder().setSkipEnvset(true)
                .setOsPromptPattern("osPromptPattern").setCclPromptPattern("cclPromptPattern").build();
        executor.setTerminalProperties(terminalProperties);
        assertThat(executor.getTerminalProperties()).isEqualTo(terminalProperties);
    }
}