package com.youku.gaiax.js.impl.qjs

import com.alibaba.fastjson.JSONObject
import com.youku.gaiax.js.GaiaXJS
import com.youku.gaiax.js.core.GaiaXContext
import com.youku.gaiax.js.core.api.IContext
import com.youku.gaiax.js.core.api.IEngine
import com.youku.gaiax.js.core.api.IRuntime
import com.youku.gaiax.js.support.GaiaXScriptBuilder

/**
 *  @author: shisan.lms
 *  @date: 2023-03-14
 *  Description:
 */
internal class GaiaXJSDebuggerContext(val host: GaiaXContext, val engine: GaiaXJSDebuggerEngine, val runtime: GaiaXJSDebuggerRuntime) : IContext {

    companion object {
        fun create(host: GaiaXContext, engine: IEngine, runtime: IRuntime): GaiaXJSDebuggerContext {
            return GaiaXJSDebuggerContext(host, engine as GaiaXJSDebuggerEngine, runtime as GaiaXJSDebuggerRuntime)
        }
    }

    private var bootstrap: String? = null

    override fun initContext() {

    }

    override fun evaluateJS(script: String, argsMap: JSONObject) {
        if (argsMap.containsKey("instanceId")) {
            runtime.gxJSSocketClientWrapper.sendCreateJSComponent(
                templateId = argsMap["templateId"].toString(),
                instanceId = argsMap["instanceId"].toString(),
                templateVersion = argsMap["templateVersion"].toString(),
                bizId = argsMap["bizId"].toString()
            )
        } else {
            runtime.gxJSSocketClientWrapper.sendEvalScript(script)
        }
    }

    override fun destroyContext() {
        TODO("Not yet implemented")
    }

    override fun initModule(module: String) {
        runtime.gxJSSocketClientWrapper.bridge = host.bridge
    }

    override fun initBootstrap() {
        if (bootstrap == null) {
            val sb = StringBuilder()
//            sb.append(GaiaXScriptBuilder.buildImportScript())
            sb.append(GaiaXScriptBuilder.buildGlobalContext(host.host.host.engineId, 10))
            sb.append("__globalThis.__DEV__ = true; \r\n")
            sb.append("/**bridge.ts**/\r\n")
            sb.append("/**bootstrap.ts**/\r\n")
//            sb.append(GaiaXScriptBuilder.buildExtendAndAssignScript())
//            sb.append(GaiaXJS.instance.buildBootstrapScript())
            sb.append(GaiaXJS.instance.buildModulesScript())
            sb.append(GaiaXScriptBuilder.buildDebugStyle())
            bootstrap = sb.toString()
            runtime.gxJSSocketClientWrapper.bootstrap = bootstrap
        }
    }

    override fun startBootstrap() {
        bootstrap?.let { runtime.gxJSSocketClientWrapper.sendInitEnvJSScript(it) }
    }

    override fun initPendingJob() {
//        TODO("Not yet implemented")
    }

    override fun destroyPendingJob() {
        TODO("Not yet implemented")
    }
}