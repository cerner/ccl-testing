package com.cerner.ccl.parser.text.documentation.parser;

import static org.fest.assertions.Assertions.assertThat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;

import com.cerner.ccl.parser.AbstractFileReaderITest;
import com.cerner.ccl.parser.TimingsWriter;
import com.cerner.ccl.parser.data.CodeSet;
import com.cerner.ccl.parser.data.EnumeratedValue;
import com.cerner.ccl.parser.data.record.InterfaceStructureType;
import com.cerner.ccl.parser.text.data.util.DocumentationParserSupport;
import com.cerner.ccl.parser.text.documentation.AbstractDocumentation;
import com.cerner.ccl.parser.text.documentation.Field;
import com.cerner.ccl.parser.text.documentation.Parameter;
import com.cerner.ccl.parser.text.documentation.RecordStructureDocumentation;
import com.cerner.ccl.parser.text.documentation.SubroutineDocumentation;

import etm.core.monitor.EtmMonitor;

/**
 * Integration tests for {@link DocumentationParser}.
 * 
 * @author Joshua Hyde
 * 
 */

public class DocumentationParserITest extends AbstractFileReaderITest {
    private static final EtmMonitor monitor = TimingsWriter.startMonitor();
    private final DocumentationParser parser = new DocumentationParser();

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

            TimingsWriter.writeTimings(monitor, DocumentationParserITest.class);
        }
    }

    /**
     * Test the parsing of a {@code 
     * 
     * <pre />
     * } tag.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParsePreTagInclude() throws Exception {
        final AbstractDocumentation doc = parser.parse(0, super.readResource("pre_tag.inc"));
        final String lineSeparator = System.getProperty("line.separator");
        assertThat(doc.getDescription()).isEqualTo("I have a <pre /> tag.<pre>" + lineSeparator
                + "    This is a pre tag with a star(*) in it." + lineSeparator + "</pre>");
    }

    /**
     * Test the parsing of record structure documentation.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseRecordStructure() throws Exception {
        final List<String> source = readResource("record_structure_documentation.txt");
        final RecordStructureDocumentation documentation = (RecordStructureDocumentation) parser.parse(0, source);

        assertThat(documentation.getDescription())
                .isEqualTo("This is the top-level documentation for the record structure.");

        final List<Field> fields = documentation.getFields();
        assertThat(fields).hasSize(4);

        final Field personId = fields.get(0);
        assertThat(personId.getName()).isEqualTo("person_id");
        assertThat(personId.getDescription()).isEqualTo("The ID of the person.");
        assertThat(personId.isOptional()).isFalse();
        assertThat(personId.getValues()).isEmpty();
        assertThat(personId.getCodeSets()).isEmpty();

        final Field eventDtTm = fields.get(1);
        assertThat(eventDtTm.getName()).isEqualTo("event_dt_tm");
        assertThat(eventDtTm.getDescription()).isEqualTo("The date and time of the event.");
        assertThat(eventDtTm.isOptional()).isTrue();
        assertThat(eventDtTm.getCodeSets()).isEmpty();
        assertThat(eventDtTm.getValues()).isEmpty();

        final Field sexCd = fields.get(2);
        assertThat(sexCd.getName()).isEqualTo("sex_cd");
        assertThat(sexCd.getDescription()).isEmpty();
        assertThat(sexCd.isOptional()).isFalse();
        assertThat(sexCd.getValues()).isEmpty();

        final List<CodeSet> sexCodeSets = sexCd.getCodeSets();
        assertThat(sexCodeSets).hasSize(1);
        assertThat(sexCodeSets.get(0).getCodeSet()).isEqualTo(291);
        assertThat(sexCodeSets.get(0).getDescription()).isEqualTo("The originating code set");

        final Field typeFlag = fields.get(3);
        assertThat(typeFlag.getName()).isEqualTo("type_flag");
        assertThat(typeFlag.getDescription()).isEmpty();
        assertThat(typeFlag.isOptional()).isFalse();
        assertThat(typeFlag.getCodeSets()).isEmpty();

        final List<EnumeratedValue> typeValues = typeFlag.getValues();
        assertThat(typeValues).hasSize(3);

        assertThat(typeValues.get(0).getValue()).isEqualTo("FEMALE");
        assertThat(typeValues.get(0).getDescription()).isEqualTo("female");

        assertThat(typeValues.get(1).getValue()).isEqualTo("MALE");
        assertThat(typeValues.get(1).getDescription()).isEqualTo("male");

        assertThat(typeValues.get(2).getValue()).isEqualTo("\"UNKNOWN\"");
        assertThat(typeValues.get(2).getDescription()).isEqualTo("\"unknown\"");
    }

    /**
     * If the request tag has no description following it, then
     * 
     * @throws Exception
     *             Sometimes bad things happen.
     */
    @Test
    public void testParseRequestNoDescription() throws Exception {
        final RecordStructureDocumentation documentation = (RecordStructureDocumentation) parser.parse(0,
                readResource("request_record_no_description.inc"));
        assertThat(documentation.getDescription()).isEmpty();
        assertThat(documentation.getStructureType()).isEqualTo(InterfaceStructureType.REQUEST);

        final List<Field> fields = documentation.getFields();
        assertThat(fields).hasSize(1);
        assertThat(fields.get(0).getName()).isEqualTo("status");
        assertThat(fields.get(0).getDescription()).isEqualTo("A status");
        assertThat(fields.get(0).getCodeSets()).isEmpty();
        assertThat(fields.get(0).getValues()).isEmpty();
    }

    /**
     * Test the parsing of subroutine documentation.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseSubroutine() throws Exception {
        final List<String> source = readResource("subroutine_documentation.txt");
        final SubroutineDocumentation documentation = (SubroutineDocumentation) parser.parse(0, source);

        assertThat(documentation.getDescription()).isEqualTo(
                "This is a basic documentation of a subroutine. It contains parameters and a return statement.");
        assertThat(documentation.getReturnDescription()).isEqualTo("It returns something.");

        final List<Parameter> parameters = documentation.getParameters();
        assertThat(parameters).hasSize(3);

        final Parameter arg1 = parameters.get(0);
        assertThat(arg1.getName()).isEqualTo("arg1");
        assertThat(arg1.getDescription()).isEqualTo("The first parameter.");

        final Parameter arg2 = parameters.get(1);
        assertThat(arg2.getName()).isEqualTo("arg2");
        assertThat(arg2.getDescription()).isEqualTo("The second parameter.");

        final Parameter noDoc = parameters.get(2);
        assertThat(noDoc.getName()).isEqualTo("noDoc");
        assertThat(noDoc.getDescription()).isEmpty();
    }

    @Override
    protected List<String> readResource(final String path) throws IOException {
        return normalize(super.readResource(path));
    }

    /**
     * Normalize all read-in data in a format as expected by the documentation parser.
     * 
     * @param source
     *            A {@link List} of {@link String} objects to be {@link DocumentationParserSupport#normalize(String)
     *            normalized}.
     * @return A {@link List} of {@link String} objects representing the normalized source.
     */
    private List<String> normalize(final List<String> source) {
        final List<String> copy = new ArrayList<String>(source.size());
        final DocumentationParserSupport support = new DocumentationParserSupport();
        for (int i = 0, size = source.size(); i < size; i++) {
            copy.add(support.normalize(source.get(i)));
        }

        return copy;
    }
}
