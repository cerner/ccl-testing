package com.cerner.ccl.cdoc.velocity.structure;

import org.apache.maven.reporting.MavenReportException;
import org.apache.velocity.app.VelocityEngine;

import com.cerner.ccl.parser.data.record.AbstractParentRecordStructureMember;
import com.cerner.ccl.parser.data.record.RecordInclude;
import com.cerner.ccl.parser.data.record.RecordStructure;
import com.cerner.ccl.parser.data.record.RecordStructureField;
import com.cerner.ccl.parser.data.record.RecordStructureMember;

/**
 * A formatter that will create HTML-formatted structures of record structures.
 *
 * @author Joshua Hyde
 *
 */

public class RecordStructureFormatter {
    private final FieldFormatter fieldFormatter;
    private final ParentalMemberFormatter listFormatter;
    private final IncludeFormatter includeFormatter;

    /**
     * Create a structure formatter.
     *
     * @param engine
     *            The {@link VelocityEngine} to be used to drive the formatting.
     * @throws MavenReportException
     *             If any errors occur during the instanitation.
     */
    public RecordStructureFormatter(final VelocityEngine engine) throws MavenReportException {
        this(new FieldFormatter(engine), new ParentalMemberFormatter(engine), new IncludeFormatter(engine));
    }

    /**
     * Create a structure formatter.
     *
     * @param fieldFormatter
     *            A {@link FieldFormatter}.
     * @param listFormatter
     *            A {@link ParentalMemberFormatter}.
     * @param includeFormatter
     *            An {@link IncludeFormatter}.
     * @throws IllegalArgumentException
     *             If any of the given objects are {@code null}.
     */
    public RecordStructureFormatter(final FieldFormatter fieldFormatter, final ParentalMemberFormatter listFormatter,
            final IncludeFormatter includeFormatter) {
        if (fieldFormatter == null) {
            throw new IllegalArgumentException("Field formatter cannot be null.");
        }

        if (listFormatter == null) {
            throw new IllegalArgumentException("List formatter cannot be null.");
        }

        if (includeFormatter == null) {
            throw new IllegalArgumentException("Include formatter cannot be null.");
        }

        this.fieldFormatter = fieldFormatter;
        this.listFormatter = listFormatter;
        this.includeFormatter = includeFormatter;
    }

    /**
     * Format a record structure.
     *
     * @param structure
     *            A {@link RecordStructure} to be formatted.
     * @return A {@link String} object representing the formatted structure of the record structure.
     * @throws IllegalArgumentException
     *             If the given structure is {@code null}.
     * @throws MavenReportException
     *             If any errors occur during the formatting.
     */
    public String format(final RecordStructure structure) throws MavenReportException {
        if (structure == null) {
            throw new IllegalArgumentException("Record structure cannot be null.");
        }

        final StringBuilder definitionBuilder = new StringBuilder();
        for (int i = 0, size = structure.getRootLevelMemberCount(); i < size; i++) {
            final RecordStructureMember member = structure.getRootLevelMember(i);
            if (member instanceof RecordStructureField) {
                definitionBuilder.append(fieldFormatter.format((RecordStructureField) member));
            } else if (member instanceof AbstractParentRecordStructureMember) {
                definitionBuilder.append(listFormatter.format((AbstractParentRecordStructureMember) member));
            } else {
                definitionBuilder.append(includeFormatter.format((RecordInclude) member));
            }
        }

        return definitionBuilder.toString();
    }
}
