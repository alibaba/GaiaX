package com.alibaba.gaiax.js.support

import com.alibaba.fastjson.JSONArray

internal class GaiaXModuleGroup private constructor(val name: String) {

    companion object {

        fun create(moduleName: String): GaiaXModuleGroup {
            return GaiaXModuleGroup(moduleName)
        }
    }

    val modules: MutableMap<Long, GaiaXModule> = mutableMapOf()

    fun addModule(module: GaiaXModule) {
        modules[module.id] = module
    }

    fun removeModule(id: Long) {
        modules.remove(id)
    }

    fun buildModuleScript(isDebugging: Boolean): String {
        val script = StringBuilder()
        val moduleScript = initModuleScript(isDebugging)
        val moduleGlobalScript = initModuleGlobalScript()
        script.append(moduleScript)
        script.append(moduleGlobalScript)
        modules.forEach {
            script.append(it.value.buildModuleScript())
        }
        return script.toString()
    }

    private fun initModuleGlobalScript(): String {
        return GaiaXScriptBuilder.buildModuleGlobalDeclareScript(name)
    }

    private fun initModuleScript(isDebugging: Boolean): String {
        if(isDebugging){
            return GaiaXScriptBuilder.buildModuleDeclareScriptForDebugger(name) ?: throw IllegalArgumentException("Module name is empty")
        }else{
            return GaiaXScriptBuilder.buildModuleDeclareScript(name) ?: throw IllegalArgumentException("Module name is empty")
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