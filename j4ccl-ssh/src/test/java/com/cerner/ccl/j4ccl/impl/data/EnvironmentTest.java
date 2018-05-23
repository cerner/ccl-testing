package com.cerner.ccl.j4ccl.impl.data;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cerner.ccl.j4ccl.TerminalProperties;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePrincipal;
import com.cerner.ccl.j4ccl.impl.jaas.JaasUtils;
import com.cerner.ccl.j4ccl.ssh.CommandExpectationGroup;
import com.cerner.ccl.j4ccl.ssh.JSchSshTerminal;
import com.cerner.ccl.j4ccl.ssh.TerminalResponse;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * Unit test for {@link Environment}.
 *
 * @author Joshua Hyde
 *
 */
@SuppressWarnings("unused")
@PowerMockIgnore("javax.security.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { JaasUtils.class, JSchSshTerminal.class, Environment.class, FileInputStream.class,
        PointFactory.class, LoggerFactory.class, TerminalProperties.class })
public class EnvironmentTest {
    /**
     * A {@link Rule} used to identify the name of the current test.
     */
    @Rule
    public TestName testName = new TestName();
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    private final String environmentName = "enviornment.name";
    private final String CER_INSTALL = "/some/folder/cer_install";
    private final String CER_PROC = "/a/proc/folder/cer_proc";
    private final String CCLUSERDIR = "/a/ccl/dir/for/users/ccluserdir";
    private final String CCLSOURCE = "/cer_script";
    private final String CER_TEMP = "/tmp/cer_temp";

    private final String KEY_CER_INSTALL = "cer_install";
    private final String KEY_CER_PROC = "cer_proc";
    private final String KEY_CCLUSERDIR = "CCLUSERDIR";
    private final String KEY_CCLSOURCE = "CCLSOURCE";
    private final String KEY_CER_TEMP = "cer_temp";

    @Mock
    private JSchSshTerminal sshTerminal;
    @Mock
    private BackendNodePrincipal backendPrincipal;
    @Mock
    private Logger mockLogger;
    @Mock
    private EtmPoint pointInit;
    @Mock
    private EtmPoint pointGetEnvData;
    @Captor
    private ArgumentCaptor<List<String>> argumentCaptorStringList;
    @Captor
    private ArgumentCaptor<List<CommandExpectationGroup>> argumentCaptorCommandExpectationGroupList;

    /**
     * One time initialization
     */
    @BeforeClass
    public static void setupOnce() {
        TerminalProperties.setGlobalTerminalProperties(
                TerminalProperties.getNewBuilder()
                        .setOsPromptPattern(
                                TerminalProperties.constructDefaultOsPromptPattern("host", "enviornment.name", "user"))
                        .build());
    }

    /**
     * Setup the mocks for each test.
     *
     * @throws Exception
     *             If any errors occur during the setup.
     */
    @Before
    public void setUp() throws Exception {
        mockStatic(LoggerFactory.class);
        when(LoggerFactory.getLogger(Environment.class)).thenReturn(mockLogger);

        whenNew(JSchSshTerminal.class).withNoArguments().thenReturn(sshTerminal);

        final Map<String, String> logicals = new HashMap<String, String>(5);
        logicals.put(KEY_CCLSOURCE, CCLSOURCE);
        logicals.put(KEY_CCLUSERDIR, CCLUSERDIR);
        logicals.put(KEY_CER_INSTALL, CER_INSTALL);
        logicals.put(KEY_CER_PROC, CER_PROC);
        logicals.put(KEY_CER_TEMP, CER_TEMP);

        mockStatic(JaasUtils.class);
        when(JaasUtils.getPrincipal(BackendNodePrincipal.class)).thenReturn(backendPrincipal);
        when(backendPrincipal.getEnvironmentName()).thenReturn(getEnvironmentName());

        final String envData = !testName.getMethodName().equals("testConstructMissingLogical")
                ? loadEnvResource("/property/environment.properties")
                : loadEnvResource("/property/environment-no-ccluserdir.properties");
        final TerminalResponse trIgnored = new TerminalResponse(0, "");
        final TerminalResponse trEnv = new TerminalResponse(0, envData);

        mockStatic(PointFactory.class);
        when(PointFactory.getPoint(Environment.class, "<init>")).thenReturn(pointInit);
        when(PointFactory.getPoint(Environment.class, "getEnvData")).thenReturn(pointGetEnvData);
        if (testName.getMethodName().equals("testSkipEnvset")) {
            when(sshTerminal.executeCommandGroups(ArgumentMatchers.<List<CommandExpectationGroup>> any()))
                    .thenReturn(trEnv);
        } else if (testName.getMethodName().equals("testEnvironmentRetrievalTotalFailure")) {
            final TerminalResponse trWhatever = new TerminalResponse(0, "whatever the terminal happens to return.");
            when(sshTerminal.executeCommandGroups(ArgumentMatchers.<List<CommandExpectationGroup>> any()))
                    .thenReturn(trWhatever);
        } else {
            when(sshTerminal.executeCommandGroups(ArgumentMatchers.<List<CommandExpectationGroup>> any()))
                    .thenReturn(trEnv);
        }
    }

