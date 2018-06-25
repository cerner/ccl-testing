package com.cerner.ccl.j4ccl.ssh;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.oro.text.regex.MalformedPatternException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cerner.ccl.j4ccl.TerminalProperties;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePasswordCredential;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePrincipal;
import com.cerner.ccl.j4ccl.impl.jaas.JaasUtils;
import com.cerner.ccl.j4ccl.ssh.exception.SshException;
import com.cerner.ccl.j4ccl.ssh.exception.SshExpectationException;
import com.cerner.ccl.j4ccl.ssh.exception.SshTimeoutException;
import com.cerner.ftp.jsch.Connection;
import com.cerner.ftp.jsch.ConnectionPool;
import com.cerner.ftp.jsch.ConnectionPoolFactory;
import com.google.code.jetm.reporting.ext.PointFactory;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

import etm.core.monitor.EtmPoint;
import expect4j.Closure;
import expect4j.Expect4j;
import expect4j.ExpectState;
import expect4j.matches.Match;
import expect4j.matches.RegExpMatch;

/**
 * A wrapper around a {@link JSch} and {@link ChannelShell} object to facilitate submission of commands through an
 * emulated SSH terminal.
 *
 * @author Joshua Hyde
 *
 */

public class JSchSshTerminal {
    // TODO: either make this pattern configurable or never mind making the cclPromptPattern configurable.
    private static final Pattern REFINED_CCL_PROMPT_PATTERN = Pattern
            .compile("^(\\s{2}[1-9]|\\s[1-9]\\d|[1-9]\\d{2,})\\)$", Pattern.MULTILINE);
    private static final String CCL_EXECUTE_PROMPT_PATTERN_STRING = "\\n\\s{2}1\\)$";
    private static final Pattern CCL_EXECUTE_COMMAND_PATTERN = Pattern
            .compile("(?:^go$|.*\\sgo$|^go\\s*;.*$|.*\\sgo\\s*;.*$)", Pattern.MULTILINE);
    private static final Pattern CCL_EXIT_COMMAND_PATTERN = Pattern.compile("(?:^exit$)", Pattern.MULTILINE);
    private static final long EXPECT4J_TIMEOUT_INFINITE = -1;
    private static final long EXPECTATION_TIMEOUT_DEFAULT = 20000;

    private final ConnectionPool pool;

    private long expectationTimeout = EXPECTATION_TIMEOUT_DEFAULT;
    private OutputStream stream;

    private final Logger logger = LoggerFactory.getLogger(JSchSshTerminal.class);

    /**
     * Create a terminal with no timeout.
     */
    public JSchSshTerminal() {
        this(ConnectionPoolFactory.getInstance());
    }

    /**
     * Create a terminal.
     *
     * @param pool
     *            A {@link ConnectionPool} from which connections are to be retrieved.
     */
    public JSchSshTerminal(final ConnectionPool pool) {
        if (pool == null)
            throw new NullPointerException("Connection pool cannot be null.");

        this.pool = pool;
    }

