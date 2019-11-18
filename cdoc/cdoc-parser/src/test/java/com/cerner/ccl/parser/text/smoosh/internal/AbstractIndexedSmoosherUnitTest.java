package com.cerner.ccl.parser.text.smoosh.internal;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.cerner.ccl.parser.text.smoosh.IndexedSmoosher;

/**
 * Unit tests for {@link IndexedSmoosher} objects.
 *
 * @author Joshua Hyde
 *
 * @param <S>
 *            The type of {@link IndexedSmoosher} to be tested.
 */

public abstract class AbstractIndexedSmoosherUnitTest<S extends IndexedSmoosher<?>> {
    /**
     * Testing a {@code null} for smooshability should fail.
     */
    @Test
    public void testCanSmooshNullLine() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            getSmoosher().canSmoosh(null);
        });
        assertThat(e.getMessage()).isEqualTo("Line cannot be null.");
    }

    /**
     * If an attempt is made to retrieve the ending index before smooshing has occurred, retrieval of the ending index
     * should fail.
     */
    @Test
    public void testGetEndingIndexNotSmooshed() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            getSmoosher().getEndingIndex();
        });
        assertThat(e.getMessage()).isEqualTo("Smoosh has not been invoked on this object.");
    }

    /**
     * Smooshing an empty list should fail.
     */
    @Test
    public void testSmooshEmptyList() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            getSmoosher().smoosh(0, Collections.<String> emptyList());
        });
        assertThat(e.getMessage()).isEqualTo("List cannot be empty.");
    }

    /**
     * Smooshing with a negative starting index should fail.
     */
    @Test
    public void testSmooshNegativeStartingIndex() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            getSmoosher().smoosh(-1, Collections.singletonList("text"));
        });
        assertThat(e.getMessage()).isEqualTo("Starting index cannot be negative.");
    }

    /**
     * Smooshing with a {@code null} list should fail.
     */
    @Test
    public void testSmooshNullList() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            getSmoosher().smoosh(0, null);
        });
        assertThat(e.getMessage()).isEqualTo("List cannot be null.");
    }

    /**
     * If the starting index is too high, fail out.
     */
    @Test
    public void testSmooshStartingIndexTooHigh() {
        final List<String> list = Collections.singletonList("text");
        final int startingIndex = list.size();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            getSmoosher().smoosh(startingIndex, list);
        });
        assertThat(e.getMessage()).isEqualTo("Starting index exceeds list size; index = "
                + Integer.toString(startingIndex) + "; size = " + Integer.toString(list.size()));
    }

    /**
     * Get the smoosher to be tested.
     *
     * @return The {@link IndexedSmoosher} to be tested.
     */
    protected abstract S getSmoosher();
}
