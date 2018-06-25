package com.cerner.ccl.j4ccl.impl.data;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cerner.ccl.j4ccl.TerminalProperties;
import com.cerner.ccl.j4ccl.impl.jaas.BackendNodePrincipal;
import com.cerner.ccl.j4ccl.impl.jaas.JaasUtils;
import com.cerner.ccl.j4ccl.ssh.CommandExpectationGroup;
import com.cerner.ccl.j4ccl.ssh.JSchSshTerminal;
import com.cerner.ccl.j4ccl.ssh.TerminalResponse;
import com.cerner.ccl.j4ccl.ssh.exception.SshException;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A data object that describes an environment on a Millennium server.
 *
 * @author Joshua Hyde
 * @author Fred Eckertson
 */

public class Environment {
    private static final Map<String, Environment> CACHE = new HashMap<String, Environment>();
    private final Logger logger = LoggerFactory.getLogger(Environment.class);

    /**
     * Get a data object representing an environment.
     *
     * @return A {@link Environment} data object representing the desired environment.
     * @throws IllegalArgumentException
     *             If the given environment name is blank.
     * @throws IllegalStateException
     *             If any of the server-side logicals (such as CCLUSERDIR) cannot be determined.
     * @throws NullPointerException
     *             If the given environment name is {@code null}.
     */
    public static Environment getEnvironment() {
        final BackendNodePrincipal principal = JaasUtils.getPrincipal(BackendNodePrincipal.class);

        final String environmentName = principal.getEnvironmentName();
        if (environmentName == null)
            throw new NullPointerException("Environment name cannot be null.");

        if (StringUtils.isBlank(environmentName))
            throw new IllegalArgumentException("Environment name cannot be blank.");

        final String normalizedName = environmentName.toLowerCase(Locale.getDefault());
        if (CACHE.containsKey(normalizedName))
            return CACHE.get(normalizedName);

        final Environment env = new Environment(environmentName);
        CACHE.put(normalizedName, env);
        return env;
    }

    private final String environmentName;
    private final String cerInstall;
    private final String cerProc;
    private final String cerTemp;
    private final String cclSource;
    private final String cclUserDir;

    /**
     * Create a data object. <br>
     * This constructor is intentionally left package-private to expose it for testing while obfuscating it from
     * consumers.
     *
     * @param environmentName
     *            The name of the environment.
     */
    Environment(final String environmentName) {
        final EtmPoint point = PointFactory.getPoint(getClass(), "<init>");
        try {
            this.environmentName = environmentName;

            String envData = getEnvData();
            try (StringReader reader = new StringReader(envData)) {
                PropertyResourceBundle bundle = new PropertyResourceBundle(reader);
                cclUserDir = bundle.getString("CCLUSERDIR");
                cclSource = bundle.getString("CCLSOURCE");
                cerTemp = bundle.getString("cer_temp");
                cerProc = bundle.getString("cer_proc");
                cerInstall = bundle.getString("cer_install");
                logger.debug(String.format("cclUserDir: %s; cclSource: %s; cerTemp: %s; cerProc: %s; cerInstall: %s%n",
                        cclUserDir, cclSource, cerTemp, cerProc, cerInstall));
            } catch (final IOException e) {
                logger.error(new StringBuilder().append("\n\r").append("---------envData start---------").append("\n\r")
                        .append(envData).append("\n\r").append("---------envData end---------").toString());
                throw new RuntimeException("Failed to parse environmental parameter data.", e);
            } catch (final MissingResourceException e) {
                logger.error(new StringBuilder().append("\n\r").append("---------envData start---------").append("\n\r")
                        .append(envData).append("\n\r").append("---------envData end---------").toString());
                throw new IllegalStateException(
                        "A server-side environment logical could not be determined. Verify that the given environment name exists: "
                                + environmentName,
                        e);
            }

        } finally {
            point.collect();
        }
    }

    /**
     * Get the location of cclsource.
     *
     * @return The location of cclsource.
     */
    public String getCclSource() {
        return cclSource;
    }

    /**
     * Get the location of ccluserdir.
     *
     * @return The location of ccluserdir.
     */
    public String getCclUserDir() {
        return cclUserDir;
    }

    /**
     * Get the location of cer_install.
     *
     * @return The location of cer_install.
     */
    public String getCerInstall() {
        return cerInstall;
    }

    /**
     * Get the location of cer_proc.
     *
     * @return The location of cer_proc.
     */
    public String getCerProc() {
        return cerProc;
    }

    /**
     * Get the location of cer_temp.
     *
     * @return The location of cer_temp.
     */
    public String getCerTemp() {
        return cerTemp;
    }

    /**
     * Get the name of the environment.
     *
     * @return The name of the environment.
     */
    public String getEnvironmentName() {
        return environmentName;
    }

