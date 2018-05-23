package com.cerner.ccl.parser.text.record.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cerner.ccl.parser.data.DataType;
import com.cerner.ccl.parser.data.record.StructureMember;
import com.cerner.ccl.parser.text.data.util.CodeParserSupport;
import com.cerner.ccl.parser.text.documentation.parser.AbstractIndexedParser;
import com.cerner.ccl.parser.text.record.AbstractParentStructure;
import com.cerner.ccl.parser.text.record.FixedLengthStructureList;
import com.cerner.ccl.parser.text.record.Structure;
import com.cerner.ccl.parser.text.record.StructureCharacterField;
import com.cerner.ccl.parser.text.record.StructureField;
import com.cerner.ccl.parser.text.record.StructureInclude;
import com.cerner.ccl.parser.text.record.StructureList;
import com.cerner.ccl.parser.text.record.StructureRecord;
import com.google.code.jetm.reporting.ext.PointFactory;

import etm.core.monitor.EtmPoint;

/**
 * A parser that produces {@link Structure} objects out of CCL source code.
 *
 * @author Joshua Hyde
 *
 */

public class StructureParser extends AbstractIndexedParser<Structure> {
    private final Logger logger = LoggerFactory.getLogger(StructureParser.class);
    private static final Pattern ITEM_PATTERN = Pattern
            .compile("((\\d+)\\s+(\\w+)(?:\\s*=\\s*(\\S+)|\\s*(\\[[^]]+\\])|\\s+(\\d+))|%i\\s+[^\\s]+)");

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canParse(final String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null.");
        }

