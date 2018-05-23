package com.cerner.ccl.cdoc.mojo.data;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A {@link Comparator} that compares two {@link Documentation} objects by their object names, case-insensitively.
 * 
 * @author Joshua Hyde
 * 
 */

public class DocumentationNameComparator implements Comparator<Documentation>, Serializable {
    private static final long serialVersionUID = -3111112658501787968L;

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final Documentation o1, final Documentation o2) {
        return o1.getObjectName().compareToIgnoreCase(o2.getObjectName());
    }

}
