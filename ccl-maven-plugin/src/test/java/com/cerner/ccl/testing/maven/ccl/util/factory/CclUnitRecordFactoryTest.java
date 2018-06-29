package com.cerner.ccl.testing.maven.ccl.util.factory;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.cerner.ccl.j4ccl.record.DataType;
import com.cerner.ccl.j4ccl.record.DynamicRecordList;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.RecordList;
import com.cerner.ccl.j4ccl.record.Structure;
import com.cerner.ccl.testing.maven.ccl.util.factory.CclUnitRecordFactory;

/**
 * Unit tests of {@link CclUnitRecordFactory}.
 *
 * @author Joshua Hyde
 *
 */

public class CclUnitRecordFactoryTest {
    /**
     * Test that the request record structure is built properly.
     */
    @Test
    public void testCreateRequest() {
        final String testSubroutineName = "a_subroutine";
        final String scriptName = "scriptName";
        final List<String> scriptNames = Collections.singletonList(scriptName);
        final String testInclude = "test.inc";
        final Record request = CclUnitRecordFactory.createRequest(scriptNames, testInclude, true, null, true, "W",
                testSubroutineName);

        assertThat(request.getVC("testINCName")).isEqualTo(testInclude);
        assertThat(request.getVC("optimizerMode")).isEmpty();
        assertThat(request.getI2Boolean("enforcePredeclare")).isTrue();
        assertThat(request.getVC("deprecatedFlag")).isEqualTo("W");
        assertThat(request.getVC("testSubroutineName")).isEqualTo(testSubroutineName);

        final DynamicRecordList programs = request.getDynamicList("programs");
        assertThat(programs.getSize()).isEqualTo(1);

        final Record program = programs.get(0);
        assertThat(program.getVC("programName")).isEqualTo(scriptName);
        assertThat(program.getI2Boolean("compile")).isTrue();
    }

    /**
     * If the request has no subroutine name pattern specified, then none should be passed into the request.
     */
    @Test
    public void testCreateRequestNoSubroutineNamePattern() {
        final Record request = CclUnitRecordFactory.createRequest(Collections.singletonList("a_script"), "an_include",
                true, "RBO", false, "", null);
        assertThat(request.getVC("testSubroutineName")).isEmpty();
    }

    /**
     * Test that, if a non-{@code null} optimizer mode is given, that it will be set.
     */
    @Test
    public void testCreateRequestWithOptimizerMode() {
        final Record request = CclUnitRecordFactory.createRequest(Collections.singletonList("a_script"), "an_include",
                true, "RBO", false, "", null);
        assertThat(request.getVC("optimizerMode")).isEqualTo("RBO");
    }

    /**
     * Test that the reply record structure is built properly at the root level.
     */
    @Test
    public void testCreateReplyRootLevel() {
        final Record reply = CclUnitRecordFactory.createReply();

        assertThat(reply.getType("environmentXML")).isEqualTo(DataType.VC);
        assertThat(reply.getType("testINCListingXML")).isEqualTo(DataType.VC);
        assertThat(reply.getType("testINCCoverageXML")).isEqualTo(DataType.VC);
        assertThat(reply.getType("testINCResultsXML")).isEqualTo(DataType.VC);
    }

    /**
     * Verify that the "programs" element of the reply record structure is well-formed.
     */
    @Test
    public void testCreateReplyPrograms() {
        final Record reply = CclUnitRecordFactory.createReply();
        assertThat(reply.getType("programs")).isEqualTo(DataType.DYNAMIC_LIST);

        final DynamicRecordList list = reply.getDynamicList("programs");
        // Add an item to the list in order to be able to view its structure
        final Record program = list.addItem();
        assertThat(program.getType("programName")).isEqualTo(DataType.VC);
        assertThat(program.getType("listingXML")).isEqualTo(DataType.VC);
        assertThat(program.getType("coverageXML")).isEqualTo(DataType.VC);
    }

    /**
     * Verify that the reply record structure is created with a status data block.
     */
    @Test
    public void testCreateReplyStatusData() {
        final Record reply = CclUnitRecordFactory.createReply();

        final Record statusData = reply.getRecord("status_data");
        final Structure statusStruct = statusData.getStructure();
        assertThat(statusData.getType("status")).isEqualTo(DataType.CHARACTER);
        assertThat(statusStruct.getField("status").getDataLength()).isEqualTo(1);

        final RecordList list = statusData.getList("subeventstatus");
        assertThat(list.getSize()).isEqualTo(1);

        // Add an element to view the structure backing the list
        final Record subRecord = list.get(0);
        final Structure subStruct = subRecord.getStructure();

        assertThat(subRecord.getType("OperationName")).isEqualTo(DataType.CHARACTER);
        assertThat(subStruct.getField("OperationName").getDataLength()).isEqualTo(25);

        assertThat(subRecord.getType("OperationStatus")).isEqualTo(DataType.CHARACTER);
        assertThat(subStruct.getField("OperationStatus").getDataLength()).isEqualTo(1);

        assertThat(subRecord.getType("TargetObjectName")).isEqualTo(DataType.CHARACTER);
        assertThat(subStruct.getField("TargetObjectName").getDataLength()).isEqualTo(25);

        assertThat(subRecord.getType("TargetObjectValue")).isEqualTo(DataType.VC);
    }

}
