package com.youku.gaiax.js.support

import com.alibaba.fastjson.JSONArray
import com.youku.gaiax.js.api.GaiaXBaseModule

internal class GaiaXModuleManager : IModuleManager {

    val moduleGroup: MutableMap<String, GaiaXModuleGroup> = mutableMapOf()
    private val moduleGroupKey: MutableMap<Long, String> = mutableMapOf()

    private var clazzs: MutableList<Class<out GaiaXBaseModule>> = mutableListOf()
    private val clazzIds: MutableMap<Class<out GaiaXBaseModule>, Long> = mutableMapOf()

    override fun invokeMethodSync(moduleId: Long, methodId: Long, args: JSONArray): Any? {
        return moduleGroup[moduleGroupKey[moduleId]]?.invokeMethodSync(moduleId, methodId, args)
    }

    override fun invokeMethodAsync(moduleId: Long, methodId: Long, args: JSONArray) {
        moduleGroup[moduleGroupKey[moduleId]]?.invokeMethodAsync(moduleId, methodId, args)
    }

    override fun invokePromiseMethod(moduleId: Long, methodId: Long, args: JSONArray) {
        moduleGroup[moduleGroupKey[moduleId]]?.invokePromiseMethod(moduleId, methodId, args)
    }

    override fun registerModule(moduleClazz: Class<out GaiaXBaseModule>) {
        if (!clazzs.contains(moduleClazz)) {
            clazzs.add(moduleClazz)
            val module = GaiaXModule(moduleClazz.newInstance()).also {
                if (moduleGroup[it.name] == null) {
                    moduleGroup[it.name] = GaiaXModuleGroup.create(it.name)
                }
            }
            clazzIds[moduleClazz] = module.id
            moduleGroupKey[module.id] = module.name
            moduleGroup[module.name]?.addModule(module)
        }
    }

    override fun unregisterModule(moduleClazz: Class<out GaiaXBaseModule>) {
        if (clazzs.contains(moduleClazz)) {
            clazzs.remove(moduleClazz)
            clazzIds.remove(moduleClazz)?.let { id ->
                moduleGroupKey.remove(id)?.let { name ->
                    moduleGroup[name]?.removeModule(id)
                }
            }
        }
    }

    override fun buildModulesScript(): String {
        val script = StringBuilder()
        moduleGroup.forEach {
            script.append(it.value.buildModuleScript())
        }
        return script.toString()
    }
}