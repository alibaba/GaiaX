package com.alibaba.gaiax.js

import android.content.Context
import android.view.View
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.engine.GXHostContext
import com.alibaba.gaiax.js.engine.GXHostEngine
import com.alibaba.gaiax.js.impl.debug.DebugJSContext
import com.alibaba.gaiax.js.impl.debug.ISocketCallBridgeListener
import com.alibaba.gaiax.js.module.GXJSBuildInModule
import com.alibaba.gaiax.js.module.GXJSBuildInStorageModule
import com.alibaba.gaiax.js.module.GXJSBuildInTipsModule
import com.alibaba.gaiax.js.module.GXJSLogModule
import com.alibaba.gaiax.js.module.GXJSNativeEventModule
import com.alibaba.gaiax.js.module.GXJSNativeMessageEventModule
import com.alibaba.gaiax.js.module.GXJSNativeUtilModule
import com.alibaba.gaiax.js.support.GXModuleManager
import com.alibaba.gaiax.js.support.GXNativeEventManager
import com.alibaba.gaiax.js.support.IModuleManager
import com.alibaba.gaiax.js.utils.IdGenerator
import com.alibaba.gaiax.js.utils.Log
import com.alibaba.gaiax.js.utils.TimeUtils
import com.alibaba.gaiax.provider.module.js.GXJSNativeTargetModule
import java.util.concurrent.ConcurrentHashMap

class GXJSEngine {

    companion object {

        private const val GAIAX_JS_MODULES = "gaiax_js_modules"
        private const val MODULE_PREFIX = "module_"
        private const val MODULE_SUFFIX = ".json"

        val instance by lazy {
            return@lazy GXJSEngine()
        }
    }

    enum class EngineType {
        QuickJS, DebugJS
    }

    internal var socketProxy: ISocketProxy? = null

    /**
     * 错误日志的监控实现
     */
    internal var listener: IListener? = null

    /**
     * app的Context
     */
    internal lateinit var context: Context

    /**
     * 一个类型的engine只注册一次(QuickJs,JavaScriptCore,StudioWorker)
     */
    private val engines = ConcurrentHashMap<EngineType, GXHostEngine>()

    internal val moduleManager: IModuleManager = GXModuleManager()

    private var defaultEngine: GXHostEngine? = null
    private var debugEngine: GXHostEngine? = null

    internal lateinit var renderEngineDelegate: IRenderEngineDelegate

    fun init(context: Context): GXJSEngine {
        this.context = context.applicationContext

        initGXAdapter()?.init(this.context)

        initModules()

        return this
    }

    fun initRenderDelegate(renderEngineDelegate: IRenderEngineDelegate): GXJSEngine {
        this.renderEngineDelegate = renderEngineDelegate
        Proxy.instance.renderDelegate = renderEngineDelegate
        return this
    }

    fun initListener(listener: IListener): GXJSEngine {
        this.listener = listener
        return this
    }

