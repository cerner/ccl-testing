package com.cerner.ccl.cdoc.velocity;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

/**
 * Unit tests for {@link AbstractVelocityGenerator}.
 *
 * @author Joshua Hyde
 *
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest(value = { AbstractVelocityGenerator.class, VelocityEngine.class })
public class AbstractVelocityGeneratorTest {
    /**
     * Test the retrieval of the engine.
     *
     * @throws Exception
     *             If any errors occur during the test run.
     */
    @Test
    public void testGetEngine() throws Exception {
        final VelocityEngine engine = mock(VelocityEngine.class);
        whenNew(VelocityEngine.class).withNoArguments().thenReturn(engine);

        final ConcreteGenerator generator = new ConcreteGenerator();
        assertThat(generator.getEngine()).isEqualTo(engine);

        verify(engine).setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        verify(engine).setProperty("classpath.resource.loader.class", ClassResourceLoader.class.getName());
        verify(engine).setProperty("runtime.log.logsystem.class", NullLogChute.class.getCanonicalName());
        verify(engine).init();
    }

    /**
     * Concrete generator for testing.
     *
     * @author Joshua Hyde
     *
     */
    private static class ConcreteGenerator extends AbstractVelocityGenerator {
        public ConcreteGenerator() {
        }
    }
}
