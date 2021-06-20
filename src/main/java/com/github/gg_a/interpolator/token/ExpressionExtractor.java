/*
 * Copyright (C) 2021 GG-A, <yiyikela@qq.com, https://github.com/GG-A/string-interpolator>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.gg_a.interpolator.token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.gg_a.interpolator.InterpolationMode;

import static com.github.gg_a.interpolator.token.TokenType.*;

/**
 * Extracting expression from ${}
 *
 * @author GG-A
 * @since 0.0.1
 */
public class ExpressionExtractor {

    /**
     * ${} will parse to $ (String literal). <br>
     * 遇到 ${} 则解析成字符 $
     */
    private static final String $ = "${}";

    private static final List<String> KEYWORDS = Arrays.asList(
            "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
            "const", "continue", "default", "double", "do", "else", "enum", "extends", "false",
            "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof",
            "int", "interface", "long", "native", "new", "null", "package", "private", "protected",
            "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized",
            "this", "throw", "throws", "transient", "true", "try", "void", "volatile", "while", "_"
    );

    /**
     * regex for valid identifiers
     */
    private static final Pattern VALID_IDENTIFIERS = Pattern.compile("^[$A-Za-z_][$\\w]*$");

    /**
     * Regex for extracting expression from ${}, but { or } can't be included in ${}
     */
    private static final Pattern PATTERN = Pattern.compile("\\$\\{((?![{}]).)*}", Pattern.MULTILINE);
    /**
     * StringInterpolator will ignore the parsing when only special characters(exclude $ and _) <br>
     * and numbers are included in ${}. <br>
     * 如果${}中只包含特殊字符（除了$和_以外）和数字，StringInterpolator将会忽略解析。
     */
    private static final Pattern SPECIAL_CHAR = Pattern.compile("^\\$\\{[\\s`!-#%-@\\[-^\\{-~]+}$");

    public List<StringToken> split(String literalValue, int originalOffset, InterpolationMode parseMode) {
        List<StringToken> stringTokens = new ArrayList<>();
        final Matcher matcher = PATTERN.matcher(literalValue);

        int startIndex = 0;
        while (matcher.find()) {
            String matchStr = matcher.group();
            int start = matcher.start();    // 起始字符位置
            int end = matcher.end();        // 结束字符+1 的位置
            int offset = originalOffset + start + $.length();

            if ($.equals(matchStr)) {
                String value = literalValue.substring(startIndex, start + 1);
                stringTokens.add(new StringToken(value, value, STRING_LITERAL, offset));
            } else {
                Matcher mt = SPECIAL_CHAR.matcher(matchStr);
                if (mt.find()) {  // only contains special chars in ${}
                    String value = literalValue.substring(startIndex, end);
                    stringTokens.add(new StringToken(value, value, STRING_LITERAL, offset + matchStr.length()));
                } else {
                    String strInBrace = matchStr.substring(2, matchStr.length() - 1);
                    String afterTrim = strInBrace.trim();

                    if (parseMode == InterpolationMode.EXPRESSION) {
                        if (start != startIndex) {  // 不相等说明${}前面有一段常量还未添加进StringToken
                            String value = literalValue.substring(startIndex, start);
                            stringTokens.add(new StringToken(value, value, STRING_LITERAL, offset));
                        }
                        stringTokens.add(new StringToken(afterTrim, matchStr, EXPRESSION, offset));
                    } else {
                        if (!KEYWORDS.contains(afterTrim) && VALID_IDENTIFIERS.matcher(afterTrim).find()) {
                            if (start != startIndex) {  // 不相等说明${}前面有一段常量还未添加进StringToken
                                String value = literalValue.substring(startIndex, start);
                                stringTokens.add(new StringToken(value, value, STRING_LITERAL, offset));
                            }
                            stringTokens.add(new StringToken(afterTrim, matchStr, EXPRESSION, offset));
                        } else {
                            String value = literalValue.substring(startIndex, end);
                            stringTokens.add(new StringToken(value, value, STRING_LITERAL, offset + matchStr.length()));
                        }
                    }
                }
            }
            startIndex = end;
        }

        if (startIndex < literalValue.length()) {
            String value = literalValue.substring(startIndex);
            stringTokens.add(new StringToken(value, value, STRING_LITERAL, originalOffset + startIndex + $.length()));
        }

        return stringTokens;
    }
}
