package com.cerner.ftp.instrument;

import java.util.List;

import etm.core.configuration.EtmManager;
import etm.core.metadata.EtmMonitorMetaData;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import etm.core.plugin.EtmPlugin;
import etm.core.renderer.MeasurementRenderer;

/**
 * A factory to create {@link EtmMonitor} objects. This is to help eliminate annoying error messages when the monitor is
 * used to create points and no monitor has been started.
 *
 * @author Joshua Hyde
 *
 */

public class EtmMonitorFactory {
    private static final EtmMonitor NO_OP_MONITOR = new NoOpEtmMonitor();

    /**
     * Create a JETM monitor.
     *
     * @return An {@link EtmMonitor} object.
     */
    public static EtmMonitor getEtmMonitor() {
        final EtmMonitor monitor = EtmManager.getEtmMonitor();
        if (monitor.isStarted())
            return monitor;

        return NO_OP_MONITOR;
    }

    /**
     * An implementation of {@link EtmPoint} that does nothing.
     *
     * @author Joshua Hyde
     *
     */
    private static class NoOpEtmPoint implements EtmPoint {

        public NoOpEtmPoint() {
        }

        public void alterName(final String newName) {
        }

        public void collect() {
        }

        public long getEndTime() {
            return 0;
        }

        public String getName() {
            return null;
        }

        public EtmPoint getParent() {
            return null;
        }

        public long getStartTime() {
            return 0;
        }

        public long getStartTimeMillis() {
            return 0;
        }

        public long getTicks() {
            return 0;
        }

        public double getTransactionTime() {
            return 0;
        }
    }

    /**
     * An implementation of {@link EtmMonitor} that does nothing.
     *
     * @author Joshua Hyde
     *
     */
    private static class NoOpEtmMonitor implements EtmMonitor {
        private static final EtmPoint NO_OP_POINT = new NoOpEtmPoint();

        public NoOpEtmMonitor() {
        }

        public void addPlugin(final EtmPlugin aEtmPlugin) {
        }

        public void aggregate() {
        }

        public EtmPoint createPoint(final String symbolicName) {
            return NO_OP_POINT;
        }

        public void disableCollection() {
        }

        public void enableCollection() {
        }

        public EtmMonitorMetaData getMetaData() {
            return null;
        }

        public boolean isCollecting() {
            return false;
        }

        public boolean isStarted() {
            return false;
        }

        public void render(final MeasurementRenderer renderer) {
        }

        public void reset() {
        }

        public void reset(final String symbolicName) {
        }

        @SuppressWarnings("rawtypes")
        public void setPlugins(final List plugins) {
        }

        public void start() {
        }

        public void stop() {
        }
    }
}
