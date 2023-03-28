package com.youku.gaiax.js

import android.content.Context
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.studio.GXClientToStudioMultiType
import com.alibaba.gaiax.studio.IDevTools
import com.youku.gaiax.js.api.GaiaXJSBaseModule
import com.youku.gaiax.js.core.GaiaXContext
import com.youku.gaiax.js.core.GaiaXEngine
import com.youku.gaiax.js.support.GaiaXModuleManager
import com.youku.gaiax.js.support.IModuleManager
import com.youku.gaiax.js.support.module.*
import com.youku.gaiax.js.utils.Aop
import com.youku.gaiax.js.utils.IdGenerator
import com.youku.gaiax.js.utils.Log
import com.youku.gaiax.js.utils.MonitorUtils
import com.youku.gaiax.provider.module.js.GaiaXJSNativeEventModule
import com.youku.gaiax.provider.module.js.GaiaXJSNativeTargetModule
import java.util.concurrent.ConcurrentHashMap

/**
 *  @author: shisan.lms
 *  @date: 2023-03-23
 *  Description:
 *      1.提供业务使用方完成GaiaXJS能力初始化
 */
class GXJSEngineFactory {

    companion object {

        private const val GAIAX_JS_MODULES = "gaiax_js_modules"
        private const val MODULE_PREFIX = "module_"
        private const val MODULE_SUFFIX = ".json"

        val instance by lazy {
            return@lazy GXJSEngineFactory()
        }
    }

    enum class GaiaXJSEngineType {
        GaiaXJSEngineTypeQuickJS,
        GaiaXJSEngineTypeDebugger
    }

    /**
     * 错误日志的监控实现
     */
    internal var listener: Listener? = null

    /**
     * app的Context
     */
    internal lateinit var context: Context

    //todo：需要保障一个类型的engine只注册一次(quickjs,javascriptcore,studioworker)
    private val engines = ConcurrentHashMap<Long, GaiaXEngine>()

    internal val moduleManager: IModuleManager = GaiaXModuleManager()

    private var defaultEngine: GaiaXEngine? = null

    internal lateinit var renderEngineDelegate: IRenderEngineDelegate

    var isDebugging: Boolean = false

    val devToolsDebuggingTypeListener: IDevTools.DevToolsDebuggingTypeListener =
        object : IDevTools.DevToolsDebuggingTypeListener {
            override fun onDevToolsJSModeChanged(modeType: String) {
                when (modeType) {
                    GXClientToStudioMultiType.JS_BREAKPOINT -> {
                        isDebugging = true
                        //切换引擎逻辑
                        if (defaultEngine != null) {
                            if (defaultEngine?.type != GaiaXJSEngineType.GaiaXJSEngineTypeDebugger) {
                                if (engines.size == 2) {
                                    engines.forEach {
                                        if (it.value.type == GaiaXJSEngineType.GaiaXJSEngineTypeDebugger) {
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

    fun init(context: Context): GXJSEngineFactory {
        this.context = context.applicationContext

        //初始化GaiaXJSManager
        GaiaXJSManager.instance.appContext = this.context

        // 加载模块
        Aop.aopTaskTime({
            initModules()
        }, { time ->
            MonitorUtils.jsInitScene(MonitorUtils.TYPE_LOAD_MODULE, time)
        })

        return this
    }

    fun initRenderDelegate(renderEngineDelegate: IRenderEngineDelegate): GXJSEngineFactory {
        this.renderEngineDelegate = renderEngineDelegate
        GaiaXJSManager.instance.renderDelegate = renderEngineDelegate
        return this
    }

    fun initListener(listener: Listener): GXJSEngineFactory {
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
        val assetsModules = assetsModules(GXJSEngineFactory.GAIAX_JS_MODULES)
        assetsModules?.forEach { file ->
            if (Log.isLog()) {
                Log.d("registerAssetsModules() called with: file = $file")
            }
            if (file.startsWith(GXJSEngineFactory.MODULE_PREFIX) && file.endsWith(GXJSEngineFactory.MODULE_SUFFIX)) {
                try {
                    val bizModules = JSONObject.parseObject(
                        assetsOpen("${GXJSEngineFactory.GAIAX_JS_MODULES}/$file").bufferedReader(
                            Charsets.UTF_8
                        )
                            .use { it.readText() })
                    allModules.putAll(bizModules)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        allModules.forEach {
            val clazz = Class.forName(it.value.toString())
            if (clazz.superclass == GaiaXJSBaseModule::class.java) {
                registerModule(clazz as Class<out GaiaXJSBaseModule>)
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
        registerModule(GaiaXJSNativeUtilModule::class.java)
        registerModule(GaiaXJSNativeMessageEventModule::class.java)
        registerModule(GaiaXJSLogModule::class.java)
        registerModule(GaiaXJSNativeTargetModule::class.java)
        registerModule(GaiaXJSNativeEventModule::class.java)
        registerModule(GaiaXJSBuildInModule::class.java)
        registerModule(GaiaXJSBuildInTipsModule::class.java)
        registerModule(GaiaXJSBuildInStorageModule::class.java)
    }


    fun stopEngine() {
        synchronized(GXJSEngineFactory::class.java) {
            if (defaultEngine != null) {
                destroyEngine(defaultEngine)
                defaultEngine = null
            }
        }
    }

    fun startEngine(complete: () -> Unit = {}) {
        val lock = if (isDebugging) {
            GaiaXJSEngineType.GaiaXJSEngineTypeDebugger
        } else {
            GaiaXJSEngineType.GaiaXJSEngineTypeQuickJS
        }
        synchronized(lock) {
            // 创建引擎
            defaultEngine = Aop.aopTaskTime({
                // 创建引擎
                if (isDebugging) {
                    createJSEngine(GaiaXJSEngineType.GaiaXJSEngineTypeDebugger)
                } else {
                    createJSEngine(GaiaXJSEngineType.GaiaXJSEngineTypeQuickJS)
                }
            }, { time ->
                MonitorUtils.jsInitScene(MonitorUtils.TYPE_JS_CONTEXT_INIT, time)
            })

            // 启动引擎
            startEngine(defaultEngine, complete)
        }
    }

    private fun startEngine(engine: GaiaXEngine?, complete: () -> Unit) {
        engine?.getId()?.let { startEngine(it, complete) }
    }

    private fun startEngine(engineId: Long, complete: () -> Unit = {}) {
        if (engines.containsKey(engineId)) {
            engines[engineId]?.startEngine(complete)
        }
    }

    private fun createJSEngine(type: GaiaXJSEngineType): GaiaXEngine {
        val id = IdGenerator.genLongId()
        checkIdEngineExist(id)
        val engine = GaiaXEngine.create(id, type, isDebugging)
        engines[id] = engine
        engine.initEngine()
        return engine
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

    fun registerModule(moduleClazz: Class<out GaiaXJSBaseModule>): GXJSEngineFactory {
        if (Log.isLog()) {
            Log.d("registerModule() called with: moduleClazz = $moduleClazz")
        }
        moduleManager.registerModule(moduleClazz)
        return this
    }

    private fun unregisterModule(moduleClazz: Class<out GaiaXJSBaseModule>) {
        moduleManager.unregisterModule(moduleClazz)
    }

    internal fun getGaiaXJSContext(): GaiaXContext? {
        return defaultEngine?.runtime()?.context()
    }

    private fun checkIdEngineExist(id: Long) {
        if (engines.containsKey(id)) {
            throw IllegalArgumentException("Id Engine Exist")
        }
    }

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


}