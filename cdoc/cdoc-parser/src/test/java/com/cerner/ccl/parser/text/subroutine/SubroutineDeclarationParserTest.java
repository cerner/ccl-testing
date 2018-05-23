package com.cerner.ccl.parser.text.subroutine;

import static org.fest.assertions.Assertions.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.SimpleCharacterDataTyped;
import com.cerner.ccl.parser.exception.InvalidSubroutineException;

/**
 * Unit tests for {@link SubroutineDeclarationParser}.
 * <p>
 * This test does not test the portion of code that fails if, when parsing the list of arguments, no opening parenthesis
 * can be found. This is because the parsing of the subroutine name catches this case first.
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineDeclarationParserTest {
    /**
     * A {@link Rule} used to test for thrown exceptions.
     */
    @Rule
    public ExpectedException expected = ExpectedException.none();
    private final SubroutineDeclarationParser parser = new SubroutineDeclarationParser();

    /**
     * If the by-reference indicator is actually "(ref)" (rather than "(REF)"), the parsing should still succeed.
     */
    @Test
    public void testParseByRefLowercase() {
        final SubroutineDeclaration declaration = parser
                .parse("declare by_ref_subroutine(arg1 = vc(ref), arg2 = vc) = vc");
        assertThat(declaration.getName()).isEqualTo("by_ref_subroutine");
        assertThat(declaration.getReturnType().getDataType()).isEqualTo(DataType.VC);

        final List<SubroutineArgumentDeclaration> argDeclarations = declaration.getArguments();
        assertThat(argDeclarations).hasSize(2);

        final SubroutineArgumentDeclaration arg1Declaration = argDeclarations.get(0);
        assertThat(arg1Declaration.getName()).isEqualTo("arg1");
        assertThat(arg1Declaration.getDataType()).isEqualTo(DataType.VC);
        assertThat(arg1Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg2Declaration = argDeclarations.get(1);
        assertThat(arg2Declaration.getName()).isEqualTo("arg2");
        assertThat(arg2Declaration.getDataType()).isEqualTo(DataType.VC);
        assertThat(arg2Declaration.isByRef()).isFalse();
    }

    /**
     * Confirms that the parser does not bomb on hash map declarations declared using "HASH"
     */
    @Test
    public void testHashMap() {
        final SubroutineDeclaration declaration = parser
                .parse("declare myMap(mode = vc, mapKey = vc, mapVal = i4) = i4 with protect, map='HASH'");
        assertThat(declaration.getName()).isEqualTo("myMap");
        assertThat(declaration.getReturnType().getDataType()).isEqualTo(DataType.I4);

        final List<SubroutineArgumentDeclaration> argDeclarations = declaration.getArguments();
        assertThat(argDeclarations).hasSize(3);

        final SubroutineArgumentDeclaration arg1Declaration = argDeclarations.get(0);
        assertThat(arg1Declaration.getName()).isEqualTo("mode");
        assertThat(arg1Declaration.getDataType()).isEqualTo(DataType.VC);
        assertThat(arg1Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg2Declaration = argDeclarations.get(1);
        assertThat(arg2Declaration.getName()).isEqualTo("mapKey");
        assertThat(arg2Declaration.getDataType()).isEqualTo(DataType.VC);
        assertThat(arg2Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg3Declaration = argDeclarations.get(2);
        assertThat(arg3Declaration.getName()).isEqualTo("mapVal");
        assertThat(arg3Declaration.getDataType()).isEqualTo(DataType.I4);
        assertThat(arg3Declaration.isByRef()).isFalse();
    }

    /**
     * Confirms that the parser does not bomb on hash map declarations declared using "hAsH"
     */
    @Test
    public void testHashMapMixedCase() {
        final SubroutineDeclaration declaration = parser
                .parse("declare myMap(mode = vc, mapKey = vc, mapVal = i4) = i4 with protect, map='hAsH'");
        assertThat(declaration.getName()).isEqualTo("myMap");
        assertThat(declaration.getReturnType().getDataType()).isEqualTo(DataType.I4);

        final List<SubroutineArgumentDeclaration> argDeclarations = declaration.getArguments();
        assertThat(argDeclarations).hasSize(3);

        final SubroutineArgumentDeclaration arg1Declaration = argDeclarations.get(0);
        assertThat(arg1Declaration.getName()).isEqualTo("mode");
        assertThat(arg1Declaration.getDataType()).isEqualTo(DataType.VC);
        assertThat(arg1Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg2Declaration = argDeclarations.get(1);
        assertThat(arg2Declaration.getName()).isEqualTo("mapKey");
        assertThat(arg2Declaration.getDataType()).isEqualTo(DataType.VC);
        assertThat(arg2Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg3Declaration = argDeclarations.get(2);
        assertThat(arg3Declaration.getName()).isEqualTo("mapVal");
        assertThat(arg3Declaration.getDataType()).isEqualTo(DataType.I4);
        assertThat(arg3Declaration.isByRef()).isFalse();
    }

    /**
     * If the equals sign that indicates that data type of the argument is missing, then parsing should fail.
     */
    @Test
    public void testParseMissingArgumentEquals() {
        expected.expect(InvalidSubroutineException.class);
        expected.expectMessage("Invalid argument definition: birth_dt_tm dq8");
        parser.parse("declare no_equals(person_id = f8,birth_dt_tm dq8) = null");
    }

    /**
     * If the subroutine contains no arguments in its declaration, then parsing should NOT fail. The documentation
     * generator does not need to decided if this is a problem. The CCL compiler will do that so let it.
     */
    @Test
    public void testParseNoArguments() {
        final String subroutineDeclaration = "declare no_args() = null";
        SubroutineDeclaration declaration = parser.parse(subroutineDeclaration);
        assertThat(declaration.getName()).isEqualTo("no_args");
        assertThat(declaration.getArguments().size()).isEqualTo(0);
        assertThat(declaration.getReturnType()).isEqualTo(null);
    }

    /**
     * If the declaration contains no {@code declare} keyword, parsing should fail.
     */
    @Test
    public void testParseNoDeclareKeyword() {
        final String subroutineDeclaration = "no_declare(person_id = f8)";
        expected.expect(InvalidSubroutineException.class);
        expected.expectMessage("SubroutineDeclarationParser.parse inovked with an invalid subroutine declaration: "
                + subroutineDeclaration);
        parser.parse(subroutineDeclaration);
    }

    /**
     * Parsing a declaration with no opening parenthesis should fail.
     */
    @Test
    public void testParseNoOpenParenthesis() {
        final String subroutineDeclaration = "declare no_open_paren";
        expected.expect(InvalidSubroutineException.class);
        expected.expectMessage("No opening parenthesis found in subroutine declaration: " + subroutineDeclaration);
        parser.parse(subroutineDeclaration);
    }

    /**
     * Parsing a subroutine with no return type should fail.
     */
    @Test
    public void testParseNoReturnType() {
        final String subroutineDeclaration = "declare no_return_type(null)";
        expected.expect(InvalidSubroutineException.class);
        expected.expectMessage("No return data type found within declaration: " + subroutineDeclaration);
        parser.parse(subroutineDeclaration);
    }

    /**
     * Test the parsing a subroutine with {@code NULL} arguments.
     */
    @Test
    public void testParseNullArguments() {
        final SubroutineDeclaration declaration = parser.parse("declare get_all_people(null) = null");
        assertThat(declaration.getName()).isEqualTo("get_all_people");
        assertThat(declaration.getArguments()).isEmpty();
        assertThat(declaration.getReturnType()).isNull();
    }

    /**
     * Parsing of a {@code null} declaration should fail.
     */
    @Test
    public void testParseNullDeclaration() {
        expected.expect(IllegalArgumentException.class);
        expected.expectMessage("Subroutine declaration cannot be null.");
        parser.parse(null);
    }

    /**
     * Parsing a declaration in which, somehow, the opening parenthesis precedes the {@code declare} keyword should
     * fail.
     */
    @Test
    public void testParsePrecedingOpenParenthesis() {
        final String subroutineDeclaration = "(declare preceding_open_paren(person_id = f8)";
        expected.expect(InvalidSubroutineException.class);
        expected.expectMessage("SubroutineDeclarationParser.parse inovked with an invalid subroutine declaration: "
                + subroutineDeclaration);
        parser.parse(subroutineDeclaration);
    }

    /**
     * Test the parsing of a subroutine with a return data type.
     */
    @Test
    public void testParseReturnDataType() {
        final SubroutineDeclaration declaration = parser.parse("declare get_first_name(person_id = f8) = vc");
        assertThat(declaration.getName()).isEqualTo("get_first_name");

        final SubroutineArgumentDeclaration personId = declaration.getArguments().get(0);
        assertThat(personId.getName()).isEqualTo("person_id");
        assertThat(personId.getDataType()).isEqualTo(DataType.F8);
        assertThat(personId.isByRef()).isFalse();

        assertThat(declaration.getReturnType().getDataType()).isEqualTo(DataType.VC);
    }

    /**
     * Test the parsing of a subroutine argument when the argument is a fixed-length character.
     */
    @Test
    public void testParseWithFixedLengthCharArgument() {
        final SubroutineDeclaration declaration = parser.parse("declare get_by_last_name(person_name = c32) = i4");
        assertThat(declaration.getName()).isEqualTo("get_by_last_name");
        assertThat(declaration.getReturnType().getDataType()).isEqualTo(DataType.I4);

        final SubroutineCharacterArgumentDeclaration personNameArg = (SubroutineCharacterArgumentDeclaration) declaration
                .getArguments().get(0);
        assertThat(personNameArg).isNotNull();
        assertThat(personNameArg.getName()).isEqualTo("person_name");
        assertThat(personNameArg.getDataType()).isEqualTo(DataType.CHAR);
        assertThat(personNameArg.getDataLength()).isEqualTo(32);
    }

    /**
     * Test the parsing of a subroutine that returns a fixed-length character variable.
     */
    @Test
    public void testParseWithFixedLengthCharReturnType() {
        final SubroutineDeclaration declaration = parser.parse("declare get_last_name(birth_dt_tm = dq8) = c23");
        assertThat(declaration.getName()).isEqualTo("get_last_name");
        assertThat(declaration.getReturnType().getDataType()).isEqualTo(DataType.CHAR);
        assertThat(((SimpleCharacterDataTyped) declaration.getReturnType()).getDataLength()).isEqualTo(23);

        final SubroutineArgumentDeclaration birthDtTmArg = declaration.getArguments().get(0);
        assertThat(birthDtTmArg).isNotNull();
        assertThat(birthDtTmArg.getName()).isEqualTo("birth_dt_tm");
        assertThat(birthDtTmArg.getDataType()).isEqualTo(DataType.DQ8);
        assertThat(birthDtTmArg.isByRef()).isFalse();
    }

    /**
     * Test the parsing of a {@code void} subroutine.
     */
    @Test
    public void testParseVoidSubroutine() {
        final SubroutineDeclaration declaration = parser
                .parse("declare get_person_id(person_id = f8, active_ind = i2(REF)) = null");
        assertThat(declaration.getName()).isEqualTo("get_person_id");
        assertThat(declaration.getReturnType()).isNull();

        final List<SubroutineArgumentDeclaration> arguments = declaration.getArguments();

        final SubroutineArgumentDeclaration personIdArg = arguments.get(0);
        assertThat(personIdArg).isNotNull();
        assertThat(personIdArg.getName()).isEqualTo("person_id");
        assertThat(personIdArg.getDataType()).isEqualTo(DataType.F8);
        assertThat(personIdArg.isByRef()).isFalse();

        final SubroutineArgumentDeclaration activeIndArg = arguments.get(1);
        assertThat(activeIndArg).isNotNull();
        assertThat(activeIndArg.getName()).isEqualTo("active_ind");
        assertThat(activeIndArg.getDataType()).isEqualTo(DataType.I2);
        assertThat(activeIndArg.isByRef()).isTrue();
    }

    /**
     * If a subroutine argument contains a qualifier other than {@code (REF)}, it should be ignored.
     */
    @Test
    public void testParseIgnoreNotByRef() {
        final SubroutineDeclaration declaration = parser
                .parse("declare some_subroutine(arg1=vc(ref), arg2=i4(value,CURREF), arg3=i2(val)) = i2");
        assertThat(declaration.getName()).isEqualTo("some_subroutine");
        assertThat(declaration.getReturnType().getDataType()).isEqualTo(DataType.I2);
        assertThat(declaration.getArguments()).hasSize(3);

        final List<SubroutineArgumentDeclaration> arguments = declaration.getArguments();

        final SubroutineArgumentDeclaration arg1 = arguments.get(0);
        assertThat(arg1).isNotNull();
        assertThat(arg1.getName()).isEqualTo("arg1");
        assertThat(arg1.getDataType()).isEqualTo(DataType.VC);
        assertThat(arg1.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg2 = arguments.get(1);
        assertThat(arg2).isNotNull();
        assertThat(arg2.getName()).isEqualTo("arg2");
        assertThat(arg2.getDataType()).isEqualTo(DataType.I4);
        assertThat(arg2.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg3 = arguments.get(2);
        assertThat(arg3).isNotNull();
        assertThat(arg3.getName()).isEqualTo("arg3");
        assertThat(arg3.getDataType()).isEqualTo(DataType.I2);
        assertThat(arg3.isByRef()).isFalse();
    }

    /**
     * If the {@code (REF)} qualified is nested within other qualifiers, it should be parsed out successfully. Spaces
     * should be irrelevant.
     */
    @Test
    public void testParseByRefNested() {
        final SubroutineDeclaration declaration = parser
                .parse("declare some_subroutine(arg1=i4(ref,CURREF), arg2=vc) = i2");
        assertThat(declaration.getName()).isEqualTo("some_subroutine");
        assertThat(declaration.getReturnType().getDataType()).isEqualTo(DataType.I2);
        assertThat(declaration.getArguments()).hasSize(2);

        final List<SubroutineArgumentDeclaration> arguments = declaration.getArguments();

        final SubroutineArgumentDeclaration arg1 = arguments.get(0);
        assertThat(arg1).isNotNull();
        assertThat(arg1.getName()).isEqualTo("arg1");
        assertThat(arg1.getDataType()).isEqualTo(DataType.I4);
        assertThat(arg1.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg2 = arguments.get(1);
        assertThat(arg2).isNotNull();
        assertThat(arg2.getName()).isEqualTo("arg2");
        assertThat(arg2.getDataType()).isEqualTo(DataType.VC);
        assertThat(arg2.isByRef()).isFalse();

        final SubroutineDeclaration declaration_2 = parser
                .parse("  declare some_subroutine(  arg1  =   i4  ( ref ,  CURREF ) , arg2  = vc  )  =   i2  ");
        assertThat(declaration_2.getName()).isEqualTo("some_subroutine");
        assertThat(declaration_2.getReturnType().getDataType()).isEqualTo(DataType.I2);
        assertThat(declaration_2.getArguments()).hasSize(2);

        final List<SubroutineArgumentDeclaration> arguments_2 = declaration_2.getArguments();

        final SubroutineArgumentDeclaration arg1_2 = arguments_2.get(0);
        assertThat(arg1_2).isNotNull();
        assertThat(arg1_2.getName()).isEqualTo("arg1");
        assertThat(arg1_2.getDataType()).isEqualTo(DataType.I4);
        assertThat(arg1_2.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg2_2 = arguments_2.get(1);
        assertThat(arg2_2).isNotNull();
        assertThat(arg2_2.getName()).isEqualTo("arg2");
        assertThat(arg2_2.getDataType()).isEqualTo(DataType.VC);
        assertThat(arg2_2.isByRef()).isFalse();
    }

    /**
     * An argument should only be considered to be passed by reference if the first qualifier is {@code (REF)}. CCL
     * allows having a variable named ref and {@code p=(VAL,REF)} should be by value not by reference.
     */
    @Test
    public void testParseByRefNestedLookAlike() {
        final SubroutineDeclaration declaration = parser
                .parse("declare some_subroutine(arg1=i4(val,ref), arg2=vc) = i2");
        assertThat(declaration.getName()).isEqualTo("some_subroutine");
        assertThat(declaration.getReturnType().getDataType()).isEqualTo(DataType.I2);
        assertThat(declaration.getArguments()).hasSize(2);

        final List<SubroutineArgumentDeclaration> arguments = declaration.getArguments();

        final SubroutineArgumentDeclaration arg1 = arguments.get(0);
        assertThat(arg1).isNotNull();
        assertThat(arg1.getName()).isEqualTo("arg1");
        assertThat(arg1.getDataType()).isEqualTo(DataType.I4);
        assertThat(arg1.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg2 = arguments.get(1);
        assertThat(arg2).isNotNull();
        assertThat(arg2.getName()).isEqualTo("arg2");
        assertThat(arg2.getDataType()).isEqualTo(DataType.VC);
        assertThat(arg2.isByRef()).isFalse();
    }

    /**
     * If the subroutine contains a scope, then its scope should not interfere with parsing.
     */
    @Test
    public void testParseWithScope() {
        final SubroutineDeclaration declaration = parser
                .parse("declare write_person_name(person_id = f8, name = vc(REF)) = i2 with protect");
        assertThat(declaration.getName()).isEqualTo("write_person_name");
        assertThat(declaration.getReturnType().getDataType()).isEqualTo(DataType.I2);

        final List<SubroutineArgumentDeclaration> arguments = declaration.getArguments();
        final SubroutineArgumentDeclaration personId = arguments.get(0);
        assertThat(personId).isNotNull();
        assertThat(personId.getName()).isEqualTo("person_id");
        assertThat(personId.getDataType()).isEqualTo(DataType.F8);
        assertThat(personId.isByRef()).isFalse();

        final SubroutineArgumentDeclaration nameArg = arguments.get(1);
        assertThat(nameArg).isNotNull();
        assertThat(nameArg.getName()).isEqualTo("name");
        assertThat(nameArg.getDataType()).isEqualTo(DataType.VC);
        assertThat(nameArg.isByRef()).isTrue();
    }

    /**
     * Verifies the successful parsing of an in-line subroutine definition
     */
    @Test
    public void testParseInLineDeclaration() {
        final SubroutineDeclaration declaration = parser
                .parse("subroutine  (  in_line_sub(person_id = f8, name = vc(ReF)) = i2 with protect  )  ");
        assertThat(declaration.getName()).isEqualTo("in_line_sub");
        assertThat(declaration.getReturnType().getDataType()).isEqualTo(DataType.I2);

        final List<SubroutineArgumentDeclaration> arguments = declaration.getArguments();
        final SubroutineArgumentDeclaration personId = arguments.get(0);
        assertThat(personId).isNotNull();
        assertThat(personId.getName()).isEqualTo("person_id");
        assertThat(personId.getDataType()).isEqualTo(DataType.F8);
        assertThat(personId.isByRef()).isFalse();

        final SubroutineArgumentDeclaration nameArg = arguments.get(1);
        assertThat(nameArg).isNotNull();
        assertThat(nameArg.getName()).isEqualTo("name");
        assertThat(nameArg.getDataType()).isEqualTo(DataType.VC);
        assertThat(nameArg.isByRef()).isTrue();
    }

    /**
     * Verifies that all known types parse correctly.
     */
    @Test
    public void testDeclareAllTheTypes() {
        final SubroutineDeclaration declaration = parser.parse(
                "declare allTheTypes(arg1 = vc, arg2 = vc(ref), arg3 = f4, arg4 = f4(ref), arg5 = f8, arg6 = f8(ref), "
                        + "arg7 = i1, arg8 = i1(ref), arg9 = ui1, arg10 = ui1(ref), arg11 = i2, arg12 = i2(ref), arg13 = ui2, arg14 = ui2(ref), "
                        + "arg15 = i4, arg16 = i4(ref), arg17 = ui4, arg18 = ui4(ref), arg19 = w8, arg20 = w8(ref), arg21 = uw8, arg22 = uw8(ref),  "
                        + "arg23 = h, arg24 = h(ref), arg25 = gvc, arg26 = gvc(ref),  arg27 = ZVC, arg28 = ZVC(ref),  arg29 = ZGVC, arg30 = ZGVC(ref),  "
                        + "arg31 = DQ8, arg32 = DQ8(ref),  arg33 = DM12, arg34 = DM12(ref),  arg35 = DM14, arg36 = DM14(ref)) = vc");

        final List<SubroutineArgumentDeclaration> argDeclarations = declaration.getArguments();
        assertThat(argDeclarations).hasSize(36);

        final SubroutineArgumentDeclaration arg0Declaration = argDeclarations.get(0);
        assertThat(arg0Declaration.getName()).isEqualTo("arg1");
        assertThat(arg0Declaration.getDataType()).isEqualTo(DataType.VC);
        assertThat(arg0Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg1Declaration = argDeclarations.get(1);
        assertThat(arg1Declaration.getName()).isEqualTo("arg2");
        assertThat(arg1Declaration.getDataType()).isEqualTo(DataType.VC);
        assertThat(arg1Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg2Declaration = argDeclarations.get(2);
        assertThat(arg2Declaration.getName()).isEqualTo("arg3");
        assertThat(arg2Declaration.getDataType()).isEqualTo(DataType.F4);
        assertThat(arg2Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg3Declaration = argDeclarations.get(3);
        assertThat(arg3Declaration.getName()).isEqualTo("arg4");
        assertThat(arg3Declaration.getDataType()).isEqualTo(DataType.F4);
        assertThat(arg3Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg4Declaration = argDeclarations.get(4);
        assertThat(arg4Declaration.getName()).isEqualTo("arg5");
        assertThat(arg4Declaration.getDataType()).isEqualTo(DataType.F8);
        assertThat(arg4Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg5Declaration = argDeclarations.get(5);
        assertThat(arg5Declaration.getName()).isEqualTo("arg6");
        assertThat(arg5Declaration.getDataType()).isEqualTo(DataType.F8);
        assertThat(arg5Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg6Declaration = argDeclarations.get(6);
        assertThat(arg6Declaration.getName()).isEqualTo("arg7");
        assertThat(arg6Declaration.getDataType()).isEqualTo(DataType.I1);
        assertThat(arg6Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg7Declaration = argDeclarations.get(7);
        assertThat(arg7Declaration.getName()).isEqualTo("arg8");
        assertThat(arg7Declaration.getDataType()).isEqualTo(DataType.I1);
        assertThat(arg7Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg8Declaration = argDeclarations.get(8);
        assertThat(arg8Declaration.getName()).isEqualTo("arg9");
        assertThat(arg8Declaration.getDataType()).isEqualTo(DataType.UI1);
        assertThat(arg8Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg9Declaration = argDeclarations.get(9);
        assertThat(arg9Declaration.getName()).isEqualTo("arg10");
        assertThat(arg9Declaration.getDataType()).isEqualTo(DataType.UI1);
        assertThat(arg9Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg10Declaration = argDeclarations.get(10);
        assertThat(arg10Declaration.getName()).isEqualTo("arg11");
        assertThat(arg10Declaration.getDataType()).isEqualTo(DataType.I2);
        assertThat(arg10Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg11Declaration = argDeclarations.get(11);
        assertThat(arg11Declaration.getName()).isEqualTo("arg12");
        assertThat(arg11Declaration.getDataType()).isEqualTo(DataType.I2);
        assertThat(arg11Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg12Declaration = argDeclarations.get(12);
        assertThat(arg12Declaration.getName()).isEqualTo("arg13");
        assertThat(arg12Declaration.getDataType()).isEqualTo(DataType.UI2);
        assertThat(arg12Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg13Declaration = argDeclarations.get(13);
        assertThat(arg13Declaration.getName()).isEqualTo("arg14");
        assertThat(arg13Declaration.getDataType()).isEqualTo(DataType.UI2);
        assertThat(arg13Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg14Declaration = argDeclarations.get(14);
        assertThat(arg14Declaration.getName()).isEqualTo("arg15");
        assertThat(arg14Declaration.getDataType()).isEqualTo(DataType.I4);
        assertThat(arg14Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg15Declaration = argDeclarations.get(15);
        assertThat(arg15Declaration.getName()).isEqualTo("arg16");
        assertThat(arg15Declaration.getDataType()).isEqualTo(DataType.I4);
        assertThat(arg15Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg16Declaration = argDeclarations.get(16);
        assertThat(arg16Declaration.getName()).isEqualTo("arg17");
        assertThat(arg16Declaration.getDataType()).isEqualTo(DataType.UI4);
        assertThat(arg16Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg17Declaration = argDeclarations.get(17);
        assertThat(arg17Declaration.getName()).isEqualTo("arg18");
        assertThat(arg17Declaration.getDataType()).isEqualTo(DataType.UI4);
        assertThat(arg17Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg18Declaration = argDeclarations.get(18);
        assertThat(arg18Declaration.getName()).isEqualTo("arg19");
        assertThat(arg18Declaration.getDataType()).isEqualTo(DataType.W8);
        assertThat(arg18Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg19Declaration = argDeclarations.get(19);
        assertThat(arg19Declaration.getName()).isEqualTo("arg20");
        assertThat(arg19Declaration.getDataType()).isEqualTo(DataType.W8);
        assertThat(arg19Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg20Declaration = argDeclarations.get(20);
        assertThat(arg20Declaration.getName()).isEqualTo("arg21");
        assertThat(arg20Declaration.getDataType()).isEqualTo(DataType.UW8);
        assertThat(arg20Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg21Declaration = argDeclarations.get(21);
        assertThat(arg21Declaration.getName()).isEqualTo("arg22");
        assertThat(arg21Declaration.getDataType()).isEqualTo(DataType.UW8);
        assertThat(arg21Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg22Declaration = argDeclarations.get(22);
        assertThat(arg22Declaration.getName()).isEqualTo("arg23");
        assertThat(arg22Declaration.getDataType()).isEqualTo(DataType.H);
        assertThat(arg22Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg23Declaration = argDeclarations.get(23);
        assertThat(arg23Declaration.getName()).isEqualTo("arg24");
        assertThat(arg23Declaration.getDataType()).isEqualTo(DataType.H);
        assertThat(arg23Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg24Declaration = argDeclarations.get(24);
        assertThat(arg24Declaration.getName()).isEqualTo("arg25");
        assertThat(arg24Declaration.getDataType()).isEqualTo(DataType.GVC);
        assertThat(arg24Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg25Declaration = argDeclarations.get(25);
        assertThat(arg25Declaration.getName()).isEqualTo("arg26");
        assertThat(arg25Declaration.getDataType()).isEqualTo(DataType.GVC);
        assertThat(arg25Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg26Declaration = argDeclarations.get(26);
        assertThat(arg26Declaration.getName()).isEqualTo("arg27");
        assertThat(arg26Declaration.getDataType()).isEqualTo(DataType.ZVC);
        assertThat(arg26Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg27Declaration = argDeclarations.get(27);
        assertThat(arg27Declaration.getName()).isEqualTo("arg28");
        assertThat(arg27Declaration.getDataType()).isEqualTo(DataType.ZVC);
        assertThat(arg27Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg28Declaration = argDeclarations.get(28);
        assertThat(arg28Declaration.getName()).isEqualTo("arg29");
        assertThat(arg28Declaration.getDataType()).isEqualTo(DataType.ZGVC);
        assertThat(arg28Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg29Declaration = argDeclarations.get(29);
        assertThat(arg29Declaration.getName()).isEqualTo("arg30");
        assertThat(arg29Declaration.getDataType()).isEqualTo(DataType.ZGVC);
        assertThat(arg29Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg30Declaration = argDeclarations.get(30);
        assertThat(arg30Declaration.getName()).isEqualTo("arg31");
        assertThat(arg30Declaration.getDataType()).isEqualTo(DataType.DQ8);
        assertThat(arg30Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg31Declaration = argDeclarations.get(31);
        assertThat(arg31Declaration.getName()).isEqualTo("arg32");
        assertThat(arg31Declaration.getDataType()).isEqualTo(DataType.DQ8);
        assertThat(arg31Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg32Declaration = argDeclarations.get(32);
        assertThat(arg32Declaration.getName()).isEqualTo("arg33");
        assertThat(arg32Declaration.getDataType()).isEqualTo(DataType.DM12);
        assertThat(arg32Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg33Declaration = argDeclarations.get(33);
        assertThat(arg33Declaration.getName()).isEqualTo("arg34");
        assertThat(arg33Declaration.getDataType()).isEqualTo(DataType.DM12);
        assertThat(arg33Declaration.isByRef()).isTrue();

        final SubroutineArgumentDeclaration arg34Declaration = argDeclarations.get(34);
        assertThat(arg34Declaration.getName()).isEqualTo("arg35");
        assertThat(arg34Declaration.getDataType()).isEqualTo(DataType.DM14);
        assertThat(arg34Declaration.isByRef()).isFalse();

        final SubroutineArgumentDeclaration arg35Declaration = argDeclarations.get(35);
        assertThat(arg35Declaration.getName()).isEqualTo("arg36");
        assertThat(arg35Declaration.getDataType()).isEqualTo(DataType.DM14);
        assertThat(arg35Declaration.isByRef()).isTrue();
    }
}
