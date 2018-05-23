package com.cerner.ccl.parser.text;

import java.util.List;

import com.cerner.ccl.parser.data.IncludeDocumentation;
import com.cerner.ccl.parser.data.IncludeFile;
import com.cerner.ccl.parser.data.record.RecordStructure;
import com.cerner.ccl.parser.data.subroutine.Subroutine;

/**
 * A {2link {@link DocumentedObjectFactory} used to create {@link IncludeFile} objects.
 * 
 * @author Joshua Hyde
 * 
 */

public class IncludeFileFactory implements DocumentedObjectFactory<IncludeFile, IncludeDocumentation> {
    /**
     * {@inheritDoc}
     */
    @Override
    public IncludeFile build(final String objectName, final IncludeDocumentation topLevelDocumentation,
            final List<Subroutine> subroutines, final List<RecordStructure> recordStructures) {
        return new IncludeFile(objectName,
                topLevelDocumentation == null ? new IncludeDocumentation() : topLevelDocumentation, subroutines,
                recordStructures);
    }

}
