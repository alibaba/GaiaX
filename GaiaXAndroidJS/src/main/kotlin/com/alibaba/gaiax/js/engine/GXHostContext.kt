package com.alibaba.gaiax.js.engine

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.impl.debug.DebugJSContext
import com.alibaba.gaiax.js.impl.qjs.QuickJSContext
import com.alibaba.gaiax.js.utils.GaiaXJSTaskQueue
import com.alibaba.gaiax.js.utils.Log
import java.util.concurrent.ConcurrentHashMap

/**
 * 每个Context下的Component对应一个模板。
 * 如果模板是嵌套模板，那么Context会有多个Component。
 */
internal class GXHostContext(
    val hostRuntime: GXHostRuntime, val realRuntime: IRuntime, val type: GXJSEngine.EngineType
) {

    internal val bridge = object : ICallBridgeListener {

        override fun callSync(
            contextId: Long, moduleId: Long, methodId: Long, args: JSONArray
        ): Any? {
            if (Log.isLog()) {
                Log.d("callSync() called with: contextId = $contextId, moduleId = $moduleId, methodId = $methodId, args = $args")
            }
            return GXJSEngine.Proxy.instance.invokeSyncMethod(moduleId, methodId, args)
        }

        override fun callAsync(contextId: Long, moduleId: Long, methodId: Long, args: JSONArray) {
            if (Log.isLog()) {
                Log.d("callAsync() called with: contextId = $contextId, moduleId = $moduleId, methodId = $methodId, args = $args")
            }
            GXJSEngine.Proxy.instance.invokeAsyncMethod(moduleId, methodId, args)
        }

        override fun callPromise(contextId: Long, moduleId: Long, methodId: Long, args: JSONArray) {
            if (Log.isLog()) {
                Log.d("callPromise() called with: contextId = $contextId, moduleId = $moduleId, methodId = $methodId, args = $args")
            }
            GXJSEngine.Proxy.instance.invokePromiseMethod(moduleId, methodId, args)
        }
    }
    private var taskQueue: GaiaXJSTaskQueue? = null

    var realContext: IContext? = null

    /**
     * components = {instanceId(ComponentId), ComponentObject}
     * bizIdMap = {BizId, TemplateIdMap = {templateId,instanceId}}
     */
    private val components: ConcurrentHashMap<Long, GXHostComponent> = ConcurrentHashMap()

    private val bizIdMap: ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> =
        ConcurrentHashMap()

    fun initContext() {
        if (realContext == null) {
            realContext = createContext()
        }
        if (taskQueue == null) {
            taskQueue = GaiaXJSTaskQueue.create(hostRuntime.hostEngine.engineId)
        }
        taskQueue?.initTaskQueue()

        realContext?.initContext()
        realContext?.initModule(MODULE_TIMER)
        realContext?.initModule(MODULE_GAIAX_BRIDGE)
        realContext?.initPendingJob()
    }

    fun startContext(complete: (() -> Unit)? = null) {
        // 执行初始化脚本
        executeTask {
            realContext?.initBootstrap()
            realContext?.startBootstrap()
            complete?.invoke()
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
        realContext?.destroyPendingJob()

        taskQueue?.destroyTaskQueue()
        taskQueue = null

        realContext?.destroyContext()
        realContext = null
    }

    fun evaluateJS(script: String) {
        executeTask { evaluateJSWithoutTask(script) }
    }

    fun evaluateJSWithoutTask(script: String) {
        realContext?.evaluateJS(script)
    }

    fun evaluateJSWithoutTask(script: String, argsMap: JSONObject) {
        realContext?.evaluateJS(script, argsMap)
    }

    private fun createContext(): IContext {
        return when (type) {
            GXJSEngine.EngineType.QuickJS -> QuickJSContext.create(
                this, hostRuntime.realEngine, realRuntime
            )

            GXJSEngine.EngineType.DebugJS -> DebugJSContext(this)
        }
    }

    fun registerComponent(
        componentId: Long,
        bizId: String,
        templateId: String,
        templateVersion: String,
        script: String
    ): Long {

        val component =
            GXHostComponent.create(this, componentId, bizId, templateId, templateVersion, script)

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

    fun getComponent(instanceId: Long): IComponent? {
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

        fun create(
            host: GXHostRuntime, runtime: IRuntime, type: GXJSEngine.EngineType
        ): GXHostContext {
            return GXHostContext(host, runtime, type)
        }
    }
}