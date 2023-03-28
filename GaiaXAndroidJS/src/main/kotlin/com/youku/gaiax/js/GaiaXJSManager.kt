package com.youku.gaiax.js

import android.content.Context
import com.alibaba.fastjson.JSONArray
import com.youku.gaiax.js.core.GaiaXContext


internal class GaiaXJSManager {

    internal lateinit var appContext: Context

    internal lateinit var renderDelegate: IRenderEngineDelegate

    internal var errorListener: GXJSEngineFactory.Listener? = null

    fun remoteDelayTask(taskId: Int) {
        GXJSEngineFactory.instance.getGaiaXJSContext()?.remoteDelayTask(taskId)
    }

    fun executeDelayTask(taskId: Int, delay: Long, function: () -> Unit) {
        GXJSEngineFactory.instance.getGaiaXJSContext()?.executeDelayTask(taskId, delay, function)
    }

    fun executeTask(func: () -> Unit) {
        GXJSEngineFactory.instance.getGaiaXJSContext()?.executeTask(func)
    }

    fun executeIntervalTask(taskId: Int, interval: Long, func: () -> Unit) {
        GXJSEngineFactory.instance.getGaiaXJSContext()?.executeIntervalTask(taskId, interval, func)
    }

    fun remoteIntervalTask(taskId: Int) {
        GXJSEngineFactory.instance.getGaiaXJSContext()?.remoteIntervalTask(taskId)
    }

    internal fun invokeSyncMethod(moduleId: Long, methodId: Long, args: JSONArray): Any? {
        return GXJSEngineFactory.instance.moduleManager.invokeMethodSync(moduleId, methodId, args)
    }

    internal fun invokeAsyncMethod(moduleId: Long, methodId: Long, args: JSONArray) {
        GXJSEngineFactory.instance.moduleManager.invokeMethodAsync(moduleId, methodId, args)
    }

    internal fun invokePromiseMethod(moduleId: Long, methodId: Long, args: JSONArray) {
        GXJSEngineFactory.instance.moduleManager.invokePromiseMethod(moduleId, methodId, args)
    }

    internal fun buildModulesScript(): String {
        return GXJSEngineFactory.instance.moduleManager.buildModulesScript(GXJSEngineFactory.instance.isDebugging)
    }

    internal fun buildBootstrapScript(): String {
        return GXJSEngineFactory.instance.context.resources.assets.open(GaiaXContext.BOOTSTRAP_JS)
            .bufferedReader(Charsets.UTF_8).use { it.readText() }
    }


    companion object {
        val instance by lazy {
            return@lazy GaiaXJSManager()
        }
    }
}