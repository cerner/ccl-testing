package com.cerner.ccl.parser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.google.code.jetm.reporting.BindingMeasurementRenderer;
import com.google.code.jetm.reporting.xml.XmlAggregateBinder;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;

/**
 * A utility class to assist in the writing of JETM timing data.
 * 
 * @author Joshua Hyde
 * 
 */

public class TimingsWriter {
    /**
     * Create and start a monitor.
     * 
     * @return An {@link EtmMonitor}.
     */
    public static EtmMonitor startMonitor() {
        BasicEtmConfigurator.configure();

        final EtmMonitor monitor = EtmManager.getEtmMonitor();
        monitor.start();
        return monitor;
    }

    /**
     * Writing timings to disk.
     * 
     * @param monitor
     *            The {@link EtmMonitor} carrying the timing data to be written.
     * @param owningClass
     *            The {@link Class} of the class that owns the timing data.
     * @throws IOException
     *             If any errors occur during the writing out of data.
     */
    public static void writeTimings(final EtmMonitor monitor, final Class<?> owningClass) throws IOException {
        final File timingDirectory = new File("target/jetm");
        FileUtils.forceMkdir(timingDirectory);

        final File timingFile = new File(timingDirectory, owningClass.getSimpleName() + ".xml");
        final FileWriter writer = new FileWriter(timingFile);
        try {
            monitor.render(new BindingMeasurementRenderer(new XmlAggregateBinder(), writer));
        } finally {
            writer.close();
        }
    }
}
