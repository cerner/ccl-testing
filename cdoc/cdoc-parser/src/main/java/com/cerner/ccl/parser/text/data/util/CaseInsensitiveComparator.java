package com.cerner.ccl.parser.text.data.util;

import java.io.Serializable;
import java.util.Comparator;

/**
 * A {@link Comparator} of {@link String} objects that compares them case-insensitively.
 * 
 * @author Joshua Hyde
 * 
 */

public class CaseInsensitiveComparator implements Comparator<String>, Serializable {
    private static final long serialVersionUID = -4203476005628558075L;
    private static final CaseInsensitiveComparator INSTANCE = new CaseInsensitiveComparator();

    /**
     * Get the singleton instance of this comparator.
     * 
     * @return A {@link CaseInsensitiveComparator}.
     */
    public static CaseInsensitiveComparator getInstance() {
        return INSTANCE;
    }

    /**
     * Private constructor to prevent instantiation.
     */
    private CaseInsensitiveComparator() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final String o1, final String o2) {
        return o1.compareToIgnoreCase(o2);
    }

}
