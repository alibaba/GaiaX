package com.alibaba.gaiax.js.impl.debug

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.engine.GXHostContext
import com.alibaba.gaiax.js.engine.IContext
import com.alibaba.gaiax.js.support.GXScriptBuilder

/**
 *  @author: shisan.lms
 *  @date: 2023-03-14
 *  Description:
 */
internal class DebugJSContext(
    private val hostContext: GXHostContext
) : IContext {

    var socketBridge: DebugJSBridgeModule = DebugJSBridgeModule(hostContext, this)

    internal var bootstrap: String? = null

    override fun initContext() {

    }

    override fun evaluateJS(script: String, argsMap: JSONObject) {
        if (argsMap.containsKey("instanceId")) {
            socketBridge.sendCreateJSComponent(
                templateId = argsMap["templateId"].toString(),
                instanceId = argsMap["instanceId"].toString(),
                templateVersion = argsMap["templateVersion"].toString(),
                bizId = argsMap["bizId"].toString()
            )
        } else {
            socketBridge.sendEvalScript(script)
        }
    }

    override fun destroyContext() {
    }

    override fun initModule(module: String) {
    }

    override fun initBootstrap() {
        if (bootstrap == null) {
            val sb = StringBuilder()
            sb.append(
                GXScriptBuilder.buildGlobalContext(
                    hostContext.hostRuntime.hostEngine.engineId, 10
                )
            )
            sb.append("__globalThis.__DEV__ = true; \r\n")
            sb.append("/**bridge.ts**/\r\n")
            sb.append("/**bootstrap.ts**/\r\n")
            sb.append(GXJSEngine.Proxy.buildModulesScript(GXJSEngine.EngineType.DebugJS))
            sb.append(GXScriptBuilder.buildDebugStyle())
            bootstrap = sb.toString()
        }
    }

    override fun startBootstrap() {
        bootstrap?.let { socketBridge.sendInitEnvJSScript(it) }
    }

    override fun initPendingJob() {
    }

    override fun destroyPendingJob() {
    }
}