    private String getEnvData() {
        final EtmPoint point = PointFactory.getPoint(getClass(), "getEnvData");
        try {
            final TerminalProperties tpDefault = TerminalProperties.getGlobalTerminalProperties();
            String osPromptPattern = tpDefault.getOsPromptPattern();
            final JSchSshTerminal terminal = new JSchSshTerminal();
            terminal.setExpectationTimeout(tpDefault.getExpectationTimeout());
            TerminalResponse terminalRepsonse = null;

            List<CommandExpectationGroup> commandExpectationGroups = new ArrayList<CommandExpectationGroup>();
            CommandExpectationGroup commandExpectationGroup = null;

            if (!TerminalProperties.getGlobalTerminalProperties().getSkipEnvset()) {
                commandExpectationGroup = new CommandExpectationGroup();
                commandExpectationGroup.addCommand("envset " + environmentName);
                commandExpectationGroup.addExpectation("Environment '" + environmentName + "' set.");
                commandExpectationGroups.add(commandExpectationGroup);
            }

            commandExpectationGroup = new CommandExpectationGroup();
            commandExpectationGroup.addCommand("echo 'ENV CAPTURE START'");
            commandExpectationGroup.addExpectation("[^']ENV CAPTURE START[^']");
            commandExpectationGroups.add(commandExpectationGroup);

            commandExpectationGroup = new CommandExpectationGroup();
            commandExpectationGroup.addCommand("echo '||cer_proc='$cer_proc'||'");
            commandExpectationGroup.addExpectation("[^']||cer_proc=[^\\|]*||[^']");
            commandExpectationGroups.add(commandExpectationGroup);

            commandExpectationGroup = new CommandExpectationGroup();
            commandExpectationGroup.addCommand("echo '||cer_temp='$cer_temp'||'");
            commandExpectationGroup.addExpectation("[^']||cer_temp=[^\\|]*||[^']");
            commandExpectationGroups.add(commandExpectationGroup);

            commandExpectationGroup = new CommandExpectationGroup();
            commandExpectationGroup.addCommand("echo '||cer_install='$cer_install'||'");
            commandExpectationGroup.addExpectation("[^']||cer_install=[^\\|]*||[^']");
            commandExpectationGroups.add(commandExpectationGroup);

            commandExpectationGroup = new CommandExpectationGroup();
            commandExpectationGroup.addCommand("echo '||CCLSOURCE='$CCLSOURCE'||'");
            commandExpectationGroup.addExpectation("[^']||CCLSOURCE=[^\\|]*||[^']");
            commandExpectationGroups.add(commandExpectationGroup);

            commandExpectationGroup = new CommandExpectationGroup();
            commandExpectationGroup.addCommand("echo '||CCLUSERDIR='$CCLUSERDIR'||'");
            commandExpectationGroup.addExpectation("[^']||CCLUSERDIR=[^\\|]*||[^']");
            commandExpectationGroups.add(commandExpectationGroup);

            commandExpectationGroup = new CommandExpectationGroup();
            commandExpectationGroup.addCommand("echo 'ENV CAPTURE END'");
            commandExpectationGroup.addExpectation("[^']ENV CAPTURE END[^']");
            commandExpectationGroups.add(commandExpectationGroup);

            terminalRepsonse = terminal.executeCommandGroups(commandExpectationGroups);

            int startPos = 0;
            String rawOutput = terminalRepsonse.getOutput();
            rawOutput = rawOutput.replaceAll(osPromptPattern + "\\s*", "");

            Matcher matcherStart = Pattern.compile("[^']ENV CAPTURE START[^']").matcher(rawOutput);
            if (matcherStart.find()) {
                startPos = matcherStart.end();
            }
            rawOutput = rawOutput.substring(startPos);
            rawOutput = rawOutput.replaceAll(osPromptPattern, "");

            StringBuilder finalOutput = new StringBuilder();
            Matcher matcherEnv = Pattern
                    .compile("(\\|\\|(?>cer_proc|cer_temp|cer_install|CCLSOURCE|CCLUSERDIR)=[^'|$]*\\|\\|)",
                            Pattern.MULTILINE)
                    .matcher(rawOutput);
            int logicalCount = 0;
            while (matcherEnv.find()) {
                logicalCount++;
                finalOutput.append("\n").append(matcherEnv.group(0));
            }
            if (logicalCount < 5) {
                logger.error("Some environment logical retrieval commamds failed.");
                logger.error(new StringBuilder().append("\n\r").append("---------raw output start---------")
                        .append("\n\r").append(terminalRepsonse.getOutput()).append("\n\r")
                        .append("---------raw output end---------").toString());
            }
            return logicalCount > 0 ? finalOutput.substring(1).replaceAll("\\|\\|", "") : "";
        } catch (SshException e) {
            e.printStackTrace();
            return "";
        } finally {
            point.collect();
        }
    }
}
