package com.cerner.ccl.cdoc.mojo.data;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;

/**
 * Unit tests for {@link DocumentationNameComparator}.
 * 
 * @author Joshua Hyde
 * 
 */

public class DocumentationNameComparatorTest {
    private final DocumentationNameComparator comparator = new DocumentationNameComparator();

    /**
     * Test the comparison of two documentation objects.
     */
    @Test
    public void testCompare() {
        final String objectNameA = "objectNameA";
        final Documentation docA = mock(Documentation.class);
        when(docA.getObjectName()).thenReturn(objectNameA);

        final String objectNameB = "objectNameB";
        final Documentation docB = mock(Documentation.class);
        when(docB.getObjectName()).thenReturn(objectNameB);

        assertThat(comparator.compare(docA, docB)).isEqualTo(objectNameA.compareToIgnoreCase(objectNameB));
    }
}
