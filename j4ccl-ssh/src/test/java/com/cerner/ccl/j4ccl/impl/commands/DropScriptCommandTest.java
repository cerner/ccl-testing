package com.cerner.ccl.j4ccl.impl.commands;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.exception.CclCommandException;
import com.cerner.ccl.j4ccl.internal.AbstractUnitTest;
import com.cerner.ccl.j4ccl.ssh.CclCommandTerminal;
import com.cerner.ccl.j4ccl.ssh.JSchSshTerminal;
import com.cerner.ccl.j4ccl.ssh.exception.SshException;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * Unit test for {@link DropScriptCommand}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { CclCommandException.class, DropScriptCommand.class, JSchSshTerminal.class,
        PointFactory.class })
public class DropScriptCommandTest extends AbstractUnitTest {
    private final String scriptName = "script.name";
    private DropScriptCommand command;

    /**
     * Set up the command for each test.
     */
    @Before
    public void setUp() {
        command = new DropScriptCommand(scriptName);
    }

    /**
     * Construction with a blank script name should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructBlankScriptName() {
        expect(IllegalArgumentException.class);
        expect("Invalid script name; given script name was []");
        new DropScriptCommand("");
    }

    /**
     * Construction with a {@code null} script name should fail.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructNullScriptName() {
        expect(IllegalArgumentException.class);
        expect("Invalid script name; given script name was [<null>]");
        new DropScriptCommand(null);
    }

    /**
     * Test the execution of the drop command.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testRun() throws Exception {
        final EtmPoint point = mock(EtmPoint.class);
        mockStatic(PointFactory.class);
        when(PointFactory.getPoint(DropScriptCommand.class, "run")).thenReturn(point);

        final JSchSshTerminal sshTerminal = mock(JSchSshTerminal.class);
        whenNew(JSchSshTerminal.class).withNoArguments().thenReturn(sshTerminal);

        final CclCommandTerminal cclTerminal = mock(CclCommandTerminal.class);

        command.run(cclTerminal);

        verify(cclTerminal).executeCommands(sshTerminal,
                Collections.singletonList("drop program " + scriptName + " go"), false);
        verify(point).collect();
    }

    /**
     * If the SSH terminal throws an {@link SshException}, then it should be rethrown as an {@link CclCommandException}.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testRunSshException() throws Exception {
        final CclCommandTerminal cclTerminal = mock(CclCommandTerminal.class);
        final JSchSshTerminal sshTerminal = mock(JSchSshTerminal.class);
        whenNew(JSchSshTerminal.class).withAnyArguments().thenReturn(sshTerminal);

        doThrow(new SshException("uh-oh!")).when(cclTerminal).executeCommands(ArgumentMatchers.<JSchSshTerminal> any(),
                ArgumentMatchers.<List<String>> any(), anyBoolean());

        expect(CclCommandException.class);
        expect("Dropping script " + scriptName + " failed.");
        command.run(cclTerminal);
    }
}