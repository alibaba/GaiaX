package com.youku.gaiax.js.core

import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.youku.gaiax.js.GaiaXJS
import com.youku.gaiax.js.core.api.ICallBridgeListener
import com.youku.gaiax.js.core.api.IComponent
import com.youku.gaiax.js.core.api.IContext
import com.youku.gaiax.js.core.api.IRuntime
import com.youku.gaiax.js.impl.qjs.QuickJSContext
import com.youku.gaiax.js.utils.Aop
import com.youku.gaiax.js.utils.GaiaXJSTaskQueue
import com.youku.gaiax.js.utils.Log
import com.youku.gaiax.js.utils.MonitorUtils
import java.util.concurrent.ConcurrentHashMap

/**
 * 每个Context下的Component对应一个模板。
 * 如果模板是嵌套模板，那么Context会有多个Component。
 */
internal class GaiaXContext private constructor(val host: GaiaXRuntime, val runtime: IRuntime, val type: GaiaXJS.GaiaXJSType) {

    internal val bridge = object : ICallBridgeListener {

        override fun callSync(contextId: Long, moduleId: Long, methodId: Long, args: JSONArray): Any? {
            if (Log.isLog()) {
                Log.d("callSync() called with: contextId = $contextId, moduleId = $moduleId, methodId = $methodId, args = $args")
            }
            return GaiaXJS.instance.invokeSyncMethod(moduleId, methodId, args)
        }

        override fun callAsync(contextId: Long, moduleId: Long, methodId: Long, args: JSONArray) {
            if (Log.isLog()) {
                Log.d("callAsync() called with: contextId = $contextId, moduleId = $moduleId, methodId = $methodId, args = $args")
            }
            GaiaXJS.instance.invokeAsyncMethod(moduleId, methodId, args)
        }

        override fun callPromise(contextId: Long, moduleId: Long, methodId: Long, args: JSONArray) {
            if (Log.isLog()) {
                Log.d("callPromise() called with: contextId = $contextId, moduleId = $moduleId, methodId = $methodId, args = $args")
            }
            GaiaXJS.instance.invokePromiseMethod(moduleId, methodId, args)
        }
    }
    private var taskQueue: GaiaXJSTaskQueue? = null

    private var context: IContext? = null

    private val components: ConcurrentHashMap<Long, IComponent> = ConcurrentHashMap()

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

    fun startContext(complete: () -> Unit) {
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

    private fun createContext(): IContext {
        return when (type) {
            GaiaXJS.GaiaXJSType.QuickJS -> QuickJSContext.create(this, host.engine, runtime)
        }
    }

    fun registerComponent(bizId: String, templateId: String, templateVersion: String, script: String): Long {
        val component = GaiaXComponent.create(this, bizId, templateId, templateVersion, script)
        components[component.id] = component
        component.initComponent()
        return component.id
    }

    fun unregisterComponent(id: Long) {
        components.remove(id)?.destroyComponent()
    }

    fun onReadyComponent(id: Long) {
        components[id]?.onReady()
    }

    fun onReuseComponent(id: Long) {
        components[id]?.onReuse()
    }

    fun onShowComponent(id: Long) {
        components[id]?.onShow()
    }

    fun onHiddenComponent(id: Long) {
        components[id]?.onHide()
    }

    fun onDestroyComponent(id: Long) {
        components[id]?.onDestroy()
    }

    fun onLoadMoreComponent(id: Long, data: JSONObject) {
        components[id]?.onLoadMore(data)
    }

    fun onEventComponent(id: Long, type: String, data: JSONObject) {
        components[id]?.onEvent(type, data)
    }

    fun onNativeEventComponent(id: Long, data: JSONObject) {
        components[id]?.onNativeEvent(data)
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

        fun create(host: GaiaXRuntime, runtime: IRuntime, type: GaiaXJS.GaiaXJSType): GaiaXContext {
            return GaiaXContext(host, runtime, type)
        }
    }
}