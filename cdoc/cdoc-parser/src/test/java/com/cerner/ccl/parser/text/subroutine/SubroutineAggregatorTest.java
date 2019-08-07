package com.cerner.ccl.parser.text.subroutine;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.cerner.ccl.parser.AbstractUnitTest;
import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.DataTyped;
import com.cerner.ccl.parser.data.SimpleDataTyped;
import com.cerner.ccl.parser.data.subroutine.Subroutine;
import com.cerner.ccl.parser.data.subroutine.SubroutineArgument;
import com.cerner.ccl.parser.data.subroutine.SubroutineCharacterArgument;
import com.cerner.ccl.parser.text.documentation.Parameter;
import com.cerner.ccl.parser.text.documentation.SubroutineDocumentation;

/**
 * Unit tests for {@link SubroutineAggregator}.
 *
 * @author Joshua Hyde
 *
 */

public class SubroutineAggregatorTest extends AbstractUnitTest {
    private final SubroutineAggregator aggregator = new SubroutineAggregator();

    /**
     * Aggregation with {@code null} declarations should fail.
     */
    @Test
    public void testAggregateNullDeclarations() {
        expect(IllegalArgumentException.class);
        expect("Declarations cannot be null.");
        aggregator.aggregate(null, Collections.<SubroutineDefinition> emptyList(),
                Collections.<String, SubroutineDocumentation> emptyMap());
    }

    /**
     * Aggregation with {@code null} definitions should fail.
     */
    @Test
    public void testAggregateNullDefinitions() {
        expect(IllegalArgumentException.class);
        expect("Definitions cannot be null.");
        aggregator.aggregate(Collections.<String, SubroutineDeclaration> emptyMap(), null,
                Collections.<String, SubroutineDocumentation> emptyMap());
    }

    /**
     * Aggregation with {@code null} subroutine documentation should fail.
     */
    @Test
    public void testAggregateNullSubroutineDocumentation() {
        expect(IllegalArgumentException.class);
        expect("Subroutine documentation cannot be null.");
        aggregator.aggregate(Collections.<String, SubroutineDeclaration> emptyMap(),
                Collections.<SubroutineDefinition> emptyList(), null);
    }

    /**
     * Test the aggregation of a subroutine without a declaration but <b>with</b> documentation.
     */
    @Test
    public void testAggregateMissingDeclaration() {
        final Parameter arg1 = new Parameter("arg1", "a description of arg1");

        final SubroutineDefinition def = new SubroutineDefinition("subroutine",
                Collections.singletonList(arg1.getName()));
        final SubroutineDocumentation doc = new SubroutineDocumentation("This is the description",
                Collections.singletonList(arg1), "i am the return description");

        final List<Subroutine> aggregates = aggregator.aggregate(Collections.<String, SubroutineDeclaration> emptyMap(),
                Collections.singletonList(def), Collections.singletonMap(def.getName(), doc));
        assertThat(aggregates).hasSize(1);

        final Subroutine aggregate = aggregates.get(0);
        assertThat(aggregate.getName()).isEqualTo(def.getName());
        assertThat(aggregate.getDescription()).isEqualTo(doc.getDescription());
        assertThat(aggregate.getReturnDataDescription()).isEqualTo(doc.getReturnDescription());
        assertThat(aggregate.<DataTyped> getReturnDataType()).isEqualTo(Subroutine.UNKNOWN_RETURN_TYPE);

        final List<SubroutineArgument> args = aggregate.getArguments();
        assertThat(args).hasSize(1);

        final SubroutineArgument arg = args.get(0);
        assertThat(arg.getName()).isEqualTo(arg1.getName());
        assertThat(arg.getDataType()).isNull();
        assertThat(arg.getDescription()).isEqualTo(arg1.getDescription());
        assertThat(arg.isByRef()).isFalse();
    }

    /**
     * Test aggregation when there is no declaration <b>or</b> documentation available.
     */
    @Test
    public void testAggregateMissingDeclarationNoDocumentation() {
        final SubroutineDefinition def = new SubroutineDefinition("subroutine",
                Collections.singletonList("no_doc_arg"));
        final List<Subroutine> aggregates = aggregator.aggregate(Collections.<String, SubroutineDeclaration> emptyMap(),
                Collections.singletonList(def), Collections.<String, SubroutineDocumentation> emptyMap());
        assertThat(aggregates).hasSize(1);

        final Subroutine aggregate = aggregates.get(0);
        assertThat(aggregate.getName()).isEqualTo(def.getName());
        assertThat(aggregate.getDescription()).isEmpty();
        assertThat(aggregate.getReturnDataDescription()).isEmpty();
        assertThat(aggregate.<DataTyped> getReturnDataType()).isEqualTo(Subroutine.UNKNOWN_RETURN_TYPE);

        final List<SubroutineArgument> args = aggregate.getArguments();
        assertThat(args).hasSize(1);

        final SubroutineArgument arg = args.get(0);
        assertThat(arg.getName()).isEqualTo("no_doc_arg");
        assertThat(arg.getDataType()).isNull();
        assertThat(arg.getDescription()).isEmpty();
        assertThat(arg.isByRef()).isFalse();
    }

