package com.cerner.ccl.parser.text.documentation.parser;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;

import com.cerner.ccl.parser.TimingsWriter;
import com.cerner.ccl.parser.data.CodeSet;
import com.cerner.ccl.parser.data.EnumeratedValue;
import com.cerner.ccl.parser.text.documentation.Field;

import etm.core.monitor.EtmMonitor;

/**
 * Integration tests for {@link FieldParser}.
 * 
 * @author Joshua Hyde
 * 
 */

public class FieldParserITest {
    private static final EtmMonitor monitor = TimingsWriter.startMonitor();
    private final FieldParser parser = new FieldParser();

    /**
     * Write out the results of all of the test runs.
     * 
     * @throws Exception
     *             If any errors occur during the write-out.
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        if (monitor != null) {
            monitor.stop();

            TimingsWriter.writeTimings(monitor, FieldParserITest.class);
        }
    }

    /**
     * Testing the parsing of field data.
     */
    @Test
    public void testParse() {
        final List<String> source = Arrays.asList("@field", "testField", "This is documentation", "for this field", "@field anotherField @codeset 291", "*/");
        final Field field = parser.parse(0, source);
        assertThat(parser.getEndingIndex()).isEqualTo(source.size() - 2);

        assertThat(field.getName()).isEqualTo("testField");
        assertThat(field.getDescription()).isEqualTo("This is documentation for this field");
        assertThat(field.isOptional()).isFalse();
        assertThat(field.getCodeSets()).isEmpty();
        assertThat(field.getValues()).isEmpty();
    }

    /**
     * Test the parsing of code sets.
     */
    @Test
    public void testParseCodeSets() {
        final List<String> source = Arrays.asList("@field testField Documentation!", "@codeSet 291", "@codeSet 292 with documentation", "*/");
        final Field field = parser.parse(0, source);
        assertThat(parser.getEndingIndex()).isEqualTo(source.size() - 1);

        assertThat(field.getName()).isEqualTo("testField");
        assertThat(field.getDescription()).isEqualTo("Documentation!");
        assertThat(field.isOptional()).isFalse();
        assertThat(field.getValues()).isEmpty();

        final List<CodeSet> codeSets = field.getCodeSets();
        assertThat(codeSets).hasSize(2);

        assertThat(codeSets.get(0).getCodeSet()).isEqualTo(291);
        assertThat(codeSets.get(0).getDescription()).isEmpty();

        assertThat(codeSets.get(1).getCodeSet()).isEqualTo(292);
        assertThat(codeSets.get(1).getDescription()).isEqualTo("with documentation");
    }

    /**
     * Test the parsing of the optionality indicator.
     */
    @Test
    public void testParseIsOptional() {
        final List<String> source = Arrays.asList("@field isOptional This field is optional", "@optional", "@field disregarded", "*/");
        final Field field = parser.parse(0, source);
        assertThat(parser.getEndingIndex()).isEqualTo(source.size() - 2);
    
        assertThat(field.getName()).isEqualTo("isOptional");
        assertThat(field.getDescription()).isEqualTo("This field is optional");
        assertThat(field.isOptional()).isTrue();

    }

    /**
     * Test the parsing of values.
     */
    @Test
    public void testParseValues() {
        final List<String> source = Arrays.asList("@field withValues This has values.", "@value \"\"\"I have quotes!\"\"\" and documentation, to boot \"with quotes\"",
                "@value \"I am just a string\" with documentation", "@value 291 just an integer", "*/");
        final Field field = parser.parse(0, source);
        assertThat(parser.getEndingIndex()).isEqualTo(source.size() - 1);

        assertThat(field.getName()).isEqualTo("withValues");
        assertThat(field.getDescription()).isEqualTo("This has values.");
        assertThat(field.isOptional()).isFalse();
        assertThat(field.getCodeSets()).isEmpty();

        final List<EnumeratedValue> values = field.getValues();
        assertThat(values).hasSize(3);

        assertThat(values.get(0).getValue()).isEqualTo("\"I have quotes!\"");
        assertThat(values.get(0).getDescription()).isEqualTo("and documentation, to boot \"with quotes\"");

        assertThat(values.get(1).getValue()).isEqualTo("I am just a string");
        assertThat(values.get(1).getDescription()).isEqualTo("with documentation");

        assertThat(values.get(2).getValue()).isEqualTo("291");
        assertThat(values.get(2).getDescription()).isEqualTo("just an integer");
    }
}
