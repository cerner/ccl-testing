package com.cerner.ccl.j4ccl.impl.adders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.util.Collections;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.impl.CommandQueue;
import com.cerner.ccl.j4ccl.impl.commands.ScriptCompilerCommand;
import com.cerner.ccl.j4ccl.internal.AbstractUnitTest;

/**
 * Unit tests for {@link ScriptCompilerAdderImpl}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { ScriptCompilerAdderImpl.class, ScriptCompilerCommand.class })
public class ScriptCompilerAdderImplTest extends AbstractUnitTest {
    @Mock
    private File sourceCodeFile;
    @Mock
    private CommandQueue queue;
    private ScriptCompilerAdderImpl adder;

    /**
     * Set up the compiler adder for each test.
     */
    @Before
    public void setUp() {
        when(sourceCodeFile.getName()).thenReturn("a_script.prg");
        adder = new ScriptCompilerAdderImpl(sourceCodeFile, queue);
    }

    /**
     * Construction with a non-PRG should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNotPrg() {
        final File notPrg = mock(File.class);
        when(notPrg.getName()).thenReturn("not_prg.inc");

        expect(IllegalArgumentException.class);
        expect("Source code file must be a .PRG file.");
        new ScriptCompilerAdderImpl(notPrg, queue);
    }

    /**
     * Construction with a {@code null} {@link CommandQueue} should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullCommandQueue() {
        expect(NullPointerException.class);
        expect("Command queue cannot be null.");
        new ScriptCompilerAdderImpl(sourceCodeFile, null);
    }

    /**
     * Construction with a {@code null} source code file should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullSourceCodeFile() {
        expect(NullPointerException.class);
        expect("Source code cannot be null.");
        new ScriptCompilerAdderImpl(null, queue);
    }

    /**
     * Test committing a script compiler command.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCommit() throws Exception {
        final File dependencyFile = mock(File.class);
        final File listingFile = mock(File.class);

        final ScriptCompilerCommand command = mock(ScriptCompilerCommand.class);
        whenNew(ScriptCompilerCommand.class).withArguments(sourceCodeFile, Collections.singleton(dependencyFile),
                listingFile, Boolean.FALSE).thenReturn(command);
        adder.withDependency(dependencyFile).withListingOutput(listingFile).commit();
        verify(queue).addInCclSessionCommand(command);
    }

    /**
     * Test committing a script compiler command with debug.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCommitWithDebug() throws Exception {
        final File dependencyFile = mock(File.class);
        final File listingFile = mock(File.class);

        final ScriptCompilerCommand command = mock(ScriptCompilerCommand.class);
        whenNew(ScriptCompilerCommand.class)
                .withArguments(sourceCodeFile, Collections.singleton(dependencyFile), listingFile, Boolean.TRUE)
                .thenReturn(command);
        adder.withListingOutput(listingFile).withDebugModeEnabled(true).withDependency(dependencyFile).commit();
        verify(queue).addInCclSessionCommand(command);
    }
}