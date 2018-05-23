package com.cerner.ccl.parser.text.documentation.parser;

/**
 * Definition of an object that can parse documentation.
 * 
 * @author Joshua Hyde
 */

public interface Parser {
    /**
     * Determine whether or not the given line can be parsed.
     * 
     * @param line
     *            The line whose parseability is to be determined.
     * @return {@code true} if the line can be parsed; {@code false} if not.
     * @throws IllegalArgumentException
     *             If the given line is {@code null}.
     */
    boolean canParse(String line);
}
