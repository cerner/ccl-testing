package com.cerner.ccl.j4ccl.impl.adders;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.impl.CommandQueue;
import com.cerner.ccl.j4ccl.impl.commands.ScriptCompilerCommand;

/**
 * Unit tests for {@link ScriptCompilerAdderImpl}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { ScriptCompilerAdderImpl.class, ScriptCompilerCommand.class, FileUtils.class })
public class ScriptCompilerAdderImplTest {
    @Mock
    private CommandQueue queue;

    /**
     * Set up the compiler adder for each test.
     *
     * @throws IOException
     *             Not expected.
     */
    @Before
    public void setUp() throws IOException {
    }

    /**
     * Construction with a non-PRG should fail.
     *
     * @throws Exception
     *             Sometimes bad things happen.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNotPrg() throws Exception {
        final File notPrg = mock(File.class);
        when(notPrg.getName()).thenReturn("not_prg.inc");
        StringBuilder sbProgramCode = new StringBuilder();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new ScriptCompilerAdderImpl(notPrg, queue);
        });
        assertThat(e.getMessage()).isEqualTo("Source code file must have a .prg extension: not_prg.inc");
    }

    /**
     * Construction with a {@code null} {@link CommandQueue} should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullCommandQueue() {
        File sourceCodeFile = mock(File.class);
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            new ScriptCompilerAdderImpl(sourceCodeFile, null);
        });
        assertThat(e.getMessage()).isEqualTo("Command queue cannot be null.");
    }

    /**
     * Construction with a {@code null} source code file should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullSourceCodeFile() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            new ScriptCompilerAdderImpl(null, queue);
        });
        assertThat(e.getMessage()).isEqualTo("Source code cannot be null.");
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
        final File sourceCodeFile = mock(File.class);
        when(sourceCodeFile.getName()).thenReturn("a_script.prg");
        mockStatic(FileUtils.class);
        when(FileUtils.readFileToString(sourceCodeFile, Charset.forName("utf-8")))
                .thenReturn("create program a_script");

        whenNew(ScriptCompilerCommand.class)
                .withArguments(sourceCodeFile, Collections.singleton(dependencyFile), listingFile, Boolean.FALSE)
                .thenReturn(command);
        ScriptCompilerAdderImpl adder = new ScriptCompilerAdderImpl(sourceCodeFile, queue);
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
        final File sourceCodeFile = mock(File.class);
        when(sourceCodeFile.getName()).thenReturn("a_script.prg");
        mockStatic(FileUtils.class);
        when(FileUtils.readFileToString(sourceCodeFile, Charset.forName("utf-8")))
                .thenReturn("create program a_script");

        whenNew(ScriptCompilerCommand.class)
                .withArguments(sourceCodeFile, Collections.singleton(dependencyFile), listingFile, Boolean.TRUE)
                .thenReturn(command);
        ScriptCompilerAdderImpl adder = new ScriptCompilerAdderImpl(sourceCodeFile, queue);
        adder.withListingOutput(listingFile).withDebugModeEnabled(true).withDependency(dependencyFile).commit();
        verify(queue).addInCclSessionCommand(command);
    }

    /**
     * Confirms that an IllegalArgumentException is thrown if the script's file name is not lower case.
     *
     * @throws Exception
     *             Not expected
     */
    @SuppressWarnings("unused")
    @Test
    public void testFileNameNotLowerCase() throws Exception {
        final File file = mock(File.class);
        when(file.getName()).thenReturn("NotLowerCase.prg");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new ScriptCompilerAdderImpl(file, queue);
        });
        assertThat(e.getMessage()).isEqualTo("Source code file name must be all lower case: NotLowerCase.prg");
    }

    /**
     * Confirms that an IllegalArgumentException is thrown if the script's file name does not match the script name.
     *
     * @throws Exception
     *             Not expected
     */
    @SuppressWarnings("unused")
    @Test
    public void testFileNameDoesNotMatchScriptName() throws Exception {
        final File file = mock(File.class);
        when(file.getName()).thenReturn("some_script.prg");
        mockStatic(FileUtils.class);
        when(FileUtils.readFileToString(file, Charset.forName("utf-8"))).thenReturn("create program a_script");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new ScriptCompilerAdderImpl(file, queue);
        });
        assertThat(e.getMessage())
                .isEqualTo("Source code file name must match generated program name: some_script.prg");
    }

    /**
     * Confirms that an IllegalArgumentException is not thrown when the script's file name does match the script name.
     *
     * @throws Exception
     *             Not expected
     */
    @SuppressWarnings("unused")
    @Test
    public void testFileNameDoesMatchScriptName() throws Exception {
        final File fileA = new File(
                ScriptCompilerAdderImplTest.class.getResource("/program/sample_script_with_competition.prg").getFile());
        new ScriptCompilerAdderImpl(fileA, queue);

        final File fileB = new File(
                ScriptCompilerAdderImplTest.class.getResource("/program/sample_script.prg").getFile());
        new ScriptCompilerAdderImpl(fileB, queue);
    }

    /**
     * Confirms that an IllegalArgumentException is thrown if the file name does not create a script.
     *
     * @throws Exception
     *             Not expected
     */
    @SuppressWarnings("unused")
    @Test
    public void testFileDoesCreateAScript() throws Exception {
        final File file = mock(File.class);
        when(file.getName()).thenReturn("some_script.prg");
        mockStatic(FileUtils.class);
        when(FileUtils.readFileToString(file, Charset.forName("utf-8")))
                .thenReturn("this is not really a script file.");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new ScriptCompilerAdderImpl(file, queue);
        });
        assertThat(e.getMessage()).isEqualTo("Source code must create a program: some_script.prg");
    }

    /**
     * Confirms it is okay to have lost of whitespace and a group name in the script name.
     *
     * @throws Exception
     *             Not expected
     */
    @Test
    public void testFormattingAndGroup() throws Exception {
        final File dependencyFile = mock(File.class);
        final File listingFile = mock(File.class);

        StringBuilder sbCode = new StringBuilder();
        sbCode.append("drop program a_script:dba go").append("\n")
                .append(" \t create \t program \t\t  a_script  \t  :  \t\t \t  dba").append("\n").append("code body")
                .append("\n").append("end go");

        final ScriptCompilerCommand command = mock(ScriptCompilerCommand.class);
        final File sourceCodeFile = mock(File.class);
        when(sourceCodeFile.getName()).thenReturn("a_script.prg");
        mockStatic(FileUtils.class);
        when(FileUtils.readFileToString(sourceCodeFile, Charset.forName("utf-8"))).thenReturn(sbCode.toString());

        whenNew(ScriptCompilerCommand.class)
                .withArguments(sourceCodeFile, Collections.singleton(dependencyFile), listingFile, Boolean.FALSE)
                .thenReturn(command);
        ScriptCompilerAdderImpl adder = new ScriptCompilerAdderImpl(sourceCodeFile, queue);
        adder.withDependency(dependencyFile).withListingOutput(listingFile).commit();
        verify(queue).addInCclSessionCommand(command);
    }
}