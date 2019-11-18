package com.cerner.ccl.parser.text;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.cerner.ccl.parser.data.CclScript;
import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.ScriptDocumentation;
import com.cerner.ccl.parser.data.SimpleDataTyped;
import com.cerner.ccl.parser.data.record.InterfaceStructureType;
import com.cerner.ccl.parser.data.record.RecordStructure;
import com.cerner.ccl.parser.data.subroutine.Subroutine;

/**
 * Unit tests for {@link TextParser}.
 *
 * @author Joshua Hyde
 * @see TextParserITest
 */

public class TextParserTest {
    private final TextParser parser = new TextParser();

    /**
     * Parsing a script with a {@code null} object name should fail.
     */
    @Test
    public void testParseNullObjectName() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            parser.parseCclScript(null, Collections.<String> emptyList());
        });
        assertThat(e.getMessage()).isEqualTo("Object name cannot be null.");
    }

    /**
     * Parsing of a script with {@code null} source should fail.
     */
    @Test
    public void testParseNullSource() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            parser.parseCclScript("an_object_name", null);
        });
        assertThat(e.getMessage()).isEqualTo("Source cannot be null.");
    }

    /**
     * Parsing an actual script should not fail.
     *
     * @throws IOException
     *             Not expected.
     */
    @Test
    public void testParseAScript() throws IOException {
        String line = "";
        List<String> sourceA = new ArrayList<String>();
        try (InputStream isA = TextParserTest.class.getResourceAsStream("/normal_script.prg");
                BufferedReader br = new BufferedReader(new InputStreamReader(isA))) {
            assertThat(isA).isNotNull();
            while ((line = br.readLine()) != null) {
                sourceA.add(line);
            }
        }
        CclScript cclScriptA = parser.parseCclScript("script", sourceA);

        List<String> sourceB = new ArrayList<String>();
        try (InputStream isB = TextParserTest.class.getResourceAsStream("/normal_script_extra_spaces.prg");
                BufferedReader br = new BufferedReader(new InputStreamReader(isB))) {
            assertThat(isB).isNotNull();

            while ((line = br.readLine()) != null) {
                sourceB.add(line);
            }
        }
        CclScript cclScriptB = parser.parseCclScript("script", sourceB);

        List<String> sourceC = new ArrayList<String>();
        try (InputStream isC = TextParserTest.class.getResourceAsStream("/normal_script_tabs.prg");
                BufferedReader br = new BufferedReader(new InputStreamReader(isC))) {
            assertThat(isC).isNotNull();
            while ((line = br.readLine()) != null) {
                sourceC.add(line);
            }
        }
        CclScript cclScriptC = parser.parseCclScript("script", sourceA);

        List<String> sourceD = new ArrayList<String>();
        try (InputStream isD = TextParserTest.class.getResourceAsStream("/normal_script_extra_tabs.prg");
                BufferedReader br = new BufferedReader(new InputStreamReader(isD))) {
            assertThat(isD).isNotNull();

            while ((line = br.readLine()) != null) {
                sourceD.add(line);
            }
        }
        CclScript cclScriptD = parser.parseCclScript("script", sourceD);

        assertThat(cclScriptB.equals(cclScriptA)).isTrue();
        assertThat(cclScriptC).isEqualTo(cclScriptA);
        assertThat(cclScriptD).isEqualTo(cclScriptA);
    }

    /**
     * Validates the parsing of a script with all types of subroutine declarations.
     *
     * @throws IOException
     *             Not expected.
     */
    @Test
    public void testSubroutineDeclarations() throws IOException {
        String line = "";
        List<String> source = new ArrayList<String>();
        try (InputStream is = TextParserTest.class.getResourceAsStream("/all_sub_types.prg");
                BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            assertThat(is).isNotNull();
            while ((line = br.readLine()) != null) {
                source.add(line);
            }
        }
        CclScript cclScript = parser.parseCclScript("all_sub_types", source);
        assertThat(cclScript.getName()).isEqualTo("all_sub_types");
        ScriptDocumentation documentation = cclScript.getScriptDocumentation();
        List<RecordStructure> recordStructures = cclScript.getRecordStructures();
        List<Subroutine> subroutines = cclScript.getSubroutines();
        assertThat(documentation.getBoundTransaction()).isEqualTo(98765);
        assertThat(documentation.getDescription()).isEqualTo(
                "This is an example script with all types of subroutine declarations, predeclared, in-line and undeclared. It has arguments and a bound transaction.");
        assertThat(documentation.getScriptArguments().size()).isEqualTo(2);
        assertThat(documentation.getScriptArguments().get(0).getDescription()).isEqualTo("This is the first argument.");
        assertThat(documentation.getScriptArguments().get(1).getDescription())
                .isEqualTo("This is the second argument.");
        assertThat(recordStructures.size()).isEqualTo(5);
        assertThat(recordStructures.get(0).getName()).isEqualTo("test1");
        assertThat(recordStructures.get(0).getDescription()).isEqualTo("");
        assertThat(recordStructures.get(0).getRootLevelMemberCount()).isEqualTo(2);
        assertThat(recordStructures.get(0).getRootLevelMember(0).getName()).isEqualTo("field1");
        assertThat(recordStructures.get(0).getRootLevelMember(0).getDescription()).isEqualTo("");
        assertThat(recordStructures.get(0).getRootLevelMember(1).getName()).isEqualTo("field2");
        assertThat(recordStructures.get(0).getRootLevelMember(1).getDescription()).isEqualTo("");
        assertThat(recordStructures.get(1).getStructureType()).isEqualTo(InterfaceStructureType.REPLY);
        assertThat(recordStructures.get(1).getName()).isEqualTo("test");
        assertThat(recordStructures.get(1).getDescription()).isEqualTo("");
        assertThat(recordStructures.get(1).getRootLevelMemberCount()).isEqualTo(3);
        assertThat(recordStructures.get(1).getRootLevelMember(0).getName()).isEqualTo("field");
        assertThat(recordStructures.get(1).getRootLevelMember(1).getName()).isEqualTo("status_data");
        assertThat(recordStructures.get(1).getRootLevelMember(2).getName()).isEqualTo("cclsource:test.inc");
        assertThat(recordStructures.get(2).getStructureType()).isEqualTo(InterfaceStructureType.REQUEST);
        assertThat(recordStructures.get(2).getName()).isEqualTo("the_request");
        assertThat(recordStructures.get(2).getDescription()).isEqualTo("This is the request record structure.");
        assertThat(recordStructures.get(2).getRootLevelMemberCount()).isEqualTo(4);
        assertThat(recordStructures.get(2).getRootLevelMember(0).getName()).isEqualTo("first_list");
        assertThat(recordStructures.get(2).getRootLevelMember(1).getName()).isEqualTo("second_list");
        assertThat(recordStructures.get(2).getRootLevelMember(2).getName()).isEqualTo("shared_name");
        assertThat(recordStructures.get(2).getRootLevelMember(3).getName()).isEqualTo("shared_name_list");
        assertThat(recordStructures.get(3).getStructureType()).isEqualTo(InterfaceStructureType.REPLY);
        assertThat(recordStructures.get(3).getName()).isEqualTo("the_reply");
        assertThat(recordStructures.get(3).getDescription()).isEqualTo("What-what?");
        assertThat(recordStructures.get(3).getRootLevelMemberCount()).isEqualTo(1);
        assertThat(recordStructures.get(3).getRootLevelMember(0).getName()).isEqualTo("success_ind");
        assertThat(recordStructures.get(4).getStructureType()).isNull();
        assertThat(recordStructures.get(4).getName()).isEqualTo("med_data");
        assertThat(recordStructures.get(4).getDescription()).isEqualTo("This really doesn't have to do with anything");
        assertThat(recordStructures.get(4).getRootLevelMemberCount()).isEqualTo(1);
        assertThat(recordStructures.get(4).getRootLevelMember(0).getName()).isEqualTo("char_field");

        assertThat(subroutines.size()).isEqualTo(6);

        assertThat(subroutines.get(0).getName()).isEqualTo("declared_subroutine_doc");
        assertThat(subroutines.get(0).getDescription()).isEqualTo("This is a declared subroutine with documentation.");
        assertThat(subroutines.get(0).<SimpleDataTyped> getReturnDataType())
                .isEqualTo(new SimpleDataTyped(DataType.I4));
        assertThat(subroutines.get(0).getReturnDataDescription()).isEqualTo("Some I4 value.");

        assertThat(subroutines.get(1).getName()).isEqualTo("get_birth_dt_tm");
        assertThat(subroutines.get(1).getDescription()).isEqualTo(
                "This is a simple subroutine declared in-line that has its documentation wrapped in leading stars.");
        assertThat(subroutines.get(1).<SimpleDataTyped> getReturnDataType()).isEqualTo(null);
        assertThat(subroutines.get(1).getReturnDataDescription()).isEmpty();

        assertThat(subroutines.get(2).getName()).isEqualTo("declared_subroutine_no_doc");
        assertThat(subroutines.get(2).getDescription()).isEmpty();
        assertThat(subroutines.get(2).<SimpleDataTyped> getReturnDataType())
                .isEqualTo(new SimpleDataTyped(DataType.VC));
        assertThat(subroutines.get(2).getReturnDataDescription()).isEmpty();

        assertThat(subroutines.get(3).getName()).isEqualTo("get_last_name");
        assertThat(subroutines.get(3).getDescription()).isEqualTo(
                "This is a subroutine declared in-line without leading stars in its documentation. It also has scope!");
        assertThat(subroutines.get(3).<SimpleDataTyped> getReturnDataType())
                .isEqualTo(new SimpleDataTyped(DataType.VC));
        assertThat(subroutines.get(3).getReturnDataDescription()).isEqualTo("The last name of the person found");

        assertThat(subroutines.get(4).getName()).isEqualTo("undeclared_subroutine_no_doc");
        assertThat(subroutines.get(4).getDescription()).isEmpty();
        assertThat(subroutines.get(4).<SimpleDataTyped> getReturnDataType()).isEqualTo(Subroutine.UNKNOWN_RETURN_TYPE);
        assertThat(subroutines.get(4).getReturnDataDescription()).isEmpty();

        assertThat(subroutines.get(5).getName()).isEqualTo("get_ssn");
        assertThat(subroutines.get(5).getDescription())
                .isEqualTo("This subroutine has no declaration, so the information about it will be severely limited.");
        assertThat(subroutines.get(5).<SimpleDataTyped> getReturnDataType()).isEqualTo(Subroutine.UNKNOWN_RETURN_TYPE);
        assertThat(subroutines.get(5).getReturnDataDescription()).isEqualTo("The given person's SSN.");
    }
}
