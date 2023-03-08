package com.youku.gaiax.js.support

import com.alibaba.fastjson.JSONArray
import com.youku.gaiax.js.api.GaiaXBaseModule

internal interface IModuleManager {

    fun registerModule(moduleClazz: Class<out GaiaXBaseModule>)

    fun unregisterModule(moduleClazz: Class<out GaiaXBaseModule>)

    fun buildModulesScript(): String

    fun invokeMethodSync(moduleId: Long, methodId: Long, args: JSONArray): Any?

    fun invokeMethodAsync(moduleId: Long, methodId: Long, args: JSONArray)

    fun invokePromiseMethod(moduleId: Long, methodId: Long, args: JSONArray)
}