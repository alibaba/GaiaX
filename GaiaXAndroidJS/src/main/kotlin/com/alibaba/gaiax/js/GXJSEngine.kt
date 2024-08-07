package com.alibaba.gaiax.js

import android.content.Context
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.engine.GXHostEngine
import com.alibaba.gaiax.js.impl.debug.DebugJSContext
import com.alibaba.gaiax.js.impl.debug.ISocketBridgeListener
import com.alibaba.gaiax.js.support.GXModuleManager
import com.alibaba.gaiax.js.support.IModuleManager
import com.alibaba.gaiax.js.utils.IdGenerator
import com.alibaba.gaiax.js.utils.Log

/**
 * JS引擎类，负责JS引擎的启动、关闭，自定义模块的注册等逻辑
 */
class GXJSEngine {

    companion object {

        private const val GAIAX_JS_MODULES = "gaiax_js_modules"
        private const val MODULE_PREFIX = "module_"
        private const val MODULE_SUFFIX = ".json"

        val instance by lazy {
            return@lazy GXJSEngine()
        }
    }

    internal enum class EngineType {
        QuickJS, DebugJS
    }

    internal var socketSender: ISocketSender? = null

    /**
     * 错误日志的监控实现
     */
    internal var logListener: ILogListener? = null

    /**
     * JS内抛出的错误
     */
    internal var jsExceptionListener: IJsExceptionListener? = null

    /**
     * app的Context
     */
    lateinit var context: Context

    internal val moduleManager: IModuleManager = GXModuleManager()

    internal var quickJSEngine: GXHostEngine? = null

    internal var debugEngine: GXHostEngine? = null

    /**
     * JS引擎初始化
     *  - 初始化上下文
     *  - 初始化内建模块
     */
    fun init(context: Context): GXJSEngine {
        this.context = context.applicationContext
        initModules()
        return this
    }

    fun setLogListener(listener: ILogListener): GXJSEngine {
        this.logListener = listener
        return this
    }

    fun setJSExceptionListener(listener: IJsExceptionListener): GXJSEngine {
        this.jsExceptionListener = listener;
        return this
    }

