package com.cerner.ccl.parser.text.smoosh.internal;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.cerner.ccl.parser.AbstractUnitTest;
import com.cerner.ccl.parser.text.smoosh.IndexedSmoosher;

/**
 * Unit tests for {@link IndexedSmoosher} objects.
 * 
 * @author Joshua Hyde
 * 
 * @param <S>
 *            The type of {@link IndexedSmoosher} to be tested.
 */

public abstract class AbstractIndexedSmoosherUnitTest<S extends IndexedSmoosher<?>> extends AbstractUnitTest {
    /**
     * Testing a {@code null} for smooshability should fail.
     */
    @Test
    public void testCanSmooshNullLine() {
        expect(IllegalArgumentException.class);
        expect("Line cannot be null.");
        getSmoosher().canSmoosh(null);
    }

    /**
     * If an attempt is made to retrieve the ending index before smooshing has occurred, retrieval of the ending index
     * should fail.
     */
    @Test
    public void testGetEndingIndexNotSmooshed() {
        expect(IllegalStateException.class);
        expect("Smoosh has not been invoked on this object.");
        getSmoosher().getEndingIndex();
    }

    /**
     * Smooshing an empty list should fail.
     */
    @Test
    public void testSmooshEmptyList() {
        expect(IllegalArgumentException.class);
        expect("List cannot be empty.");
        getSmoosher().smoosh(0, Collections.<String> emptyList());
    }

    /**
     * Smooshing with a negative starting index should fail.
     */
    @Test
    public void testSmooshNegativeStartingIndex() {
        expect(IllegalArgumentException.class);
        expect("Starting index cannot be negative.");
        getSmoosher().smoosh(-1, Collections.singletonList("text"));
    }

    /**
     * Smooshing with a {@code null} list should fail.
     */
    @Test
    public void testSmooshNullList() {
        expect(IllegalArgumentException.class);
        expect("List cannot be null.");
        getSmoosher().smoosh(0, null);
    }

    /**
     * If the starting index is too high, fail out.
     */
    @Test
    public void testSmooshStartingIndexTooHigh() {
        final List<String> list = Collections.singletonList("text");
        final int startingIndex = list.size();
        expect(IllegalArgumentException.class);
        expect("Starting index exceeds list size; index = " + Integer.toString(startingIndex) + "; size = "
                + Integer.toString(list.size()));
        getSmoosher().smoosh(startingIndex, list);
    }

    /**
     * Get the smoosher to be tested.
     * 
     * @return The {@link IndexedSmoosher} to be tested.
     */
    protected abstract S getSmoosher();
}
