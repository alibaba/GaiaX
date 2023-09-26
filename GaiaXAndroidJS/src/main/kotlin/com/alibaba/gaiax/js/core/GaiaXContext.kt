package com.alibaba.gaiax.js.core

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.GXJSEngineFactory
import com.alibaba.gaiax.js.GaiaXJSManager
import com.alibaba.gaiax.js.core.api.ICallBridgeListener
import com.alibaba.gaiax.js.core.api.IComponent
import com.alibaba.gaiax.js.core.api.IContext
import com.alibaba.gaiax.js.core.api.IRuntime
import com.alibaba.gaiax.js.impl.qjs.GaiaXJSDebuggerContext
import com.alibaba.gaiax.js.impl.qjs.QuickJSContext
import com.alibaba.gaiax.js.utils.Aop
import com.alibaba.gaiax.js.utils.GaiaXJSTaskQueue
import com.alibaba.gaiax.js.utils.Log
import com.alibaba.gaiax.js.utils.MonitorUtils
import java.util.concurrent.ConcurrentHashMap

/**
 * 每个Context下的Component对应一个模板。
 * 如果模板是嵌套模板，那么Context会有多个Component。
 */
internal class GaiaXContext private constructor(val host: GaiaXRuntime, val runtime: IRuntime, val type: GXJSEngineFactory.GaiaXJSEngineType) {

    internal val bridge = object : ICallBridgeListener {

        override fun callSync(contextId: Long, moduleId: Long, methodId: Long, args: JSONArray): Any? {
            if (Log.isLog()) {
                Log.d("callSync() called with: contextId = $contextId, moduleId = $moduleId, methodId = $methodId, args = $args")
            }
            return GaiaXJSManager.instance.invokeSyncMethod(moduleId, methodId, args)
        }

        override fun callAsync(contextId: Long, moduleId: Long, methodId: Long, args: JSONArray) {
            if (Log.isLog()) {
                Log.d("callAsync() called with: contextId = $contextId, moduleId = $moduleId, methodId = $methodId, args = $args")
            }
            GaiaXJSManager.instance.invokeAsyncMethod(moduleId, methodId, args)
        }

        override fun callPromise(contextId: Long, moduleId: Long, methodId: Long, args: JSONArray) {
            if (Log.isLog()) {
                Log.d("callPromise() called with: contextId = $contextId, moduleId = $moduleId, methodId = $methodId, args = $args")
            }
            GaiaXJSManager.instance.invokePromiseMethod(moduleId, methodId, args)
        }
    }
    private var taskQueue: GaiaXJSTaskQueue? = null

    private var context: IContext? = null

    /**
     * components = {instanceId(ComponentId), ComponentObject}
     * bizIdMap = {BizId, TemplateIdMap = {templateId,instanceId}}
     */
    private val components: ConcurrentHashMap<Long, GaiaXComponent> = ConcurrentHashMap()

    private val bizIdMap: ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> = ConcurrentHashMap()
    fun initContext() {
        if (context == null) {
            context = createContext()
        }
        if (taskQueue == null) {
            taskQueue = GaiaXJSTaskQueue.create(host.host.engineId)
        }
        taskQueue?.initTaskQueue()

        context?.initContext()
        context?.initModule(MODULE_TIMER)
        context?.initModule(MODULE_GAIAX_BRIDGE)
        context?.initPendingJob()
    }

    fun startContext(complete: () -> Unit = {}) {
        // 执行初始化脚本
        executeTask {
            Aop.aopTaskTime({
                context?.initBootstrap()
                context?.startBootstrap()
                complete()
            }, { time ->
                MonitorUtils.jsInitScene(MonitorUtils.TYPE_JS_LIBRARY_INIT, time)
            })
        }
    }

    fun executeIntervalTask(taskId: Int, interval: Long, func: () -> Unit) {
        taskQueue?.executeIntervalTask(taskId, interval, func)
    }

    fun remoteIntervalTask(taskId: Int) {
        taskQueue?.remoteIntervalTask(taskId)
    }

    fun executeDelayTask(taskId: Int, delay: Long, func: () -> Unit) {
        taskQueue?.executeDelayTask(taskId, delay, func)
    }

    fun executeTask(func: () -> Unit) {
        taskQueue?.executeTask(func)
    }

    fun remoteDelayTask(taskId: Int) {
        taskQueue?.remoteDelayTask(taskId)
    }

    fun destroyContext() {
        context?.destroyPendingJob()

        taskQueue?.destroyTaskQueue()
        taskQueue = null

        context?.destroyContext()
        context = null
    }

    fun evaluateJS(script: String) {
        executeTask { evaluateJSWithoutTask(script) }
    }

    fun evaluateJSWithoutTask(script: String) {
        context?.evaluateJS(script)
    }

    fun evaluateJSWithoutTask(script: String, argsMap: JSONObject) {
        if (host.host.isDebugging) {
            context?.evaluateJS(script, argsMap)
        } else {
            context?.evaluateJS(script)
        }
    }

    private fun createContext(): IContext {
        return when (type) {
            GXJSEngineFactory.GaiaXJSEngineType.GaiaXJSEngineTypeQuickJS -> QuickJSContext.create(this, host.engine, runtime)
            GXJSEngineFactory.GaiaXJSEngineType.GaiaXJSEngineTypeDebugger -> GaiaXJSDebuggerContext.create(this, host.engine, runtime)
        }
    }

    fun registerComponent(bizId: String, templateId: String, templateVersion: String, script: String): Long {
        val component = GaiaXComponent.create(this, bizId, templateId, templateVersion, script)

        components[component.id] = component

        if (bizIdMap.contains(bizId)) {
            bizIdMap[bizId]?.set(templateId, component.id)
        } else {
            val templateIdMap = ConcurrentHashMap<String, Long>()
            templateIdMap[templateId] = component.id
            bizIdMap[bizId] = templateIdMap
        }
        component.initComponent()
        return component.id

    }

    fun unregisterComponent(id: Long) {
        components.remove(id)?.destroyComponent()
    }

    fun getComponentByInstanceId(instanceId: Long): IComponent? {
        return components[instanceId]
    }

    companion object {

        const val BOOTSTRAP_JS = "bootstrap.js"

        const val MODULE_TIMER = "timer"
        const val MODULE_STD = "std"
        const val MODULE_OS = "os"
        const val MODULE_GAIAX_BRIDGE = "GaiaXBridge"

        /**
         * Global code.
         */
        const val EVAL_TYPE_GLOBAL = 0

        /**
         * Module code.
         */
        const val EVAL_TYPE_MODULE = 1

        fun create(host: GaiaXRuntime, runtime: IRuntime, type: GXJSEngineFactory.GaiaXJSEngineType): GaiaXContext {
            return GaiaXContext(host, runtime, type)
        }
    }
}