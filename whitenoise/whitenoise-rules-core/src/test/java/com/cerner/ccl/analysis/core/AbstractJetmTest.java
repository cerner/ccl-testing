package com.cerner.ccl.analysis.core;

import java.io.File;
import java.io.FileWriter;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;

import com.google.code.jetm.reporting.BindingMeasurementRenderer;
import com.google.code.jetm.reporting.xml.XmlAggregateBinder;

import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;

/**
 * Skeleton definition of a class used to collect JETM timings.
 *
 * @author Joshua Hyde
 *
 */

public abstract class AbstractJetmTest {
    /**
     * A {@link Rule} used to retrieve the current test name.
     */
    @Rule
    public TestName testName = new TestName();

    private EtmMonitor monitor;

    /**
     * Configure and start the JETM monitor.
     */
    @BeforeClass
    public static void configureJetm() {
        BasicEtmConfigurator.configure();
    }

    /**
     * Start the timer for each test.
     */
    @Before
    public void startMonitor() {
        monitor = EtmManager.getEtmMonitor();
        monitor.start();
    }

    /**
     * Write out the results of all of the test runs.
     *
     * @throws Exception
     *             If any errors occur during the write-out.
     */
    @After
    public void recordTimings() throws Exception {
        if (monitor != null) {
            monitor.stop();

            final File timingDirectory = new File("target/jetm");
            FileUtils.forceMkdir(timingDirectory);

            final File timingFile = new File(timingDirectory,
                    getClass().getSimpleName() + "." + getTestName() + ".xml");
            final FileWriter writer = new FileWriter(timingFile);
            try {
                monitor.render(new BindingMeasurementRenderer(new XmlAggregateBinder(), writer));
            } finally {
                writer.close();
            }

            monitor.reset();
        }
    }

    /**
     * Get the name of the current test.
     * 
     * @return The name of the current test.
     */
    protected String getTestName() {
        return testName.getMethodName();
    }
}
