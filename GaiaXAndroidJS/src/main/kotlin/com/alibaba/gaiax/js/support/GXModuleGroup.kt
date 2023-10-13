package com.alibaba.gaiax.js.support

import com.alibaba.fastjson.JSONArray
import com.alibaba.gaiax.js.GXJSEngine

internal class GXModuleGroup private constructor(val name: String) {

    companion object {

        fun create(moduleName: String): GXModuleGroup {
            return GXModuleGroup(moduleName)
        }
    }

    private val modules: MutableMap<Long, GXModule> = mutableMapOf()

    fun addModule(module: GXModule) {
        modules[module.id] = module
    }

    fun removeModule(id: Long) {
        modules.remove(id)
    }

    fun buildModuleScript(type: GXJSEngine.EngineType): String {
        val script = StringBuilder()
        val moduleScript = initModuleScript(type)
        val moduleGlobalScript = initModuleGlobalScript()
        script.append(moduleScript)
        script.append(moduleGlobalScript)
        modules.forEach {
            script.append(it.value.buildModuleScript())
        }
        return script.toString()
    }

    private fun initModuleGlobalScript(): String {
        return GXScriptBuilder.buildModuleGlobalDeclareScript(name)
    }

    private fun initModuleScript(type: GXJSEngine.EngineType): String {
        return when (type) {
            GXJSEngine.EngineType.QuickJS -> GXScriptBuilder.buildModuleDeclareScript(name)
                ?: throw IllegalArgumentException("Module name is empty")

            GXJSEngine.EngineType.DebugJS -> GXScriptBuilder.buildModuleDeclareScriptForDebug(name)
                ?: throw IllegalArgumentException("Module name is empty")
        }

    }

    fun invokeMethodSync(moduleId: Long, methodId: Long, args: JSONArray): Any? {
        return modules[moduleId]?.invokeMethodSync(methodId, args)
    }

    fun invokeMethodAsync(moduleId: Long, methodId: Long, args: JSONArray) {
        modules[moduleId]?.invokeMethodAsync(methodId, args)
    }

    fun invokePromiseMethod(moduleId: Long, methodId: Long, args: JSONArray) {
        modules[moduleId]?.invokePromiseMethod(methodId, args)
    }
}