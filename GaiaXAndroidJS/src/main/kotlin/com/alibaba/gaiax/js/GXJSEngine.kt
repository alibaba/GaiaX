package com.alibaba.gaiax.js

import android.content.Context
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.api.IGXPage
import com.alibaba.gaiax.js.engine.GXHostEngine
import com.alibaba.gaiax.js.impl.debug.DebugJSContext
import com.alibaba.gaiax.js.impl.debug.ISocketBridgeListener
import com.alibaba.gaiax.js.support.GXModuleManager
import com.alibaba.gaiax.js.support.IModuleManager
import com.alibaba.gaiax.js.utils.IdGenerator
import com.alibaba.gaiax.js.utils.Log
import com.alibaba.gaiax.js.utils.runE

/**
 * JS引擎类，负责JS引擎的启动、关闭，自定义模块的注册等逻辑
 */
class GXJSEngine {

    companion object {

        private const val TAG = "GXJSEngine"

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
        Log.runE(TAG) { "init() called" }
        this.context = context.applicationContext
        initModules()
        Log.runE(TAG) { "init() called end" }
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
            Log.runE(TAG) { "initModules() called with: e = $e"  }
        }
    }

    private fun registerAssetsModules() {
        // all assist/gaiax_js_modules/module_biz_name.json
        val allModules = JSONObject()
        val assetsModules = assetsModules(GAIAX_JS_MODULES)
        assetsModules?.forEach { file ->
            Log.runE(TAG) { "registerAssetsModules() called with: file = $file" }
            if (file.startsWith(MODULE_PREFIX) && file.endsWith(MODULE_SUFFIX)) {
                try {
                    val bizModules = JSONObject.parseObject(
                            assetsOpen("$GAIAX_JS_MODULES/$file").bufferedReader(Charsets.UTF_8).use { it.readText() })
                    allModules.putAll(bizModules)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.runE(TAG) { "registerAssetsModules() called with: e = $e" }
                }
            }
        }
        Log.runE(TAG) { "registerAssetsModules() called with: allModules = $allModules" }
        allModules.forEach {
            try {
                Class.forName(it.value.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                Log.runE(TAG) { "registerAssetsModules() called with: e = $e" }
                null
            }?.let { clazz ->
                if (clazz.superclass == GXJSBaseModule::class.java) {
                    registerModule(clazz as Class<out GXJSBaseModule>)
                } else {
                    throw IllegalArgumentException("Register Module $clazz Illegal")
                }
            }
        }
    }

    private fun assetsOpen(file: String) = synchronized(context.assets) { context.assets.open(file) }

    private fun assetsModules(path: String): Array<out String>? = synchronized(context.assets) { context.assets.list(path) }


    /**
     * 启动JS引擎
     */
    fun startDefaultEngine() {
        Log.runE(TAG) { "startDefaultEngine() called" }
        synchronized(EngineType.QuickJS) {
            if (quickJSEngine == null) {
                // 创建引擎
                quickJSEngine = createJSEngine(EngineType.QuickJS)

                // 启动引擎
                quickJSEngine?.startEngine()
            }
        }
    }

    fun startDebugEngine() {
        Log.runE(TAG) { "startDebugEngine() called" }
        synchronized(EngineType.DebugJS) {
            if (debugEngine == null) {
                // 创建引擎
                debugEngine = createJSEngine(EngineType.DebugJS)

                // 启动引擎
                debugEngine?.startEngine()
            }
        }
    }

    fun stopDefaultEngine() {
        Log.runE(TAG) { "stopDefaultEngine() called" }
        synchronized(EngineType.QuickJS) {
            if (quickJSEngine != null) {
                quickJSEngine?.destroyEngine()
                quickJSEngine = null
            }
        }
    }

    fun stopDebugEngine() {
        Log.runE(TAG) { "stopDebugEngine() called" }
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
        Log.runE(TAG) { "registerModule() called with: moduleClazz = $moduleClazz" }
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
        if (data.getBooleanValue("isPage")) {
            findPage(componentId, EngineType.QuickJS)?.let {
                it.onNativeEvent(data)
            }
        }
        debugEngine?.runtime()?.context()?.getComponent(componentId)?.onNativeEvent(data)
    }

    fun postAnimationMessage(data: JSONObject) {
        quickJSEngine?.runtime()?.context()?.postAnimationMessage(data)
        debugEngine?.runtime()?.context()?.postAnimationMessage(data)
    }

    fun postModalMessage(data: JSONObject) {
        quickJSEngine?.runtime()?.context()?.postModalMessage(data)
        debugEngine?.runtime()?.context()?.postModalMessage(data)
    }

    fun onReady(componentId: Long) {
        quickJSEngine?.runtime()?.context()?.getComponent(componentId)?.onReady()
        debugEngine?.runtime()?.context()?.getComponent(componentId)?.onReady()
    }

    fun onDataInit(componentId: Long, data: JSONObject): JSONObject? {
        return quickJSEngine?.runtime()?.context()?.getComponent(componentId)?.onDataInit(data)
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

    fun generateUniqueInstanceId(): Long {
        return IdGenerator.genLongId()
    }

    fun registerComponentWithId(
            instanceId: Long, bizId: String, templateId: String, templateVersion: String, script: String?
    ) {
        if (script != null) {
            quickJSEngine?.runtime()?.context()
                    ?.registerComponent(instanceId, bizId, templateId, templateVersion, script)
            debugEngine?.runtime()?.context()?.registerComponent(instanceId, bizId, templateId, templateVersion, script)
        }
    }

    /**
     * 为视图解除JS组件
     */
    fun unregisterComponent(componentId: Long) {
        quickJSEngine?.runtime()?.context()?.unregisterComponent(componentId)
        debugEngine?.runtime()?.context()?.unregisterComponent(componentId)
    }

    fun registerPage(
            bizId: String,
            templateId: String,
            templateVersion: String,
            script: String,
            nativePage: IGXPage
    ): Long {
        // 页面instanceId从50000起
        val pageId = IdGenerator.genLongId() + 50000
        quickJSEngine?.runtime()?.context()
                ?.registerPage(pageId, bizId, templateId, templateVersion, script, nativePage)
        debugEngine?.runtime()?.context()?.registerPage(pageId, bizId, templateId, templateVersion, script, nativePage)
        return pageId
    }

    fun unregisterPage(id: Long) {
        quickJSEngine?.runtime()?.context()?.unregisterPage(id)
        debugEngine?.runtime()?.context()?.unregisterPage(id)
    }

    fun findPage(id: Long, engineType: EngineType): IGXPage? {
        return when (engineType) {
            EngineType.QuickJS -> quickJSEngine?.runtime()?.context()?.findPage(id)
            EngineType.DebugJS -> debugEngine?.runtime()?.context()?.findPage(id)
        }
    }

    fun onPageLoad(id: Long, data: JSONObject) {
        quickJSEngine?.runtime()?.context()?.findPage(id)?.onLoad(data)
        debugEngine?.runtime()?.context()?.findPage(id)?.onLoad(data)
    }

    fun onPageUnload(id: Long) {
        quickJSEngine?.runtime()?.context()?.findPage(id)?.onUnload()
        debugEngine?.runtime()?.context()?.findPage(id)?.onUnload()
    }

    fun onPageReady(id: Long) {
        quickJSEngine?.runtime()?.context()?.findPage(id)?.onReady()
        debugEngine?.runtime()?.context()?.findPage(id)?.onReady()
    }

    fun onPageShow(id: Long) {
        quickJSEngine?.runtime()?.context()?.findPage(id)?.onShow()
        debugEngine?.runtime()?.context()?.findPage(id)?.onShow()
    }

    fun onPageHide(id: Long) {
        quickJSEngine?.runtime()?.context()?.findPage(id)?.onHide()
        debugEngine?.runtime()?.context()?.findPage(id)?.onHide()
    }

    fun onPageScroll(id: Long, data: JSONObject) {
        quickJSEngine?.runtime()?.context()?.findPage(id)?.onPageScroll(data)
        debugEngine?.runtime()?.context()?.findPage(id)?.onPageScroll(data)
    }

    fun onPageReachBottom(id: Long) {
        quickJSEngine?.runtime()?.context()?.findPage(id)?.onReachBottom()
        debugEngine?.runtime()?.context()?.findPage(id)?.onReachBottom()
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