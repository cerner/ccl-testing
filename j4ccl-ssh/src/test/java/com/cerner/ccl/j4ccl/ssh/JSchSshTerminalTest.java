package com.cerner.ccl.j4ccl.ssh;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.LoggerFactory;

import com.cerner.ccl.j4ccl.TerminalProperties;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePasswordCredential;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePrincipal;
import com.cerner.ccl.j4ccl.impl.jaas.JaasUtils;
import com.cerner.ftp.jsch.Connection;
import com.cerner.ftp.jsch.ConnectionPool;
import com.cerner.ftp.jsch.ConnectionPoolFactory;
import com.jcraft.jsch.ChannelShell;

import expect4j.Expect4j;
import expect4j.ExpectState;
import expect4j.matches.Match;
import expect4j.matches.RegExpMatch;

/**
 * Unit test of {@link JSchSshTerminal}.
 *
 * @author Joshua Hyde
 *
 */
@PowerMockIgnore("javax.security.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { JaasUtils.class, JSchSshTerminal.class, TerminalResponse.class, ConnectionPoolFactory.class,
        LoggerFactory.class })
public class JSchSshTerminalTest {
    private final String username = "username";
    private final String password = "password";
    private final String serverAddress = "http://www.google.com";

    @Mock
    private Connection conn;
    @Mock
    private ConnectionPool pool;
    @Mock
    private ChannelShell shell;
    @Mock
    private InputStream mockInputStream;
    @Mock
    private OutputStream mockOutputStream;
    // @Mock
    // private Logger logger;

    private JSchSshTerminal terminal;

    @Captor
    private ArgumentCaptor<List<String>> argumentCaptorListString;
    @Captor
    private ArgumentCaptor<List<Match>> argumentCaptorListMatch;

    /**
     * One time initialization
     */
    @BeforeClass
    public static void setupOnce() {
        TerminalProperties.setGlobalTerminalProperties(
                new TerminalProperties.TerminalPropertiesBuilder().setOsPromptPattern("hi guys").build());
    }

    /**
     * Set up the connection pool and configuration for each test.
     *
     * @throws IOException
     *             Sometimes bad things happen.
     */
    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        final BackendNodePrincipal principal = mock(BackendNodePrincipal.class);
        when(principal.getUsername()).thenReturn(username);
        when(principal.getHostname()).thenReturn(serverAddress);

        final BackendNodePasswordCredential credential = mock(BackendNodePasswordCredential.class);
        when(credential.getPassword()).thenReturn(password);

        mockStatic(JaasUtils.class);
        when(JaasUtils.getPrincipal(BackendNodePrincipal.class)).thenReturn(principal);
        when(JaasUtils.getPrivateCredential(BackendNodePasswordCredential.class)).thenReturn(credential);

        when(pool.getConnection(username, password, URI.create(serverAddress))).thenReturn(conn);
        when(conn.getShell()).thenReturn(shell);
        when(conn.isClosed()).thenReturn(false, true);

        when(shell.getInputStream()).thenReturn(mockInputStream);
        when(shell.getOutputStream()).thenReturn(mockOutputStream);
        when(shell.getExitStatus()).thenReturn(Integer.MAX_VALUE);
        when(shell.isConnected()).thenReturn(false, true);

        terminal = new JSchSshTerminal(pool);

