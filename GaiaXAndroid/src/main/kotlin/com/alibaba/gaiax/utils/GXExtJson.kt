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

package com.alibaba.gaiax.utils

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import java.util.regex.Pattern

private var sArrayPattern: Pattern? = null

/**
 * @suppress
 */
fun String.safeParseToJson(): JSONObject = try {
    JSONObject.parseObject(this)
} catch (e: Exception) {
    JSONObject()
}

fun JSON.getAnyExt(valuePath: String): Any? {
    try {
        val keyIndex = valuePath.indexOf(".")
        val arrayLeftSymbolIndex = valuePath.indexOf("[")
        val arrayRightSymbolIndex = valuePath.indexOf("]")

        // 数组
        if (keyIndex == -1 && arrayLeftSymbolIndex != -1 && arrayRightSymbolIndex != -1) {
            val arrayName = valuePath.substring(0, arrayLeftSymbolIndex)
            val arrayIndex = valuePath.substring(arrayLeftSymbolIndex + 1, arrayRightSymbolIndex).trim().toInt()
            return (this as? JSONObject)?.getJSONArray(arrayName)?.get(arrayIndex)
        }

        // 对象
        if (keyIndex == -1) {
            return (this as? JSONObject)?.get(valuePath)
        }

        // 递归
        val firstKey = valuePath.substring(0, keyIndex).trim()
        val restKey = valuePath.substring(keyIndex + 1, valuePath.length)
        return (this as? JSONObject)?.getJSONObject(firstKey)?.getAnyExt(restKey)
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


/**
 * 例子：keyParams = curKey.nextKey.key.key;
 * curKey = curKey
 * nextKey = nextKey;
 * otherKey = nextKey.key.key
 *
 * @param expression
 * @param value
 * @suppress
 */
fun JSON.setValueExt(expression: String, value: Any) {
    try {
        val keys = JsonExt.parserKey(expression)
        if (keys.isNotEmpty()) {

            val firstKey = keys[0]

            var otherKey: String? = null
            if (keys.size >= 2) {
                otherKey = keys[1]
            }

            //如果是对象，index == -1, 如果是数组，index >= 0(数组的索引)
            val arrayIndex = JsonExt.getArrayIndex(firstKey)

            //标识还没有到keyParams的最后一层
            if (otherKey != null && otherKey.isNotEmpty()) {
                if (arrayIndex == JsonExt.ARRAY_INDEX_NO) {
                    if (this is JSONObject) {
                        val obj = this[firstKey]
                        if (obj is JSON) {
                            obj.setValueExt(otherKey, value)
                        }
                    }
                } else {
                    //数组的key    例如：data[0] key为data, 索引为0
                    val obj = JsonExt.getObjFromArray(this, firstKey, arrayIndex)
                    if (obj is JSON) {
                        obj.setValueExt(otherKey, value)
                    }
                }
            } else {
                //最后一层设置Value
                if (arrayIndex == JsonExt.ARRAY_INDEX_NO && this is JSONObject) {
                    this[firstKey] = value
                } else {
                    val key = JsonExt.getArrayKey(firstKey)
                    if (this is JSONObject) {
                        if (containsKey(key)) {
                            this.getJSONArray(key).add(arrayIndex, value)
                        }
                    } else if (this is JSONArray) {
                        this.add(arrayIndex, value)
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
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

    /**
     * 解析协议的key ,主要讲Key分成两层，当前层和剩余部分的所有key
     * 例如： key:a.b.c.d   解析为[a]和[b.c.d]两个数组
     *
     */
    fun parserKey(key: String): Array<String> {
        var result: Array<String> = arrayOf()
        if (key.isNotEmpty()) {
            val index = key.indexOf(".")
            result = if (index == -1) {
                arrayOf(key)
            } else {
                if (index + 1 < key.length) {
                    val key1 = key.substring(0, index)
                    val key2 = key.substring(index + 1)
                    arrayOf(key1, key2)
                } else {
                    arrayOf(key.substring(0, index))
                }
            }
        }
        return result
    }

    /**
     * 获取当前协议下一层级的key值
     * @param key first.second
     * @return second
     */
    fun getNextKey(key: String): String {
        var result = ""
        if (key.isNotEmpty()) {
            val keys = key.split(".").toTypedArray()
            if (keys.size > 1) {
                result = keys[1]
            }
        }
        return result
    }

    /**
     * 获取数组类型协议的索引
     *
     * @param arrayKey : key[0]
     * @return
     */
    fun getArrayIndex(arrayKey: String): Int {
        var result = ARRAY_INDEX_NO
        if (isArrayType(arrayKey)) {
            if (sArrayPattern == null) {
                sArrayPattern = Pattern.compile("(?<=\\[)(.+?)(?=\\])")
            }
            result = matchArrayIndex(
                arrayKey, result
            )
        }
        return result
    }

    /**
     * 获取数组类型协议的Key
     *
     * @param arrayKey :  key[0]
     * @return
     */
    fun getArrayKey(arrayKey: String): String {
        if (arrayKey.isNotEmpty()) {
            val index = arrayKey.indexOf("[")
            return if (index < 0) "" else arrayKey.substring(0, index)
        }
        return ""
    }

    /**
     * 从 src 中获取指定数组索引的对象
     *
     * @param src json
     * @param arrayIndexKey array[index]
     * @param arrayIndex array[index] 中的 index
     */
    fun getObjFromArray(src: Any, arrayIndexKey: String, arrayIndex: Int): Any? {
        // array[index] 中的 array
        val arrayKey = getArrayKey(arrayIndexKey)
        var obj: Any? = null
        if (src is JSONObject) {
            if (src.containsKey(arrayKey)) {
                //如果是data[0], 先取出data数组，再取数组中的0 有越界的可能，需要cache
                val jsonArray = src.getJSONArray(arrayKey)
                if (jsonArray.size > arrayIndex) {
                    obj = jsonArray[arrayIndex]
                }
            }
        } else if (src is JSONArray) {
            if (src.size > arrayIndex) {
                //如果是[0]， 直接获取索引的内容
                obj = src[arrayIndex]
            }
        }
        return obj
    }

    private fun isArrayType(arrayKey: String) = arrayKey.isNotEmpty() && arrayKey.contains("[")

    private fun matchArrayIndex(arrayKey: String, result: Int): Int {
        var result = result
        try {
            // 现在创建 matcher 对象
            val m = sArrayPattern?.matcher(arrayKey)
            if (m != null && m.find()) {
                result = m.group().toInt()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

}