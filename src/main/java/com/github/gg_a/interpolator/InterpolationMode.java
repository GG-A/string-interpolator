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

/**
 * Interpolation Mode
 *
 * @author GG-A
 * @since 0.0.1
 */
public enum InterpolationMode {
    /**
     * only parse Java valid identifiers in ${}, invalid identifiers will be ignore.
     */
    IDENTIFIER,
    /**
     * parse expresssion, include: <br>
     * operation expression(+, -, *, /, etc.), methods call, constructor call, etc.
     */
    EXPRESSION
}
