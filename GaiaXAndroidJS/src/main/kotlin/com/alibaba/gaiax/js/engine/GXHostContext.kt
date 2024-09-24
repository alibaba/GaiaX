package com.alibaba.gaiax.js.engine

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.api.IGXPage
import com.alibaba.gaiax.js.impl.debug.DebugJSContext
import com.alibaba.gaiax.js.impl.qjs.QuickJSContext
import com.alibaba.gaiax.js.support.script.GXScriptBuilder
import com.alibaba.gaiax.js.utils.GaiaXJSTaskQueue
import java.util.concurrent.ConcurrentHashMap

/**
 * 每个Context下的Component对应一个模板。
 * 如果模板是嵌套模板，那么Context会有多个Component。
 */
internal class GXHostContext(
    val hostRuntime: GXHostRuntime,
    val realRuntime: IRuntime,
    val type: GXJSEngine.EngineType
) {

    /**
     * 用于从JS运行时调用Module代码的的桥接
     */
    internal val bridge = object : ICallBridgeListener {

        override fun callSync(contextId: Long, moduleId: Long, methodId: Long, args: JSONArray): Any? {
            return GXJSEngine.instance.moduleManager.invokeMethodSync(moduleId, methodId, args)
        }

        override fun callAsync(contextId: Long, moduleId: Long, methodId: Long, args: JSONArray) {
            GXJSEngine.instance.moduleManager.invokeMethodAsync(moduleId, methodId, args)
        }

        override fun callPromise(contextId: Long, moduleId: Long, methodId: Long, args: JSONArray) {
            GXJSEngine.instance.moduleManager.invokePromiseMethod(moduleId, methodId, args)
        }
    }
    private var taskQueue: GaiaXJSTaskQueue? = null

    internal var realContext: IContext? = null

    /**
     * components = {instanceId(ComponentId), ComponentObject}
     * bizIdMap = {BizId, TemplateIdMap = {templateId,instanceId}}
     */
    private val components: ConcurrentHashMap<Long, GXHostComponent> = ConcurrentHashMap()

    private val bizIdMap: ConcurrentHashMap<String, ConcurrentHashMap<String, Long>> = ConcurrentHashMap()

    /**
     * pages = {instanceId(PageInstanceId), PageObject}
     */
    private val pages: ConcurrentHashMap<Long, GXHostPage> = ConcurrentHashMap()


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

    fun startContext() {
        realContext?.initBootstrap()
        realContext?.startBootstrap()
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
        executeTask { realContext?.evaluateJS(script) }
    }

    fun evaluateJSSync(script: String): JSONObject? {
        realContext?.evaluateJS(script, String::class.java)?.let {
            return JSON.parseObject(it)
        }
        return null
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

        val component = GXHostComponent.create(this, componentId, bizId, templateId, templateVersion, script)

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

    fun registerPage(
        pageInstanceId: Long,
        bizId: String,
        templateId: String,
        templateVersion: String,
        script: String,
        nativePage: IGXPage
    ): Long {
        val page = GXHostPage.create(this, bizId, pageInstanceId, templateId, templateVersion, script, nativePage);
        pages[page.id] = page
        page.initPage()
        return page.id
    }

    fun unregisterPage(id: Long) {
        pages.remove(id)?.onUnload()
    }

    fun findPage(id: Long): IGXPage? {
        return pages[id]
    }

    fun postAnimationMessage(data: JSONObject) {
        GXScriptBuilder.buildPostAnimationMessage(data.toJSONString())
            .apply { evaluateJS(this) }
    }

    fun postModalMessage(data: JSONObject) {
        GXScriptBuilder.buildPostModalMessage(data.toJSONString())
            .apply { evaluateJS(this) }
    }

    companion object {

        const val BOOTSTRAP_JS = "bootstrap.js"

        const val MODULE_TIMER = "timer"
        const val MODULE_STD = "std"
        const val MODULE_OS = "os"
        const val MODULE_GAIAX_BRIDGE = "GaiaXJSBridge"

        /**
         * Global code.
         */
        const val EVAL_TYPE_GLOBAL = 0

        /**
         * Module code.
         */
        const val EVAL_TYPE_MODULE = 1

        fun create(host: GXHostRuntime, runtime: IRuntime, type: GXJSEngine.EngineType): GXHostContext {
            return GXHostContext(host, runtime, type)
        }
    }
}