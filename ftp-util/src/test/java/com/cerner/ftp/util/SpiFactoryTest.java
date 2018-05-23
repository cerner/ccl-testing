package com.cerner.ftp.util;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.sql.Statement;

import org.junit.After;
import org.junit.Test;

import com.cerner.ftp.util.internal.MockSPI;
import com.cerner.ftp.util.internal.MockSpiImpl;

/**
 * Unit test for {@link SpiFactory}.
 *
 * @author Joshua Hyde
 *
 */

public class SpiFactoryTest {
    /**
     * Remove any overrides in {@link SpiFactory}.
     */
    @After
    public void tearDown() {
        SpiFactory.removeOverride(MockSPI.class);
    }

    /**
     * Test that mocked overrides will override META-INF/services
     */
    @Test
    public void testOverride() {
        final MockSPI mock = mock(MockSPI.class);
        SpiFactory.override(MockSPI.class, mock);
        assertThat(SpiFactory.getProvider(MockSPI.class)).isSameAs(mock);
    }

    /**
     * Test that, if the override is removed, the mock object is not returned.
     */
    @Test
    public void testRemoveOverride() {
        SpiFactory.override(MockSPI.class, mock(MockSPI.class));
        SpiFactory.removeOverride(MockSPI.class);
        assertThat(SpiFactory.getProvider(MockSPI.class)).isInstanceOf(MockSpiImpl.class);
    }

    /**
     * Test that a provider can be found using META-INF/services
     */
    @Test
    public void testGetProvider() {
        assertThat(SpiFactory.getProvider(MockSPI.class)).isInstanceOf(MockSpiImpl.class);
    }

    /**
     * Verify that, if no override and no META-INF/services file exists for a class, that the factory throws the correct
     * exception.
     */
    @Test(expected = IllegalStateException.class)
    public void testGetProviderNotFound() {
        SpiFactory.getProvider(Statement.class);
    }

}
