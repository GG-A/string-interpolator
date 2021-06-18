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
package com.github.gg_a.interpolator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

import com.sun.source.util.Trees;
import com.sun.tools.javac.api.JavacTrees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.github.gg_a.interpolator.ast.InterpolatorTranslator;

import static javax.tools.Diagnostic.Kind.*;

/**
 * Processor for {@link StringInterpolator} annotation
 *
 * @author GG-A
 * @since 0.0.1
 */
@SupportedAnnotationTypes("com.github.gg_a.interpolator.StringInterpolator")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class InterpolatorProcessor extends AbstractProcessor {

    private JavacProcessingEnvironment env;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        messager = processingEnv.getMessager();
        messager.printMessage(NOTE, ">>> StringInterpolator is running!");
        env = (JavacProcessingEnvironment) processingEnv;
        super.init(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (roundEnv.processingOver()) return false;

        if (!annotations.isEmpty()) {
            Context context = env.getContext();
            Trees trees = Trees.instance(env);

            Set<Element> elements = new HashSet<>();
            List<ElementAnnoInfo> elementAnnoInfos = new ArrayList<>();

            Set<? extends Element> elementsAnno = roundEnv.getElementsAnnotatedWith(StringInterpolator.class);

            elementsAnno.forEach(e -> getElements(elements, elementAnnoInfos, e, null));
            elementAnnoInfos.forEach(e -> {
                JCTree tree = ((JavacTrees) trees).getTree(e.getElement());
                InterpolatorTranslator.translate(context, tree, e);
            });

            return true;
        }

        return false;
    }

    private void getElements(Set<Element> elements, List<ElementAnnoInfo> elementAnnoInfos, Element e, InterpolationMode parentParseMode) {
        StringInterpolator annotation = e.getAnnotation(StringInterpolator.class);
        if (annotation != null) {
            if (annotation.value())
                getElementsWithAnno(elements, elementAnnoInfos, e, annotation.parseMode());
        } else {
            if (parentParseMode != null)
                getElementsWithAnno(elements, elementAnnoInfos, e, parentParseMode);
        }
    }

    private void getElementsWithAnno(Set<Element> elements, List<ElementAnnoInfo> elementAnnoInfos, Element e, InterpolationMode parentParseMode) {
        if (isType(e)) {
            List<? extends Element> enclosedElements = e.getEnclosedElements();
            for (Element enclosedElement : enclosedElements) {
                if (isType(enclosedElement)) {
                    getElements(elements, elementAnnoInfos, enclosedElement, parentParseMode);
                } else {
                    StringInterpolator anno = enclosedElement.getAnnotation(StringInterpolator.class);
                    if (anno == null) {
                        addElementToSet(elements, elementAnnoInfos, enclosedElement, parentParseMode);
                    } else {
                        if (anno.value())
                            addElementToSet(elements, elementAnnoInfos, enclosedElement, anno.parseMode());
                    }
                }
            }
        } else {
            addElementToSet(elements, elementAnnoInfos, e, parentParseMode);
        }
    }

    private void addElementToSet(Set<Element> elements, List<ElementAnnoInfo> elementAnnoInfos, Element e, InterpolationMode interpolationMode) {
        if (!elements.contains(e)) {
            Element enclosingElement = e.getEnclosingElement();
            elements.add(e);
            elementAnnoInfos.add(new ElementAnnoInfo(e, enclosingElement.toString(), interpolationMode));
        }
    }

    private boolean isType(Element codeElement) {
        return codeElement.getKind() == ElementKind.CLASS ||
                codeElement.getKind() == ElementKind.INTERFACE ||
                codeElement.getKind() == ElementKind.ENUM;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

}
