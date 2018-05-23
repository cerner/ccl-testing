package com.cerner.ccl.j4ccl;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import java.util.Enumeration;

import org.apache.commons.discovery.tools.Service;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit test of {@link CclExecutor}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = Service.class)
public class CclExecutorTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

    /**
     * Test the retrieval of service providers.
     */
    @Test
    public void testGetExecutor() {
        final CclExecutor executor = mock(CclExecutor.class);

        @SuppressWarnings("unchecked")
        final Enumeration<CclExecutor> executorEnum = mock(Enumeration.class);
        when(executorEnum.hasMoreElements()).thenReturn(Boolean.TRUE);
        when(executorEnum.nextElement()).thenReturn(executor);

        mockStatic(Service.class);
        when(Service.providers(CclExecutor.class)).thenReturn(executorEnum);

        assertThat(CclExecutor.getExecutor()).isEqualTo(executor);
    }

    /**
     * Getting an executor should fail if there are no providers available.
     */
    @Test
    public void testGetExecutorNoProviders() {
        @SuppressWarnings("unchecked")
        final Enumeration<CclExecutor> executorEnum = mock(Enumeration.class);
        when(executorEnum.hasMoreElements()).thenReturn(Boolean.FALSE);

        mockStatic(Service.class);
        when(Service.providers(CclExecutor.class)).thenReturn(executorEnum);

        expected.expect(IllegalStateException.class);
        expected.expectMessage("No implementations found of: " + CclExecutor.class.getName());
        CclExecutor.getExecutor();
    }
}
