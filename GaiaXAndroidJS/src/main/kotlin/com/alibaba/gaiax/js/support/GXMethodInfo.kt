package com.alibaba.gaiax.js.support

import com.alibaba.fastjson.JSONArray
import com.alibaba.gaiax.js.api.IGXModuleMethod
import java.lang.reflect.Method

/**
 * JS映射关系
 *
 * int -> Int
 * float -> Float
 * double -> Double
 * string -> String
 * number -> Int
 * array -> MutableList
 * map -> MutableList
 * map -> JSONObject
 */
internal open class GXMethodInfo(val id: Long, val rawMethod: Method) : IGXModuleMethod {

    val name: String
        get() = rawMethod.name

    val method: GXMethod
    var arguments: Array<Any?>? = null

    init {
        rawMethod.isAccessible = true
        method = GXMethod.create(rawMethod.returnType, rawMethod)
    }

    override fun invoke(obj: Any, args: JSONArray): Any? {
        // https://agrawalsuneet.github.io/blogs/variable-number-of-arguments-vararg-kotlin/
        convertArgsToArguments(args)
        return rawMethod.invoke(obj, *arguments!!)
    }

    override fun convertArgsToArguments(args: JSONArray) {
        val methodSize = method.parameterTypes.size
        val argsSize = args.size
        arguments = if (argsSize == methodSize) {
            val result = mutableListOf<Any?>()
            method.parameterTypes.forEachIndexed { index, valueType ->
                val value = args[index]
                val convertValue = JSDataConvert.convertToJavaValue(valueType, value)
                result.add(convertValue)
            }
            result.toTypedArray()
        } else {
            arrayOf()
        }
    }

    class GaiaXSyncMethodInfo(id: Long, method: Method) : GXMethodInfo(id, method)

    class GaiaXAsyncMethodInfo(id: Long, method: Method) : GXMethodInfo(id, method)

    class GaiaXPromiseMethodInfo(id: Long, method: Method) : GXMethodInfo(id, method)
}
