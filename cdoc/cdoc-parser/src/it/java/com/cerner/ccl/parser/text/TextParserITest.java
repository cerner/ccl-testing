package com.cerner.ccl.parser.text;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;

import com.cerner.ccl.parser.AbstractFileReaderITest;
import com.cerner.ccl.parser.TimingsWriter;
import com.cerner.ccl.parser.data.CclScript;
import com.cerner.ccl.parser.data.CodeSet;
import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.DataTyped;
import com.cerner.ccl.parser.data.EnumeratedValue;
import com.cerner.ccl.parser.data.IncludeFile;
import com.cerner.ccl.parser.data.ScriptArgument;
import com.cerner.ccl.parser.data.ScriptDocumentation;
import com.cerner.ccl.parser.data.record.AbstractParentRecordStructureMember;
import com.cerner.ccl.parser.data.record.FixedLengthRecordStructureList;
import com.cerner.ccl.parser.data.record.InterfaceStructureType;
import com.cerner.ccl.parser.data.record.RecordInclude;
import com.cerner.ccl.parser.data.record.RecordStructure;
import com.cerner.ccl.parser.data.record.RecordStructureCharacterField;
import com.cerner.ccl.parser.data.record.RecordStructureField;
import com.cerner.ccl.parser.data.subroutine.Subroutine;
import com.cerner.ccl.parser.data.subroutine.SubroutineArgument;

import etm.core.monitor.EtmMonitor;

/**
 * Integration test for {@link TextParser}.
 *
 * @author Joshua Hyde
 *
 */

public class TextParserITest extends AbstractFileReaderITest {
    private static final EtmMonitor monitor = TimingsWriter.startMonitor();
    private final TextParser parser = new TextParser();

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

