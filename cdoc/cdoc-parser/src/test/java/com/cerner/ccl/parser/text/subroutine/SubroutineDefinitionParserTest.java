package com.cerner.ccl.parser.text.subroutine;

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import com.cerner.ccl.parser.exception.InvalidSubroutineException;

/**
 * Unit tests for {@link SubroutineDefinitionParser}.
 * <p>
 * There is a case of parsing the argument list when there is no opening parenthesis; this case cannot be tested, as the
 * cause of it (a lack of an opening parenthesis) first causes the parsing of the subroutine name to fail.
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineDefinitionParserTest {
    private final SubroutineDefinitionParser parser = new SubroutineDefinitionParser();

    /**
     * Test the parsing of a definition.
     */
    @Test
    public void testParse() {
        final SubroutineDefinition definition = parser.parse("subroutine get_last_name(person_id, birth_dt_tm)");
        assertThat(definition.getName()).isEqualTo("get_last_name");
        assertThat(definition.getArgumentNames()).containsExactly("person_id", "birth_dt_tm");
    }

    /**
     * If the subroutine definition has no closing parenthesis, then parsing should fail.
     */
    @Test
    public void testParseNoClosingParenthesis() {
        final String subroutineDefinition = "subroutine no_closing_paren(arg1";
        InvalidSubroutineException e = assertThrows(InvalidSubroutineException.class, () -> {
            parser.parse(subroutineDefinition);
        });
        assertThat(e.getMessage())
                .isEqualTo("Unable to find closing parenthesis after open parenthesis: " + subroutineDefinition);
    }

    /**
     * Parsing a subroutine with no opening parenthesis should fail.
     */
    @Test
    public void testParseNoOpeningParenthesis() {
        final String subroutineDefinition = "subroutine no_open_parenthesis";
        InvalidSubroutineException e = assertThrows(InvalidSubroutineException.class, () -> {
            parser.parse(subroutineDefinition);
        });
        assertThat(e.getMessage())
                .isEqualTo("Unable to find opening parenthesis in definition: " + subroutineDefinition);
    }

    /**
     * If there is no {@code subroutine} keyword, then parsing the subroutine name should fail.
     */
    @Test
    public void testParseNoSubroutineKeyword() {
        final String subroutineDefinition = "no_subroutine_keyword";
        InvalidSubroutineException e = assertThrows(InvalidSubroutineException.class, () -> {
            parser.parse(subroutineDefinition);
        });
        assertThat(e.getMessage())
                .isEqualTo("Unable to retrieve subroutine name from definition: " + subroutineDefinition);
    }

    /**
     * Test the parsing of a subroutine with no arguments.
     */
    @Test
    public void testParseNullArguments() {
        final SubroutineDefinition definition = parser.parse("subroutine no_arguments(null)");
        assertThat(definition.getName()).isEqualTo("no_arguments");
        assertThat(definition.getArgumentNames()).isEmpty();
    }

    /**
     * Parsing with a {@code null} definition should fail.
     */
    @Test
    public void testParseNullDefinition() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> {
            parser.parse(null);
        });
        assertThat(e.getMessage()).isEqualTo("Subroutine definition cannot be null.");
    }

    /**
     * If the open parenthesis somehow precedes the {@code subroutine} keyword, parsing should fail.
     */
    @Test
    public void testParsePrecedingOpenParenthesis() {
        final String subroutineDefinition = "(subroutine preceding_parenthesis(";
        InvalidSubroutineException e = assertThrows(InvalidSubroutineException.class, () -> {
            parser.parse(subroutineDefinition);
        });
        assertThat(e.getMessage())
                .isEqualTo("Opening parenthesis precedes the subroutine keyword: " + subroutineDefinition);
    }
}
