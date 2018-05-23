package com.cerner.ccl.parser.text.smoosh;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;

import com.cerner.ccl.parser.text.smoosh.internal.AbstractIndexedSmoosherUnitTest;

/**
 * Unit tests for {@link DocumentationBlockIndexedSmoosher}.
 * 
 * @author Joshua Hyde
 * 
 */

public class DocumentationBlockIndexedSmoosherTest
        extends AbstractIndexedSmoosherUnitTest<DocumentationBlockIndexedSmoosher> {
    private final DocumentationBlockIndexedSmoosher smoosher = new DocumentationBlockIndexedSmoosher();

    /**
     * Test the determination of a line's smooshability - in this case, anything should be smooshable.
     */
    @Test
    public void testCanSmoosh() {
        assertThat(smoosher.canSmoosh("sdfjk 72489423lk as@#$ADFasd;zlkdjffSdfS9087 3rjksaf;z")).isTrue();
    }

    @Override
    protected DocumentationBlockIndexedSmoosher getSmoosher() {
        return smoosher;
    }
}
