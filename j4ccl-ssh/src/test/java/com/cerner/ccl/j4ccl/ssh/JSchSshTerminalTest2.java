package com.cerner.ccl.j4ccl.ssh;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cerner.ccl.j4ccl.TerminalProperties;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePasswordCredential;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePrincipal;
import com.cerner.ccl.j4ccl.impl.jaas.JaasUtils;
import com.cerner.ccl.j4ccl.ssh.exception.SshException;
import com.cerner.ftp.jsch.Connection;
import com.cerner.ftp.jsch.ConnectionPool;
import com.cerner.ftp.jsch.ConnectionPoolFactory;
import com.google.code.jetm.reporting.ext.PointFactory;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSchException;

import etm.core.monitor.EtmPoint;
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
        LoggerFactory.class, PointFactory.class })
public class JSchSshTerminalTest2 {
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
    @Mock
    Logger logger;

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
                TerminalProperties.getNewBuilder().setOsPromptPattern("osPromptPattern").build());
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

        when(shell.getInputStream()).thenReturn(mockInputStream);
        when(shell.getOutputStream()).thenReturn(mockOutputStream);
        when(shell.getExitStatus()).thenReturn(Integer.MAX_VALUE);
        when(shell.isClosed()).thenReturn(Boolean.TRUE);

        mockStatic(ConnectionPoolFactory.class);
        when(ConnectionPoolFactory.getInstance()).thenReturn(pool);

        mockStatic(LoggerFactory.class);
        when(LoggerFactory.getLogger(JSchSshTerminal.class)).thenReturn(logger);
        when(logger.isDebugEnabled()).thenReturn(true);
    }

    /**
     * Validates the logging in executeCommandGroups via the happy path. Also validates translateExpectVal for -3.
     *
     * @throws Exception
     *             Bad things might happen
     */
    @Test
    public void testExecuteCommandGroupsLogging() throws Exception {
        final ArgumentCaptor<String> debugXCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> debugXYCaptor1 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Object> debugXYCaptor2 = ArgumentCaptor.forClass(Object.class);
        final ArgumentCaptor<String> debugWXYZCaptor1 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> debugWXYZCaptor2 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Object> debugWXYZCaptor3 = ArgumentCaptor.forClass(String.class);

        final EtmPoint point = mock(EtmPoint.class);
        final EtmPoint pointGetConnection = mock(EtmPoint.class);
        mockStatic(PointFactory.class);
        when(PointFactory.getPoint(JSchSshTerminal.class, "executeCommandGroups")).thenReturn(point);
        when(PointFactory.getPoint(JSchSshTerminal.class, "getConnection")).thenReturn(pointGetConnection);

        when(shell.isConnected()).thenReturn(true);
        when(shell.getExitStatus()).thenReturn(17);
        when(conn.isClosed()).thenReturn(false);

        final List<CommandExpectationGroup> commandExpectationGroups = new ArrayList<CommandExpectationGroup>();

        CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 1", "command 2", "command 3" }));
        commandExpectationGroup.addExpectations(Arrays.asList(new String[] { "expectation 1", "expecation 2" }));
        commandExpectationGroups.add(commandExpectationGroup);

        commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 4", "command 5", "command 6" }));
        commandExpectationGroup.addExpectations(Arrays.asList(new String[] { "expectation 3" }));
        commandExpectationGroups.add(commandExpectationGroup);

        commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 7", "command 8" }));
        commandExpectationGroup
                .addExpectations(Arrays.asList(new String[] { "expectation 4", "expectation 5", "expectation 6" }));
        commandExpectationGroups.add(commandExpectationGroup);

        final ExpectState expectState = mock(ExpectState.class);
        when(expectState.getMatch()).thenReturn("match string");
        when(expectState.getBuffer()).thenReturn("-this is the expectState buffer");

        final Answer<Integer> expectAnswer = new Answer<Integer>() {
            int invocationCount = 0;

            @Override
            public Integer answer(final InvocationOnMock invocation) throws Throwable {
                final List<RegExpMatch> matches = invocation.<List<RegExpMatch>> getArgument(0);
                for (final RegExpMatch match : matches) {
                    match.getClosure().run(expectState);
                }
                invocationCount++;
                return invocationCount == 7 ? 1 : invocationCount == 8 ? -3 : 0;
            }
        };

        final Expect4j expect = mock(Expect4j.class);
        when(expect.expect(ArgumentMatchers.<List<Match>> any())).thenAnswer(expectAnswer);
        when(expect.getLastState()).thenReturn(expectState);

        whenNew(Expect4j.class).withAnyArguments().thenReturn(expect);

        final JSchSshTerminal someTerminal = new JSchSshTerminal(pool);
        final OutputStream os = new ByteArrayOutputStream();
        someTerminal.setOutputStream(os);
        final TerminalResponse terminalResponse = someTerminal.executeCommandGroups(commandExpectationGroups);
        assertThat(terminalResponse.getExitStatus()).isEqualTo(17);
        assertThat(terminalResponse.getOutput()).isEqualTo(
                "-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer");
        os.flush();
        os.close();
        final String osText = os.toString();
        assertThat(osText).contains(
                "-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer-this is the expectState buffer");

        verify(expect, times(8)).expect(ArgumentMatchers.<List<Match>> any());
        verify(logger, times(9)).debug(debugXCaptor.capture());
        verify(logger, times(4)).debug(debugXYCaptor1.capture(), debugXYCaptor2.capture());
        verify(logger, times(1)).debug(debugWXYZCaptor1.capture(), debugWXYZCaptor2.capture(),
                debugWXYZCaptor3.capture(), argumentCaptorListString.capture());

        final List<String> logMessages = debugXCaptor.getAllValues();
        assertThat(logMessages.get(0)).isEqualTo("entering JSchSshTerminal.executeCommandGroups");
        assertThat(logMessages.get(1)).isEqualTo(String.format("sending command (command 1)"));
        assertThat(logMessages.get(2)).isEqualTo(String.format("sending command (command 2)"));
        assertThat(logMessages.get(3)).isEqualTo(String.format("sending command (command 3)"));
        assertThat(logMessages.get(4)).isEqualTo(String.format("sending command (command 4)"));
        assertThat(logMessages.get(5)).isEqualTo(String.format("sending command (command 5)"));
        assertThat(logMessages.get(6)).isEqualTo(String.format("sending command (command 6)"));
        assertThat(logMessages.get(7)).isEqualTo(String.format("sending command (command 7)"));
        assertThat(logMessages.get(8)).isEqualTo(String.format("sending command (command 8)"));

        assertThat(debugXYCaptor1.getAllValues().get(0)).isEqualTo("commandExpectationGroup: {};");
        assertThat(debugXYCaptor1.getAllValues().get(1)).isEqualTo("commandExpectationGroup: {};");
        assertThat(debugXYCaptor1.getAllValues().get(2)).isEqualTo("commandExpectationGroup: {};");
        assertThat(debugXYCaptor1.getAllValues().get(3)).isEqualTo("osPromptPattern = {}");
        assertThat(debugXYCaptor2.getAllValues().get(0)).isEqualTo(commandExpectationGroups.get(0));
        assertThat(debugXYCaptor2.getAllValues().get(1)).isEqualTo(commandExpectationGroups.get(1));
        assertThat(debugXYCaptor2.getAllValues().get(2)).isEqualTo(commandExpectationGroups.get(2));
        assertThat(debugXYCaptor2.getAllValues().get(3))
                .isEqualTo(TerminalProperties.getGlobalTerminalProperties().getOsPromptPattern());

        assertThat(debugWXYZCaptor1.getAllValues().get(0))
                .isEqualTo("The expectation result was {} for command {} with patterns {}");
        assertThat(debugWXYZCaptor2.getAllValues().get(0)).isEqualTo("EOF (-3)");
        assertThat(debugWXYZCaptor3.getAllValues().get(0)).isEqualTo("command 8");
        assertThat(argumentCaptorListString.getAllValues().get(0))
                .isEqualTo(commandExpectationGroups.get(2).getExpectations());

        verify(conn).close();
        verify(shell).disconnect();
        verify(pointGetConnection).collect();
        verify(point).collect();
    }

    /**
     * Validates the logging in executeCommandGroups when a timeout occurs. Also validates translateExpectVal for -2.
     *
     * @throws Exception
     *             Bad things might happen
     */
    @Test
    public void testExecuteCommandGroupsLoggingTimeout() throws Exception {
        when(shell.isConnected()).thenReturn(true);
        when(conn.isClosed()).thenReturn(false);

        final ArgumentCaptor<String> debugXCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> debugXYCaptor1 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Object> debugXYCaptor2 = ArgumentCaptor.forClass(Object.class);
        final ArgumentCaptor<String> debugWXYZCaptor1 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> debugWXYZCaptor2 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Object> debugWXYZCaptor3 = ArgumentCaptor.forClass(String.class);

        mockStatic(LoggerFactory.class);
        when(LoggerFactory.getLogger(JSchSshTerminal.class)).thenReturn(logger);

        final List<CommandExpectationGroup> commandExpectationGroups = new ArrayList<CommandExpectationGroup>();

        CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 1", "", "" }));
        commandExpectationGroup.addExpectations(Arrays.asList(new String[] { "expectation 1", "expecation 2" }));
        commandExpectationGroups.add(commandExpectationGroup);

        commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 2", "command 3" }));
        commandExpectationGroup.addExpectations(Arrays.asList(new String[] { "expectation 3" }));
        commandExpectationGroups.add(commandExpectationGroup);

        final ExpectState expectState = mock(ExpectState.class);
        when(expectState.getMatch(0)).thenReturn("match string");
        final Expect4j expect = mock(Expect4j.class);
        when(expect.expect(ArgumentMatchers.<List<Match>> any())).thenReturn(0).thenReturn(3).thenReturn(-2);
        when(expect.getLastState()).thenReturn(expectState);

        whenNew(Expect4j.class).withAnyArguments().thenReturn(expect);

        boolean caughtException = false;
        try {
            new JSchSshTerminal(pool).executeCommandGroups(commandExpectationGroups);
        } catch (final SshException e) {
            caughtException = true;
            assertThat(e.getMessage()).isEqualTo("expectj4 send status TIMEOUT (-2) : command (command 3)")
                    .as("expected a specific SshException message");
        }
        assertThat(caughtException).isEqualTo(true).as("expected an SshException to be thrown");

        verify(expect, times(3)).expect(ArgumentMatchers.<List<Match>> any());
        verify(logger, times(6)).debug(debugXCaptor.capture());
        verify(logger, times(3)).debug(debugXYCaptor1.capture(), debugXYCaptor2.capture());
        verify(logger, times(1)).debug(debugWXYZCaptor1.capture(), debugWXYZCaptor2.capture(),
                debugWXYZCaptor3.capture(), argumentCaptorListString.capture());
        final List<String> logMessages = debugXCaptor.getAllValues();

        assertThat(logMessages.get(0)).isEqualTo("entering JSchSshTerminal.executeCommandGroups");
        assertThat(logMessages.get(1)).isEqualTo(String.format("sending command (command 1)"));
        assertThat(logMessages.get(2)).isEqualTo(String.format("sending command ()"));
        assertThat(logMessages.get(3)).isEqualTo(String.format("sending command ()"));
        assertThat(logMessages.get(4)).isEqualTo(String.format("sending command (command 2)"));
        assertThat(logMessages.get(5)).isEqualTo(String.format("sending command (command 3)"));

        assertThat(debugXYCaptor1.getAllValues().get(0)).isEqualTo("commandExpectationGroup: {};");
        assertThat(debugXYCaptor1.getAllValues().get(1)).isEqualTo("commandExpectationGroup: {};");
        assertThat(debugXYCaptor1.getAllValues().get(2)).isEqualTo("osPromptPattern = {}");

        assertThat(debugXYCaptor2.getAllValues().get(0)).isEqualTo(commandExpectationGroups.get(0));
        assertThat(debugXYCaptor2.getAllValues().get(1)).isEqualTo(commandExpectationGroups.get(1));
        assertThat(debugXYCaptor2.getAllValues().get(2))
                .isEqualTo(TerminalProperties.getGlobalTerminalProperties().getOsPromptPattern());

        assertThat(debugWXYZCaptor1.getAllValues().get(0))
                .isEqualTo("The expectation result was {} for command {} with patterns {}");
        assertThat(debugWXYZCaptor2.getAllValues().get(0)).isEqualTo("TIMEOUT (-2)");
        assertThat(debugWXYZCaptor3.getAllValues().get(0)).isEqualTo("command 3");

        assertThat(argumentCaptorListString.getAllValues().get(0))
                .isEqualTo(commandExpectationGroups.get(1).getExpectations());

        verify(conn).close();
        verify(shell).disconnect();
    }

    /**
     * Validates the logging in executeCommandGroups when a tried once occurs. Also validates translateExpectVal for -4.
     *
     * @throws Exception
     *             Bad things might happen
     */
    @Test
    public void testExecuteCommandGroupsLoggingTriedOnce() throws Exception {
        final ArgumentCaptor<String> debugXCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> debugXYCaptor1 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Object> debugXYCaptor2 = ArgumentCaptor.forClass(Object.class);
        final ArgumentCaptor<String> debugWXYZCaptor1 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> debugWXYZCaptor2 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Object> debugWXYZCaptor3 = ArgumentCaptor.forClass(String.class);

        final List<CommandExpectationGroup> commandExpectationGroups = new ArrayList<CommandExpectationGroup>();

        CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 1", "", "" }));
        commandExpectationGroup.addExpectations(Arrays.asList(new String[] { "expectation 1", "expecation 2" }));
        commandExpectationGroups.add(commandExpectationGroup);

        commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 2", "command 3" }));
        commandExpectationGroup.addExpectations(Arrays.asList(new String[] { "expectation 3" }));
        commandExpectationGroups.add(commandExpectationGroup);

        final ExpectState expectState = mock(ExpectState.class);
        when(expectState.getMatch(0)).thenReturn("match string");
        final Expect4j expect = mock(Expect4j.class);
        when(expect.expect(ArgumentMatchers.<List<Match>> any())).thenReturn(0).thenReturn(3).thenReturn(-4);
        when(expect.getLastState()).thenReturn(expectState);

        whenNew(Expect4j.class).withAnyArguments().thenReturn(expect);

        boolean caughtException = false;
        try {
            new JSchSshTerminal(pool).executeCommandGroups(commandExpectationGroups);
        } catch (final SshException e) {
            caughtException = true;
            assertThat(e.getMessage()).isEqualTo("expectj4 send status TRIED_ONCE (-4) : command (command 3)")
                    .as("expected a specific SshException message");
        }
        assertThat(caughtException).isEqualTo(true).as("expected an SshException to be thrown");

        verify(expect, times(3)).expect(ArgumentMatchers.<List<Match>> any());
        verify(logger, times(6)).debug(debugXCaptor.capture());
        verify(logger, times(3)).debug(debugXYCaptor1.capture(), debugXYCaptor2.capture());
        verify(logger, times(1)).debug(debugWXYZCaptor1.capture(), debugWXYZCaptor2.capture(),
                debugWXYZCaptor3.capture(), argumentCaptorListString.capture());

        final List<String> logMessages = debugXCaptor.getAllValues();
        assertThat(logMessages.get(0)).isEqualTo("entering JSchSshTerminal.executeCommandGroups");
        assertThat(logMessages.get(1)).isEqualTo(String.format("sending command (command 1)"));
        assertThat(logMessages.get(2)).isEqualTo(String.format("sending command ()"));
        assertThat(logMessages.get(3)).isEqualTo(String.format("sending command ()"));
        assertThat(logMessages.get(4)).isEqualTo(String.format("sending command (command 2)"));
        assertThat(logMessages.get(5)).isEqualTo(String.format("sending command (command 3)"));

        assertThat(debugXYCaptor1.getAllValues().get(0)).isEqualTo("commandExpectationGroup: {};");
        assertThat(debugXYCaptor1.getAllValues().get(1)).isEqualTo("commandExpectationGroup: {};");
        assertThat(debugXYCaptor1.getAllValues().get(2)).isEqualTo("osPromptPattern = {}");
        assertThat(debugXYCaptor2.getAllValues().get(0)).isEqualTo(commandExpectationGroups.get(0));
        assertThat(debugXYCaptor2.getAllValues().get(1)).isEqualTo(commandExpectationGroups.get(1));
        assertThat(debugXYCaptor2.getAllValues().get(2))
                .isEqualTo(TerminalProperties.getGlobalTerminalProperties().getOsPromptPattern());

        assertThat(debugWXYZCaptor1.getAllValues().get(0))
                .isEqualTo("The expectation result was {} for command {} with patterns {}");
        assertThat(debugWXYZCaptor2.getAllValues().get(0)).isEqualTo("TRIED_ONCE (-4)");
        assertThat(debugWXYZCaptor3.getAllValues().get(0)).isEqualTo("command 3");
        assertThat(argumentCaptorListString.getAllValues().get(0))
                .isEqualTo(commandExpectationGroups.get(1).getExpectations());
    }

    /**
     * Validates the logging in executeCommandGroups when an unknown issue occurs. Also validates translateExpectVal for
     * -1.
     *
     * @throws Exception
     *             Bad things might happen
     */
    @Test
    public void testExecuteCommandGroupsLoggingUnkownOne() throws Exception {
        final ArgumentCaptor<String> debugXCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> debugXYCaptor1 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Object> debugXYCaptor2 = ArgumentCaptor.forClass(Object.class);
        final ArgumentCaptor<String> debugWXYZCaptor1 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> debugWXYZCaptor2 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Object> debugWXYZCaptor3 = ArgumentCaptor.forClass(String.class);

        final List<CommandExpectationGroup> commandExpectationGroups = new ArrayList<CommandExpectationGroup>();

        CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 1", "", "" }));
        commandExpectationGroup.addExpectations(Arrays.asList(new String[] { "expectation 1", "expecation 2" }));
        commandExpectationGroups.add(commandExpectationGroup);

        commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 2", "command 3" }));
        commandExpectationGroup.addExpectations(Arrays.asList(new String[] { "expectation 3" }));
        commandExpectationGroups.add(commandExpectationGroup);

        final ExpectState expectState = mock(ExpectState.class);
        when(expectState.getMatch(0)).thenReturn("match string");
        final Expect4j expect = mock(Expect4j.class);
        when(expect.expect(ArgumentMatchers.<List<Match>> any())).thenReturn(0).thenReturn(3).thenReturn(-1);
        when(expect.getLastState()).thenReturn(expectState);

        whenNew(Expect4j.class).withAnyArguments().thenReturn(expect);

        boolean caughtException = false;
        try {
            new JSchSshTerminal(pool).executeCommandGroups(commandExpectationGroups);
        } catch (final SshException e) {
            caughtException = true;
            assertThat(e.getMessage()).isEqualTo("expectj4 send status UNKNOWN (-1) : command (command 3)")
                    .as("expected a specific SshException message");
        }
        assertThat(caughtException).isEqualTo(true).as("expected an SshException to be thrown");

        verify(expect, times(3)).expect(ArgumentMatchers.<List<Match>> any());
        verify(logger, times(6)).debug(debugXCaptor.capture());
        verify(logger, times(3)).debug(debugXYCaptor1.capture(), debugXYCaptor2.capture());
        verify(logger, times(1)).debug(debugWXYZCaptor1.capture(), debugWXYZCaptor2.capture(),
                debugWXYZCaptor3.capture(), argumentCaptorListString.capture());

        final List<String> logMessages = debugXCaptor.getAllValues();
        assertThat(logMessages.get(0)).isEqualTo("entering JSchSshTerminal.executeCommandGroups");
        assertThat(logMessages.get(1)).isEqualTo(String.format("sending command (command 1)"));
        assertThat(logMessages.get(2)).isEqualTo(String.format("sending command ()"));
        assertThat(logMessages.get(3)).isEqualTo(String.format("sending command ()"));
        assertThat(logMessages.get(4)).isEqualTo(String.format("sending command (command 2)"));
        assertThat(logMessages.get(5)).isEqualTo(String.format("sending command (command 3)"));

        assertThat(debugXYCaptor1.getAllValues().get(0)).isEqualTo("commandExpectationGroup: {};");
        assertThat(debugXYCaptor1.getAllValues().get(1)).isEqualTo("commandExpectationGroup: {};");
        assertThat(debugXYCaptor1.getAllValues().get(2)).isEqualTo("osPromptPattern = {}");
        assertThat(debugXYCaptor2.getAllValues().get(0)).isEqualTo(commandExpectationGroups.get(0));
        assertThat(debugXYCaptor2.getAllValues().get(1)).isEqualTo(commandExpectationGroups.get(1));
        assertThat(debugXYCaptor2.getAllValues().get(2))
                .isEqualTo(TerminalProperties.getGlobalTerminalProperties().getOsPromptPattern());

        assertThat(debugWXYZCaptor1.getAllValues().get(0))
                .isEqualTo("The expectation result was {} for command {} with patterns {}");
        assertThat(debugWXYZCaptor2.getAllValues().get(0)).isEqualTo("UNKNOWN (-1)");
        assertThat(debugWXYZCaptor3.getAllValues().get(0)).isEqualTo("command 3");
        assertThat(argumentCaptorListString.getAllValues().get(0))
                .isEqualTo(commandExpectationGroups.get(1).getExpectations());
    }

    /**
     * Validates the logging in executeCommandGroups when an unknown issue occurs. Also validates translateExpectVal for
     * -5.
     *
     * @throws Exception
     *             Bad things might happen
     */
    @Test
    public void testExecuteCommandGroupsLoggingUnkownTwo() throws Exception {
        final ArgumentCaptor<String> debugXCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> debugXYCaptor1 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Object> debugXYCaptor2 = ArgumentCaptor.forClass(Object.class);
        final ArgumentCaptor<String> debugWXYZCaptor1 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> debugWXYZCaptor2 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Object> debugWXYZCaptor3 = ArgumentCaptor.forClass(String.class);

        final List<CommandExpectationGroup> commandExpectationGroups = new ArrayList<CommandExpectationGroup>();

        CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 1", "", "" }));
        commandExpectationGroup.addExpectations(Arrays.asList(new String[] { "expectation 1", "expecation 2" }));
        commandExpectationGroups.add(commandExpectationGroup);

        commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 2", "command 3" }));
        commandExpectationGroup.addExpectations(Arrays.asList(new String[] { "expectation 3" }));
        commandExpectationGroups.add(commandExpectationGroup);

        final ExpectState expectState = mock(ExpectState.class);
        when(expectState.getMatch(0)).thenReturn("match string");
        final Expect4j expect = mock(Expect4j.class);
        when(expect.expect(ArgumentMatchers.<List<Match>> any())).thenReturn(0).thenReturn(3).thenReturn(-5);
        when(expect.getLastState()).thenReturn(expectState);

        whenNew(Expect4j.class).withAnyArguments().thenReturn(expect);

        boolean caughtException = false;
        try {
            new JSchSshTerminal(pool).executeCommandGroups(commandExpectationGroups);
        } catch (final SshException e) {
            caughtException = true;
            assertThat(e.getMessage()).isEqualTo("expectj4 send status UNKNOWN (-5) : command (command 3)")
                    .as("expected a specific SshException message");
        }
        assertThat(caughtException).isEqualTo(true).as("expected an SshException to be thrown");

        verify(expect, times(3)).expect(ArgumentMatchers.<List<Match>> any());
        verify(logger, times(6)).debug(debugXCaptor.capture());
        verify(logger, times(3)).debug(debugXYCaptor1.capture(), debugXYCaptor2.capture());
        verify(logger, times(1)).debug(debugWXYZCaptor1.capture(), debugWXYZCaptor2.capture(),
                debugWXYZCaptor3.capture(), argumentCaptorListString.capture());

        final List<String> logMessages = debugXCaptor.getAllValues();
        assertThat(logMessages.get(0)).isEqualTo("entering JSchSshTerminal.executeCommandGroups");
        assertThat(logMessages.get(1)).isEqualTo(String.format("sending command (command 1)"));
        assertThat(logMessages.get(2)).isEqualTo(String.format("sending command ()"));
        assertThat(logMessages.get(3)).isEqualTo(String.format("sending command ()"));
        assertThat(logMessages.get(4)).isEqualTo(String.format("sending command (command 2)"));
        assertThat(logMessages.get(5)).isEqualTo(String.format("sending command (command 3)"));

        assertThat(debugXYCaptor1.getAllValues().get(0)).isEqualTo("commandExpectationGroup: {};");
        assertThat(debugXYCaptor1.getAllValues().get(1)).isEqualTo("commandExpectationGroup: {};");
        assertThat(debugXYCaptor1.getAllValues().get(2)).isEqualTo("osPromptPattern = {}");
        assertThat(debugXYCaptor2.getAllValues().get(0)).isEqualTo(commandExpectationGroups.get(0));
        assertThat(debugXYCaptor2.getAllValues().get(1)).isEqualTo(commandExpectationGroups.get(1));
        assertThat(debugXYCaptor2.getAllValues().get(2))
                .isEqualTo(TerminalProperties.getGlobalTerminalProperties().getOsPromptPattern());

        assertThat(debugWXYZCaptor1.getAllValues().get(0))
                .isEqualTo("The expectation result was {} for command {} with patterns {}");
        assertThat(debugWXYZCaptor2.getAllValues().get(0)).isEqualTo("UNKNOWN (-5)");
        assertThat(debugWXYZCaptor3.getAllValues().get(0)).isEqualTo("command 3");
        assertThat(argumentCaptorListString.getAllValues().get(0))
                .isEqualTo(commandExpectationGroups.get(1).getExpectations());
    }

    /**
     * Exercise ExecuteCommanGroups with a shell connect failure.
     *
     * @throws Exception
     *             Bad things might happen
     * @throws IOException
     *             Bad things might happen
     */
    @Test
    public void testExecuteCommandGroupsWithShellConnectFailure() throws Exception {
        Mockito.doThrow(new JSchException("Huston. We have a problem.")).when(shell).connect();

        when(shell.isConnected()).thenReturn(false);
        when(conn.isClosed()).thenReturn(false);

        final ArgumentCaptor<String> stringCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<CommandExpectationGroup> commandExpectationGroupCaptor = ArgumentCaptor
                .forClass(CommandExpectationGroup.class);

        final List<CommandExpectationGroup> commandExpectationGroups = new ArrayList<CommandExpectationGroup>();

        CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 1", "", "" }));
        commandExpectationGroup.addExpectations(Arrays.asList(new String[] { "expectation 1", "expecation 2" }));
        commandExpectationGroups.add(commandExpectationGroup);

        commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 2", "command 3" }));
        commandExpectationGroup.addExpectations(Arrays.asList(new String[] { "expectation 3" }));
        commandExpectationGroups.add(commandExpectationGroup);

        final Expect4j expect = mock(Expect4j.class);
        whenNew(Expect4j.class).withAnyArguments().thenReturn(expect);

        SshException e = assertThrows(SshException.class, () -> {
            try {
                new JSchSshTerminal(pool).executeCommandGroups(commandExpectationGroups);
            } catch (final SshException ex) {
                verify(expect, times(0)).expect(ArgumentMatchers.<List<Match>> any());
                verify(logger, times(1)).debug(stringCaptor.capture());
                verify(logger, times(2)).debug(stringCaptor.capture(), commandExpectationGroupCaptor.capture());
                final List<String> logMessages = stringCaptor.getAllValues();
                final List<CommandExpectationGroup> expectationGroups = commandExpectationGroupCaptor.getAllValues();
                assertThat(logMessages.get(0)).isEqualTo("entering JSchSshTerminal.executeCommandGroups");
                assertThat(logMessages.get(1)).isEqualTo("commandExpectationGroup: {};");
                assertThat(logMessages.get(2)).isEqualTo("commandExpectationGroup: {};");
                assertThat(expectationGroups.get(0)).isEqualTo(commandExpectationGroups.get(0));
                assertThat(expectationGroups.get(1)).isEqualTo(commandExpectationGroups.get(1));
                verify(conn).close();
                verify(shell, times(0)).disconnect();
                throw ex;
            }
        });
        assertThat(e.getMessage()).isEqualTo("Failed to connect SSH shell.");
    }

    /**
     * Confirms that ExecuteCommandGroups trudges on when a malformed pattern is encountered echoing the exception trace
     * to standard err.
     *
     * @throws Exception
     *             Bad things might happen.
     */
    @Test
    public void testExecuteCommandGroupsWithMalformedPattern() throws Exception {
        when(shell.isConnected()).thenReturn(true);
        when(conn.isClosed()).thenReturn(false);

        final ArgumentCaptor<String> debugXCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> debugXYCaptor1 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Object> debugXYCaptor2 = ArgumentCaptor.forClass(Object.class);

        final List<CommandExpectationGroup> commandExpectationGroups = new ArrayList<CommandExpectationGroup>();

        CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 1", "", "" }));
        commandExpectationGroup
                .addExpectations(Arrays.asList(new String[] { "[malformed expectation 1", "ok expecation 1" }));
        commandExpectationGroups.add(commandExpectationGroup);

        commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 2", "command 3" }));
        commandExpectationGroup.addExpectations(
                Arrays.asList(new String[] { "ok expecation 2", "(malformed expectation 2, ", "ok expectation 3" }));
        commandExpectationGroups.add(commandExpectationGroup);

        final ExpectState expectState = mock(ExpectState.class);
        when(expectState.getMatch(0)).thenReturn("match string");
        final Expect4j expect = mock(Expect4j.class);
        when(expect.expect(ArgumentMatchers.<List<Match>> any())).thenReturn(0);
        when(expect.getLastState()).thenReturn(expectState);

        final ArgumentCaptor<String> commandCaptor = ArgumentCaptor.forClass(String.class);

        whenNew(Expect4j.class).withAnyArguments().thenReturn(expect);

        final PrintStream err = System.err;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setErr(new PrintStream(out));

        new JSchSshTerminal(pool).executeCommandGroups(commandExpectationGroups);

        String outText = out.toString("UTF-8");
        assertThat(outText)
                .startsWith("org.apache.oro.text.regex.MalformedPatternException: Unmatched [] in expression.");
        assertThat(outText)
                .contains("\tat com.cerner.ccl.j4ccl.ssh.JSchSshTerminal.executeCommandGroups(JSchSshTerminal.java:");
        outText = outText.substring(80 + outText
                .indexOf("\tat com.cerner.ccl.j4ccl.ssh.JSchSshTerminal.executeCommandGroups(JSchSshTerminal.java:"));
        assertThat(outText).contains("org.apache.oro.text.regex.MalformedPatternException: Unmatched parentheses.");
        assertThat(outText)
                .contains("\tat com.cerner.ccl.j4ccl.ssh.JSchSshTerminal.executeCommandGroups(JSchSshTerminal.java:");
        System.setErr(err);

        verify(expect, times(10)).send(commandCaptor.capture());
        verify(expect, times(3)).expect(argumentCaptorListMatch.capture());
        verify(logger, times(6)).debug(debugXCaptor.capture());
        verify(logger, times(3)).debug(debugXYCaptor1.capture(), debugXYCaptor2.capture());

        final List<String> logMessages = debugXCaptor.getAllValues();
        assertThat(logMessages.get(0)).isEqualTo("entering JSchSshTerminal.executeCommandGroups");
        assertThat(logMessages.get(1)).isEqualTo(String.format("sending command (command 1)"));
        assertThat(logMessages.get(2)).isEqualTo(String.format("sending command ()"));
        assertThat(logMessages.get(3)).isEqualTo(String.format("sending command ()"));
        assertThat(logMessages.get(4)).isEqualTo(String.format("sending command (command 2)"));
        assertThat(logMessages.get(5)).isEqualTo(String.format("sending command (command 3)"));

        assertThat(debugXYCaptor1.getAllValues().get(0)).isEqualTo("commandExpectationGroup: {};");
        assertThat(debugXYCaptor1.getAllValues().get(1)).isEqualTo("commandExpectationGroup: {};");
        assertThat(debugXYCaptor1.getAllValues().get(2)).isEqualTo("osPromptPattern = {}");
        assertThat(debugXYCaptor2.getAllValues().get(0)).isEqualTo(commandExpectationGroups.get(0));
        assertThat(debugXYCaptor2.getAllValues().get(1)).isEqualTo(commandExpectationGroups.get(1));
        assertThat(debugXYCaptor2.getAllValues().get(2))
                .isEqualTo(TerminalProperties.getGlobalTerminalProperties().getOsPromptPattern());

        final List<String> commands = commandCaptor.getAllValues();
        assertThat(commands.get(0)).isEqualTo("command 1").as("exected to send command 1");
        assertThat(commands.get(1)).isEqualTo("\r").as("exected to send \r after command 1");
        assertThat(commands.get(2)).isEqualTo("").as("exected to send \"\"");
        assertThat(commands.get(3)).isEqualTo("\r").as("exected to send \r after \"\"");
        assertThat(commands.get(4)).isEqualTo("").as("exected to send \"\"");
        assertThat(commands.get(5)).isEqualTo("\r").as("exected to send \r after \"\"");
        assertThat(commands.get(6)).isEqualTo("command 2").as("exected to send command 2");
        assertThat(commands.get(7)).isEqualTo("\r").as("exected to send \r after command 2");
        assertThat(commands.get(8)).isEqualTo("command 3").as("exected to send command 3");
        assertThat(commands.get(9)).isEqualTo("\r").as("exected to send \r after command 3");

        final List<List<Match>> matches = argumentCaptorListMatch.getAllValues();
        assertThat(matches.get(0).size()).isEqualTo(1).as("exected command 1 to have 1 valid expectation");
        assertThat(matches.get(1).size()).isEqualTo(2).as("exected command 2 to have 2 valid expectations");
        assertThat(matches.get(2).size()).isEqualTo(2).as("exected command 3 to have 2 valid expectations");

        verify(conn).close();
        verify(shell).disconnect();
    }

    /**
     * Confirm that executeCommandGroups applies the expectationTimout from the provided TerminalProperties
     *
     * @throws Exception
     *             Bad things can happen.
     */
    @Test
    public void testExecuteCommandGroupsExpectationTimeout() throws Exception {
        final ArgumentCaptor<String> debugXCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> debugXYCaptor1 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Object> debugXYCaptor2 = ArgumentCaptor.forClass(Object.class);
        final ArgumentCaptor<String> debugWXYZCaptor1 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> debugWXYZCaptor2 = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Object> debugWXYZCaptor3 = ArgumentCaptor.forClass(String.class);

        when(shell.isConnected()).thenReturn(true);
        when(conn.isClosed()).thenReturn(false);

        final List<CommandExpectationGroup> commandExpectationGroups = new ArrayList<CommandExpectationGroup>();

        CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 1", "command 2", "command 3" }));
        commandExpectationGroup.addExpectations(Arrays.asList(new String[] { "expectation 1", "expecation 2" }));
        commandExpectationGroups.add(commandExpectationGroup);

        commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 4", "command 5", "command 6" }));
        commandExpectationGroup.addExpectations(Arrays.asList(new String[] { "expectation 3" }));
        commandExpectationGroups.add(commandExpectationGroup);

        commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommands(Arrays.asList(new String[] { "command 7", "command 8" }));
        commandExpectationGroup
                .addExpectations(Arrays.asList(new String[] { "expectation 4", "expectation 5", "expectation 6" }));
        commandExpectationGroups.add(commandExpectationGroup);

        final ExpectState expectState = mock(ExpectState.class);
        when(expectState.getMatch(0)).thenReturn("match string");
        final Expect4j expect = mock(Expect4j.class);
        when(expect.expect(ArgumentMatchers.<List<Match>> any())).thenReturn(0).thenReturn(0).thenReturn(0)
                .thenReturn(0).thenReturn(0).thenReturn(0).thenReturn(1).thenReturn(-3);
        when(expect.getLastState()).thenReturn(expectState);

        whenNew(Expect4j.class).withAnyArguments().thenReturn(expect);

        final JSchSshTerminal someTerminal = new JSchSshTerminal(pool);
        final OutputStream os = new ByteArrayOutputStream();
        someTerminal.setOutputStream(os);
        someTerminal.executeCommandGroups(commandExpectationGroups);
        os.flush();
        assertThat(os.toString().concat("match string")).as("expected the output stream to contain 'match string.");

        verify(expect, times(8)).expect(ArgumentMatchers.<List<Match>> any());
        verify(logger, times(9)).debug(debugXCaptor.capture());
        verify(logger, times(4)).debug(debugXYCaptor1.capture(), debugXYCaptor2.capture());
        verify(logger, times(1)).debug(debugWXYZCaptor1.capture(), debugWXYZCaptor2.capture(),
                debugWXYZCaptor3.capture(), argumentCaptorListString.capture());

        final List<String> logMessages = debugXCaptor.getAllValues();
        assertThat(logMessages.get(0)).isEqualTo("entering JSchSshTerminal.executeCommandGroups");
        assertThat(logMessages.get(1)).isEqualTo(String.format("sending command (command 1)"));
        assertThat(logMessages.get(2)).isEqualTo(String.format("sending command (command 2)"));
        assertThat(logMessages.get(3)).isEqualTo(String.format("sending command (command 3)"));
        assertThat(logMessages.get(4)).isEqualTo(String.format("sending command (command 4)"));
        assertThat(logMessages.get(5)).isEqualTo(String.format("sending command (command 5)"));
        assertThat(logMessages.get(6)).isEqualTo(String.format("sending command (command 6)"));
        assertThat(logMessages.get(7)).isEqualTo(String.format("sending command (command 7)"));
        assertThat(logMessages.get(8)).isEqualTo(String.format("sending command (command 8)"));

        assertThat(debugXYCaptor1.getAllValues().get(0)).isEqualTo("commandExpectationGroup: {};");
        assertThat(debugXYCaptor1.getAllValues().get(1)).isEqualTo("commandExpectationGroup: {};");
        assertThat(debugXYCaptor1.getAllValues().get(2)).isEqualTo("commandExpectationGroup: {};");
        assertThat(debugXYCaptor1.getAllValues().get(3)).isEqualTo("osPromptPattern = {}");
        assertThat(debugXYCaptor2.getAllValues().get(0)).isEqualTo(commandExpectationGroups.get(0));
        assertThat(debugXYCaptor2.getAllValues().get(1)).isEqualTo(commandExpectationGroups.get(1));
        assertThat(debugXYCaptor2.getAllValues().get(2)).isEqualTo(commandExpectationGroups.get(2));
        assertThat(debugXYCaptor2.getAllValues().get(3))
                .isEqualTo(TerminalProperties.getGlobalTerminalProperties().getOsPromptPattern());

        assertThat(debugWXYZCaptor1.getAllValues().get(0))
                .isEqualTo("The expectation result was {} for command {} with patterns {}");
        assertThat(debugWXYZCaptor2.getAllValues().get(0)).isEqualTo("EOF (-3)");
        assertThat(debugWXYZCaptor3.getAllValues().get(0)).isEqualTo("command 8");
        assertThat(argumentCaptorListString.getAllValues().get(0))
                .isEqualTo(commandExpectationGroups.get(2).getExpectations());

        verify(conn).close();
        verify(shell).disconnect();

    }

    /**
     * Exercise executeCommandGroups when the connection is null.
     *
     * @throws Exception
     *             Bad things happen sometimes.
     */
    @Test
    public void testExecteCommandGroupsNullConnection() throws Exception {
        final ConnectionPool pool = mock(ConnectionPool.class);
        when(pool.getConnection(anyString(), anyString(), any(URI.class))).thenReturn(null);
        final JSchSshTerminal terminal = new JSchSshTerminal(pool);
        final List<CommandExpectationGroup> commandExpectationGroups = new ArrayList<CommandExpectationGroup>();
        final CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommand("command");
        commandExpectationGroups.add(commandExpectationGroup);

        final PrintStream err = System.err;
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setErr(new PrintStream(out));
        final TerminalResponse terminalResponse = terminal.executeCommandGroups(commandExpectationGroups);
        assertThat(out.toString("UTF-8")).startsWith(String.format(
                "java.lang.NullPointerException%n\tat com.cerner.ccl.j4ccl.ssh.JSchSshTerminal.executeCommandGroups(JSchSshTerminal.java:"));
        System.setErr(err);
        assertThat(terminalResponse.getExitStatus()).isEqualTo(0);
        assertThat(terminalResponse.getOutput()).isEqualTo("Error");
    }

    /**
     * Exercise executeCommandGroups when the shell is null and the connection is open.
     *
     * @throws Exception
     *             Bad things happen sometimes.
     */
    @Test
    public void testExecteCommandGroupsNullShellConnectionOpen() throws Exception {
        final ConnectionPool pool = mock(ConnectionPool.class);
        final Connection connection = mock(Connection.class);
        when(pool.getConnection(anyString(), anyString(), any(URI.class))).thenReturn(connection);
        when(connection.getShell()).thenReturn(null);
        when(connection.isClosed()).thenReturn(false);
        final JSchSshTerminal terminal = new JSchSshTerminal(pool);
        final List<CommandExpectationGroup> commandExpectationGroups = new ArrayList<CommandExpectationGroup>();
        final CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommand("command");
        commandExpectationGroups.add(commandExpectationGroup);
        terminal.executeCommandGroups(commandExpectationGroups);
        verify(connection).close();
    }

    /**
     * Exercise executeCommandGroups when the shell is null and the connection is not open.
     *
     * @throws Exception
     *             Bad things happen sometimes.
     */
    @Test
    public void testExecteCommandGroupsNullShellConnectionNotOpen() throws Exception {
        final ConnectionPool pool = mock(ConnectionPool.class);
        final ChannelShell shell = mock(ChannelShell.class);
        final Connection connection = mock(Connection.class);
        when(pool.getConnection(anyString(), anyString(), any(URI.class))).thenReturn(connection);
        when(connection.getShell()).thenReturn(shell);
        when(connection.isClosed()).thenReturn(true);
        when(shell.isConnected()).thenReturn(false);
        final JSchSshTerminal terminal = new JSchSshTerminal(pool);
        final List<CommandExpectationGroup> commandExpectationGroups = new ArrayList<CommandExpectationGroup>();
        final CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommand("command");
        commandExpectationGroups.add(commandExpectationGroup);
        terminal.executeCommandGroups(commandExpectationGroups);
        verify(connection, times(0)).close();
        verify(shell, times(0)).disconnect();
    }

}