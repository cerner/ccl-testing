package com.cerner.ccl.j4ccl.impl.commands;

import java.util.Collections;

import org.apache.commons.lang.StringUtils;

import com.cerner.ccl.j4ccl.exception.CclCommandException;
import com.cerner.ccl.j4ccl.ssh.CclCommandTerminal;
import com.cerner.ccl.j4ccl.ssh.JSchSshTerminal;
import com.cerner.ccl.j4ccl.ssh.exception.SshException;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * An {@link AbstractCclCommand} object that drops a specified script.
 *
 * @author Joshua Hyde
 *
 */

public class DropScriptCommand extends AbstractCclCommand {
    private final String scriptName;

    /**
     * Create a command to drop a script.
     *
     * @param scriptName
     *            The name of the script to be dropped.
     */
    public DropScriptCommand(final String scriptName) {
        super();

        if (StringUtils.isBlank(scriptName))
            throw new IllegalArgumentException("Invalid script name; given script name was ["
                    + (scriptName == null ? "<null>" : scriptName) + "]");

        this.scriptName = scriptName;
    }

    @Override
    public void run(final CclCommandTerminal terminal) {
        final EtmPoint point = PointFactory.getPoint(getClass(), "run");
        try {
            try {
                terminal.executeCommands(new JSchSshTerminal(),
                        Collections.singletonList("drop program " + scriptName + " go"), false);
            } catch (final SshException e) {
                throw new CclCommandException("Dropping script " + scriptName + " failed.", e);
            }
        } finally {
            point.collect();
        }
    }

}
