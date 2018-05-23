package com.cerner.ccl.parser.text;

import java.util.List;

import com.cerner.ccl.parser.data.Described;
import com.cerner.ccl.parser.data.record.RecordStructure;
import com.cerner.ccl.parser.data.subroutine.Subroutine;

/**
 * A factory used to produce documented objects.
 * 
 * @author Joshua Hyde
 * 
 * @param <T>
 *            The type of object to be constructed.
 * @param <D>
 *            The type of top-level documentation object to describe the object.
 */

public interface DocumentedObjectFactory<T, D extends Described> {
    /**
     * Build an object.
     * 
     * @param objectName
     *            The name of the object to be built.
     * @param topLevelDocumentation
     *            A {@link Described} object representing the top-level documentation of the object.
     * @param subroutines
     *            A {@link List} of {@link Subroutine} objects representing the subroutines defined within the object.
     * @param recordStructures
     *            A {@link List} of {@link RecordStructure} objects representing the record structures defined within
     *            the object.
     * @return A {@code T} object built out of the given data.
     */
    T build(String objectName, D topLevelDocumentation, List<Subroutine> subroutines,
            List<RecordStructure> recordStructures);
}
