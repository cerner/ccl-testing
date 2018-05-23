package com.cerner.ccl.parser.text.documentation.parser;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

/**
 * Unit tests for {@link AbstractIndexedParser}.
 *
 * @author Joshua Hyde
 *
 */

public class AbstractIndexedParserTest {
    private final ConcreteParser parser = new ConcreteParser();

    /**
     * Test the retrieval of the ending index.
     */
    @Test
    public void testGetEndingIndex() {
        final int index = 3;
        parser.setEndingIndex(index);
        assertThat(parser.getEndingIndex()).isEqualTo(index);
    }

    /**
     * If parsing is never called, the return index should be {@code -1}.
     */
    @Test
    public void testGetEndingIndexNeverParsed() {
        assertThat(parser.getEndingIndex()).isEqualTo(-1);
    }

    /**
     * A simple concrete implementation of {@link AbstractIndexedParser} to assist in testing.
     *
     * @author Joshua Hyde
     *
     */
    private static class ConcreteParser extends AbstractIndexedParser<Object> {
        ConcreteParser() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean canParse(final String line) {
            return false;
        }

        @Override
        public Object parse(final int startingIndex, final List<String> lines) {
            return null;
        }

    }
}
