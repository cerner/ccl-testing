package com.cerner.ccl.cdoc.velocity;

import java.io.InputStream;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

/**
 * A {@link ResourceLoader} that returns resources using this object's {@link Class}.
 *
 * @author Joshua Hyde
 *
 */

public class ClassResourceLoader extends ResourceLoader {

    @Override
    public void init(final ExtendedProperties configuration) {
        // no initialization needed
    }

    @Override
    public InputStream getResourceStream(final String source) throws ResourceNotFoundException {
        final InputStream stream = ClassResourceLoader.class.getResourceAsStream(source);
        if (stream == null) {
            throw new ResourceNotFoundException("Resource not found: " + source);
        }

        return stream;
    }

    @Override
    public boolean isSourceModified(final Resource resource) {
        return false;
    }

    @Override
    public long getLastModified(final Resource resource) {
        return 0;
    }
}
