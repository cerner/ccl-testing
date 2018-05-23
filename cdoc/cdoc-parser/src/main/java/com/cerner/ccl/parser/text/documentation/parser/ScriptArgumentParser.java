package com.cerner.ccl.parser.text.documentation.parser;

import org.apache.commons.lang.StringUtils;

import com.cerner.ccl.parser.data.ScriptArgument;

/**
 * A parser that parsers out {@link ScriptArgument} objects from {@code @arg} documentation tags.
 * 
 * @author Joshua Hyde
 * 
 */

public class ScriptArgumentParser extends AbstractMultiTagParser<ScriptArgument> {

    @Override
    protected boolean isParseable(final String line) {
        return line.contains("@arg");
    }

    @Override
    protected ScriptArgument parseElement(final String line) {
        final int spacePos = line.indexOf(' ');
        return new ScriptArgument(StringUtils.strip(line.substring(spacePos + 1)));
    }

}
