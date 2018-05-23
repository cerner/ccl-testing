package com.cerner.ccl.parser.text.data.util;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * Common CCL code parsing utilities.
 *
 * @author Fred Eckertson
 *
 */
@SuppressWarnings("javadoc")
public class CodeParserSupport {
    public static class ExtendToRightParenResponse {
        private final String response;
        private final int finalLineNumber;

        public ExtendToRightParenResponse(String response, int finalLineNumber) {
            this.response = response;
            this.finalLineNumber = finalLineNumber;
        }

        public String getResponse() {
            return response;
        }

        public int getFinalLineNumber() {
            return finalLineNumber;
        }
    }


    static class ExtendLineResponse {
        private final String response;
        private final int finalLineNumber;

        public ExtendLineResponse(String response, int finalLineNumber) {
            this.response = response;
            this.finalLineNumber = finalLineNumber;
        }

        public String getResponse() {
            return response;
        }

        public int getFinalLineNumber() {
            return finalLineNumber;
        }
    }

    public static ExtendLineResponse extendLine(final List<String> lines, final String source, final int lineNumber,
            String mask) {
        String response = source;
        int finalLineNumber = lineNumber;
        MaskTextResponse maskTextResponse = maskText(response, mask);
        response = maskTextResponse.getResponse();
        while (maskTextResponse.recheckLine() || maskTextResponse.continueLine()) {
            if (maskTextResponse.continueLine()) {
                if (response.endsWith("\\")) {
                    response = response.substring(0, response.length() - 1);
                }
                response = response + " " + lines.get(++finalLineNumber);
            } else {
                response = maskTextResponse.getResponse();
            }
            maskTextResponse = maskText(response, mask);
            response = maskTextResponse.getResponse();
        }
        if (finalLineNumber == lineNumber) {
            if (response.endsWith("\\")) {
                response = response.substring(0, response.length() - 1);
            }
            response = response + " " + lines.get(++finalLineNumber);
        }
        maskTextResponse = maskText(response, mask);
        response = maskTextResponse.getResponse();
        while (maskTextResponse.recheckLine() || maskTextResponse.continueLine()) {
            if (maskTextResponse.continueLine()) {
                if (response.endsWith("\\")) {
                    response = response.substring(0, response.length() - 1);
                }
                response = response + " " + lines.get(++finalLineNumber);
            } else {
                response = maskTextResponse.getResponse();
            }
            maskTextResponse = maskText(response, mask);
            response = maskTextResponse.getResponse();
        }
        return new ExtendLineResponse(response, finalLineNumber);
    }

    public static ExtendToRightParenResponse extendToRightParen(final List<String> lines, final String source,
            final int lineNumber, String mask) {
        int finalLineNumber = lineNumber;
        String response = source;

        while (response.indexOf(")") == -1) {
            ExtendLineResponse extendLineResponse = extendLine(lines, response, finalLineNumber, mask);
            response = extendLineResponse.getResponse();
            finalLineNumber = extendLineResponse.getFinalLineNumber();
        }
        return new ExtendToRightParenResponse(response, finalLineNumber);
    }

    static class MaskTextResponse {
        private final String response;
        private final boolean continuation;
        private final boolean recheck;

        public MaskTextResponse(String response, boolean continuation, boolean recheck) {
            this.response = response;
            this.continuation = continuation;
            this.recheck = recheck;
        }

        public String getResponse() {
            return response;
        }

        public boolean continueLine() {
            return continuation;
        }

        public boolean recheckLine() {
            return recheck;
        }
    }

    static MaskTextResponse maskText(final String source, String mask) {
        final Matcher matcher = Pattern.compile("(?i:@\\d+\\:|\\\"|\\^|~|'|\\||;|![^=]|/\\*)").matcher(source);
        if (matcher.find()) {
            final int startPosition = matcher.start();
            final String matchGroup = matcher.group(0);
            final int matchGroupLength = matchGroup.length();
            if (matchGroup.equals("/*")) {
                int endPosition = source.indexOf("*/", startPosition + 1);
                if (endPosition > -1) {
                    return new MaskTextResponse(source.substring(0, startPosition)
                            + StringUtils.repeat(mask, 2 + endPosition - startPosition)
                            + source.substring(endPosition + 2, source.length()), false, true);
                }
                return new MaskTextResponse(source, true, false);
            }
            if (matchGroup.startsWith(";") || matchGroup.startsWith("!")) {
                if (source.endsWith("\\")) {
                    return new MaskTextResponse(source, true, false);
                }
                return new MaskTextResponse(
                        source.substring(0, startPosition) + StringUtils.repeat(mask, source.length() - startPosition),
                        false, false); // no need to re-check.
            }
            // if it is not an @, then the CCL compile should fail
            if (matchGroup.startsWith("@")) {
                int quoteLength = Integer.parseInt(matchGroup.substring(1, matchGroupLength - 1));
                if (source.length() < startPosition + matchGroupLength + quoteLength + 1) {
                    return new MaskTextResponse(source, true, false);
                }
                if (source.substring(startPosition + matchGroupLength + quoteLength,
                        startPosition + matchGroupLength + quoteLength + 1).equals("@")) {
                    return new MaskTextResponse(source.substring(0, matcher.start())
                            + StringUtils.repeat(mask, matchGroupLength + quoteLength + 1)
                            + source.substring(startPosition + matchGroupLength + quoteLength + 1, source.length()),
                            false, true);
                }
            }
            int endPosition = source.indexOf(matchGroup, startPosition + 1);
            if (endPosition > -1) {
                return new MaskTextResponse(
                        source.substring(0, startPosition) + StringUtils.repeat(mask, 1 + endPosition - startPosition)
                                + source.substring(endPosition + 1, source.length()),
                        false, true);
            }
            return new MaskTextResponse(source, true, false);
        }
        return new MaskTextResponse(source, false, false);
    }
}
