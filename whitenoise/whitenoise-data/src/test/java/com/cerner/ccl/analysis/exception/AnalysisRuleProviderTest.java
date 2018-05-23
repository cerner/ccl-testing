package com.cerner.ccl.analysis.exception;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Enumeration;
import java.util.Set;

import org.apache.commons.discovery.tools.Service;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.cerner.ccl.analysis.data.AnalysisRule;

/**
 * Unit tests for {@link AnalysisRuleProvider}.
 * 
 * @author Joshua Hyde
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { Service.class })
public class AnalysisRuleProviderTest {
    private final AnalysisRuleProvider provider = new AnalysisRuleProvider();

    /**
     * Test the retrieval of the rule set.
     */
    @Test
    public void testGetRules() {
        final AnalysisRule rule = mock(AnalysisRule.class);
        @SuppressWarnings("unchecked")
        final Enumeration<AnalysisRule> enumeration = mock(Enumeration.class);
        when(enumeration.hasMoreElements()).thenReturn(Boolean.TRUE, Boolean.FALSE);
        when(enumeration.nextElement()).thenReturn(rule);

        mockStatic(Service.class);
        when(Service.providers(AnalysisRule.class)).thenReturn(enumeration);

        final Set<AnalysisRule> rules = provider.getRules();
        assertThat(rules).containsOnly(rule);
        // Rules should be cached
        assertThat(rules).isSameAs(provider.getRules());
    }
}