    /**
     * Execute a list of CommandExpectationGroup. Consumers of this method should set the expectationTimeout if the
     * default value needs to be overridden
     *
     * @param commandExpectationGroups
     *            The command expectation groups to execute.
     * @return The TerminalResponse for executing the commands.
     * @throws SshException
     *             The exception thrown if something goes wrong.
     */
    public TerminalResponse executeCommandGroups(final List<CommandExpectationGroup> commandExpectationGroups)
            throws SshException {
        logger.debug("entering JSchSshTerminal.executeCommandGroups");
        for (final CommandExpectationGroup expectationCommandGroup : commandExpectationGroups) {
            logger.debug("commandExpectationGroup: {};", expectationCommandGroup);
        }
        final EtmPoint point = PointFactory.getPoint(getClass(), "executeCommandGroups");
        try {
            if (commandExpectationGroups.isEmpty())
                return new TerminalResponse(0, "");
            Connection connection = null;
            ChannelShell shell = null;

            try {
                connection = getConnection();
                shell = connection.getShell();

                if (stream == null) {
                    String logfileLocation = TerminalProperties.getGlobalTerminalProperties().getLogfileLocation();
                    if (!logfileLocation.isEmpty()) {
                        try {
                            FileUtils.forceMkdirParent(new File(logfileLocation));
                        } catch (IOException e) {
                            stream = new ByteArrayOutputStream();
                            e.printStackTrace();
                        }
                        stream = new FileOutputStream(logfileLocation);
                    } else {
                        stream = new ByteArrayOutputStream();
                    }

                }
                shell.setOutputStream(stream);

                try {
                    shell.connect();
                } catch (final JSchException e) {
                    throw new SshException("Failed to connect SSH shell.", e);
                }

                final Expect4j expect = new Expect4j(shell.getInputStream(), shell.getOutputStream());

                final Map<String, Boolean> closureComm = new HashMap<String, Boolean>();
                final StringBuilder buffer = new StringBuilder();
                final Closure closure = new Closure() {
                    @Override
                    @SuppressWarnings("synthetic-access")
                    public void run(final ExpectState expectState) throws Exception {
                        final String expectStateBuffer = expectState.getBuffer().replace("\\r", "");
                        buffer.append(expectStateBuffer);
                        stream.write(expectStateBuffer.getBytes("UTF-8"));

                        // ********* helpful for debugging issues with expect **********
                        // System.out.println("-------------------------------buffer
                        // start------------------------------");
                        // System.out.println(expectStateBuffer);
                        // System.out.println("-------------------------------buffer
                        // end------------------------------");

                        String match = expectState.getMatch().replaceAll("(?:\r|\n)", "");
                        // cclPromptIsSet && pairIndex==0 --> ccl prompt or ccl execute prompt pattern was matched.
                        if (closureComm.containsKey("cclPromptIsSet") && closureComm.get("cclPromptIsSet")
                                && expectState.getPairIndex() == 0
                                && !REFINED_CCL_PROMPT_PATTERN.matcher(match).matches()) {
                            logger.debug("Premature cclPromptPattern match detected. Waiting for additional output.");
                            expectState.exp_continue();
                        }
                    }
                };

                // don't start sending commands until the terminal is ready for service.
                logger.debug("osPromptPattern = {}",
                        TerminalProperties.getGlobalTerminalProperties().getOsPromptPattern());
                expect.setDefaultTimeout(expectationTimeout);
                expect.expect(new Match[] { new RegExpMatch(
                        TerminalProperties.getGlobalTerminalProperties().getOsPromptPattern(), closure) });

                List<Match> cclExecuteExpectationPatterns = new ArrayList<Match>();
                cclExecuteExpectationPatterns.add(new RegExpMatch(CCL_EXECUTE_PROMPT_PATTERN_STRING, closure));
                cclExecuteExpectationPatterns.add(new RegExpMatch(CclCommandTerminal.CCL_VIEWER_PATTERN, closure));
                cclExecuteExpectationPatterns.add(new RegExpMatch(CclCommandTerminal.CCL_ABORT_PATTERN, closure));

                for (final CommandExpectationGroup commandExpectationGroup : commandExpectationGroups) {
                    closureComm.put("cclPromptIsSet", false);
                    final List<Match> lstPattern = new ArrayList<Match>();
                    for (final String regexElement : commandExpectationGroup.getExpectations()) {
                        try {
                            final Match match = new RegExpMatch(regexElement, closure);
                            lstPattern.add(match);
                            if (regexElement
                                    .equals(TerminalProperties.getGlobalTerminalProperties().getCclPromptPattern())) {
                                closureComm.put("cclPromptIsSet", true);
                            }

                        } catch (final MalformedPatternException e) {
                            e.printStackTrace();
                        }
                    }
                    for (final String command : commandExpectationGroup.getCommands()) {
                        String commandDisplay = commandExpectationGroup.maskCommands() ? "*******" : command;
                        expect.setDefaultTimeout(getCommandSpecificTimeout(command));
                        if (logger.isDebugEnabled()) {
                            logger.debug("sending command (" + commandDisplay + ")");
                        }
                        expect.send(command);
                        expect.send("\r");
                        final int expectVal = command.isEmpty() ? 0 : isCclExecuteCommand(command)
                                ? expect.expect(cclExecuteExpectationPatterns) : expect.expect(lstPattern);
                        if (expectVal < 0) {
                            logger.debug("The expectation result was {} for command {} with patterns {}",
                                    translateExpectVal(expectVal), commandDisplay,
                                    commandExpectationGroup.getExpectations());
                            if (expectVal != Expect4j.RET_EOF) {
                                throw new SshExpectationException("expectj4 send status "
                                        + translateExpectVal(expectVal) + " : command (" + commandDisplay + ")");
                            }
                        } else {
                            final String matchString = expect.getLastState().getMatch(0);
                            if (matchString != null) {
                                if (Pattern.matches(CclCommandTerminal.CCL_VIEWER_PATTERN, matchString)) {
                                    logger.error("select without 'nl:' detected");
                                    throw new SshException("select without 'nl:' detected");
                                } else if (Pattern.matches(CclCommandTerminal.CCL_ABORT_PATTERN, matchString)) {
                                    logger.error("CCL session abort detected");
                                    throw new SshException("CCL session abort detected");
                                }
                            }
                        }
                    }
                }
                return new TerminalResponse(shell.getExitStatus(), buffer.toString());
            } catch (final SshTimeoutException e) {
                throw e;
            } catch (final SshException e) {
                throw e;
            } catch (final Exception e) {
                e.printStackTrace();
                return new TerminalResponse(0, "Error");
            } finally {
                if (shell != null && shell.isConnected()) {
                    shell.disconnect();
                }
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            }
        } finally {
            point.collect();
        }
    }

