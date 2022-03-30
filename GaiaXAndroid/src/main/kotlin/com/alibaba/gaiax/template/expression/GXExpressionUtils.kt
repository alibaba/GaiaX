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
            override fun computeValueExpression(valuePath: String, source: Any): Long {
                if (source is JSONObject) {
                    val value = source.getAnyExt(valuePath)
                    if (value is JSONArray) {
                        return GXAnalyze.createValueArray(value)
                    } else if (value is JSONObject) {
                        return GXAnalyze.createValueMap(value)
                    } else if (value is Boolean) {
                        return GXAnalyze.createValueBool(value)
                    } else if (value is String) {
                        return GXAnalyze.createValueString(value)
                    } else if (value is Int) {
                        return GXAnalyze.createValueFloat64(value.toFloat())
                    } else if (value is Float) {
                        return GXAnalyze.createValueFloat64(value)
                    }
                }
                return 0L
            }

            /**
             * 用于处理函数逻辑
             */
            override fun computeFunctionExpression(functionName: String, params: LongArray): Long {
                if (functionName == "size" && params.size == 1) {
                    val value = GXAnalyze.wrapAsGXValue(params[0])
                    if (value is GXString) {
                        value.getString()?.let {
                            return GXAnalyze.createValueFloat64(it.length.toFloat())
                        }
                    } else if (value is GXMap) {
                        (value.getValue() as? JSONObject)?.let {
                            return GXAnalyze.createValueFloat64(it.size.toFloat())
                        }
                    } else if (value is GXArray) {
                        (value.getValue() as? JSONArray)?.let {
                            return GXAnalyze.createValueFloat64(it.size.toFloat())
                        }
                    }
                }
                return 0L
            }
        })
    }

    class GXAnalyzeWrapper(val expression: String) : GXIExpression {

        override fun value(templateData: JSON?): Any? {
            return analyze.getResult(expression, templateData ?: JSONObject())
        }
    }

    fun create(expression: Any?): GXIExpression? {
        return if (expression is String) {
            GXAnalyzeWrapper(expression)
        } else {
            null
        }
    }
}