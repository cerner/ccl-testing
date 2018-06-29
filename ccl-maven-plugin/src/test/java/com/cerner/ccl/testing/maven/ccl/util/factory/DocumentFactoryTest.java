package com.cerner.ccl.testing.maven.ccl.util.factory;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.cerner.ccl.testing.maven.ccl.util.factory.DocumentFactory;

/**
 * Unit tests for {@link DocumentFactory}.
 *
 * @author Joshua Hyde
 *
 */

public class DocumentFactoryTest {
    /**
     * If given bad XML, the XML string should be contained in the returned message.
     */
    @Test
    public void testCreateBadXml() {
        final String badXml = "<i am bad xml";

        RuntimeException caught = null;
        try {
            DocumentFactory.create(badXml);
        } catch (final RuntimeException e) {
            caught = e;
        }

        assertThat(caught).isNotNull();
        assertThat(caught.getMessage()).contains(badXml);
    }
}
