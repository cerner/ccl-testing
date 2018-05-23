package com.cerner.ccl.parser.text.record.parser;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Test;

import com.cerner.ccl.parser.AbstractFileReaderITest;
import com.cerner.ccl.parser.TimingsWriter;
import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.text.record.AbstractParentStructure;
import com.cerner.ccl.parser.text.record.FixedLengthStructureList;
import com.cerner.ccl.parser.text.record.Structure;
import com.cerner.ccl.parser.text.record.StructureCharacterField;
import com.cerner.ccl.parser.text.record.StructureField;
import com.cerner.ccl.parser.text.record.StructureInclude;
import com.cerner.ccl.parser.text.record.StructureList;
import com.cerner.ccl.parser.text.record.StructureRecord;

import etm.core.monitor.EtmMonitor;

/**
 * Integration tests for {@link StructureParser}.
 * 
 * @author Joshua Hyde
 * 
 */

public class StructureParserITest extends AbstractFileReaderITest {
    private static final EtmMonitor monitor = TimingsWriter.startMonitor();
    private final StructureParser parser = new StructureParser();

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

            TimingsWriter.writeTimings(monitor, StructureParserITest.class);
        }
    }

    /**
     * Verify that the ending index is properly stored.
     * 
     * @throws Exception
     *             Sometimes bad things happen.
     */
    @Test
    public void testGetEndingIndex() throws Exception {
        final Structure structure = parser.parse(0, readResource("extra_lines.inc"));
        assertThat(structure.getName()).isEqualTo("withExtras");
        assertThat(structure.getRootLevelMemberCount()).isEqualTo(1);
        assertThat(parser.getEndingIndex()).isEqualTo(2);

        final StructureField member = structure.getRootLevelMember(0);
        assertThat(member.getName()).isEqualTo("field");
        assertThat(member.getLevel()).isEqualTo(1);
        assertThat(member.getDataType()).isEqualTo(DataType.VC);
    }

    /**
     * Test the parsing of a record structure.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParse() throws Exception {
        final List<String> declaration = readResource("record_structure_declaration.inc");
        final Structure structure = parser.parse(0, declaration);
        assertThat(parser.getEndingIndex()).isEqualTo(declaration.size() - 1);

        assertThat(structure.getName()).isEqualTo("request");
        assertThat(structure.getRootLevelMemberCount()).isEqualTo(3);

        {
            final AbstractParentStructure list = structure.getRootLevelMember(0);
            assertThat(list.getName()).isEqualTo("list");
            assertThat(list.getLevel()).isEqualTo(1);
            assertThat(list.getChildMemberCount()).isEqualTo(3);

            final StructureField listInd = list.getChildMember(0);
            assertThat(listInd.getName()).isEqualTo("first_list_ind");
            assertThat(listInd.getLevel()).isEqualTo(2);
            assertThat(listInd.getDataType()).isEqualTo(DataType.I2);

            final StructureCharacterField listChar = list.getChildMember(1);
            assertThat(listChar.getName()).isEqualTo("first_list_char");
            assertThat(listChar.getLevel()).isEqualTo(2);
            assertThat(listChar.getDataType()).isEqualTo(DataType.CHAR);
            assertThat(listChar.getDataLength()).isEqualTo(32);

            final AbstractParentStructure nestedList = list.getChildMember(2);
            assertThat(nestedList.getName()).isEqualTo("nested_list");
            assertThat(nestedList.getLevel()).isEqualTo(2);
            assertThat(nestedList.getChildMemberCount()).isEqualTo(1);

            final StructureField nestedVC = nestedList.getChildMember(0);
            assertThat(nestedVC.getName()).isEqualTo("nested_list_element");
            assertThat(nestedVC.getLevel()).isEqualTo(3);
            assertThat(nestedVC.getDataType()).isEqualTo(DataType.VC);
        }

        final StructureField rootLevelVC = structure.getRootLevelMember(1);
        assertThat(rootLevelVC.getName()).isEqualTo("root_level_vc");
        assertThat(rootLevelVC.getDataType()).isEqualTo(DataType.VC);
        assertThat(rootLevelVC.getLevel()).isEqualTo(1);

        {
            final FixedLengthStructureList list = structure.getRootLevelMember(2);
            assertThat(list.getName()).isEqualTo("second_list");
            assertThat(list.getListSize()).isEqualTo(1);
            assertThat(list.getChildMemberCount()).isEqualTo(1);
            assertThat(list.getLevel()).isEqualTo(1);
            
            final StructureField member = list.getChildMember(0);
            assertThat(member.getName()).isEqualTo("list_element");
            assertThat(member.getDataType()).isEqualTo(DataType.I2);
            assertThat(member.getLevel()).isEqualTo(2);
        }
    }

    /**
     * If there's a comment on the same line as the declared field, the documentation should be ignored
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseDocumentationOnSameLine() throws Exception {
        final Structure structure = parser.parse(0, readResource("documentation_on_same_line.inc"));
        assertThat(structure.getName()).isEqualTo("request");
        assertThat(structure.getRootLevelMemberCount()).isEqualTo(1);

        final StructureField member = structure.getRootLevelMember(0);
        assertThat(member.getName()).isEqualTo("field");
        assertThat(member.getLevel()).isEqualTo(1);
        assertThat(member.getDataType()).isEqualTo(DataType.VC);
    }

    /**
     * If the record structure does a gradual traversal - e.g., 1 -&gt; 2 -&gt; 3 -&gt; 2 -&gt; 1 - with regard to the levels of the members in its structure, make sure it can be parsed correctly.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseGradualTraversal() throws Exception {
        final Structure structure = parser.parse(0, readResource("gradual_traversal.inc"));
        assertThat(structure.getName()).isEqualTo("gradual");
        assertThat(structure.getRootLevelMemberCount()).isEqualTo(2);

        final StructureField rootF8 = structure.getRootLevelMember(1);
        assertThat(rootF8.getName()).isEqualTo("rootF8");
        assertThat(rootF8.getDataType()).isEqualTo(DataType.F8);
        assertThat(rootF8.getLevel()).isEqualTo(1);

        final AbstractParentStructure rootList = structure.getRootLevelMember(0);
        assertThat(rootList.getName()).isEqualTo("rootList");
        assertThat(rootList.getChildMemberCount()).isEqualTo(2);
        assertThat(rootList.getLevel()).isEqualTo(1);

        final StructureField nestedDq8 = rootList.getChildMember(1);
        assertThat(nestedDq8.getName()).isEqualTo("nested_dq8");
        assertThat(nestedDq8.getDataType()).isEqualTo(DataType.DQ8);
        assertThat(nestedDq8.getLevel()).isEqualTo(2);

        final FixedLengthStructureList nestedList = rootList.getChildMember(0);
        assertThat(nestedList.getName()).isEqualTo("nested_list");
        assertThat(nestedList.getListSize()).isEqualTo(1);
        assertThat(nestedList.getLevel()).isEqualTo(2);

        final StructureField nestedNestedField = nestedList.getChildMember(0);
        assertThat(nestedNestedField.getName()).isEqualTo("nested_nested_field");
        assertThat(nestedNestedField.getDataType()).isEqualTo(DataType.DQ8);
        assertThat(nestedNestedField.getLevel()).isEqualTo(3);
    }

    /**
     * If there is a block comment within the record structure declaration, the parser should be able to ignore it.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseInterwovenBlockComment() throws Exception {
        final List<String> source = readResource("interwoven_block_comment.inc");
        final Structure structure = parser.parse(0, source);
        assertThat(parser.getEndingIndex()).isEqualTo(source.size() - 1);

        assertThat(structure.getName()).isEqualTo("blockCommentRequest");
        assertThat(structure.getRootLevelMemberCount()).isEqualTo(2);

        final StructureField firstField = structure.getRootLevelMember(0);
        assertThat(firstField.getName()).isEqualTo("first_field");
        assertThat(firstField.getDataType()).isEqualTo(DataType.VC);
        assertThat(firstField.getLevel()).isEqualTo(1);

        final StructureField secondField = structure.getRootLevelMember(1);
        assertThat(secondField.getName()).isEqualTo("second_field");
        assertThat(secondField.getDataType()).isEqualTo(DataType.F8);
        assertThat(secondField.getLevel()).isEqualTo(1);
    }

    /**
     * If there is a single-line comment interwoven into the record structure declaration, it should be ignored.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseInterwovenSingleCommand() throws Exception {
        final List<String> source = readResource("single_line_comment.inc");
        final Structure structure = parser.parse(0, source);
        assertThat(parser.getEndingIndex()).isEqualTo(source.size() - 1);

        assertThat(structure.getName()).isEqualTo("singleLineCommentRequest");
        assertThat(structure.getRootLevelMemberCount()).isEqualTo(2);

        final StructureField firstField = structure.getRootLevelMember(0);
        assertThat(firstField.getName()).isEqualTo("first_field");
        assertThat(firstField.getDataType()).isEqualTo(DataType.I4);
        assertThat(firstField.getLevel()).isEqualTo(1);

        final StructureField secondField = structure.getRootLevelMember(1);
        assertThat(secondField.getName()).isEqualTo("second_field");
        assertThat(secondField.getDataType()).isEqualTo(DataType.DQ8);
        assertThat(secondField.getLevel()).isEqualTo(1);
    }

    /**
     * Test the parsing of a record from within a record structure.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseRecord() throws Exception {
        final Structure structure = parser.parse(0, readResource("record_record.inc"));
        assertThat(structure.getName()).isEqualTo("with_record");
        assertThat(structure.getRootLevelMemberCount()).isEqualTo(1);

        final StructureRecord record = structure.getRootLevelMember(0);
        assertThat(record.getName()).isEqualTo("record_element");
        assertThat(record.getChildMemberCount()).isEqualTo(1);
        assertThat(record.getLevel()).isEqualTo(1);

        final StructureCharacterField status = record.getChildMember(0);
        assertThat(status.getName()).isEqualTo("status");
        assertThat(status.getDataType()).isEqualTo(DataType.CHAR);
        assertThat(status.getDataLength()).isEqualTo(1);
        assertThat(status.getLevel()).isEqualTo(2);
    }

    /**
     * Test the parsing of a record structure with a status_block.inc inclusion.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseStatusBlockInc() throws Exception {
        final Structure structure = parser.parse(0, readResource("status_block_record.inc"));
        assertThat(structure.getName()).isEqualTo("reply");
        assertThat(structure.getRootLevelMemberCount()).isEqualTo(3);

        final StructureField nonStatusMember = structure.getRootLevelMember(0);
        assertThat(nonStatusMember.getName()).isEqualTo("non_status_member");
        assertThat(nonStatusMember.getDataType()).isEqualTo(DataType.VC);
        assertThat(nonStatusMember.getLevel()).isEqualTo(1);

        final StructureList nestedList = structure.getRootLevelMember(1);
        assertThat(nestedList.getName()).isEqualTo("nested_list");
        assertThat(nestedList.getLevel()).isEqualTo(1);

        final StructureField nestedMember = nestedList.getChildMember(0);
        assertThat(nestedMember.getName()).isEqualTo("nested_member");
        assertThat(nestedMember.getDataType()).isEqualTo(DataType.I4);
        assertThat(nestedMember.getLevel()).isEqualTo(2);

        final StructureRecord statusData = structure.getRootLevelMember(2);
        assertThat(statusData.getName()).isEqualTo("status_data");
        assertThat(statusData.getChildMemberCount()).isEqualTo(2);
        assertThat(statusData.getLevel()).isEqualTo(1);

        final StructureCharacterField status = statusData.getChildMember(0);
        assertThat(status.getName()).isEqualTo("status");
        assertThat(status.getDataType()).isEqualTo(DataType.CHAR);
        assertThat(status.getDataLength()).isEqualTo(1);
        assertThat(status.getLevel()).isEqualTo(2);

        final FixedLengthStructureList subeventStatus = statusData.getChildMember(1);
        assertThat(subeventStatus.getName()).isEqualTo("subeventstatus");
        assertThat(subeventStatus.getListSize()).isEqualTo(1);
        assertThat(subeventStatus.getChildMemberCount()).isEqualTo(4);
        assertThat(subeventStatus.getLevel()).isEqualTo(2);

        final StructureCharacterField operationName = subeventStatus.getChildMember(0);
        assertThat(operationName.getName()).isEqualTo("OperationName");
        assertThat(operationName.getDataType()).isEqualTo(DataType.CHAR);
        assertThat(operationName.getDataLength()).isEqualTo(25);
        assertThat(operationName.getLevel()).isEqualTo(3);

        final StructureCharacterField operationStatus = subeventStatus.getChildMember(1);
        assertThat(operationStatus.getName()).isEqualTo("OperationStatus");
        assertThat(operationStatus.getDataType()).isEqualTo(DataType.CHAR);
        assertThat(operationStatus.getDataLength()).isEqualTo(1);
        assertThat(operationStatus.getLevel()).isEqualTo(3);

        final StructureCharacterField targetObjectName = subeventStatus.getChildMember(2);
        assertThat(targetObjectName.getName()).isEqualTo("TargetObjectName");
        assertThat(targetObjectName.getDataType()).isEqualTo(DataType.CHAR);
        assertThat(targetObjectName.getDataLength()).isEqualTo(25);
        assertThat(targetObjectName.getLevel()).isEqualTo(3);

        final StructureField targetObjectValue = subeventStatus.getChildMember(3);
        assertThat(targetObjectValue.getName()).isEqualTo("TargetObjectValue");
        assertThat(targetObjectValue.getDataType()).isEqualTo(DataType.VC);
        assertThat(targetObjectValue.getLevel()).isEqualTo(3);
    }

    /**
     * Test the parsing of a structure with an include file embedded into the definition.
     * 
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testParseWithInclude() throws Exception {
        final Structure structure = parser.parse(0, readResource("record_structure_with_include.inc"));
        assertThat(structure.getName()).isEqualTo("hasInclude");
        assertThat(structure.getRootLevelMemberCount()).isEqualTo(3);

        /*
         * Verify the list
         */
        {
            final FixedLengthStructureList list = structure.getRootLevelMember(0);
            assertThat(list.getName()).isEqualTo("list");
            assertThat(list.getLevel()).isEqualTo(1);
            assertThat(list.getChildMemberCount()).isEqualTo(1);

            final StructureCharacterField charField = list.getChildMember(0);
            assertThat(charField.getName()).isEqualTo("char_field");
            assertThat(charField.getLevel()).isEqualTo(2);
            assertThat(charField.getDataType()).isEqualTo(DataType.CHAR);
            assertThat(charField.getDataLength()).isEqualTo(32);
        }
        
        final StructureInclude include = structure.getRootLevelMember(1);
        assertThat(include.getName()).isEqualTo("cclsource:some_status.inc");

        final StructureField afterField = structure.getRootLevelMember(2);
        assertThat(afterField.getName()).isEqualTo("after_field");
        assertThat(afterField.getDataType()).isEqualTo(DataType.F8);
        assertThat(afterField.getLevel()).isEqualTo(1);
    }
}
