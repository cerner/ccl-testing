package com.cerner.ccl.j4ccl.ssh;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import com.cerner.ccl.j4ccl.TerminalProperties;
import com.cerner.ccl.j4ccl.enums.OutputType;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePrincipal;
import com.cerner.ccl.j4ccl.impl.jaas.JaasUtils;
import com.cerner.ccl.j4ccl.impl.jaas.MillenniumDomainPasswordCredential;
import com.cerner.ccl.j4ccl.impl.jaas.MillenniumDomainPrincipal;
import com.cerner.ccl.j4ccl.impl.util.CclOutputStreamProxy;
import com.cerner.ccl.j4ccl.impl.util.OutputStreamConfiguration;
import com.cerner.ccl.j4ccl.impl.util.OutputStreamProxy;
import com.google.code.jetm.reporting.ext.PointFactory;
import etm.core.monitor.EtmPoint;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit tests for {@link CclCommandTerminal}.
 *
 * @author Joshua Hyde
 */
@PowerMockIgnore("javax.security.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { CclOutputStreamProxy.class, CclCommandTerminal.class, JaasUtils.class,
        OutputStreamProxy.class, com.google.code.jetm.reporting.ext.PointFactory.class })
public class CclCommandTerminalTest {
    private static final String hostName = "TheHostName";
    private static final String environmentName = "environment.name";
    private static final String userName = "TheUserName";
    private static final String domainName = "TheDomainName";
    private static final String domainUserName = "TheDomainUserName";
    private static final String domainPassword = "TheDomainPassword";
    private static final String defaultOsPromptPattern = TerminalProperties.constructDefaultOsPromptPattern(hostName,
            environmentName, userName);

    @Mock
    private JSchSshTerminal sshTerminal;
    @Mock
    private BackendNodePrincipal principal;
    @Mock
    private MillenniumDomainPrincipal domainPrincipal;
    @Mock
    private MillenniumDomainPasswordCredential domainPasswordCredential;

    @Mock
    private EtmPoint jetmMock;

    @Captor
    private ArgumentCaptor<List<String>> argumentCaptorListString;

    @Captor
    private ArgumentCaptor<List<CommandExpectationGroup>> argumentCaptorListCommandExpectationGroup;

    /** One time initialization */
    @BeforeClass
    public static void setupOnce() {
        TerminalProperties.setGlobalTerminalProperties(
                TerminalProperties.getNewBuilder().setOsPromptPattern(defaultOsPromptPattern).build());
    }

    /** Set up the principal for each test. */
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(principal.getEnvironmentName()).thenReturn(environmentName);
        when(principal.getUsername()).thenReturn(userName);
        when(principal.getHostname()).thenReturn(hostName);

        when(domainPrincipal.getDomainName()).thenReturn(domainName);
        when(domainPrincipal.getUsername()).thenReturn(domainUserName);
        when(domainPasswordCredential.getPassword()).thenReturn(domainPassword);

        mockStatic(JaasUtils.class);
        when(JaasUtils.getPrincipal(BackendNodePrincipal.class)).thenReturn(principal);
        when(JaasUtils.hasPrincipal(MillenniumDomainPrincipal.class)).thenReturn(true);
        when(JaasUtils.getPrincipal(MillenniumDomainPrincipal.class)).thenReturn(domainPrincipal);
        when(JaasUtils.getPrivateCredential(MillenniumDomainPasswordCredential.class))
                .thenReturn(domainPasswordCredential);

