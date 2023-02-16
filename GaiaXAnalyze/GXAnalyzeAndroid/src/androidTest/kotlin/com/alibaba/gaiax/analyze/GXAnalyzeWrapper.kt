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

package com.alibaba.gaiax.analyze

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import java.math.BigDecimal

object GXAnalyzeWrapper {

    val analyze = GXAnalyze()

    init {
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
                            return GXAnalyze.createValueLong(value.toLong())
                        }
                        is Float -> {
                            return GXAnalyze.createValueFloat64(value)
                        }
                        is Double -> {
                            return GXAnalyze.createValueFloat64(value.toFloat())
                        }
                        is BigDecimal -> {
                            return GXAnalyze.createValueFloat64(value.toFloat())
                        }
                        is Long -> {
                            return GXAnalyze.createValueLong(value)
                        }
                        null -> {
                            return GXAnalyze.createValueNull()
                        }
                        else -> {
                            throw IllegalArgumentException("Not recognize value = $value")
                        }
                    }
                }
                return 0L
            }

            /**
             * 用于处理函数逻辑
             */
            override fun computeFunctionExpression(
                functionName: String, params: LongArray
            ): Long {
                if (functionName == "size" && params.size == 1) {
                    return functionSize(params)
                } else if (functionName == "env" && params.size == 1) {
                    return functionEnv(params)
                }
                return 0L
            }
        })
    }

    private fun functionEnv(params: LongArray): Long {
        val value = GXAnalyze.wrapAsGXValue(params[0])
        if (value is GXString) {
            val envValue = value.getString()
            if ("isAndroid".equals(envValue, ignoreCase = true)) {
                return GXAnalyze.createValueBool(true)
            } else if ("isiOS".equals(envValue, ignoreCase = true)) {
                return GXAnalyze.createValueBool(false)
            }
        }
        return 0L
    }

    private fun functionSize(params: LongArray): Long {
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
            else -> {
                return GXAnalyze.createValueFloat64(0f)
            }
        }
        // nothing
        return 0L
    }
}
