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

import java.lang.annotation.*;

/**
 * String Interpolator
 *
 * @author GG-A
 * @since 0.0.1
 */
@Target({ElementType.CONSTRUCTOR,
        ElementType.FIELD,
        ElementType.METHOD,
        ElementType.TYPE})
@Retention(RetentionPolicy.CLASS)
@Documented
public @interface StringInterpolator {
    /**
     * Whether enable String Interpolator
     * @return return {@code true} if String Interpolator enable
     */
    boolean value() default true;

    /**
     * Interpolation Mode: IDENTIFIER, EXPRESSION
     * @return InterpolationMode
     */
    InterpolationMode parseMode() default InterpolationMode.EXPRESSION;

}
