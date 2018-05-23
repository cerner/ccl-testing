package com.cerner.ccl.cdoc.velocity.structure;

import java.io.StringWriter;
import java.util.Random;

import org.apache.maven.reporting.MavenReportException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.cerner.ccl.parser.data.record.AbstractParentRecordStructureMember;
import com.cerner.ccl.parser.data.record.RecordStructureField;
import com.cerner.ccl.parser.data.record.RecordStructureMember;

/**
 * A {@link MemberFormatter} for {@link AbstractParentRecordStructureMember} objects.
 *
 * @author Joshua Hyde
 *
 */

public class ParentalMemberFormatter implements MemberFormatter<AbstractParentRecordStructureMember> {
    private final Random random = new Random();
    private final Template template;
    private final FieldFormatter fieldFormatter;

    /**
     * Get the template to be used to format a parental member.
     *
     * @param engine
     *            The {@link VelocityEngine} to be used to retrieve the template.
     * @return A {@link Template}.
     * @throws MavenReportException
     *             If any errors occur while attempting to read the template.
     */
    private static Template getTemplate(final VelocityEngine engine) throws MavenReportException {
        try {
            return engine.getTemplate("/velocity/record-structure-list-doc.vm", "utf-8");
        } catch (final Exception e) {
            throw new MavenReportException("Failed to initialize template.", e);
        }
    }

    /**
     * Create a parental member formatter.
     *
     * @param engine
     *            The {@link VelocityEngine} engine to be used to perform the formatting.
     * @throws MavenReportException
     *             If any errors occur during the instantiation.
     */
    public ParentalMemberFormatter(final VelocityEngine engine) throws MavenReportException {
        this(new FieldFormatter(engine), getTemplate(engine));
    }

    /**
     * Create a parental member formatter.
     *
     * @param formatter
     *            A {@link FieldFormatter} to be used to format the individual fields beneath a given parental member.
     * @param template
     *            A {@link Template} to be used in the formatting of a given parental member.
     * @throws IllegalArgumentException
     *             If the given formatter or template is {@code null}.
     */
    public ParentalMemberFormatter(final FieldFormatter formatter, final Template template) {
        if (formatter == null) {
            throw new IllegalArgumentException("Field formatter cannot be null.");
        }

        if (template == null) {
            throw new IllegalArgumentException("Template cannot be null.");
        }

        this.fieldFormatter = formatter;
        this.template = template;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String format(final AbstractParentRecordStructureMember member) throws MavenReportException {
        final VelocityContext context = new VelocityContext();
        context.put("member", member);
        // Needed for expand/collapse of details
        context.put("detailsDivName", member.getName() + random.nextInt());

        final StringWriter writer = new StringWriter();
        try {
            template.merge(context, writer);
        } catch (final Exception e) {
            throw new MavenReportException("Failed to merge template.", e);
        }
        final StringBuilder listBuilder = new StringBuilder(writer.toString());
        for (int i = 0, size = member.getChildMemberCount(); i < size; i++) {
            final RecordStructureMember childMember = member.getChildMember(i);
            if (childMember instanceof RecordStructureField) {
                listBuilder.append(fieldFormatter.format((RecordStructureField) childMember));
            } else {
                listBuilder.append(format((AbstractParentRecordStructureMember) childMember));
            }
        }

        return listBuilder.toString();
    }

}
