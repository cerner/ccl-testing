package com.cerner.ccl.cdoc.velocity;

import java.io.File;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

import com.cerner.ccl.cdoc.script.ScriptExecutionDetails;
import com.cerner.ccl.cdoc.velocity.navigation.Navigation;
import com.cerner.ccl.parser.data.IncludeFile;
import com.cerner.ccl.parser.data.record.RecordStructure;

/**
 * An object to generate documentation for an include file.
 *
 * @author Joshua Hyde
 *
 */

public class IncludeDocGenerator extends AbstractSourceDocumentationGenerator<IncludeFile> {
    private final IncludeFile include;

    /**
     * Create an include file documentation generator.
     *
     * @param include
     *            An {@link IncludeFile} object representing the include file to be documented.
     * @param cssDirectory
     *            A {@link File} representing the directory to which any CSS files should be written.
     * @param details
     *            A {@link ScriptExecutionDetails} object.
     * @param writer
     *            A {@link Writer} to which the documentation should be written.
     * @param backNavigation
     *            A {@link Navigation} object representing the anchor text and destination for the "back" link in the
     *            generated report.
     * @throws IllegalArgumentException
     *             If the given include file object is {@code null}.
     */
    public IncludeDocGenerator(final IncludeFile include, final File cssDirectory, final ScriptExecutionDetails details,
            final Writer writer, final Navigation backNavigation) {
        super(writer, cssDirectory, details, backNavigation);

        if (include == null) {
            throw new IllegalArgumentException("Include file cannot be null.");
        }

        this.include = include;
    }

    @Override
    protected IncludeFile getObject() {
        return include;
    }

    @Override
    protected String getObjectFilename() {
        return getObject().getName().toLowerCase(Locale.US);
    }

    @Override
    protected String getObjectName() {
        return getObjectFilename();
    }

    @Override
    protected List<RecordStructure> getRecordStructures() {
        return getObject().getRecordStructures();
    }
}