    private String getEnvironmentName() {
        if (testName.getMethodName().equals("testSkipEnvset")) {
            return "test.skip.envset";
        } else if (testName.getMethodName().equals("testNoSkipEnvset")) {
            return "test.noskip.envset";
        }
        return environmentName;
    }

    /**
     * Verify that blank-name environments cannot be created.
     */
    @Test
    public void testGetEnvironmentBlankName() {
        final BackendNodePrincipal badPrincipal = mock(BackendNodePrincipal.class);
        when(badPrincipal.getEnvironmentName()).thenReturn(" ");

        mockStatic(JaasUtils.class);
        when(JaasUtils.getPrincipal(BackendNodePrincipal.class)).thenReturn(badPrincipal);

        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Environment name cannot be blank.");
        Environment.getEnvironment();
    }

    /**
     * Verify that environment objects are cached.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetEnvironmentCached() throws Exception {
        final Environment env = Environment.getEnvironment();
        final Environment otherEnv = Environment.getEnvironment();

        assertThat(env).isSameAs(otherEnv);
    }

    /**
     * Test that null-named environments cannot be created.
     */
    @Test
    public void testGetEnvironmentNullName() {
        expected.expect(NullPointerException.class);
        expected.expectMessage("Environment name cannot be null.");

        final BackendNodePrincipal badPrincipal = mock(BackendNodePrincipal.class);
        when(JaasUtils.getPrincipal(BackendNodePrincipal.class)).thenReturn(badPrincipal);

        Environment.getEnvironment();
    }

    /**
     * Test of {@link Environment#getCclSource()}.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetCclSource() throws Exception {
        final Environment env = Environment.getEnvironment();
        assertThat(env.getCclSource()).isEqualTo(CCLSOURCE);
    }

    /**
     * Test of {@link Environment#getCclUserDir()}.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetCclUserDir() throws Exception {
        final Environment env = Environment.getEnvironment();
        assertThat(env.getCclUserDir()).isEqualTo(CCLUSERDIR);
    }

    /**
     * Test of {@link Environment#getCerInstall()}.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetCerInstall() throws Exception {
        final Environment env = Environment.getEnvironment();
        assertThat(env.getCerInstall()).isEqualTo(CER_INSTALL);
    }

    /**
     * Test of {@link Environment#getCerProc()}.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetCerProc() throws Exception {
        final Environment env = Environment.getEnvironment();
        assertThat(env.getCerProc()).isEqualTo(CER_PROC);
    }

    /**
     * Test of {@link Environment#getCerTemp()}.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetCerTemp() throws Exception {
        final Environment env = Environment.getEnvironment();
        assertThat(env.getCerTemp()).isEqualTo(CER_TEMP);
    }

    /**
     * Test of {@link Environment#getEnvironmentName()}.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetEnvironmentName() throws Exception {
        final Environment env = Environment.getEnvironment();
        assertThat(env.getEnvironmentName()).isEqualTo(environmentName);
    }

    /**
     * Confirms logging an point collection with no env retrieval failures
     *
     * @throws Exception
     *             Sometimes bad things happen.
     */

    @Test
    public void testEnvironmentRetrievalSuccess() throws Exception {
        new Environment(environmentName);
        final ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);

        verify(pointInit, times(1)).collect();
        verify(mockLogger, times(1)).debug(logCaptor.capture());

