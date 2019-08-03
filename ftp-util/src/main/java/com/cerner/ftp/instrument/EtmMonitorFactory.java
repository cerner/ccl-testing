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
        if (monitor.isStarted()) {
			return monitor;
		}

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

		@Override
		public void alterName(final String newName) {
        }

		@Override
		public void collect() {
        }

		@Override
		public long getEndTime() {
            return 0;
        }

		@Override
		public String getName() {
            return null;
        }

		@Override
		public EtmPoint getParent() {
            return null;
        }

		@Override
		public long getStartTime() {
            return 0;
        }

		@Override
		public long getStartTimeMillis() {
            return 0;
        }

		@Override
		public long getTicks() {
            return 0;
        }

		@Override
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

		@Override
		public void addPlugin(final EtmPlugin aEtmPlugin) {
        }

		@Override
		public void aggregate() {
        }

		@Override
		public EtmPoint createPoint(final String symbolicName) {
            return NO_OP_POINT;
        }

		@Override
		public void disableCollection() {
        }

		@Override
		public void enableCollection() {
        }

		@Override
		public EtmMonitorMetaData getMetaData() {
            return null;
        }

		@Override
		public boolean isCollecting() {
            return false;
        }

		@Override
		public boolean isStarted() {
            return false;
        }

		@Override
		public void render(final MeasurementRenderer renderer) {
        }

		@Override
		public void reset() {
        }

		@Override
		public void reset(final String symbolicName) {
        }

		@Override
		@SuppressWarnings("rawtypes")
        public void setPlugins(final List plugins) {
        }

		@Override
		public void start() {
        }

		@Override
		public void stop() {
        }
    }
}
