package com.alibaba.gaiax.js.impl.debug

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.engine.GXHostContext
import com.alibaba.gaiax.js.engine.IContext
import com.alibaba.gaiax.js.engine.IEngine
import com.alibaba.gaiax.js.engine.IRuntime
import com.alibaba.gaiax.js.support.GXScriptBuilder

/**
 *  @author: shisan.lms
 *  @date: 2023-03-14
 *  Description:
 */
internal class DebugJSContext(
    val host: GXHostContext, val engine: DebugJSEngine, val runtime: DebugJSRuntime
) : IContext {

    companion object {
        fun create(host: GXHostContext, engine: IEngine, runtime: IRuntime): DebugJSContext {
            return DebugJSContext(host, engine as DebugJSEngine, runtime as DebugJSRuntime)
        }
    }

    private var bootstrap: String? = null

    override fun initContext() {

    }

    override fun evaluateJS(script: String, argsMap: JSONObject) {
        if (argsMap.containsKey("instanceId")) {
            runtime.debugJSSocket.sendCreateJSComponent(
                templateId = argsMap["templateId"].toString(),
                instanceId = argsMap["instanceId"].toString(),
                templateVersion = argsMap["templateVersion"].toString(),
                bizId = argsMap["bizId"].toString()
            )
        } else {
            runtime.debugJSSocket.sendEvalScript(script)
        }
    }

    override fun destroyContext() {
    }

    override fun initModule(module: String) {
        runtime.debugJSSocket.bridge = host.bridge
    }

    override fun initBootstrap() {
        if (bootstrap == null) {
            val sb = StringBuilder()
            sb.append(GXScriptBuilder.buildGlobalContext(host.hostRuntime.hostEngine.engineId, 10))
            sb.append("__globalThis.__DEV__ = true; \r\n")
            sb.append("/**bridge.ts**/\r\n")
            sb.append("/**bootstrap.ts**/\r\n")
            sb.append(GXJSEngine.Proxy.instance.buildModulesScript(GXJSEngine.EngineType.DebugJS))
            sb.append(GXScriptBuilder.buildDebugStyle())
            bootstrap = sb.toString()
            runtime.debugJSSocket.bootstrap = bootstrap
        }
    }

    override fun startBootstrap() {
        bootstrap?.let { runtime.debugJSSocket.sendInitEnvJSScript(it) }
    }

    override fun initPendingJob() {
//        TODO("Not yet implemented")
    }

    override fun destroyPendingJob() {
        TODO("Not yet implemented")
    }
}