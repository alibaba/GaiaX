package com.alibaba.gaiax.js.support

import com.alibaba.fastjson.JSONArray
import com.alibaba.gaiax.js.api.GaiaXJSBaseModule

internal interface IModuleManager {

    fun registerModule(moduleClazz: Class<out GaiaXJSBaseModule>)

    fun unregisterModule(moduleClazz: Class<out GaiaXJSBaseModule>)

    fun buildModulesScript(isDebugging: Boolean): String

    fun invokeMethodSync(moduleId: Long, methodId: Long, args: JSONArray): Any?

    fun invokeMethodAsync(moduleId: Long, methodId: Long, args: JSONArray)

    fun invokePromiseMethod(moduleId: Long, methodId: Long, args: JSONArray)
}