    private fun initModules() {
        try {
            registerInnerModules()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            registerAssetsModules()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun registerAssetsModules() {
        // all gaiax_js_modules/module_biz_name.json
        val allModules = JSONObject()
        val assetsModules = assetsModules(GXJSEngine.GAIAX_JS_MODULES)
        assetsModules?.forEach { file ->
            if (Log.isLog()) {
                Log.d("registerAssetsModules() called with: file = $file")
            }
            if (file.startsWith(GXJSEngine.MODULE_PREFIX) && file.endsWith(GXJSEngine.MODULE_SUFFIX)) {
                try {
                    val bizModules =
                        JSONObject.parseObject(assetsOpen("${GXJSEngine.GAIAX_JS_MODULES}/$file").bufferedReader(
                            Charsets.UTF_8
                        ).use { it.readText() })
                    allModules.putAll(bizModules)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        allModules.forEach {
            val clazz = Class.forName(it.value.toString())
            if (clazz.superclass == GXJSBaseModule::class.java) {
                registerModule(clazz as Class<out GXJSBaseModule>)
            } else {
                throw IllegalArgumentException("Register Module $clazz Illegal")
            }
        }
    }

    private fun assetsOpen(file: String) =
        synchronized(context.assets) { context.assets.open(file) }

    private fun assetsModules(path: String): Array<out String>? =
        synchronized(context.assets) { context.assets.list(path) }

    private fun registerInnerModules() {
        registerModule(GXJSNativeUtilModule::class.java)
        registerModule(GXJSNativeMessageEventModule::class.java)
        registerModule(GXJSLogModule::class.java)
        registerModule(GXJSNativeTargetModule::class.java)
        registerModule(GXJSNativeEventModule::class.java)
        registerModule(GXJSBuildInModule::class.java)
        registerModule(GXJSBuildInTipsModule::class.java)
        registerModule(GXJSBuildInStorageModule::class.java)
    }

    fun stopDefaultEngine() {
        synchronized(EngineType.QuickJS) {
            if (defaultEngine != null) {
                destroyEngine(defaultEngine)
                defaultEngine = null
            }
        }
    }

    fun stopDebugEngine() {
        synchronized(EngineType.DebugJS) {
            if (debugEngine != null) {
                destroyEngine(debugEngine)
                debugEngine = null
            }
        }
    }

    fun startAllEngine() {
        startDefaultEngine()
        startDebugEngine()
    }

    fun stopAllEngine() {
        stopDebugEngine()
        stopDefaultEngine()
    }

    fun startDefaultEngine(complete: (() -> Unit)? = null) {
        synchronized(EngineType.QuickJS) {
            if (debugEngine == null) {
                // 创建引擎
                defaultEngine = obtainJSEngine(EngineType.QuickJS)

                // 启动引擎
                startJSEngine(defaultEngine, complete)
            }
        }
    }

    fun startDebugEngine(complete: (() -> Unit)? = null) {
        synchronized(EngineType.DebugJS) {
            if (debugEngine == null) {
                // 创建引擎
                debugEngine = obtainJSEngine(EngineType.DebugJS)

                // 启动引擎
                startJSEngine(debugEngine, complete)
            }
        }
    }

    private fun startJSEngine(engine: GXHostEngine?, complete: (() -> Unit)?) {
        val engineType = engine?.type
        if (engines.containsKey(engineType)) {
            engines[engineType]?.startEngine(complete)
        }
    }

    private fun obtainJSEngine(type: EngineType): GXHostEngine {
        val id = IdGenerator.genLongId()
        var engine = GXHostEngine.create(id, type)
        if (engines.containsKey(type)) {
            engine = engines.getValue(type)
        } else {
            engines[type] = engine
        }
        engine.initEngine()
        return engine
    }

    private fun destroyEngine(engine: GXHostEngine?) {
        val engineType = engine?.type
        if (engines.containsKey(engineType)) {
            val instance = engines.remove(engineType)
            instance?.destroyEngine()
        }
    }

    fun registerModule(moduleClazz: Class<out GXJSBaseModule>): GXJSEngine {
        if (Log.isLog()) {
            Log.d("registerModule() called with: moduleClazz = $moduleClazz")
        }
        moduleManager.registerModule(moduleClazz)
        return this
    }

    fun unregisterModule(moduleClazz: Class<out GXJSBaseModule>) {
        moduleManager.unregisterModule(moduleClazz)
    }

    private fun initGXAdapter(): IAdapter? {
        return try {
            val clazz = Class.forName("com.alibaba.gaiax.js.adapter.GXJSAdapter")
            clazz.newInstance() as IAdapter
        } catch (e: Exception) {
            null
        }
    }

    fun getRenderDelegate(): IRenderEngineDelegate {
        return this.renderEngineDelegate
    }

    /**
     * 设置socket代理，用于Debug运行时与Studio通信
     */
    fun setSocketProxy(proxy: ISocketProxy) {
        this.socketProxy = proxy
    }

    fun getSocketCallBridge(): ISocketCallBridgeListener? {
        return (debugEngine?.runtime()?.context()?.realContext as? DebugJSContext)?.socketBridge
    }

    interface IListener {
        fun errorLog(data: JSONObject)
        fun monitor(
            scene: String,
            biz: String = "",
            id: String = "",
            type: String = "",
            state: String = "",
            value: Long = -1L,
            jsModuleName: String = "",
            jsApiName: String = "",
            jsApiType: String = ""
        )
    }

    interface IAdapter {
        fun init(context: Context)
    }

    internal class Proxy {

        internal lateinit var renderDelegate: IRenderEngineDelegate

        private fun gxHostContext(it: Map.Entry<EngineType, GXHostEngine>) =
            it.value.runtime()?.context()

        private fun moduleManager() = GXJSEngine.instance.moduleManager

        fun remoteDelayTask(taskId: Int) {
            GXJSEngine.instance.engines.forEach {
                gxHostContext(it)?.remoteDelayTask(taskId)
            }
        }

        fun executeDelayTask(taskId: Int, delay: Long, function: () -> Unit) {
            GXJSEngine.instance.engines.forEach {
                gxHostContext(it)?.executeDelayTask(taskId, delay, function)
            }
        }

        fun executeTask(func: () -> Unit) {
            GXJSEngine.instance.engines.forEach {
                gxHostContext(it)?.executeTask(func)
            }
        }

        fun executeIntervalTask(taskId: Int, interval: Long, func: () -> Unit) {
            GXJSEngine.instance.engines.forEach {
                gxHostContext(it)?.executeIntervalTask(taskId, interval, func)
            }
        }

        fun remoteIntervalTask(taskId: Int) {
            GXJSEngine.instance.engines.forEach {
                gxHostContext(it)?.remoteIntervalTask(taskId)
            }
        }

        internal fun invokeSyncMethod(moduleId: Long, methodId: Long, args: JSONArray): Any? {
            return moduleManager().invokeMethodSync(moduleId, methodId, args)
        }

        internal fun invokeAsyncMethod(moduleId: Long, methodId: Long, args: JSONArray) {
            moduleManager().invokeMethodAsync(moduleId, methodId, args)
        }

        internal fun invokePromiseMethod(moduleId: Long, methodId: Long, args: JSONArray) {
            moduleManager().invokePromiseMethod(moduleId, methodId, args)
        }

        internal fun buildModulesScript(type: EngineType): String {
            return moduleManager().buildModulesScript(type)
        }

        internal fun buildBootstrapScript(): String {
            return GXJSEngine.instance.context.resources.assets.open(GXHostContext.BOOTSTRAP_JS)
                .bufferedReader(Charsets.UTF_8).use { it.readText() }
        }

        companion object {
            val instance by lazy {
                return@lazy Proxy()
            }
        }
    }

    /**
     * JS生命周期的变化需要通过该类执行
     */
    object Component {

        private fun getHostContext(it: Map.Entry<EngineType, GXHostEngine>) =
            it.value.runtime()?.context()

        fun onEvent(componentId: Long, type: String, data: JSONObject) {
            instance.engines.forEach {
                getHostContext(it)?.getComponent(componentId)?.onEvent(type, data)
            }
        }

        fun onNativeEvent(componentId: Long, data: JSONObject) {
            instance.engines.forEach {
                getHostContext(it)?.getComponent(componentId)?.onNativeEvent(data)
            }
        }

        fun onReady(componentId: Long) {
            instance.engines.forEach {
                getHostContext(it)?.getComponent(componentId)?.onReady()
            }
        }

        fun onReuse(componentId: Long) {
            instance.engines.forEach {
                getHostContext(it)?.getComponent(componentId)?.onReuse()
            }
        }

        fun onShow(componentId: Long) {
            instance.engines.forEach {
                getHostContext(it)?.getComponent(componentId)?.onShow()
            }
        }

        fun onHide(componentId: Long) {
            instance.engines.forEach {
                getHostContext(it)?.getComponent(componentId)?.onHide()
            }
        }

        fun onDestroy(componentId: Long) {
            instance.engines.forEach {
                getHostContext(it)?.getComponent(componentId)?.onDestroy()
            }
        }

        fun onLoadMore(componentId: Long, data: JSONObject) {
            instance.engines.forEach {
                getHostContext(it)?.getComponent(componentId)?.onLoadMore(data)
            }
        }

        fun registerComponent(
            bizId: String, templateId: String, templateVersion: String, script: String, view: View
        ): Long {
            // 为引擎注册组件的时候，不同的引擎都使用同一个组件ID
            val componentId = IdGenerator.genLongId()
            instance.engines.forEach {
                getHostContext(it)?.registerComponent(
                    componentId, bizId, templateId, templateVersion, script
                )
                instance.renderEngineDelegate.bindComponentToView(view, componentId)
            }
            return componentId
        }

        fun unregisterComponent(componentId: Long) {
            instance.engines.forEach {
                getHostContext(it)?.unregisterComponent(componentId)
                instance.renderEngineDelegate.unbindComponentAndView(componentId)
            }
        }

        fun dispatcherNativeMessageEventToJS(data: JSONObject) {
            GXNativeEventManager.instance.eventsData.forEach { componentData ->
                val componentId = componentData.getLongValue("instanceId")
                instance.engines.forEach {
                    getHostContext(it)?.getComponent(componentId)?.let { component ->
                        val result = JSONObject().apply {
                            this.putAll(data)
                            this.putAll(componentData)
                            this["timestamp"] = TimeUtils.elapsedRealtime()
                        }
                        component.onNativeEvent(result)
                    }
                    instance.renderEngineDelegate.unbindComponentAndView(componentId)
                }
            }
        }
    }

    interface ISocketProxy {
        fun sendMessage(data: JSONObject)
    }
}