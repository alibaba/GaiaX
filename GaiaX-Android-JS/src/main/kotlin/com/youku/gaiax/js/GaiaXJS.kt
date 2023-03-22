package com.youku.gaiax.js

import android.content.Context
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.studio.GXClientToStudioMultiType
import com.alibaba.gaiax.studio.IDevTools
import com.youku.gaiax.js.api.GaiaXBaseModule
import com.youku.gaiax.js.core.GaiaXContext
import com.youku.gaiax.js.core.GaiaXEngine
import com.youku.gaiax.js.support.GaiaXModuleManager
import com.youku.gaiax.js.support.IModuleManager
import com.youku.gaiax.js.support.module.GaiaXLogModule
import com.youku.gaiax.js.support.module.GaiaXNativeLogTestModule
import com.youku.gaiax.js.support.module.GaiaXNativeMessageEventModule
import com.youku.gaiax.js.support.module.GaiaXNativeUtilModule
import com.youku.gaiax.js.utils.Aop
import com.youku.gaiax.js.utils.IdGenerator
import com.youku.gaiax.js.utils.Log
import com.youku.gaiax.js.utils.MonitorUtils
import com.youku.gaiax.provider.module.js.GaiaXNativeEventModule
import com.youku.gaiax.provider.module.js.GaiaXNativeTargetModule
import java.util.concurrent.ConcurrentHashMap


class GaiaXJS {

    interface Listener {
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

    internal enum class GaiaXJSType {
        GaiaXJSEngineTypeQuickJS,
        GaiaXJSEngineTypeDebugger
    }

    internal var listener: Listener? = null
    internal lateinit var context: Context

    //todo：需要保障一个类型的engine只注册一次(quickjs,javascriptcore,studioworker)
    private val engines = ConcurrentHashMap<Long, GaiaXEngine>()

    private val moduleManager: IModuleManager = GaiaXModuleManager()

    private var defaultEngine: GaiaXEngine? = null

    var renderEngineDelegate: IRenderEngineDelegate? = null

    var isDebugging: Boolean = false

    val devToolsDebuggingTypeListener: IDevTools.DevToolsDebuggingTypeListener = object : IDevTools.DevToolsDebuggingTypeListener {
        override fun onDevToolsJSModeChanged(modeType: String) {
            when (modeType) {
                GXClientToStudioMultiType.JS_BREAKPOINT -> {
                    isDebugging = true
                    //切换引擎逻辑
                    if (defaultEngine != null) {
                        if (defaultEngine?.type != GaiaXJSType.GaiaXJSEngineTypeDebugger) {
                            if (engines.size == 2) {
                                engines.forEach {
                                    if (it.value.type == GaiaXJSType.GaiaXJSEngineTypeDebugger) {
                                        defaultEngine = it.value
                                    }
                                }
                            } else {
                                startEngine { }
                            }
                        }
                    } else {
                        startEngine { }
                        throw java.lang.IllegalArgumentException("额外的engine被注册")
                    }
                }
                GXClientToStudioMultiType.JS_DEFAULT -> {
                    isDebugging = false
                }
            }
        }

    }

    fun init(context: Context): GaiaXJS {
        this.context = context.applicationContext

        // 加载模块
        Aop.aopTaskTime({
            initModules()
        }, { time ->
            MonitorUtils.jsInitScene(MonitorUtils.TYPE_LOAD_MODULE, time)
        })

        return this
    }

    fun initRenderDelegate(renderEngineDelegate: IRenderEngineDelegate): GaiaXJS {
        this.renderEngineDelegate = renderEngineDelegate
        return this
    }

