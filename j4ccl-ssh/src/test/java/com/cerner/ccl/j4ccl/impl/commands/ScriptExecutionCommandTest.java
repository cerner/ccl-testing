package com.cerner.ccl.j4ccl.impl.commands;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.j4ccl.adders.arguments.Argument;
import com.cerner.ccl.j4ccl.impl.commands.util.RecordDataExtractor;
import com.cerner.ccl.j4ccl.impl.commands.util.ScriptExecutionBuilder;
import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.Field;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.Structure;
import com.cerner.ccl.j4ccl.ssh.CclCommandTerminal;
import com.cerner.ccl.j4ccl.ssh.JSchSshTerminal;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * Unit test of {@link ScriptExecutionCommand}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { JSchSshTerminal.class, RecordDataExtractor.class, ScriptExecutionBuilder.class,
        ScriptExecutionCommand.class, PointFactory.class })
public class ScriptExecutionCommandTest {
    private final String scriptName = "script_name";
    @Mock
    private List<Argument> arguments;

    /**
     * Verify that the {@link ScriptExecutionBuilder} is invoked using the given arguuments.
     */
    @SuppressWarnings("unused")
    @Test
    public void testConstructWithArguments() {
        final ScriptExecutionBuilder builder = mock(ScriptExecutionBuilder.class);
        when(builder.withArguments(arguments)).thenReturn(builder);

        mockStatic(ScriptExecutionBuilder.class);
        when(ScriptExecutionBuilder.getBuilder(scriptName)).thenReturn(builder);

        new ScriptExecutionCommand(scriptName, arguments, false);
        verify(builder).withArguments(arguments);
    }

    /**
     * Test the addition of a record structure as part of the script execution.
     */
    @Test
    public void testAddWithReplace() {
        final ScriptExecutionCommand command = new ScriptExecutionCommand(scriptName, arguments, false);
        final Record reply = mock(Record.class);
        final String recordName = "reply";

        command.addWithReplace(recordName, reply);
        assertThat(command.getRecordStructures()).includes(entry(recordName.toUpperCase(), reply));
    }

    /**
     * Test that adding a record structure with a blank name fails.
     */
    @Test
    public void testAddWithReplaceBlankName() {
        final ScriptExecutionCommand command = new ScriptExecutionCommand(scriptName, arguments, false);
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            command.addWithReplace("  ", mock(Record.class));
        });
        assertThat(e.getMessage()).isEqualTo("Record name cannot be blank.");
    }

    /**
     * Test that adding a record structure with a null name fails.
     */
    @Test
    public void testAddWithReplaceNullName() {
        final ScriptExecutionCommand command = new ScriptExecutionCommand(scriptName, arguments, false);
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            command.addWithReplace(null, mock(Record.class));
        });
        assertThat(e.getMessage()).isEqualTo("Record name cannot be null.");
    }

    /**
     * Test that adding a record structure with a null record fails.
     */
    @Test
    public void testAddWithReplaceNullRecord() {
        final ScriptExecutionCommand command = new ScriptExecutionCommand(scriptName, arguments, false);
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            command.addWithReplace("reply", null);
        });
        assertThat(e.getMessage()).isEqualTo("Record cannot be null.");
    }

    /**
     * Test a run with no prerequisite records.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testRun() throws Exception {
        final String buildCommand = "build step";

        final ScriptExecutionBuilder builder = mock(ScriptExecutionBuilder.class);
        when(builder.build()).thenReturn(Collections.singleton(buildCommand));

        final JSchSshTerminal sshTerminal = mock(JSchSshTerminal.class);
        whenNew(JSchSshTerminal.class).withNoArguments().thenReturn(sshTerminal);

        final CclCommandTerminal cclTerminal = mock(CclCommandTerminal.class);

        final ScriptExecutionCommand command = new ScriptExecutionCommand(builder, false);
        command.run(cclTerminal);

        final ArgumentCaptor<List> commandCaptor = ArgumentCaptor.forClass(List.class);
        verify(cclTerminal).executeCommands(eq(sshTerminal), commandCaptor.capture(), eq(false));
        assertThat(commandCaptor.getValue()).containsOnly(buildCommand);
    }

    /**
     * Test the execution of a script with a prerequisite record structure.
     *
     * @throws Exception
     *             If an error occurs during the test run.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testRunWithRecord() throws Exception {
        final EtmPoint point = mock(EtmPoint.class);
        mockStatic(PointFactory.class);
        when(PointFactory.getPoint(ScriptExecutionCommand.class, "run")).thenReturn(point);

        final String extractCommand = "do_extract go";
        final String buildCommand = "build step go";
        final String recordName = "mock_request";
        final String recordDeclaration = "declaration";
        final String fieldName = "field";

        final Field field = mock(Field.class);
        when(field.getName()).thenReturn(fieldName);
        when(field.getType()).thenReturn(DataType.I2);

        final Structure structure = mock(Structure.class);
        when(structure.getFields()).thenReturn(Arrays.asList(field));

        final Record request = mock(Record.class);
        when(request.getName()).thenReturn(recordName);
        when(request.getDeclaration()).thenReturn(recordDeclaration);
        when(request.getStructure()).thenReturn(structure);
        when(request.getI2(fieldName)).thenReturn(Short.valueOf((short) 12));

        final RecordDataExtractor mockExtractor = mock(RecordDataExtractor.class);
        when(mockExtractor.getExtractionCommands()).thenReturn(Arrays.asList(extractCommand));

        whenNew(RecordDataExtractor.class).withArguments(request).thenReturn(mockExtractor);

        final ScriptExecutionBuilder builder = mock(ScriptExecutionBuilder.class);
        when(builder.build()).thenReturn(Collections.singleton(buildCommand));

        final JSchSshTerminal sshTerminal = mock(JSchSshTerminal.class);
        whenNew(JSchSshTerminal.class).withNoArguments().thenReturn(sshTerminal);

        final CclCommandTerminal cclTerminal = mock(CclCommandTerminal.class);

        final ScriptExecutionCommand command = new ScriptExecutionCommand(builder, false);
        command.addWithReplace("request", request);
        command.run(cclTerminal);

        final List<String> expected = new ArrayList<String>(5);
        expected.add(String.format("free record %s go", request.getName()));
        expected.add(recordDeclaration);
        expected.add("go");
        expected.add("set mock_request->field = 12 go");
        expected.add(buildCommand);
        expected.add(extractCommand);

        final ArgumentCaptor<List> commandCaptor = ArgumentCaptor.forClass(List.class);
        verify(cclTerminal).executeCommands(eq(sshTerminal), commandCaptor.capture(), eq(false));
        assertThat(commandCaptor.getValue()).isEqualTo(expected);
        // Verify that the record structure data was "extracted"
        verify(mockExtractor).extractRecordData();
        verify(builder).addWithReplace("request", request);
        verify(point).collect();
    }
}