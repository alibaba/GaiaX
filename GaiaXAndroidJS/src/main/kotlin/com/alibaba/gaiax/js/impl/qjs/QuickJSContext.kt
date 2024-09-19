package com.alibaba.gaiax.js.impl.qjs

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.engine.GXHostContext
import com.alibaba.gaiax.js.engine.IContext
import com.alibaba.gaiax.js.engine.IEngine
import com.alibaba.gaiax.js.engine.IRuntime
import com.alibaba.gaiax.js.impl.qjs.module.QuickJSBridgeModule
import com.alibaba.gaiax.js.impl.qjs.module.QuickJSTimer
import com.alibaba.gaiax.js.support.script.GXScriptBuilder
import com.alibaba.gaiax.js.utils.IdGenerator
import com.alibaba.gaiax.js.utils.Log
import com.alibaba.gaiax.quickjs.BridgeModuleListener
import com.alibaba.gaiax.quickjs.JSContext

internal class QuickJSContext(
    private val hostContext: GXHostContext,
    val engine: QuickJSEngine,
    private val runtime: QuickJSRuntime
) : IContext {

    companion object {
        fun create(host: GXHostContext, engine: IEngine, runtime: IRuntime): QuickJSContext {
            return QuickJSContext(host, engine as QuickJSEngine, runtime as QuickJSRuntime)
        }
    }

    private var bridgeModule: BridgeModuleListener? = null
    private var bootstrap: String? = null
    private var jsContext: JSContext? = null

    override fun initContext() {
        engine.checkQuickJS()
        runtime.checkRuntime()
        jsContext = runtime.jsRuntime?.createJSContext()
        if (Log.isLog()) {
            Log.d("initContext() engine = $engine, runtime = $runtime, jsContext = $jsContext")
        }
    }

    override fun initModule(module: String) {
        engine.checkQuickJS()
        runtime.checkRuntime()
        checkContext()

        if (Log.isLog()) {
            Log.d("initModule() called with: module = $module")
        }

        when (module) {
            "timer" -> initModuleTimer()
            "os" -> jsContext?.initModuleOs()
            "std" -> jsContext?.initModuleStd()
            "GaiaXJSBridge" -> {
                if (bridgeModule == null && jsContext != null) {
                    bridgeModule = QuickJSBridgeModule(hostContext, jsContext!!)
                }
                jsContext?.registerBridgeModuleListener(bridgeModule)
                jsContext?.initModuleBridge(module)
            }
        }
    }

    override fun initBootstrap() {
        if (bootstrap == null) {
            val contextId = hostContext.hostRuntime.hostEngine.engineId
            val sb = StringBuilder()
            sb.append(GXScriptBuilder.buildImportScript())
            sb.append(GXScriptBuilder.buildGlobalContext(contextId, 0))
            sb.append(GXScriptBuilder.buildExtendAndAssignScript())
            sb.append(
                GXJSEngine.instance.context.resources.assets.open(GXHostContext.BOOTSTRAP_JS)
                    .bufferedReader(Charsets.UTF_8).use { it.readText() })
            sb.append(GXJSEngine.instance.moduleManager.buildModulesScript(GXJSEngine.EngineType.QuickJS))
            sb.append(GXScriptBuilder.buildStyle())
            bootstrap = sb.toString()
        }
    }

    private val pendingTaskId = IdGenerator.genIntId()

    override fun initPendingJob() {
        hostContext.executeIntervalTask(pendingTaskId, 10) {
            val executePendingJob = jsContext?.executePendingJob()
        }
    }

    override fun destroyPendingJob() {
        hostContext.remoteIntervalTask(pendingTaskId)
    }

    override fun startBootstrap() {
        if (Log.isLog()) {
            Log.d("startBootstrap() called")
        }
        bootstrap?.let { bootstrap ->
            evaluateJS(bootstrap)
        }
    }

    private fun initModuleTimer() {
        jsContext?.let { context ->
            context.globalObject.setProperty(
                "setTimeout",
                context.createJSFunction(QuickJSTimer.createSetTimeoutFunc())
            )
            context.globalObject.setProperty(
                "clearTimeout",
                context.createJSFunction(QuickJSTimer.createClearTimeoutFunc())
            )
            context.globalObject.setProperty(
                "setInterval",
                context.createJSFunction(QuickJSTimer.createSetIntervalFunc())
            )
            context.globalObject.setProperty(
                "clearInterval",
                context.createJSFunction(QuickJSTimer.createClearIntervalFunc())
            )
        }
    }

    private fun checkContext() {
        if (jsContext == null) {
            throw IllegalArgumentException("JSContext Instance Null")
        }
    }

    override fun evaluateJS(script: String, argsMap: JSONObject) {
        if (Log.isScriptLog()) {
            Log.e("jsContext = $jsContext, evaluateJS() called with: script = $script")
        }
        this.jsContext?.evaluate(script, "index.js", GXHostContext.EVAL_TYPE_MODULE, JSContext.EVAL_FLAG_STRIP)
    }

    override fun <T> evaluateJS(script: String, clazz: Class<T>?): T? {
        if (Log.isScriptLog()) {
            Log.e("jsContext = $jsContext, evaluateJS() called with: script = $script")
        }
        val ret = this.jsContext?.evaluate(script, "index.js", JSContext.EVAL_TYPE_MODULE, JSContext.EVAL_FLAG_STRIP, clazz)
        if (Log.isScriptLog()) {
            Log.e("jsContext = $jsContext, evaluateJS() called with: ret=$ret")
        }
        return ret
    }

    override fun destroyContext() {
        jsContext?.close()
        jsContext = null
    }
}