        final List<String> logEntries = logCaptor.getAllValues();
        assertThat(logEntries.get(0))
                .isEqualTo(String.format("cclUserDir: %s; cclSource: %s; cerTemp: %s; cerProc: %s; cerInstall: %s%n",
                        CCLUSERDIR, CCLSOURCE, CER_TEMP, CER_PROC, CER_INSTALL));
    }

    /**
     * If one of the logicals is missing from the environment properties file, then the appropriate error should be
     * thrown.
     *
     * @throws Exception
     *             Not expected.
     */
    @Test
    public void testConstructMissingLogical() throws Exception {
        expected.expect(IllegalStateException.class);
        expected.expectMessage(
                "A server-side environment logical could not be determined. Verify that the given environment name exists: "
                        + environmentName);
        try {
            new Environment(environmentName);
        } finally {
            final ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);

            verify(pointInit, times(1)).collect();
            verify(mockLogger, times(3)).error(logCaptor.capture());

            final List<String> logEntries = logCaptor.getAllValues();
            assertThat(logEntries.get(0))
                    .isEqualTo("Some environment logical retrieval commamds failed.");
            assertThat(logEntries.get(2)).contains("---------envData start---------");
            assertThat(logEntries.get(2)).contains("---------envData end---------");
        }
    }

    /**
     * Confirms error logging occurs with none of the environment logicals could be retrived.
     *
     * @throws Exception
     *             Sometimes bad things happen.
     */

    @Test
    public void testEnvironmentRetrievalTotalFailure() throws Exception {
        expected.expect(IllegalStateException.class);
        expected.expectMessage(
                "A server-side environment logical could not be determined. Verify that the given environment name exists: "
                        + environmentName);
        try {
            new Environment(environmentName);
        } finally {
            final ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);

            verify(pointInit, times(1)).collect();
            verify(mockLogger, times(3)).error(logCaptor.capture());

            final List<String> logEntries = logCaptor.getAllValues();
            assertThat(logEntries.get(0))
                    .isEqualTo("Some environment logical retrieval commamds failed.");
            assertThat(logEntries.get(1)).isEqualTo(
                    "\n\r---------raw output start---------\n\rwhatever the terminal happens to return.\n\r---------raw output end---------");
            assertThat(logEntries.get(2)).contains("---------envData start---------");
            assertThat(logEntries.get(2)).contains("---------envData end---------");
        }
    }

    private String loadEnvResource(String resourceName) throws IOException {
        final InputStream is = EnvironmentTest.class.getResourceAsStream(resourceName);
        final BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        final StringBuilder sbData = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sbData.append(line).append("\n");
        }
        return sbData.toString();
    }

    /**
     * Confirms that envset is skipped if the TerminalProperties has skipEnvset set.
     *
     * @throws Exception
     *             Sometimes bad things happen.
     */
    @Test
    public void testSkipEnvset() throws Exception {
        final TerminalProperties terminalProperties = TerminalProperties.getNewBuilder()
                .setOsPromptPattern("osPromptPattern").setSkipEnvset(true).build();
        mockStatic(TerminalProperties.class);
        when(TerminalProperties.getGlobalTerminalProperties()).thenReturn(terminalProperties);

        final ArgumentCaptor<TerminalProperties> captorTerminalProperties = ArgumentCaptor
                .forClass(TerminalProperties.class);

        Environment.getEnvironment();
        verify(sshTerminal, times(1)).executeCommandGroups(argumentCaptorCommandExpectationGroupList.capture());
        final List<List<CommandExpectationGroup>> commandExpectationsGroupsList = argumentCaptorCommandExpectationGroupList
                .getAllValues();
        assertThat(commandExpectationsGroupsList.size()).isEqualTo(1);
        for (List<CommandExpectationGroup> commandExpectationsGroups : commandExpectationsGroupsList) {
            for (CommandExpectationGroup commandExpectationsGroup : commandExpectationsGroups) {
                for (String command : commandExpectationsGroup.getCommands()) {
                    assertThat(command).doesNotContain("envset");
                }
            }
        }
    }

    /**
     * Confirms that envset is not skipped if the TerminalProperties has skipEnvset set.
     *
     * @throws Exception
     *             Sometimes bad things happen.
     */
    @Test
    public void testNoSkipEnvset() throws Exception {
        Environment.getEnvironment();
        verify(sshTerminal, times(1)).executeCommandGroups(argumentCaptorCommandExpectationGroupList.capture());
        final List<List<CommandExpectationGroup>> commandExpectationsGroupsList = argumentCaptorCommandExpectationGroupList
                .getAllValues();
        assertThat(commandExpectationsGroupsList.size()).isEqualTo(1);
        assertThat(commandExpectationsGroupsList.get(0).get(0).getCommands().size()).isEqualTo(1);
        assertThat(commandExpectationsGroupsList.get(0).get(0).getCommands().get(0)).contains("envset");
    }
}