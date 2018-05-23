package com.cerner.ccl.j4ccl.impl.adders;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.io.File;

import org.junit.Test;

import com.cerner.ccl.j4ccl.internal.AbstractUnitTest;

/**
 * Unit tests for {@link AbstractCompilerAdder}.
 *
 * @author Joshua Hyde
 *
 */

public class AbstractCompilerAdderTest extends AbstractUnitTest {
    private final ConcreteAdder adder = new ConcreteAdder();

    /**
     * Test the addition of a dependency.
     */
    @Test
    public void testAddDependency() {
        final File dependency = mock(File.class);
        adder.addDependency(dependency);
        assertThat(adder.getDependencies()).containsOnly(dependency);
    }

    /**
     * Adding a {@code null} dependency should fail.
     */
    @Test
    public void testAddDependencyNullFile() {
        expect(NullPointerException.class);
        expect("Dependent file cannot be a null reference.");
        adder.addDependency(null);
    }

    /**
     * Test the determination of whether or not the listing location is defined.
     */
    @Test
    public void testHasListingLocationDefined() {
        final File listingLocation = mock(File.class);
        assertThat(adder.hasListingLocationDefined()).isFalse();
        adder.setListingLocation(listingLocation);
        assertThat(adder.hasListingLocationDefined()).isTrue();
    }

    /**
     * Test the setting of the listing location.
     */
    @Test
    public void testSetListingLocation() {
        final File listingLocation = mock(File.class);
        adder.setListingLocation(listingLocation);
        assertThat(adder.getListingLocation()).isEqualTo(listingLocation);
    }

    /**
     * The setting of the listing location to {@code null} should fail.
     */
    @Test
    public void testSetListingLocationNullFile() {
        expect(NullPointerException.class);
        expect("Listing location cannot be null.");
        adder.setListingLocation(null);
    }

    /**
     * Concrete implementation of {@link AbstractCompilerAdder} for testing.
     *
     * @author Joshua Hyde
     *
     */
    private static class ConcreteAdder extends AbstractCompilerAdder {
        public ConcreteAdder() {
        }
    }
}
