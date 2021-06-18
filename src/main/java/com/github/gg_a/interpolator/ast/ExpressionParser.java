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

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;
import javax.tools.*;

import com.sun.source.tree.CompilationUnitTree;
import com.sun.tools.javac.api.JavacTaskImpl;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.Names;
import com.github.gg_a.interpolator.token.StringToken;

/**
 * Expression Parser
 * @author Korovin Anatoliy
 */
public class ExpressionParser {
    public static Logger logger = Logger.getLogger(ExpressionParser.class.toString());

    private final Names names;
    private DiagnosticCollector<JavaFileObject> diagColl;

    public ExpressionParser(Names names) {
        this.names = names;
    }

    public JCTree.JCExpression parse(StringToken token, String currentClassName) {
        CompilationUnitTree tree = getCompilationUnitTree(token.getValue(), currentClassName);
        if (tree == null) return null;
        JCTree.JCClassDecl declr = (JCTree.JCClassDecl) tree.getTypeDecls().get(0);
        JCTree.JCVariableDecl field = (JCTree.JCVariableDecl) declr.getMembers().get(0);
        JCTree.JCExpression expression = field.getInitializer();
        expression.setPos(token.getOffset());
        expression.accept(new IdentResolver(token.getOffset()));
        return expression;
    }

    /*
     * Modified by GG-A
     */
    private CompilationUnitTree getCompilationUnitTree(String code, String currentClassName) {
        JavaCompiler tool = ToolProvider.getSystemJavaCompiler();
        JavaFileManager fm = tool.getStandardFileManager(null, null, null);
        List<FakeJavaFileWrapper> javaFile = Collections.singletonList(new FakeJavaFileWrapper(code));
        diagColl = new DiagnosticCollector<>();
        JavacTaskImpl ct = (JavacTaskImpl) tool.getTask(null, fm, diagColl, null, null, javaFile);
        try {
            CompilationUnitTree unitTree = ct.parse().iterator().next();
            List<Diagnostic<? extends JavaFileObject>> diagnostics = diagColl.getDiagnostics();
            if (diagnostics.size() > 0) {
                Locale aDefault = Locale.getDefault();
                String skipParse = aDefault.getLanguage().equals("zh")
                        ? "字符串插值器将‘忽略’此表达式的解析。"
                        : "String Interpolator will 'ignore' parse this symbol or expression.";
                String msg = "";
                for (Diagnostic<? extends JavaFileObject> diag : diagnostics) {
                    msg += diag.getCode() + ": " + diag.getMessage(aDefault) + "\n";
                }
                logger.warning(msg
                        + ">>>>>>  " + skipParse + "\n"
                        + "symbol: " + code + "\n"
                        + "position: in class: " + currentClassName
                );

                return null;
            }

            return unitTree;
        } catch (Throwable e) {
            // Depending on JDK version, `parse()` either throws or does not throw `IOException`;
            // to avoid setup-dependent compilation errors, let's just catch Exception here.
            e.printStackTrace();
            throw new RuntimeException("Error while parsing expression in the string literal: " + code, e);
        }
    }

    private class FakeJavaFileWrapper extends SimpleJavaFileObject {

        private final String text;

        public FakeJavaFileWrapper(String text) {
            super(URI.create("myfake:/Test.java"), Kind.SOURCE);
            this.text = "class Test { Object value = String.valueOf(" + text + "); }";
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return text;
        }
    }

    private class IdentResolver extends TreeTranslator {

        private final int offset;

        public IdentResolver(int offset) {
            this.offset = offset;
        }

        @Override
        public void visitApply(JCTree.JCMethodInvocation jcMethodInvocation) {
            super.visitApply(jcMethodInvocation);
            jcMethodInvocation.pos = offset;
        }

        @Override
        public void visitIdent(JCTree.JCIdent jcIdent) {
            super.visitIdent(jcIdent);
            jcIdent.name = names.fromString(jcIdent.getName().toString());
            jcIdent.pos = offset;
        }

        @Override
        public void visitSelect(JCTree.JCFieldAccess jcFieldAccess) {
            super.visitSelect(jcFieldAccess);
            jcFieldAccess.name = names.fromString(jcFieldAccess.name.toString());
            jcFieldAccess.pos = offset;
        }
    }
}