    private fun initModules() {
        try {
            registerAssetsModules()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun registerAssetsModules() {
        // all assist/gaiax_js_modules/module_biz_name.json
        val allModules = JSONObject()
        val assetsModules = assetsModules(GAIAX_JS_MODULES)
        assetsModules?.forEach { file ->
            if (Log.isLog()) {
                Log.d("registerAssetsModules() called with: file = $file")
            }
            if (file.startsWith(MODULE_PREFIX) && file.endsWith(MODULE_SUFFIX)) {
                try {
                    val bizModules = JSONObject.parseObject(assetsOpen("$GAIAX_JS_MODULES/$file").bufferedReader(Charsets.UTF_8).use { it.readText() })
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

    private fun assetsOpen(file: String) = synchronized(context.assets) { context.assets.open(file) }

    private fun assetsModules(path: String): Array<out String>? = synchronized(context.assets) { context.assets.list(path) }


    fun startDefaultEngine(complete: (() -> Unit)? = null) {
        if (Log.isLog()) {
            Log.d("startDefaultEngine()")
        }
        synchronized(EngineType.QuickJS) {
            if (debugEngine == null) {
                // 创建引擎
                quickJSEngine = createJSEngine(EngineType.QuickJS)

                // 启动引擎
                quickJSEngine?.startEngine(complete)
            }
        }
    }

    fun startDebugEngine(complete: (() -> Unit)? = null) {
        if (Log.isLog()) {
            Log.d("startDebugEngine()")
        }
        synchronized(EngineType.DebugJS) {
            if (debugEngine == null) {
                // 创建引擎
                debugEngine = createJSEngine(EngineType.DebugJS)

                // 启动引擎
                debugEngine?.startEngine(complete)
            }
        }
    }

    fun stopDefaultEngine() {
        if (Log.isLog()) {
            Log.d("stopDefaultEngine()")
        }
        synchronized(EngineType.QuickJS) {
            if (quickJSEngine != null) {
                quickJSEngine?.destroyEngine()
                quickJSEngine = null
            }
        }
    }

    fun stopDebugEngine() {
        if (Log.isLog()) {
            Log.d("stopDebugEngine()")
        }
        synchronized(EngineType.DebugJS) {
            if (debugEngine != null) {
                debugEngine?.destroyEngine()
                debugEngine = null
            }
        }
    }

    private fun createJSEngine(type: EngineType): GXHostEngine {
        val id = IdGenerator.genLongId()
        val engine = GXHostEngine.create(id, type)
        engine.initEngine()
        return engine
    }

    /**
     * 注册自定义模块
     */
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

    fun setSocketSender(iSocketSender: ISocketSender) {
        this.socketSender = iSocketSender
    }

    fun getSocketBridge(): ISocketBridgeListener? {
        return (debugEngine?.runtime()?.context()?.realContext as? DebugJSContext)?.socketBridge
    }

    fun getSocketSender(): ISocketSender? {
        return socketSender
    }

    fun onEvent(componentId: Long, type: String, data: JSONObject) {
        quickJSEngine?.runtime()?.context()?.getComponent(componentId)?.onEvent(type, data)
        debugEngine?.runtime()?.context()?.getComponent(componentId)?.onEvent(type, data)
    }

    fun onNativeEvent(componentId: Long, data: JSONObject) {
        quickJSEngine?.runtime()?.context()?.getComponent(componentId)?.onNativeEvent(data)
        debugEngine?.runtime()?.context()?.getComponent(componentId)?.onNativeEvent(data)
    }

    fun onReady(componentId: Long) {
        quickJSEngine?.runtime()?.context()?.getComponent(componentId)?.onReady()
        debugEngine?.runtime()?.context()?.getComponent(componentId)?.onReady()
    }

    fun onReuse(componentId: Long) {
        quickJSEngine?.runtime()?.context()?.getComponent(componentId)?.onReuse()
        debugEngine?.runtime()?.context()?.getComponent(componentId)?.onReuse()
    }

    fun onShow(componentId: Long) {
        quickJSEngine?.runtime()?.context()?.getComponent(componentId)?.onShow()
        debugEngine?.runtime()?.context()?.getComponent(componentId)?.onShow()
    }

    fun onHide(componentId: Long) {
        quickJSEngine?.runtime()?.context()?.getComponent(componentId)?.onHide()
        debugEngine?.runtime()?.context()?.getComponent(componentId)?.onHide()
    }

    fun onDestroy(componentId: Long) {
        quickJSEngine?.runtime()?.context()?.getComponent(componentId)?.onDestroy()
        debugEngine?.runtime()?.context()?.getComponent(componentId)?.onDestroy()
    }

    fun onLoadMore(componentId: Long, data: JSONObject) {
        quickJSEngine?.runtime()?.context()?.getComponent(componentId)?.onLoadMore(data)
        debugEngine?.runtime()?.context()?.getComponent(componentId)?.onLoadMore(data)
    }

    /**
     * 为视图注册JS组件
     */
    fun registerComponent(bizId: String, templateId: String, templateVersion: String, script: String): Long {
        // 为引擎注册组件的时候，不同的引擎都使用同一个组件ID
        val componentId = IdGenerator.genLongId()
        quickJSEngine?.runtime()?.context()?.registerComponent(componentId, bizId, templateId, templateVersion, script)
        debugEngine?.runtime()?.context()?.registerComponent(componentId, bizId, templateId, templateVersion, script)
        return componentId
    }

    /**
     * 为视图解除JS组件
     */
    fun unregisterComponent(componentId: Long) {
        quickJSEngine?.runtime()?.context()?.unregisterComponent(componentId)
        debugEngine?.runtime()?.context()?.unregisterComponent(componentId)
    }

    interface IJsExceptionListener {

        /**
         * 当JS执行发生异常时调用
         * {
         *  "templateId":"test",
         *  "templateVersion":-1,
         *  "bizId":"fastpreview",
         *  "message":"'data' is not defined",
         *  "stack":"    at onReady ()\n    at call (native)\n    at <anonymous> ()\n    at <anonymous> (:5)\n    at <anonymous> (:7)\n"
         * }
         */
        fun exception(data: JSONObject)
    }

    interface ILogListener {
        fun errorLog(data: JSONObject)
    }

    interface ISocketSender {
        fun onSendMsg(data: JSONObject)
    }
}