    /**
     * If there is missing documentation for one or more of the parameters, aggregation should still succeed.
     */
    @Test
    public void testAggregateMissingParameterDocumentation() {
        final DataTyped returnType = new SimpleDataTyped(DataType.I2);
        final SubroutineArgumentDeclaration hasDoc = new SubroutineArgumentDeclaration("arg1", true, DataType.DQ8);
        final SubroutineArgumentDeclaration noDoc = new SubroutineArgumentDeclaration("arg2", false, DataType.F8);
        final SubroutineCharacterArgumentDeclaration hasDocChar = new SubroutineCharacterArgumentDeclaration("arg3Char",
                23, false);
        final SubroutineCharacterArgumentDeclaration noDocChar = new SubroutineCharacterArgumentDeclaration("arg4Char",
                43, false);

        final SubroutineDefinition def = new SubroutineDefinition("subroutine",
                Arrays.asList(hasDoc.getName(), noDoc.getName(), hasDocChar.getName(), noDocChar.getName()));
        final SubroutineDeclaration decl = new SubroutineDeclaration(def.getName(), returnType,
                Arrays.asList(hasDoc, noDoc, hasDocChar, noDocChar));

        final Parameter hasDocParam = new Parameter(hasDoc.getName(),
                "this is a description of a field with documentation");
        final Parameter hasDocCharParam = new Parameter(hasDocChar.getName(),
                "this is a description of a fixed-length character field with documentation");
        final SubroutineDocumentation doc = new SubroutineDocumentation(
                "the description of a subroutine missing some parameter doc",
                Arrays.asList(hasDocParam, hasDocCharParam), "return description of the weird subroutine");

        final List<Subroutine> aggregates = aggregator.aggregate(Collections.singletonMap(decl.getName(), decl),
                Collections.singletonList(def), Collections.singletonMap(def.getName(), doc));
        assertThat(aggregates).hasSize(1);

        final Subroutine aggregate = aggregates.get(0);
        assertThat(aggregate.getName()).isEqualTo(def.getName());
        assertThat(aggregate.getDescription()).isEqualTo(doc.getDescription());
        assertThat(aggregate.getReturnDataDescription()).isEqualTo(doc.getReturnDescription());
        assertThat(aggregate.<DataTyped> getReturnDataType()).isEqualTo(returnType);

        final List<SubroutineArgument> args = aggregate.getArguments();
        assertThat(args).hasSize(4);

        final SubroutineArgument aggregatedHasDoc = args.get(0);
        assertThat(aggregatedHasDoc.getName()).isEqualTo(hasDoc.getName());
        assertThat(aggregatedHasDoc.getDataType()).isEqualTo(hasDoc.getDataType());
        assertThat(aggregatedHasDoc.getDescription()).isEqualTo(hasDocParam.getDescription());
        assertThat(aggregatedHasDoc.isByRef()).isEqualTo(hasDoc.isByRef());

        final SubroutineArgument aggregatedNoDoc = args.get(1);
        assertThat(aggregatedNoDoc.getName()).isEqualTo(noDoc.getName());
        assertThat(aggregatedNoDoc.getDataType()).isEqualTo(noDoc.getDataType());
        assertThat(aggregatedNoDoc.getDescription()).isEmpty();
        assertThat(aggregatedNoDoc.isByRef()).isEqualTo(noDoc.isByRef());

        final SubroutineCharacterArgument aggregatedHasDocChar = (SubroutineCharacterArgument) args.get(2);
        assertThat(aggregatedHasDocChar.getName()).isEqualTo(hasDocChar.getName());
        assertThat(aggregatedHasDocChar.getDataType()).isEqualTo(hasDocChar.getDataType());
        assertThat(aggregatedHasDocChar.getDataLength()).isEqualTo(hasDocChar.getDataLength());
        assertThat(aggregatedHasDocChar.getDescription()).isEqualTo(hasDocCharParam.getDescription());
        assertThat(aggregatedHasDocChar.isByRef()).isEqualTo(hasDocChar.isByRef());

        final SubroutineCharacterArgument aggregatedNoDocChar = (SubroutineCharacterArgument) args.get(3);
        assertThat(aggregatedNoDocChar.getName()).isEqualTo(noDocChar.getName());
        assertThat(aggregatedNoDocChar.getDataType()).isEqualTo(noDocChar.getDataType());
        assertThat(aggregatedNoDocChar.getDescription()).isEmpty();
        assertThat(aggregatedNoDocChar.getDataLength()).isEqualTo(noDocChar.getDataLength());
        assertThat(aggregatedNoDocChar.isByRef()).isEqualTo(noDocChar.isByRef());
    }

