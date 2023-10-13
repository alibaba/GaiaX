package com.alibaba.gaiax.js.support

import com.alibaba.fastjson.JSONArray
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.api.GXJSBaseModule

internal interface IModuleManager {

    fun registerModule(moduleClazz: Class<out GXJSBaseModule>)

    fun unregisterModule(moduleClazz: Class<out GXJSBaseModule>)

    fun buildModulesScript(type: GXJSEngine.EngineType): String

    fun invokeMethodSync(moduleId: Long, methodId: Long, args: JSONArray): Any?

    fun invokeMethodAsync(moduleId: Long, methodId: Long, args: JSONArray)

    fun invokePromiseMethod(moduleId: Long, methodId: Long, args: JSONArray)
}