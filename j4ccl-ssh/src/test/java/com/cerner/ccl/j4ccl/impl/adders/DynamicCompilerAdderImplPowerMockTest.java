package com.cerner.ccl.j4ccl.impl.adders;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.impl.CommandQueue;
import com.cerner.ccl.j4ccl.impl.commands.DropScriptCommand;
import com.cerner.ccl.j4ccl.impl.commands.ScriptCompilerCommand;
import com.cerner.ccl.j4ccl.impl.util.ScriptRegistrar;
import com.cerner.ccl.j4ccl.internal.AbstractUnitTest;

/**
 * Unit tests for {@link DynamicCompilerAdderImpl}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { DynamicCompilerAdderImpl.class, DropScriptCommand.class, File.class, FileUtils.class,
        ScriptCompilerCommand.class, ScriptRegistrar.class })
public class DynamicCompilerAdderImplPowerMockTest extends AbstractUnitTest {
    private final String sourceCodeFileName = "source.code.inc";
    @Mock
    private CommandQueue queue;
    @Mock
    private File sourceCodeFile;
    private DynamicCompilerAdderImpl adder;

    @Captor
    private ArgumentCaptor<Collection<File>> argumentCaptorFileCollection;

    /**
     * Set up the adder for each test.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(sourceCodeFile.getName()).thenReturn(sourceCodeFileName);
        adder = new DynamicCompilerAdderImpl(sourceCodeFile, queue);
    }

    /**
     * Test the committing of a dynamic compiler to a command queue.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCommit() throws Exception {
        final File dependencyFile = mock(File.class);
        final File listingOutputFile = mock(File.class);

        mockStatic(FileUtils.class);

        final ArgumentCaptor<File> scriptFileCaptor = ArgumentCaptor.forClass(File.class);
        final ScriptCompilerCommand compileCommand = mock(ScriptCompilerCommand.class);

        whenNew(ScriptCompilerCommand.class).withArguments(scriptFileCaptor.capture(),
                argumentCaptorFileCollection.capture(), eq(listingOutputFile), eq(Boolean.FALSE))
                .thenReturn(compileCommand);

        final DropScriptCommand dropCommand = mock(DropScriptCommand.class);
        final ArgumentCaptor<String> dropScriptNameCaptor = ArgumentCaptor.forClass(String.class);
        whenNew(DropScriptCommand.class).withArguments(dropScriptNameCaptor.capture()).thenReturn(dropCommand);

        adder.withDependency(dependencyFile).withListingOutput(listingOutputFile).commit();

        final File scriptFile = scriptFileCaptor.getAllValues().get(0);
        assertThat(scriptFile.getParentFile()).isEqualTo(new File(System.getProperty("java.io.tmpdir")));

        final String tempScriptFilename = scriptFile.getName();
        final String tempScriptName = tempScriptFilename.substring(0, tempScriptFilename.lastIndexOf('.'));
        assertThat(tempScriptFilename)
                .startsWith(
                        "j4ccl_" + System.getProperty("user.name").toLowerCase(Locale.getDefault()).replace("$", ""))
                .endsWith(".prg");
        assertThat(dropScriptNameCaptor.getValue()).isEqualTo(tempScriptName);

        verifyStatic(FileUtils.class);
        FileUtils.writeLines(scriptFile, "utf-8",
                Arrays.asList("drop program " + tempScriptName + " go", "create program " + tempScriptName,
                        "%i cclsource:" + sourceCodeFileName.toLowerCase(Locale.US), "end go"));

        final Collection<File> dependencies = argumentCaptorFileCollection.getAllValues().get(0);
        assertThat(dependencies.contains(dependencyFile)).isTrue();
        assertThat(dependencies.contains(sourceCodeFile)).isTrue();
    }

    /**
     * If the script name is set, then that should be the name of the script used.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCommitWithScriptName() throws Exception {
        final String scriptName = "script_name";

        final ArgumentCaptor<File> scriptFileCaptor = ArgumentCaptor.forClass(File.class);
        final ScriptCompilerCommand compileCommand = mock(ScriptCompilerCommand.class);
        whenNew(ScriptCompilerCommand.class).withArguments(scriptFileCaptor.capture(),
                ArgumentMatchers.<Collection<File>> any(), ArgumentMatchers.<File> any(), eq(Boolean.FALSE))
                .thenReturn(compileCommand);

        final DropScriptCommand dropCommand = mock(DropScriptCommand.class);
        final ArgumentCaptor<String> dropScriptNameCaptor = ArgumentCaptor.forClass(String.class);
        whenNew(DropScriptCommand.class).withArguments(dropScriptNameCaptor.capture()).thenReturn(dropCommand);

        mockStatic(FileUtils.class);

        adder.withScriptName(scriptName).commit();

        final File scriptFile = scriptFileCaptor.getAllValues().get(0);
        assertThat(scriptFile.getName()).isEqualTo(scriptName + ".prg");

        verifyStatic(FileUtils.class);
        FileUtils.writeLines(scriptFile, "utf-8", Arrays.asList("drop program " + scriptName + " go",
                "create program " + scriptName, "%i cclsource:" + sourceCodeFileName.toLowerCase(Locale.US), "end go"));
    }

    /**
     * If debug is set then the script should be compiled with debug turned on.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCommitWithDebug() throws Exception {
        final String scriptName = "script_name";

        final ArgumentCaptor<File> scriptFileCaptor = ArgumentCaptor.forClass(File.class);
        final ScriptCompilerCommand compileCommand = mock(ScriptCompilerCommand.class);
        whenNew(ScriptCompilerCommand.class).withArguments(scriptFileCaptor.capture(),
                ArgumentMatchers.<Collection<File>> any(), ArgumentMatchers.<File> any(), eq(Boolean.TRUE))
                .thenReturn(compileCommand);

        final DropScriptCommand dropCommand = mock(DropScriptCommand.class);
        final ArgumentCaptor<String> dropScriptNameCaptor = ArgumentCaptor.forClass(String.class);
        whenNew(DropScriptCommand.class).withArguments(dropScriptNameCaptor.capture()).thenReturn(dropCommand);

        mockStatic(FileUtils.class);

        adder.withDebugModeEnabled(true).withScriptName(scriptName).commit();

        final File scriptFile = scriptFileCaptor.getAllValues().get(0);
        assertThat(scriptFile.getName()).isEqualTo(scriptName + ".prg");

        verifyStatic(FileUtils.class);
        FileUtils.writeLines(scriptFile, "utf-8", Arrays.asList("drop program " + scriptName + " go",
                "create program " + scriptName, "%i cclsource:" + sourceCodeFileName.toLowerCase(Locale.US), "end go"));
    }

    /**
     * If a script name is not provided and the "random" name is the maximum allowed length.
     *
     * @throws Exception
     *             Unexpected but sometimes bad things happen.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testWithRandomNameAtMaxLength() throws Exception {
        mockStatic(System.class);
        when(System.getProperty("user.name")).thenReturn("XYZXYZXY0050000");
        when(System.currentTimeMillis()).thenReturn(1465725135007L);
        mockStatic(FileUtils.class);
        final File incFile = mock(File.class);
        when(incFile.getName()).thenReturn("test.sub");
        final CommandQueue implQueue = new CommandQueue();
        final DynamicCompilerAdderImpl impl = new DynamicCompilerAdderImpl(incFile, implQueue);
        impl.commit();

        assertThat(implQueue.getOnCclStartCommands()).isEmpty();
        assertThat(implQueue.getInCclSessionCommands()).hasSize(1);
        assertThat(implQueue.getOnCclCloseCommands()).hasSize(1);

        Field dynamicScriptsField = ScriptRegistrar.class.getDeclaredField("dynamicScripts");
        Field compiledScriptsField = ScriptRegistrar.class.getDeclaredField("compiledScripts");
        dynamicScriptsField.setAccessible(true);
        compiledScriptsField.setAccessible(true);

        Field startCommandsField = CommandQueue.class.getDeclaredField("onCclStartCommands");
        Field sessionCommandsField = CommandQueue.class.getDeclaredField("inSessionCommands");
        Field closeCommandsField = CommandQueue.class.getDeclaredField("onCclCloseCommands");
        startCommandsField.setAccessible(true);
        sessionCommandsField.setAccessible(true);
        closeCommandsField.setAccessible(true);

        Field sourceCodeLocationField = ScriptCompilerCommand.class.getDeclaredField("sourceCodeLocation");
        sourceCodeLocationField.setAccessible(true);

        Field scriptNameField = DropScriptCommand.class.getDeclaredField("scriptName");
        scriptNameField.setAccessible(true);

        Collection<ScriptCompilerCommand> sessionCommands = (Collection<ScriptCompilerCommand>) sessionCommandsField
                .get(implQueue);
        Collection<DropScriptCommand> closeCommands = (Collection<DropScriptCommand>) closeCommandsField.get(implQueue);

        String sourceFileName = "";
        for (ScriptCompilerCommand sessionCommand : sessionCommands) {
            File sourceFile = (File) sourceCodeLocationField.get(sessionCommand);
            sourceFileName = sourceFile.getName();
        }
        String scriptName = "";
        for (DropScriptCommand closeCommand : closeCommands) {
            scriptName = (String) scriptNameField.get(closeCommand);
        }

        String regexScriptName = "j4ccl_xyzxyzxy0050000_\\d+5135007";
        Pattern scriptNamePattern = Pattern.compile(regexScriptName);
        Matcher scriptNameMatcher = scriptNamePattern.matcher(scriptName);
        boolean scriptNameMatches = scriptNameMatcher.matches();
        assertThat(scriptNameMatches).isTrue();
        Pattern sourceFilePattern = Pattern.compile(regexScriptName + ".prg");
        Matcher sourceFileNameMatcher = sourceFilePattern.matcher(sourceFileName);
        boolean sourceFileNameMatches = sourceFileNameMatcher.matches();
        assertThat(sourceFileNameMatches).isTrue();

        Set<String> dynamicScripts = (Set<String>) dynamicScriptsField.get(new ScriptRegistrar());
        Set<String> compiledScripts = (Set<String>) compiledScriptsField.get(new ScriptRegistrar());
        assertThat(dynamicScripts).contains(sourceFileName.toUpperCase());
        assertThat(compiledScripts).contains(sourceFileName.toUpperCase());

        assertThat(ScriptRegistrar.isDynamicScript(sourceFileName)).isTrue();
        assertThat(ScriptRegistrar.isDynamicScript(sourceFileName.toUpperCase())).isTrue();
    }

    /**
     * If a script name is not provided and the "random" name exceeds the maximum allowed length.
     *
     * @throws Exception
     *             Unexpected but sometimes bad things happen.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testWithNoNamePovidedAndAShortUserName() throws Exception {
        mockStatic(System.class);
        when(System.getProperty("user.name")).thenReturn("XYZXYZXYZ0050000");
        when(System.currentTimeMillis()).thenReturn(11465725154321L);
        mockStatic(FileUtils.class);
        final File incFile = mock(File.class);
        when(incFile.getName()).thenReturn("test.sub");
        final CommandQueue implQueue = new CommandQueue();
        final DynamicCompilerAdderImpl impl = new DynamicCompilerAdderImpl(incFile, implQueue);
        impl.commit();

        assertThat(implQueue.getOnCclStartCommands()).isEmpty();
        assertThat(implQueue.getInCclSessionCommands()).hasSize(1);
        assertThat(implQueue.getOnCclCloseCommands()).hasSize(1);

        Field dynamicScriptsField = ScriptRegistrar.class.getDeclaredField("dynamicScripts");
        Field compiledScriptsField = ScriptRegistrar.class.getDeclaredField("compiledScripts");
        dynamicScriptsField.setAccessible(true);
        compiledScriptsField.setAccessible(true);

        Field startCommandsField = CommandQueue.class.getDeclaredField("onCclStartCommands");
        Field sessionCommandsField = CommandQueue.class.getDeclaredField("inSessionCommands");
        Field closeCommandsField = CommandQueue.class.getDeclaredField("onCclCloseCommands");
        startCommandsField.setAccessible(true);
        sessionCommandsField.setAccessible(true);
        closeCommandsField.setAccessible(true);

        Field sourceCodeLocationField = ScriptCompilerCommand.class.getDeclaredField("sourceCodeLocation");
        sourceCodeLocationField.setAccessible(true);

        Field scriptNameField = DropScriptCommand.class.getDeclaredField("scriptName");
        scriptNameField.setAccessible(true);

        Collection<ScriptCompilerCommand> sessionCommands = (Collection<ScriptCompilerCommand>) sessionCommandsField
                .get(implQueue);
        Collection<DropScriptCommand> closeCommands = (Collection<DropScriptCommand>) closeCommandsField.get(implQueue);

        String sourceFileName = "";
        for (ScriptCompilerCommand sessionCommand : sessionCommands) {
            File sourceFile = (File) sourceCodeLocationField.get(sessionCommand);
            sourceFileName = sourceFile.getName();
        }
        String scriptName = "";
        for (DropScriptCommand closeCommand : closeCommands) {
            scriptName = (String) scriptNameField.get(closeCommand);
        }

        String regexScriptName = "j4ccl_xyzxyzxyz0050000_\\d+515432";
        Pattern scriptNamePattern = Pattern.compile(regexScriptName);
        Matcher scriptNameMatcher = scriptNamePattern.matcher(scriptName);
        boolean scriptNameMatches = scriptNameMatcher.matches();
        assertThat(scriptNameMatches).isTrue();
        Pattern sourceFilePattern = Pattern.compile(regexScriptName + ".prg");
        Matcher sourceFileNameMatcher = sourceFilePattern.matcher(sourceFileName);
        boolean sourceFileNameMatches = sourceFileNameMatcher.matches();
        assertThat(sourceFileNameMatches).isTrue();

        Set<String> dynamicScripts = (Set<String>) dynamicScriptsField.get(new ScriptRegistrar());
        Set<String> compiledScripts = (Set<String>) compiledScriptsField.get(new ScriptRegistrar());
        assertThat(dynamicScripts).contains(sourceFileName.toUpperCase());
        assertThat(compiledScripts).contains(sourceFileName.toUpperCase());

        assertThat(implQueue.getInCclSessionCommands()).isNotEmpty();
        assertThat(ScriptRegistrar.isDynamicScript(sourceFileName)).isTrue();
        assertThat(ScriptRegistrar.isDynamicScript(sourceFileName.toUpperCase())).isTrue();
    }
}
