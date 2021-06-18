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
package com.github.gg_a.interpolator.token;

/**
 * Created on 2019-09-11
 *
 * internal type which used to split string
 * literal with expressions on the list of tokens.
 *
 * @author Korovin Anatoliy
 */
public class StringToken {

    private String value;
    private String originValue;
    private TokenType type;
    private int offset;

    public StringToken(String value, String originValue, TokenType type, int offset) {
        this.value = value;
        this.originValue = originValue;
        this.type = type;
        this.offset = offset;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getOriginValue() {
        return originValue;
    }

    public void setOriginValue(String originValue) {
        this.originValue = originValue;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String toString() {
        return "StringToken{" +
                "value='" + value + '\'' +
                ", originValue='" + originValue + '\'' +
                ", type=" + type +
                ", offset=" + offset +
                '}';
    }
}
