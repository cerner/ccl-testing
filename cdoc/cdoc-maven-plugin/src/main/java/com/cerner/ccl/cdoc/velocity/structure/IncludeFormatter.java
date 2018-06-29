package com.cerner.ccl.cdoc.velocity.structure;

import java.io.StringWriter;

import org.apache.maven.reporting.MavenReportException;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import com.cerner.ccl.parser.data.record.RecordInclude;

/**
 * A formatter for {@link RecordInclude} objects.
 *
 * @author Joshua Hyde
 *
 */

public class IncludeFormatter implements MemberFormatter<RecordInclude> {
    private final Template template;

    /**
     * Create an include formatter.
     *
     * @param engine
     *            A {@link VelocityEngine} object used to perform the formatting.
     * @throws IllegalArgumentException
     *             If the given engine is {@code null}.
     * @throws MavenReportException
     *             If any errors occur while setting up this object.
     */
    public IncludeFormatter(final VelocityEngine engine) throws MavenReportException {
        if (engine == null) {
            throw new IllegalArgumentException("Engine cannot be null.");
        }

        try {
            this.template = engine.getTemplate("/velocity/record-structure-include-doc.vm", "utf-8");
        } catch (final Exception e) {
            throw new MavenReportException("Failed to initialize template.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String format(final RecordInclude member) throws MavenReportException {
        final VelocityContext context = new VelocityContext();
        context.put("member", member);

        final StringWriter writer = new StringWriter();
        try {
            template.merge(context, writer);
        } catch (final Exception e) {
            throw new MavenReportException("Failed to merge template for member " + member.getName(), e);
        }
        return writer.toString();
    }

}
