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

package com.alibaba.gaiax.template.expression

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.analyze.GXAnalyze
import com.alibaba.gaiax.analyze.GXArray
import com.alibaba.gaiax.analyze.GXMap
import com.alibaba.gaiax.analyze.GXString
import com.alibaba.gaiax.utils.getAnyExt

/**
 * @suppress
 */
object GXExpressionUtils {

    private lateinit var analyze: GXAnalyze

    fun initAnalyze() {
        analyze = GXAnalyze()
        analyze.initComputeExtend(object : GXAnalyze.IComputeExtend {

            /**
             * 用于处理取值逻辑
             */
            override fun computeValueExpression(valuePath: String, source: Any?): Long {
                if (valuePath == "$$") {
                    if (source is JSONArray) {
                        return GXAnalyze.createValueArray(source)
                    } else if (source is JSONObject) {
                        return GXAnalyze.createValueMap(source)
                    }
                }
                if (source is JSONObject) {
                    when (val value = source.getAnyExt(valuePath)) {
                        is JSONArray -> {
                            return GXAnalyze.createValueArray(value)
                        }
                        is JSONObject -> {
                            return GXAnalyze.createValueMap(value)
                        }
                        is Boolean -> {
                            return GXAnalyze.createValueBool(value)
                        }
                        is String -> {
                            return GXAnalyze.createValueString(value)
                        }
                        is Int -> {
                            return GXAnalyze.createValueFloat64(value.toFloat())
                        }
                        is Float -> {
                            return GXAnalyze.createValueFloat64(value)
                        }
                        null -> {
                            return GXAnalyze.createValueNull()
                        }
                    }
                }
                return 0L
            }

            /**
             * 用于处理函数逻辑
             */
            override fun computeFunctionExpression(functionName: String, params: LongArray): Long {
                if (functionName == "size" && params.size == 1) {
                    when (val value = GXAnalyze.wrapAsGXValue(params[0])) {
                        is GXString -> {
                            value.getString()?.let {
                                return GXAnalyze.createValueFloat64(it.length.toFloat())
                            }
                        }
                        is GXMap -> {
                            (value.getValue() as? JSONObject)?.let {
                                return GXAnalyze.createValueFloat64(it.size.toFloat())
                            }
                        }
                        is GXArray -> {
                            (value.getValue() as? JSONArray)?.let {
                                return GXAnalyze.createValueFloat64(it.size.toFloat())
                            }
                        }
                    }
                } else if (functionName == "env") {
                }
                return 0L
            }
        })
    }

    class GXAnalyzeWrapper(val expression: String) : GXIExpression {

        fun valuePath(): String? {
            if (expression.startsWith("$")) {
                return expression.substring(1, expression.length)
            }
            return null
        }

        override fun value(templateData: JSON?): Any? {
            return analyze.getResult(expression, templateData)
        }
    }

    class GXAnalyzeJsonWrapper(val expression: JSONObject) : GXIExpression {

        override fun value(templateData: JSON?): Any? {
            return value2(expression, templateData)
        }

        private fun value2(expression: JSONObject, templateData: JSON?): Any? {
            val result = JSONObject()
            expression.forEach {
                val value = it.value
                if (value is String) {
                    result[it.key] = analyze.getResult(value, templateData)
                } else if (value is JSONObject) {
                    result[it.key] = value2(value, templateData)
                }
            }
            return result
        }
    }

    fun create(expression: Any?): GXIExpression? {
        return when (expression) {
            is String -> GXAnalyzeWrapper(expression)
            is JSONObject -> GXAnalyzeJsonWrapper(expression)
            else -> null
        }
    }
}