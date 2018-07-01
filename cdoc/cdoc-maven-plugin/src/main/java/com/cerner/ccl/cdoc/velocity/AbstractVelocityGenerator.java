package com.cerner.ccl.cdoc.velocity;

import org.apache.maven.reporting.MavenReportException;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;

/**
 * Skeleton definition of an object used to generate a report using Apache Velocity.
 *
 * @author Joshua Hyde
 *
 */

public abstract class AbstractVelocityGenerator {
    /**
     * Get the Velocity engine used to generate the report.
     *
     * @return A {@link VelocityEngine} used to generate a report.
     * @throws MavenReportException
     *             If any errors occur while setting up the engine.
     */
    protected VelocityEngine getEngine() throws MavenReportException {
        final VelocityEngine engine = new VelocityEngine();
        engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        engine.setProperty("classpath.resource.loader.class", ClassResourceLoader.class.getName());
        engine.setProperty("runtime.log.logsystem.class", NullLogChute.class.getCanonicalName());
        try {
            engine.init();
        } catch (final Exception e) {
            throw new MavenReportException("Failed to initialize Velocity engine.", e);
        }
        return engine;
    }
}