    /**
     * Test the aggregation of documentation into a single subroutine object.
     */
    @Test
    public void testAggregateWithDocumentation() {
        final DataTyped returnType = new SimpleDataTyped(DataType.I2);
        final SubroutineArgumentDeclaration argDecl = new SubroutineArgumentDeclaration("arg1", true, DataType.DQ8);
        final Parameter argParam = new Parameter(argDecl.getName(), "this is a description of the parameter");

        final SubroutineDefinition def = new SubroutineDefinition("subroutine",
                Collections.singletonList(argDecl.getName()));
        final SubroutineDeclaration decl = new SubroutineDeclaration(def.getName(), returnType,
                Collections.singletonList(argDecl));
        final SubroutineDocumentation doc = new SubroutineDocumentation("this is a description of the subroutine",
                Collections.singletonList(argParam), "this is a return description");

        final List<Subroutine> aggregates = aggregator.aggregate(Collections.singletonMap(decl.getName(), decl),
                Collections.singletonList(def), Collections.singletonMap(decl.getName(), doc));
        assertThat(aggregates).hasSize(1);

        final Subroutine aggregate = aggregates.get(0);
        assertThat(aggregate.getName()).isEqualTo(def.getName());
        assertThat(aggregate.getDescription()).isEqualTo(doc.getDescription());
        assertThat(aggregate.getReturnDataDescription()).isEqualTo(doc.getReturnDescription());
        assertThat(aggregate.<DataTyped> getReturnDataType()).isEqualTo(returnType);

        final List<SubroutineArgument> args = aggregate.getArguments();
        assertThat(args).hasSize(1);

        final SubroutineArgument arg = args.get(0);
        assertThat(arg.getName()).isEqualTo(argDecl.getName());
        assertThat(arg.getDataType()).isEqualTo(argDecl.getDataType());
        assertThat(arg.getDescription()).isEqualTo(argParam.getDescription());
        assertThat(arg.isByRef()).isEqualTo(argDecl.isByRef());

    }

    /**
     * Test aggregation when a declaration and definition are available, but documentation is not.
     */
    @Test
    public void testAggregateNoDocumentation() {
        final DataTyped returnType = new SimpleDataTyped(DataType.I2);
        final SubroutineArgumentDeclaration nonCharDecl = new SubroutineArgumentDeclaration("arg1", true, DataType.DQ8);
        final SubroutineCharacterArgumentDeclaration charDecl = new SubroutineCharacterArgumentDeclaration("char_field",
                4783, false);

        final SubroutineDefinition def = new SubroutineDefinition("subroutine",
                Arrays.asList(nonCharDecl.getName(), charDecl.getName()));
        final SubroutineDeclaration decl = new SubroutineDeclaration(def.getName(), returnType,
                Arrays.asList(nonCharDecl, charDecl));

        final List<Subroutine> aggregates = aggregator.aggregate(Collections.singletonMap(decl.getName(), decl),
                Collections.singletonList(def), Collections.<String, SubroutineDocumentation> emptyMap());
        assertThat(aggregates).hasSize(1);

        final Subroutine aggregate = aggregates.get(0);
        assertThat(aggregate.getName()).isEqualTo(def.getName());
        assertThat(aggregate.getDescription()).isEmpty();
        assertThat(aggregate.getReturnDataDescription()).isEmpty();
        assertThat(aggregate.<DataTyped> getReturnDataType()).isEqualTo(returnType);

        final List<SubroutineArgument> args = aggregate.getArguments();
        assertThat(args).hasSize(2);

        final SubroutineArgument nonCharArg = args.get(0);
        assertThat(nonCharArg.getName()).isEqualTo(nonCharDecl.getName());
        assertThat(nonCharArg.getDataType()).isEqualTo(nonCharDecl.getDataType());
        assertThat(nonCharArg.getDescription()).isEmpty();
        assertThat(nonCharArg.isByRef()).isEqualTo(nonCharDecl.isByRef());

        final SubroutineArgument charArg = args.get(1);
        assertThat(charArg.getName()).isEqualTo(charDecl.getName());
        assertThat(charArg.getDataType()).isEqualTo(charDecl.getDataType());
        assertThat(charArg.getDescription()).isEmpty();
        assertThat(charArg.isByRef()).isEqualTo(charDecl.isByRef());
    }
}
