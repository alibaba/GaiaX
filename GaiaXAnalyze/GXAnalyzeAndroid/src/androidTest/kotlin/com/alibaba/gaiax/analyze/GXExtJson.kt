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

import android.util.Log
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import java.util.regex.Pattern

private var sArrayPattern: Pattern? = null

fun JSON.getAnyExt(valuePath: String): Any? {
    try {
        val keyIndex = valuePath.indexOf(".")
        val arrayLeftSymbolIndex = valuePath.indexOf("[")
        val arrayRightSymbolIndex = valuePath.indexOf("]")

        // 纯数组
        // nodes[0]
        if (keyIndex == -1 && arrayLeftSymbolIndex != -1 && arrayRightSymbolIndex != -1) {
            val arrayName = valuePath.substring(0, arrayLeftSymbolIndex)
            val arrayIndex =
                valuePath.substring(arrayLeftSymbolIndex + 1, arrayRightSymbolIndex).toInt()
            (this as? JSONObject)?.getJSONArray(arrayName)?.let {
                if (it.size > arrayIndex) {
                    return it[arrayIndex]
                } else {
                    Log.e(
                        "[GaiaX]",
                        "getAnyExt IndexOutOfBounds: XPath: $valuePath Index: ${arrayIndex}, Size: ${it.size}"
                    )
                    return null
                }
            }
            return null
        }

        // 纯对象
        // title
        if (keyIndex == -1 && arrayLeftSymbolIndex == -1 && arrayRightSymbolIndex == -1) {
            return (this as? JSONObject)?.get(valuePath)
        }

        // 拆解XPATH
        val firstKey = valuePath.substring(0, keyIndex).trim()
        val restKey = valuePath.substring(keyIndex + 1, valuePath.length)
        return ((this as? JSONObject)?.getAnyExt(firstKey) as? JSON)?.getAnyExt(restKey)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return null
}


/**
 * 通过Key值获取Sting类型数据
 *
 * @param expression key值的协议  a.b[0].c.d
 * 参数说明:
 * a为json根目录下的属性值；
 * b为a下的一个数组，b[0]代表a下的b数组的第0个索引对象(数组)
 * c为b数组的第0个索引下的key为c的对象。
 * d为c对象下的属性名，key为d的value取值对象不能确定
 * @return
 *
 * @suppress
 */
fun JSON.getStringExt(expression: String): String {
    return getStringExtCanNull(expression) ?: ""
}

fun JSON.getStringExtCanNull(expression: String): String? {
    return getAnyExt(expression) as? String
}

fun JSON.getJSONArrayExt(expression: String): JSONArray {
    return getAnyExt(expression) as? JSONArray ?: JSONArray()
}

fun JSON.getJSONObjectExt(expression: String): JSONObject {
    return getAnyExt(expression) as? JSONObject ?: JSONObject()
}

/**
 * 通过Key值获取boolean类型数据
 *
 * @param expression
 * @suppress
 */
fun JSON.getBooleanExt(expression: String): Boolean {
    val value = this.getStringExt(expression)
    return "true".equals(value, ignoreCase = true)
}

/**
 * 通过Key值获取Int类型数据
 *
 * @param expression
 * @suppress
 */
fun JSON.getIntExt(expression: String): Int {
    val value = this.getStringExt(expression)
    return try {
        return if (value.isNotEmpty()) {
            value.toInt()
        } else {
            JsonExt.ERROR_RESULT_INT
        }
    } catch (e: Exception) {
        e.printStackTrace()
        JsonExt.ERROR_RESULT_INT
    }
}

/**
 * 通过Key值获取long类型数据
 *
 * @param expression
 * @suppress
 */
fun JSON.getLongExt(expression: String): Long {
    val value = this.getStringExt(expression)
    return try {
        return if (value.isNotEmpty()) {
            value.toLong()
        } else {
            JsonExt.ERROR_RESULT_LONG
        }
    } catch (e: Exception) {
        e.printStackTrace()
        JsonExt.ERROR_RESULT_LONG
    }
}

/**
 * 通过Key值获取float类型数据
 *
 * @param expression
 * @suppress
 */
fun JSON.getFloatExt(expression: String): Float {
    val value = this.getStringExt(expression)
    return try {
        return if (value.isNotEmpty()) {
            value.toFloat()
        } else {
            JsonExt.ERROR_RESULT_FLOAT
        }
    } catch (e: Exception) {
        e.printStackTrace()
        JsonExt.ERROR_RESULT_FLOAT
    }
}

/**
 * 通过Key值获取double类型数据
 *
 * @param expression
 * @suppress
 */
fun JSON.getDoubleExt(expression: String): Double {
    val value = this.getStringExt(expression)
    return try {
        return if (value.isNotEmpty()) {
            value.toDouble()
        } else {
            JsonExt.ERROR_RESULT_DOUBLE
        }
    } catch (e: Exception) {
        e.printStackTrace()
        JsonExt.ERROR_RESULT_DOUBLE
    }
}

/**
 * @suppress
 */
object JsonExt {
    const val ARRAY_INDEX_NO = -1
    const val ERROR_RESULT_LONG = -1L
    const val ERROR_RESULT_FLOAT = -1F
    const val ERROR_RESULT_INT = -1
    const val ERROR_RESULT_DOUBLE = -1.0
}