            TimingsWriter.writeTimings(monitor, TextParserITest.class);
        }
    }

    /**
     * Test the parsing of a CCL script.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseCclScript() throws Exception {
        final CclScript script = parser.parseCclScript("normal_script", readResource("normal_script.prg"));
        assertThat(script.getName()).isEqualTo("normal_script");

        /*
         * Verify script documentation
         */
        {
            final ScriptDocumentation documentation = script.getScriptDocumentation();
            assertThat(documentation.getDescription()).isEqualTo("This is an example of a normal script. It has arguments and a bound transaction.");
            assertThat(documentation.getBoundTransaction()).isEqualTo(1337);

            final List<ScriptArgument> arguments = documentation.getScriptArguments();
            assertThat(arguments).hasSize(2);
            assertThat(arguments.get(0).getDescription()).isEqualTo("This is the first argument.");
            assertThat(arguments.get(1).getDescription()).isEqualTo("This is the second argument.");
        }
        /*
         * Verify subroutines
         */
        {
            final List<Subroutine> subroutines = script.getSubroutines();
            assertThat(subroutines).hasSize(3);

            /*
             * Verify "get_birth_dt_tm"
             */
            {
                final Subroutine subroutine = subroutines.get(0);
                assertThat(subroutine.getName()).isEqualTo("get_birth_dt_tm");
				assertThat(subroutine.<DataTyped>getReturnDataType()).isNull();
                assertThat(subroutine.getDescription()).isEqualTo("This is a simple subroutine that has its documentation wrapped in stars.");
                assertThat(subroutine.getReturnDataDescription()).isEmpty();

                final List<SubroutineArgument> arguments = subroutine.getArguments();
                assertThat(arguments).hasSize(2);

                final SubroutineArgument personId = arguments.get(0);
                assertThat(personId.getName()).isEqualTo("person_id");
                assertThat(personId.getDataType()).isEqualTo(DataType.F8);
                assertThat(personId.isByRef()).isFalse();

                final SubroutineArgument birthDtTm = arguments.get(1);
                assertThat(birthDtTm.getName()).isEqualTo("birth_dt_tm");
                assertThat(birthDtTm.getDataType()).isEqualTo(DataType.DQ8);
                assertThat(birthDtTm.isByRef()).isTrue();
            }

            /*
             * Verify "get_last_name"
             */
            {
                final Subroutine subroutine = subroutines.get(1);
                assertThat(subroutine.getName()).isEqualTo("get_last_name");
                final DataTyped returnDataType = subroutine.getReturnDataType();
                assertThat(returnDataType).isNotNull();
                assertThat(returnDataType.getDataType()).isEqualTo(DataType.VC);
                assertThat(subroutine.getDescription()).isEqualTo("This is a subroutine with no encasing stars. It also has scope!");
                assertThat(subroutine.getReturnDataDescription()).isEqualTo("The last name of the person found");

                final List<SubroutineArgument> arguments = subroutine.getArguments();
                assertThat(arguments).hasSize(1);

                final SubroutineArgument personId = arguments.get(0);
                assertThat(personId.getName()).isEqualTo("person_id");
                assertThat(personId.getDataType()).isEqualTo(DataType.F8);
                assertThat(personId.isByRef()).isFalse();
            }

            /*
             * Verify "get_ssn"
             */
            {
                final Subroutine subroutine = subroutines.get(2);
                assertThat(subroutine.getName()).isEqualTo("get_ssn");
				assertThat(subroutine.<DataTyped>getReturnDataType()).isEqualTo(Subroutine.UNKNOWN_RETURN_TYPE);
                assertThat(subroutine.getDescription()).isEqualTo("This subroutine has no declaration, so the information about it will be severely limited.");
                assertThat(subroutine.getReturnDataDescription()).isEqualTo("The given person's SSN.");

                final List<SubroutineArgument> arguments = subroutine.getArguments();
                assertThat(arguments).hasSize(1);

                final SubroutineArgument personId = arguments.get(0);
                assertThat(personId.getName()).isEqualTo("person_id");
                assertThat(personId.getDataType()).isNull();
                assertThat(personId.isByRef()).isFalse();
            }
        }

        /*
         * Verify record structures
         */
        {
            final List<RecordStructure> recordStructures = script.getRecordStructures();
            assertThat(recordStructures).hasSize(3);

            {
                final RecordStructure request = recordStructures.get(0);
                assertThat(request.getName()).isEqualTo("request");
                assertThat(request.getDescription()).isEqualTo("This is the request record structure.");
                assertThat(request.getStructureType()).isEqualTo(InterfaceStructureType.REQUEST);
                assertThat(request.getRootLevelMemberCount()).isEqualTo(4);

                {
                    final AbstractParentRecordStructureMember firstList = request.getRootLevelMember(0);
                    assertThat(firstList.getName()).isEqualTo("first_list");
                    assertThat(firstList.getLevel()).isEqualTo(1);
                    assertThat(firstList.getDescription()).isEqualTo("The first list");
                    assertThat(firstList.getChildMemberCount()).isEqualTo(1);

                    final RecordStructureField firstListInd = firstList.getChildMember(0);
                    assertThat(firstListInd.getName()).isEqualTo("first_list_ind");
                    assertThat(firstListInd.getDescription()).isEqualTo("Indicator in the first list");
                    assertThat(firstListInd.getDataType()).isEqualTo(DataType.I2);
                    assertThat(firstListInd.getLevel()).isEqualTo(2);
                    assertThat(firstListInd.getCodeSets()).isEmpty();
                    assertThat(firstListInd.getValues()).isEmpty();
                }

                {
                    final AbstractParentRecordStructureMember secondList = request.getRootLevelMember(1);
                    assertThat(secondList.getName()).isEqualTo("second_list");
                    assertThat(secondList.getDescription()).isEqualTo("The second list");
                    assertThat(secondList.getLevel()).isEqualTo(1);
                    assertThat(secondList.getChildMemberCount()).isEqualTo(1);

                    final RecordStructureField secondListVc = secondList.getChildMember(0);
                    assertThat(secondListVc.getName()).isEqualTo("second_list_vc");
                    assertThat(secondListVc.getDataType()).isEqualTo(DataType.VC);
                    assertThat(secondListVc.getLevel()).isEqualTo(2);
                    assertThat(secondListVc.getDescription()).isEqualTo("The name of the member of the second list");
                    assertThat(secondListVc.getValues()).isEmpty();

                    final List<CodeSet> codeSets = secondListVc.getCodeSets();
                    assertThat(codeSets).hasSize(1);

                    assertThat(codeSets.get(0).getCodeSet()).isEqualTo(387);
                    assertThat(codeSets.get(0).getDescription()).isEqualTo("The code set for second_list_vc");
                }

                {
                    final RecordStructureCharacterField sharedNameField = request.getRootLevelMember(2);
                    assertThat(sharedNameField.getName()).isEqualTo("shared_name");
                    assertThat(sharedNameField.getDescription()).isEqualTo("The first instance of a shared-name field");
                    assertThat(sharedNameField.getLevel()).isEqualTo(1);
                    assertThat(sharedNameField.getDataType()).isEqualTo(DataType.CHAR);
                    assertThat(sharedNameField.getDataLength()).isEqualTo(24);
                    assertThat(sharedNameField.getCodeSets()).isEmpty();

                    final List<EnumeratedValue> values = sharedNameField.getValues();
                    assertThat(values).hasSize(3);

                    assertThat(values.get(0).getValue()).isEqualTo("1");
                    assertThat(values.get(0).getDescription()).isEqualTo("Yahoo!");

                    assertThat(values.get(1).getValue()).isEqualTo("2");
                    assertThat(values.get(1).getDescription()).isEqualTo("Too blue!");

                    assertThat(values.get(2).getValue()).isEqualTo("3");
                    assertThat(values.get(2).getDescription()).isEqualTo("This time's the charm.");
                }

                {
                    final FixedLengthRecordStructureList sharedNameList = request.getRootLevelMember(3);
                    assertThat(sharedNameList.getName()).isEqualTo("shared_name_list");
                    assertThat(sharedNameList.getDescription()).isEqualTo("A list that contains a member with a shared name");
                    assertThat(sharedNameList.getLevel()).isEqualTo(1);
                    assertThat(sharedNameList.getListSize()).isEqualTo(31);
                    assertThat(sharedNameList.getChildMemberCount()).isEqualTo(1);

                    final RecordStructureField sharedNameField = sharedNameList.getChildMember(0);
                    assertThat(sharedNameField.getName()).isEqualTo("shared_name");
                    assertThat(sharedNameField.getLevel()).isEqualTo(2);
                    assertThat(sharedNameField.getDataType()).isEqualTo(DataType.F8);
                    assertThat(sharedNameField.getDescription()).isEqualTo("The second field that has a shared name");
                    assertThat(sharedNameField.getCodeSets()).isEmpty();
                    assertThat(sharedNameField.getValues()).isEmpty();
                }
            } // request

            {
                final RecordStructure reply = recordStructures.get(1);
                assertThat(reply.getName()).isEqualTo("reply");
                assertThat(reply.getStructureType()).isEqualTo(InterfaceStructureType.REPLY);
                assertThat(reply.getRootLevelMemberCount()).isEqualTo(1);

                final RecordStructureField field = reply.getRootLevelMember(0);
                assertThat(field.getName()).isEqualTo("success_ind");
                assertThat(field.getDataType()).isEqualTo(DataType.I2);
                assertThat(field.getLevel()).isEqualTo(1);
                assertThat(field.getDescription()).isEqualTo("The success indicator");
                assertThat(field.getValues()).isEmpty();
                assertThat(field.getCodeSets()).isEmpty();
            } // reply

            {
                final RecordStructure medData = recordStructures.get(2);
                assertThat(medData.getName()).isEqualTo("med_data");
                assertThat(medData.getDescription()).isEqualTo("This really doesn't have to do with anything");
                assertThat(medData.getStructureType()).isNull();
                assertThat(medData.getRootLevelMemberCount()).isEqualTo(1);

                final RecordStructureCharacterField field = medData.getRootLevelMember(0);
                assertThat(field.getName()).isEqualTo("char_field");
                assertThat(field.getDescription()).isEqualTo("This is the character field");
                assertThat(field.getLevel()).isEqualTo(1);
                assertThat(field.getDataType()).isEqualTo(DataType.CHAR);
                assertThat(field.getDataLength()).isEqualTo(1);
                assertThat(field.getValues()).isEmpty();
                assertThat(field.getCodeSets()).isEmpty();
            } // med_data
        }
    }

    /**
     * Verify that, if there is no top-level documentation, that the documentation block is correctly associated with the correct object inside the script.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseCclScriptNoTopLevelDoc() throws Exception {
        final CclScript script = parser.parseCclScript("no_top_level_doc", readResource("no_top_level_doc.prg"));
        assertThat(script.getName()).isEqualTo("no_top_level_doc");
        assertThat(script.getScriptDocumentation().getDescription()).isEmpty();

        final List<Subroutine> subroutines = script.getSubroutines();
        assertThat(subroutines).hasSize(1);

        final Subroutine subroutine = subroutines.get(0);
        assertThat(subroutine.getDescription()).isEqualTo("This is actually documentation belonging to the subroutine");
    }

    /**
     * Test the parsing of an include file.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseIncludeFile() throws Exception {
        final IncludeFile include = parser.parseIncludeFile("normal_include.inc", readResource("normal_include.inc"));
        assertThat(include.getName()).isEqualTo("normal_include.inc");
        assertThat(include.getIncludeDocumentation().getDescription()).isEqualTo("This is an include file.");

        /*
         * Validate record structures
         */
        {
            final List<RecordStructure> records = include.getRecordStructures();
            assertThat(records).hasSize(2);

            final RecordStructure request = records.get(0);
            assertThat(request.getName()).isEqualTo("includeRequest");
            assertThat(request.getStructureType()).isEqualTo(InterfaceStructureType.REQUEST);
            assertThat(request.getRootLevelMemberCount()).isEqualTo(1);

            final RecordStructureField personId = request.getRootLevelMember(0);
            assertThat(personId.getName()).isEqualTo("person_id");
            assertThat(personId.getDataType()).isEqualTo(DataType.F8);
            assertThat(personId.getDescription()).isEqualTo("The ID of the person.");
            assertThat(personId.getCodeSets()).isEmpty();
            assertThat(personId.getValues()).isEmpty();

            final RecordStructure reply = records.get(1);
            assertThat(reply.getName()).isEqualTo("includeReply");
            assertThat(reply.getStructureType()).isEqualTo(InterfaceStructureType.REPLY);
            assertThat(reply.getRootLevelMemberCount()).isEqualTo(1);

            final RecordStructureCharacterField status = reply.getRootLevelMember(0);
            assertThat(status.getName()).isEqualTo("status");
            assertThat(status.getDataType()).isEqualTo(DataType.CHAR);
            assertThat(status.getDataLength()).isEqualTo(1);
            assertThat(status.getDescription()).isEqualTo("The status of the call.");
            assertThat(status.getCodeSets()).isEmpty();

            final List<EnumeratedValue> statusValues = status.getValues();
            assertThat(statusValues).hasSize(2);

            assertThat(statusValues.get(0).getValue()).isEqualTo("S");
            assertThat(statusValues.get(0).getDescription()).isEqualTo("The call succeeded.");
            assertThat(statusValues.get(1).getValue()).isEqualTo("F");
            assertThat(statusValues.get(1).getDescription()).isEqualTo("The call failed.");
        }

        /*
         * Validate subroutines
         */
        {
            final List<Subroutine> subroutines = include.getSubroutines();
            assertThat(subroutines).hasSize(1);

            final Subroutine includedSub = subroutines.get(0);
            assertThat(includedSub.getName()).isEqualTo("included_sub");
			assertThat(includedSub.<DataTyped>getReturnDataType()).isNull();
            assertThat(includedSub.returnsVoid()).isTrue();
            assertThat(includedSub.getDescription()).isEmpty();
            assertThat(includedSub.getReturnDataDescription()).isEmpty();

            final List<SubroutineArgument> arguments = includedSub.getArguments();
            assertThat(arguments).hasSize(1);

            final SubroutineArgument argument = arguments.get(0);
            assertThat(argument.getName()).isEqualTo("arg1");
            assertThat(argument.getDataType()).isEqualTo(DataType.F8);
            assertThat(argument.getDescription()).isEmpty();
            assertThat(argument.isByRef()).isFalse();
        }
    }

    /**
     * Test parsing of an record structure with an include file statement within it that is not {@code status_block.inc}.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseWithRecordInclude() throws Exception {
        final IncludeFile include = parser.parseIncludeFile("record_struct_with_include.inc", readResource("record_struct_with_include.inc"));
        assertThat(include.getName()).isEqualTo("record_struct_with_include.inc");

        assertThat(include.getRecordStructures()).hasSize(1);
        final RecordStructure withInclude = include.getRecordStructures().get(0);
        assertThat(withInclude.getName()).isEqualTo("test");
        assertThat(withInclude.getRootLevelMemberCount()).isEqualTo(2);

        final RecordStructureField field = withInclude.getRootLevelMember(0);
        assertThat(field.getName()).isEqualTo("field");
        assertThat(field.getDataType()).isEqualTo(DataType.F8);

        final RecordInclude includeFile = withInclude.getRootLevelMember(1);
        assertThat(includeFile.getName()).isEqualTo("cclsource:test.inc");
        assertThat(includeFile.getDescription()).isEmpty();
    }
}
