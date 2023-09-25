package com.youku.gaiax.js.api

import androidx.annotation.Keep
import com.alibaba.fastjson.JSONArray

@Keep
interface IGaiaXModuleMethod {
    fun invoke(obj: Any, args: JSONArray): Any?
    fun convertArgsToArguments(args: JSONArray)
}