        mockStatic(PointFactory.class);
        when(PointFactory.getPoint((Class<?>) ArgumentMatchers.<Object> any(), ArgumentMatchers.<String> any()))
                .thenReturn(jetmMock);
    }

    /**
     * Validates an IllegalArgumentException is thrown if a CclCommdTerminal is constructed with null
     * TerminalProperties.
     */
    @SuppressWarnings("unused")
    @Test(expected = IllegalArgumentException.class)
    public void testTwoArgsConstructorNullTerminalProperties() {
        final OutputStreamConfiguration mockOutputStreamConfiguration = mock(OutputStreamConfiguration.class);
        new CclCommandTerminal(null, mockOutputStreamConfiguration);
    }

    /**
     * Validates that each terminal property value is set to the default value when the provided terminal property value
     * is blank and to the provided value otherwise.
     */
    @Test
    public void testGetTerminalProperties() {

        final TerminalProperties terminalPropertiesDefault = TerminalProperties.getGlobalTerminalProperties();
        final TerminalProperties terminalProperties0 = TerminalProperties.getNewBuilder()
                .setOsPromptPattern(defaultOsPromptPattern).build();
        final CclCommandTerminal terminal0 = new CclCommandTerminal(terminalProperties0, null);

        assertThat(terminal0.getTerminalProperties().getSkipEnvset()).isFalse();
        assertThat(terminal0.getTerminalProperties().getSpecifyDebugCcl()).isTrue();
        assertThat(terminal0.getTerminalProperties().getOsPromptPattern()).isEqualTo(defaultOsPromptPattern);
        assertThat(terminal0.getTerminalProperties().getCclPromptPattern())
                .isEqualTo(terminalPropertiesDefault.getCclPromptPattern());
        assertThat(terminal0.getTerminalProperties().getCclLoginPromptPattern())
                .isEqualTo(terminalPropertiesDefault.getCclLoginPromptPattern());
        assertThat(terminal0.getTerminalProperties().getCclLoginSuccessPromptPattern())
                .isEqualTo(terminalPropertiesDefault.getCclLoginSuccessPromptPattern());
        assertThat(terminal0.getTerminalProperties().getCclLoginFailurePromptPatterns())
                .isEqualTo(terminalPropertiesDefault.getCclLoginFailurePromptPatterns());

        final TerminalProperties terminalProperties1 = TerminalProperties.getNewBuilder().setSkipEnvset(true)
                .setSpecifyDebugCcl(false).setOsPromptPattern("osPromptPattern").setCclPromptPattern("cclPromptPattern")
                .setCclLoginPromptPattern("cclLoginPromptPattern")
                .setCclLoginSuccessPromptPattern("cclLoginSuccessPromptPattern")
                .setCclLoginFailurePromptPatterns(Arrays.asList(new String[] { "pattern1", "pattern2" })).build();
        final CclCommandTerminal terminal1 = new CclCommandTerminal(terminalProperties1, null);
        assertThat(terminal1.getTerminalProperties().getSkipEnvset()).isTrue();
        assertThat(terminal1.getTerminalProperties().getSpecifyDebugCcl()).isFalse();
        assertThat(terminal1.getTerminalProperties().getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminal1.getTerminalProperties().getCclPromptPattern()).isEqualTo("cclPromptPattern");
        assertThat(terminal1.getTerminalProperties().getCclLoginPromptPattern()).isEqualTo("cclLoginPromptPattern");
        assertThat(terminal1.getTerminalProperties().getCclLoginSuccessPromptPattern())
                .isEqualTo("cclLoginSuccessPromptPattern");
        assertThat(terminal1.getTerminalProperties().getCclLoginFailurePromptPatterns()).containsExactly("pattern1",
                "pattern2");

        final TerminalProperties terminalProperties2 = TerminalProperties.getNewBuilder().setSkipEnvset(true)
                .setSpecifyDebugCcl(true).setOsPromptPattern("osPromptPattern")
                .setCclLoginPromptPattern("cclLoginPromptPattern")
                .setCclLoginFailurePromptPatterns(Arrays.asList(new String[] { "pattern1", "pattern2" })).build();
        final CclCommandTerminal terminal2 = new CclCommandTerminal(terminalProperties2, null);
        assertThat(terminal2.getTerminalProperties().getSkipEnvset()).isTrue();
        assertThat(terminal2.getTerminalProperties().getSpecifyDebugCcl()).isTrue();
        assertThat(terminal2.getTerminalProperties().getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminal2.getTerminalProperties().getCclPromptPattern())
                .isEqualTo(terminalPropertiesDefault.getCclPromptPattern());
        assertThat(terminal2.getTerminalProperties().getCclLoginPromptPattern()).isEqualTo("cclLoginPromptPattern");
        assertThat(terminal2.getTerminalProperties().getCclLoginSuccessPromptPattern())
                .isEqualTo(terminalPropertiesDefault.getCclLoginSuccessPromptPattern());
        assertThat(terminal2.getTerminalProperties().getCclLoginFailurePromptPatterns())
                .isEqualTo(Arrays.asList("pattern1", "pattern2"));

        final TerminalProperties terminalProperties3 = TerminalProperties.getNewBuilder()
                .setOsPromptPattern("osPromptPattern").setCclPromptPattern("cclPromptPattern")
                .setCclLoginSuccessPromptPattern("cclLoginSuccessPromptPattern").build();
        final CclCommandTerminal terminal3 = new CclCommandTerminal(terminalProperties3, null);
        assertThat(terminal3.getTerminalProperties().getSkipEnvset()).isFalse();
        assertThat(terminal3.getTerminalProperties().getSpecifyDebugCcl()).isTrue();
        assertThat(terminal3.getTerminalProperties().getOsPromptPattern()).isEqualTo("osPromptPattern");
        assertThat(terminal3.getTerminalProperties().getCclPromptPattern()).isEqualTo("cclPromptPattern");
        assertThat(terminal3.getTerminalProperties().getCclLoginPromptPattern())
                .isEqualTo(terminalPropertiesDefault.getCclLoginPromptPattern());
        assertThat(terminal3.getTerminalProperties().getCclLoginSuccessPromptPattern())
                .isEqualTo("cclLoginSuccessPromptPattern");
        assertThat(terminal3.getTerminalProperties().getCclLoginFailurePromptPatterns())
                .isEqualTo(terminalPropertiesDefault.getCclLoginFailurePromptPatterns());
    }

    /**
     * Test the execution of commands with envset and without authentication or specifyDebugCcl.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecuteCommandsWithEnvsetWithoutAuthentication() throws Exception {
        final TerminalProperties terminalProperties = TerminalProperties.getNewBuilder().setSpecifyDebugCcl(false)
                .setOsPromptPattern("osPromptPattern").setCclPromptPattern("cclPromptPattern")
                .setCclLoginPromptPattern("cclLoginPromptPattern")
                .setCclLoginSuccessPromptPattern("cclLoginSuccessPromptPattern")
                .setCclLoginFailurePromptPatterns(Arrays.asList(new String[] { "pattern1", "pattern2" })).build();

        final CclCommandTerminal terminal = new CclCommandTerminal(terminalProperties, null);
        final String command1 = "command1 go";

        terminal.executeCommands(sshTerminal, Collections.singletonList(command1), false);

        final ArgumentCaptor<List<CommandExpectationGroup>> commandGroupsCaptor = argumentCaptorListCommandExpectationGroup;
        verify(sshTerminal).executeCommandGroups(commandGroupsCaptor.capture());
        final List<CommandExpectationGroup> commandExpectationGroups = commandGroupsCaptor.getValue();

        assertThat(commandExpectationGroups.size()).isEqualTo(6);
        assertThat(commandExpectationGroups.get(0).getCommands()).isEqualTo(Arrays.asList("envset " + environmentName));
        assertThat(commandExpectationGroups.get(0).getExpectations()).isEqualTo(Arrays.asList("osPromptPattern"));
        assertThat(commandExpectationGroups.get(1).getCommands()).isEqualTo(Arrays.asList("ccl"));
        assertThat(commandExpectationGroups.get(1).getExpectations()).isEqualTo(Arrays.asList("cclLoginPromptPattern"));
        assertThat(commandExpectationGroups.get(2).getCommands()).isEqualTo(Arrays.asList("", ""));
        assertThat(commandExpectationGroups.get(3).getCommands()).isEqualTo(Arrays.asList("reset", "%t"));
        assertThat(commandExpectationGroups.get(3).getExpectations()).isEqualTo(Arrays.asList("cclPromptPattern"));
        assertThat(commandExpectationGroups.get(4).getCommands())
                .isEqualTo(Arrays.asList("set width 132 go", command1));
        assertThat(commandExpectationGroups.get(4).getExpectations()).isEqualTo(Arrays.asList("cclPromptPattern",
                CclCommandTerminal.CCL_VIEWER_PATTERN, CclCommandTerminal.CCL_ABORT_PATTERN));
        assertThat(commandExpectationGroups.get(5).getCommands()).isEqualTo(Arrays.asList("exit"));
        assertThat(commandExpectationGroups.get(5).getExpectations()).isEqualTo(Arrays.asList("osPromptPattern"));

        // No output stream was set, so no output should have been set
        verify(sshTerminal, never()).setOutputStream(ArgumentMatchers.<OutputStream> any());
    }

    /**
     * Test the execution of commands without envset and with authentication.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecuteCommandsWithoutEnvsetWithAuthentication() throws Exception {
        final TerminalProperties terminalProperties = TerminalProperties.getNewBuilder().setSkipEnvset(true)
                .setSpecifyDebugCcl(true).setOsPromptPattern("osPromptPattern").setCclPromptPattern("cclPromptPattern")
                .setCclLoginPromptPattern("cclLoginPromptPattern")
                .setCclLoginSuccessPromptPattern("cclLoginSuccessPromptPattern")
                .setCclLoginFailurePromptPatterns(Arrays.asList(new String[] { "pattern1", "pattern2" }))
                .setExpectationTimeout(333).build();
        final CclCommandTerminal terminal = new CclCommandTerminal(terminalProperties, null);
        final String command1 = "command1 go";

        terminal.executeCommands(sshTerminal, Collections.singletonList(command1), true);

        final ArgumentCaptor<List<CommandExpectationGroup>> commandGroupsCaptor = argumentCaptorListCommandExpectationGroup;
        verify(sshTerminal).executeCommandGroups(commandGroupsCaptor.capture());
        final List<CommandExpectationGroup> commandExpectationGroups = commandGroupsCaptor.getValue();

        assertThat(commandExpectationGroups.size()).isEqualTo(9);
        assertThat(commandExpectationGroups.get(0).getCommands()).isEqualTo(Collections.<String> emptyList());
        assertThat(commandExpectationGroups.get(0).getExpectations()).isEqualTo(Collections.<String> emptyList());
        assertThat(commandExpectationGroups.get(1).getCommands()).isEqualTo(Arrays.asList("$cer_exe/cclora_dbg"));
        assertThat(commandExpectationGroups.get(1).getExpectations()).isEqualTo(Arrays.asList("cclLoginPromptPattern"));
        assertThat(commandExpectationGroups.get(2).getCommands()).isEqualTo(Arrays.asList(domainUserName));
        assertThat(commandExpectationGroups.get(2).getExpectations()).isEqualTo(Arrays.asList(domainUserName));
        assertThat(commandExpectationGroups.get(3).getCommands()).isEqualTo(Arrays.asList(domainName));
        assertThat(commandExpectationGroups.get(3).getExpectations()).isEqualTo(Arrays.asList(domainName));
        assertThat(commandExpectationGroups.get(4).getCommands()).isEqualTo(Arrays.asList(domainPassword));
        assertThat(commandExpectationGroups.get(4).getExpectations())
                .isEqualTo(Arrays.asList("cclLoginSuccessPromptPattern", "pattern1", "pattern2"));
        assertThat(commandExpectationGroups.get(5).getCommands()).isEqualTo(Arrays.asList("", ""));
        assertThat(commandExpectationGroups.get(6).getCommands()).isEqualTo(Arrays.asList("reset", "%t"));
        assertThat(commandExpectationGroups.get(6).getExpectations()).isEqualTo(Arrays.asList("cclPromptPattern"));
        assertThat(commandExpectationGroups.get(7).getCommands())
                .isEqualTo(Arrays.asList("set width 132 go", command1));
        assertThat(commandExpectationGroups.get(7).getExpectations()).isEqualTo(Arrays.asList("cclPromptPattern",
                CclCommandTerminal.CCL_VIEWER_PATTERN, CclCommandTerminal.CCL_ABORT_PATTERN));
        assertThat(commandExpectationGroups.get(8).getCommands()).isEqualTo(Arrays.asList("exit"));
        assertThat(commandExpectationGroups.get(8).getExpectations()).isEqualTo(Arrays.asList("osPromptPattern"));
        verify(sshTerminal).setExpectationTimeout(333);

        // No output stream was set, so no output should have been set
        verify(sshTerminal, never()).setOutputStream(ArgumentMatchers.<OutputStream> any());
        verify(jetmMock).collect();
    }

    /**
     * If the subject has a {@link MillenniumDomainPrincipal} on it, then the user's Millennium credentials should be
     * used as part of the login.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecuteCommandsDomainLogin() throws Exception {
        final String username = "username";
        final String password = "password";
        final String domainName = "domain.name";

        final MillenniumDomainPrincipal principal = mock(MillenniumDomainPrincipal.class);
        when(principal.getUsername()).thenReturn(username);
        when(principal.getDomainName()).thenReturn(domainName);

        final MillenniumDomainPasswordCredential credential = mock(MillenniumDomainPasswordCredential.class);
        when(credential.getPassword()).thenReturn(password);

        when(JaasUtils.hasPrincipal(MillenniumDomainPrincipal.class)).thenReturn(Boolean.TRUE);
        when(JaasUtils.getPrincipal(MillenniumDomainPrincipal.class)).thenReturn(principal);
        when(JaasUtils.getPrivateCredential(MillenniumDomainPasswordCredential.class)).thenReturn(credential);

        final String command1 = "command1 go";
        final String command2 = "command2 go";
        final String command3 = "command2 go";

        final TerminalProperties terminalProperties = TerminalProperties.getNewBuilder().setSkipEnvset(true)
                .setSpecifyDebugCcl(true).setOsPromptPattern("osPromptPattern").setCclPromptPattern("cclPromptPattern")
                .setCclLoginPromptPattern("cclLoginPromptPattern")
                .setCclLoginSuccessPromptPattern("cclLoginSuccessPromptPattern")
                .setCclLoginFailurePromptPatterns(Arrays.asList(new String[] { "pattern1", "pattern2" })).build();

        final CclCommandTerminal terminal = new CclCommandTerminal(terminalProperties, null);
        terminal.executeCommands(sshTerminal, Arrays.asList(command1, command2, command3), true);

        final ArgumentCaptor<List<CommandExpectationGroup>> commandGroupsCaptor = argumentCaptorListCommandExpectationGroup;
        verify(sshTerminal).executeCommandGroups(commandGroupsCaptor.capture());
        final List<CommandExpectationGroup> commandExpectationGroups = commandGroupsCaptor.getValue();
        assertThat(commandExpectationGroups.size()).isEqualTo(9);
        assertThat(commandExpectationGroups.get(0).getCommands()).isEqualTo(Collections.<String> emptyList());
        assertThat(commandExpectationGroups.get(0).getExpectations()).isEqualTo(Collections.<String> emptyList());
        assertThat(commandExpectationGroups.get(1).getCommands()).isEqualTo(Arrays.asList("$cer_exe/cclora_dbg"));
        assertThat(commandExpectationGroups.get(1).getExpectations()).isEqualTo(Arrays.asList("cclLoginPromptPattern"));
        assertThat(commandExpectationGroups.get(2).getCommands()).isEqualTo(Arrays.asList(username));
        assertThat(commandExpectationGroups.get(2).getExpectations()).isEqualTo(Arrays.asList(username));
        assertThat(commandExpectationGroups.get(3).getCommands()).isEqualTo(Arrays.asList(domainName));
        assertThat(commandExpectationGroups.get(3).getExpectations()).isEqualTo(Arrays.asList(domainName));
        assertThat(commandExpectationGroups.get(4).getCommands()).isEqualTo(Arrays.asList(password));
        assertThat(commandExpectationGroups.get(4).getExpectations())
                .isEqualTo(Arrays.asList("cclLoginSuccessPromptPattern", "pattern1", "pattern2"));
        assertThat(commandExpectationGroups.get(5).getCommands()).isEqualTo(Arrays.asList("", ""));
        assertThat(commandExpectationGroups.get(6).getCommands()).isEqualTo(Arrays.asList("reset", "%t"));
        assertThat(commandExpectationGroups.get(6).getExpectations()).isEqualTo(Arrays.asList("cclPromptPattern"));
        assertThat(commandExpectationGroups.get(7).getCommands())
                .isEqualTo(Arrays.asList("set width 132 go", command1, command2, command3));
        assertThat(commandExpectationGroups.get(7).getExpectations()).isEqualTo(Arrays.asList("cclPromptPattern",
                CclCommandTerminal.CCL_VIEWER_PATTERN, CclCommandTerminal.CCL_ABORT_PATTERN));
        assertThat(commandExpectationGroups.get(8).getCommands()).isEqualTo(Arrays.asList("exit"));
        assertThat(commandExpectationGroups.get(8).getExpectations()).isEqualTo(Arrays.asList("osPromptPattern"));

        // No output stream was set, so no output should have been set
        verify(sshTerminal, never()).setOutputStream(ArgumentMatchers.<OutputStream> any());
        verify(jetmMock).collect();
    }

    /**
     * Test the setting of the output stream for CCL session output.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSetCclOutputStream() throws Exception {
        final OutputStream outputStream = mock(OutputStream.class);
        final CclOutputStreamProxy proxy = mock(CclOutputStreamProxy.class);
        whenNew(CclOutputStreamProxy.class)
                .withArguments(outputStream, CclCommandTerminal.OUTPUT_START, CclCommandTerminal.OUTPUT_END)
                .thenReturn(proxy);

        final OutputStreamConfiguration streamConfiguration = mock(OutputStreamConfiguration.class);
        when(streamConfiguration.getOutputStream()).thenReturn(outputStream);
        when(streamConfiguration.getOutputType()).thenReturn(OutputType.CCL_SESSION);

        final CclCommandTerminal terminal = new CclCommandTerminal(
                TerminalProperties.getNewBuilder().setOsPromptPattern("osPromptPattern").build(), streamConfiguration);
        terminal.executeCommands(sshTerminal, Collections.singletonList("a command go"), false);

        final ArgumentCaptor<List<CommandExpectationGroup>> commandGroupsCaptor = argumentCaptorListCommandExpectationGroup;
        verify(sshTerminal).executeCommandGroups(commandGroupsCaptor.capture());
        final List<CommandExpectationGroup> commandExpectationGroups = commandGroupsCaptor.getValue();
        assertThat(commandExpectationGroups.size()).isEqualTo(6);
        assertThat(commandExpectationGroups.get(0).getCommands()).isEqualTo(Arrays.asList("envset " + environmentName));
        assertThat(commandExpectationGroups.get(1).getCommands()).isEqualTo(Arrays.asList("$cer_exe/cclora_dbg"));
        assertThat(commandExpectationGroups.get(2).getCommands()).isEqualTo(Arrays.asList("", ""));
        assertThat(commandExpectationGroups.get(3).getCommands()).isEqualTo(Arrays.asList("reset", "%t"));
        assertThat(commandExpectationGroups.get(4).getCommands())
                .isEqualTo(Arrays.asList("set width 132 go", CclCommandTerminal.CCL_OUTPUT_START_COMMAND,
                        "a command go", CclCommandTerminal.CCL_OUTPUT_END_COMMAND));
        assertThat(commandExpectationGroups.get(5).getCommands()).isEqualTo(Arrays.asList("exit"));

        verify(sshTerminal).setOutputStream(proxy);
        verify(jetmMock).collect();
    }

    /**
     * Test the setting of the output stream for all session output.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testSetFullDebugOutputStream() throws Exception {
        final OutputStream outStream = mock(OutputStream.class);
        final OutputStreamProxy proxy = mock(OutputStreamProxy.class);
        whenNew(OutputStreamProxy.class).withArguments(outStream).thenReturn(proxy);

        final OutputStreamConfiguration streamConfiguration = mock(OutputStreamConfiguration.class);
        when(streamConfiguration.getOutputStream()).thenReturn(outStream);
        when(streamConfiguration.getOutputType()).thenReturn(OutputType.FULL_DEBUG);

        final CclCommandTerminal terminal = new CclCommandTerminal(
                TerminalProperties.getNewBuilder().setOsPromptPattern("osPromptPattern").build(), streamConfiguration);
        terminal.executeCommands(sshTerminal, Collections.singletonList("a command go"), false);

        verify(sshTerminal).setOutputStream(proxy);
        verify(jetmMock).collect();

        final ArgumentCaptor<List<CommandExpectationGroup>> commandGroupsCaptor = argumentCaptorListCommandExpectationGroup;
        verify(sshTerminal).executeCommandGroups(commandGroupsCaptor.capture());
        final List<CommandExpectationGroup> commandExpectationGroups = commandGroupsCaptor.getValue();
        assertThat(commandExpectationGroups.size()).isEqualTo(6);
        assertThat(commandExpectationGroups.get(0).getCommands()).isEqualTo(Arrays.asList("envset " + environmentName));
        assertThat(commandExpectationGroups.get(1).getCommands()).isEqualTo(Arrays.asList("$cer_exe/cclora_dbg"));
        assertThat(commandExpectationGroups.get(2).getCommands()).isEqualTo(Arrays.asList("", ""));
        assertThat(commandExpectationGroups.get(3).getCommands()).isEqualTo(Arrays.asList("reset", "%t"));
        assertThat(commandExpectationGroups.get(4).getCommands())
                .isEqualTo(Arrays.asList("set width 132 go", "a command go"));
        assertThat(commandExpectationGroups.get(5).getCommands()).isEqualTo(Arrays.asList("exit"));
    }
}
