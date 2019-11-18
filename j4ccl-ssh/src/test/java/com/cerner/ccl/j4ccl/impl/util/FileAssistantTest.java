package com.cerner.ccl.j4ccl.impl.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.File;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.impl.data.Environment;

/**
 * Unit test of {@link FileAssistant}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { Environment.class, FileAssistant.class, ScriptRegistrar.class })
public class FileAssistantTest {
    private final String cclSource = "cclsource";
    private final String cerTemp = "cer_temp";
    private final String cerProc = "cer_proc";
    private final String cerInstall = "cer_install";

    @Mock
    private Environment environment;
    @Mock
    private File localFile;

    /**
     * Set up the testing harness.
     */
    @Before
    public void setUp() {
        when(localFile.exists()).thenReturn(Boolean.TRUE);
        when(localFile.isFile()).thenReturn(Boolean.TRUE);

        mockStatic(Environment.class);
        when(Environment.getEnvironment()).thenReturn(environment);

        when(environment.getCclSource()).thenReturn("cclsource");
        when(environment.getCerTemp()).thenReturn(cerTemp);
        when(environment.getCerProc()).thenReturn(cerProc);
        when(environment.getCerInstall()).thenReturn(cerInstall);

        mockStatic(ScriptRegistrar.class);
    }

    /**
     * Verify that the remote path determination fails if given a null file.
     */
    @Test
    public void testCreateRemotePathNullFile() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            FileAssistant.createRemotePath(null);
        });
        assertThat(e.getMessage()).isEqualTo("Local file cannot be null.");
    }

    /**
     * Verify that the remote path determination fails if given a non-file object.
     */
    @Test
    public void testCreateRemotePathDirectory() {
        final File notFile = mock(File.class);
        when(notFile.exists()).thenReturn(Boolean.TRUE);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            FileAssistant.createRemotePath(notFile, mock(Environment.class));
        });
        assertThat(e.getMessage()).isEqualTo("File must be an actual file.");
    }

    /**
     * Verify that the remote path determination fails if given a null environment object.
     *
     * @throws Exception
     *             If any errors occur while running the test.
     */
    @Test
    public void testCreateRemotePathNullEnvironment() throws Exception {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            FileAssistant.createRemotePath(localFile, null);
        });
        assertThat(e.getMessage()).isEqualTo("Environment cannot be null.");
    }

    /**
     * Test that, if a script is marked as being a dynamic script, it will be uploaded to cer_temp, not cclsource.
     */
    @Test
    public void testDynamicScript() {
        final String fileName = "a_dynamic_script.prg";
        when(localFile.getName()).thenReturn(fileName);

        when(ScriptRegistrar.isDynamicScript(fileName)).thenReturn(Boolean.TRUE);
        assertThat(FileAssistant.createRemotePath(localFile).toString()).isEqualTo(cerTemp + "/" + fileName);
    }

    /**
     * Test the returned path for a .PRG file.
     *
     * @throws Exception
     *             If any errors occur during the test.
     */
    @Test
    public void testPrgFile() throws Exception {
        final String fileName = "a_script.prg";
        when(localFile.getName()).thenReturn(fileName);

        assertThat(FileAssistant.createRemotePath(localFile).toString()).isEqualTo(cclSource + "/" + fileName);
    }

    /**
     * Test the returned path for a .INC file.
     *
     * @throws Exception
     *             If any errors occur during the test.
     */
    @Test
    public void testIncFile() throws Exception {
        final String fileName = "tEST.inC";
        when(localFile.getName()).thenReturn(fileName);

        assertThat(FileAssistant.createRemotePath(localFile).toString())
                .isEqualTo(cclSource + "/" + fileName.toLowerCase(Locale.US));
    }

    /**
     * Test the returned path for a .SUB file.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSubFile() throws Exception {
        final String fileName = "tEST.Sub";
        when(localFile.getName()).thenReturn(fileName);

        assertThat(FileAssistant.createRemotePath(localFile).toString())
                .isEqualTo(cclSource + "/" + fileName.toLowerCase(Locale.US));
    }

    /**
     * Test the returned path for a .COM file.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testComFile() throws Exception {
        final String fileName = "tEST.com";
        when(localFile.getName()).thenReturn(fileName);

        assertThat(FileAssistant.createRemotePath(localFile).toString()).isEqualTo(cerProc + "/" + fileName);
    }

    /**
     * Test the returned path for a .KSH file.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testKshFile() throws Exception {
        final String fileName = "TEst.kSh";
        when(localFile.getName()).thenReturn(fileName);

        assertThat(FileAssistant.createRemotePath(localFile).toString()).isEqualTo(cerProc + "/" + fileName);
    }

    /**
     * Test the returned path for .TXT file.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testTxtFile() throws Exception {
        final String fileName = "teST.txt";
        when(localFile.getName()).thenReturn(fileName);

        assertThat(FileAssistant.createRemotePath(localFile).toString()).isEqualTo(cerInstall + "/" + fileName);
    }

    /**
     * Test the returned path for a .CSV file.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testCsvFile() throws Exception {
        final String fileName = "teST.CsV";
        when(localFile.getName()).thenReturn(fileName);

        assertThat(FileAssistant.createRemotePath(localFile).toString()).isEqualTo(cerInstall + "/" + fileName);
    }
}