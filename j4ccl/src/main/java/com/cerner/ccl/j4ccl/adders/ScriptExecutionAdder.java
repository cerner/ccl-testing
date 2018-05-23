package com.cerner.ccl.j4ccl.adders;

import com.cerner.ccl.j4ccl.adders.arguments.Argument;
import com.cerner.ccl.j4ccl.record.Record;

/**
 * An adder to commit a step to execute a specified script.
 *
 * @author Joshua Hyde
 *
 */

public interface ScriptExecutionAdder extends Adder {
    /**
     * Specify a record structure that should be defined and used by the script to be executed.
     *
     * @param recordName
     *            The name of the record whose references within the script are to be replaced by references to the
     *            given record structure.
     * @param record
     *            A {@link Record} object representing the record structure to be defined and used in place of the given
     *            record structure name.
     * @return This object.
     * @throws IllegalArgumentException
     *             If the given record name is blank.
     * @throws NullPointerException
     *             If the given record name or record is {@code null}.
     */
    ScriptExecutionAdder withReplace(String recordName, Record record);

    /**
     * Specify any command-line arguments to be used in the execution of the script.
     * <br>
     * This is not a cumulative method. Multiple calls to this method will overwrite any previously-set arguments.
     *
     * @param arguments
     *            A vararg array of {@link Argument} objects representing the arguments to be used in the script
     *            execution.
     * @return This object.
     * @throws IllegalArgumentException
     *             If the given vararg array is of zero length of contains {@code null}.
     */
    ScriptExecutionAdder withArguments(Argument... arguments);

    /**
     * Indicate whether the domain credentials should be used to authenticate the CCL session before running the script.
     * The system will not validate the credentials. The script should do that if it is important.
     * <br>
     * This is not a cumulative method. Multiple calls to this method will overwrite any previously-set value.
     *
     * @param authenticate
     *            A boolean flag indicating whether of not to authenticate using the domain credentials.
     * @return This object.
     */
    ScriptExecutionAdder withAuthentication(boolean authenticate);
}
