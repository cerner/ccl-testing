package com.cerner.ccl.cdoc.velocity;

import java.io.File;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

import com.cerner.ccl.cdoc.script.ScriptExecutionDetails;
import com.cerner.ccl.cdoc.velocity.navigation.Navigation;
import com.cerner.ccl.parser.data.CclScript;
import com.cerner.ccl.parser.data.record.RecordStructure;

/**
 * This bean generates CDoc for a CCL script.
 *
 * @author Joshua Hyde
 *
 */

public class ScriptDocGenerator extends AbstractSourceDocumentationGenerator<CclScript> {
    private final CclScript cclScript;

    /**
     * Create a script documentation generator.
     *
     * @param cclScript
     *            A {@link CclScript} object for which the documentation is to be generated.
     * @param cssDirectory
     *            A {@link File} representing the directory to which any CSS files should be written.
     * @param details
     *            A {@link ScriptExecutionDetails} object.
     * @param writer
     *            The {@link Writer} to which the output is to be written.
     * @param backNavigation
     *            A {@link Navigation} object representing the anchor text and destination for the "back" link in the
     *            generated report.
     * @throws IllegalArgumentException
     *             If the given CCL script is {@code null}.
     */
    public ScriptDocGenerator(final CclScript cclScript, final File cssDirectory, final ScriptExecutionDetails details,
            final Writer writer, final Navigation backNavigation) {
        super(writer, cssDirectory, details, backNavigation);

        if (cclScript == null) {
            throw new IllegalArgumentException("CCL script cannot be null.");
        }

        this.cclScript = cclScript;
    }

    @Override
    protected CclScript getObject() {
        return cclScript;
    }

    @Override
    protected String getObjectFilename() {
        return getObject().getName().toLowerCase(Locale.US) + ".prg";
    }

    @Override
    protected String getObjectName() {
        return getObject().getName();
    }

    @Override
    protected List<RecordStructure> getRecordStructures() {
        return getObject().getRecordStructures();
    }
}