    /**
     * Determines the amount of time to wait on the expectations for different commands.
     *
     * <br>
     * This is a hack. setDefaultTimeout limits how long we wait for the terminal to respond with something that matches
     * our expectations. Unfortunately we have no idea how long the "execute ... go" might take to execute since it
     * could be kicking off a very lengthy test case. In that case we will wait forever. The only reason to ever have a
     * timeout is to protect against an erroneous expectations that would lead to an infinite wait to receive something
     * that will never arrive. Here we are assuming that (1) if a " go" command is submitted, then the CCL prompt
     * expectation has already proven to be non-erroneous (2) any long running CCL command will end with " go".
     *
     * @param The
     *            command.
     * @return The timeout of the command.
     */
    private long getCommandSpecificTimeout(final String command) {
        // TODO: allow different wait times for the "ccl" command and the "exit" command. ???
        return expectationTimeout == EXPECT4J_TIMEOUT_INFINITE ? EXPECT4J_TIMEOUT_INFINITE
                : isCclExecuteCommand(command) ? EXPECT4J_TIMEOUT_INFINITE
                        : isCclExitCommand(command) ? 2 * expectationTimeout : expectationTimeout;
    }

    private boolean isCclExecuteCommand(final String command) {
        return CCL_EXECUTE_COMMAND_PATTERN.matcher(command).matches();
    }

    private boolean isCclExitCommand(final String command) {
        return CCL_EXIT_COMMAND_PATTERN.matcher(command).matches();
    }

    private String translateExpectVal(final int expectVal) {
        switch (expectVal) {
        case -2:
            return "TIMEOUT (-2)";
        case -3:
            return "EOF (-3)";
        case -4:
            return "TRIED_ONCE (-4)";
        default:
            return "UNKNOWN (" + expectVal + ")";
        }
    }

    /**
     * Set an output stream to which console output should be piped.
     *
     * @param stream
     *            The {@link OutputStream} object to which SSH console data should be piped.
     */
    public void setOutputStream(final OutputStream stream) {
        this.stream = stream;
    }

    /**
     * Sets the expectation timeout for this terminal.
     *
     * @param expectationTimeout
     *            The expecationTimout value to set. The value will not be set if the provided value is not positive. If
     *            the value was not previously set it will be the default value 20000.
     */
    public void setExpectationTimeout(final long expectationTimeout) {
        if (expectationTimeout == -1 || expectationTimeout > 0) {
            this.expectationTimeout = expectationTimeout;
        }
    }

    /**
     * Get a connection to the server.
     *
     * @return A {@link Connection} object.
     */
    private Connection getConnection() {
        final EtmPoint point = PointFactory.getPoint(getClass(), "getConnection");
        try {
            final BackendNodePrincipal principal = JaasUtils.getPrincipal(BackendNodePrincipal.class);
            final BackendNodePasswordCredential passwordCredential = JaasUtils
                    .getPrivateCredential(BackendNodePasswordCredential.class);

            return pool.getConnection(principal.getUsername(), passwordCredential.getPassword(),
                    URI.create(principal.getHostname()));
        } finally {
            point.collect();
        }
    }
}