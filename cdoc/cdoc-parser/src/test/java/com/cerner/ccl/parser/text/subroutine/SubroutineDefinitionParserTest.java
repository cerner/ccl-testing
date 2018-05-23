package com.cerner.ccl.parser.text.subroutine;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

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
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();

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
        expected.expect(InvalidSubroutineException.class);
        expected.expectMessage("Unable to find closing parenthesis after open parenthesis: " + subroutineDefinition);
        parser.parse(subroutineDefinition);
    }

    /**
     * Parsing a subroutine with no opening parenthesis should fail.
     */
    @Test
    public void testParseNoOpeningParenthesis() {
        final String subroutineDefinition = "subroutine no_open_parenthesis";
        expected.expect(InvalidSubroutineException.class);
        expected.expectMessage("Unable to find opening parenthesis in definition: " + subroutineDefinition);
        parser.parse(subroutineDefinition);
    }

    /**
     * If there is no {@code subroutine} keyword, then parsing the subroutine name should fail.
     */
    @Test
    public void testParseNoSubroutineKeyword() {
        final String subroutineDefinition = "no_subroutine_keyword";
        expected.expect(InvalidSubroutineException.class);
        expected.expectMessage("Unable to retrieve subroutine name from definition: " + subroutineDefinition);
        parser.parse(subroutineDefinition);
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
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Subroutine definition cannot be null.");
        parser.parse(null);
    }

    /**
     * If the open parenthesis somehow precedes the {@code subroutine} keyword, parsing should fail.
     */
    @Test
    public void testParsePrecedingOpenParenthesis() {
        final String subroutineDefinition = "(subroutine preceding_parenthesis(";
        expected.expect(InvalidSubroutineException.class);
        expected.expectMessage("Opening parenthesis precedes the subroutine keyword: " + subroutineDefinition);
        parser.parse(subroutineDefinition);
    }
}
