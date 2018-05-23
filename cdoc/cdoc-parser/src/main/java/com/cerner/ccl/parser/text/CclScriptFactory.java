package com.cerner.ccl.parser.text;

import java.util.List;

import com.cerner.ccl.parser.data.CclScript;
import com.cerner.ccl.parser.data.ScriptDocumentation;
import com.cerner.ccl.parser.data.record.RecordStructure;
import com.cerner.ccl.parser.data.subroutine.Subroutine;

/**
 * A factory used to create {@link CclScript} objects.
 * 
 * @author Joshua Hyde
 * 
 */

public class CclScriptFactory implements DocumentedObjectFactory<CclScript, ScriptDocumentation> {
    /**
     * {@inheritDoc}
     */
    @Override
    public CclScript build(final String objectName, final ScriptDocumentation topLevelDocumentation,
            final List<Subroutine> subroutines, final List<RecordStructure> recordStructures) {
        return new CclScript(objectName,
                topLevelDocumentation == null ? new ScriptDocumentation() : topLevelDocumentation, subroutines,
                recordStructures);
    }

}
