package com.cerner.ccl.j4ccl.ssh;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.cerner.ccl.j4ccl.TerminalProperties;
import com.cerner.ccl.j4ccl.enums.OutputType;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePrincipal;
import com.cerner.ccl.j4ccl.impl.jaas.JaasUtils;
import com.cerner.ccl.j4ccl.impl.jaas.MillenniumDomainPasswordCredential;
import com.cerner.ccl.j4ccl.impl.jaas.MillenniumDomainPrincipal;
import com.cerner.ccl.j4ccl.impl.util.CclOutputStreamProxy;
import com.cerner.ccl.j4ccl.impl.util.OutputStreamConfiguration;
import com.cerner.ccl.j4ccl.impl.util.OutputStreamProxy;
import com.cerner.ccl.j4ccl.ssh.exception.SshException;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A forwarding object that wraps {@link JSchSshTerminal} to facilitate accessing CCL, authenticating, executing
 * commands, and logging.
 *
 * @author Joshua Hyde
 *
 */

public class CclCommandTerminal {

    /**
     * The keyword used to trigger the CCL output collection.
     */
    static final String OUTPUT_START = "##CCL##OUTPUT##BEGIN##";
    /**
     * The keyword used to end the CCL output collection.
     */
    static final String OUTPUT_END = "##CCL##OUTPUT##END##";
    /**
     * The CCL command issued as part of the operation to trigger the collection of CCL output.
     */
    static final String CCL_OUTPUT_START_COMMAND = "reset ;" + OUTPUT_START;
    /**
     * The CCL command issued as part of the operation to end the collection of CCL output.
     */
    static final String CCL_OUTPUT_END_COMMAND = "reset ;" + OUTPUT_END;

    private static final String CCL_WIDTH_COMMAND = "set width 132 go";

    /**
     * A regex for the header row in CCL's results viewer.
     */
    public static final String CCL_VIEWER_PATTERN = "EXIT  VIEW  FIND  PRINT  HELP  SCROLL  BREAK  DIRECTION  WIDTH  MARGIN";
    /**
     * A regex for the string that is output when CCL aborts.
     */
    public static final String CCL_ABORT_PATTERN = "(?:Segmentation fault|Aborted) \\(core dumped\\)";
    private final OutputStreamConfiguration streamConfiguration;
    private final TerminalProperties terminalProperties;
    private final String environmentName;

    /**
     * Create a CCL command proxy out of the given configuration.
     *
     * @param terminalProperties
     *            The TerminalProperties to apply to this terminal.
     * @param streamConfiguration
     *            The configured location to which output should be written.
     */
    public CclCommandTerminal(final TerminalProperties terminalProperties,
            final OutputStreamConfiguration streamConfiguration) {
        if (terminalProperties == null) {
            throw new IllegalArgumentException("terminalProperties cannot be null");
        }
        final BackendNodePrincipal principal = JaasUtils.getPrincipal(BackendNodePrincipal.class);
        this.environmentName = principal.getEnvironmentName();
        this.streamConfiguration = streamConfiguration;
        this.terminalProperties = terminalProperties;
    }

    /**
     * Launches a CCL session, authenticated if authentication is specified, executes a provided list of CCL commands
     * and exits the CCL session.
     *
     * @param terminal
     *            A {@link JSchSshTerminal} to be used to interact with the remote server.
     * @param commands
     *            A {@link Collection} of {@code String} objects representing the commands to be run.
     * @param authenticate
     *            A boolean flag indicating whether or not to authenticate the CCL session
     * @throws SshException
     *             If any errors occur during sessionCommandsthe execution of the commands.
     */
    public void executeCommands(final JSchSshTerminal terminal, final List<String> commands, final boolean authenticate)
            throws SshException {
        final EtmPoint point = PointFactory.getPoint(getClass(), "executeCommands");
        try {
            final boolean hasStreamConfiguration = streamConfiguration != null;
            final boolean hasCclOutputStream = hasStreamConfiguration
                    && OutputType.CCL_SESSION.equals(streamConfiguration.getOutputType());
            if (hasStreamConfiguration) {
                terminal.setOutputStream(hasCclOutputStream
                        ? new CclOutputStreamProxy(streamConfiguration.getOutputStream(), OUTPUT_START, OUTPUT_END)
                        : new OutputStreamProxy(streamConfiguration.getOutputStream()));
            }
            terminal.setExpectationTimeout(terminalProperties.getExpectationTimeout());

            final List<String> nonMergedCommands = new ArrayList<String>();
            for (String command : commands) {
                final String splitCommands[] = command.split("\\n");
                for (int index = 0; index < splitCommands.length; index++) {
                    nonMergedCommands.add(splitCommands[index]);
                }
            }

            final List<CommandExpectationGroup> commandExpectationGroups = new ArrayList<CommandExpectationGroup>();
            commandExpectationGroups.add(getSessionStartupGroup());
            commandExpectationGroups.add(getCclLaunchGroup());
            if (authenticate) {
                commandExpectationGroups.addAll(getAuthenticationGroups());
            }
            commandExpectationGroups.add(getFinalizeLoginGroup());
            commandExpectationGroups.add(getWaitForCcclReadyGroup());
            commandExpectationGroups.add(getCclSessionGroup(nonMergedCommands, hasCclOutputStream));
            commandExpectationGroups.add(getCclExitGroup());

            terminal.executeCommandGroups(commandExpectationGroups);
        } finally {
            point.collect();
        }
    }

