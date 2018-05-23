package com.cerner.ccl.analysis.core.rules;

import java.util.Set;

import org.jdom2.Document;
import org.jdom2.JDOMException;

import com.cerner.ccl.analysis.data.Violation;
import com.cerner.ccl.analysis.jdom.JdomAnalysisRule.Delegate;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * An extension of {@link Delegate} that handles the measurement of the start and end of a rule's {@link #analyze()
 * analysis}.
 *
 * @author Joshua Hyde
 *
 */

public abstract class TimedDelegate extends Delegate {

    /**
     * @param document
     *            The {@link Document} representing the XML representation for the CCL script to be analyzed.
     */
    public TimedDelegate(final Document document) {
        super(document);
    }

    @Override
    protected Set<Violation> analyze() throws JDOMException {
        final EtmPoint point = PointFactory.getPoint(getClass(), "analyze");
        try {
            return doMeasuredAnalysis();
        } finally {
            point.collect();
        }
    }

    /**
     * Perform the actual work of analysis within the context of a {@link EtmPoint} measurement actively running.
     *
     * @return A {@link Set} of {@link Violation} objects to be added to the sum of all delegates' reported violations.
     * @throws JDOMException
     *             If any errors occur during the analysis of the XML.
     */
    protected abstract Set<Violation> doMeasuredAnalysis() throws JDOMException;

}
