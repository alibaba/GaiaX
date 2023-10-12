package com.alibaba.gaiax.js.support

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.api.IGXCallback
import com.alibaba.gaiax.js.api.IGXPromise
import com.alibaba.gaiax.quickjs.JSContext
import com.alibaba.gaiax.quickjs.JSValue
import java.lang.reflect.Type

object JSDataConvert {

    fun getDataTypeByValue(value: Any): String {
        return when (value) {
            is JSONObject -> "JSONObject"
            is String -> "String"
            is Int -> "Int"
            is Double -> "Double"
            is Float -> "Float"
            is Long -> "Long"
            is Boolean -> "Boolean"
            else -> "Any"
        }
    }

    fun getDataValueByType(type: String, data: Any?): Any? {
        data?.let {
            return when (type) {
                "JSONObject" -> JSONObject.parseObject(data.toString())
                "String" -> data.toString()
                "Int" -> data.toString().toInt()
                "Double" -> data.toString().toDouble()
                "Float" -> data.toString().toFloat()
                "Long" -> data.toString().toLong()
                "Boolean" -> data.toString().toBoolean()
                else -> data.toString()
            }
        }
        return null
    }

    fun convertToJSValue(jsContext: JSContext, result: Any?): JSValue {
        if (result == null) {
            return jsContext.createJSUndefined()
        }
        return when (result) {
            // 常规
            is JSONObject -> jsContext.createJSJsonObject(result.toJSONString())
            is String -> jsContext.createJSString(result)
            is Int -> jsContext.createJSNumber(result)
            is Double -> jsContext.createJSNumber(result)
            is Float -> jsContext.createJSNumber(result.toDouble())
            is Long -> jsContext.createJSNumber(result)
            is Boolean -> jsContext.createJSBoolean(result)
            // 非常规
            else -> jsContext.createJSUndefined()
        }
    }

    fun convertToJavaValue(valueType: Type, value: Any?): Any? {
        if (value == null) {
            return null
        }
        return when (valueType) {
            // 常规
            JSONObject::class.java -> JSONObject.parseObject(value.toString())
            String::class.java -> value.toString()
            Int::class.java -> value.toString().toInt()
            Double::class.java -> value.toString().toDouble()
            Float::class.java -> value.toString().toFloat()
            Long::class.java -> value.toString().toLong()
            Boolean::class.java -> value.toString().toBoolean()
            // 非常规
            IGXCallback::class.java -> value as IGXCallback
            IGXPromise::class.java -> value as IGXPromise
            is Types.ParameterizedTypeImpl -> {
                if (valueType.rawType == MutableMap::class.java) {
                    val targetType = valueType.typeArguments[0]
                    val targetValueType = valueType.typeArguments[1]
                    val result: MutableMap<Any, Any?> = mutableMapOf()
                    val valueJsonObj = JSONObject.parseObject(value.toString())
                    valueJsonObj.keys.forEach { fromKey ->
                        convertToJavaValue(targetType, fromKey)?.let { targetResultKey ->
                            val targetResultValue = convertToJavaValue(targetValueType, valueJsonObj[fromKey])
                            result[targetResultKey] = targetResultValue
                        }
                    }
                    return result
                } else if (valueType.rawType == MutableList::class.java) {
                    val targetType = valueType.typeArguments[0]
                    val result: MutableList<Any?> = mutableListOf()
                    JSONArray.parseArray(value.toString()).forEach { fromValue ->
                        val element = convertToJavaValue(targetType, fromValue)
                        result.add(element)
                    }
                    return result
                } else {
                    value
                }
            }
            else -> value
        }
    }

}