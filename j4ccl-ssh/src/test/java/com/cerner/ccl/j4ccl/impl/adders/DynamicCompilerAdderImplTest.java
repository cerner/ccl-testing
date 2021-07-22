package com.cerner.ccl.j4ccl.impl.adders;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.impl.CommandQueue;
import com.cerner.ccl.j4ccl.impl.commands.DropScriptCommand;
import com.cerner.ccl.j4ccl.impl.commands.ScriptCompilerCommand;
import com.cerner.ccl.j4ccl.impl.util.ScriptRegistrar;

/**
 * Unit tests for {@link DynamicCompilerAdderImpl}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { DropScriptCommand.class, File.class, FileUtils.class, ScriptCompilerCommand.class,
        ScriptRegistrar.class, DynamicCompilerAdderImpl.class })
public class DynamicCompilerAdderImplTest {
    private final String sourceCodeFileName = "source.code.inc";
    @Mock
    private CommandQueue queue;
    @Mock
    private File sourceCodeFile;
    private DynamicCompilerAdderImpl adder;

    @Captor
    private ArgumentCaptor<Collection<File>> argumentCaptorFileCollection;
    @Captor
    private ArgumentCaptor<List<String>> argumentCaptorListString;

    /**
     * Set up the adder for each test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(sourceCodeFile.getName()).thenReturn(sourceCodeFileName);
        adder = new DynamicCompilerAdderImpl(sourceCodeFile, queue);
    }

    /**
     * Construction with a {@code null} command queue should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testContructNullCommandQueue() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            new DynamicCompilerAdderImpl(sourceCodeFile, null);
        });
        assertThat(e.getMessage()).isEqualTo("Command queue cannot be null.");
    }

    /**
     * Construction with a {@code null} file should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullFile() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            new DynamicCompilerAdderImpl(null, queue);
        });
        assertThat(e.getMessage()).isEqualTo("Source code cannot be null.");
    }

    /**
     * Construction with a {@code .inc} file should succeed.
     */
    @Test
    public void testConstructWithInc() {
        mockStatic(FileUtils.class);
        final File incFile = mock(File.class);
        when(incFile.getName()).thenReturn("test.inc");
        final CommandQueue implQueue = new CommandQueue();
        final DynamicCompilerAdderImpl impl = new DynamicCompilerAdderImpl(incFile, implQueue).withScriptName("test");
        impl.commit();
        assertThat(implQueue.getInCclSessionCommands()).isNotEmpty();
    }

    /**
     * Construction with a {code .prg} file should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructWithPrg() {
        final File prgFile = mock(File.class);
        when(prgFile.getName()).thenReturn("test.prg");

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            new DynamicCompilerAdderImpl(prgFile, queue);
        });
        assertThat(e.getMessage()).isEqualTo("Source code file must be a .INC or .SUB file.");
    }

    /**
     * Construction with a {@code .sub} file should succeed.
     */
    @Test
    public void testConstructWithSub() {
        mockStatic(FileUtils.class);
        final File incFile = mock(File.class);
        when(incFile.getName()).thenReturn("test.sub");
        final CommandQueue implQueue = new CommandQueue();
        final DynamicCompilerAdderImpl impl = new DynamicCompilerAdderImpl(incFile, implQueue).withScriptName("test");
        impl.commit();
        assertThat(implQueue.getInCclSessionCommands()).isNotEmpty();
    }

    /**
     * Setting a blank script name should fail.
     */
    @Test
    public void testWithScriptNameBlankName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            adder.withScriptName("");
        });
        assertThat(e.getMessage()).isEqualTo("Script name cannot be blank.");
    }

    /**
     * Setting a {@code null} script name should fail.
     */
    @Test
    public void testWithScriptNameNullName() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            adder.withScriptName(null);
        });
        assertThat(e.getMessage()).isEqualTo("Script name cannot be null.");
    }

    /**
     * Oddly, having no script name is okay though. One will be created based on the user name then truncated to 30
     * characters.
     *
     * @throws Exception
     *             Bad things happen sometimes.
     *
     */
    @Test
    public void testWithNoScriptName() throws Exception {
        mockStatic(FileUtils.class);
        final File incFile = mock(File.class);
        when(incFile.getName()).thenReturn("test.sub");
        final CommandQueue implQueue = new CommandQueue();
        final DynamicCompilerAdderImpl impl = new DynamicCompilerAdderImpl(incFile, implQueue)
                .withDebugModeEnabled(true);
        mockStatic(System.class);
        when(System.getProperty("user.name")).thenReturn("userNameWithMoreThanTwentyThreeCharacters");
        final ScriptCompilerCommand command = mock(ScriptCompilerCommand.class);
        whenNew(ScriptCompilerCommand.class).withAnyArguments().thenReturn(command);
        impl.commit();
        assertThat(implQueue.getInCclSessionCommands()).isNotEmpty();
        assertThat(implQueue.getInCclSessionCommands().size()).isEqualTo(1);
        assertThat(ScriptRegistrar.isDynamicScript("j4ccl_usernamewithmorethantwen.prg")).isTrue();
    }

    /**
     * If the script name is too long, then setting it as the script name should fail.
     */
    @Test
    public void testWithScriptNameTooLong() {
        final StringBuilder nameBuilder = new StringBuilder();
        nameBuilder.append(StringUtils.repeat("a", 31));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            adder.withScriptName(nameBuilder.toString());
        });
        assertThat(e.getMessage()).isEqualTo("Script name exceeds max length: 31 > 30");
    }

    /**
     * If the script name is the maximum length, it is okay.
     */
    @Test
    public void testWithMaximumScriptNameLength() {
        mockStatic(FileUtils.class);
        final File incFile = mock(File.class);
        when(incFile.getName()).thenReturn("test.sub");
        final CommandQueue implQueue = new CommandQueue();
        final String scriptName = StringUtils.repeat("a", 30);
        final DynamicCompilerAdderImpl impl = new DynamicCompilerAdderImpl(incFile, implQueue)
                .withScriptName(scriptName);
        impl.commit();
        assertThat(implQueue.getInCclSessionCommands()).isNotEmpty();
        assertThat(ScriptRegistrar.isDynamicScript(scriptName + ".prg")).isTrue();
    }
}