        return StringUtils.strip(line).startsWith("record ");
    }

    private static class NextItemResponse {
        private final int level;
        private final String name;
        private final String type;
        private final int endingPosition;

        /**
         * @param level
         *            The level of the next item
         * @param name
         *            The name of the next item
         * @param type
         *            The type of the next item
         * @param endingPosition
         *            The position where the item ends within the source
         */
        public NextItemResponse(int level, String name, String type, int endingPosition) {
            super();
            this.level = level;
            this.name = name;
            this.type = type;
            this.endingPosition = endingPosition;
        }

        public int getLevel() {
            return level;
        }

        public String getName() {
            return name;
        }

        public String getType() {
            return type;
        }

        public int getEndingPosition() {
            return endingPosition;
        }

    }

    NextItemResponse getNextItem(String source, int startingPosition) {
        Matcher matcher = ITEM_PATTERN.matcher(source);
        if (startingPosition < source.length() && matcher.find(startingPosition)) {
            return new NextItemResponse(matcher.group(2) != null ? Integer.parseInt(matcher.group(2)) : 0,
                    matcher.group(3) != null ? matcher.group(3) : matcher.group(1),
                    matcher.group(4) != null ? matcher.group(4)
                            : matcher.group(5) != null ? matcher.group(5) : matcher.group(6) != null ? "rec" : "inc",
                    matcher.group(6) != null ? matcher.end() - matcher.group(6).length() - 1 : matcher.end());
        }
        return new NextItemResponse(0, "", "", 0);
    }

    @Override
    public Structure parse(final int startingIndex, final List<String> lines) {
        final EtmPoint point = PointFactory.getPoint(getClass(), "parse(int, List)");
        logger.debug("StructureParser.parse");
        try {
            CodeParserSupport.ExtendToRightParenResponse response = CodeParserSupport.extendToRightParen(lines, "",
                    startingIndex - 1, "");
            setEndingIndex(response.getFinalLineNumber());
            final String declaration = response.getResponse();
            final String name = getName(declaration);
            final int leftParenPosition = declaration.indexOf("(");
            final int rightParenPosition = declaration.indexOf(")");
            final String definition = declaration.substring(leftParenPosition + 1, rightParenPosition);
            logger.debug("declaration: {}", declaration);

            final List<StructureMember> rootMembers = new ArrayList<StructureMember>();
            final List<AbstractParentStructure> parentLists = new ArrayList<AbstractParentStructure>();
            NextItemResponse nextItemResponse = getNextItem(definition, 0);
            while (nextItemResponse.getLevel() > 0 || nextItemResponse.getType().equals("inc")) {
                String nextItemName = nextItemResponse.getName();
                String nextItemType = nextItemResponse.getType();
                int nextItemLevel = nextItemResponse.getLevel();
                logger.debug("{} {} {} {}", nextItemLevel, nextItemName, nextItemType,
                        nextItemResponse.getEndingPosition());
                if (!nextItemResponse.getType().equals("inc")) {
                    while (nextItemLevel <= parentLists.size()) {
                        parentLists.remove(parentLists.size() - 1);
                    }
                }
                if (nextItemType.equals("inc")) {
                    StructureMember include = isStatusBlockInclude(nextItemName) ? createStatusData()
                            : parseInclude(nextItemName);
                    if (isStatusBlockInclude(nextItemName)) {
                        nextItemLevel = 1;
                        parentLists.clear();
                    }
                    if (parentLists.size() > 1) {
                        parentLists.get(parentLists.size() - 1).addChildMember(include);
                    } else {
                        rootMembers.add(include);
                    }
                } else if (nextItemType.startsWith("[") || nextItemType.equals("rec")) {
                    AbstractParentStructure structure = null;
                    if (nextItemType.equals("rec")) {
                        structure = new StructureRecord(nextItemName, nextItemLevel);
                    } else if (nextItemType.contains("*")) {
                        structure = new StructureList(nextItemName, nextItemLevel);
                    } else {
                        structure = new FixedLengthStructureList(nextItemName, nextItemLevel,
                                Integer.parseInt(nextItemType.substring(1, nextItemType.length() - 1).trim()));
                    }
                    if (nextItemLevel == 1) {
                        rootMembers.add(structure);
                    } else {
                        parentLists.get(parentLists.size() - 1).addChildMember(structure);
                    }
                    parentLists.add(structure);
                } else {
                    final DataType dataType = DataType.forDeclaration(nextItemType);
                    StructureField structureField = DataType.CHAR.equals(dataType)
                            ? new StructureCharacterField(nextItemName, nextItemLevel,
                                    Integer.parseInt(nextItemType.substring(1)))
                            : new StructureField(nextItemName, nextItemLevel, dataType);
                    if (nextItemLevel == 1) {
                        rootMembers.add(structureField);
                    } else {
                        parentLists.get(parentLists.size() - 1).addChildMember(structureField);
                    }
                }
                nextItemResponse = getNextItem(definition, 1 + nextItemResponse.getEndingPosition());
            }
            return new Structure(name, rootMembers);

        } finally {
            point.collect();
        }
    }

    /**
     * Create the {@code status_data} record from {@code status_block.inc}.
     *
     * @return A {@link StructureMember} representing the structure of the {@code status_data} from
     *         {@code status_block.inc}.
     */
    private StructureMember createStatusData() {
        final StructureField targetObjectValue = new StructureField("TargetObjectValue", 3, DataType.VC);
        final StructureCharacterField targetObjectName = new StructureCharacterField("TargetObjectName", 3, 25);
        final StructureCharacterField operationStatus = new StructureCharacterField("OperationStatus", 3, 1);
        final StructureCharacterField operationName = new StructureCharacterField("OperationName", 3, 25);

        final FixedLengthStructureList subeventStatus = new FixedLengthStructureList("subeventstatus", 2, 1);
        subeventStatus.addChildMember(operationName);
        subeventStatus.addChildMember(operationStatus);
        subeventStatus.addChildMember(targetObjectName);
        subeventStatus.addChildMember(targetObjectValue);

        final StructureRecord statusData = new StructureRecord("status_data", 1);
        statusData.addChildMember(new StructureCharacterField("status", 2, 1));
        statusData.addChildMember(subeventStatus);

        return statusData;
    }

    /**
     * Get the name of the record structure.
     *
     * @param line
     *            The line from which the record structure name is to be parsed.
     * @return The name of the record structure.
     */
    private String getName(final String line) {
        final String stripped = StringUtils.strip(line);
        final int firstSpacePos = stripped.indexOf(' ');
        final int parenPos = stripped.indexOf('(');
        return StringUtils.strip(
                parenPos < 0 ? stripped.substring(firstSpacePos + 1) : stripped.substring(firstSpacePos + 1, parenPos));
    }

    /**
     * Determine whether or not the given line is the inclusion of the popular {@code status_block.inc} file.
     *
     * @param line
     *            The line to be examined.
     * @return {@code true} if the given line is an inclusion of the {@code status_block.inc} include file;
     *         {@code false} if not.
     */
    private boolean isStatusBlockInclude(final String line) {
        return line.startsWith("%i cclsource:status_block.inc");
    }

    /**
     * Parse an include file from the given file.
     *
     * @param line
     *            The line from which the include file's filename is to be parsed.
     * @return A {@link StructureInclude} representing the given line.
     */
    private StructureInclude parseInclude(final String line) {
        final int firstSpacePos = line.indexOf(' ');
        final int secondSpacePos = line.indexOf(' ', firstSpacePos + 1);
        return new StructureInclude(secondSpacePos < 0 ? line.substring(firstSpacePos + 1)
                : line.substring(firstSpacePos + 1, secondSpacePos));
    }
}