        mockStatic(ConnectionPoolFactory.class);
        when(ConnectionPoolFactory.getInstance()).thenReturn(pool);
    }

    /**
     * Test the correct return code is given when the terminal is given an empty list of commands.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testExecuteEmptyCollection() throws Exception {
        when(shell.getExitStatus()).thenReturn(Integer.valueOf(0));
        final List<String> commands = Collections.emptyList();
        assertThat(executeCommandsHelper(terminal, commands, "osPromptPattern").getExitStatus()).isZero();
    }

    /**
     * Verify that the return code is returned by the {@link ChannelShell} in use by the terminal.
     *
     * @throws Exception
     *             If any errors occur while running the test.
     */
    @Test
    public void testExecuteReturnCode() throws Exception {
        assertThat(executeCommandsHelper(terminal, Arrays.asList("echo hello"), "osPromptPattern").getExitStatus())
                .isEqualTo(Integer.MAX_VALUE);

        final ArgumentCaptor<OutputStream> fileCaptor = ArgumentCaptor.forClass(OutputStream.class);
        verify(shell).setOutputStream(fileCaptor.capture());
        fileCaptor.getValue().close();
    }

    /**
     * Test that the expectationTimeout is honored when executeCommandGroups is called. If the globalTerminalProperties
     * is -1 or has a non-zero value. Otherwise the default value 20000 will be used. Regardless, an infinite timeout
     * (-1) will be used if the command ends in go.
     *
     * @throws Exception
     *             If any errors occur during the test.
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testExpectationTimeoutForExecuteCommandGroups() throws Exception {
        final JSchSshTerminal timeoutTerminal = new JSchSshTerminal(pool);

        final ExpectState expectState = mock(ExpectState.class);
        when(expectState.getMatch(0)).thenReturn("match string");
        final Expect4j expect = mock(Expect4j.class);
        when(expect.expect(any(List.class))).thenReturn(1);
        when(expect.getLastState()).thenReturn(expectState);

        whenNew(Expect4j.class).withAnyArguments().thenReturn(expect);

        final ArgumentCaptor<Long> captor = ArgumentCaptor.forClass(Long.class);

        final String osPromptValue = TerminalProperties.getGlobalTerminalProperties().getOsPromptPattern();

        // since the expectationTimeout has not been set, the default value will be used
        executeCommandsHelper(timeoutTerminal, Arrays.asList(new String[] { "some command", "command ending with go" }),
                null);

        // since the expectationTimeout has been set, the set value 301 will be used.
        timeoutTerminal.setExpectationTimeout(301);
        executeCommandsHelper(timeoutTerminal, Arrays.asList(new String[] { "some command", "command ending with go" }),
                null);

        // setting the expectationTimeout to 0 does nothing. Last set value will be used.
        timeoutTerminal.setExpectationTimeout(0);
        executeCommandsHelper(timeoutTerminal, Arrays.asList(new String[] { "some command", "command ending with go" }),
                null);

        // since the expectationTimeout has been set, the set value 1234 will be used.
        timeoutTerminal.setExpectationTimeout(1234);
        executeCommandsHelper(timeoutTerminal, Arrays.asList(new String[] { "some command", "command ending with go" }),
                null);

        // setting the expectationTimeout to a large negative number does nothing. Last set value will be used.
        timeoutTerminal.setExpectationTimeout(-10);
        executeCommandsHelper(timeoutTerminal, Arrays.asList(new String[] { "some command", "command ending with go" }),
                null);
        // the value -1 is honored though
        timeoutTerminal.setExpectationTimeout(-1);
        executeCommandsHelper(timeoutTerminal, Arrays.asList(new String[] { "some command", "command ending with go" }),
                null);

        verify(expect, times(12)).expect(argumentCaptorListMatch.capture());

        boolean defaultOsPromptWasExpectedAtLeastOnce = false;
        for (final List<Match> matches : argumentCaptorListMatch.getAllValues()) {
            for (final Match match : matches) {
                final String pattern = ((RegExpMatch) match).getPattern().getPattern();
                assertThat(pattern).isNotEmpty();
                if (pattern.equals(osPromptValue)) {
                    defaultOsPromptWasExpectedAtLeastOnce = true;
                }
            }
        }
        assertThat(defaultOsPromptWasExpectedAtLeastOnce).as("exected an expectation for the default OS prompt")
                .isTrue();

        verify(expect, times(18)).setDefaultTimeout(captor.capture());
        final List<Long> values = captor.getAllValues();
        assertThat(values.get(0)).isEqualTo(20000);
        assertThat(values.get(1)).isEqualTo(20000);
        assertThat(values.get(2)).isEqualTo(-1);
        assertThat(values.get(3)).isEqualTo(301);
        assertThat(values.get(4)).isEqualTo(301);
        assertThat(values.get(5)).isEqualTo(-1);
        assertThat(values.get(6)).isEqualTo(301);
        assertThat(values.get(7)).isEqualTo(301);
        assertThat(values.get(8)).isEqualTo(-1);
        assertThat(values.get(9)).isEqualTo(1234);
        assertThat(values.get(10)).isEqualTo(1234);
        assertThat(values.get(11)).isEqualTo(-1);
        assertThat(values.get(12)).isEqualTo(1234);
        assertThat(values.get(13)).isEqualTo(1234);
        assertThat(values.get(14)).isEqualTo(-1);
        assertThat(values.get(15)).isEqualTo(-1);
        assertThat(values.get(16)).isEqualTo(-1);
        assertThat(values.get(17)).isEqualTo(-1);
    }

    /**
     * Test that the output stream set for the terminal is passed to the underlying {@link ChannelShell} object.
     *
     * @throws Exception
     *             If an error occurs during the test run.
     */
    @Test
    public void testSetOutputStream() throws Exception {
        final OutputStream stream = mock(OutputStream.class);
        when(shell.isClosed()).thenReturn(Boolean.TRUE);

        terminal.setOutputStream(stream);
        executeCommandsHelper(terminal, Arrays.asList("I am a command"), "osPromptPattern");
        verify(shell, times(1)).setOutputStream(stream);
    }

    /**
     * Confirms that the OS prompt pattern is used if it is specified.
     *
     * @throws Exception
     *             Bad things might happen
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testOSPromptPatternIsHonored() throws Exception {
        final JSchSshTerminal timeoutTerminal = new JSchSshTerminal();
        final ExpectState expectState = mock(ExpectState.class);
        when(expectState.getMatch(0)).thenReturn("match string");
        final Expect4j expect = mock(Expect4j.class);
        when(expect.expect(any(List.class))).thenReturn(1);
        when(expect.getLastState()).thenReturn(expectState);

        whenNew(Expect4j.class).withAnyArguments().thenReturn(expect);

        executeCommandsHelper(timeoutTerminal, Collections.singletonList("blarg!"), "os-prompt-pattern>>");
        final ArgumentCaptor<OutputStream> fileCaptor = ArgumentCaptor.forClass(OutputStream.class);
        verify(expect).send("blarg!");
        verify(expect).send("\r");
        verify(expect).expect(argumentCaptorListMatch.capture());
        assertThat(argumentCaptorListMatch.getAllValues().size()).isEqualTo(1);
        assertThat(((RegExpMatch) argumentCaptorListMatch.getAllValues().get(0).get(0)).getPattern().getPattern())
                .isEqualTo("os-prompt-pattern>>");

        verify(shell).setOutputStream(fileCaptor.capture());
        fileCaptor.getValue().close();
    }

    /**
     * Test executeCommandGroups with an empty list of CommandExpectationGroup.
     *
     * @throws Exception
     *             Not expected but sometimes bad things happen.
     */
    @Test
    public void testExecteCommandGroupsEmptyList() throws Exception {
        final List<CommandExpectationGroup> commandExpectationGroups = new ArrayList<CommandExpectationGroup>();
        final TerminalResponse terminalResponse = terminal.executeCommandGroups(commandExpectationGroups);
        assertThat(terminalResponse.getExitStatus()).isEqualTo(0);
        assertThat(terminalResponse.getOutput()).isEqualTo("");
        verify(pool, times(0)).getConnection(anyString(), anyString(), any(URI.class));
        verify(shell, times(0)).disconnect();
        verify(conn, times(0)).close();
    }

    private TerminalResponse executeCommandsHelper(final JSchSshTerminal terminal, final List<String> commands,
            final String prompt) throws Exception {
        List<CommandExpectationGroup> commandExpectationGroups = new ArrayList<CommandExpectationGroup>();
        CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(commands);
        commandExpectationGroup.addExpectation(
                prompt != null ? prompt : TerminalProperties.getGlobalTerminalProperties().getOsPromptPattern());
        commandExpectationGroups.add(commandExpectationGroup);
        return terminal.executeCommandGroups(commandExpectationGroups);
    }
}