    fun initListener(listener: Listener): GaiaXJS {
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

//    @SuppressWarnings("unchecked")
    private fun registerAssetsModules() {
        // all gaiax_js_modules/module_biz_name.json
        val allModules = JSONObject()
        val assetsModules = assetsModules(GAIAX_JS_MODULES)
        assetsModules?.forEach { file ->
            if (Log.isLog()) {
                Log.d("registerAssetsModules() called with: file = $file")
            }
            if (file.startsWith(MODULE_PREFIX) && file.endsWith(MODULE_SUFFIX)) {
                try {
                    val bizModules = JSONObject.parseObject(
                        assetsOpen("$GAIAX_JS_MODULES/$file").bufferedReader(Charsets.UTF_8)
                            .use { it.readText() })
                    allModules.putAll(bizModules)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        allModules.forEach {
            val clazz = Class.forName(it.value.toString())
            if (clazz.superclass == GaiaXBaseModule::class.java) {
                registerModule(clazz as Class<out GaiaXBaseModule>)
            } else {
                throw IllegalArgumentException("Register Module $clazz Illegal")
            }
        }
//        registerModule(G)
    }

    private fun assetsOpen(file: String) =
        synchronized(context.assets) { context.assets.open(file) }

    private fun assetsModules(path: String): Array<out String>? =
        synchronized(context.assets) { context.assets.list(path) }

    private fun registerInnerModules() {
        registerModule(GaiaXNativeUtilModule::class.java)
        registerModule(GaiaXNativeMessageEventModule::class.java)
        registerModule(GaiaXLogModule::class.java)
        registerModule(GaiaXNativeTargetModule::class.java)
        registerModule(GaiaXNativeEventModule::class.java)
        //todo
        registerModule(GaiaXNativeLogTestModule::class.java)
    }

    fun onEventComponent(id: Long, type: String, data: JSONObject) {
        defaultEngine?.runtime()?.context()?.onEventComponent(id, type, data)
    }

    fun onNativeEventComponent(id: Long, data: JSONObject) {
        defaultEngine?.runtime()?.context()?.onNativeEventComponent(id, data)
    }

    fun onReadyComponent(id: Long) {
        defaultEngine?.runtime()?.context()?.onReadyComponent(id)
    }

    fun onReuseComponent(id: Long) {
        defaultEngine?.runtime()?.context()?.onReuseComponent(id)
    }

    fun onShowComponent(id: Long) {
        defaultEngine?.runtime()?.context()?.onShowComponent(id)
    }

    fun onHiddenComponent(id: Long) {
        defaultEngine?.runtime()?.context()?.onHiddenComponent(id)
    }

    fun onDestroyComponent(id: Long) {
        defaultEngine?.runtime()?.context()?.onDestroyComponent(id)
    }

    fun onLoadMoreComponent(id: Long, data: JSONObject) {
        defaultEngine?.runtime()?.context()?.onLoadMoreComponent(id, data)
    }

    fun registerComponent(
        bizId: String,
        templateId: String,
        templateVersion: String,
        script: String
    ): Long {
        return defaultEngine?.runtime()?.context()
            ?.registerComponent(bizId, templateId, templateVersion, script)
            ?: -1L
    }

    fun unregisterComponent(id: Long) {
        defaultEngine?.runtime()?.context()?.unregisterComponent(id)
    }

    fun startEngine(complete: () -> Unit) {
        val lock = if (isDebugging) {
            GaiaXJSType.GaiaXJSEngineTypeDebugger
        } else {
            GaiaXJSType.GaiaXJSEngineTypeQuickJS
        }
        synchronized(lock) {
            // 创建引擎
            defaultEngine = Aop.aopTaskTime({
                // 创建引擎
                if (isDebugging) {
                    createEngine(GaiaXJSType.GaiaXJSEngineTypeDebugger)
                } else {
                    createEngine(GaiaXJSType.GaiaXJSEngineTypeQuickJS)
                }
            }, { time ->
                MonitorUtils.jsInitScene(MonitorUtils.TYPE_JS_CONTEXT_INIT, time)
            })

            // 启动引擎
            startEngine(defaultEngine, complete)
        }
    }

    fun stopEngine() {
        synchronized(GaiaXJS::class.java) {
            if (defaultEngine != null) {
                destroyEngine(defaultEngine)
                defaultEngine = null
            }
        }
    }

    private fun createEngine(type: GaiaXJSType): GaiaXEngine {
        val id = IdGenerator.genLongId()
        checkIdEngineExist(id)
        val engine = GaiaXEngine.create(id, type)
        engines[id] = engine
        engine.initEngine()
        return engine
    }

    private fun startEngine(engine: GaiaXEngine?, complete: () -> Unit) {
        engine?.getId()?.let { startEngine(it, complete) }
    }

    private fun startEngine(engineId: Long, complete: () -> Unit) {
        if (engines.containsKey(engineId)) {
            engines[engineId]?.startEngine(complete)
        }
    }

    private fun destroyEngine(engine: GaiaXEngine?) {
        engine?.getId()?.let { destroyEngine(it) }
    }

    private fun destroyEngine(engineId: Long) {
        if (engines.containsKey(engineId)) {
            val instance = engines.remove(engineId)
            instance?.destroyEngine()
        }
    }

    fun registerModule(moduleClazz: Class<out GaiaXBaseModule>): GaiaXJS {
        if (Log.isLog()) {
            Log.d("registerModule() called with: moduleClazz = $moduleClazz")
        }
        moduleManager.registerModule(moduleClazz)
        return this
    }

    private fun unregisterModule(moduleClazz: Class<out GaiaXBaseModule>) {
        moduleManager.unregisterModule(moduleClazz)
    }

    fun remoteDelayTask(taskId: Int) {
        defaultEngine?.runtime()?.context()?.remoteDelayTask(taskId)
    }

    fun executeDelayTask(taskId: Int, delay: Long, function: () -> Unit) {
        defaultEngine?.runtime()?.context()?.executeDelayTask(taskId, delay, function)
    }

    fun executeTask(func: () -> Unit) {
        defaultEngine?.runtime()?.context()?.executeTask(func)
    }

    fun executeIntervalTask(taskId: Int, interval: Long, func: () -> Unit) {
        defaultEngine?.runtime()?.context()?.executeIntervalTask(taskId, interval, func)
    }

    fun remoteIntervalTask(taskId: Int) {
        defaultEngine?.runtime()?.context()?.remoteIntervalTask(taskId)
    }

    internal fun context(engineId: Long): GaiaXContext? {
        return engines[engineId]?.runtime()?.context()
    }

    private fun checkIdEngineExist(id: Long) {
        if (engines.containsKey(id)) {
            throw IllegalArgumentException("Id Engine Exist")
        }
    }

    internal fun invokeSyncMethod(moduleId: Long, methodId: Long, args: JSONArray): Any? {
        return moduleManager.invokeMethodSync(moduleId, methodId, args)
    }

    internal fun invokeAsyncMethod(moduleId: Long, methodId: Long, args: JSONArray) {
        moduleManager.invokeMethodAsync(moduleId, methodId, args)
    }

    internal fun invokePromiseMethod(moduleId: Long, methodId: Long, args: JSONArray) {
        moduleManager.invokePromiseMethod(moduleId, methodId, args)
    }

    internal fun buildModulesScript(): String {
        return moduleManager.buildModulesScript()
    }

    internal fun buildBootstrapScript(): String {
        return context.resources.assets.open(GaiaXContext.BOOTSTRAP_JS)
            .bufferedReader(Charsets.UTF_8).use { it.readText() }
    }


    companion object {

        private const val GAIAX_JS_MODULES = "gaiax_js_modules"
        private const val MODULE_PREFIX = "module_"
        private const val MODULE_SUFFIX = ".json"

        val instance by lazy {
            return@lazy GaiaXJS()
        }
    }
}