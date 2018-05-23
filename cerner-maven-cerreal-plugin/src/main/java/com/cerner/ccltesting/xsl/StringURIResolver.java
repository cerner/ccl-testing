package com.cerner.ccltesting.xsl;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

/**
 * A {@link URIResolver} used to track resources and create stream references. It uses a concatenation of the base URI and {@code href} attribute to identify resources.
 * 
 * @author Jeff Wiedemann
 * 
 */

public class StringURIResolver implements URIResolver {
    private final Map<String, String> resources = new HashMap<String, String>();

    /**
     * {@inheritDoc}
     */
    public Source resolve(String hRef, String base) throws TransformerException {
        final String resource = resources.get(base + hRef);

        if (resource == null)
            throw new TransformerException("Failed to resolve [" + base + hRef + "] to a valid resource");

        return new StreamSource(new StringReader(resource));
    }

    /**
     * Add a resource.
     * 
     * @param base
     *            An href attribute, which may be relative or absolute.
     * @param hRef
     *            An href attribute, which may be relative or absolute.
     * @param resource
     *            The location of the resource to be able to be resolved.
     */
    public void addResource(String base, String hRef, String resource) {
        resources.put(base + hRef, resource);
    }

}
