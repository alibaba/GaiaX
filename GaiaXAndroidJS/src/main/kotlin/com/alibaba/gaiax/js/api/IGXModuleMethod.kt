package com.alibaba.gaiax.js.api

import androidx.annotation.Keep
import com.alibaba.fastjson.JSONArray

@Keep
internal interface IGXModuleMethod {
    fun invoke(obj: Any, args: JSONArray): Any?
    fun convertArgsToArguments(args: JSONArray)
}