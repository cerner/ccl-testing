package com.cerner.ccl.testing.maven.ccl.util.factory;

import java.util.List;

import com.cerner.ccl.j4ccl.record.DynamicRecordList;
import com.cerner.ccl.j4ccl.record.Record;
import com.cerner.ccl.j4ccl.record.Structure;
import com.cerner.ccl.j4ccl.record.StructureBuilder;
import com.cerner.ccl.j4ccl.record.factory.RecordFactory;

/**
 * Factory for creating record structures used by the CCLUnit framework
 *
 * @author Joshua Hyde
 * @author Mark Cummings
 */
public final class CclUnitRecordFactory {
    private static final Structure REQUEST_STRUCTURE;
    private static final Structure REPLY_STRUCTURE;

    static {
        /*
         * Build the request record structure
         */
        final Structure requestPrograms = StructureBuilder.getBuilder().addVC("programName").addI2("compile").build();
        REQUEST_STRUCTURE = StructureBuilder.getBuilder().addVC("testINCName")
                .addDynamicList("programs", requestPrograms).addVC("optimizerMode").addI2("enforcePredeclare")
                .addVC("deprecatedFlag").addVC("testSubroutineName").build();

        /*
         * Build the reply record structure
         */
        final Structure replyPrograms = StructureBuilder.getBuilder().addVC("programName").addVC("listingXML")
                .addVC("coverageXML").build();
        REPLY_STRUCTURE = StructureBuilder.getBuilder().addVC("environmentXML").addVC("testINCListingXML")
                .addVC("testINCCoverageXML").addVC("testINCResultsXML").addDynamicList("programs", replyPrograms)
                .addStatusData().build();

    }

    /**
     * Build a record structure that represents a request submitted to the CCL unit testing framework. <br>
     * The build record structure looks like the following:
     *
     * <pre>
     *  record request
     * (
     *   1 testINCName = vc
     *   1 programs[*]
     *     2 programName = vc
     *     2 compile = i2
     *   1 optimizerMode = vc
     *   1 enforcePredeclare = i2
     * )
     * </pre>
     *
     * @param scriptNames
     *            The name of the scripts for which the tests will execute.
     * @param testIncludeName
     *            The name of the include test file to be executed.
     * @param compileScripts
     *            A {@code boolean} value to tell the unit testing framework whether or not the programs being tested
     *            should be compiled.
     * @param optimizerMode
     *            The Oracle optimizer mode to be used. If {@code null}, this value will remain unset in the request.
     * @param enforcePredeclare
     *            A {@code boolean}; if {@code true}, then the testing framework will be configured to enforce that all
     *            variables be declared. If {@code false}, then no such enforcement will be configured.
     * @param deprecatedFlag
     *            The deprecated value (E,W,L,I,D) to set when running the scripts.
     * @param testSubroutineName
     *            The optional name of the test subroutine to be executed. If {@code null}, then no test subroutine name
     *            will be set.
     * @return A {@link Record} object representing the request record structure.
     */
    public static Record createRequest(final List<String> scriptNames, final String testIncludeName,
            final boolean compileScripts, final String optimizerMode, final boolean enforcePredeclare,
            final String deprecatedFlag, final String testSubroutineName) {
        final Record request = buildRequest();

        final DynamicRecordList programs = request.getDynamicList("programs");

        for (final String scriptName : scriptNames) {
            final Record program = programs.addItem();
            program.setVC("programName", scriptName);
            program.setI2("compile", compileScripts);
        }

        request.setVC("testINCName", testIncludeName);

        if (optimizerMode != null)
            request.setVC("optimizerMode", optimizerMode);

        request.setI2("enforcePredeclare", enforcePredeclare);
        request.setVC("deprecatedFlag", deprecatedFlag);

        if (testSubroutineName != null)
            request.setVC("testSubroutineName", testSubroutineName);

        return request;
    }

    /**
     * Construct a reply record structure to hold data returned by the CCL unit testing framework.
     * <br>
     * The build record structure looks like the following:
     *
     * <pre>
     * record reply (
     *   1 environmentXML = vc     ;Contains the environment information
     *   1 testINCListingXML = vc  ;Listing for the compiled INC
     *   1 testINCCoverageXML = vc ;Coverage for the compiled INC
     *   1 testINCResultsXML = vc  ;The results of testINC
     *   1 programs[*]
     *     2 programName = vc
     *     2 listingXML = vc       ;The listing for the program
     *     2 coverageXML = vc      ;Code coverage for the program
     *   1 status_data
     *     2 status = c1
     *     2 subeventstatus[1]
     *       3 OperationName = c25
     *       3 OperationStatus = c1
     *       3 TargetObjectName = c25
     *       3 TargetObjectValue = vc
     * )
     * </pre>
     *
     * @return A {@link Record} object that represents the reply record structure.
     */
    public static Record createReply() {
        return buildReply();
    }

    /**
     * Create the request record structure.
     *
     * @return A {@link Record} object representing the request record structure.
     */
    private static Record buildRequest() {
        /*
         * Give the record a specific name to avoid name clashes in the CCLUnit framework
         */
        return RecordFactory.create("cclutRequest", REQUEST_STRUCTURE);
    }

    /**
     * Build the reply record structure.
     *
     * @return A {@link Record} object representing the reply record structure.
     */
    private static Record buildReply() {
        /*
         * Give the record a specific name to avoid name clashes in the CCLUnit framework
         */
        return RecordFactory.create("cclutReply", REPLY_STRUCTURE);
    }
}
