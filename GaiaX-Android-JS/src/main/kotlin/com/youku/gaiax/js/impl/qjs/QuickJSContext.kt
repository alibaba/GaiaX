package com.youku.gaiax.js.impl.qjs

import com.alibaba.fastjson.JSONObject
import com.youku.gaiax.js.GaiaXJS
import com.youku.gaiax.js.core.GaiaXContext
import com.youku.gaiax.js.core.api.IContext
import com.youku.gaiax.js.core.api.IEngine
import com.youku.gaiax.js.core.api.IRuntime
import com.youku.gaiax.js.impl.qjs.module.QuickJSBridgeModule
import com.youku.gaiax.js.impl.qjs.module.QuickJSTimer
import com.youku.gaiax.js.support.GaiaXScriptBuilder
import com.youku.gaiax.js.utils.IdGenerator
import com.youku.gaiax.js.utils.Log
import com.youku.gaiax.quickjs.BridgeModuleListener
import com.youku.gaiax.quickjs.JSContext

internal class QuickJSContext(val host: GaiaXContext, val engine: QuickJSEngine, val runtime: QuickJSRuntime) : IContext {

    companion object {
        fun create(host: GaiaXContext, engine: IEngine, runtime: IRuntime): QuickJSContext {
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
    }

    override fun initModule(module: String) {
        engine.checkQuickJS()
        runtime.checkRuntime()
        checkContext()
        when (module) {
            "timer" -> initModuleTimer()
            "os" -> jsContext?.initModuleOs()
            "std" -> jsContext?.initModuleStd()
            "GaiaXBridge" -> {
                if (bridgeModule == null && jsContext != null) {
                    bridgeModule = QuickJSBridgeModule(host, jsContext!!)
                }
                jsContext?.registerBridgeModuleListener(bridgeModule)
                jsContext?.initModuleBridge(module)
            }
        }
    }

    override fun initBootstrap() {
        if (bootstrap == null) {
            val sb = StringBuilder()
            sb.append(GaiaXScriptBuilder.buildImportScript())
            sb.append(GaiaXScriptBuilder.buildGlobalContext(host.host.host.engineId, 0))
            sb.append(GaiaXScriptBuilder.buildExtendAndAssignScript())
            sb.append(GaiaXJS.instance.buildBootstrapScript())
            sb.append(GaiaXJS.instance.buildModulesScript())
            sb.append(GaiaXScriptBuilder.buildStyle())
            bootstrap = sb.toString()
        }
    }

    private val pendingTaskId = IdGenerator.genIntId()

    override fun initPendingJob() {
        host.executeIntervalTask(pendingTaskId, 10) {
            val executePendingJob = jsContext?.executePendingJob()
        }
    }

    override fun destroyPendingJob() {
        host.remoteIntervalTask(pendingTaskId)
    }

    override fun startBootstrap() {
        bootstrap?.let { bootstrap ->
            evaluateJS(bootstrap)
        }
    }

    private fun initModuleTimer() {
        jsContext?.let { context ->
            context.globalObject.setProperty("setTimeout", context.createJSFunction(QuickJSTimer.createSetTimeoutFunc()))
            context.globalObject.setProperty("clearTimeout", context.createJSFunction(QuickJSTimer.createClearTimeoutFunc()))
            context.globalObject.setProperty("setInterval", context.createJSFunction(QuickJSTimer.createSetIntervalFunc()))
            context.globalObject.setProperty("clearInterval", context.createJSFunction(QuickJSTimer.createClearIntervalFunc()))
        }
    }

    private fun checkContext() {
        if (jsContext == null) {
            throw IllegalArgumentException("JSContext Instance Null")
        }
    }

    override fun evaluateJS(script: String, argsMap: JSONObject) {
        if (Log.isLog()) {
            Log.e("evaluateJS() called with: script = $script")
        }
        this.jsContext?.evaluate(script, "", GaiaXContext.EVAL_TYPE_MODULE, 0)
    }

    override fun destroyContext() {
        jsContext?.close()
        jsContext = null
    }
}