/*
 * Copyright (c) 2021, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.gaiax.template.utils

import com.alibaba.fastjson.JSONObject

/**
 * @suppress
 */
class GXCssFileParserUtils {

    companion object {
        private const val INSIDE_SELECTOR = 0
        private const val INSIDE_PROPERTY_NAME = 1
        private const val INSIDE_VALUE = 2

        val instance = GXCssFileParserUtils()
    }

    private var selectorName = StringBuilder()
    private var lastSelectorName = ""

    private var propertyName = StringBuilder()
    private var valueName = StringBuilder()
    private val values: JSONObject = JSONObject()

    private var state: Int = INSIDE_SELECTOR

    private var previousChar: Char? = null

    fun parseToJson(css: String): JSONObject {
        val rules = JSONObject()
        for (i in css.indices) {
            parse(rules, css[i])
        }
        return rules
    }

    private fun parse(rules: JSONObject, c: Char) {
        when (state) {
            INSIDE_SELECTOR -> {
                parseSelector(c)
            }
            INSIDE_PROPERTY_NAME -> {
                parsePropertyName(rules, c)
            }
            INSIDE_VALUE -> {
                parseValue(c)
            }
        }
        // Save the previous character
        previousChar = c
    }

    private fun parseValue(c: Char) {
        if (';' == c) {
            values[propertyName.toString().trim()] = valueName.toString().trim()
            propertyName.clear()
            valueName.clear()
            state = INSIDE_PROPERTY_NAME
            return
        } else {
            valueName.append(c)
            return
        }
    }

    private fun parsePropertyName(rules: JSONObject, c: Char) {
        when (c) {
            ':' -> state = INSIDE_VALUE
            '}' -> {
                val rule = JSONObject()
                rule.putAll(values)
                rules[lastSelectorName] = rule
                values.clear()
                state = INSIDE_SELECTOR
            }
            else -> propertyName.append(c)
        }
    }

    private fun parseSelector(c: Char) {
        if ('{' == c) {
            state = INSIDE_PROPERTY_NAME
            lastSelectorName = selectorName.toString().trim().substring(1)
            selectorName.clear()
            return
        } else {
            selectorName.append(c)
            return
        }
    }
}