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

import javax.lang.model.element.Element;

/**
 * Element and Annotation Info
 *
 * @author GG-A
 * @since 0.0.1
 */
public class ElementAnnoInfo {
    private Element element;
    private String parentClassName;
    private InterpolationMode interpolationMode;

    public ElementAnnoInfo(Element element, String parentClassName, InterpolationMode interpolationMode) {
        this.element = element;
        this.parentClassName = parentClassName;
        this.interpolationMode = interpolationMode;
    }

    public Element getElement() {
        return element;
    }

    public String getParentClassName() {
        return parentClassName;
    }

    public InterpolationMode getInterpolationMode() {
        return interpolationMode;
    }

    @Override
    public String toString() {
        return "ElementAnnoInfo{" +
                "element=" + element +
                ", \t\tparentClassName='" + parentClassName + '\'' +
                ", \t\tinterpolationMode=" + interpolationMode +
                '}';
    }
}
