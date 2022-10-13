package com.alibaba.gaiax.utils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.analyze.GXAnalyze
import com.alibaba.gaiax.analyze.GXArray
import com.alibaba.gaiax.analyze.GXMap
import com.alibaba.gaiax.analyze.GXString
import com.alibaba.gaiax.template.GXIExpression
import java.math.BigDecimal

class GXOpenExpression(private val expression: Any) : GXIExpression {
    override fun expression(): Any {
        return expression
    }

    override fun value(templateData: JSON?): Any? {
        return analyze.getResult(expression, templateData)
    }

    companion object {
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
                                return GXAnalyze.createValueFloat64(value.toFloat())
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
                    functionName: String,
                    params: LongArray
                ): Long {
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
                            else -> {
                                return GXAnalyze.createValueFloat64(0f)
                            }
                        }
                    } else if (functionName == "env") {
                    }
                    return 0L
                }
            })
        }
    }
}