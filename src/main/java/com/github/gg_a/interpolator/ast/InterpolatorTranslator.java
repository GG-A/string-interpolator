/*
 * Copyright (C) 2019, Korovin Anatoliy. <https://github.com/antkorwin/better-strings>
 *
 * Modifications Copyright (C) 2021, GG-A
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.gg_a.interpolator.ast;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.gg_a.interpolator.*;
import com.github.gg_a.interpolator.token.StringToken;
import com.github.gg_a.interpolator.token.ExpressionExtractor;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;

import static com.github.gg_a.interpolator.token.TokenType.STRING_LITERAL;

/**
 * Created on 2019-09-10
 * <p>
 * AST translator to change the code of String literals
 * on the code of java expressions.
 *
 * @author Korovin Anatoliy
 */
public class InterpolatorTranslator extends TreeTranslator {

    private final TreeMaker treeMaker;
    private final ExpressionExtractor exprExtractor;
    private final ExpressionParser expressionParser;
    private final ElementAnnoInfo elementAnnoInfo;
    private final String currentClassName;
    private final InterpolationMode parseMode;
    private final AtomicInteger annotationCount = new AtomicInteger();

    public InterpolatorTranslator(Context context, ElementAnnoInfo elementAnnoInfo) {
        this.treeMaker = TreeMaker.instance(context);
        this.exprExtractor = new ExpressionExtractor();
        this.expressionParser = new ExpressionParser(Names.instance(context));
        this.elementAnnoInfo = elementAnnoInfo;
        currentClassName = elementAnnoInfo.getParentClassName();
        parseMode = elementAnnoInfo.getInterpolationMode();
    }

    public static <T extends JCTree> void translate(Context context, T t, ElementAnnoInfo elementAnnoInfo) {
        new InterpolatorTranslator(context, elementAnnoInfo).translate(t);
    }

    @Override
    public <T extends JCTree> T translate(T t) {
        return super.translate(t);
    }

    @Override
    public void visitAnnotation(JCTree.JCAnnotation jcAnnotation) {
        annotationCount.incrementAndGet();
        super.visitAnnotation(jcAnnotation);
        annotationCount.decrementAndGet();
    }

    /*
     * Modified by GG-A
     */
    @Override
    public void visitLiteral(JCTree.JCLiteral jcLiteral) {
        super.visitLiteral(jcLiteral);
        if (annotationCount.get() == 0) parseStringLiteral(jcLiteral);
    }

    /*
     * Modified by GG-A
     */
    private void parseStringLiteral(JCTree.JCLiteral jcLiteral) {
        if (jcLiteral.getValue() instanceof String) {
            String literalValue = (String) jcLiteral.getValue();
            int originalOffset = jcLiteral.getPreferredPosition();
            if (literalValue == null || literalValue.equals("")) return;

            List<StringToken> stringTokens = exprExtractor.split(literalValue, originalOffset, parseMode);

            if (stringTokens.isEmpty()) return;

            StringToken stringToken = stringTokens.get(0);
            if (stringTokens.size() == 1) {
                JCTree.JCExpression result = convertToExpr(stringToken, currentClassName);
                this.result = (result == null) ? jcLiteral : result;
                return;
            }

            JCTree.JCExpression exprLeft = convertToExpr(stringToken, currentClassName);
            exprLeft = handleNull(stringToken, exprLeft);

            for (int i = 1; i < stringTokens.size(); i++) {
                StringToken st = stringTokens.get(i);
                JCTree.JCExpression exprRight = convertToExpr(st, currentClassName);
                exprRight = handleNull(st, exprRight);

                exprLeft = treeMaker.Binary(JCTree.Tag.PLUS, exprLeft, exprRight);
                exprLeft.setPos(stringToken.getOffset());
            }

            result = exprLeft;
        }
    }

    private JCTree.JCExpression handleNull(StringToken stringToken, JCTree.JCExpression expr) {
        if (expr == null) {
            stringToken.setType(STRING_LITERAL);
            stringToken.setValue(stringToken.getOriginValue());
            expr = convertToExpr(stringToken, currentClassName);
        }
        return expr;
    }

    private JCTree.JCExpression convertToExpr(StringToken stringToken, String currentClassName) {
        switch (stringToken.getType()) {
            case EXPRESSION:
                return expressionParser.parse(stringToken, currentClassName);
            case STRING_LITERAL:
                JCTree.JCLiteral literal = treeMaker.Literal(stringToken.getValue());
                literal.setPos(stringToken.getOffset());
                return literal;
            default:
                throw new RuntimeException("Unexpected token type: " + stringToken.getType());
        }
    }
}
