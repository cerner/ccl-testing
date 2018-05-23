package com.cerner.ccl.j4ccl.impl.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.cerner.ccl.j4ccl.record.DynamicRecordList;
import com.cerner.ccl.j4ccl.record.Field;
import com.cerner.ccl.j4ccl.record.Record;

/**
 * A utility object to create "set" CCL commands for values within a record structure.
 *
 * @author Joshua Hyde
 *
 */

public class RecordSetter {
    private static final DateTimeFormatter formatter;

    static {
        final DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        builder.appendDayOfMonth(2).appendLiteral('-').appendMonthOfYearShortText().appendLiteral('-').appendYear(4, 4)
                .appendLiteral(' ').appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).appendLiteral(':')
                .appendSecondOfMinute(2).appendLiteral('.').appendMillisOfSecond(3);
        formatter = builder.toFormatter();
    }

    /**
     * Get a list of setter commands for a given record structure.
     *
     * @param record
     *            A {@link Record} object representing the values to be set and the structure of the record structure
     *            for which values are to be set.
     * @return A {@link List} of {@code String} objects representing individual CCL commands to be run.
     */
    public static List<String> getSetterCommands(final Record record) {
        final List<String> commands = new ArrayList<String>();
        final BreadCrumb rootCrumb = new BreadCrumb(record.getName());

        for (final Field field : record.getStructure().getFields())
            getSetterCommands(rootCrumb, field, record, commands);
        return commands;
    }

    /**
     * Get the setter commands for a specific field within a record structure.
     *
     * @param crumb
     *            A {@link BreadCrumb} object used to track where in the record structure the currently-processed
     *            element resides.
     * @param field
     *            A {@link Field} object representing the field for which a value is to be set.
     * @param record
     *            A {@link Record} object representing an instance of a record structure containing the values to be
     *            assigned to the record structure.
     * @param commands
     *            A {@link List} to which the setter commands will be added.
     */
    private static void getSetterCommands(final BreadCrumb crumb, final Field field, final Record record,
            final List<String> commands) {
        switch (field.getType()) {
        case DQ8:
            addDateSetter(crumb, commands, field, record.getDQ8(field.getName()));
            break;
        case I4:
            final int i4Value = record.getI4(field.getName());
            if (i4Value != 0)
                addIntegerSetter(crumb, commands, field, i4Value);
            break;
        case I2:
            final short i2Value = record.getI2(field.getName());
            if (i2Value != 0)
                addIntegerSetter(crumb, commands, field, i2Value);
            break;
        case F8:
            final double f8Value = record.getF8(field.getName());
            if (f8Value != 0)
                addDoubleSetter(crumb, commands, field, f8Value);
            break;
        case VC:
            final String vcValue = record.getVC(field.getName());
            if (!StringUtils.isEmpty(vcValue))
                addStringSetter(crumb, commands, field, vcValue);
            break;
        case CHARACTER:
            final String charValue = record.getChar(field.getName());
            if (!StringUtils.isEmpty(charValue))
                addStringSetter(crumb, commands, field, charValue);
            break;
        case LIST:
            int fixedListIndex = 0;
            for (final Record listRecord : record.getList(field.getName())) {
                final BreadCrumb currentCrumb = new BreadCrumb(crumb,
                        String.format("%s[%d]", field.getName(), ++fixedListIndex));
                for (final Field listField : field.getStructure().getFields())
                    getSetterCommands(currentCrumb, listField, listRecord, commands);
            }
            break;
        case DYNAMIC_LIST:
            final DynamicRecordList dynamicList = record.getDynamicList(field.getName());
            int dynamicListIndex = 0;

            final StringBuilder varListCrumbBuilder = buildBreadCrumbTrail(crumb, field);
            if (dynamicList.getSize() > 0) {
                commands.add(String.format("set stat = alterlist(%s, %d) go", varListCrumbBuilder.toString(),
                        dynamicList.getSize()));

                for (final Record currentRecord : dynamicList) {
                    final BreadCrumb currentCrumb = new BreadCrumb(crumb,
                            String.format("%s[%d]", field.getName(), ++dynamicListIndex));
                    for (final Field listField : field.getStructure().getFields())
                        getSetterCommands(currentCrumb, listField, currentRecord, commands);
                }
            }
            break;
        case RECORD:
            final Record nestedRecord = record.getRecord(field.getName());
            final BreadCrumb nestedRecordCrumb = new BreadCrumb(crumb, field.getName());
            for (final Field recordField : nestedRecord.getStructure().getFields())
                getSetterCommands(nestedRecordCrumb, recordField, nestedRecord, commands);
            break;
        default:
            throw new IllegalArgumentException("Unrecognized data type: " + field.getType());
        }
    }

    /**
     * Add a command to assign a string value to a variable.
     *
     * @param crumb
     *            A {@link BreadCrumb} object that tracks the traversal of the record structure.
     * @param commands
     *            A {@link Collection} of {@code String} objects that is the command queue to which the assignment
     *            operation will be contributed.
     * @param field
     *            A {@link Field} object representing the field to which a value is to be assigned.
     * @param value
     *            A {@code String} object representing the value to be assigned.
     */
    private static void addStringSetter(final BreadCrumb crumb, final Collection<String> commands, final Field field,
            final String value) {
        final StringBuilder setterBuilder = buildSetCommand(crumb, field);
        commands.add(setterBuilder.toString());

        boolean first = true;
        final List<String> sanitizedValues = sanitizeValueLength(setterBuilder, value);
        final boolean hasMultipleLines = sanitizedValues.size() > 1;

        if (hasMultipleLines)
            commands.add("concat(");

        for (final String sanitized : sanitizedValues) {
            if (first)
                first = false;
            else
                commands.add(",");
            /*
             * The command uses single-quote delimitations - rebuild the string so that it properly re-builds the
             * string.
             */
            if (sanitized.contains("'")) {
                commands.add("concat(");

                final String[] pieces = sanitized.split("'");
                commands.add(String.format("'%s'", pieces[0]));
                for (int i = 1, size = pieces.length; i < size; i++) {
                    commands.add(", \"'\"");
                    commands.add(String.format(", '%s'", pieces[i]));
                }
                commands.add(")");
            } else
                commands.add("'" + sanitized + "'");
        }

        if (hasMultipleLines)
            commands.add(")");

        commands.add("go");
    }

    /**
     * Add a command to assign a date value to a variable.
     *
     * @param crumb
     *            A {@link BreadCrumb} object that tracks the traversal of the record structure.
     * @param commands
     *            A {@link Collection} of {@code String} objects that is the command queue to which the assignment
     *            operation will be contributed.
     * @param field
     *            A {@link Field} object representing the field to which a value is to be assigned.
     * @param value
     *            A {@link Date} object representing the value to be assigned.
     */
    private static void addDateSetter(final BreadCrumb crumb, final Collection<String> commands, final Field field,
            final Date value) {
        final StringBuilder builder = buildSetCommand(crumb, field);

        builder.append("cnvtdatetime('").append(formatter.print(value.getTime()).toUpperCase(Locale.getDefault()))
                .append("') go");
        commands.add(builder.toString());
    }

    /**
     * Add a command to assign a double variable to a command queue.
     *
     * @param crumb
     *            A {@link BreadCrumb} object that tracks the traversal of the record structure.
     * @param commands
     *            A {@link Collection} of {@code String} objects that is the command queue to which the assignment
     *            operation will be contributed.
     * @param field
     *            A {@link Field} object representing the field to which a value is to be assigned.
     * @param value
     *            A {@link double} value representing the value to be assigned.
     */
    private static void addDoubleSetter(final BreadCrumb crumb, final Collection<String> commands, final Field field,
            final double value) {
        final StringBuilder builder = buildSetCommand(crumb, field);

        builder.append(String.format("%f go", value));
        commands.add(builder.toString());
    }

    /**
     * Add a command to assign an integer variable to a command queue.
     *
     * @param crumb
     *            A {@link BreadCrumb} object that tracks the traversal of the record structure.
     * @param commands
     *            A {@link Collection} of {@code String} objects that is the command queue to which the assignment
     *            operation will be contributed.
     * @param field
     *            A {@link Field} object representing the field to which a value is to be assigned.
     * @param value
     *            A {@code int} value to be assigned to the field.
     */
    private static void addIntegerSetter(final BreadCrumb crumb, final Collection<String> commands, final Field field,
            final int value) {
        final StringBuilder builder = buildSetCommand(crumb, field);
        builder.append(String.format("%d go", value));
        commands.add(builder.toString());
    }

    private static StringBuilder buildBreadCrumbTrail(final BreadCrumb crumb, final Field field) {
        final StringBuilder crummyBuilder = new StringBuilder(crumb.getCrumb());
        crummyBuilder.append("->").append(field.getName());

        BreadCrumb currentCrumb = crumb;
        while ((currentCrumb = currentCrumb.getParent()) != null)
            crummyBuilder.insert(0, "->").insert(0, currentCrumb.getCrumb());

        return crummyBuilder;
    }

    /**
     * Create a string builder that lays the initial groundwork for the left side of an assignment to a variable.
     *
     * @param crumb
     *            A {@link BreadCrumb} object representing the path up to the given field.
     * @param field
     *            A {@link Field} object representing the field to which the value is to be assigned.
     * @return A {@link StringBuilder} that can be used to construct an assignment operator.
     */
    private static StringBuilder buildSetCommand(final BreadCrumb crumb, final Field field) {
        final StringBuilder crummyBuilder = new StringBuilder(crumb.getCrumb());
        crummyBuilder.append("->").append(field.getName()).append(" = ");

        BreadCrumb currentCrumb = crumb;
        while ((currentCrumb = currentCrumb.getParent()) != null)
            crummyBuilder.insert(0, "->").insert(0, currentCrumb.getCrumb());

        crummyBuilder.insert(0, "set ");
        return crummyBuilder;
    }

    /**
     * To accommodate strings that may exceed the 132-character-maximum of the emulated SSH terminal, ensure that the
     * assigned values are not longer than the assignment statement itself.
     *
     * @param assignmentBuilder
     *            A {@link StringBuilder} that is being used to assemble the left side (including the equals sign) of
     *            the assignment statement.
     * @param value
     *            The value to be assigned to a character record structure member.
     * @return A {@link List} of {@code String} objects, each sized to be no more than 132, less the length of the
     *         assignment statement.
     */
    private static List<String> sanitizeValueLength(final StringBuilder assignmentBuilder, String value) {
        /*
         * If the length of the assignment statement and the data would normally exceed the maximum width when nested
         * within a concat call, then just err on the side of caution and break it up.
         */
        final int assignmentStatementLength = assignmentBuilder.length() + "concat('',\"'\",'')".length();
        if (assignmentStatementLength + value.length() <= 132)
            return Collections.singletonList(value);

        final int maxValueLength = 132 - assignmentStatementLength;
        final List<String> splitValues = new LinkedList<String>();
        splitValues.add(StringUtils.substring(value, 0, maxValueLength));
        value = value.substring(maxValueLength);
        do {
            /*
             * Use 129 to account for the fact that the string will begin and end with single quote marks ("'") and
             * potentially have a comma at the end.
             */
            splitValues.add(StringUtils.substring(value, 0, Math.min(129, value.length())));
            value = value.substring(Math.min(129, value.length()));
        } while (value.length() > 0);
        return splitValues;
    }

    /**
     * An object to assist in a parser's ability to track its traversal down the hierarchy of a record structure data
     * object.
     *
     * @author Joshua Hyde
     *
     */
    private static class BreadCrumb {
        private final BreadCrumb parent;
        private final String crumb;

        /**
         * Create a breadcrumb with no parent.
         *
         * @param crumb
         *            The name of the breadcrumb.
         */
        public BreadCrumb(final String crumb) {
            this(null, crumb);
        }

        /**
         * Create a breadcrumb with a parent.
         *
         * @param parent
         *            A {@link BreadCrumb} object representing the parent breadcrumb of this new breadcrumb.
         * @param crumb
         *            The name of the breadcrumb.
         */
        public BreadCrumb(final BreadCrumb parent, final String crumb) {
            this.parent = parent;
            this.crumb = crumb;
        }

        /**
         * Get the parent breadcrumb.
         *
         * @return {@code null} if this breadcrumb has no parent; otherwise, the breadcrumb object that is its parent.
         */
        public BreadCrumb getParent() {
            return parent;
        }

        /**
         * Get the name of the breadcrumb.
         *
         * @return The name of the breadcrumb.
         */
        public String getCrumb() {
            return crumb;
        }
    }
}
