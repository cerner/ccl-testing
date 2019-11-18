package com.cerner.ccl.j4ccl.impl.commands.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.cerner.ccl.j4ccl.adders.arguments.Argument;
import com.cerner.ccl.j4ccl.record.Record;

/**
 * Unit test for {@link ScriptExecutionBuilder}.
 *
 * @author Joshua Hyde
 *
 */

public class ScriptExecutionBuilderTest {
    private static final String SCRIPT_NAME = "script_name";
    private final ScriptExecutionBuilder builder = ScriptExecutionBuilder.getBuilder(SCRIPT_NAME);

    /**
     * Test the addition of a record structure reference.
     */
    @Test
    public void testAddWithReplace() {
        final Record record = mock(Record.class);
        final String recordName = "mock_request";

        builder.addWithReplace(recordName, record);

        assertThat(builder.getRecordStructures()).includes(entry(recordName.toUpperCase(), record));
    }

    /**
     * Test the addition of a record structure reference with a blank record name.
     */
    @Test
    public void testAddWithReplaceBlankName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            builder.addWithReplace("  ", mock(Record.class));
        });
        assertThat(e.getMessage()).isEqualTo("Record name cannot be blank.");
    }

    /**
     * Test the addition of a record structure reference with a null record name.
     */
    @Test
    public void testAddWithReplaceNullName() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            builder.addWithReplace(null, mock(Record.class));
        });
        assertThat(e.getMessage()).isEqualTo("Record name cannot be null.");
    }

    /**
     * Test the addition of a record structure reference with a null record reference.
     */
    @Test
    public void testAddWithReplaceNullRecord() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            builder.addWithReplace("request", null);
        });
        assertThat(e.getMessage()).isEqualTo("Record cannot be null.");
    }

    /**
     * Test that the execution commands are properly constructed.
     */
    @Test
    public void testBuild() {
        final Record request = mock(Record.class);
        when(request.getName()).thenReturn("mock_request");

        final Record reply = mock(Record.class);
        when(reply.getName()).thenReturn("mock_reply");

        builder.addWithReplace("request", request);
        builder.addWithReplace("reply", reply);

        final String executionCommand = String.format("execute %s", SCRIPT_NAME);

        /*
         * The ordering of the collection of record structures backing the builder can return the structures in a
         * different order, depending on the behavior of the JDK or JRE. To account for this, test to see if the request
         * or reply was returned first and then compare a sublist of the execution queue to the "expected" set.
         */
        final List<String> replyFirst = new ArrayList<String>(2);
        replyFirst.add("WITH REPLACE(\"REPLY\", \"MOCK_REPLY\")");
        replyFirst.add(", REPLACE(\"REQUEST\", \"MOCK_REQUEST\")");

        final List<String> requestFirst = new ArrayList<String>(2);
        requestFirst.add("WITH REPLACE(\"REQUEST\", \"MOCK_REQUEST\")");
        requestFirst.add(", REPLACE(\"REPLY\", \"MOCK_REPLY\")");

        final List<String> commands = new ArrayList<String>(builder.build());
        assertThat(commands).hasSize(4);
        assertThat(commands.get(0)).isEqualTo(executionCommand);
        assertThat(commands.get(3)).isEqualTo("go;script_name");

        // Validate the REPLACE commands
        final List<String> replaceCommands = commands.subList(1, 3);
        if (replaceCommands.get(0).equals(requestFirst.get(0))) {
            assertThat(replaceCommands).isEqualTo(requestFirst);
        } else if (replaceCommands.get(0).equals(replyFirst.get(0))) {
            assertThat(replaceCommands).isEqualTo(replyFirst);
        } else {
            fail("First line of replace commands does not match any known value: " + replaceCommands.get(0));
        }
    }

    /**
     * Test the construction of an execution with arguments.
     */
    @Test
    public void testBuildWithArguments() {
        final String argument1Value = "arg1.value";
        final Argument argument1 = mock(Argument.class);
        when(argument1.getCommandLineValue()).thenReturn(argument1Value);

        final String argument2Value = "arg2.value";
        final Argument argument2 = mock(Argument.class);
        when(argument2.getCommandLineValue()).thenReturn(argument2Value);

        final List<String> built = new ArrayList<String>(
                builder.withArguments(Arrays.asList(argument1, argument2)).build());
        assertThat(built).hasSize(4);
        assertThat(built.get(0)).isEqualTo("execute " + SCRIPT_NAME);
        assertThat(built.get(1)).isEqualTo(argument1Value);
        assertThat(built.get(2)).isEqualTo("," + argument2Value);
        assertThat(built.get(3)).isEqualTo("go;script_name");
    }

    /**
     * Test the construction of an execution with both command-line arguments and record replacements.
     */
    @Test
    public void testBuildWithArgumentsAndWithReplace() {
        final String argument1Value = "arg1.value";
        final Argument argument1 = mock(Argument.class);
        when(argument1.getCommandLineValue()).thenReturn(argument1Value);

        final String argument2Value = "arg2.value";
        final Argument argument2 = mock(Argument.class);
        when(argument2.getCommandLineValue()).thenReturn(argument2Value);

        final Record request = mock(Record.class);
        when(request.getName()).thenReturn("mock_request");

        final Record reply = mock(Record.class);
        when(reply.getName()).thenReturn("mock_reply");

        builder.addWithReplace("request", request);
        builder.addWithReplace("reply", reply);
        builder.withArguments(Arrays.asList(argument1, argument2));

        final String executionCommand = String.format("execute %s", SCRIPT_NAME);

        /*
         * The ordering of the collection of record structures backing the builder can return the structures in a
         * different order, depending on the behavior of the JDK or JRE. To account for this, test to see if the request
         * or reply was returned first and then compare a sublist of the execution queue to the "expected" set.
         */
        final List<String> replyFirst = new ArrayList<String>(2);
        replyFirst.add("WITH REPLACE(\"REPLY\", \"MOCK_REPLY\")");
        replyFirst.add(", REPLACE(\"REQUEST\", \"MOCK_REQUEST\")");

        final List<String> requestFirst = new ArrayList<String>(2);
        requestFirst.add("WITH REPLACE(\"REQUEST\", \"MOCK_REQUEST\")");
        requestFirst.add(", REPLACE(\"REPLY\", \"MOCK_REPLY\")");

        final List<String> commands = new ArrayList<String>(builder.build());
        assertThat(commands).hasSize(6);
        assertThat(commands.get(0)).isEqualTo(executionCommand);
        assertThat(commands.get(1)).isEqualTo(argument1Value);
        assertThat(commands.get(2)).isEqualTo("," + argument2Value);
        assertThat(commands.get(5)).isEqualTo("go;script_name");

        // Validate the REPLACE commands
        final List<String> replaceCommands = commands.subList(3, 5);
        if (replaceCommands.get(0).equals(requestFirst.get(0))) {
            assertThat(replaceCommands).isEqualTo(requestFirst);
        } else if (replaceCommands.get(0).equals(replyFirst.get(0))) {
            assertThat(replaceCommands).isEqualTo(replyFirst);
        } else {
            fail("First line of replace commands does not match any known value: " + replaceCommands.get(0));
        }
    }

    /**
     * Test that a builder can be fetched for a script.
     */
    @Test
    public void testGetBuilder() {
        final ScriptExecutionBuilder builder = ScriptExecutionBuilder.getBuilder(SCRIPT_NAME);
        assertThat(builder.getScriptName()).isEqualTo(SCRIPT_NAME);
    }

    /**
     * Test that using a null script name fails.
     */
    @Test
    public void testGetBuilderNullScriptName() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            ScriptExecutionBuilder.getBuilder(null);
        });
        assertThat(e.getMessage()).isEqualTo("Script name cannot be null.");
    }

    /**
     * Test that using a blank script name fails.
     */
    @Test
    public void testGetBuilderBlankScriptName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            ScriptExecutionBuilder.getBuilder(" ");
        });
        assertThat(e.getMessage()).isEqualTo("Script name cannot be blank.");
    }

    /**
     * Passing in a {@code null} {@link List} for arguments should fail.
     */
    @Test
    public void testWithArgumentsNullList() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> {
            builder.withArguments(null);
        });
        assertThat(e.getMessage()).isEqualTo("Arguments cannot be null.");
    }
}