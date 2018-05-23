package com.cerner.ccl.cdoc.velocity.structure;

import java.io.StringWriter;
import java.util.Random;

import org.apache.maven.reporting.MavenReportException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.cerner.ccl.parser.data.record.RecordStructureField;

/**
 * A {@link MemberFormatter} used to format {@link RecordStructureField} objects.
 *
 * @author Joshua Hyde
 *
 */

public class FieldFormatter implements MemberFormatter<RecordStructureField> {
    private final Random random = new Random();
    private final Template template;

    /**
     * Create a formatter.
     *
     * @param engine
     *            The {@link VelocityEngine} to be used to perform the formatting.
     * @throws IllegalArgumentException
     *             If the given engine is {@code null}.
     * @throws MavenReportException
     *             If any errors occur during instantiation.
     */
    public FieldFormatter(final VelocityEngine engine) throws MavenReportException {
        if (engine == null) {
            throw new IllegalArgumentException("Engine cannot be null.");
        }

        try {
            this.template = engine.getTemplate("/velocity/record-structure-member-doc.vm", "utf-8");
        } catch (final Exception e) {
            throw new MavenReportException("Failed to initialize template.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String format(final RecordStructureField member) throws MavenReportException {
        final VelocityContext context = new VelocityContext();
        context.put("member", member);
        context.put("detailsDivName", member.getName() + random.nextInt());

        final StringWriter writer = new StringWriter();
        try {
            template.merge(context, writer);
        } catch (final Exception e) {
            throw new MavenReportException("Failed to merge template for member " + member.getName(), e);
        }
        return writer.toString();
    }
}