    private CommandExpectationGroup getSessionStartupGroup() {
        final CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        if (!terminalProperties.getSkipEnvset()) {
            commandExpectationGroup.addCommand("envset " + environmentName);
            commandExpectationGroup.addExpectation(terminalProperties.getOsPromptPattern());
        }
        return commandExpectationGroup;
    }

    private CommandExpectationGroup getCclLaunchGroup() {
        final CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        if (terminalProperties.getSpecifyDebugCcl()) {
            commandExpectationGroup.addCommand("$cer_exe/cclora_dbg");
        } else {
            commandExpectationGroup.addCommand("ccl");
        }
        commandExpectationGroup.addExpectation(terminalProperties.getCclLoginPromptPattern());
        return commandExpectationGroup;
    }

    private List<CommandExpectationGroup> getAuthenticationGroups() {
        final List<CommandExpectationGroup> loginCommandExpectationGroups = new ArrayList<CommandExpectationGroup>();
        if (JaasUtils.hasPrincipal(MillenniumDomainPrincipal.class)) {
            final MillenniumDomainPrincipal domainPrincipal = JaasUtils.getPrincipal(MillenniumDomainPrincipal.class);
            final MillenniumDomainPasswordCredential domainPasswordCredential = JaasUtils
                    .getPrivateCredential(MillenniumDomainPasswordCredential.class);
            final String userName = domainPrincipal.getUsername();
            final String domainName = domainPrincipal.getDomainName();
            final String password = domainPasswordCredential.getPassword();

            final CommandExpectationGroup commandExpectationGroupCCLUserName = new CommandExpectationGroup();
            final CommandExpectationGroup commandExpectationGroupCCLDomain = new CommandExpectationGroup();
            final MaskedCommandExpectationGroup commandExpectationGroupCCLPassword = new MaskedCommandExpectationGroup();

            commandExpectationGroupCCLUserName.addCommand(userName);
            commandExpectationGroupCCLUserName.addExpectation(userName);

            commandExpectationGroupCCLDomain.addCommand(domainName);
            commandExpectationGroupCCLDomain.addExpectation(domainName);

            commandExpectationGroupCCLPassword.addCommand(password);
            commandExpectationGroupCCLPassword.addExpectation(terminalProperties.getCclLoginSuccessPromptPattern());
            commandExpectationGroupCCLPassword.addExpectations(terminalProperties.getCclLoginFailurePromptPatterns());

            loginCommandExpectationGroups.add(commandExpectationGroupCCLUserName);
            loginCommandExpectationGroups.add(commandExpectationGroupCCLDomain);
            loginCommandExpectationGroups.add(commandExpectationGroupCCLPassword);
        }
        return loginCommandExpectationGroups;
    }

    private CommandExpectationGroup getFinalizeLoginGroup() {
        final CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        // This will confirm or dismiss the login prompt regardless of whether
        // domain credentials are entered or whether valid credentials are entered.
        commandExpectationGroup.addCommand("");
        commandExpectationGroup.addCommand("");
        return commandExpectationGroup;
    }

    private CommandExpectationGroup getWaitForCcclReadyGroup() {
        final CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        // This will ensure commands are not sent until the CCL prompt appears.
        commandExpectationGroup.addCommand("reset");
        commandExpectationGroup.addExpectation(terminalProperties.getCclPromptPattern());
        return commandExpectationGroup;
    }

    private CommandExpectationGroup getCclSessionGroup(final List<String> commands, final boolean hasCclOutputStream) {
        final CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommand(CCL_WIDTH_COMMAND);
        if (hasCclOutputStream) {
            commandExpectationGroup.addCommand(CCL_OUTPUT_START_COMMAND);
        }
        commandExpectationGroup.addCommands(commands);
        if (hasCclOutputStream) {
            commandExpectationGroup.addCommand(CCL_OUTPUT_END_COMMAND);
        }
        commandExpectationGroup.addExpectation(terminalProperties.getCclPromptPattern());
        commandExpectationGroup.addExpectation(CCL_VIEWER_PATTERN);
        commandExpectationGroup.addExpectation(CCL_ABORT_PATTERN);
        return commandExpectationGroup;
    }

    private CommandExpectationGroup getCclExitGroup() {
        final CommandExpectationGroup commandExpectationGroup = new CommandExpectationGroup();
        commandExpectationGroup.addCommand("exit");
        commandExpectationGroup.addExpectation(terminalProperties.getOsPromptPattern());
        return commandExpectationGroup;
    }

    /**
     * Retrieves the TerminalProperties object currently in effect for this CclCommandTerminal.
     *
     * @return The TerminalProperties object currently in effect for this CclCommandTerminal.
     */
    public TerminalProperties getTerminalProperties() {
        return terminalProperties;
    }
}